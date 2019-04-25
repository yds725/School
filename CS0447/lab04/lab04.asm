.data 
	userInput: .space 64
	output: .space 64
	prompt1: .asciiz "Enter a string:\n"
	sentence1: .asciiz  "This string has "
	sentence2: .asciiz  " characters.\n"
	prompt_Index1: .asciiz  "Specify start index: "
	prompt_Index2: .asciiz  "Specify end index: "
	result: .asciiz "Your substring is:\n"
	
.text
	addi $v0, $0, 4
	la $a0, prompt1
	syscall
	
	la $a0, userInput
	addi $a1, $zero, 64 #100
	jal readString # automaticaaly stores 13 in ra register
	
	la $a0, userInput
	jal stringLength
	add $s0, $0, $v0 # it is good to copy returned value into other register; counter stringLength
	
	addi $v0, $0, 4
	la $a0, sentence1
	syscall
	
	addi $v0, $0, 1
	add $a0, $0, $s0 # print out stringLength
	syscall
	
	addi $v0, $0, 4
	la $a0, sentence2
	syscall
	
	addi $v0, $0, 4
	la $a0, prompt_Index1
	syscall
	
	addi $v0, $0, 5
	syscall
	add $a2, $0, $v0 #start index
	
	addi $v0, $0, 4
	la $a0, prompt_Index2
	syscall
	
	addi $v0, $0, 5
	syscall
	add $a3, $0, $v0 #end idex
		
	la $a0, userInput # load address of input string
	la $a1, output # a1 address of output buffer
	jal subString
	add $a1, $0, $v0 #putting output address back to a1
	
	addi $v0, $0, 4
	la $a0, result
	syscall
	
	addi $v0, $0, 4
	la $a0, output
	syscall
	
	addi $v0, $0, 10
	syscall
	
readString:
	addi $v0, $0, 8 # read string syscall; needs two arguments
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
	
stringLength: # need to define null terminated address 
	add $t0, $0, $a0 #t0 is address of a0
	addi $t1, $0, 0 #counter
	
loop:
	lbu $t2, ($t0) # load each byte of address into t2 
	beq $t2, $0, end #if reaches null character, go finish loop
	addi $t1, $t1, 1 # increment counter of characters 
	addi $t0, $t0, 1 # increment 1 byte of address or character n
	j loop
	
end:
	add $v0, $0, $t1 # return value: counter for stringLength
	jr $ra
	
subString:
	add $t2, $0, $a2 # start index
	add $t3, $0, $a3 # end index
	add $t0, $0, $a0 # load input string
	add $s1, $0, $s0 # current string length
	add $t1, $0, $a1 # t1 is address of output

	slt $s2, $s1, $t3 # s2 = 1 if current string length is less than end index
	beq $s2, 1, updateEndIndex

continue:	
	slt $s2, $t2, $0 # if start < 0, s2 = 1
	beq $s2, 1, emptyString
	
	slt $s2, $t3, $0 # if end < 0, s2 = 1
	beq $s2, 1, emptyString
	
	slt $s2, $t3, $t2 # if end < start, s2 = 1
	beq $s2, 1, emptyString

	
	#loop that goes through between start & end  amd store only that value into output buffer
	add $t4, $t0, $t2 # t4 is address of [startIndex]
loop2:	
	lbu $t5, 0($t4) # t5 = buffer[start]
	beq $t2, $t3, finish # if loop reaches end index finish
	sb $t5, 0($t1) #put each byte into output buffer
	addi $t4, $t4, 1 # increment 1 byte of address or char
	addi $t1, $t1, 1 # increment output buffer so to save
	addi $t2, $t2, 1 # increment check
	j loop2
	
finish:
	add $v0, $0, $t1 #return value: address of output buffer
	jr $ra
	
updateEndIndex:
	add $t3, $0, $s1 # end index equal to string length
	j continue

emptyString:
	add $v0, $0, $t1
	jr $ra
	
	