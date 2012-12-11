#if !defined(OBERON_SYMBOLTABLE_H)
#define OBERON_SYMBOLTABLE_H
/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/
#include "Scanner.h"
#include "Parser.h"
#include <assert.h>

namespace Oberon {

class Parser;
class Errors;

struct Module {  // object describing a declared name
private:
	Parser::ModuleRecord *moduleAST;
	Module *next; // to next object in same scope //TODO reimplement as HashTable<wchar_t*,ModuleRecord*> name2moduleAST.
public:
	Module(Parser::ModuleRecord *moduleAST_):moduleAST(moduleAST_),next(0){}
	Parser::ModuleRecord *getAST(){return moduleAST;}
	void addNext(Module* m){
		assert(m!=0);
		this->next=m;
	}
	Module *getnext(){return next;}
};

struct ModuleTable
{
	Errors *errors;
	Module *topScope;

	ModuleTable(Errors *errors_);
	void Err(const wchar_t* msg);
	Module* NewModule(Parser::ModuleRecord &moduleAST);
	Module* Find (wchar_t* name);
};

}; // namespace

#endif // !defined(OBERON_SYMBOLTABLE_H)
