// micqlib.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "micqlib.h"

BOOL APIENTRY DllMain( HANDLE hModule, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved
					 )
{
    return TRUE;
}

extern "C" void initMicqlib0();

void DLL initMicqlib(){
  initMicqlib0();
}

extern "C" void tickMicqlib0();

void DLL tickMicqlib(){
  tickMicqlib0();
}

extern "C" void deinitMicqlib0();

void DLL deinitMicqlib(){
  deinitMicqlib0();
}

extern "C" BOOL quitMicqlib0();

BOOL DLL quitMicqlib(){
  return quitMicqlib0();
}