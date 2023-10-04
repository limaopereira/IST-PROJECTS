/*
  Ficheiro: estruturas.h
  Autor: Manuel Pereira ist198580/al98580
  Descricao: Ficheiro em que se definem todas as estruturas.
*/

#ifndef _Estruturas_
#define _Estruturas_

/*DECLARACAO DE ESTRUTURAS*/

typedef struct Directory Dir;
typedef struct AVLnode* AVLnode;
typedef struct ListNode LNode;
typedef struct DoublyList DList;

struct Directory{
	char* path;
	char* value;
	AVLnode* AVLchildren;
	DList* LLchildren;
};


struct AVLnode{
	Dir* directory;
	AVLnode l,r;
	int height;
};


struct ListNode{
	Dir* directory;
	LNode *next,*previous;
};


struct DoublyList{
	LNode *head,*last;
};

/*DECLARACAO DE FUNCOES DIR*/

Dir* DIRinit(char*);
void add_children(Dir*,Dir*);
void deleteDIR(Dir*);

/*DECLARACAO DE FUNCOES AVL*/


void AVLinit(AVLnode*);
int AVLcount(AVLnode);
Dir* AVLsearch(AVLnode,char*);
void AVLinsert(AVLnode*,Dir*);
void AVLdelete(AVLnode*, Dir*);
void AVLsort(AVLnode);
void AVLfree(AVLnode*);

/*DECLARACAO DE FUNCOES LINKEDLIST*/


DList* mk_list();
void free_list(DList* l);
void add_last(DList* l, Dir* d);
void printL(DList* l);
void list_delete(DList* l, Dir* d);

/*DECLARACAO DE FUNCOES COMANDOS*/


void printR(LNode* node,char* s,int len);
void list(Dir* d);
void find(Dir* d);
void delete_c_R(LNode* node);
void delete_c(Dir* d);
void search(LNode* node,char* s,int len,char* valor);
void help();
char* leValor();
void set(Dir*,char*);
char* concatenate(char*,char*);
char* str_dup(char*);



#endif

