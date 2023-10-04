#include "fs/operations.h"
#include <assert.h>
#include <string.h>
#include <pthread.h>


void* rotina1(void* arg){
    int f=*(int*) arg;
    ssize_t r;
    char *str= "O";
    
    r = tfs_write(f, str, strlen(str));
    assert(r == strlen(str));
    return NULL;

}

void* rotina2(void* arg){
    int f=*(int*) arg;
    ssize_t r;
    char *str= "L";
    
    r = tfs_write(f, str, strlen(str));
    assert(r == strlen(str));
    return NULL;
}

void* rotina3(void* arg){
    int f=*(int*) arg;
    ssize_t r;
    char *str= "A";
    
    r = tfs_write(f, str, strlen(str));
    assert(r == strlen(str));
    return NULL;

}


int main() {

    char *str = "OLA";
    char *path = "/f1";
    char buffer[40];
    int f;
    ssize_t r;
    pthread_t tid[3];

    assert(tfs_init() != -1);


    

    f = tfs_open(path, TFS_O_CREAT);
    assert(f != -1);

	
	pthread_create(&tid[0], NULL, rotina1, &f);
	pthread_create(&tid[1], NULL, rotina2, &f);
	pthread_create(&tid[2], NULL, rotina3, &f);

    pthread_join(tid[0],NULL);
    pthread_join(tid[1],NULL);
    pthread_join(tid[2],NULL);

    assert(tfs_close(f)!=-1);

    f = tfs_open(path, 0);
    assert(f != -1);

    r = tfs_read(f, buffer, sizeof(buffer) - 1);
    assert(r == strlen(str));

    buffer[r] = '\0';

    printf("%s\n",buffer);

    assert(tfs_close(f)!=-1);
    assert(tfs_destroy()!=-1);

    printf("Successful test.\n");

    return 0;
}