coco_build:
	make -C COCOR/COCOR_ITSELF/ gpp all
	
Parser.h:
Parser.cpp:
Scanner.cpp:
Scanner.h:
coco:	coco_build 
	COCOR/COCOR_ITSELF/Coco smm.atg -namespace smm

smm_exec:	clean coco Parser.cpp Scanner.cpp Scanner.h Parser.h
	g++ -std=c++0x -g *.cpp -o smm $(CFLAGS)
	
all:	smm_exec

wakeSMM:	smm_exec
	./smm prop_small_proved.smm
	
clean:
	rm -f smm Parser.cpp Parser.cpp.old Parser.h Parser.h.old Scanner.h Scanner.h.old Scanner.cpp Scanner.cpp.old

