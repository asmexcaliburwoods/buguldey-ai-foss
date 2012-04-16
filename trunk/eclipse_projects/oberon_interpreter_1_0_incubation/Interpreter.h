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
#include <list>
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
	struct TopImportsRecord{
		Parser* module;
		TopImportsRecord* topImports;
	};

	ModuleTable *modules;

	//moduleAlias can be null
	void processImport(Parser &module, const wchar_t * const moduleAlias, const wchar_t * const moduleName, TopImportsRecord *curModule){
		wprintf(L"IMPORT: %ls -> %ls\n",module.modulePtr->moduleName, moduleName);
		if(existErrorsOfCircularImports(moduleName,curModule))exit(1);
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
				interpreter->interpret(*parser,0,argv,curModule);
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
	void enumModuleImportEntry(Parser &module, Oberon::Parser::ModuleImportEntryRecord &import, TopImportsRecord *curModule){
		if(import.rhsPresent){
			wchar_t* moduleName=import.rhs;
			wchar_t* moduleAlias=import.lhs;
			processImport(module, moduleAlias,moduleName,curModule);

		}else{
			wchar_t* moduleName=import.lhs;
			processImport(module, 0, moduleName,curModule);
		}

	}
	void enumImports(Parser &module, Oberon::Parser::ImportListRecord *imports, TopImportsRecord *curModule){
		Oberon::Parser::ImportListRecord *currentRecord=imports;
		while(true){
			if(currentRecord==0)return;
			enumModuleImportEntry(module, currentRecord->moduleImportEntry,curModule);
			currentRecord=currentRecord->nullOrPtrToNextModuleImportEntriesList;
		}
	}
	void interpretModuleImports(Parser &module, TopImportsRecord *curModule) {
		Oberon::Parser::ModuleRecord * moduleAST=module.modulePtr;
		Oberon::ModuleTable *modules=module.modules;

		this->modules = modules;

		//indirectly imported into each module except for in itself
		static wchar_t moduleName_str[]=L"SYSTEM";
		wchar_t* moduleName=&(moduleName_str[0]);
		wchar_t* currModuleName=moduleAST->moduleName;
		if(!coco_string_equal(currModuleName, moduleName)){
			//wprintf(L"SYSTEM: %ls -> %ls\n",currModuleName,moduleName);
			processImport(module,0,moduleName,curModule);
		}

		Oberon::Parser::ImportListRecord *imports=moduleAST->importListPtr;
		enumImports(module, imports,curModule);
		wprintf(L"\n");
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

	/** @return true on error */
	bool existErrorsOfCircularImports(const wchar_t* modName,TopImportsRecord *curModule){
		TopImportsRecord *pChk=curModule;
		assert(pChk!=0);
		pChk=pChk->topImports;
		while(1){
			if(pChk==0)return false;//all ok
			const wchar_t* oldModName=pChk->module->modulePtr->moduleName;
			if(coco_string_equal(modName,oldModName)){
				//output an error
				std::list<wchar_t> buf;
				const wchar_t *prefix=L"circular module dependency: ";
				size_t len=wstrlen(prefix);
				for(int i=0;i<len;++i){buf.push_back(prefix[i]);}

				len=wstrlen(modName);
				for(int i=0;i<len;++i){buf.push_back(modName[i]);}

				const wchar_t *arrow=L" <- ";
				len=wstrlen(arrow);
				for(int i=0;i<len;++i){buf.push_back(arrow[i]);}

				TopImportsRecord *pPrint=curModule;
				while(1){
					if(pPrint==0)break;
					const wchar_t* oldModName=pPrint->module->modulePtr->moduleName;
					len=wstrlen(oldModName);
					for(int i=0;i<len;++i){buf.push_back(oldModName[i]);}

					if(coco_string_equal(modName,oldModName))break;

					pPrint=pPrint->topImports;

					if(pPrint!=0){
						len=wstrlen(arrow);
						for(int i=0;i<len;++i){buf.push_back(arrow[i]);}
					}
				}

				wchar_t *msg=new wchar_t(buf.size());abortIfNull(msg);
				typedef std::list<wchar_t>::iterator iter;
				int i=0;
				iter it=buf.begin();
				for(;it!=buf.end();){msg[i++]=*it++;}
				pChk->module->Err(msg);
				delete[] msg;
				return true;
			}
			pChk=pChk->topImports;
		}
	}
public:
	void interpret(Parser &module, int argc, wchar_t *argv[], TopImportsRecord *prevModule) {
		setCommandLine(argc, argv);
		TopImportsRecord curModule;
		curModule.module=&module;
		curModule.topImports=prevModule;
		interpretModuleImports(module,&curModule);
		interpretModuleDeclarations(module);
		interpretModuleStmtSeq(module);
	}
};

}; // namespace

#endif // !defined(INTERPRETER_H)
