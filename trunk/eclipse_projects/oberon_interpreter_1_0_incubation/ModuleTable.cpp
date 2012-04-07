/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/
#include "ModuleTable.h"
#include "Parser.h"

namespace Oberon {

ModuleTable::ModuleTable(Parser *parser) {
	errors = parser->errors;
	topScope=0;
}

void ModuleTable::Err(const wchar_t* msg) {
	errors->Error(0, 0, msg);
}

ModuleTable::Module* ModuleTable::NewModule(Parser::ModuleRecord &moduleAST) {
	Module *p = topScope; Module *last = 0;
	while (p != 0) {
		if (coco_string_equal(p->moduleAST->moduleName, moduleAST.moduleName)){
			const wchar_t *fmt=L"name declared twice: %ls";
			int len=wstrlen(fmt)-3+wstrlen(moduleAST.moduleName)+1;
			wchar_t *msg=new wchar_t[len];
			wsprintf(msg, fmt, moduleAST.moduleName);
			Err(msg);
			delete[] msg;
			return 0;
		}
		last = p; p = p->next;
	}
	Module *obj = new Module();
	if(obj==0){
		wprintf(L"No memory.\n");
		exit(1);
	}
	obj->moduleAST=&moduleAST;
	obj->next=0;
	if (last == 0){last=topScope=obj;}
	last->next=obj;
	return obj;
}


// search the name in all open scopes and return its object node
ModuleTable::Module* ModuleTable::Find (wchar_t* name) {
	Module *obj=topScope;
	while (obj != 0) {  // for all objects in this scope
		if (coco_string_equal(obj->moduleAST->moduleName, name)) return obj;
		obj = obj->next;
	}
	return 0;
}

}; // namespace
