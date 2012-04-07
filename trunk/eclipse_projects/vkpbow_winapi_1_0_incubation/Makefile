MAKE_Windows_NT=c:/cygwin/bin/make.exe

OINT_HOME=../oberon_interpreter_1_0_incubation_win32

MAKE=$(MAKE_$(OS))
	
os:

oberon_interpreter:	os
	CPATH=`pwd` && cd $(OINT_HOME) && $(MAKE) all && cd $(CPATH)

vkpbow_winapi: vkpbow_winapi.Mod

all:	os vkpbow_winapi

wake_vkpbow_winapi:	all
	$(OINT_HOME)/oberon_interpret.exe vkpbow_winapi.Mod

clean:	os
	cd ../oberon_interpreter_1_0_incubation_win32 && $(MAKE) clean
