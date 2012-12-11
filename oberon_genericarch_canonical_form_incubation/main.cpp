/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/

#include <stdio.h>
#include <wchar.h>

#include "common.h"
#include "SymbolTable.h"
#include "Parser.h"
#include "Scanner.h"
#include <sys/timeb.h>
#include "ModuleTable.h"

#include "interpreter.h"

using namespace Oberon;

int main (const int argc, const char *argv[]) {
	if (argc == 2) {
		wchar_t *fileName = coco_string_create(argv[1]);
		Errors *errors = new Errors(); abortIfNull(errors);
		ModuleTable *modules = new ModuleTable(errors);	abortIfNull(modules);
		int errorsCount=run(modules, fileName);
		if (errorsCount != 0) return 2;
		return 0;
	} else {
		wprintf(L"No source file name specified.\n");
		return 1;
	}
}
