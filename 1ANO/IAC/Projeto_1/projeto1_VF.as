ORIG            4000h   

dim             EQU     80
inicioterreno   TAB     80
altura          word    2
semente         word    5


ORIG            0000h 
                
                MVI     R6,4000h

                MVI     R4,80 ; Número de vezes que a atualizajogo é chamada
                
loopteste:      MVI     R1,inicioterreno
                MVI     R2,dim 

                JAL     atualizajogo
                
                DEC     R4
                
                BR.NZ   loopteste
                
                
FIM:            BR      FIM

atualizajogo:   DEC     R6
                STOR    M[R6],R4 ;Guarda o valor de R4 na pilha
                
                DEC     R6
                STOR    M[R6],R5 ;Guarda o valor de R5 na pilha
                
                DEC     R2 ;Para que o tamanho do vetor no loop não seja ultrapassado

loop:           MOV     R4,R1 ;Move o endereço do elemento n para R4 para depois 
                              ;ser possível guardar em R4 o valor do elemento n+1

                INC     R1
                LOAD    R5,M[R1] ;Permite obter o valor do elemento n+1
                
                STOR    M[R4],R5 ;Passa o valor do elemento n+1 para n
                
                DEC     R2 ; Diminui dim em uma unidade
                
                BR.P    loop ;Enquanto dim>0 volta a repetir o processo
                
                DEC     R6
                STOR    M[R6],R7
                
                JAL     geracacto
                
                STOR    M[R1],R3 ; Como R1 encontra-se no último elemento do vetor
                                 ; basta guardar nesse endereço o valor de retorno de geracacto
                
                LOAD    R7,M[R6]
                INC     R6
                
                LOAD    R5,M[R6] ;Repõe o valor de R5
                INC     R6
                
                LOAD    R4,M[R6] ;Repõe o valor de R4
                INC     R6
                
                
                
                JMP     R7

                
geracacto:      DEC     R6
                STOR    M[R6],R1 ;Guarda o valor de R1 na pilha
                DEC     R6
                STOR    M[R6],R4 ;Guarda o valor de R4 na pilha
                DEC     R6 
                STOR    M[R6],R5 ;Guarda o valor de R5 na pilha

                MVI     R1,altura
                MVI     R2,semente
                LOAD    R4,M[R2] ;Carrega o valor da semente em R4
                MVI     R5,1
                AND     R5,R4,R5
                SHR     R4
                CMP     R5,R0
                BR.NZ   if1
                
                
mid:            MVI     R5,62258
                CMP     R4,R5
                BR.C    if2
                
                LOAD    R5,M[R1] ;Carrega o valor da altura máxima dos cactos em R5
                
                DEC     R5
                
                AND     R3,R4,R5
                INC     R3
                
end:            STOR    M[R2],R4 ;Atualiza o valor da semente

                LOAD    R5,M[R6] ;Repõe o valor de R5
                INC     R6
                LOAD    R4,M[R6] ;Repõe o valor de R4
                INC     R6
                LOAD    R1,M[R6] ;Repõe o valor de R1
                INC     R6

                JMP     R7
                
if1:            MVI     R5,b400h
                XOR     R4,R4,R5
                BR      mid

if2:            MVI     R3,0
                BR      end