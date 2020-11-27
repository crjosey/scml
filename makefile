#
#	DPL Make File
# 
#	makefile
#	CS403 | DPL
#
#	Author: Ryan Josey
#

top:
	javac -d bin -sourcepath src src/Interpreter.java

clean:
	rm -f bin/*.class
	clear

error1:
	echo error1 - missing doctype
	cat ./examples/error1.scml

error1x:
	./dpl ./examples/error1.scml
	
error2:
	echo error1 - missing function name
	cat ./examples/error2.scml

error2x:
	./dpl ./examples/error2.scml

error3:
	echo error1 - global tag not closed correctly
	cat -n ./examples/error3.scml

error3x: 
	./dpl ./examples/error3.scml

arrays:
	echo arrays - dynamic and static arrays
	cat -n ./examples/arrays.scml

arraysx:
	./dpl ./examples/arrays.scml

conditionals:
	echo conditionals
	cat -n ./examples/conditionals.scml
	
conditionalsx:
	./dpl ./examples/conditionals.scml

recursion:
	echo recursion
	cat -n ./examples/recursion.scml

recursionx:
	./dpl ./examples/recursion.scml

iteration:
	echo iteration
	cat -n ./examples/iteration.scml

iterationx:
	./dpl ./examples/iteration.scml

functions:
	echo functions
	cat -n ./examples/functions.scml

functionsx:
	./dpl ./examples/functions.scml

dictionary:
	echo Not implemented

dictionaryx:
	echo Not implemented
	
problem:
	echo problem
	cat -n ./examples/rpn.scml
    
problemx:
	./dpl ./examples/rpn.scml
