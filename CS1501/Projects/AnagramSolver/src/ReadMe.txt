Anagram Decoder
Daesang Yoon day42@pitt.edu
October 2018

INTRODUCTION

Anagram decoding problem. StringTable is implementation for hash table
AnagramDecoder is main execution function.

INSTALLATION

The following files should be present:

     AnagramDecoder.java (main method)
	 AnagramSolver.java
	 CharCompare.java
	 StringTable.java

USAGE
First, compile AnagramDecoder.java file (javac AnagramDecoder.java)
Then, run AnagramDecoder with command (java AnagramDecoder dictionary.txt). It takes only args[0] which is dictionary file.
Then, enter a word (to decode) with no white space followed by instruction.

I changed how program should run differently from the example instructor gave. 
I think entering word that user wants to decode on command line as argument everytime is 
inconvenient for grader to test and run program.
So this program just asks user to enter any word that user wants to scramble during runtime. So that grader can save some time.

Also hashtable is implemented with array and array size is 3001 (which is larger than number of dictionary words).

The tricky part of this project is actually decoding anagram. It has nothing to do with hash table as hash table is just for
storing words in dictionary. 

PROBLEMS
It compiles and run successfully.
Compared to example output, the order is different but it still prints all anagrams same as example output.
Entering 'meastntiitchi' (which produces more than 5000 lines of anagrams according to example output) takes some time to
print all possible anagrams but it is still reasonable (30sec~1min)
Also, entering odg prints god, dog which it should print.
