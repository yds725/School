.text
	addi $s0, $0, 3 #set s0 as 3
	addi $v0, $0, 1 #syscall 1 - print integer (this statement does not have to stay with syscall)
	addi $a0, $0, 5 # print 5 a0

loop:
	beq $a0, $0, finish #if printed integer == 0 loop is done
	bne $a0, $s0, printout # if integer printed 1= 3 go to printout that number
	addi $a0, $a0, -1 # decrement a0 
	j loop
	
printout:
	syscall
	addi $a0, $a0, -1 #decrement a0
	j loop 
finish:
	addi $v0, $0, 10 # terminate program if loop is done
	syscall
	
	