/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/

#include "stdio.h"
#include "Parser.h"
#include "Scanner.h"
#include <sys/timeb.h>
#include <wchar.h>
#include "Interpreter.h"

using namespace Oberon;

int main (const int argc, const char *argv[]) {
	if (argc == 2) {
		wchar_t *fileName = coco_string_create(argv[1]);
		Scanner *scanner = new Scanner(fileName);
		if(scanner==0){wprintf(L"No memory.\n");exit(1);}
		Parser *parser = new Parser(scanner);
		if(parser==0){wprintf(L"No memory.\n");exit(1);}
		//parser->addParserListener(ParserListener)
		ModuleTable *modules = new ModuleTable(parser->errors);
		if(modules==0){wprintf(L"No memory.\n");exit(1);}
		parser->modules=modules;
		Interpreter* interpreter = new Interpreter();
		if(interpreter==0){wprintf(L"No memory.\n");exit(1);}
		parser->Parse();
		int errorsCount=parser->errors->count;
		if (errorsCount == 0) {
			wprintf(L"Read success! Interpreting...\n");
			interpreter->interpret(*parser);
		}else{
			wprintf(L"Read failed: %d errors.\n",errorsCount);
		}

		coco_string_delete(fileName);
		delete interpreter;
		delete modules;
		delete parser;
		delete scanner;
		if (errorsCount != 0) return 2;
	} else {
		wprintf(L"No file name specified or extra parameters.\n");
		return 1;
	}

	return 0;
}
