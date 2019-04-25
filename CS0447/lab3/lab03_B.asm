.data
	prompt: .asciiz "Please enter one integer: "
	result: .asciiz "Here is the output: " 
.text

addi $t0, $0, 0x00000f00 # number 0000 0000 0000 0000 0000 1111 0000 0000 (number that compare 8~11 bits)

addi $v0, $0, 4 # print prompt
la $a0, prompt
syscall

addi $v0, $0, 5 # read user 's input
syscall
add $s0, $0, $v0 # s0 = user's value

and $t1, $s0, $t0 # do AND logical to get only 8,9,10,11 bit
srl $t2, $t1, 8 # shit that t1 to right 8 bits 

addi $v0, $0, 4 # print result
la $a0, result
syscall

addi $v0, $0, 1 # print the output or final integer 8~11 bits
add $a0, $0, $t2 # t2 = result
syscall

addi $v0, $0, 10 # finsih program
syscall




