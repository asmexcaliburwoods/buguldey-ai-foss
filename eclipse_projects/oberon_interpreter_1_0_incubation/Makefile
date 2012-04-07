Parser.h:
Parser.cpp:
Scanner.cpp:
Scanner.h:
coco:	Coco/Coco
	Coco/Coco Oberon.atg -namespace Oberon

oberonInterpret:	clean coco Parser.cpp Scanner.cpp Scanner.h Parser.h
	g++ -g *.cpp -o oberon_interpret $(CFLAGS)
	
all:	oberonInterpret

wakeOberon:	oberonInterpret
	./oberon_interpret test.Mod 
	
clean:
	rm -f b oberon86 Parser.cpp Parser.cpp.old Parser.h Parser.h.old Scanner.h Scanner.h.old Scanner.cpp Scanner.cpp.old

