#include "tecnicofs_client_api.h"
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <errno.h>

static int fserv,fclient,session_id=-1;
static char client_path[MAX_FILE_NAME];


int send_msg(int fpipe,void const *str,size_t len){
    size_t written=0;
    while(written<len){
        ssize_t retw=write(fpipe,str+written,len-written);
        if(retw==-1 && errno!=EINTR)
            return -1;
        written+=(size_t)retw;
    }
    return 0;
}


ssize_t read_msg(int fpipe,void *str, size_t len){
    ssize_t readed=read(fpipe,str,len);
    while(readed==-1 && errno!=EINTR)
        readed=read(fpipe,str+readed,len);
    return readed;
}


int open_file(void const *str,int flags){
    int fhandle=open(str,flags);
    while(fhandle==-1 && errno==EINTR)
        fhandle=open(str,flags);
    return fhandle; 
}


int close_file(int fhandle){
    int ret=close(fhandle);
    while(ret==-1 && errno==EINTR)
        ret=close(fhandle);
    return ret;
}


int tfs_mount(char const *client_pipe_path, char const *server_pipe_path) {
    size_t size=sizeof(char)+MAX_FILE_NAME;
    char buffer[size];
    memcpy(client_path,client_pipe_path,strlen(client_pipe_path));
    memset(buffer,0,size);
    buffer[0]=TFS_OP_CODE_MOUNT;
    memcpy(&buffer[1],client_pipe_path,strlen(client_pipe_path));
    if(unlink(client_pipe_path)!=0 && errno!=ENOENT)
        return -1;
    if(mkfifo(client_pipe_path,0777)!=0)
        return -1;
    fserv=open_file(server_pipe_path,O_WRONLY);
    if(fserv==-1)
        return -1;
    if(send_msg(fserv,buffer,sizeof(buffer))==-1)
        return -1;
    fclient=open_file(client_pipe_path,O_RDONLY);
    if(fclient==-1){
        close_file(fserv);
        unlink(client_pipe_path);
        return -1;
    }
    if(read_msg(fclient,&session_id,sizeof(int))==-1)
        return -1;
    if(session_id==-1)
        return -1;
    return 0;
}


int tfs_unmount() {
    int return_value,size=sizeof(char)+sizeof(int);
    char buffer[size];
    char *ptr=buffer;
    if(session_id==-1)
        return -1;
    (*ptr++)=TFS_OP_CODE_UNMOUNT;
    memcpy(ptr,&session_id,sizeof(int));
    if(send_msg(fserv,buffer,sizeof(buffer))==-1)
        return -1;
    if(read_msg(fclient,&return_value,sizeof(int))==-1)
        return -1;
    if(return_value==0){
        if(close_file(fclient)==-1)
            return -1;
        if(close_file(fserv)==-1)
            return -1;
        if(unlink(client_path)!=0 && errno!=ENOENT)
            return -1;  
        return 0;
    }
    return -1;
}


int tfs_open(char const *name, int flags) {
    int return_value;
    size_t size=sizeof(char)+sizeof(int)*2+MAX_FILE_NAME;
    char buffer[size];
    char *ptr=buffer;
    if(session_id==-1)
        return -1;
    memset(buffer,0,size);
    (*ptr++)=TFS_OP_CODE_OPEN;
    memcpy(ptr,&session_id,sizeof(int));
    ptr+=sizeof(int);
    memcpy(ptr,name,MAX_FILE_NAME);
    ptr+=40;
    memcpy(ptr,&flags,sizeof(int));
    if(send_msg(fserv,buffer,sizeof(buffer))==-1)
        return -1;
    if(read_msg(fclient,&return_value,sizeof(int))==-1)
        return -1;
    return return_value;
}


int tfs_close(int fhandle) {
    int return_value,size=sizeof(char)+sizeof(int)*2;
    char buffer[size];
    char *ptr=buffer;
    if(session_id==-1)
        return -1;
    (*ptr++)=TFS_OP_CODE_CLOSE;
    memcpy(ptr,&session_id,sizeof(int));
    ptr+=sizeof(int);
    memcpy(ptr,&fhandle,sizeof(int));
    if(send_msg(fserv,buffer,sizeof(buffer))==-1)
        return -1;
    if(read_msg(fclient,&return_value,sizeof(int))==-1)
        return -1;
    return return_value;
}


ssize_t tfs_write(int fhandle, void const *buffer, size_t len) {
    int return_value;
    size_t size=sizeof(char)+sizeof(int)*2+sizeof(size_t)+len;
    char buffer_write[size];
    char *ptr=buffer_write;
    if(session_id==-1)
        return -1;
    (*ptr++)=TFS_OP_CODE_WRITE;
    memcpy(ptr,&session_id,sizeof(int));
    ptr+=sizeof(int);
    memcpy(ptr,&fhandle,sizeof(int));
    ptr+=sizeof(int);
    memcpy(ptr,&len,sizeof(size_t));
    ptr+=sizeof(size_t);
    memcpy(ptr,buffer,len);
    if(send_msg(fserv,buffer_write,sizeof(buffer_write))==-1)
        return -1;
    if(read_msg(fclient,&return_value,sizeof(int))==-1)
        return -1;
    return return_value;
}


ssize_t tfs_read(int fhandle, void *buffer, size_t len) {
    int return_value,size=sizeof(char)+sizeof(int)*2+sizeof(size_t);
    char buffer_read[size];
    char *ptr=buffer_read;
    if(session_id==-1)
        return -1;
    (*ptr++)=TFS_OP_CODE_READ;
    memcpy(ptr,&session_id,sizeof(int));
    ptr+=sizeof(int);
    memcpy(ptr,&fhandle,sizeof(int));
    ptr+=sizeof(int);
    memcpy(ptr,&len,sizeof(size_t));
    if(send_msg(fserv,buffer_read,sizeof(buffer_read))==-1)
        return -1;
    if(read_msg(fclient,&return_value,sizeof(int))==-1)
        return -1;
    if(return_value==-1)
        return -1;
    if(read_msg(fclient,buffer,len)==-1)
        return -1;
    return return_value;
}


int tfs_shutdown_after_all_closed() {
    int return_value,size=sizeof(char)+sizeof(int);
    char buffer[size];
    char *ptr=buffer;
    if(session_id==-1)
        return -1;
    (*ptr++)=TFS_OP_CODE_SHUTDOWN_AFTER_ALL_CLOSED;
    memcpy(ptr,&session_id,sizeof(int));
    if(send_msg(fserv,buffer,sizeof(buffer))==-1)
        return -1;
    if(read_msg(fclient,&return_value,sizeof(int))==-1)
        return -1;
    return return_value;
}
