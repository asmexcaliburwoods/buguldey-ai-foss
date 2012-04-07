#include "wchar.h"
#include "stdio.h"
#include "stdlib.h"

void abortIfNull(void* ptr){
	if(ptr==0){
		wprintf(L"No memory.\n");
		exit(2);
	}
}
