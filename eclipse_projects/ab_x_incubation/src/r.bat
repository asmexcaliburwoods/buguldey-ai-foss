javac *.java -d ..\classes
@if errorlevel 1 goto q
java -cp ..\classes Main
@:q