# pascal-compiler

This is a **Pascal** compiler written in Java.

`TokenScanner.java` - reads each character and creates a list of tokens
`Token.java` - class for the Token objects

`Parser.java` - proccesses/analyzes the list of tokens and and generates a byte array of instructions containing P-codes and addresses
`Simulator.java` - reads the instructions returned by the Parser and runs it on a stack data type

`Emulator.java` - main program that passes the token list returned from the TokenScanner to the Parser. Then the instruction array is passed to the Simulator to run the Pascal program and generate output.

`SymbolTable.java` - hash table to store symbols
`Symbol.java` - class for the objects stored in SymbolTable

`keywords.txt` - list of Pascal keywords

## How to run:
1. Change directory to src/
2. Compile Emulator.java
3. Provide an Pascal file as input to the Emulator program`

## For example: 
`cd src/`
`javac Emulator.java`
`java Emulator ../examples/array.pas`
