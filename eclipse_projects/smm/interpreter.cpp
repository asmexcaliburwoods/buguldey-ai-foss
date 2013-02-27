#include <stdio.h>
#include <wchar.h>

#include "common.h"
#include "Parser.h"
#include "Scanner.h"
#include <sys/timeb.h>

int run(wchar_t* fileName, Errors* errors_){
    wprintf(L"Attempting to run %ls...\n",fileName);
	Scanner *scanner = new Scanner(fileName); abortIfNull(scanner);
	Parser *parser = new Parser(scanner, errors_); abortIfNull(parser);
	int errorsCount2=errors_->count;
	wprintf(L"Parsing %ls...\n",fileName);
	parser->Parse();
	int errorsCount=errors_->count;
	if (errorsCount == 0) {
		wprintf(L"Parsed successfully! Interpreting %ls.\n", fileName);
		errorsCount = 0;
	}else{
		wprintf(L"Parsing of %ls failed: %d errors.\n",fileName, errorsCount);
	}

/*	coco_string_delete(fileName);
	delete parser->gen;
	delete parser->tab;
	delete parser;
	delete scanner;
*/
	return errorsCount;
}

