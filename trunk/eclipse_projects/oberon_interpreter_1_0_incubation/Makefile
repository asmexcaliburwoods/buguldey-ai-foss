MAKE_Windows_NT=c:/cygwin/bin/make.exe
MAKE_Linux=make
MAKE=$(MAKE_`uname -s`)
	
os:
	echo $0 $*

Parser.h:
Parser.cpp:
Scanner.cpp:
Scanner.h:
coco:	./Coco/Coco
	./Coco/Coco Oberon.atg -namespace Oberon

oberonInterpret:	clean coco Parser.cpp Scanner.cpp Scanner.h Parser.h
#-std=c++0x is for hashmaps (#incl <unordered_map>)
	g++ -std=c++0x -g *.cpp -o oberon_interpret $(CFLAGS)
	@echo g++ finished
	
all:	oberonInterpret

wakeOberon:	oberonInterpret
	./oberon_interpret test.Mod 
	
clean:	os
	#cd Coco && $(MAKE) clean && cd ..
	uname -s
	rm -f b oberon_interpret oberon_interpret.exe Parser.cpp Parser.cpp.old Parser.h Parser.h.old Scanner.h Scanner.h.old Scanner.cpp Scanner.cpp.old

