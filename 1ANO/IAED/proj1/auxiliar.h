/*
  Ficheiro: auxiliar.h
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro que define funcoes auxiliares.
*/

/*IMPORTS*/
#include <stdio.h>
#include <string.h>
#include "estruturas.h"

/*DECLARACAO DE FUNCOES*/
int findUser(char utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],
				int left,int right,char user[MAX_DESCRICAO_UTILIZADOR]);
int findActivity(char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE],
				int left, int right,char atividade[MAX_DESCRICAO_ATIVIDADE]);
int binarySearchDescricao(Tarefa t[],int arr[],int l, int r,
				char descricao[MAX_DESCRICAO_TAREFA],int aux1, int aux2);
void arrayInsert(int arr[],int x, int size);
void mergeSort(Tarefa t[],int arr[],int aux[], int l, int r);
void merge(Tarefa t[],int arr[],int aux[], int l,int m,int r);


/*DEFINICAO DE FUNCOES*/

/*
  findUser: char*,int,int,char* -> int
  Procura um utilizador numa tabela de utilizadores e caso seja sucedido 
  retorna o indice, caso contrario retorna -1.
*/
int findUser(char utilizadores[MAX_UTILIZADOR][MAX_DESCRICAO_UTILIZADOR],
				int left,int right,char user[MAX_DESCRICAO_UTILIZADOR]){
  	int i;
    for(i=left;i<right;i++)
    	if(strcmp(utilizadores[i],user)==0){
        	return i;
                }
    return -1;
}

/*
  findActivity: char*,int,int,char* -> int
  Procura uma atividade numa tabela de atividades e caso seja sucedido 
  retorna o indice, caso contrario retorna -1.
*/
int findActivity(char atividades[MAX_ATIVIDADE][MAX_DESCRICAO_ATIVIDADE],
				int left, int right,char atividade[MAX_DESCRICAO_ATIVIDADE]){
   	int i;
   	for(i=left;i<right;i++)
    	if(strcmp(atividades[i],atividade)==0)
        	return i;
   	return -1;
}

/*
  binarySearchDescricao: Tarefa*,int*,int,int,char*,int,int -> int
  Funcao auxiliar do algoritmo binary search. Procura por um id no vetor com os id's 
  ordenados por descricao. Caso encontre retorna -1, caso contrario retorna a 
  posicao onde o id deve ser inserido.
*/
int binarySearchDescricao(Tarefa t[],int arr[],int l, int r,
				char descricao[MAX_DESCRICAO_TAREFA],int aux1, int aux2){
    int mid,comparation;
    if(r<0)
 	return 0;
    if (r>=l){
   	mid=l+(r-l)/2;
    	comparation=strcmp(t[arr[mid]-1].descricao,descricao);
     	if(comparation==0)
      		return -1;
      	else if(comparation>0)
            	return binarySearchDescricao(t,arr, l, mid - 1, descricao,mid,comparation);
        return binarySearchDescricao(t,arr, mid + 1, r, descricao,mid,comparation);
    }
    if(aux2>0){
   	return aux1;
    }
    else{
   	return aux1+1;
    }
}

/*
  arrayInsert:int*,int,int,int -> ()
  Insere um id num determinado vetor de id's e num determinado indice.
*/
void arrayInsert(int arr[],int x, int size){
  	int i;
   	for(i=size;i>x;i--)
    	arr[i]=arr[i-1];
  	arr[x]=size+1;
}

/*
  mergeSort:Tarefa*,int*,int*,int,int -> ()
  Ordena um vetor de inteiros de acordo com o algoritmo merge sort.
*/  
void mergeSort(Tarefa t[],int arr[],int aux[], int l, int r){
    int m;
   	m = (r+l)/ 2;
    if(r<=l) return;
    mergeSort(t,arr,aux,l,m);
   	mergeSort(t,arr,aux,m+1,r);
  	merge(t,arr,aux,l,m,r);
}


/*
  merge:Tarefa*,int*,int*,int,int,int -> ()
  Funcao auxiliar do algoritmo merge sort.
*/

void merge(Tarefa t[],int arr[],int aux[], int l,int m,int r){
    int i,j,k;
    for(i=m+1;i>l;i--)
   		aux[i-1]=arr[i-1];
    for(j=m;j<r;j++)
        aux[r+m-j]=arr[j+1];
    for(k=l;k<=r;k++)
        if(t[aux[j]-1].instante_inicial<t[aux[i]-1].instante_inicial || i==m+1)
            arr[k]=aux[j--];
        else
            arr[k]=aux[i++];
}

