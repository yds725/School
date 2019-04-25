.data
	type: .asciiz "bit", "nybble", "byte", "half", "word"
	bits: .asciiz "one", "four", "eight", "sixteen", "thirty-two"
	input: .space 100
	prompt1: .asciiz "Please enter a datatype:\n"
	result: .asciiz "Number of bits: " 
	not_found: .asciiz "Not Found!"
	output: .space 50
	
.text
	# print out prompt1
	addi $v0, $0, 4
	la $a0, prompt1
	syscall
	
	la $a0, input
	addi $a1, $0, 100
	jal readString
	
	add $s1, $0, $0 #index 
	add $s0, $0, $0 #loading address of type array (starting at 0) s0 will be used for increment	
	add $s3, $0, $0 #increment for address of bits array
main:
	la $a1, type # load type array
	jal checkType
	add $t0, $0, $v0 #result if equal 1
	
	beq $t0, 1, getBits #debug
	beq $t0, 0, elseStatement

readString:
	addi $v0, $0, 8
	syscall
	
	add $t0, $0, $a0 #loading address of string (starting at 0) 
setNewLineToNull:
	lbu $t2, ($t0)	# load each byte address value
	addi $t0, $t0, 1 # increment index of byte
	bne $t2, $0, setNewLineToNull # loop until fining null character
	sub $t0, $t0, 2 # subtract 2 from Null character index to remove newline (last character)
	sb $0, ($t0) # save or put 0 into that newline index (last char index)
	#add $t3, $0, $ra # so for recursion you just cannot do this way  
	#jal stringLength
	#add $ra, $0, $t3	
	jr $ra
	
checkType:
	add $t4, $s0, $a1 #loading adress of type array[s0]
	add $t0, $0, $0 #loading address of input string (starting at 0)
	
loop:
	lbu $t2, input($t0) # loading each byte of input string
	lbu $t3, 0($t4) # loading each byte of type array
	beq $t3, 0, check # checking if 
	beq $t2, 0, missMatch #if it reaches the end null character
	slt $t5, $t3, $t2 # compare them
	bne $t5, 0, missMatch
	# bne $t2, $t3, missMatch # if not equal 
	addi $t0, $t0, 1 #increment each byte 
	addi $t4, $t4, 1 #increment each byte
	j loop
	
missMatch:
	addi $v0, $0, 0
	jr $ra	
check: 
	bne $t2, 0, missMatch
	addi $v0, $0, 1
	jr $ra

getBits:
	la $a2, bits #loading address of bits array
	la $a3, output #loading output
	jal lookUp
	
	addi $v0, $0, 4 #print out result
	la $a0, result
	syscall
	
	addi $v0, $0, 4 #print out string stored in output buffer
	la $a0, output
	syscall
	
	addi $v0, $0, 10
	syscall

lookUp:
	add $t5, $0, $a2 #t0 is now bits array
	add $t1, $0, $s3 #t1 is now current index
	add $t4, $t5, $t1 #t4 is output address
	  # get address from current index
loop2:
	lb $t3, 0($t4) # put that adress into return value
	beq $t3, 0, endLoop2	
	sb $t3, ($a3) # store that into output
	addi $t4, $t4,1 #increment address of string array
	addi $a3, $a3, 1 #increment output address
	j loop2

endLoop2:
	jr $ra
	
elseStatement:
	addi $s1, $s1, 1 #increment index
	beq $s1, 5, notFound
	
	la $a1, type # loading address of type
	add $s4, $0, $s0 #to save s0...
	jal strLength
	add $s2, $0, $v0 # s2 is now string of length
	addi $s0, $s0, 1 # type adrees[s0] = s0 + 1 + string size (s2)
	add $s0, $s0, $s2 
	
	la $a1, bits #loading bits address
	add $s4, $0, $s3 #to save s3
	jal strLength
	add $t3, $0, $v0
	addi $s3, $s3, 1
	add $s3, $s3, $t3 
	
	j main
	
notFound:
	addi $v0, $0 4
	la $a0, not_found #print not found
	syscall
	
	addi $v0, $0, 10 #system exit
	syscall 
	
strLength:
	add $t6, $s4, $a1 #t6 is addres of type array starting at s0
	addi $t1, $0, 0 #counter
	
loop3:
	lbu $t2, ($t6) # load each byte of address into t2 
	beq $t2, $0, endLoop3 #if reaches null character, go finish loop
	addi $t1, $t1, 1 # increment counter of characters 
	addi $t6, $t6, 1 # increment 1 byte of address or character n
	j loop3
	
endLoop3:
	add $v0, $0, $t1 # return value: counter for stringLength
	jr $ra
