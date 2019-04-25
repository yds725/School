.text

addi $v0, $0, 30 # syscall 30: return system time a0(lower 32bit) a1(higher 32bit)
syscall
add $s0, $0, $a0

addi $v0, $0, 40 # syscall 40: set RNG seed 
addi $a0, $0, 1 # RNG ID to a0
add $a1, $0, $s0 # seed value in a1(lower 32bits)
syscall
	
	addi $v0,$0, 42 # syscall 42: generate random integers
	add $a0, $0, 1 # Set RNG ID
	addi $a1, $0, 10 # setting upper bound of Random-generated number
	syscall
	add $s0, $0, $a0 # that generated number stored in a0
	
	#addi $v0, $0, 1
	#add $a0, $0, $s0
	#syscall