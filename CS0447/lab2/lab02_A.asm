.data
	prompt1: .asciiz "What is the first value?\n"
	prompt2: .asciiz "What is the second value?\n"
	result1: .asciiz "The sum of " 
	result2: .asciiz " and "
	result3: .asciiz " is "
.text
	addi $v0, $0, 4 # print prompt1
	la $a0, prompt1
	syscall
	
	addi $v0, $0, 5 # read from user prompt 1
	syscall
	add $s0, $0, $v0 # store address of answer into s0
	
	addi $v0, $0, 4 # print prompt 2
	la $a0, prompt2
	syscall
	
	addi $v0, $0, 5 # read from user prompt 2
	syscall
	add $s1, $0, $v0 # store address of answer into s1
	
	add $s2, $s0, $s1 # add sum of two numbers read
	
	addi $v0, $0, 4 #print result1
	la $a0, result1
	syscall 
	
	addi $v0, $0, 1 # print integer from first prompt
	add $a0, $0, $s0 
	syscall
	
	addi $v0, $0, 4 # print result2 "and"
	la $a0, result2
	syscall
	
	addi $v0, $0, 1 # print integer from 2nd prompt
	add $a0, $0, $s1
	syscall
	
	addi $v0, $0, 4 # print result 3 "is"
	la $a0, result3
	syscall
	
	addi $v0, $0, 1 # print sum of two integers
	add $a0, $0, $s2 # s2 is sum
	syscall
	
	addi $v0, $0, 10 #terminate program at the end
	syscall
	
	
	
	
