#include "fs/operations.h"
#include <assert.h>
#include <string.h>
#include <pthread.h>


void* rotina1(void* arg){
    char *path = "/f1";

    *(int*)arg=tfs_open(path,TFS_O_CREAT);
    assert(*(int*)arg!=-1);
    //assert(tfs_close(*(int*)arg)!=-1);

    return NULL;
}


int main() {

    char *str = "OOO";
    char *path = "/f1";
    char buffer[40];
    int f,f1,f2,f3;
    ssize_t r;
    pthread_t tid[3];

    assert(tfs_init() != -1);
	
	pthread_create(&tid[0], NULL, rotina1, &f1);
	pthread_create(&tid[1], NULL, rotina1, &f2);
	pthread_create(&tid[2], NULL, rotina1, &f3);

    pthread_join(tid[0],NULL);
    pthread_join(tid[1],NULL);
    pthread_join(tid[2],NULL);



    assert(f1!=f2 && f1!=f3 && f2!=f3);
    
    // assert(tfs_close(f1)!=-1);
    // assert(tfs_close(f2)!=-1);
    // assertf(tfs_close(f3)!=-1);

    f=tfs_open(path,TFS_O_CREAT);
    r=tfs_write(f,str,strlen(str));
    assert(r==strlen(str));

    r=tfs_read(f1,buffer,sizeof(buffer)-1);
    assert(r == strlen(str));
    buffer[r] = '\0';
    assert(strcmp(buffer, str) == 0);

    r=tfs_read(f2,buffer,sizeof(buffer)-1);
    assert(r == strlen(str));
    buffer[r] = '\0';
    assert(strcmp(buffer, str) == 0);

    r=tfs_read(f3,buffer,sizeof(buffer)-1);
    assert(r == strlen(str));
    buffer[r] = '\0';
    assert(strcmp(buffer, str) == 0);

    assert(tfs_close(f1)!=-1);
    assert(tfs_close(f2)!=-1);
    assert(tfs_close(f3)!=-1);
    assert(tfs_close(f)!=-1);

    assert(tfs_destroy()!=-1);
    //assert((f1==f2)==f3);

    printf("Successful test.\n");

    return 0;
}