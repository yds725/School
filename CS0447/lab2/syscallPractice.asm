.data 
	hello: .asciiz "Hello, World\n"
.text
	addi $v0, $0,4 #Syscall 4: print string
	la $a0, hello
	syscall
	
	addi $v0, $0, 5
	syscall
	add $t0, $0, $v0
	
	addi $v0, $0, 1 # Syscall 1: print integer
	add $a0, $0, $t0
	syscall 
	
	addi $v0, $0, 40 # syscall 40: set RNG seed 
	add $a0, $0, 1 # RNG ID to a0
	addi $a1, $0, 5 # seed value in a1
	syscall
	
	addi $v0,$0, 42 # syscall 42: generate random integers
	add $a0, $0, 1 # Set RNG ID
	addi $a1, $0, 10 # setting upper bound of Random-generated number
	syscall
	add $s0, $0, $a0 # that generated number stored in a0
	
	addi $v0, $0, 1
	add $a0, $0, $s0
	syscall