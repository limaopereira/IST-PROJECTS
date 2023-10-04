#include "fs/operations.h"
#include <assert.h>
#include <string.h>
#include <pthread.h>

void* read(void* arg){
    int f=*(int*)arg;
    ssize_t r;
    char buffer[40];
    r = tfs_read(f, buffer, 3);
    buffer[r]='\0';
    printf("%s\n",buffer);
    return NULL;
}

int main() {

    char *str = "ABCDFEGHI";
    char *path = "/f1";
    //char buffer[40];
    int f;
    ssize_t r;
    pthread_t tid[3];

    assert(tfs_init() != -1);

    f=tfs_open(path,TFS_O_CREAT);
    //printf("%d\n",f);
    r=tfs_write(f,str,strlen(str));
    assert(r==strlen(str));
    assert(tfs_close(f)!=-1);

    f=tfs_open(path,0);

	pthread_create(&tid[0], NULL, read, &f);
	pthread_create(&tid[1], NULL, read,&f);
    pthread_create(&tid[2],NULL,read,&f);

    pthread_join(tid[0],NULL);
    pthread_join(tid[1],NULL);
    pthread_join(tid[2],NULL);   

    assert(tfs_close(f)!=-1); 

    assert(tfs_destroy()!=-1);
    return 0;
}