#ifdef MICQLIB_EXPORTS
#define DLL __declspec( dllexport ) 
#else 
#define DLL __declspec( dllimport ) 
#endif 

void DLL initMicqlib();

void DLL tickMicqlib();

void DLL deinitMicqlib();

BOOL DLL quitMicqlib();
