/*
  Ficheiro: funcoes.h
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro que define as funcoes principais
*/


/*IMPORTS*/
#include "auxiliar.h"

/*DECLARACAO DE FUNCOES*/

int comando_t(Tarefa t[],int n_tarefas, int tarefas_descricao[MAX_TAREFA],
				int tarefas_instante[MAX_TAREFA]);
void comando_l(Tarefa t[],int n_tarefas, int tarefas_descricao[MAX_TAREFA]);
int comando_n(int duracao_global);
int comando_u(char utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],
				int n_utilizadores);
void comando_m(Tarefa t[], int n_tarefas,
				char utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],
				int n_utilizadores,
				char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE],
             	int n_atividades, int duracao_global);
void comando_d(Tarefa t[], int n_tarefas,
				char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE], 
				int n_atividades,int tarefas_instante[MAX_TAREFA]);
int comando_a(char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE],
				int n_atividades);
int findUser(char utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],
				int left,int right,char user[MAX_DESCRICAO_UTILIZADOR]);

/*DEFINICAO DE FUNCOES*/

/*
  comando_t: Tarefa*,int, int*, int* -> int
  Adiciona uma nova tarefa ao sistema e devolve o numero de tarefas no sistema.
*/
int comando_t(Tarefa t[],int n_tarefas, int tarefas_descricao[MAX_TAREFA],
				int tarefas_instante[MAX_TAREFA]){
    char c,descricao_tarefa[MAX_DESCRICAO_TAREFA];
    int x,i=0,estado=FORA,duracao;
    scanf("%d",&duracao);
    while((c=getchar())!='\n' && i<MAX_DESCRICAO_TAREFA-1){
        if(c!=' ' && estado==FORA){
            estado=DENTRO;
        }
        if(estado==DENTRO){
            descricao_tarefa[i]=c;
            i++;
        }
    }
    descricao_tarefa[i]='\0';
    if((x=binarySearchDescricao(t,tarefas_descricao,0,n_tarefas-1,descricao_tarefa,0,0))!=-1 &&
                    n_tarefas<MAX_TAREFA && duracao>0){
            t[n_tarefas].duracao=duracao;
            strcpy(t[n_tarefas].descricao,descricao_tarefa);
            strcpy(t[n_tarefas].atividade,TO_DO);
            t[n_tarefas].identificador=n_tarefas+1;
            t[n_tarefas].instante_inicial=0;
            arrayInsert(tarefas_descricao,x,n_tarefas);
            arrayInsert(tarefas_instante,x,n_tarefas);
            printf("task %d\n",n_tarefas+1);
            return ++n_tarefas;
    }
    else{
        if(n_tarefas>=MAX_TAREFA)
            printf(TOO_MANY_TASKS);
        else if(x==-1)
            printf(DUPLICATE_DESCRIPTION);
        else if(duracao<=0)
            printf(INVALID_DURATION);
        return n_tarefas;
    }
}

/*
  comando_l: Tarefa*,int, int* -> ()
  Lista as tarefas no sistema. Se o comando for invocado sem argumentos, 
  todas as tarefas sao listadas por ordem alafabetica, caso contrario sao 
  listadas pela ordem dos respetivos id's.
*/
void comando_l(Tarefa t[],int n_tarefas,int tarefas_descricao[MAX_TAREFA]){
    char c;
    int i,x,instrucoes=0,id=0,estado=FORA,sinal=POSITIVO;
    while((c=getchar())!='\n'){
        if(c!=' ' && estado==FORA){
            estado=DENTRO;
            instrucoes++;
        }
        else if(c==' ' && estado==DENTRO){
            estado=FORA;
            if((id=id*sinal)>0 && id<n_tarefas+1)
                printf("%d %s #%d %s\n",id,t[id-1].atividade,t[id-1].duracao,t[id-1].descricao);
            else
                printf("%d: "NO_SUCH_TASK,id);
            id=0;
            sinal=POSITIVO;
        }
        if(estado==DENTRO){
            if(c=='-')
                sinal=NEGATIVO;
            else
                id=id*10+(c-'0');
        }
    }
    if(instrucoes!=0){
        if((id=id*sinal)>0 && id<n_tarefas+1)
            printf("%d %s #%d %s\n",id,t[id-1].atividade,t[id-1].duracao,t[id-1].descricao);
        else
            printf("%d: "NO_SUCH_TASK,id);
    }
    else{
        for(i=0;i<n_tarefas;i++){
            x=tarefas_descricao[i]-1;
            printf("%d %s #%d %s\n",t[x].identificador,t[x].atividade,t[x].duracao,t[x].descricao);
        }
    }
}

/*
  comando_n: int -> int
  Avanca o tempo do sistema, devolvendo o tempo total.
*/
int comando_n(int duracao_global){
    int duracao;
    scanf("%d",&duracao);
    if(duracao>=0){
        printf("%d\n",duracao+duracao_global);
        return duracao;
    }
    printf(INVALID_TIME);
    return 0;
}

/*
  comando_u: char*,int -> int
  Adiciona um utilizador ao sistema ou, caso nao sejam dados argumentos, lista
  todos os utilizadores do sistema. Devolve o numero de utilizadores no 
  sistema.
*/
int comando_u(char utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],int n_utilizadores){
    char c,utilizador[MAX_DESCRICAO_UTILIZADOR];
    int i=0,contador=0;
    while((c=getchar())!='\n'){
        if(c!=' ' && contador<MAX_DESCRICAO_UTILIZADOR){
            utilizador[i]=c;
            contador++;
            i++;
        }
    }
    utilizador[i]='\0';
    if(contador>0){

        if(findUser(utilizadores,0,n_utilizadores,utilizador)!=-1)
            printf(USER_ALREADY_EXISTS);
        else if(n_utilizadores>=MAX_UTILIZADOR)
            printf(TOO_MANY_USERS);
        else{
            strcpy(utilizadores[n_utilizadores],utilizador);
            return ++n_utilizadores;
        }
    }
    else
        for(i=0;i<n_utilizadores;i++)
            printf("%s\n",utilizadores[i]);
    return n_utilizadores;
}

/*
  comando_m: Tarefa*,int,char*,int,char*,int,int -> ()
  Move uma tarefa de uma atividade para uma outra atividade no sistema.
*/
void comando_m(Tarefa t[], int n_tarefas,char utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],
                int n_utilizadores,char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE],
                int n_atividades, int duracao_global){
    char c,utilizador[MAX_DESCRICAO_UTILIZADOR],atividade[MAX_DESCRICAO_ATIVIDADE];
    int i=0,estado=FORA,id;
    scanf("%d",&id);
    scanf("%s",utilizador);
    while((c=getchar())!='\n'){
        if(c!=' ' && estado==FORA)
            estado=DENTRO;
        if(estado==DENTRO){
            atividade[i]=c;
            i++;
        }
    }
    atividade[i]='\0';
    if(id>0 && id<=n_tarefas && strcmp(t[id-1].atividade,atividade)!=0){
        if(findUser(utilizadores,0,n_utilizadores,utilizador)==-1)
            printf(NO_SUCH_USER);
        else if(findActivity(atividades,0,n_atividades,atividade)==-1)
            printf(NO_SUCH_ACTIVITY);
        else if(strcmp(atividade,TO_DO)==0){
            if(strcmp(t[id-1].atividade,TO_DO)!=0)
                printf(TASK_ALREADY_STARTED);
        }
        else{
            if(strcmp(t[id-1].atividade,TO_DO)==0)
                t[id-1].instante_inicial=duracao_global;

            strcpy(t[id-1].utilizador,utilizador);
            strcpy(t[id-1].atividade,atividade);
            if(strcmp(t[id-1].atividade,DONE)==0)
                printf("duration=%d slack=%d\n",duracao_global-t[id-1].instante_inicial,
                                (duracao_global-t[id-1].instante_inicial)-t[id-1].duracao);
        }
    }
    else if(id>n_tarefas || id<=0)
        printf(NO_SUCH_TASK);
}

/*
  comando_d: Tarefa*,int,char*,int,int* -> ()
  Lista todas as tarefas do sistema que estejam numa dada atividade.
*/
void comando_d(Tarefa t[], int n_tarefas,
				char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE],
			   	int n_atividades,int tarefas_instante[MAX_TAREFA]){
    char c,atividade[MAX_DESCRICAO_ATIVIDADE];
    int id,i=0,estado=FORA,aux[MAX_TAREFA];
    while((c=getchar())!='\n'){
        if(c!=' ' && estado==FORA)
            estado=DENTRO;
        if(estado==DENTRO){
            atividade[i]=c;
            i++;
        }
    }
    atividade[i]='\0';
    if(findActivity(atividades,0,n_atividades,atividade)==-1)
        printf(NO_SUCH_ACTIVITY);
    else{
        mergeSort(t,tarefas_instante,aux,0,n_tarefas-1);
        for(i=0;i<n_tarefas;i++){
            id=tarefas_instante[i]-1;
            if(strcmp(t[id].atividade,atividade)==0)
                printf("%d %d %s\n",t[id].identificador,t[id].instante_inicial,t[id].descricao);
        }
    }
}

/*
  comando_a: char*,int -> int
  Adiciona uma atividade ao sistema ou, caso nao sejam dados argumentos, lista
  todas as atividades do sistema. Devolve o numero de atividades no
  sistema.
*/
int comando_a(char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE],int n_atividades){
	char c,atividade[MAX_DESCRICAO_ATIVIDADE];
   	int i=0,letras_m=0,estado=FORA;
   	while((c=getchar())!='\n'){
   		if(c!=' ' && estado==FORA)
    			estado=DENTRO;
  		if(estado==DENTRO && i<MAX_DESCRICAO_ATIVIDADE-1){
     			atividade[i]=c;
       			i++;
      			if(c>='a' && c<='z')
        			letras_m++;
    		}
  	}
  	atividade[i]='\0';
  	if(i>0){
   		if(findActivity(atividades,0,n_atividades,atividade)!=-1)
   			printf(DUPLICATE_ACTIVITY);
      		else if(n_atividades>=MAX_ATIVIDADE)
           		printf(TOO_MANY_ACTIVITIES);
    		else if(letras_m>0)
        		printf(INVALID_DESCRIPTION);
       		else{
        		strcpy(atividades[n_atividades],atividade);
        		return ++n_atividades;
     		}	
   	}
  	else
 		for(i=0;i<n_atividades;i++)
      			printf("%s\n",atividades[i]);	
	return n_atividades;
}

