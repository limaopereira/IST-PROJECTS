#include "operations.h"
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>



int tfs_init() {
    state_init();
    /* create root inode */
    int root = inode_create(T_DIRECTORY);
    if (root != ROOT_DIR_INUM) {
        return -1;
    }
    return 0;
}


int tfs_destroy() {
    state_destroy();
    return 0;
}


static bool valid_pathname(char const *name) {
    return name != NULL && strlen(name) > 1 && name[0] == '/';
}


int tfs_lookup(char const *name) {
    if (!valid_pathname(name)) {
        return -1;
    }
    // skip the initial '/' character
    name++;
    return find_in_dir(ROOT_DIR_INUM, name);
}


int tfs_open(char const *name, int flags) { 
    int inum;
    size_t offset;
    /* Checks if the path name is valid */
    if (!valid_pathname(name)) {
        return -1;
    }
    inode_wrlock(ROOT_DIR_INUM);
    inum = tfs_lookup(name);  
    if (inum >= 0) {
        inode_unlock(ROOT_DIR_INUM);
        /* The file already exists */
        inode_wrlock(inum);
        inode_t *inode = inode_get(inum);
        if (inode == NULL) {
            inode_unlock(inum);
            return -1;
        }
        /* Trucate (if requested) */
        if (flags & TFS_O_TRUNC) {
            if (inode->i_size > 0) {
                size_t number_blocks=inode->i_size/BLOCK_SIZE;
                for(size_t i=0;i<number_blocks && i<MAX_DIRECT_BLOCKS;i++){
                    if(data_block_free(inode->i_data_block[i])==-1){
                        inode_unlock(inum);   
                        return -1;
                    }
                }
                if(number_blocks>=MAX_DIRECT_BLOCKS){
                    int* block_referencia=data_block_get(inode->i_data_block[MAX_DIRECT_BLOCKS]);
                    if(block_referencia==NULL){
                        inode_unlock(inum);
                        return -1;
                    }
                    for(size_t i=0;i<number_blocks-MAX_DIRECT_BLOCKS;i++){
                        if(data_block_free(block_referencia[i])==-1){
                            inode_unlock(inum);
                            return -1;
                        }
                    }
                }
                inode->i_size=0;
            }
        }
        /* Determine initial offset */
        if (flags & TFS_O_APPEND) {
            offset = inode->i_size;
        } else {
            offset = 0;
        }
        inode_unlock(inum);
    } else if (flags & TFS_O_CREAT) {
        /* The file doesn't exist; the flags specify that it should be created*/
        /* Create inode */
        inum = inode_create(T_FILE);
        /* Add entry in the root directory */
        if (add_dir_entry(ROOT_DIR_INUM, inum, name + 1) == -1) {
            inode_delete(inum);
            inode_unlock(ROOT_DIR_INUM);
            return -1;
        }
        offset = 0; 
        inode_unlock(ROOT_DIR_INUM);
    } else {
        inode_unlock(ROOT_DIR_INUM);
        return -1;
    }

    /* Finally, add entry to the open file table and
     * return the corresponding handle */
    return add_to_open_file_table(inum, offset,flags);

    /* Note: for simplification, if file was created with TFS_O_CREAT and there
     * is an error adding an entry to the open file table, the file is not
     * opened but it remains created */
}


int tfs_close(int fhandle) { return remove_from_open_file_table(fhandle); }


void tfs_write_block(open_file_entry_t *file, inode_t *inode, void const **buffer, void *block, size_t *to_write, size_t *i){ 
    size_t offset_aux=file->of_offset-BLOCK_SIZE*(*i);
    size_t to_write_aux;
    if(*to_write+offset_aux>BLOCK_SIZE)
        to_write_aux=BLOCK_SIZE-offset_aux;
    else
        to_write_aux=*to_write;
    memcpy(block+offset_aux,*buffer,to_write_aux);
    *buffer+=to_write_aux;  
    file->of_offset+=to_write_aux;
    if(file->of_offset>inode->i_size)
        inode->i_size=file->of_offset;
    *to_write-=to_write_aux;
    (*i)++;
}


ssize_t tfs_write(int fhandle, void const *buffer, size_t to_write) {
    size_t to_write_o=to_write;
    file_lock(fhandle);
    open_file_entry_t *file = get_open_file_entry(fhandle);
    if (file == NULL) {
        file_unlock(fhandle);
        return -1;
    }
    inode_wrlock(file->of_inumber);
    /* From the open file table entry, we get the inode */
    inode_t *inode = inode_get(file->of_inumber);
    if (inode == NULL) {
        inode_unlock(file->of_inumber);
        file_unlock(fhandle);
        return -1;
    }
    if(file->of_flags & TFS_O_APPEND)
        file->of_offset=inode->i_size;
    if(to_write+file->of_offset> BLOCK_SIZE*(MAX_DIRECT_BLOCKS +(BLOCK_SIZE/sizeof(int))))
        to_write=BLOCK_SIZE*(MAX_DIRECT_BLOCKS+(BLOCK_SIZE/sizeof(int)))-file->of_offset;
    size_t i=file->of_offset/BLOCK_SIZE;
    while(to_write>0 && i<MAX_DIRECT_BLOCKS){ 
        if(inode->i_data_block[i]==-1){
            int b=data_block_alloc();
            if(b==-1){
                inode_unlock(file->of_inumber);
                file_unlock(fhandle);
                return -1;
            }
            inode->i_data_block[i]=b;
        }
        void *block=data_block_get(inode->i_data_block[i]);
        if(block==NULL){
            file_unlock(fhandle);
            inode_unlock(file->of_inumber);
            return -1;
        }
        tfs_write_block(file,inode,&buffer,block,&to_write,&i);  
    }
    if(i>=MAX_DIRECT_BLOCKS && to_write>0){
        int* block_referencia;
        if(inode->i_data_block[MAX_DIRECT_BLOCKS]==-1){
            int b=data_block_alloc();
            if(b==-1){
                inode_unlock(file->of_inumber);
                file_unlock(fhandle);
                return -1;
            }
            inode->i_data_block[MAX_DIRECT_BLOCKS]=b;
            block_referencia=(int*)data_block_get(inode->i_data_block[MAX_DIRECT_BLOCKS]);
            if(block_referencia==NULL){
                inode_unlock(file->of_inumber);
                file_unlock(fhandle);
                return -1;
            }
            for(int j=0;j<BLOCK_SIZE/sizeof(int);j++)
                block_referencia[j]=-1;
        }
        else{
            block_referencia=(int*)data_block_get(inode->i_data_block[MAX_DIRECT_BLOCKS]);
            if(block_referencia==NULL){
                inode_unlock(file->of_inumber);
                file_unlock(fhandle);
                return -1;
            }
        }
        while(to_write>0){
            if(block_referencia[i-MAX_DIRECT_BLOCKS]==-1){
                int b=data_block_alloc();
                if(b==-1){
                    inode_unlock(file->of_inumber);
                    file_unlock(fhandle);
                    return -1;
                }
                block_referencia[i-MAX_DIRECT_BLOCKS]=b;
            }
            void *block=data_block_get(block_referencia[i-MAX_DIRECT_BLOCKS]);
            if(block==NULL){
                inode_unlock(file->of_inumber);
                file_unlock(fhandle);
                return -1;
            }
            tfs_write_block(file,inode,&buffer,block,&to_write,&i);
        }
    }
    inode_unlock(file->of_inumber);
    file_unlock(fhandle);
    return (ssize_t)(to_write_o-to_write);
}


void tfs_read_block(open_file_entry_t *file, void **buffer, void *block, size_t *to_read, size_t *i){
    // Temos de ter um lock para manter o offset, penso que talvez um lock no inode e/ou no file 
    size_t offset_aux=file->of_offset-BLOCK_SIZE**i;
    size_t to_read_aux;
    if(*to_read+offset_aux>BLOCK_SIZE)
        to_read_aux=BLOCK_SIZE-offset_aux;
    else
        to_read_aux=*to_read;
    memcpy(*buffer,block+offset_aux,to_read_aux);
    *buffer+=to_read_aux;
    file->of_offset+=to_read_aux;
    *to_read-=to_read_aux;
    (*i)++;
}


ssize_t tfs_read(int fhandle, void *buffer, size_t len) {
    file_lock(fhandle);
    open_file_entry_t *file = get_open_file_entry(fhandle);
    if (file == NULL) {
        file_unlock(fhandle);
        return -1;
    }
    inode_rdlock(file->of_inumber);
    /* From the open file table entry, we get the inode */
    inode_t *inode = inode_get(file->of_inumber);
    if (inode == NULL) {
        inode_unlock(file->of_inumber);
        file_unlock(fhandle);
        return -1;
    }
    /* Determine how many bytes to read */
    size_t to_read = inode->i_size - file->of_offset;  
    if (to_read > len) {
        to_read = len;
    }
    size_t to_read_o=to_read;
    size_t i=file->of_offset/BLOCK_SIZE;
    while(to_read>0 && i<MAX_DIRECT_BLOCKS){ //Falta ler nas referencias indiretas
        void *block=data_block_get(inode->i_data_block[i]);
        if(block==NULL){
            file_unlock(fhandle);
            inode_unlock(file->of_inumber);
            return -1;
        }
        tfs_read_block(file,&buffer,block,&to_read,&i);
    }
    if(i>=MAX_DIRECT_BLOCKS && to_read >0){
        int* block_referencia=(int*)data_block_get(inode->i_data_block[MAX_DIRECT_BLOCKS]);
        if(block_referencia==NULL){ // confirmar se é necessário
            file_unlock(fhandle);
            inode_unlock(file->of_inumber);
            return -1;
        }
        while(to_read>0){
            void *block=data_block_get(block_referencia[i-MAX_DIRECT_BLOCKS]);
            if(block==NULL){
                file_unlock(fhandle);
                inode_unlock(file->of_inumber);
                return -1;
            }
            tfs_read_block(file,&buffer,block,&to_read,&i);
        }
    }
    file_unlock(fhandle);
    inode_unlock(file->of_inumber);
    return (ssize_t)(to_read_o-to_read);
}


int tfs_copy_to_external_fs(char const* source_path, char const* dest_path){
    ssize_t bytes_read;
    size_t bytes_write;
    char buffer[MAX_BUFFER_SIZE]; //256
    int source_handle=tfs_open(source_path, 0);
    if(source_handle==-1)
        return -1;
    FILE *f = fopen(dest_path,"w");
    if(f==NULL)
        return -1;
    while((bytes_read=tfs_read(source_handle,buffer,MAX_BUFFER_SIZE))==MAX_BUFFER_SIZE){ //256
        bytes_write=fwrite(buffer,sizeof(char),MAX_BUFFER_SIZE,f); //256
        if(bytes_write==0 && feof(f))
            return -1;
    }
    if(bytes_read==-1)
        return -1;
    buffer[bytes_read]='\0'; // Para apenas escrever o número de bytes que leu quando o número é menor que o tamanho do buffer
    bytes_write=fwrite(buffer,sizeof(char),strlen(buffer),f);
    if(bytes_write==0 && feof(f))
        return -1;
    if(tfs_close(source_handle)==-1)
        return -1;
    if(fclose(f)!=0) //&& feof(f))
        return -1;
    return 0;
}