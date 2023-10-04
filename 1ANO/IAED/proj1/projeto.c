/*
  Ficheiro: projeto.c
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ponto de entrada para o projeto de IAED20/21
*/


/*IMPORTS*/
#include "funcoes.h"

/*FUNCAO MAIN*/

/*
  main: () -> int
  Primeira funcao a ser executada, trata de reconhecer o comando presente no input.
*/

int main(){
    Tarefa tarefas[MAX_TAREFA];
    int n_tarefas=0,n_utilizadores=0,n_atividades=3,duracao_global=0,tarefas_descricao[MAX_TAREFA],
        tarefas_instante[MAX_TAREFA];
    char c,utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],
        atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE]={TO_DO,IN_PROGRESS,DONE};
    while((c=getchar())!='q'){
        switch(c){
            case 't':
                n_tarefas=comando_t(tarefas,n_tarefas,tarefas_descricao,tarefas_instante);
                break;
            case 'l':
                comando_l(tarefas,n_tarefas,tarefas_descricao);
                break;
            case 'n':
                duracao_global+=comando_n(duracao_global);
                break;
            case 'u':
                n_utilizadores=comando_u(utilizadores,n_utilizadores);
                break;
            case 'm':
                comando_m(tarefas,n_tarefas,utilizadores,n_utilizadores,atividades,n_atividades,
                duracao_global);
                break;
            case 'd':
                comando_d(tarefas,n_tarefas,atividades,n_atividades,tarefas_instante);
                break;
            case 'a':
                n_atividades=comando_a(atividades,n_atividades);
                break;
        }
    }
    return 0;
}


