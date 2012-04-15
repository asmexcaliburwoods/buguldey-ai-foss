#if !defined(INTERPRETER_H)
#define INTERPRETER_H

#include "wchar.h"
#include "strings.h"
#include "assert.h"

/*
Oberon2 interpreter
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/

#include "Parser.h"
#include "Scanner.h"
#include <stdlib.h>
#include <stdio.h>
#include "checks.h"

namespace Oberon {

class Interpreter
{
public:

	Interpreter() {
		argc=0;
	}

	~Interpreter() {
	}

private:

	ModuleTable *modules;

	//moduleAlias can be null
	void processImport(Parser &module, const wchar_t * const moduleAlias, const wchar_t * const moduleName){
		wprintf(L"  %ls -> %ls\n",module.modulePtr->moduleName, moduleName);
		Oberon::Parser *parser=modules->Find(moduleName);
		if(parser==0){
			const wchar_t *fmt=L"%ls%ls";
			size_t len=wcslen(fmt)-3-3+wcslen(moduleName)+4+1;
			wchar_t *msg=new wchar_t[len];abortIfNull(msg);
			(void)swprintf(msg, len, fmt, moduleName, L".Mod");
			wchar_t *fileName=msg;
			Scanner *scanner = new Scanner(fileName);
			abortIfNull(scanner);
			parser = new Parser(scanner);
			abortIfNull(parser);
			parser->modules=this->modules;
			Interpreter* interpreter = this;
			parser->Parse();
			int errorsCount=parser->errors->count;
			if (errorsCount == 0) {
				modules->NewModule(*parser);
			}
			errorsCount=parser->errors->count;
			if (errorsCount == 0) {
				wprintf(L"Read success! Interpreting...\n");
				wchar_t**argv=(wchar_t**)malloc(sizeof(wchar_t**)); abortIfNull(argv);
				*argv=0;
				interpreter->interpret(*parser,0,argv);
				delete[] argv;
			}else{
				wprintf(L"Read failed: %d errors.\n",errorsCount);
				exit(1);
			}

			delete[] fileName;
			delete scanner;
		}

		assert(parser!=0);
		module.modulePtr->addImportedModuleAlias(moduleName, parser);
		if(moduleAlias!=0)module.modulePtr->addImportedModuleAlias(moduleAlias, parser);
	}
	void enumModuleImportEntry(Parser &module, Oberon::Parser::ModuleImportEntryRecord &import){
		if(import.rhsPresent){
			wchar_t* moduleName=import.rhs;
			wchar_t* moduleAlias=import.lhs;
			processImport(module, moduleAlias,moduleName);

		}else{
			wchar_t* moduleName=import.lhs;
			processImport(module, 0, moduleName);
		}

	}
	void enumImports(Parser &module, Oberon::Parser::ImportListRecord *imports){
		Oberon::Parser::ImportListRecord *currentRecord=imports;
		while(true){
			if(currentRecord==0)return;
			enumModuleImportEntry(module, currentRecord->moduleImportEntry);
			currentRecord=currentRecord->nullOrPtrToNextModuleImportEntriesList;
		}
	}
	void interpretModuleImports(Parser &module) {
		Oberon::Parser::ModuleRecord * moduleAST=module.modulePtr;
		Oberon::ModuleTable *modules=module.modules;

		this->modules = modules;

		//indirectly imported into each module except for in itself
		static wchar_t moduleName_str[]=L"SYSTEM";
		wchar_t* moduleName=&(moduleName_str[0]);
		wchar_t* currModuleName=moduleAST->moduleName;
		if(!coco_string_equal(currModuleName, moduleName)){
			wprintf(L"SYSTEM: %ls -> %ls\n",currModuleName,moduleName);
			processImport(module,0,moduleName);
		}

		Oberon::Parser::ImportListRecord *imports=moduleAST->importListPtr;
		enumImports(module, imports);
		wprintf(L"\n");
		wprintf(L"%ls NOT TESTED: Interpreter::interpretModuleImports\n",module.modulePtr->moduleName);
	}

	void interpretModuleDeclarations(Parser &module) {
		//module.modulePtr->declSeq
		wprintf(L"%ls NOT IMPL: Interpreter::interpretModuleDeclarations\n",module.modulePtr->moduleName);
	}

	void interpretModuleStmtSeq(Parser &module) {
		Parser::StatementSeqRecord *seqPtr=module.modulePtr->stmtSeqPtr;
		while(1){
			if(seqPtr==0)return;
			Parser::StatementRecord *stmt=seqPtr->statementPtr;
			if(stmt==0){wprintf(L"VERIFICATION ERROR: Null statement\n");exit(1);}
			stmt->interpret();
			seqPtr=seqPtr->nullOrPtrToNextStatementSeq;
		}
	}

	int argc;
	wchar_t **argv;

	void setCommandLine(int argc, wchar_t **argv){
		this->argc=argc;
		this->argv=argv;
	}

public:
	void interpret(Parser &module, int argc, wchar_t *argv[]) {
		setCommandLine(argc, argv);
		interpretModuleImports(module);
		interpretModuleDeclarations(module);
		interpretModuleStmtSeq(module);
	}
};

}; // namespace

#endif // !defined(INTERPRETER_H)
