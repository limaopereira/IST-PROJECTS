;=================================================================
; CONSTANTS
;-----------------------------------------------------------------

; Field Dimension
dim             EQU     80
; Text window
TERM_READ       EQU     FFFFh
TERM_WRITE      EQU     FFFEh
TERM_STATUS     EQU     FFFDh
TERM_CURSOR     EQU     FFFCh
TERM_COLOR      EQU     FFFBh
; 7 segment display
DISP7_D0        EQU     FFF0h
DISP7_D1        EQU     FFF1h
DISP7_D2        EQU     FFF2h
DISP7_D3        EQU     FFF3h
DISP7_D4        EQU     FFEEh
DISP7_D5        EQU     FFEFh
; Stack
SP_INIT         EQU     7000h
; timer
TIMER_CONTROL   EQU     FFF7h
TIMER_COUNTER   EQU     FFF6h
TIMER_SETSTART  EQU     1
TIMER_SETSTOP   EQU     0
TIMERCOUNT_INIT EQU     1
; interruptions
INT_MASK        EQU     FFFAh
INT_MASK_VAL    EQU     8009h ; 1000 0000 0000 1001 b

;=================================================================
; Program global variables
;-----------------------------------------------------------------
                ORIG    0
inicioterreno   TAB     80
altura          WORD    8
semente         WORD    5
TIMER_COUNTVAL  WORD    TIMERCOUNT_INIT ; states the current counting period
TIMER_TICK      WORD    0               ; indicates the number of unattended
                                        ; timer interruptions
TIME_0          WORD    0
TIME_1          WORD    0
TIME_2          WORD    0
TIME_3          WORD    0
TIME_4          WORD    0
TIME_5          WORD    0

JUMP            WORD    0
JUMP_SPEED      WORD    0
JUMP_TIME       WORD    0
GAME_OVER       WORD    1
ALTURA_MAX_DINO WORD    0
ALTURA_DINO     WORD    2828h
ALTURA_CACTO    WORD    0
FloorStr        STR     0,1,2900h,0,2,88h,'████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████',0,0
GameoverStr     STR     0,1,1410h,0,2,1fh,'  ▓▓▓▓ ▓▓▓▓ ▓   ▓ ▓▓▓  ▓▓▓▓ ▓   ▓ ▓▓▓ ▓▓▓                       ',0,1,1500h,'                  ▓    ▓  ▓ ▓▓ ▓▓ ▓    ▓  ▓ ▓   ▓ ▓   ▓  ▓',0,1,1600h,'                  ▓ ▓▓ ▓▓▓▓ ▓ ▓ ▓ ▓▓▓  ▓  ▓ ▓▓ ▓▓ ▓▓▓ ▓▓▓',0,1,1700h,'                  ▓  ▓ ▓  ▓ ▓   ▓ ▓    ▓  ▓  ▓▓▓  ▓   ▓  ▓',0,1,1800h,'                  ▓▓▓▓ ▓  ▓ ▓   ▓ ▓▓▓  ▓▓▓▓   ▓   ▓▓▓ ▓   ▓',0,0

;=================================================================
; MAIN: the starting point of your program
;-----------------------------------------------------------------
                ORIG    0
MAIN:           MVI     R6,SP_INIT
                ; CONFIGURE TIMER ROUNTINES
                ; interrupt mask
                MVI     R1,INT_MASK
                MVI     R2,INT_MASK_VAL
                STOR    M[R1],R2
                ; enable interruptions
                ENI
                ; JUMP SPEED CALCULATION
                MVI     R1,altura
                LOAD    R2,M[R1]
                SHRA    R2                
                MVI     R1,0100h
                MVI     R4,0
.LOOP_SPEED:    ADD     R4,R4,R1
                DEC     R2
                BR.NZ    .LOOP_SPEED
                MVI     R1,JUMP_SPEED
                STOR    M[R1],R4
                ; MAXIMUM HEIGHT DINO CALCULATION
                MVI     R1,altura
                LOAD    R2,M[R1]
                SHLA    R2
                MVI     R1,ALTURA_DINO
                LOAD    R4,M[R1]
                MVI     R1,0100h
.LOOP_HEIGHT:   SUB     R4,R4,R1
                DEC     R2
                BR.NZ    .LOOP_HEIGHT
                MVI     R1,ALTURA_MAX_DINO
                STOR    M[R1],R4
                ; JUMPING TIME CALCULATION
                MVI     R1,JUMP_SPEED
                LOAD    R4,M[R1]
                MVI     R1,ALTURA_MAX_DINO
                LOAD    R5,M[R1]
                MVI     R1,ALTURA_DINO
                LOAD    R2,M[R1]
                MOV     R1,R0
.LOOP_JUMP:     INC     R1
                SUB     R2,R2,R4
                CMP     R2,R5
                BR.NZ   .LOOP_JUMP 
                MVI     R2,JUMP_TIME
                SHLA    R1
                STOR    M[R2],R1
                ; GROUND IMAGE
                JAL     imagemterreno
                ;DINO IMAGE
                JAL     imagemdino
                ; START TIMER
                MVI     R2,TIMERCOUNT_INIT
                MVI     R1,TIMER_COUNTER
                STOR    M[R1],R2          ; set timer to count 1x100ms
                MVI     R1,TIMER_TICK
                STOR    M[R1],R0          ; clear all timer ticks
                MVI     R1,TIMER_CONTROL
                MVI     R2,TIMER_SETSTART
                STOR    M[R1],R2          ; start timer
                ; WAIT FOR EVENT (TIMER)
                MVI     R5,TIMER_TICK
.LOOP_TIMER:          LOAD    R1,M[R5]
                CMP     R1,R0
                ; disable interruptions
                DSI
                JAL.NZ  PROCESS_TIMER_EVENT
                ; enable interruptions
                ENI
                BR      .LOOP_TIMER        
                
;=================================================================
; resetdisplay: function that clears display
;-----------------------------------------------------------------
resetdisplay:   ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R1
                ; CLEAR DISPLAY
                MVI     R1,TIME_0
                STOR    M[R1],R0
                MVI     R1,TIME_1
                STOR    M[R1],R0
                MVI     R1,TIME_2
                STOR    M[R1],R0
                MVI     R1,TIME_3
                STOR    M[R1],R0
                MVI     R1,TIME_4
                STOR    M[R1],R0
                MVI     R1,TIME_5
                STOR    M[R1],R0
                ; RESTORE CONTEXT 
                LOAD    R1,M[R6]
                INC     R6
                JMP     R7
                
;=================================================================
; tempodecimal: function that turns time to decimal
;-----------------------------------------------------------------
tempodecimal:   ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R2
                MVI     R2,10
                CMP     R1,R2
                BR.NC   .decimal
                ; IF R1<10
                MOV     R3,R0
                DEC     R6
                STOR    M[R6],R3
                MOV     R3,R1
.return:        JMP     R7
.decimal:       ; IF R1>=10
                MVI     R3,1
                DEC     R6
                STOR    M[R6],R3
                MOV     R3,R0
                BR      .return

;=================================================================
; atualizadisplay: function that updates display
;-----------------------------------------------------------------
atualizadisplay:; SAVE CONTEXT 
                DEC     R6
                STOR    M[R6],R3
                DEC     R6
                STOR    M[R6],R7
                ; SHOW TIME ON DISP7_D0
                MVI     R2,TIME_0
                LOAD    R1,M[R2]
                INC     R1
                JAL     tempodecimal
                MOV     R1,R3
                LOAD    R3,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                STOR    M[R2],R1
                MVI     R2,DISP7_D0
                STOR    M[R2],R1
                ; SHOW TIME ON DISP7_D1
                MVI     R2,TIME_1
                LOAD    R1,M[R2]
                ADD     R1,R1,R3
                JAL     tempodecimal
                MOV     R1,R3
                LOAD    R3,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                STOR    M[R2],R1
                MVI     R2,DISP7_D1
                STOR    M[R2],R1
                ; SHOW TIME ON DISP7_D2
                MVI     R2,TIME_2
                LOAD    R1,M[R2]
                ADD     R1,R1,R3
                JAL     tempodecimal
                MOV     R1,R3
                LOAD    R3,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                STOR    M[R2],R1
                MVI     R2,DISP7_D2
                STOR    M[R2],R1
                ; SHOW TIME ON DISP7_D3
                MVI     R2,TIME_3
                LOAD    R1,M[R2]
                ADD     R1,R1,R3
                JAL     tempodecimal
                MOV     R1,R3
                LOAD    R3,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                STOR    M[R2],R1
                MVI     R2,DISP7_D3
                STOR    M[R2],R1
                ; SHOW TIME ON DISP7_D4
                MVI     R2,TIME_4
                LOAD    R1,M[R2]
                ADD     R1,R1,R3
                JAL     tempodecimal
                MOV     R1,R3
                LOAD    R3,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                STOR    M[R2],R1
                MVI     R2,DISP7_D4
                STOR    M[R2],R1
                ; SHOW TIME ON DISP7_D5
                MVI     R2,TIME_5
                LOAD    R1,M[R2]
                ADD     R1,R1,R3
                JAL     tempodecimal
                MOV     R1,R3
                LOAD    R3,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                STOR    M[R2],R1
                MVI     R2,DISP7_D5
                STOR    M[R2],R1
                ; RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                LOAD    R3,M[R6]
                INC     R6
                JMP     R7

;=================================================================
; colisao: function that checks collision
;-----------------------------------------------------------------
colisao:        ; SAVE CONTEXT 
                DEC     R6
                STOR    M[R6],R4
                ; GET TO DINO COLUMN
                MVI     R1,inicioterreno
                MVI     R2,40
                ADD     R1,R1,R2
                LOAD    R2,M[R1]
                MVI     R1,2828h
                MVI     R4,0100h
                ; CHECK FOR CACTUS
                CMP     R2,R0
                BR.Z    .return
.loop:          ; CALCULATE CACTUS HEIGHT
                SUB     R1,R1,R4
                DEC     R2
                BR.NZ   .loop
                ; COMPARE HEIGHT OF CACTUS AND DINO
                MVI     R2,ALTURA_DINO
                LOAD    R4,M[R2]
                CMP     R1,R4
                BR.C    .gameover ; height cactus > height dino 
                ; RESTORE CONTEXT
.return:        LOAD    R4,M[R6]
                INC     R6
                JMP     R7
                ; GAME OVER
.gameover:      MVI     R3,1
                MVI     R1,GAME_OVER
                STOR    M[R1],R3
                BR      .return
                
;=================================================================
; saltodino: function that calculates dino jump
;-----------------------------------------------------------------
saltodino:      ; SAVE CONTEXT 
                DEC     R6
                STOR    M[R6],R5
                DEC     R6
                STOR    M[R6],R4
                ; VERIFY IF UP OR DOWN
                MVI     R5,JUMP_TIME
                LOAD    R4,M[R5]
                SHRA    R4
                INC     R4
                CMP     R2,R4
                BR.C    .desce
                ; GOES UP
                MVI     R1,ALTURA_DINO
                LOAD    R3,M[R1]
                MVI     R5,JUMP_SPEED
                LOAD    R4,M[R5]
                SUB     R3,R3,R4
                STOR    M[R1],R3
.return:        ; UPDATE JUMP VARIABLE
                DEC     R2   
                MVI     R1,JUMP
                STOR    M[R1],R2
                ; RESTORE CONTEXT 
                LOAD    R4,M[R6]
                INC     R6
                LOAD    R5,M[R6]
                INC     R6
                JMP     R7  
.desce:         ; GOES DOWN
                MVI     R1,ALTURA_DINO
                LOAD    R3,M[R1]
                MVI     R5,JUMP_SPEED
                LOAD    R4,M[R5]
                ADD     R3,R3,R4
                STOR    M[R1],R3
                BR      .return

;=================================================================
; imagemdino: function that creates the image of dino
;-----------------------------------------------------------------
imagemdino:     ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R5
                DEC     R6
                STOR    M[R6],R7
                ; CHECK IF THERE IS AN ONGOING JUMP
                MVI     R1,JUMP
                LOAD    R2,M[R1]
                CMP     R2,R0
                JAL.NZ  saltodino
.return:        ; DINO FEET POSITION
                MVI     R2,ALTURA_DINO
                LOAD    R1,M[R2]
                MVI     R2,TERM_CURSOR
                STOR    M[R2],R1
                ; DINO FEET COLOR
                MVI     R1,e0h
                MVI     R2,TERM_COLOR
                STOR    M[R2],R1
                ; WRITE DINO FEET
                MVI     R1,'M'
                MVI     R2,TERM_WRITE
                STOR    M[R2],R1
                ; DINO BODY POSITION
                MVI     R4,2
                MVI     R2,ALTURA_DINO
                LOAD    R5,M[R2]
.loop:          MVI     R2,0100h
                SUB     R5,R5,R2
                MVI     R2,TERM_CURSOR
                STOR    M[R2],R5
                ; DINO BODY COLOR
                MVI     R1,e0h
                MVI     R2,TERM_COLOR
                STOR    M[R2],R1
                ; WRITE DINO BODY
                MVI     R1,'│'
                MVI     R2,TERM_WRITE
                STOR    M[R2],R1
                DEC     R4
                BR.NZ   .loop
                ; DINO HEAD POSITION
                MVI     R2,0100h
                SUB     R5,R5,R2
                MVI     R2,TERM_CURSOR
                STOR    M[R2],R5
                ; DINO HEAD COLOR
                MVI     R1,e0h
                MVI     R2,TERM_COLOR
                STOR    M[R2],R1
                ; WRITE DINO HEAD
                MVI     R1,'☻'
                MVI     R2,TERM_WRITE
                STOR    M[R2],R1
                ;RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                LOAD    R5,M[R6]
                INC     R6                
                JMP     R7
                
;=================================================================
; imagemterreno: function that creates ground image
;-----------------------------------------------------------------
imagemterreno:  ; SAVE CONTEXT 
                DEC     R6
                STOR    M[R6],R3
                DEC     R6
                STOR    M[R6],R4
                DEC     R6
                STOR    M[R6],R5
                ; TERMINAL ADDRESSES
                MVI     R1, TERM_CURSOR
                MVI     R2, TERM_COLOR
                MVI     R3, TERM_WRITE
                MVI     R4, FloorStr
.terminalloop:  ; WRITE GROUND
                LOAD    R5, M[R4]
                INC     R4
                CMP     R5, R0
                BR.Z    .control
                STOR    M[R3], R5
                BR      .terminalloop
.control:       ; CONTROL
                LOAD    R5, M[R4]
                INC     R4
                DEC     R5
                BR.Z    .position
                DEC     R5
                BR.Z    .color
                BR      .end
.position:      ; GROUND POSITION
                LOAD    R5, M[R4]
                INC     R4
                STOR    M[R1], R5
                BR      .terminalloop
.color:         ; GROUND COLOR
                LOAD    R5, M[R4]
                INC     R4
                STOR    M[R2], R5
                BR      .terminalloop
.end:           ; RESTORE CONTEXT
                LOAD    R5,M[R6]
                INC     R6
                LOAD    R4,M[R6]
                INC     R6
                LOAD    R3,M[R6]
                INC     R6
                JMP     R7
                
;=================================================================
; imagemgameover: function that creates the game over image
;-----------------------------------------------------------------                
imagemgameover: ; SAVE CONTEXT 
                DEC     R6
                STOR    M[R6],R3
                DEC     R6
                STOR    M[R6],R4
                DEC     R6
                STOR    M[R6],R5
                ; TERMINAL ADDRESSES
                MVI     R1, TERM_CURSOR
                MVI     R2, TERM_COLOR
                MVI     R3, TERM_WRITE
                MVI     R4, GameoverStr
.terminalloop:  ; WRITE GROUND
                LOAD    R5, M[R4]
                INC     R4
                CMP     R5, R0
                BR.Z    .control
                STOR    M[R3], R5
                BR      .terminalloop
.control:       ; CONTROL
                LOAD    R5, M[R4]
                INC     R4
                DEC     R5
                BR.Z    .position
                DEC     R5
                BR.Z    .color
                BR      .end
.position:      ; GROUND POSITION
                LOAD    R5, M[R4]
                INC     R4
                STOR    M[R1], R5
                BR      .terminalloop
.color:         ; GROUND COLOR
                LOAD    R5, M[R4]
                INC     R4
                STOR    M[R2], R5
                BR      .terminalloop
.end:           ; RESTORE CONTEXT
                LOAD    R5,M[R6]
                INC     R6
                LOAD    R4,M[R6]
                INC     R6
                LOAD    R3,M[R6]
                INC     R6
                JMP     R7
                
;=================================================================
; imagenscato: function that creates the cactus image
;-----------------------------------------------------------------
imagenscato:    ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R4
                DEC     R6
                STOR    M[R6],R5
                ; INITIAL FIELD POSITION TERMINAL
                MVI     R1,inicioterreno
                MVI     R2,dim
                MVI     R4,2800h
.loop:          LOAD    R5,M[R1]
                ; CHECK CACTUS
                CMP     R5,R0
                BR.NZ   .criarcacto
                BR.Z    .end
.end:           ; ADVANCE POSITION
                INC     R4
                INC     R1
                DEC     R2
                BR.NZ   .loop
                ; RESTORE CONTEXT
                LOAD    R5,M[R6]
                INC     R6
                LOAD    R4,M[R6]
                INC     R6
                JMP     R7
.criarcacto:    ; CACTUS HEIGHT > 0 
                DEC     R6
                STOR    M[R6],R1
                DEC     R6
                STOR    M[R6],R2
                DEC     R6
                STOR    M[R6],R4
                ; CACTUS POSITION
.loopcriar:     MOV     R1,R4
                MVI     R2,TERM_CURSOR
                STOR    M[R2],R1
                ; CACTUS COLOR
                MVI     R1,1ch
                MVI     R2,TERM_COLOR
                STOR    M[R2],R1
                ; WRITE CACTUS
                MVI     R1,'╞'
                MVI     R2,TERM_WRITE
                STOR    M[R2],R1
                MVI     R1,0100h
                SUB     R4,R4,R1
                DEC     R5
                BR.NZ   .loopcriar
                LOAD    R4,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                LOAD    R1,M[R6]
                INC     R6
                BR      .end

;=================================================================
; atualizajogo: function that updates the playing field
;-----------------------------------------------------------------
atualizajogo:   ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R4 
                DEC     R6
                STOR    M[R6],R5 
                DEC     R6
                STOR    M[R6],R7
                ;MOVE ELEMENTS ONE POSITION LEFT
                DEC     R2 
loop:           MOV     R4,R1 
                INC     R1
                LOAD    R5,M[R1] 
                STOR    M[R4],R5 
                DEC     R2 
                BR.P    loop 
                ; GENERATES CACTUS HEIGHT
                JAL     geracacto
                ; SAVE CACTUS HEIGHT
                STOR    M[R1],R3        
                ; RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                LOAD    R5,M[R6] 
                INC     R6
                LOAD    R4,M[R6] 
                INC     R6                
                JMP     R7
                
;=================================================================
; geracacto: function that generates height of the cactus
;-----------------------------------------------------------------
geracacto:      ;SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R1 
                DEC     R6
                STOR    M[R6],R4 
                DEC     R6 
                STOR    M[R6],R5 
                ; LOAD SEED
                MVI     R1,altura
                MVI     R2,semente
                LOAD    R4,M[R2] 
                MVI     R5,1
                AND     R5,R4,R5
                SHR     R4
                CMP     R5,R0
                BR.NZ   if1
mid:            ; 95% OF THE VALUES REPRESENTED WITH 16 BITS
                MVI     R5,62258
                CMP     R4,R5
                BR.C    if2
                ; LOAD MAXIMUM HEIGHT OF CACTUS
                LOAD    R5,M[R1] 
                DEC     R5
                AND     R3,R4,R5
                INC     R3
                ; UPDATES SEED VALUE
end:            STOR    M[R2],R4 
                ; RESTORE CONTEXT
                LOAD    R5,M[R6] ;Repõe o valor de R5
                INC     R6
                LOAD    R4,M[R6] ;Repõe o valor de R4
                INC     R6
                LOAD    R1,M[R6] ;Repõe o valor de R1
                INC     R6
                JMP     R7
if1:            ;IF SEED ODD
                MVI     R5,b400h
                XOR     R4,R4,R5
                BR      mid
if2:            ;IF IN [0,62258] 
                MVI     R3,0
                BR      end

;=================================================================
; PROCESS_TIMER_EVENT: processes events from the timer
;-----------------------------------------------------------------
PROCESS_TIMER_EVENT:
                ; DEC TIMER_TICK
                MVI     R2,TIMER_TICK   
                LOAD    R1,M[R2]
                DEC     R1
                STOR    M[R2],R1
                ; IGNORE EVENT IF GAMEOVER
                MVI     R1,GAME_OVER
                LOAD    R2,M[R1]
                CMP     R2,R0
                JMP.NZ  R7
                ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R7
                ; CLEAR DISPLAY
                MVI     R1,FFFFh
                MVI     R2,TERM_CURSOR
                STOR    M[R2],R1
                ; UPDATE FIELD
                MVI     R1,inicioterreno
                MVI     R2,dim 
                JAL     atualizajogo
                ; FIELD IMAGE
                JAL     imagemterreno
                JAL     imagenscato
                JAL     imagemdino
                ; VERIFY COLISION
                JAL     colisao
                ; CHECK GAMEOVER
                MVI     R1,GAME_OVER
                LOAD    R2,M[R1]
                CMP     R2,R0
                BR.Z    .notgameover
                JAL     imagemgameover
                ; RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                JMP.NZ  R7
.notgameover:   ; UPDATE DISPLAY
                JAL     atualizadisplay
                ; RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                JMP     R7


;*****************************************************************
; AUXILIARY INTERRUPT SERVICE ROUTINES
;*****************************************************************
AUX_TIMER_ISR:  ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R1
                DEC     R6
                STOR    M[R6],R2
                ; RESTART TIMER       ; set timer to count value
                MVI     R1,TIMER_CONTROL
                MVI     R2,TIMER_SETSTART
                STOR    M[R1],R2          ; start timer
                ; INC TIMER FLAG
                MVI     R2,TIMER_TICK
                LOAD    R1,M[R2]
                INC     R1
                STOR    M[R2],R1
                ; RESTORE CONTEXT
                LOAD    R2,M[R6]
                INC     R6
                LOAD    R1,M[R6]
                INC     R6
                JMP     R7
                
                
AUX_KEYZERO:    ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R1
                DEC     R6
                STOR    M[R6],R2
                ; RESET DISPLAY
                DEC     R6
                STOR    M[R6],R7
                JAL     resetdisplay
                LOAD    R7,M[R6]
                INC     R6
                ; RESET TERMINAL 
                MVI     R1,TERM_CURSOR
                MVI     R2,FFFFh
                STOR    M[R1],R2
                ; RESET SEED
                MVI     R1,semente
                MVI     R2,5
                STOR    M[R1],R2
                ; RESET JUMP
                MVI     R1,JUMP
                STOR    M[R1],R0
                MVI     R1,ALTURA_DINO
                MVI     R2,2828h
                STOR    M[R1],R2
                ;RESET FIELD
                MVI     R1,inicioterreno         
                MVI     R2,dim
.loop:          STOR    M[R1],R0
                INC     R1
                DEC     R2
                BR.NZ   .loop
                ; START GAME
                MVI     R1,GAME_OVER
                STOR    M[R1],R0
                ; RESTORE CONTEXT
                LOAD    R2,M[R6]
                INC     R6
                LOAD    R1,M[R6]
                INC     R6
                JMP     R7


AUX_KEYUP:      ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R1
                DEC     R6
                STOR    M[R6],R2
                DEC     R6
                STOR    M[R6],R4
                ; VERIFY JUMP IN PROGRESS
                MVI     R1,JUMP
                LOAD    R2,M[R1]
                CMP     R2,R0
                BR.NZ   .end_key_up
                MVI     R2,JUMP_TIME
                LOAD    R4,M[R2]
                STOR    M[R1],R4
                ; RESTORE CONTEXT
.end_key_up:    LOAD    R4,M[R6]
                INC     R6
                LOAD    R2,M[R6]
                INC     R6
                LOAD    R1,M[R6]
                INC     R6
                JMP     R7
                

;*****************************************************************
; INTERRUPT SERVICE ROUTINES
;*****************************************************************
                ORIG    7FF0h
TIMER_ISR:      ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R7
                ; CALL AUXILIARY FUNCTION
                JAL     AUX_TIMER_ISR
                ; RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                RTI

                ORIG    7F00h
KEYZERO:        ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R7
                ;CALL AUXILIARY FUNCTION
                JAL     AUX_KEYZERO
                ; RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                RTI

                ORIG    7F30h
KEYUP:          ; SAVE CONTEXT
                DEC     R6
                STOR    M[R6],R7
                ; CALL AUXILIARY FUNCTION
                JAL     AUX_KEYUP
                ; RESTORE CONTEXT
                LOAD    R7,M[R6]
                INC     R6
                RTI
