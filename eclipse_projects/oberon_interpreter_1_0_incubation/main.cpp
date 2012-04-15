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
#include "checks.h"

using namespace Oberon;

int main (const int argc, const char *argv[]) {
	if (argc >= 2) {
		wchar_t *fileName = coco_string_create(argv[1]);
		Scanner *scanner = new Scanner(fileName); abortIfNull(scanner);
		Parser *parser = new Parser(scanner); abortIfNull(parser);
		//parser->addParserListener(ParserListener)
		ModuleTable *modules = new ModuleTable(parser->errors); abortIfNull(modules);
		parser->modules=modules;
		Interpreter* interpreter = new Interpreter();abortIfNull(interpreter);
		parser->Parse();
		int errorsCount=parser->errors->count;
		if (errorsCount == 0) {
			wprintf(L"Read success! Interpreting...\n");

			const int oargc=argc-1;
			wchar_t **oargv=(wchar_t **)malloc(sizeof(wchar_t **)*(oargc+1));abortIfNull(oargv);
			int n=0;
			while(n<oargc){
				oargv[n]=coco_string_create(argv[n+1]);
				++n;
			}
			oargv[n]=0;

			interpreter->interpret(*parser,oargc,oargv);

			n=0;while(n<oargc){coco_string_delete(oargv[n]);++n;}
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
		wprintf(L"No module file name specified.\n");
		return 1;
	}

	return 0;
}
