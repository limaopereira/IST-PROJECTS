#Manuel Pereira, n. 98580


# TAD posicao
# Representacao interna: A posica e representada por uma lista em que o  
#                        primeiro elemento contem a coluna c e o segundo 
#                        elemento contem a linha l ([c,l])
# cria_posicao: string x string -> posicao
# cria_copia_posicao: posicao -> posicao
# obter_pos_c: posicao -> string
# obter_pos_l: posicao -> string
# eh_posicao: universal -> booleano
# posicoes_iguais: universal x universal -> booleano
# posicao_para_str: posicao -> str


def cria_posicao(string1,string2):
    #string x string -> posicao
    '''
    Recebe 2 cadeias de carateres correspondentes a coluna c e a linha l de 
    uma posicao e devolve a posicao correspondente.
    '''    
    if string1 not in ['a','b','c'] or string2 not in ['1','2','3']:
        raise ValueError('cria_posicao: argumentos invalidos')
    else:
        return [string1,string2]


def cria_copia_posicao(pos):
    #posicao -> posicao
    '''
    Recebe uma posicao e devolve uma copia nova da posicao.
    '''        
    return [pos[0],pos[1]]


def obter_pos_c(pos):
    #posicao -> string
    '''
    Recebe uma posicao e devolve a componente coluna c.
    '''         
    return pos[0]


def obter_pos_l(pos):
    #posicao -> string
    '''
    Recebe uma posicao e devolve a componente linha l.
    '''             
    return pos[1]


def eh_posicao(arg):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True caso o seu argumento 
    seja um TAD posicao e False caso contrario.
    '''             
    return type(arg)==list and len(arg)==2 \
           and arg[0] in ['a','b','c'] and arg[1] in ['1','2','3']


def posicoes_iguais(pos1,pos2):
    #universal x universal -> booleano
    '''
    Recebe 2 argumento de qualquer tipo e devolve True caso ambos os 
    argumentos sejam um TAD posicao e iguais, e devolve False caso contrario. 
    '''     
    return eh_posicao(pos1) and eh_posicao(pos2) and pos1==pos2


def posicao_para_str(pos):
    #posicao -> string
    '''
    Recebe uma posicao e devolve a cadeia de carateres 'cl' que representa o seu
    argumento, sendo os valores c e l as componentes coluna e linha da posicao.
    '''         
    return str(pos[0])+str(pos[1])

    
def obter_posicoes_adjacentes(pos):
    #posicao -> tuplo de posicoes
    '''
    Recebe uma posicao e devolve um tuplo com as posicoes adjacentes a posicao 
    de acordo com a ordem de leitura do tabuleiro.
    '''         
    colunas=['a','b','c']
    linhas=['1','2','3']
    ic=colunas.index(obter_pos_c(pos))
    il=linhas.index(obter_pos_l(pos))    
    pos_adj=()
    for la in range(3):
        for ca in range(3):
            if not posicoes_iguais(pos,cria_posicao(colunas[ca],linhas[la]))\
                and (la==il+1 or la==il or la==il-1)\
                and (ca==ic+1 or ca==ic or ca==ic-1):
                if (ic+il)%2==0:
                    pos_adj=pos_adj+(cria_posicao(colunas[ca],linhas[la]),)
                else:
                    if not (la==il+1 and ca==ic+1)\
                       and not (la==il+1 and ca==ic-1)\
                       and not (la==il-1 and ca==ic+1)\
                       and not (la==il-1 and ca==ic-1):
                        pos_adj=pos_adj+(cria_posicao(colunas[ca],linhas[la]),)
    return pos_adj


# TAD peca
# Representacao interna: A peca e representada por uma lista que contem apenas
#                        o identificador de um dos dois jogadores 
#                        (['X'] ou ['O']) 
# cria_peca: string -> peca
# cria_copia_peca: peca -> peca
# eh_peca: universal -> booleano
# pecas_iguais: universal x universal -> booleano
# peca_para_str: peca -> string


def cria_peca(string):
    #string -> peca
    '''
    Recebe uma cadeia de carateres correspondente ao identificador de um dos 
    dois jogador ('X' ou 'O') ou a uma peca livre (' ') e devolve a peca 
    correspondente.
    '''             
    if string in ('X','O',' '):
        return [string]
    else:
        raise ValueError('cria_peca: argumento invalido')

def cria_copia_peca(peca):
    #peca -> peca
    '''
    Recebe uma peca e devolve a copia nova da peca.
    '''        
    return [peca[0]]


def eh_peca(arg):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True caso o argumento seja um
    TAD peca e False caso contrario.
    '''        
    return type(arg)==list and len(arg)==1 and arg[0] in ('X','O',' ')


def pecas_iguais(peca1,peca2):
    #universal x universal -> booleano
    '''
    Recebe 2 argumento de qualquer tipo e devolve True caso ambos os 
    argumentos sejam um TAD peca e iguais, e devolve False caso contrario. 
    '''         
    return eh_peca(peca1) and eh_peca(peca2) and peca1==peca2


def peca_para_str(peca):
    #peca -> string
    '''
    Recebe uma peca e devolve a cadeia de carateres que representa o jogador 
    dono da peca, '[X]', '[O]' ou '[]'.
    '''             
    return '[{0}]'.format(peca[0])


def peca_para_inteiro(peca):
    #peca -> inteiro
    '''
    Recebe uma peca e devolve um valor inteiro 1, -1 ou 0, dependendo se a peca 
    e do jogador 'X', 'O' ou livre, respetivamente.
    '''             
    if pecas_iguais(peca,cria_peca('X')):
        return 1
    elif pecas_iguais(peca,cria_peca('O')):
        return -1
    else:
        return 0


# TAD tabuleiro
# Representacao interna: O tabuleiro e representado por uma lista com 3 
#                        sublistas em que cada uma contem 3 pecas.
#                        ([[peca,peca,peca],[peca,peca,peca],[peca,peca,peca]])
# cria_tabuleiro: {} -> tabuleiro
# cria_copia_tabuleiro: tabuleiro -> tabuleiro
# obter_peca: tabuleiro x posicao -> peca
# obter_vetor: tabuleiro x string -> tuplo de pecas
# coloca_peca: tabuleiro x peca x posicao -> tabuleiro
# remove_peca: tabuleiro x posicao -> tabuleiro
# move_peca: tabuleiro x posicao x posicao -> tabuleiro
# eh_tabuleiro: universal -> booleano
# eh_posicao_livre: tabuleiro x posicao -> booleano
# tabuleiros_iguais: universal x universal -> booleano
# tabuleiro_para_str: tabuleiro -> str
# tuplo_para_tabuleiro: tuplo -> tabuleiro
    

def cria_tabuleiro():
    #{} -> tabuleiro
    '''
    Devolve um tabuleiro de jogo do moinho de 3x3 sem posicoes ocupadas por 
    pecas de jogador.
    '''             
    tab=[]
    for il in range(3):
        linha=[]
        for ic in range(3):
            linha.append(cria_peca(' '))
        tab.append(linha)
    return tab
        

def cria_copia_tabuleiro(tab):
    #tabuleiro -> tabuleiro
    '''
    Recebe um tabuleiro e devolve uma copia nova do tabuleiro.
    '''       
    copia_tab=[]
    for linha in tab:
        linha_l=[]
        for coluna in linha:
            linha_l.append(coluna)
        copia_tab.append(linha_l)
    return copia_tab


def obter_peca(tab,pos):
    #tabuleiro x posicao -> peca
    '''
    Recebe um tabuleiro e uma posicao, e devolve a peca na posicao do tabuleiro.
    Caso a posicao esteja ocupada, devolve uma peca livre.
    '''           
    colunas=['a','b','c']
    linhas=['1','2','3']
    ic=colunas.index(obter_pos_c(pos))
    il=linhas.index(obter_pos_l(pos))
    return tab[il][ic]


def obter_vetor(tab,string):
    #tabuleiro x string -> tuplo de pecas
    '''
    Recebe um tabuleiro e uma cadeia de carateres, e devolve todas as pecas da 
    linha ou coluna do tabuleiro, especificadas pela cadeia de carateres.
    '''           
    colunas=['a','b','c']
    linhas=['1','2','3']
    res=()
    if string in colunas:
        ic=colunas.index(string)
        for i in range(len(linhas)):
            res=res+(tab[i][ic],)
    elif string in linhas:
        il=linhas.index(string)
        res=tuple(tab[il])
    return res


def coloca_peca(tab,peca,pos):
    #tabuleiro x peca x posicao -> tabuleiro
    '''
    Recebe um tabuleiro, uma peca e uma posicao, e modifica destrutivamente o 
    tabuleiro colocando a peca na posicao, e devolve o proprio tabuleiro.
    '''           
    colunas=['a','b','c']
    linhas=['1','2','3']
    ic=colunas.index(obter_pos_c(pos))
    il=linhas.index(obter_pos_l(pos))
    tab[il][ic]=peca
    return tab


def remove_peca(tab,pos):
    #tabuleiro x posicao -> tabuleiro
    '''
    Recebe um tabuleiro e uma posicao, e modifica destrutivamente o tabuleiro 
    removendo a peca da posicao, e devolve o proprio tabuleiro.
    '''           
    colunas=['a','b','c']
    linhas=['1','2','3']
    ic=colunas.index(obter_pos_c(pos))
    il=linhas.index(obter_pos_l(pos))
    tab[il][ic]=cria_peca(' ')
    return tab


def move_peca(tab,pos1,pos2):
    #tabuleiro x posicao x posicao -> tabuleiro
    '''
    Recebe um tabuleiro e 2 posicoes, e modifica destrutivamente a peca que
    se encontra na posicao pos1 para a posicao pos2, e devolve o proprio 
    tabuleiro.
    '''           
    peca=obter_peca(tab,pos1)
    remove_peca(tab,pos1)
    coloca_peca(tab,peca,pos2)
    return tab


def eh_tabuleiro(arg):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True caso o argumento seja um
    TAD tabuleiro e False caso contrario.
    '''                 
    if type(arg)!=list or len(arg)!=3:
        return False
    colunas_linhas=['a','b','c','1','2','3']
    l_soma_x=[]
    l_soma_o=[]
    for string in colunas_linhas:
        soma_x,soma_o=0,0
        coluna_linha=obter_vetor(arg,string)
        if len(coluna_linha)!=3:
            return False
        for peca in coluna_linha:
            if not eh_peca(peca):
                return False
            if pecas_iguais(peca,cria_peca('X')):
                soma_x=soma_x+1
            elif pecas_iguais(peca,cria_peca('O')):
                soma_o=soma_o-1
        l_soma_x.append(soma_x)
        l_soma_o.append(soma_o)
    if sum(l_soma_x)>6 or sum(l_soma_o)<-6 or\
       abs(sum(l_soma_x)+sum(l_soma_o))>2 or\
       (l_soma_x.count(3)+l_soma_o.count(-3))>1:
        return False
    return True


def eh_posicao_livre(tab,pos):
    #tabuleiro x posicao -> booleano
    '''
    Recebe um tabuleiro e uma posicao, e devolve True no caso da posicao 
    corresponder a uma posicao livre e False caso contrario.
    '''        
    return pecas_iguais(obter_peca(tab,pos),cria_peca(' '))


def tabuleiros_iguais(tab1,tab2):
    #universal x universal -> booleano
    '''
    Recebe 2 argumento de qualquer tipo e devolve True caso ambos os 
    argumentos sejam um TAD tabuleiro e iguais, e devolve False caso contrario. 
    '''                  
    if eh_tabuleiro(tab1) and eh_tabuleiro(tab2):
        colunas=['a','b','c']
        linhas=['1','2','3']
        for coluna in colunas:
            for linha in linhas:
                pos=cria_posicao(coluna,linha)
                if not pecas_iguais(obter_peca(tab1,pos),obter_peca(tab2,pos)):
                    return False
        return True
    return False


def tabuleiro_para_str(tab):
    #tabuleiro -> string
    '''
    Recebe um tabuleiro e devolve a cadeia de carateres que o representa.
    '''           
    linhas=['1','2','3']
    tab_str='   a   b   c\n'
    for string in linhas:
        linha=obter_vetor(tab,string)
        l_linha=[]
        for peca in linha:
            l_linha.append(peca_para_str(peca))
        linha_str='-'.join(l_linha)
        if string!='2':
            tab_str=tab_str+string+' '+linha_str+'\n'
        else:
            tab_str=tab_str+'   | \ | / |\n'+string+' '+\
                linha_str+'\n'+'   | / | \ |\n'
    return tab_str[:-1]


def tuplo_para_tabuleiro(tuplo):
    #tuplo -> tabuleiro
    '''
    Recebe um tuplo com 3 tuplos, cada um deles contendo 3 valores inteiros 
    iguais a 1, -1 ou 0 e devolve o tabuleiro que esse tuplo representa, sendo 
    que cada valor inteiro representa uma peca do jogador 'X', 'O' ou livre, 
    respetivamente. 
    '''           
    colunas=['a','b','c']
    linhas=['1','2','3']
    tab=cria_tabuleiro()
    for linha in range(len(tuplo)):
        for coluna in range(len(tuplo[linha])):
            if tuplo[linha][coluna]==1:
                coloca_peca(tab,cria_peca('X'),\
                            cria_posicao(colunas[coluna],linhas[linha]))
            elif tuplo[linha][coluna]==-1:
                coloca_peca(tab,cria_peca('O'),\
                            cria_posicao(colunas[coluna],linhas[linha]))
            else:
                coloca_peca(tab,cria_peca(' '),\
                            cria_posicao(colunas[coluna],linhas[linha]))
    return tab


def obter_linhas(tab):
    #tabuleiro -> lista de linhas
    '''
    Recebe um tabuleiro e devolve uma lista com todas as linhas do tabuleiro.
    '''           
    linhas=['1','2','3']
    linhas_tab=[]
    for linha in linhas:
        linhas_tab.append(obter_vetor(tab,linha))
    return linhas_tab


def obter_colunas(tab):
    #tabuleiro -> lista de colunas
    '''
    Recebe um tabuleiro e devolve uma lista com todas as colunas do tabuleiro.
    '''       
    colunas=['a','b','c']
    colunas_tab=[]
    for coluna in colunas:
        colunas_tab.append(obter_vetor(tab,coluna))
    return colunas_tab


def obter_ganhador(tab):
    #tabuleiro -> peca
    '''
    Recebe um tabuleiro e devolve uma peca do jogador que tenhas as suas 3 pecas
    em linha na vertical ou na horizontal no tabuleiro. Se nao existir 
    nenhum ganhador, devolve uma peca livre.
    '''     
    linhas_tab=obter_linhas(tab)
    colunas_tab=obter_colunas(tab)
    for linha in linhas_tab:
        soma=0
        for peca in linha:
            soma=soma+peca_para_inteiro(peca)
        if soma==3:
            return cria_peca('X')
        elif soma==-3:
            return cria_peca('O')
    for coluna in colunas_tab:
        soma=0
        for peca in coluna:
            soma=soma+peca_para_inteiro(peca)
        if soma==3:
            return cria_peca('X')
        elif soma==-3:
            return cria_peca('O')
    return cria_peca(' ')


def obter_posicoes_livres(tab):
    #tabuleiro -> tuplo de posicoes
    '''
    Recebe um tabuleiro e devolve um tuplo com as posicoes nao ocupadas pelas 
    pecas de qualquer um dos dois jogadores na ordem de leitura do tabuleiro.
    '''         
    return obter_posicoes_jogador(tab,cria_peca(' '))


def obter_posicoes_jogador(tab,peca):
    #tabuleiro x peca -> tuplo de posicoes
    '''
    Recebe um tabuleiro e uma peca, e devolve um tuplo com as posicoes ocupadas 
    pela peca na ordem de leitura do tabuleiro.
    '''        
    colunas=['a','b','c']
    linhas=['1','2','3']
    posicoes_jogador=()
    for linha in linhas:
        for coluna in colunas:
            if pecas_iguais(obter_peca(tab,cria_posicao(coluna,linha)),peca):
                posicoes_jogador=posicoes_jogador+\
                    (cria_posicao(coluna,linha),)
    return posicoes_jogador


def obter_posicoes_adjacentes_livres(tab,pos):
    #tabuleiro x posicao -> tuplo de posicoes
    '''
    Recebe um tabuleiro e uma posicao, e devolve um tuplo com as posicoes 
    adjacentes livres a posicao de acordo com a ordem de leitura do tabuleiro.
    '''        
    pos_adj=obter_posicoes_adjacentes(pos)
    pos_adj_livres=[]
    for pos in pos_adj:
        if eh_posicao_livre(tab,pos):
            pos_adj_livres.append(pos)
    return pos_adj_livres


def eh_posicao_externa(string):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True caso o seu argumento 
    seja uma representacao externa de uma posicao e False caso contrario.
    '''           
    return len(string)==2 and string[0] in ['a','b','c'] and\
           string[1] in ['1','2','3'] 
    

def obter_movimento_manual_colocacao(tab):
    #tabuleiro x peca -> tuplo de uma posicao
    '''
    Recebe um tabuleiro, realiza a leitura da posicao escolhida
    manualmente pelo jogador para colocar a peca, e devolve um tuplo com a 
    posicao escolhida. 
    '''               
    pos_ext=input('Turno do jogador. Escolha uma posicao: ')
    if eh_posicao_externa(pos_ext):
        pos=cria_posicao(pos_ext[0],pos_ext[1])
        if eh_posicao_livre(tab,pos):
            return (pos,)
        else:
            raise ValueError('obter_movimento_manual: escolha invalida')
    else:
        raise ValueError('obter_movimento_manual: escolha invalida')    
   
    
def obter_movimento_manual_movimentacao(tab,peca):
    #tabuleiro x peca -> tuplo de posicoes
    '''
    Recebe um tabuleiro e uma peca, realiza a leitura das posicoes escolhidas 
    manualmente pelo jogador para movimentar a peca, e devolve um tuplo com as 
    posicoes escolhidas. 
    '''   
    pos_jog=obter_posicoes_jogador(tab,peca)
    pos_ext=input('Turno do jogador. Escolha um movimento: ')
    if len(pos_ext)==4 and eh_posicao_externa(pos_ext[:2]) and\
       eh_posicao_externa(pos_ext[2:4]):
        pos_i,pos_f=cria_posicao(pos_ext[:2][0],pos_ext[:2][1]),\
            cria_posicao(pos_ext[2:4][0],pos_ext[2:4][1])
        if posicoes_iguais(pos_i,pos_f):
            for pos_j in pos_jog:
                pos_adj_livres=obter_posicoes_adjacentes_livres(tab,pos_j)
                if len(pos_adj_livres)!=0:
                    raise ValueError('obter_movimento_manual: '\
                                     'escolha invalida')
            return (pos_i,pos_f)
        for pos_j in pos_jog:
            if posicoes_iguais(pos_i,pos_j):
                pos_adj_livres=obter_posicoes_adjacentes_livres(tab,pos_i)
                for pos_a in pos_adj_livres:
                    if posicoes_iguais(pos_f,pos_a):
                        return (pos_i,pos_f)
    raise ValueError('obter_movimento_manual: escolha invalida')    
    
    
         
def obter_movimento_manual(tab,peca):
    #tabuleiro x peca -> tuplo de posicoes
    '''
    Recebe um tabuleiro e uma peca, e devolve um tuplo com 1 ou 2 posicoes que 
    representam uma posicao ou um movimento introduzido manualmente pelo 
    jogador.
    '''               
    pos_jog=obter_posicoes_jogador(tab,peca)
    if len(pos_jog)<3:
        return obter_movimento_manual_colocacao(tab)
    else:
        return obter_movimento_manual_movimentacao(tab,peca)


def vitoria(tab,peca):
    #tabuleiro x peca -> posicao
    '''
    Recebe um tabuleiro e uma peca, e devolve uma posicao caso essa 
    posicao seja livre e permita ao jogador da peca ganhar o jogo.
    '''       
    pos_livres=obter_posicoes_livres(tab)
    for pos in pos_livres:
        tab_copia=cria_copia_tabuleiro(tab)
        coloca_peca(tab_copia,peca,pos)
        if pecas_iguais(obter_ganhador(tab_copia),peca):
            return pos


def bloqueio(tab,peca):
    #tabuleiro x peca -> posicao
    '''
    Recebe um tabuleiro e uma peca, e devolve uma posicao caso 
    essa posicao seja livre e permita ao jogador adversario da peca ganhar o 
    jogo.
    '''           
    if pecas_iguais(peca,cria_peca('X')):
        return vitoria(tab,cria_peca('O'))
    else:
        return vitoria(tab,cria_peca('X'))
    

def centro(tab):
    #tabuleiro -> posicao
    '''
    Recebe um tabuleiro e devolve uma posicao caso a posicao central
    do tabuleiro esteja livre.
    '''            
    pos_livres=obter_posicoes_livres(tab)
    for pos in pos_livres:
        if posicoes_iguais(pos,cria_posicao('b','2')):
            return cria_posicao('b','2')


def canto_vazio(tab):
    #tabuleiro -> posicao
    '''
    Recebe um tabuleiro e devolve uma posicao caso exista um canto livre.
    '''     
    pos_livres=obter_posicoes_livres(tab)
    cantos=[cria_posicao('a','1'),cria_posicao('c','1'),\
                   cria_posicao('a','3'),cria_posicao('c','3')]
    for pos in pos_livres:
        for canto in cantos:
            if posicoes_iguais(pos,canto):
                return pos

def lateral_vazio(tab):
    #tabuleiro -> posicao
    '''
    Recebe um tabuleiro e devolve uma posicao caso exista uma lateral livre.
    '''         
    pos_livres=obter_posicoes_livres(tab)
    laterais=[cria_posicao('b','1'),cria_posicao('a','2'),\
                   cria_posicao('c','2'),cria_posicao('b','3')]
    for pos in pos_livres:
        for lateral in laterais:
            if posicoes_iguais(pos,lateral):
                return pos    
        

def minimax(tab,peca,prof,seq_mov):
    #tabuleiro x peca x inteiro x vetor -> inteiro x vetor  
    '''
    Recebe um tabuleiro, uma peca, um inteiro e um vetor, e devolve um vetor 
    com a melhor sequencia de movimentos obtidos atraves do algoritmo minimax e 
    um inteiro 1, -1 ou 0 que representa a vitoria do jogador 'X','O' ou de 
    nenhum jogador, respetivamente. 
    '''             
    if not pecas_iguais(obter_ganhador(tab),cria_peca(' ')) or prof==0:
        return peca_para_inteiro(obter_ganhador(tab)),seq_mov
    else:
        melhor_seq_mov=None
        melhor_res=-1*peca_para_inteiro(peca)
        pos_jog=obter_posicoes_jogador(tab,peca)
        for pos_i in pos_jog:
            pos_adj_livres=obter_posicoes_adjacentes_livres(tab,pos_i)
            for pos_f in pos_adj_livres:
                tab_copia=cria_copia_tabuleiro(tab)
                move_peca(tab_copia,pos_i,pos_f)
                if pecas_iguais(peca,cria_peca('X')):
                    nov_res,nov_seq_mov=minimax(tab_copia,cria_peca('O'),\
                                                prof-1,seq_mov+(pos_i,pos_f))
                    if melhor_seq_mov==None or nov_res>melhor_res:
                        melhor_res,melhor_seq_mov=nov_res,nov_seq_mov
                else:
                    nov_res,nov_seq_mov=minimax(tab_copia,cria_peca('X'),\
                                                prof-1,seq_mov+(pos_i,pos_f))   
                    if melhor_seq_mov==None or nov_res<melhor_res:
                        melhor_res,melhor_seq_mov=nov_res,nov_seq_mov         
        return melhor_res,melhor_seq_mov
    

def fase_colocacao(tab,peca):
    #tabuleiro x peca -> tuplo de uma posicao
    '''
    Recebe um tabuleiro e uma peca, e devolve um tuplo com uma posicao de acordo
    com as regras da fase de colocacao 
    '''          
    if vitoria(tab,peca)!=None:
        return (vitoria(tab,peca),)
    elif bloqueio(tab,peca)!=None:
        return (bloqueio(tab,peca),)
    elif centro(tab)!=None:
        return (centro(tab),)
    elif canto_vazio(tab)!=None:
        return (canto_vazio(tab),)
    else:
        return (lateral_vazio(tab),)
           
           
def obter_movimento_auto(tab,peca,est):
    #tabuleiro x peca x string -> tuplo de posicoes 
    '''
    Recebe um tabuleiro, uma peca e uma cadeia de carateres representando o 
    nivel de dificuldade do jogo, e devolve um tuplo com uma ou duas posicoes 
    que representam uma posicao ou um movimento escolhido automaticamente. 
    '''
    pos_jog=obter_posicoes_jogador(tab,peca)
    if len(pos_jog)<3:
        return fase_colocacao(tab,peca)
    else:
        if est=='facil':
            for pos_i in pos_jog:
                pos_adj=obter_posicoes_adjacentes(pos_i)
                for pos_f in pos_adj:
                    if eh_posicao_livre(tab,pos_f):
                        return (pos_i,pos_f)            
        elif est=='normal':
            return minimax(tab,peca,1,())[1][:2]
        else:
            return minimax(tab,peca,5,())[1][:2]
        

def moinho(peca_ext,dif):
    #string x string -> string 
    '''
    Recebe 2 cadeias de carateres, onde a primeira corresponde a representacao 
    externa da peca do jogador humano e a segunda ao nivel de dificuldade do 
    jogo, e devolve a representacao externa da peca ganhadora ('[X]' ou '[O]').
    '''   
    if peca_ext not in ['[X]','[O]'] or dif not in ['facil','normal','dificil']:
        raise ValueError('moinho: argumentos invalidos')
    print('Bem-vindo ao JOGO DO MOINHO. Nivel de dificuldade {0}.'.format(dif))
    tab=cria_tabuleiro()
    peca=cria_peca('X')
    print(tabuleiro_para_str(tab))
    while pecas_iguais(obter_ganhador(tab),cria_peca(' ')):
        if peca_ext=='[X]':
            m=obter_movimento_manual(tab,peca)
            peca_ext='[O]'
        else:
            print('Turno do computador ({0}):'.format(dif))
            m=obter_movimento_auto(tab,peca,dif)
            peca_ext='[X]'
        if len(m)==1:
            coloca_peca(tab,peca,m[0])
        else:
            move_peca(tab,m[0],m[1])        
        if pecas_iguais(peca,cria_peca('X')):
            peca=cria_peca('O')
        else:
            peca=cria_peca('X')
        print(tabuleiro_para_str(tab))
    return peca_para_str(obter_ganhador(tab))
            
        