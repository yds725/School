.data
	title: .asciiz  "a*b calculator\n"
	prompt1: .asciiz  "Please enter a: "
	prompt2: .asciiz  "Please enter b: "
	operand: .asciiz  " * "
	equal: .asciiz  " = "
	nonnegative: .asciiz  "Integer must be nonnegative!\n"
	
.text
	
	addi $t0, $0, 0 # this is product variable result
	
	addi $v0, $0, 4 # print title
	la $a0, title
	syscall

loop1:	
	addi $v0, $0, 4 # print prompt 1
	la $a0, prompt1
	syscall
	
	addi $v0, $0, 5 # read user's integer input
	syscall
	add $s0, $0, $v0 # s0 = first integer
	
	slt $s2, $s0, $0 # if(s0 < 0 ) s2 = 1
	beq $s2, 1, negative1 # if s0 is less than 0 
	
	
	add $t1, $0, $s0 # store s0 into t1 (for printing)

loop2:	
	addi $v0, $0, 4 # print prompt 2
	la $a0, prompt2
	syscall
	
	addi $v0, $0, 5 # read second integer
	syscall
	add $s1, $0, $v0 # s1 = second integer
	
	slt $s2, $s1, $0 # if s1 < 0 go to negative 2
	beq $s2, 1, negative2
	
	add $t3, $0, $s1 # store s1 into t3 (for printing)

compareSecondInteger:
	andi $t2, $s1, 1 # use AND operation to get only LSB from second integer(1 or 0)	
	beq $t2, 0, shiftBit # if LBS is 1 go to shift bit and multiply
	add $t0, $t0, $s0 # result += first integer 
	
	#srl $s1, $s1, 1 # shift second integer right one time 2^0 -> 2^1 -> 2^2 -> 2^3 ....
	#addi $t1, $t1, 1 # increment shift counter 
	#beq $t2, 0, compareSecondInteger # if LSB = 0 do not shift bits but go back check next one
shiftBit:
	sll $s0, $s0, 1 # shit first integer left one time (multiply function) x*2^3 // x << 3
	srl $s1, $s1, 1 # shift second integer right one time 2^0 -> 2^1 -> 2^2 -> 2^3 ....
													#addi $t1, $t1, 1 # increment shift counter 
	bne $s1, $0, compareSecondInteger # if second integer still remains factors of 2 go back to compare again or multipy again
	beq $s1, $0, result
	
negative1:
	addi $v0, $0, 4 # print nonnegative
	la $a0, nonnegative
	syscall
	j loop1 # go back to prompt 1 

negative2:
	addi $v0, $0, 4 # print nonnegative
	la $a0, nonnegative
	syscall
	j loop2 # go back to prompt 2
	
	
result:
	addi $v0, $0, 1 # print first integer
	add $a0, $0, $t1
	syscall
	
	addi $v0, $0, 4 # print final result
	la $a0, operand
	syscall
	
	addi $v0, $0, 1 # print second integer
	add $a0, $0, $t3
	syscall
	
	addi $v0, $0, 4 # print final result
	la $a0, equal
	syscall
	
	addi $v0 $0, 1 # print product integer
	add $a0, $0, $t0
	syscall
	
	addi $v0, $0, 10 # finish program
	syscall
	
	
	
	
	
	
