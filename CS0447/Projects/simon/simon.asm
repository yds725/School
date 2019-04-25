	# SIMON GAME Program 
	# Author: Daesang Yoon

.data 
	array: .space 1000 # making spaces

.text
# $s0 $s4 $s3 $s5 $t0 $t2   $t9, $t8 

reset:	
	add $t9, $0, $0 # set t9 to 0
	
first_start:	
	addi $s3, $0, 16 # starting game
	bne $s3, $t9, first_start # wait for user to press START button
	addi $t8, $0, 16 # lit 3 times and play tone and enable button
	add $s1, $0, $0 # determines user failed or not (1-Fail 0-Continue)
	add $s0, $0, $0 # counter index
# making random number
generate_RND:

	addi $v0, $0, 30 # system time syscall 
	syscall
	add $s4, $0, $a0 # set s4 = a0 (lower bit)
	
	addi $v0, $0, 40 # set RNG ID
	add $a0, $0, $0 # a0 RNG ID
	add $a1, $0, $s4 # lower bit seed
	syscall
	
	addi $v0, $0, 42 # generating RND upper bounds
	add $a0, $0, $0
	addi $a1, $0, 4 # RND from 0 to 3
	syscall 
	
	add $s4, $0, $a0 # random number from 0 to 3
	
	add $t0, $0, $0 # value going to be stored in array
	
	# l = blue(0) 2 = yellow(1) 4 = green(2) 8 = red(3) 
	addi $t4, $0, 0 # blue button 
	beq $s4, $t4, blue
	
	addi $t4, $0, 1 # yellow button
	beq $s4, $t4, yellow
	
	addi $t4, $0, 2 # green button
	beq $s4, $t4, green
	
	addi $t4, $0, 3 # red button
	beq $s4, $t4, red
	
blue:
	addi $t0, $0, 1 # 1 = blue 
	j storeIntoArray
	
yellow:
	addi $t0, $0, 2 # 2 = yellow
	j storeIntoArray
	
green:
	addi $t0, $0, 4 # 4 = green
	j storeIntoArray
	
red: addi $t0, $0, 8 # 8 = red
	j storeIntoArray
	
storeIntoArray:
	la $s5, array # load address of array
	add $t2, $s5, $s0 # $t2 is address of array[s0]
	sb $t0, 0($t2) # array[$s0] = t0
	add $s0, $s0, 1 # increment index
	j playTune
	
main:	
	beq $s1, $0, generate_RND # if user successful, keep making new sequence
	beq $s1, 1, game_OVER # if user failed, game over 
	
game_OVER:
	addi $t8, $0, 15 # play gameover sound and enable START button
	add $s0, $0, $0 # index of array becoming zero, making new array
	j reset
	
#load number stored from array and pass it to $t8 to play sequence	
playTune:
		
	add $t1, $0, $0 # counter for comparing to array size($s0)
loop1:
	beq $t1, $s0, finishLoop1 # if counter reaches the end of array go to finishLoop
	la $s5, array # load address of array again	
	add $t2, $s5, $t1 # $t2 is address of array[t1]
	lb $t0, 0($t2) # load each number into t0 as loop goes
wait:
	bne $t8, $0, wait
	add $t8, $0, $t0 # pass into t0(color number) into t8
	add $t1, $t1, 1 # increment counter
	j loop1

finishLoop1:
	j userTurn #if program finishes playing sequence now it is user's turn

userTurn:

	add $t1, $0, $0 # counter for array size($s0)

loop2:		
	beq $t1, $s0 finishLoop2 # if counter exceeds array size, finish the loop
	add $t9, $0, $0 # make sure t9 changes to 0 after button is pressed
	
	la $s5, array # load address of array	
	add $t2, $s5, $t1 # t2 is address of array[t1]
	lb $t0, 0($t2) # load each number in array again
waitForUser:
	add $t3, $t9, $0 # t3 is now t9 if button pressed
	beq $t3, $0, waitForUser
	add $t8, $t9, $0 # giving different command to t8 given by button pressed 
	bne $t0, $t3, failure
	addi $t1, $t1, 1 # increment counter
	j loop2
	
finishLoop2:
	add $s1, $0, $0 # user successful 
	j main

failure:
	addi $s1, $0, 1 # user failed
	j main
	
	 
	
	
	
