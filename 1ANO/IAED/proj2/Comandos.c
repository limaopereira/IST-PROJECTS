/*
  Ficheiro: Comandos.c
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro em que se definem todas as funcoes dos comandos,
*/



/*INCLUDES*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Estruturas.h"
#define FORA 0
#define DENTRO 1

/*DEFINICAO DE FUNCOES*/

/*
  concatenate: char*,char* -> char*
  Concatena duas strings.
*/
char* concatenate(char* s1, char* s2){
        char* x=malloc(sizeof(char)*(strlen(s1)+strlen(s2)+1));
        int i=strlen(s1);
        strcpy(x,s1);
        while(*s2!='\0')
                x[i++]=*s2++;
        x[i]='\0';
        return x;
}

/*
  set: Dir*,char* -> ()
  Adiciona ou modifica o valor a armazenar.
*/
void set(Dir* d, char* s){
        Dir* n;
        char separador[]="/";
        char* token;
        char* valor;
        token=strtok(s,separador);
        while(token!=NULL){
                if(AVLsearch(*d->AVLchildren,token)==NULL){
                        n=DIRinit(token);
                        add_children(d,n);
                        d=n;
                }
                else
                        d=AVLsearch(*d->AVLchildren,token);
                token=strtok(NULL,separador);

        }
        valor=leValor();
        if(d->value!=NULL)
                free(d->value);
        d->value=str_dup(valor);
        free(valor);
        free(token);
}

/*
  leValor: () -> char*
  Lê um input dado pelo utilizador.
*/
char* leValor(){
        char c,valor[65536];
        char* s;
        int i=0,estado=FORA;
        while((c=getchar())!='\n'){
                if(c!=' ' && estado==FORA)
                        estado=DENTRO;
                if(estado==DENTRO)
                        valor[i++]=c;
        }
        valor[i]='\0';
        s=str_dup(valor);
        return s;
}

/*
  str_dup: char* -> char*
  Retorna um ponteiro para uma nova string que é o duplicado da string og.
*/
char* str_dup(char *og) {
        char* x=malloc(strlen(og)+1);
        strcpy(x,og);
        return x;
}

/*
  printR: LNode*, char*, int -> ()
  Imprime todos os caminhos e valores.
*/
void printR(LNode* node,char* s,int len){
	char separador[]="/";
	char* aux;
	if(node==NULL){
		s[len]='\0';
		return;
	}
	len=strlen(s);
	aux=concatenate(separador,node->directory->path);
	s=concatenate(s,aux);
	free(aux);
	if(node->directory->value!=NULL)
		printf("%s %s\n",s,node->directory->value);
	printR(node->directory->LLchildren->head,s,len);
	s[len]='\0';
	printR(node->next,s,len);
	free(s);
}

/*
  list: Dir* -> ()
  Lista todos os componentes imediatos de um sub-caminho.
*/
void list(Dir* d){
	char separador[]="/";
	char* path=leValor();
	char* token;
	if(*path!='\0'){
		token=strtok(path,separador);
		while(token!=NULL){
			d=AVLsearch(*d->AVLchildren,token);
			if(d==NULL){
				printf("not found\n");
				return;
			}
			token=strtok(NULL,separador);
		}
		free(token);
	}
	AVLsort(*d->AVLchildren);
	free(path);
}

/*
  find: Dir* -> ()
  Imprime o valor armazenado de um caminho.
*/
void find(Dir* d){
	char separador[]="/";
	char* path=leValor();
	char* token;
	token=strtok(path,separador);
	while(token!=NULL){
		d=AVLsearch(*d->AVLchildren,token);
		if(d==NULL){
			printf("not found\n");
			return;
		}
		token=strtok(NULL,separador);
	}
	if(d->value==NULL)
		printf("no data\n");
	else
		printf("%s\n",d->value);
	free(path);
}

/*
  delete_c_R: LNode* -> ()
  Função auxiliar para apagar todos os caminhos de um sub-caminho.
*/
void delete_c_R(LNode* node){
	if(node==NULL)
		return ;
	delete_c_R(node->directory->LLchildren->head);
	delete_c_R(node->next);
	deleteDIR(node->directory);
}

/*
  delete_c: Dir* -> ()
  Apaga todos os caminhos de um sub-caminho.
*/
void delete_c(Dir* d){
	Dir* aux=d;
	char separador[]="/";
        char* path=leValor();
        char* token;
        token=strtok(path,separador);
        while(token!=NULL){
		aux=d;
                d=AVLsearch(*d->AVLchildren,token);
                if(d==NULL){
                        printf("not found\n");
                        return;
                }
                token=strtok(NULL,separador);
        }
	delete_c_R(d->LLchildren->head);
	if(aux!=d){
		AVLdelete(aux->AVLchildren,d);
		list_delete(aux->LLchildren,d);
		deleteDIR(d);
	}
	else{
		if(*d->AVLchildren!=NULL)
			AVLfree(d->AVLchildren);
		if(d->LLchildren->head!=NULL)
			free_list(d->LLchildren);
	}
	free(path);
}

/*
  search: LNode*, char*, int, char* -> ()
  Procura o caminho dado um valor.
*/
void search(LNode* node,char* s,int len,char* valor){
	char separador[]="/";
	char* aux;
	if(node==NULL){
		s[len]='\0';
		return ;
	}

	len=strlen(s);
	aux=concatenate(separador,node->directory->path);
	s=concatenate(s,aux);
	free(aux);
	if(valor!=NULL && node->directory->value!=NULL && strcmp(node->directory->value,valor)==0){
		printf("%s\n",s);
		valor=NULL;
	}
	search(node->directory->LLchildren->head,s,len,valor);
	s[len]='\0';
	search(node->next,s,len,valor);
	free(s);
}

/*
  help: () -> ()
  Imprime os comandos disponíveis.
*/
void help(){
	printf("help: Imprime os comandos disponíveis.\n");
	printf("quit: Termina o programa.\n");
	printf("set: Adiciona ou modifica o valor a armazenar.\n");
	printf("print: Imprime todos os caminhos e valores.\n");
	printf("find: Imprime o valor armazenado.\n");
	printf("list: Lista todos os componentes imediatos de um sub-caminho.\n");
	printf("search: Procura o caminho dado um valor.\n");
	printf("delete: Apaga um caminho e todos os subcaminhos.\n");
}


