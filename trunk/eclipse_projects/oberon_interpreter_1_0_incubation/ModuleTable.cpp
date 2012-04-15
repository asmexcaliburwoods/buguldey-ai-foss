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

ModuleTable::ModuleTable(Errors* errors_): moduleName2parser(30),errors(errors_) {}

void ModuleTable::Err(const wchar_t* msg) {
	errors->Error(0, 0, msg);
}

#define wstrlen(a) wcslen(a)
#define wsprintf swprintf

Parser* ModuleTable::NewModule(Parser & moduleAST ) {
	std::pair<wchar_t*,Parser*> newmod(moduleAST.modulePtr->moduleName, &moduleAST);

	typedef TModuleTable::iterator hmit;
	typedef std::pair<hmit, bool> retcode;

	//insert unique
	retcode insertResult = moduleName2parser.insert(newmod);

	if(insertResult.second)return &moduleAST;

	//output an error
	const wchar_t *fmt=L"name declared twice: %ls";
	size_t len=wstrlen(fmt)-3+wstrlen(moduleAST.modulePtr->moduleName)+1;
	wchar_t *msg=new wchar_t[len];abortIfNull(msg);
	(void)wsprintf(msg, len, fmt, moduleAST.modulePtr->moduleName);
	Err(msg);
	delete[] msg;
	return 0;
}


// search the name in all open scopes and return its object node
Parser* ModuleTable::Find (const wchar_t* name) {
	typedef TModuleTable::const_iterator hmit;
/*
	typedef __gnu_cxx::_Hashtable_iterator<
				std::pair<
					const wchar_t* const,
					Oberon::Parser*
				>,
				const wchar_t*,
				__gnu_cxx::hash<const wchar_t*>,
				std::_Select1st<
					std::pair<
						const wchar_t* const, Oberon::Parser*
					>
				>,
				std::equal_to<const wchar_t*>,
				std::allocator<Oberon::Parser*>
			> findresulttype;
*/
	//typedef std::pair<hmit, Parser*> retcode;
	hmit result=moduleName2parser.find(name);
	if (result != moduleName2parser.end()) {
		return (*result).second;
	}
	return 0;
}

}; // namespace Oberon
