.text
top_of_loop: addi $t9, $zero, 0

addi $v0, $zero, 32
addi $a0, $zero, 1000
syscall

addi $t9, $t9, 179

addi $v0, $zero, 32
addi $a0, $zero, 1000
syscall

addi $t9, $t9, -293

addi $v0, $zero, 32
addi $a0, $zero, 1000
syscall

addi $t9, $t9, 561
j top_of_loop

