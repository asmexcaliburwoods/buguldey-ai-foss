#include <stdio.h>
#include <wchar.h>

#include "Parser.h"
#include "Scanner.h"
#include <sys/timeb.h>
#include "common.h"
#include "interpreter.h"

int main (const int argc, const char *argv[]) {
	if (argc == 2) {
		wchar_t *fileName = coco_string_create(argv[1]);
		Errors *errors = new Errors(); abortIfNull(errors);
		int errorsCount=run(fileName, errors);
		if (errorsCount != 0) return 2;
		return 0;
	} else {
		wprintf(L"No source file name specified.\n");
		return 1;
	}
}
