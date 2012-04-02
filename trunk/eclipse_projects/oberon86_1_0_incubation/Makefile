Parser.h:
Parser.cpp:
Scanner.cpp:
Scanner.h:
coco:	Coco/Coco
	Coco/Coco oberon86.atg -namespace Oberon

oberon86:	clean coco Parser.cpp Scanner.cpp Scanner.h Parser.h
	g++ -g *.cpp -o oberon86 $(CFLAGS)
	
all:	oberon86

wakeOberon:	oberon86
	./oberon86 test.Mod 
	
clean:
	rm -f b oberon86 Parser.cpp Parser.cpp.old Parser.h Parser.h.old Scanner.h Scanner.h.old Scanner.cpp Scanner.cpp.old

