#include "operations.h"
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <string.h>
#include <pthread.h>
#include <errno.h>


typedef struct{
    char op_code;
    char name[MAX_FILE_NAME];
    char* buffer;
    int fhandle;
    int flags;
    size_t len;

} message_t;


static int fserv, shutdown_number=0;
static pthread_mutex_t lock_shutdown;

static char* pipename;

pthread_t tid[MAX_SESSIONS];
static int sessions[MAX_SESSIONS];

static message_t sessions_messages[MAX_SESSIONS];
static int fclient[MAX_SESSIONS];

static pthread_cond_t prod_cond_sessions[MAX_SESSIONS];
static pthread_cond_t cons_cond_sessions[MAX_SESSIONS];

static char free_sessions[MAX_SESSIONS];
static pthread_mutex_t lock_free_sessions;

static char working_sessions[MAX_SESSIONS];
static pthread_mutex_t locks_working_sessions[MAX_SESSIONS];


int send_msg(int fpipe,void const *str,size_t len){
    size_t written=0;
    while(written<len){
        ssize_t retw=write(fpipe,str+written,len-written);
        if(retw==-1 && errno==EPIPE)
            return -1;
        else if(retw==-1 && errno!=EINTR)
            exit(EXIT_FAILURE);
        written+=(size_t)retw;
    }
    return 0;
}


ssize_t read_msg(int fpipe,void *str, size_t len){
    ssize_t readed=read(fpipe,str,len);
    while(readed==-1 && errno!=EINTR)
        readed=read(fpipe,str+readed,len);
    if(readed==-1)
        exit(EXIT_FAILURE);
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


void* working_task(void* arg){
    int session_id=*(int*)arg;
    int value;
    ssize_t ret,ret_w;
    message_t message;
    while(1){
        if(pthread_mutex_lock(&locks_working_sessions[session_id])!=0)
            exit(EXIT_FAILURE);
        while(working_sessions[session_id]==FREE){
            if(pthread_cond_wait(&cons_cond_sessions[session_id],&locks_working_sessions[session_id])!=0)
                exit(EXIT_FAILURE);
        }
        message=sessions_messages[session_id];
        switch(message.op_code){
            case TFS_OP_CODE_MOUNT:
                fclient[session_id]=open_file(message.name,O_WRONLY);
                if(fclient[session_id]==-1){
                    if(pthread_mutex_lock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                    free_sessions[session_id]=FREE;
                    if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                }
                if(send_msg(fclient[session_id],&session_id,sizeof(int))==-1){
                    if(pthread_mutex_lock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                    close_file(fclient[session_id]);
                    free_sessions[session_id]=FREE;
                    if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                }
                break;
            case TFS_OP_CODE_UNMOUNT:
                value=0;
                send_msg(fclient[session_id],&value,sizeof(int)); 
                close_file(fclient[session_id]);
                if(pthread_mutex_lock(&lock_free_sessions)!=0)
                    exit(EXIT_FAILURE);
                free_sessions[session_id]=FREE;
                if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                    exit(EXIT_FAILURE);
                break;
            case TFS_OP_CODE_OPEN:
                value=tfs_open(message.name,message.flags);
                if(send_msg(fclient[session_id],&value,sizeof(int))==-1){
                    tfs_close(value);
                    close_file(fclient[session_id]);
                    if(pthread_mutex_lock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                    free_sessions[session_id]=FREE;
                    if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                }
                break;
            case TFS_OP_CODE_CLOSE:
                value=tfs_close(message.fhandle);
                if(send_msg(fclient[session_id],&value,sizeof(int))==-1){
                    close_file(fclient[session_id]);
                    if(pthread_mutex_lock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                    free_sessions[session_id]=FREE;
                    if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                }
                break;
            case TFS_OP_CODE_WRITE:
                if(message.buffer==NULL)
                    value=-1;
                else{            
                    ret_w=tfs_write(message.fhandle,message.buffer,message.len);
                    value=(int)ret_w;
                }
                if(send_msg(fclient[session_id],&value,sizeof(int))==-1){
                    close_file(fclient[session_id]);
                    if(pthread_mutex_lock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                    free_sessions[session_id]=FREE;
                    if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                }
                free(message.buffer);
                break;
            case TFS_OP_CODE_READ:
                message.buffer=(char*) malloc(message.len+sizeof(int));
                if(message.buffer==NULL){
                    value=-1;
                    if(send_msg(fclient[session_id],&value,sizeof(int))==-1){
                        close_file(fclient[session_id]);
                        if(pthread_mutex_lock(&lock_free_sessions)!=0)
                            exit(EXIT_FAILURE);
                        free_sessions[session_id]=FREE;
                        if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                            exit(EXIT_FAILURE);
                    }
                    break;
                }
                ret=tfs_read(message.fhandle,message.buffer+sizeof(int),message.len);
                value=(int)ret;
                memcpy(message.buffer,&value,sizeof(int));
                if(send_msg(fclient[session_id],message.buffer,message.len+sizeof(int))==-1){
                    close_file(fclient[session_id]);
                    if(pthread_mutex_lock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                    free_sessions[session_id]=FREE;
                    if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                        exit(EXIT_FAILURE);
                }
                free(message.buffer);
                break;
            case TFS_OP_CODE_SHUTDOWN_AFTER_ALL_CLOSED:
                if(pthread_mutex_lock(&lock_shutdown)!=0)
                    exit(EXIT_FAILURE);
                shutdown_number++;
                if(pthread_mutex_unlock(&lock_shutdown)!=0)
                    exit(EXIT_FAILURE);
                value=tfs_destroy_after_all_closed();
                if(pthread_mutex_lock(&lock_shutdown)!=0)
                    exit(EXIT_FAILURE);
                shutdown_number--;
                send_msg(fclient[session_id],&value,sizeof(int));
                if(shutdown_number==0)
                    exit(EXIT_FAILURE);
                if(pthread_mutex_unlock(&lock_shutdown)!=0)
                    exit(EXIT_FAILURE);
                break;
            default:
                break;
        }
        working_sessions[session_id]=FREE;
        if(pthread_cond_signal(&prod_cond_sessions[session_id])!=0)
            exit(EXIT_FAILURE);
        if(pthread_mutex_unlock(&locks_working_sessions[session_id])!=0)
            exit(EXIT_FAILURE);
    }
    return NULL;
}


void server_init(){
    if(pthread_mutex_init(&lock_shutdown,NULL)!=0)
        exit(EXIT_FAILURE);
    if(pthread_mutex_init(&lock_free_sessions,NULL)!=0)
        exit(EXIT_FAILURE);
    for(int i=0;i<MAX_SESSIONS;i++){
        if(pthread_mutex_init(&locks_working_sessions[i],NULL)!=0)
            exit(EXIT_FAILURE);
        if(pthread_cond_init(&prod_cond_sessions[i],NULL)!=0)
            exit(EXIT_FAILURE);
        if(pthread_cond_init(&cons_cond_sessions[i],NULL)!=0)
            exit(EXIT_FAILURE);
        fclient[i]=-1;
        free_sessions[i]=FREE;
        working_sessions[i]=FREE;
        sessions[i]=i;
        if(pthread_create(&tid[sessions[i]],NULL,working_task,&sessions[i])!=0)
            exit(EXIT_FAILURE);
    }
    printf("Starting TecnicoFS server with pipe called %s\n", pipename);
    if(unlink(pipename) !=0 && errno!=ENOENT)
        exit(EXIT_FAILURE);
    if(mkfifo(pipename,0777)!=0)
        exit(EXIT_FAILURE);
    if((fserv=open_file(pipename,O_RDONLY))==-1)
        exit(EXIT_FAILURE);
    if(tfs_init()==-1)
        exit(EXIT_FAILURE); 
}


int session_create(){
    if(pthread_mutex_lock(&lock_free_sessions)!=0)
        exit(EXIT_FAILURE);
    for(int session=0;session<MAX_SESSIONS;session++){
        if(free_sessions[session]==FREE){
            free_sessions[session]=TAKEN;
            if(pthread_mutex_unlock(&lock_free_sessions)!=0)
                exit(EXIT_FAILURE);
            return session;
        }
    }
    if(pthread_mutex_unlock(&lock_free_sessions)!=0)
        exit(EXIT_FAILURE);
    return -1;
}


void parse_message(char op_code, int session_id){
    ssize_t ret;
    sessions_messages[session_id].op_code=op_code;
    switch(op_code){
        case TFS_OP_CODE_MOUNT:
            ret=read_msg(fserv,sessions_messages[session_id].name,MAX_FILE_NAME);
            sessions_messages[session_id].name[ret]='\0';
            break;
        case TFS_OP_CODE_OPEN:
            ret=read_msg(fserv,sessions_messages[session_id].name,MAX_FILE_NAME);
            sessions_messages[session_id].name[ret]='\0';
            read_msg(fserv,&sessions_messages[session_id].flags,sizeof(int));
            break;
        case TFS_OP_CODE_CLOSE:
            read_msg(fserv,&sessions_messages[session_id].fhandle,sizeof(int));
            break;
        case TFS_OP_CODE_WRITE:
            read_msg(fserv,&sessions_messages[session_id].fhandle,sizeof(int));
            read_msg(fserv,&sessions_messages[session_id].len,sizeof(size_t));
            sessions_messages[session_id].buffer=(char*) malloc(sessions_messages[session_id].len);
            read_msg(fserv,sessions_messages[session_id].buffer,sessions_messages[session_id].len);   
            break;
        case TFS_OP_CODE_READ:
            read_msg(fserv,&sessions_messages[session_id].fhandle,sizeof(int));
            read_msg(fserv,&sessions_messages[session_id].len,sizeof(size_t));
            break;
        default:
            break;
    }
}


int main(int argc, char **argv) {

    int session_id;
    char op_code;

    if (argc < 2) {
        printf("Please specify the pathname of the server's pipe.\n");
        return 1;
    }
    pipename=argv[1];
    server_init();
    for(;;){
        if(read_msg(fserv,&op_code,sizeof(char))==0){
            close_file(fserv);
            fserv=open_file(pipename,O_RDONLY);
            read_msg(fserv,&op_code,sizeof(char));
        }
        if(op_code==TFS_OP_CODE_MOUNT)
            session_id=session_create();
        else
            read_msg(fserv,&session_id,sizeof(int));
        if(session_id!=-1){  
            if(pthread_mutex_lock(&locks_working_sessions[session_id])!=0)
                exit(EXIT_FAILURE);
            while(working_sessions[session_id]==TAKEN){
                if(pthread_cond_wait(&prod_cond_sessions[session_id],&locks_working_sessions[session_id])!=0)
                    exit(EXIT_FAILURE);
            }
            parse_message(op_code,session_id);
            working_sessions[session_id]=TAKEN; 
            if(pthread_cond_signal(&cons_cond_sessions[session_id])!=0)
                exit(EXIT_FAILURE);
            if(pthread_mutex_unlock(&locks_working_sessions[session_id])!=0)
                exit(EXIT_FAILURE);
        }
        else{
            char client_pipe[MAX_FILE_NAME];
            int fclient_error;
            read_msg(fserv,client_pipe,MAX_FILE_NAME);
            fclient_error=open_file(client_pipe,O_WRONLY);
            send_msg(fclient_error,&session_id,sizeof(int));
            close_file(fclient[session_id]);
        }
    }
    return 0;
}