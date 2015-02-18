// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Initalize the output 
@0
D = A
@R2
M = D 

// if R0 == 0 then GOTO END
@R0
D = M
@END
D;JEQ

// if R1 == 0 then GOTO END
@R1
D = M
@END
D;JEQ

// if R1 >= R0 then don't swap 
@R0
D = D - M // D = R1 - R0
@LOOP
D;JGE // GOTO LOOP

// R0 <-> R1

// swap = R0 
@R0
D = M 
@swap
M = D 

// R0 = R1 
@R1 
D = M
@R0
M = D

// R1 = swap (R0)
@swap
D = M
@R1
M = D

// multiplication 
(LOOP)
    // R2 += R1
    @R1
    D = M
    @R2
    M = M + D 

    // R0--
    @R0
    M = M - 1

    // if R0 > 0 then GOTO LOOP
    D = M
    @LOOP 
    D;JGT
(END)
