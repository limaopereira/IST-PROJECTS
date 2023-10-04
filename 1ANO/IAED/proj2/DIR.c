/*
  Ficheiro: DIR.c
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro em que se definem todas as funcoes da estrutura DIR.
*/


/*INCLUDES*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Estruturas.h"

/*DEFINICAO DE FUNCOES*/

/*
  DIRinit: char* -> Dir*
  Inicia uma nova diretoria.
*/
Dir* DIRinit(char* path){
	Dir* d=(Dir*) malloc(sizeof(Dir));
	DList* l=mk_list();
	AVLnode* a=malloc(sizeof(struct AVLnode));
	AVLinit(a);
	d->path=str_dup(path);
	d->value=NULL;
	d->AVLchildren=a;
	d->LLchildren=l;
	return d;
}

/*
  add_children: Dir* -> ()
  Adiciona uma diretoria a árvore AVL e a lista de uma diretoria.
*/
void add_children(Dir* d,Dir* c){
	AVLinsert(d->AVLchildren,c);
	add_last(d->LLchildren,c);
}

/*
  deleteDIR: Dir* -> ()
  Elimina uma diretoria.
*/
void deleteDIR(Dir* d){
	free(d->path);
	free(d->value);
	if(*d->AVLchildren!=NULL)
		AVLfree(d->AVLchildren);
	if(d->LLchildren->head!=NULL)
		free_list(d->LLchildren);
	free(d->AVLchildren);
	free(d->LLchildren);
	free(d);
}

