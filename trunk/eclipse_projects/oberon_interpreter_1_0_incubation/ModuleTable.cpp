/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/
#include "ModuleTable.h"
#include "Parser.h"
#include "stdio.h"
#include "checks.h"

namespace Oberon {

ModuleTable::ModuleTable(Errors* errors) {
	this->errors = errors;
	topScope=0;
}

void ModuleTable::Err(const wchar_t* msg) {
	errors->Error(0, 0, msg);
}

#define wstrlen(a) wcslen(a)
#define wsprintf swprintf

Module* ModuleTable::NewModule(Parser &moduleAST) {
	Module *p = topScope; Module *last = 0;
	while (p != 0) {
		if (coco_string_equal(p->moduleAST->modulePtr->moduleName, moduleAST.modulePtr->moduleName)){
			const wchar_t *fmt=L"name declared twice: %ls";
			size_t len=wstrlen(fmt)-3+wstrlen(moduleAST.modulePtr->moduleName)+1;
			wchar_t *msg=new wchar_t[len];abortIfNull(msg);
			(void)wsprintf(msg, len, fmt, moduleAST.modulePtr->moduleName);
			Err(msg);
			delete[] msg;
			return 0;
		}
		last = p; p = p->next;
	}
	Module *obj = new Module();abortIfNull(obj);
	obj->moduleAST=&moduleAST;
	obj->next=0;
	if (last == 0){last=topScope=obj;}
	last->next=obj;
	return obj;
}


// search the name in all open scopes and return its object node
Parser* ModuleTable::Find (const wchar_t* const name) {
	Module *obj=topScope;
	while (obj != 0) {  // for all objects in this scope
		if (coco_string_equal(obj->moduleAST->modulePtr->moduleName, name)) return obj->moduleAST;
		obj = obj->next;
	}
	return 0;
}

}; // namespace
