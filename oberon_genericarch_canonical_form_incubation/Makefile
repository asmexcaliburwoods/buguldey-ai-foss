Parser.h:
Parser.cpp:
Scanner.cpp:
Scanner.h:
coco:	Coco/Coco
	Coco/Coco Oberon.atg -namespace Oberon

oberon_exec:	clean coco Parser.cpp Scanner.cpp Scanner.h Parser.h
	g++ -g *.cpp -o oberon $(CFLAGS)
	
all:	oberon_exec

wakeOberon:	oberon_exec
	./oberon test.Mod 
	
clean:
	rm -f oberon Parser.cpp Parser.cpp.old Parser.h Parser.h.old Scanner.h Scanner.h.old Scanner.cpp Scanner.cpp.old

