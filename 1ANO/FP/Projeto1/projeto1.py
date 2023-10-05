#Manuel Pereira, n. 98580


def eh_tabuleiro(argumento):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True se o seu
    argumento corresponde a um tabuleiro e False caso contrario.
    '''
    if type(argumento)!=tuple or len(argumento)!=3:
        return False
    else:
        for subtuplo in argumento:
            if type(subtuplo)!=tuple or len(subtuplo)!=3:
                return False
            else:
                for el_subtuplo in subtuplo:
                    if el_subtuplo not in (1,-1,0) or type(el_subtuplo)!=int:
                        return False
    return True


def eh_posicao(argumento):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True se o seu
    argumento corresponde a uma posicao e False caso contrario.
    '''
    return type(argumento)==int and 1<=argumento<=9


def obter_coluna(tabuleiro,inteiro):
    #tabuleiro x inteiro -> vector
    '''
    Recebe um tabuleiro e um inteiro com  valor de 1 a 3 que 
    representa o numero da coluna, e devolve um vector com os valores dessa
    coluna.
    '''
    if eh_tabuleiro(tabuleiro) and type(inteiro)==int and 1<=inteiro<=3:
        coluna=()
        indice=inteiro-1
        for linha in tabuleiro:
            coluna=coluna+(linha[indice],)
        return coluna
            
    else:
        raise ValueError('obter_coluna: algum dos argumentos e invalido')
    

def obter_linha(tabuleiro,inteiro):
    #tabuleiro x inteiro -> vector
    '''
    Recebe um tabuleiro e um inteiro com  valor de 1 a 3 que 
    representa o numero da linha, e devolve um vector com os valores dessa
    linha.
    '''
    if eh_tabuleiro(tabuleiro) and type(inteiro)==int and 1<=inteiro<=3:
        indice=inteiro-1
        return tabuleiro[indice]
    else:
        raise ValueError('obter_linha: algum dos argumentos e invalido')
    
    
def obter_diagonal(tabuleiro,inteiro):
    #tabuleiro x inteiro -> vector
    '''
    Recebe um tabuleiro e um inteiro que representa a direcao da
    diagonal, 1 para descendente da esquerda para a direita e 2 para ascendente
    da esquerda para a direita, e devolve um vector com os valores dessa
    diagonal.
    '''    
    if eh_tabuleiro(tabuleiro) and type(inteiro)==int and 1<=inteiro<=2:
        diagonal=()
        if inteiro==1:
            indice=inteiro-1
            for linha in tabuleiro:
                diagonal=diagonal+(linha[indice],)
                indice=indice+1
        else:
            indice=inteiro
            for linha in tabuleiro:
                diagonal=(linha[indice],)+diagonal
                indice=indice-1   
        return diagonal
    else:
        raise ValueError('obter_diagonal: algum dos argumentos e invalido')


def linha_str(linha):
    #linha -> vector
    '''
    Recebe uma linha e devolve um tuplo com a cadeia de caracteres que a
    representa.
    '''
    nova_linha=()
    for el in linha:
        if el==1:
            nova_linha=nova_linha+(' X ',)
        elif el==-1:
            nova_linha=nova_linha+(' O ',)
        else:
            nova_linha=nova_linha+('   ',)
    #E devolvido um tuplo para poder utilizar .join e nao inserir o separador 
    #nos espacos em branco
    return nova_linha  
            
    
def tabuleiro_str(tabuleiro):
    # tabuleiro -> cadeia carateres
    '''
    Recebe um tabuleiro e devolve a cadeia de caracteres que o 
    representa.
    '''
    if eh_tabuleiro(tabuleiro):
        #E utilizado um tuplo para poder utilizar .join e nao inserir o 
        #separador nos espacos em branco
        tabuleiro_ext=() 
        for linha in tabuleiro:
            linha_aux=linha_str(linha)
            nova_linha_str='|'.join(linha_aux)
            tabuleiro_ext=tabuleiro_ext+(nova_linha_str,)
        tabuleiro_ext_str='\n-----------\n'.join(tabuleiro_ext)
        return tabuleiro_ext_str
        
    else:
        raise ValueError('tabuleiro_str: o argumento e invalido')
    
    
def indice_linha_coluna(posicao):
    #posicao -> vector
    '''
    Recebe uma posicao, e devolve o vector ordenado com o indice da linha e da 
    coluna associados a essa posicao.
    '''
    indice_linha=(posicao-1)//3
    indice_coluna=(posicao-1)%3
    return (indice_linha,indice_coluna)
     
    
def eh_posicao_livre(tabuleiro,posicao):
    #tabuleiro x posicao -> booleano
    '''
    Recebe um tabuleiro e uma posicao, e devolve True se a posicao
    corresponde a uma posicao livre do tabuleiro e False caso contrario.
    '''
    if eh_tabuleiro(tabuleiro) and eh_posicao(posicao):
        indice_linha,indice_coluna=indice_linha_coluna(posicao)
        return tabuleiro[indice_linha][indice_coluna]==0
    
    else:
        raise ValueError('eh_posicao_livre: algum dos argumentos e invalido')
        
    
def obter_posicoes_livres(tabuleiro):
    #tabuleiro -> vector
    '''
    Recebe um tabuleiro, e devolve o vector ordenado com todas as
    posicoes livres do tabuleiro.
    '''
    if eh_tabuleiro(tabuleiro):
        posicoes_livres=()
        for posicao in range(1,10):
            if eh_posicao_livre(tabuleiro,posicao):
                posicoes_livres=posicoes_livres+(posicao,)
        return posicoes_livres
    else:
        raise ValueError('obter_posicoes_livres: o argumento e invalido')
            

def jogador_ganhador(tabuleiro):
    #tabuleiro -> inteiro
    '''
    Recebe um tabuleiro, e devolve um valor inteiro a indicar o 
    jogador que ganhou a partida no tabuleiro passado por argumento, sendo o 
    valor igual a 1 se ganhou o jogador que joga com 'X', -1 se ganhou o jogador
    que joga com 'O', ou 0 se nao ganhou nenhum jogador.
    '''
    if eh_tabuleiro(tabuleiro):
        for linha in (1,2,3):
            if obter_linha(tabuleiro,linha)==(1,1,1):
                return 1
            elif obter_linha(tabuleiro,linha)==(-1,-1,-1):
                return -1
        for coluna in (1,2,3):
            if obter_coluna(tabuleiro,coluna)==(1,1,1):
                return 1
            elif obter_coluna(tabuleiro,coluna)==(-1,-1,-1):
                return -1
        for diagonal in (1,2):
            if obter_diagonal(tabuleiro,diagonal)==(1,1,1):
                return 1
            elif obter_diagonal(tabuleiro,diagonal)==(-1,-1,-1):
                return -1
        return 0
        
    else:
        raise ValueError('jogador_ganhador: o argumento e invalido')
    
    
def eh_jogador(argumento):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True se o seu
    argumento corresponde a um jogador e False caso contrario.    
    '''
    return type(argumento)==int and argumento in (1,-1) 
    
    
def marcar_posicao(tabuleiro,jogador,posicao):
    #tabuleiro x inteiro x posicao -> tabuleiro
    '''
    Recebe um tabuleiro, um inteiro identificando um jogador 
    (1 para o jogador 'X' ou -1 para o jogador 'O') e uma posicao livre,
    e devolve um novo tabuleiro modificado com uma nova marca do jogador 
    nessa posicao.
    '''
    if eh_tabuleiro(tabuleiro) and eh_jogador(jogador) and eh_posicao(posicao) \
       and eh_posicao_livre(tabuleiro,posicao):
        indice_linha,indice_coluna=indice_linha_coluna(posicao)
        linha=tabuleiro[indice_linha]
        nova_linha=linha[:indice_coluna]+(jogador,)+linha[indice_coluna+1:]
        novo_tabuleiro=tabuleiro[:indice_linha]+(nova_linha,)+\
            tabuleiro[indice_linha+1:]
        return novo_tabuleiro
    else:
        raise ValueError('marcar_posicao: algum dos argumentos e invalido')
    
    
def escolher_posicao_manual(tabuleiro):
    #tabuleiro -> posicao
    '''
    Realiza a leitura de uma posicao introduzida manualmente por
    um jogador e devolve esta posicao escolhida. 
    '''
    if eh_tabuleiro(tabuleiro):
        posicao=int(input('Turno do jogador. Escolha uma posicao livre: '))
        if eh_posicao(posicao) and eh_posicao_livre(tabuleiro,posicao):
            return posicao
        else:
            raise ValueError('escolher_posicao_manual: a posicao introduzida e'\
            ' invalida')
    
    else:
        raise ValueError('escolher_posicao_manual: o argumento e invalido')


def vitoria(tabuleiro,jogador):
    #tabuleiro x jogador -> posicao
    '''
    Recebe um tabuleiro e um jogador, e devolve uma posicao caso 
    essa posicao seja livre e permita ao jogador ganhar o jogo.
    '''    
    posicoes_livres=obter_posicoes_livres(tabuleiro)
    for posicao in posicoes_livres:
        if jogador_ganhador(marcar_posicao(tabuleiro,jogador,posicao))==jogador:
            return posicao
        

def bloqueio(tabuleiro,jogador):
    #tabuleiro x jogador -> posicao
    '''
    Recebe um tabuleiro e um jogador, e devolve uma posicao caso 
    essa posicao seja livre e permita ao jogador adversario ganhar o jogo.
    '''
    return vitoria(tabuleiro,-1*jogador)


def bifurcacao(tabuleiro,jogador):
    #tabuleiro x jogador -> vector
    '''
    Recebe um tabuleiro e um jogador, e devolve o vector ordenado 
    com posicoes, caso existam posicoes em que cada posicao e livre e e uma  
    posicao de interseccao, entre duas linhas/colunas/diagonais em que o jogador tenha uma das suas pecas, que permite ao jogador criar duas formas de vencer
    na jogada seguinte.
    '''
    posicoes_livres=obter_posicoes_livres(tabuleiro)
    t_bifurcacao=()
    for posicao in posicoes_livres:
        indice_linha,indice_coluna=indice_linha_coluna(posicao)
        linha=obter_linha(tabuleiro,indice_linha+1)
        coluna=obter_coluna(tabuleiro,indice_coluna+1)
        if posicao not in (2,4,6,8):
            if posicao in (1,9):
                diagonal=obter_diagonal(tabuleiro,1)
                tuplo_somas=(sum(linha),sum(coluna),sum(diagonal))   
            elif posicao in (3,7):
                diagonal=obter_diagonal(tabuleiro,2)
                tuplo_somas=(sum(linha),sum(coluna),sum(diagonal))
            elif posicao==5:
                diagonal1=obter_diagonal(tabuleiro,1)
                diagonal2=obter_diagonal(tabuleiro,2)
                tuplo_somas=(sum(linha),sum(coluna),sum(diagonal1),\
                             sum(diagonal2))
            #So e possivel criar duas formas de vencer se a 
            #linha/coluna/diagonal tiver apenas uma posicao preenchida
            #Logo so faz sentido verificar se existem 2 ou mais 
            #linhas/colunas/diagonais com uma soma igual ao jogador
            #pois se existir mais do que uma posicao preenchida a soma apenas 
            #podera ser 0,2 ou -2. O caso de estarem todas as posicoes 
            #preenchidas (soma poderia ser 1 ou -1) de uma linha/coluna/diagonal
            #nunca se coloca pois ja estamos a iterar a partir das posicoes 
            #livres no tabuleiro
            if tuplo_somas.count(jogador)>=2:
                t_bifurcacao=t_bifurcacao+(posicao,)
        else:
            #Mesma situacao que em cima mas basta verificar a soma da linha e 
            #coluna pois nao existe a incerteza em relacao ao numero de 
            #diagonais
            if sum(linha)==sum(coluna)==jogador:
                t_bifurcacao=t_bifurcacao+(posicao,)
    return t_bifurcacao
              

def bloqueio_bifurcacao(tabuleiro,jogador):
    #tabuleiro x jogador -> posicao
    '''
    Recebe um tabuleiro e um jogador, e devolve uma posicao caso o
    jogador adversario tenha uma ou mais bifurcacoes. Caso o jogador adversario 
    so tenha uma bifurcacao e devolvida a posicao que permite bloquear essa 
    bifurcacao. Caso o jogador adversario tenha mais que uma bifurcacao e 
    devolvida uma posicao que forca o jogador adversario a defender, desde que 
    essa posicao que o adversario usa para defender nao resulte na criacao de 
    uma bifurcacao para o oponente. 
    '''    
    t_bifurcacao=bifurcacao(tabuleiro,-1*jogador)
    if len(t_bifurcacao)==1:
        return min(bifurcacao(tabuleiro,-1*jogador))
    elif len(t_bifurcacao)>1:
        posicoes_livres=obter_posicoes_livres(tabuleiro)
        for posicao in posicoes_livres:
            novo_tabuleiro=marcar_posicao(tabuleiro,jogador,posicao)
            if bloqueio(novo_tabuleiro,-1*jogador)!= None \
               and bloqueio(novo_tabuleiro,-1*jogador) not in t_bifurcacao:
                    return posicao


def centro(tabuleiro):
    #tabuleiro -> posicao
    '''
    Recebe um tabuleiro e devolve uma posicao caso a posicao central
    do tabuleiro esteja livre.
    '''      
    if eh_posicao_livre(tabuleiro,5):
        return 5
    

def canto_oposto(tabuleiro,jogador):
    #tabuleiro x jogador -> posicao
    '''
    Recebe um tabuleiro e um jogador, e devolve uma posicao caso 
    essa posicao seja um canto livre e o adversario tenha uma posicao no canto
    diagonalmente oposto.
    '''
    posicoes_livres=obter_posicoes_livres(tabuleiro)
    for posicao in posicoes_livres:
        if posicao in (1,3,7,9):
            indice_linha,indice_coluna=indice_linha_coluna(posicao)
            if tabuleiro[2-indice_linha][2-indice_coluna]==-1*jogador:
                return posicao


def canto_vazio(tabuleiro):
    #tabuleiro -> posicao
    '''
    Recebe um tabuleiro e devolve uma posicao caso exista um 
    canto vazio.
    '''          
    posicoes_livres=obter_posicoes_livres(tabuleiro)
    for posicao in posicoes_livres:
        if posicao in (1,3,7,9):
            return posicao


def lateral_vazio(tabuleiro):
    #tabuleiro -> posicao
    '''
    Recebe um tabuleiro e devolve uma posicao caso exista uma 
    lateral vazia.
    '''         
    posicoes_livres=obter_posicoes_livres(tabuleiro)
    for posicao in posicoes_livres:
        if posicao in (2,4,6,8):
            return posicao
        

def estrategia_basico(tabuleiro):
    #tabuleiro -> posicao
    '''
    Recebe um tabuleiro e devolve uma posicao de acordo com a 
    estrategia 'basico'.
    '''
    if centro(tabuleiro) != None:
        return centro(tabuleiro)
    elif canto_vazio(tabuleiro) != None:
        return canto_vazio(tabuleiro)
    else:
        return lateral_vazio(tabuleiro)


def estrategia_normal(tabuleiro,jogador):
    #tabuleiro x jogador -> posicao
    '''
    Recebe um tabuleiro e um jogador, e devolve uma posicao de acordo com a 
    estrategia 'normal'.
    '''        
    if vitoria(tabuleiro,jogador) != None:
        return vitoria(tabuleiro,jogador)
    elif bloqueio(tabuleiro,jogador) != None:
        return bloqueio(tabuleiro,jogador)
    elif centro(tabuleiro)!=None:
        return centro(tabuleiro)
    elif canto_oposto(tabuleiro,jogador)!= None:
        return canto_oposto(tabuleiro,jogador)
    elif canto_vazio(tabuleiro) != None:
        return canto_vazio(tabuleiro)
    else:
        return lateral_vazio(tabuleiro)
    

def estrategia_perfeito(tabuleiro,jogador):
    #tabuleiro x jogador -> posicao
    '''
    Recebe um tabuleiro e um jogador, e devolve uma posicao de acordo com a 
    estrategia 'perfeito'.
    '''        
    if vitoria(tabuleiro,jogador) != None:
        return vitoria(tabuleiro,jogador)
    elif bloqueio(tabuleiro,jogador) != None:
        return bloqueio(tabuleiro,jogador)
    elif bifurcacao(tabuleiro,jogador) !=():
        return min(bifurcacao(tabuleiro,jogador))
    elif bloqueio_bifurcacao(tabuleiro,jogador)!=None:
        return bloqueio_bifurcacao(tabuleiro,jogador)
    elif centro(tabuleiro)!=None:
        return centro(tabuleiro)
    elif canto_oposto(tabuleiro,jogador)!= None:
        return canto_oposto(tabuleiro,jogador)
    elif canto_vazio(tabuleiro) != None:
        return canto_vazio(tabuleiro)
    else:
        return lateral_vazio(tabuleiro) 


def eh_estrategia(argumento):
    #universal -> booleano
    '''
    Recebe um argumento de qualquer tipo e devolve True se o seu
    argumento corresponde a uma estrategia e False caso contrario.    
    '''    
    return type(argumento)==str \
           and argumento in ('basico','normal','perfeito')
      
            
def escolher_posicao_auto(tabuleiro,jogador,estrategia):
    # tabuleiro x inteiro x cadeia carateres -> posicao
    '''
    Recebe um tabuleiro, um inteiro identificando um jogador 
    (1 para o jogador 'X' ou -1 para o jogador 'O') e uma cadeia de carateres 
    correspondente a estrategia, e devolve a posicao escolhida automaticamente
    de acordo com a estrategia seleccionada.
    '''
    if eh_tabuleiro(tabuleiro) and eh_jogador(jogador) \
       and eh_estrategia(estrategia):
        if estrategia=='basico':
            return estrategia_basico(tabuleiro)
        elif estrategia=='normal':
            return estrategia_normal(tabuleiro,jogador)
        else:
            return estrategia_perfeito(tabuleiro,jogador)
        
    else:
        raise ValueError('escolher_posicao_auto: algum dos argumentos e' \
                         ' invalido')


def transforma_resultado(resultado):
    #inteiro -> cadeia carateres
    '''
    Recebe um inteiro, e devolve um cadeia de carateres. Caso o 
    inteiro seja igual a 1 devolve 'X', caso  o inteiro seja igual a -1 devolve
    'O', caso contrario devolve 'EMPATE'. 
    '''
    if resultado==1:
        return ('X')
    elif resultado==-1:
        return ('O')
    else:
        return ('EMPATE')


def eh_fim_jogo(tabuleiro):
    #tabuleiro -> booleano
    '''
    Recebe um tabuleiro e devolve True se o jogo chegou ao fim e False
    caso contrario.    
    '''    
    posicoes_livres=obter_posicoes_livres(tabuleiro)
    return jogador_ganhador(tabuleiro)!=0 or len(posicoes_livres)==0 


def turno_humano(tabuleiro,jogador):
    #tabuleiro x inteiro -> tabuleiro
    '''
    Recebe um tabuleiro e um inteiro identificando um jogador, e imprime e 
    devolve o tabuleiro com a posicao escolhida por esse jogador.
    '''   
    posicao_manual=escolher_posicao_manual(tabuleiro)
    tabuleiro=marcar_posicao(tabuleiro,jogador,posicao_manual)
    print(tabuleiro_str(tabuleiro))
    return tabuleiro

    
def turno_computador(tabuleiro,jogador,estrategia):
    #tabuleiro x inteiro x cadeia carateres -> tabuleiro
    '''
    Recebe um tabuleiro, um inteiro identificando um jogador e uma cadeia de 
    carateres correspondente a estrategia, e imprime e devolve o tabuleiro com 
    a posicao escolhida automaticamente por esse jogador, 
    de acordo com a estrategia.
    '''            
    print('Turno do computador ({0}):'.format(estrategia))
    posicao_auto=escolher_posicao_auto(tabuleiro,jogador,estrategia)
    tabuleiro=marcar_posicao(tabuleiro,jogador,posicao_auto)
    print(tabuleiro_str(tabuleiro))
    return tabuleiro


def jogo_do_galo(jogador_s,estrategia):
    #cadeia carateres x cadeia carateres -> cadeia carateres 
    '''
    Recebe dois argumentos, o primeiro argumento corresponde
    a marca ('X' ou 'O') que deseja utilizar o jogador humano, e o segundo 
    argumento selecciona a estrategia de jogo utilizada pela maquina. 
    Devolve o identificador do jogador ganhador ('X' ou 'O') ou, 
    em caso de empate, devolve 'EMPATE'.
    '''
    if jogador_s in ('X','O') and eh_estrategia(estrategia):
        print("Bem-vindo ao JOGO DO GALO.\nO jogador joga com '{0}'."\
              .format(jogador_s))         
        tabuleiro=((0,0,0),(0,0,0),(0,0,0))
        t_jogador=(1,-1)
        if jogador_s=='X':
            #jogador humano joga primeiro
            jogador_humano,jogador_computador=t_jogador
            while True:
                #turno jogador humano
                tabuleiro=turno_humano(tabuleiro,jogador_humano)
                if eh_fim_jogo(tabuleiro):
                    resultado=transforma_resultado(jogador_ganhador(tabuleiro))
                    return resultado
                #turno computador
                tabuleiro=turno_computador(tabuleiro,jogador_computador,\
                                           estrategia)
                if eh_fim_jogo(tabuleiro):
                    resultado=transforma_resultado(jogador_ganhador(tabuleiro))
                    return resultado   
        else:
            #computador joga primeiro
            jogador_computador,jogador_humano=t_jogador
            while True:
                #turno computador
                tabuleiro=turno_computador(tabuleiro,jogador_computador,\
                                           estrategia)
                if eh_fim_jogo(tabuleiro):
                    resultado=transforma_resultado(jogador_ganhador(tabuleiro))
                    return resultado  
                #turno jogador humano~
                tabuleiro=turno_humano(tabuleiro,jogador_humano)
                if eh_fim_jogo(tabuleiro):
                    resultado=transforma_resultado(jogador_ganhador(tabuleiro))
                    return resultado                          
    else:
        raise ValueError('jogo_do_galo: algum dos argumentos e invalido')
    
