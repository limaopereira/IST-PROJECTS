% Manuel Pereira, n. 98580


:- [codigo_comum].


% Estrutura Espaco
% Representada por espaco(soma, variaveis), em que soma e a soma das posicoes 
% do espaco e variaveis e a lista de variaveis do espaco.

cria_espaco(Soma,Lista_V,espaco(Soma,Lista_V)).
variaveis_de(espaco(_,LV),LV).
soma_de(espaco(S,_),S).


% Estrutura par
% Representada por (pos, num), em que todas as listas numa lista de permutacoes,
% contem o numero num na posicao pos.   

cria_par(Pos,Numero,(Pos,Numero)).
numero_de((_,N),N).
posicao_de((P,_),P).


% combinacoes_soma(N, Els, Soma, Combs), em que N e um inteiro, Els e uma lista
% de inteiros, e Soma e um inteiro, significa que Combs e a lista ordenada
% cujos elementos sao as combinacoes N a N, dos elementos de Els cuja soma e
% Soma.

combinacoes_soma(N,Els,Soma,Combs) :-
	findall(SL,(combinacao(N,Els,SL),sum_list(SL,Soma)),Combs).


% permutacoes_soma(N, Els, Soma, Perms), em que N e um inteiro, Els e uma lista 
% de inteiros, e Soma e um inteiro, significa que Perms e a lista ordenada
% cujos elementos sao as permutacoes das combinacoes N a N, dos elementos de
% Els cuja soma e Soma.

permutacoes_soma(N,Els,Soma,Perms) :-
	combinacoes_soma(N,Els,Soma,Combs),
	findall(Perm,(member(X,Combs),permutation(X,Perm)),Perms_U),
	sort(Perms_U,Perms).


% espaco_fila(Fila, Esp, H_V), em que Fila e uma fila de uma puzzle e H_V e um
% dos atomos h ou v, conforme se trate de uma fila horizontal ou vertical,
% respectivamente, significa que Esp e um espaco de Fila.

espaco_fila(Fila, Esp, H_V) :- 
	espaco_fila_aux(Fila, Esp, H_V, _, []).

espaco_fila_aux([],Esp,_,Soma_aux,Lista_aux) :-
	length(Lista_aux,N),
	N>0,
	cria_espaco(Soma_aux,Lista_aux,Esp).

espaco_fila_aux([P|_],Esp,_,Soma_aux, Lista_aux) :-
	nonvar(P),
	length(Lista_aux,N),
	N>0,
	cria_espaco(Soma_aux,Lista_aux,Esp).

espaco_fila_aux([P|R], Esp, H_V, _, _) :-
	nonvar(P),
	H_V==v,
	nth0(0,P,Soma_aux1),
	espaco_fila_aux(R,Esp,H_V,Soma_aux1,[]).

espaco_fila_aux([P|R], Esp, H_V, _, _) :-
	nonvar(P),
	H_V==h,
	nth0(1,P,Soma_aux1),
	espaco_fila_aux(R,Esp,H_V,Soma_aux1,[]).

espaco_fila_aux([P|R], Esp, H_V, Soma_aux, Lista_aux) :-
	var(P),
	append(Lista_aux,[P],Lista_aux1),
	espaco_fila_aux(R,Esp,H_V,Soma_aux, Lista_aux1).


% espacos_fila(H_V, Fila, Espacos), em que Fila e uma fila de uma grelha e H_V
% e um dos atomos h ou v, signfica que Espacos e a lista de todos os espacos de
% Fila, da esquerda para a direita.

espacos_fila(H_V, Fila, Espacos) :-
	bagof(Esp, espaco_fila(Fila, Esp, H_V), Espacos),!.

espacos_fila(_,_,[]).


% espacos_puzzle(Puzzle, Espacos), em que Puzzle e um puzzle, significa que
% Espacos e a lista de espacos de Puzzle.

espacos_puzzle(Puzzle, Espacos) :-
	bagof(Esp,Fila^(member(Fila,Puzzle),espaco_fila(Fila,Esp,h)),Espacos_h),
	mat_transposta(Puzzle,Puzzle_t),
	bagof(Esp,Fila^(member(Fila,Puzzle_t),espaco_fila(Fila,Esp,v)),Espacos_v),
	append([Espacos_h,Espacos_v],Espacos).


% member_sem_unificar(El, L), em que El e um elemento e L e uma lista, verifica
% se El e membro de L, sem unificar. 

member_sem_unificar(E,[P|_]):- 
	E==P,!.
member_sem_unificar(E,[_|R]):- 
	member_sem_unificar(E,R).


% variaveis_comuns(L1, L2), em que L1 e L2 sao duas lista, verifica se existem
% elementos comuns entre as duas listas.

variaveis_comuns([P|_],L2) :- 
	member_sem_unificar(P, L2), !.

variaveis_comuns([_|R],L2) :- 
	variaveis_comuns(R,L2).


% espacos_com_posicoes_comuns(Espacos, Esp, Esps_com), em que Espacos e uma
% lista de espacos e Esp e um espaco, significa que Esps_com e a lista de
% espacos com variaveis em comum com Esp, exceptuando Esp.

espacos_com_posicoes_comuns([],_,[]).
	
espacos_com_posicoes_comuns([P|R],Esp,[P| R_aux]) :-
	variaveis_de(P,L_P),
	variaveis_de(Esp, L_E),
	P \== Esp,
	variaveis_comuns(L_P,L_E),
	espacos_com_posicoes_comuns(R,Esp,R_aux),!.

espacos_com_posicoes_comuns([_|R],Esp,Esps_com) :-
	espacos_com_posicoes_comuns(R,Esp,Esps_com).


% permutacoes_soma_espacos(Espacos, Perms_Soma), em que Esapcs e uma lista de
% espacos, significa que Perms_soma e a lista de listas de 2 elementos, em que
% o 1 elemento e um espaco de Espacos e o 2 elemento e a lista ordenada de
% permutacoes cuja soma e igual a soma do espaco. 

permutacoes_soma_espacos([],[]).

permutacoes_soma_espacos([P|R],[PS|R_aux]) :-
	variaveis_de(P,LV),
	length(LV,N),
	soma_de(P,Soma),
	permutacoes_soma(N,[1,2,3,4,5,6,7,8,9],Soma,Perms),
	append([[P],[Perms]],PS),
	permutacoes_soma_espacos(R,R_aux).


% permutacao_possivel_espaco(Perm, Esp, Espacos, Perms_soma), em que Perm e uma
% permutacao, Esp e um espaco, Espacos e uma lista de espacos, e Perms_soma e
% uma lista de listas tal como obtida pelo predicado permutacoes_soma_spacos,
% significa que Perm e uma permutacao possivel para o espaco Esp.  

permutacao_possivel_espaco(Perm,Esp,Espacos,Perms_soma)	:- 	
	member(X,Perms_soma),
	nth1(1,X,L),
	nth1(2,X,LP),
	L==Esp,
	member(Perm,LP),
	espacos_com_posicoes_comuns(Espacos,Esp,Esps_com),
	findall(LCP,(member(Esp_com,Esps_com),
		member(Y,Perms_soma),nth1(1,Y,LC),nth1(2,Y,LCP),LC==Esp_com),
			Perms_com),
	permutacao_possivel_espaco_aux(Perm,Esp,Espacos,Perms_soma, Perms_com).

permutacao_possivel_espaco_aux(_,_,_,_,[]).

permutacao_possivel_espaco_aux([P|R],_,_,_,[P_com | R_com]) :-
	append(P_com,P_com_f),
	member(P,P_com_f),!,
	permutacao_possivel_espaco_aux(R,_,_,_,R_com).


% permutacoes_possiveis_espaco(Espacos, Perms_soma, Esp, Perms_poss), em que
% Espacos e uma lista de espacos, Perms_soma e uma lista de listas tal como
% obtida pelo predicado permutacoes_soma_espacos, e Esp e um espaco, significa
% que Perms_poss e uma lista de 2 elementos em que o primeiro e a lista de
% variaveis de Esp e o segundo e a lista ordenada de permutacoes possiveis para
% o espaco Esp.
	
permutacoes_possiveis_espaco(Espacos, Perms_soma, Esp, Perms_poss) :-
	findall(Perm,permutacao_possivel_espaco(Perm,Esp,Espacos,Perms_soma),
		Perms),
	variaveis_de(Esp, LV),
	append([LV],[Perms],Perms_poss).


% permutacoes_possiveis_espacos(Espacos, Perms_poss_esps), em que Espacos e uma
% lista de espacos, significa que Perms_poss_esps e a lista de permutacoes
% possiveis.
	
permutacoes_possiveis_espacos(Espacos,Perms_poss_esps) :-
	permutacoes_soma_espacos(Espacos,Perms_soma),
	bagof(Perms_poss,Esp^(member(Esp,Espacos),
		permutacoes_possiveis_espaco(Espacos,Perms_soma,Esp,Perms_poss)),
			Perms_poss_esps).


% numeros_comuns(Lst_Perms, Numeros_comuns), em que Lst_Perms e uma lista de
% permutacoes, significa que Numeros_comuns e a lista de pares(pos,numero),
% significando que todas as listas de Lst_Perms contem o numero numero na
% posicao pos

numeros_comuns([P|R],Numeros_comuns) :- 
	length(P, N),
	findall(Par,(between(1,N,N_L),nth1(N_L,P,Y),
		forall(member(SL,[P|R]),(nth1(N_L,SL,X),X==Y)),
			cria_par(N_L,Y,Par)),Numeros_comuns).


% atribui_comuns(Perms_Possiveis), em que Perms_Possiveis e uma lista de
% permutacoes possiveis, actualiza esta lista atribuindo a cada espaco numeros
% comuns a todas as permutacoes possiveis para esse espaco.

atribui_comuns([]) :- !.

atribui_comuns([P|R]) :-
	nth1(1,P,V),
	nth1(2,P,Perms),
	numeros_comuns(Perms,Pares),
	atribui_comuns_aux(V,Pares),
	atribui_comuns(R).

atribui_comuns_aux(_,[]):- !.	

atribui_comuns_aux(V, [Par|R_Par]):-
	posicao_de(Par,Pos),
	numero_de(Par,Num),
	nth1(Pos,V,Num),
	atribui_comuns_aux(V,R_Par).


% retira_impossiveis(Perms_Possiveis, Novas_Perms_Possiveis), em que
% Perms_Possiveis e uma lista de permutacoes possiveis, significa que
% Novas_Perms_Possiveis e o resultado de tirar permutacoes impossiveis de
% Perms_Possiveis.

retira_impossiveis([],[]).

retira_impossiveis([P|R],[NP |R_NP]) :- 
	nth1(1,P,V),
	nth1(2,P,Perms),
	include(subsumes_term(V),Perms,Nova_Perms),
	append([[V],[Nova_Perms]],NP),
	retira_impossiveis(R,R_NP).


% simplifica(Perms_Possiveis, Novas_Perms_Possiveis), em que Perms_Possiveis e
% uma lista de permutacoes possiveis, significa que Novas_Perms_Possiveis e o
% resultado de simplificar Perms_Possiveis.

simplifica(Perms_Possiveis,Novas_Perms_Possiveis) :-
	atribui_comuns(Perms_Possiveis),
	retira_impossiveis(Perms_Possiveis,P),
	Perms_Possiveis\==P,!,
	simplifica(P,Novas_Perms_Possiveis).

simplifica(Perms_Possiveis,Novas_Perms_Possiveis) :-
	atribui_comuns(Perms_Possiveis),
	retira_impossiveis(Perms_Possiveis,Novas_Perms_Possiveis).


% inicializa(Puzzle,Perms_Possiveis), em que Puzzle e um puzzle, significa que
% Perms_Possiveis e a lista de permutacoes possiveis simplificada para Puzzle.

inicializa(Puzzle,Perms_Possiveis) :-
	espacos_puzzle(Puzzle,Espacos),
	permutacoes_possiveis_espacos(Espacos,Perms),
	simplifica(Perms,Perms_Possiveis).


% escolhe_menos_alternativas(Perms_Possiveis, Escolha), em que Perms_Possiveis
% e uma lista de permutacoes possiveis, significa que Escolha e o primeiro
% elemento de Perms_Possiveis com mais de uma permutacao, que tenha associado
% um numero minimo de permutacoes possiveis.  

escolhe_menos_alternativas(Perms_Possiveis,Escolha) :- 
	findall(N,(member(X,Perms_Possiveis),X=[_,Perms],length(Perms,N),N>1),
		L_N),
	min_list(L_N,Min),
	bagof(X,(member(X,Perms_Possiveis),X=[_,Perms],length(Perms,N),N==Min),
		L_Perms),
	nth1(1,L_Perms,Escolha).	


% experimenta_perm(Escolha, Perms_Possiveis, Novas_Perms_Possiveis), em que 
% Perms_Possiveis e uma lista de permutacoes possiveis, e Escolha e um dos 
% elementos escolhidos atraves do predicado escolhe_menos_alternativas, sendo 
% que Novas_Perms_Possiveis e o resultado de substituir, em Perms_Possiveis, o
% elemento Escolha pelo elemento [Esp, [Perm]], onde Esp e o espaco de Escolha
% e Perm e uma permutacao da lista de permutacoes de Escolha.    

experimenta_perm(_,[],[]).

experimenta_perm(Escolha,[P|R],[NP | R_NP]) :-
	P\==Escolha,!,
	NP=P,
	experimenta_perm(Escolha,R,R_NP).

experimenta_perm(Escolha,[P|R],[NP | R_NP]) :-
	P==Escolha,
	nth1(1,Escolha,Esp),
	nth1(2,Escolha,Lst_Perms),
	member(Perm,Lst_Perms),
	Esp=Perm,
	append([[Esp,[Perm]]],NP),
	experimenta_perm(Escolha,R,R_NP).


% resolve_aux(Perms_Possiveis, Novas_Perms_Possiveis), em que Perms_Possiveis e
% uma lista permutacoes possiveis, significa que Novas_Perms_Possiveis e o
% resultado de aplicar os predicados escolhe_menos_alternativas,
% experimenta_perm e simplifica, ate nao existir diferencas entre
% Perms_Possiveis e Novas_Perms_Possiveis.
	
resolve_aux(Perms_Possiveis, Novas_Perms_Possiveis) :-
	escolhe_menos_alternativas(Perms_Possiveis,Escolha),!,
	experimenta_perm(Escolha,Perms_Possiveis,P),
	simplifica(P,NP),
	resolve_aux(NP,Novas_Perms_Possiveis).

resolve_aux(Perms_Possiveis, Novas_Perms_Possiveis) :-
	Novas_Perms_Possiveis=Perms_Possiveis.


% resolve(Puz), em que Puz e um puzzle, resolve esse puzzle, isto e, apos a
% invocacao deste predicado a grelha de Puz tem todas as variaveis substituidas
% por numeros que respeitam as restricoes Puz.

resolve(Puz) :-
	inicializa(Puz,Perms_Possiveis),
	resolve_aux(Perms_Possiveis,_).
