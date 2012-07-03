/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/
#include "common.h"
#include "ModuleTable.h"
#include "Parser.h"
#include "CodeGenerator.h"
#include <stdio.h>
#include <wchar.h>

//for wstrlen
//#include <synce.h>

//for wsprintf
//#include <widec.h>

int wstrlen(const wchar_t* p){
	int len = 0;
	while(*p++)len++;
	return len;
}

wchar_t* wstrconcat(const wchar_t* a, int alenchars, const wchar_t* b, const int blenchars){
	wchar_t *buf = (wchar_t*)malloc((alenchars+blenchars+1)*sizeof(wchar_t));
	abortIfNull(buf);
	int len = 0;
	while(*a){buf[len++]=*a++;}
	while(*b){buf[len++]=*b++;}
	buf[len]=(wchar_t)0;
	return buf;
}

namespace Oberon {

ModuleTable::ModuleTable(Parser *parser) {
	errors = parser->errors;
	topScope=0;
}

void ModuleTable::Err(const wchar_t* msg) {
	errors->Error(0, 0, msg);
}

Module* ModuleTable::NewModule(ModuleRecord &moduleAST) {
	Module *p = topScope; Module *last = 0;
	while (p != 0) {
		if (coco_string_equal(p->moduleAST->moduleName, moduleAST.moduleName)){
			const wchar_t *a=L"name declared twice: ";
			int len_a=wstrlen(a);
			wchar_t* b = moduleAST.moduleName;
			int len_b = wstrlen(b);
			wchar_t* msg = wstrconcat(a, len_a, b, len_b);
			Err(msg);
			free(msg);
			return 0;
		}
		last = p; p = p->next;
	}
	Module *obj = new Module();
	abortIfNull(obj);
	obj->moduleAST=&moduleAST;
	obj->next=0;
	if (last == 0){last=topScope=obj;}
	last->next=obj;
	return obj;
}


// search the name in all open scopes and return its object node
Module* ModuleTable::Find (wchar_t* name) {
	Module *obj=topScope;
	while (obj != 0) {  // for all objects in this scope
		if (coco_string_equal(obj->moduleAST->moduleName, name)) return obj;
		obj = obj->next;
	}
	return 0;
}

}; // namespace
