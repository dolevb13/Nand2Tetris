// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

	@SCREEN	
	D=A						// D = 16,384
	@topScreenAddress
	M=D-1					// topScreenAddress = 16,383
	@KBD
	D=A						// D = 24,576
	@bottomScreenAddress
	M=D-1					// bottomScreenAddress = 24,575
	@topScreenAddress
	D=M						// D = topScreenAddress = 16,383
	@currentPixel
	M=D						// currentPixel = 16,383
	
(LOOP) 
	@KBD
	D=M						// D = RAM[24,576] = Scan-code of pressed key.
	@FILL_WHITE
	D;JEQ					// if RAM[24,576] = 0 (then no key is pressed) goto FILL_WHITE
	@FILL_BLACK					// else goto FILL_BLACK
	0;JMP

(FILL_BLACK) 
	@bottomScreenAddress
	D=M						// D = bottomScreenAddress = 24,575
	@currentPixel
	D=D-M					// bottomScreenAddress = bottomScreenAddress - currentPixel
	@LOOP
	D;JEQ					// if bottomScreenAddress = currentPixel (reached the bottom of the screen and has to keep the state) goto LOOP
	@currentPixel
	A=M						// A = currentPixel
	M=-1					// RAM[A] = 1111 1111 1111 1111 = fill the pixel in black
	@currentPixel
	M=M+1					// currentPixel = currentPixel + 1, moves to the next pixel from top to bottom.
	@LOOP
	0;JMP

(FILL_WHITE)
	@topScreenAddress
	D=M						// D = topScreenAddress = 16,383
	@currentPixel
	D=D-M					// topScreenAddress = topScreenAddress - currentPixel
	@LOOP
	D;JEQ					// if topScreenAddress = currentPixel (reached the top of the screen and has to keep the state) goto LOOP
	@currentPixel
	A=M						// A = currentPixel
	M=0						// RAM[A] = 0 = fill the pixel in white
	@currentPixel
	M=M-1					// currentPixel = currentPixel - 1, moves to the next pixel from bottom to top.
	@LOOP
	0;JMP