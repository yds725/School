.data 
	prompt1: .asciiz "Enter a nonnegative integer: "
	invalidInput: .asciiz "Invalid Integer; try gain.\n"
	answer: .asciiz "Feb( "
	str: .asciiz " ) = "
.text
main:
	add $v0, $0, 4 # print prompt 1
	la $a0, prompt1
	syscall
	
	li $v0, 5 # same as add instruction 
	syscall #reading integer
	
	add $s0, $0, $v0 # putting user integer to s0
	slt $t5, $s0, $0 # if s0 is less than 0 return 1
	beq $t5, 1, invalid # go to invalid 
	
	move $a0, $s0 # same as adding instru putting s0 into a0
	jal Fibo
	move $s1, $v0 # return last result (putting in s1)
	
	li $v0, 4 # print last result
	la $a0, answer
	syscall
	
	li $v0, 1 # printing user input integer
	move $a0, $s0 # a0 is now final integer
	syscall
	
	li $v0, 4 #print very last step
	la $a0, str
	syscall
	
	li $v0, 1 #print final answer integer
	move $a0, $s1 #final answer
	syscall
	
	li $v0, 10 #system exit
	syscall
	
Fibo:
	slti $t0, $a0, 3 # if input integer is less than 3 return 1
	beq $t0, 0, recurFib
	beq $t0, 1, baseCase # if they are less than 3 these are base cases
	
recurFib:	
	addi $sp, $sp, -12
	sw $ra, 0($sp) # saving last return address
	sw $s2, 4($sp) 
	sw $s3, 8($sp)
	
	move $s2, $a0 # putting first user integer to s2 : s2 is n
	addi $a0, $s2, -1 # n - 1
	jal Fibo
	
	move $s3, $v0 # putting that result of Fib(n-1) into s3
	addi $a0, $s2, -2 # n - 2
	jal Fibo
	add $v0, $v0, $s3 # return val = Feb(n-2) + Fib(n-1)
	move $a0, $s2 # we need to bring a0 back because we do not sw or lw it
	
	j endFibo
	
baseCase:
	beq $a0, $0, zero
	li $v0, 1 # if user integer is 2 or 1 Fib is 1
	jr $ra #jump back to return address
	
zero: 
	li $v0, 0 # if user integer is 0 Fibe is 0
	jr $ra
	
endFibo:
	lw $s3, 8($sp) # restoring back the input
	lw $s2, 4($sp)
	lw $ra, 0($sp)
	addi $sp, $sp, 12 #using stack to save and restore
	jr $ra
	
invalid:
	li $v0, 4 # print invalid input
	la $a0, invalidInput
	syscall
	
	j main # go back to main to try again
	
