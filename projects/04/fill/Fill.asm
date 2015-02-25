// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, the
// program clears the screen, i.e. writes "white" in every pixel.


// Overview: 

// while true 
//     while row < 256 
//         row++ 
//         while col < 32
//             col++ 
//             RAM[SCREEN + row*32 + col] = (RAM[KBD] > 0)?(black): (white)
//         end 
//     end 
// end 

// Actual Program

(INFINITE)

    @SCREEN
    D = A
    @position
    M = D

    @row 
    M = 0
    (ROW)
        
        @col 
        M = 0
        (COL)
            // Asign black to word 
            D = !0
            @assign 
            M = D 
            
            // Skip white assignment if key is pressed. 
            @KBD
            D = M
            @WHITE
            D;JGT 

            // Assign white to word 
            D = 0
            @assign
            M = D

            (WHITE)

            // write white or black to word 
            @assign
            D = M
            @position 
            A = M 
            M = D // M[position] 
            
            // increment word 
            @position
            M = M + 1

            // Loop through COL while col < 32
            @32 // 32
            D = !A // D = ~32
            @col 
            M = M + 1 // M[col]++
            D = D & M // D = ~32 & M[col]
            @COL
            D;JNE // if (M[col] & ~32 != 0) then (goto COL)

        // Loop thrugh ROW while row < 256  
        @256
        D = !A
        @row
        M = M + 1
        D = D & M
        @ROW
        D;JNE

    // Loop forever
    @INFINITE
    0;JMP
