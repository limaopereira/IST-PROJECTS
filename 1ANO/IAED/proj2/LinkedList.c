/*
  Ficheiro: LinkedList.c
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro em que se definem todas as funcoes da estrutura LinkedList.
*/


/*INCLUDES*/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Estruturas.h"


/*DEFINICAO DE FUNCOES*/

/*
  mk_list: () -> DList*
  Cria uma nova lista.
*/
DList* mk_list(){
	DList* new_list=(DList*) malloc(sizeof(DList));
	new_list->head=NULL;
	new_list->last=NULL;
	return new_list;
}

/*
  printL: DList* -> ()
  Imprime todos os elementos da lista.
*/
void printL(DList* l){
	LNode* x;
	for(x=l->head;x!=NULL;x=x->next){
		printf("path=%s,value=%s\n",x->directory->path,x->directory->value);
	}
}

/*
  free_list: DList* -> ()
  Elimina todos os elementos da lista.
*/
void free_list(DList* l){
	while(l->head!=NULL){
		LNode* next=l->head->next;
		/*deleteDIR(l->head->directory);*/
		free(l->head);
		l->head=next;
	}
	l->last=NULL;
}

/*
  list_delete: DList*, Dir* -> ()
  Elimina uma diretoria da lista.
*/
void list_delete(DList *lst, Dir* d){
    LNode* x;
    for (x=lst->head;x!= NULL;x=x->next){
        if (x->directory==d){
            break; 
        }
    } 
    if(x->previous==NULL && x->next==NULL){
	    lst->head=NULL;
	    lst->last=NULL;
    }			
    else if (x->previous==NULL){	
        lst->head = x->next;
        lst->head->previous = NULL; 
    }
    else if (x->next==NULL){
        lst->last=x->previous;
        lst->last->next=NULL;
    } 
    else{
        x->previous->next = x->next;
        x->next->previous = x->previous;
    }
    free(x);
}

/*
  add_last: DList*, Dir* -> ()
  Adiciona uma diretoria ao fim da lista.
*/
void add_last(DList* l, Dir* d){
	LNode* new_node=(LNode*) malloc(sizeof(LNode));
	new_node->directory=d;
	new_node->previous=l->last;
	new_node->next=NULL;
	if(l->last!=NULL)
		l->last->next=new_node;
	l->last=new_node;
	if(l->head==NULL)
		l->head=new_node;
}


