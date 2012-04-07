MAKE_Windows_NT=c:/cygwin/bin/make.exe
MAKE=$(MAKE_$(OS))
	
os:

Coco/Coco:	os
	#cd Coco && $(MAKE) all && cd ..
	
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
	
clean:	os
	#cd Coco && $(MAKE) clean && cd ..
	rm -f b oberon_interpret oberon_interpret.exe Parser.cpp Parser.cpp.old Parser.h Parser.h.old Scanner.h Scanner.h.old Scanner.cpp Scanner.cpp.old

