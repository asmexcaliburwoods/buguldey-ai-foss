#include <stdio.h>
#include <wchar.h>

#include "common.h"
#include "SymbolTable.h"
#include "Parser.h"
#include "Scanner.h"
#include <sys/timeb.h>
#include "ModuleTable.h"
#include "CodeGenerator.h"
/*
 * interpreter.cpp
 *
 *  Created on: 09.12.2012
 *      Author: egp
 */


using namespace Oberon;

int run(ModuleTable *modules, wchar_t* fileName){
    wprintf(L"Attempting to run %ls...\n",fileName);
	Scanner *scanner = new Scanner(fileName); abortIfNull(scanner);
	Parser *parser = new Parser(scanner, modules->errors); abortIfNull(parser);
	//parser->addParserListener(ParserListener)
	parser->tab = new SymbolTable(parser); abortIfNull(parser->tab);
	parser->gen = new CodeGenerator(modules);	abortIfNull(parser->gen);
	wprintf(L"Parsing %ls...\n",fileName);
	parser->Parse();
	wprintf(L"Adding to modcache...");
	modules->NewModule(*parser->modulePtr);
	wprintf(L"Added.\n");
	int errorsCount=parser->errors->count;
	if (errorsCount == 0) {
		wprintf(L"Parsed successfully! Interpreting %ls.\n", parser->modulePtr->moduleName);
		parser->gen->InterpretModule(parser->modulePtr, *(parser->tab));
		//parser->gen->Interpret();
	}else{
		wprintf(L"Parsing of %ls failed: %d errors.\n",fileName, errorsCount);
	}

	coco_string_delete(fileName);
	delete parser->gen;
	delete parser->tab;
	delete parser;
	delete scanner;

	return errorsCount;
}

