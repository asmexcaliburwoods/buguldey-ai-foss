/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/

#include "stdio.h"
#include "SymbolTable.h"
#include "Parser.h"
#include "Scanner.h"
#include <sys/timeb.h>
#include <wchar.h>

using namespace Oberon;

int main (const int argc, const char *argv[]) {
	if (argc == 2) {
		wchar_t *fileName = coco_string_create(argv[1]);
		Scanner *scanner = new Scanner(fileName);
		if(scanner==0){wprintf(L"No memory.\n");exit(1);}
		Parser *parser = new Parser(scanner);
		if(parser==0){wprintf(L"No memory.\n");exit(1);}
		//parser->addParserListener(ParserListener)
		parser->tab = new SymbolTable(parser);
		if(parser->tab==0){wprintf(L"No memory.\n");exit(1);}
		ModuleTable *modules = new ModuleTable(parser);
		if(modules==0){wprintf(L"No memory.\n");exit(1);}
		parser->gen = new CodeGenerator();
		if(parser->gen==0){wprintf(L"No memory.\n");exit(1);}
		wprintf(L"Reading %s...\n",argv[1]);
		parser->Parse();
		int errorsCount=parser->errors->count;
		if (errorsCount == 0) {
			wprintf(L"Read success! Generating code\n");
			parser->gen->GenerateCodeForModule(*(parser->modulePtr), *(parser->tab));
			parser->gen->Disassemble(parser);
			//parser->gen->Interpret();
		}else{
			wprintf(L"Read failed: %d errors.\n",errorsCount);
		}

		coco_string_delete(fileName);
		delete parser->gen;
		delete parser->tab;
		delete parser;
		delete scanner;
		if (errorsCount != 0) return 2;
	} else {
		wprintf(L"No source file name specified.\n");
		return 1;
	}

	return 0;
}
