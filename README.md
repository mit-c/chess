# chess
The aim is to create a fully function chess engine with UI.

`cd` to the chess folder.
The line below compiles all the .java files into .class files.
```shell

javac -d bin *.java

```

`cd`to the bin folder then build an executable .jar file:


```shell

jar cfm ../chess.jar MANIFEST.MF *.class ../chessSprites

```

Then double click file.