#if !defined(ROD_SYMBOLTABLE_H)
#define ROD_SYMBOLTABLE_H
/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/
#include "Scanner.h"

namespace Oberon {

class Parser;
class Errors;

struct Module {  // object describing a declared name
	Parser::ModuleRecord *moduleAST;
	Module *next; // to next object in same scope //TODO reimplement as HashTable<wchar_t*,ModuleRecord*> name2moduleAST.
};

struct ModuleTable
{
	Errors *errors;
	Module *topScope;

	ModuleTable(Parser *parser);
	void Err(const wchar_t* msg);
	Module* NewModule(Parser::ModuleRecord &moduleAST);
	Parser::ModuleRecord* Find (wchar_t* name);
};

}; // namespace

#endif // !defined(ROD_SYMBOLTABLE_H)
