/*
  Ficheiro: AVL.c
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro em que se definem todas as funcoes da estrutura AVL
*/


/*INCLUDES*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Estruturas.h"



/*DEFINICAO DE FUNCOES*/

/*
  NEW: Dir*,AVLnode, AVLnode -> AVLnode
  Cria um novo nó da árvore AVL.
*/
AVLnode NEW(Dir* d, AVLnode l, AVLnode r){
	AVLnode x=(AVLnode) malloc(sizeof(struct AVLnode));
	x->directory=d;
	x->l=l;
	x->r=r;
	x->height=1;
	return x;
}

/*
  AVLinit: AVLnode* -> ()
  Inicia uma nova árvore AVL
*/
void AVLinit(AVLnode* head){
	*head=NULL;
}

/*
  height: AVLnode -> int
  Devolve a altura de um nó da árvore AVL.
*/
int height(AVLnode h){
	if(h==NULL)
		return 0;
	return h->height;
}

/*
  rotL: AVLnode -> AVLnode
  Efetua uma operação de rotação esquerda num nó da árvore AVL.
*/
AVLnode rotL(AVLnode h){
	int hleft, hright, xleft, xright;
	AVLnode x=h->r;
	h->r=x->l;
	x->l=h;
	hleft=height(h->l);
	hright=height(h->r);
	h->height=hleft > hright ? hleft+1 : hright+1;
	xleft=height(x->l);
	xright=height(x->r);
	x->height=xleft > xright ? xleft+1 : xright +1;
	return x;
}

/*
  rotR: AVLnode -> AVLnode
  Efetua uma operação de rotação direita num nó da árvore AVL.
*/
AVLnode rotR(AVLnode h){
	int hleft, hright, xleft, xright;
	AVLnode x=h->l;
	h->l=x->r;
	x->r=h;
	hleft=height(h->l);
	hright=height(h->r);
	h->height=hleft > hright ? hleft+1 : hright+1;
	xleft=height(x->l);
	xright=height(x->r);
	x->height=xleft > xright ? xleft+1 : xright+1;
	return x;
}

/*
  rotLR: AVLnode -> AVLnode
  Efetua uma operação de rotação esquerda-direita num nó da árvore AVL.
*/
AVLnode rotLR(AVLnode h){
	if(h==NULL)
		return h;
	h->l=rotL(h->l);
	return rotR(h);
}

/*
  rotRL: AVLnode -> AVLnode
  Efetua uma operação de rotação direita-esquerda num nó da árvore AVL.
*/
AVLnode rotRL(AVLnode h){
	if(h==NULL)
		return h;
	h->r=rotR(h->r);
	return rotL(h);
}

/*
  balance: AVLnode -> int
  Calcula o balance factor de um nó da árvore AVL.
*/
int balance(AVLnode h){
	if(h==NULL)
		return 0;
	return height(h->l)-height(h->r);
}

/*
  AVLbalance: AVLnode -> AVLnode
  Equilibra a árvore.
*/
AVLnode AVLbalance(AVLnode h){
	int balanceFactor,hleft,hright;
	if(h==NULL)
		return h;
	balanceFactor=balance(h);
	if(balanceFactor>1){
		if(balance(h->l)>=0)
			h=rotR(h);
		else
			h=rotLR(h);
	}
	else if(balanceFactor<-1){
		if(balance(h->r)<=0)
			h=rotL(h);
		else
			h=rotRL(h);
	}
	else{
		hleft=height(h->l);
		hright=height(h->r);
		h->height=hleft > hright ? hleft+1 : hright+1;
	}
	return h;
}

/*
  AVLnode insertR: AVLnode, Dir* -> AVLnode
  Função auxiliar de inserção de uma nova diretoria na árvore.
*/
AVLnode insertR(AVLnode h, Dir* d){
	if(h==NULL)
		return NEW(d,NULL,NULL);
	if(strcmp(d->path,h->directory->path)<0)
		h->l=insertR(h->l, d);
	else
		h->r=insertR(h->r, d);
	h=AVLbalance(h);
	return h;
}

/*
  AVLinsert: AVLnode*,Dir* -> ()
  Insere uma nova diretoria na árvore.
*/
void AVLinsert(AVLnode* head, Dir* d){
      	*head=insertR(*head,d);
}

/*
  max: AVLnode -> AVLnode
  Devolve o diretoria com o maior caminho na árvore, tendo em conta a ordem ASCII.
*/
AVLnode max(AVLnode h){
	if(h==NULL || h->r ==NULL)
		return h;
	else
		return max(h->r);
}

/*
  deleteR: AVLnode,Dir* -> AVLnode
  Função auxiliar de remoação de um diretoria da árvore.
*/
AVLnode deleteR(AVLnode h, Dir* k){
	if(h==NULL)
		return h;
	else if(strcmp(k->path,h->directory->path)<0)
		h->l=deleteR(h->l,k);
	else if(strcmp(h->directory->path,k->path)<0)             
		h->r=deleteR(h->r,k);
	else{
		if(h->l!=NULL && h->r != NULL){
			AVLnode aux=max(h->l);
			{Dir* x; x=h->directory;h->directory=aux->directory;aux->directory=x;}
			h->l=deleteR(h->l,aux->directory);
		}
		else{
			AVLnode aux=h;
			if(h->l==NULL && h->r==NULL)
				h=NULL;
			else if(h->l==NULL)
				h=h->r;
			else
				h=h->l;
			free(aux);
		}
	}
	h=AVLbalance(h);
	return h;
}

/*
  AVLdelete: AVLnode*,Dir* -> ()
  Remove um dicionário da árvore.
*/
void AVLdelete(AVLnode* head, Dir* k){
	*head=deleteR(*head,k);
}

/*
  searchR: AVLnode,char* -> Dir*
  Função auxiliar de procura de uma diretoria com um determinado caminho.
*/
Dir* searchR(AVLnode h, char* v){
	if(h==NULL)
		return NULL;
	if(strcmp(v,h->directory->path)==0) 
		return h->directory;
	if(strcmp(v,h->directory->path)<0)                          
		return searchR(h->l,v);
	else
		return searchR(h->r,v);
}

/*
  AVLsearch: AVLnode,char* -> Dir*
  Devolve uma diretoria da árvore com um determinado caminho.
*/
Dir* AVLsearch(AVLnode head, char* v){
	return searchR(head,v);
}

/*
  count: AVLnode -> int
  Função auxiliar que calcula o número de diretorias de nós da árvore.
*/
int count(AVLnode h){
	if(h==NULL)
		return 0;
	else
		return count(h->r)+count(h->l)+1;
}

/*
  AVLcount: AVLnode -> int
  Devolve o número de nós da árvore.
*/
int AVLcount(AVLnode head){
	return count(head);
}

/*
  sortR: AVLnode -> ()
  Função auxiliar que imprime as diretorias da árvore por ordem alfabética.
*/
void sortR(AVLnode h){
	if(h==NULL)
		return;
	sortR(h->l);
	printf("%s\n",h->directory->path);
	sortR(h->r);
}

/*
  AVLsort: AVLnode -> ()
  Imprime as diretorias da árvore por ordem alfabética.
*/
void AVLsort(AVLnode head){
	sortR(head);
}

/*
  freeR: AVLnode -> AVLnode
  Função auxiliar que liberta os nós da árvore.
*/
AVLnode freeR(AVLnode h){
	if(h==NULL)
		return h;
	h->l=freeR(h->l);
	h->r=freeR(h->r);
	free(h);
	return NULL;

}

/*
  AVLfree: AVLnode -> ()
  Liberta os nós da árvore.
*/
void AVLfree(AVLnode* head){
	*head=freeR(*head);
}


