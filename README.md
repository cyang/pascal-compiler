# pascal-compiler

This is a one-pass **Pascal** compiler using a stack-based representation of intermediate code written in Java.

_**TokenScanner.java**_ - reads each character and creates a list of tokens

_**Token.java**_ - class for the Token objects

_**Parser.java**_ - proccesses/analyzes the list of tokens and and generates a byte array of instructions containing P-codes and addresses

_**Simulator.java**_ - reads the instructions returned by the Parser and runs it on a stack data type

_**Emulator.java**_ - main program that passes the token list returned from the TokenScanner to the Parser. Then the instruction array is passed to the Simulator to run the Pascal program and generate output.

_**SymbolTable.java**_ - hash table to store symbols

_**Symbol.java**_ - class for the objects stored in SymbolTable

_**keywords.txt**_ - list of Pascal keywords

## How to run:
1. Change directory to src/
2. Compile Emulator.java
3. Provide a Pascal file as input to the Emulator program`

## For example: 
1. `cd src/`
2. `javac Emulator.java`
3. `java Emulator ../examples/array.pas`
