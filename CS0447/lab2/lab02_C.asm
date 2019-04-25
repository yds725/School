.data
	prompt1: .asciiz "Choose a number (0 to 9):\n"
	lowguess: .asciiz "Your guess is too low, try again!\n"
	highguess: .asciiz "Your guess is too high, try again!\n"
	invalidinput: .asciiz "Wrong input, try again!\n"
	final1: .asciiz "You win, Congrats! It took you " 
	final2: .asciiz " tries!"

.text
	# generating random number:
	addi $v0, $0, 30 # syscall - system time (lower bit a0)
	syscall
	add $s0, $0, $a0 # set s0 as a0
	
	addi $v0, $0, 40 # syscall - set RNG ID
	add $a0, $0, $0 
	add $a1, $0, $s0 # set a1 seed as lower bit
	syscall
	
	addi $v0, $0, 42 # syscall - set RNG upper bound
	add $a0, $0, $0
	addi $a1, $0, 10 # 0 to 9
	syscall
	
	add $s0, $0, $a0 # s0 = generated random number
	add $s2, $0, $0 # variable counting tries
	
	addi $t3, $0, 9 # t3 = 9
	
loop:
	addi $v0, $0, 4 # print out first prompt
	la $a0, prompt1
	syscall
	
	addi $v0, $0, 5 # read user input (integer)
	syscall
	add $s1, $0, $v0 # s1 = user input (guess)
	
	slt $t1, $s1, $0 # if s1 < 0, return 1
	slt $t2, $t3, $s1 # if 9 < s1 return 1
	beq $t1, 1, invalid #if invalid input 
	beq $t2, 1, invalid # if invliad input
	
	beq $s0, $s1, finish
	
	slt $t0, $s1, $s0 # return 1 if s1 (userinput) < s0 (random n); otherwise return 0
	beq $t0, 1, lower # if guess low
	beq $t0, 0, higher # if guess high
	
invalid:
	addi $v0, $0, 4 # print out invalid input
	la $a0, invalidinput
	syscall
	j loop
	
	
lower:
	addi $s2, $s2, 1 # increment tries
	
	addi $v0, $0, 4 # print out low guess
	la $a0, lowguess
	syscall
	j loop

higher: 
	addi $s2, $s2, 1 # increment
	
	addi $v0, $0, 4 # print out high guess
	la $a0, highguess
	syscall
	j loop
		
finish:
	addi $s2, $s2, 1 # if valid input true increment tries
	
	addi $v0, $0, 4 # print out result
	la $a0, final1
	syscall
	
	addi $v0, $0, 1 # print out number of tries
	add $a0, $0, $s2 # set a0 as s2
	syscall 
	
	addi $v0, $0, 4 # print out result
	la $a0, final2
	syscall
	
	addi $v0, $0, 10 # terminate program
	syscall
	
	
