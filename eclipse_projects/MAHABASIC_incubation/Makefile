MAKE_Windows_NT=c:/cygwin/bin/make.exe
MAKE_Linux=make
MAKE=$(MAKE_`uname -s`)
	
os:
	echo $0 $*

libsCompiled:	clean
	g++ -std=c++0x -g *.cpp -o obx.o $(CFLAGS)
	@echo g++ finished
	
all:	libsCompiled

wakeMaha:	oberonInterpret
	../oberon_interpreter_1_0_incubation/oberon_interpret MahaBasic.Mod 
	
clean:	os
	#cd Coco && $(MAKE) clean && cd ..
	rm -f obx.o

