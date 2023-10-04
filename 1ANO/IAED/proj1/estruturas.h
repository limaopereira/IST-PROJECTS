/*
  Ficheiro: estruturas.h
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro em que se definem todas as estruturas e constantes
*/

/*DEFINICAO DE CONSTANTES*/
#define MAX_TAREFA 10000
#define MAX_DESCRICAO_TAREFA 51
#define MAX_ATIVIDADE 10
#define MAX_DESCRICAO_ATIVIDADE 21
#define MAX_UTILIZADOR 50
#define MAX_DESCRICAO_UTILIZADOR 21
#define TO_DO "TO DO"
#define IN_PROGRESS "IN PROGRESS"
#define DONE "DONE"
#define TOO_MANY_TASKS "too many tasks\n"
#define DUPLICATE_DESCRIPTION "duplicate description\n"
#define NO_SUCH_TASK "no such task\n"
#define TASK "task"
#define TASK_ALREADY_STARTED "task already started\n"
#define NO_SUCH_USER "no such user\n"
#define NO_SUCH_ACTIVITY "no such activity\n"
#define DURATION "duration"
#define SLACK "slack"
#define DENTRO 1
#define FORA 0
#define POSITIVO 1
#define NEGATIVO -1
#define INVALID_DURATION "invalid duration\n"
#define INVALID_TIME "invalid time\n"
#define USER_ALREADY_EXISTS "user already exists\n"
#define TOO_MANY_USERS "too many users\n"
#define DUPLICATE_ACTIVITY "duplicate activity\n"
#define TOO_MANY_ACTIVITIES "too many activities\n"
#define INVALID_DESCRIPTION "invalid description\n"


/*ESTRUTURAS*/

/*
  Tarefa: int, char*, char*,char*, int, int
  Representa uma tarefa constituida por identificador, descricao, utilizador,
  atividade, duracao e instante inicial.
*/
typedef struct{
    int identificador;
    char descricao[MAX_DESCRICAO_TAREFA];
    char utilizador[MAX_DESCRICAO_UTILIZADOR];
    char atividade[MAX_DESCRICAO_ATIVIDADE];
    int duracao;
    int instante_inicial;
} Tarefa;


