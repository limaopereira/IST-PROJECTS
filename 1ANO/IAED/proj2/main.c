/*
  Ficheiro: main.c
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ponto de entrada para o segundo projeto de IAED20/21
*/

/*INCLUDES*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Estruturas.h"
#define MAX_COMANDO 7

/*FUNCAO MAIN*/

/*
  main: () -> int
  Primeira funcao a ser executada, trata de reconhecer o comando presente no input.
*/
int main(){
	char* bufferPRINT;
	char* bufferSEARCH;
	char comando[MAX_COMANDO];
	char buffer[65536];
	char* valor;
	Dir* root=DIRinit("/");
	scanf("%s",comando);
	while(strcmp(comando,"quit")!=0){
		if(strcmp(comando,"help")==0)
			help();
		else if(strcmp(comando,"set")==0){
			scanf("%s",buffer);
			set(root,buffer);
		}
		else if(strcmp(comando,"print")==0){
			bufferPRINT=str_dup("");
			printR(root->LLchildren->head,bufferPRINT,0);
			free(bufferPRINT);
		}
		else if(strcmp(comando,"list")==0)
			list(root);
		else if(strcmp(comando,"find")==0)
			find(root);
		else if(strcmp(comando,"search")==0){
			valor=leValor();
			bufferSEARCH=str_dup("");
			search(root->LLchildren->head,bufferSEARCH,0,valor);
			free(valor);
			free(bufferSEARCH);
		}
		else if(strcmp(comando,"delete")==0)
			delete_c(root);
		scanf("%s",comando);
	}
	delete_c(root);
	deleteDIR(root);
	return 0;
}
