#if !defined(ROD_SYMBOLTABLE_H)
#define ROD_SYMBOLTABLE_H
/*
Oberon2 compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/
#include "Scanner.h"
#include <hash_map>

//for hash(wchar)
#include <cstddef>

_GLIBCXX_BEGIN_NAMESPACE(__gnu_cxx)

  using std::size_t;

	inline size_t
	hash_wstring(const wchar_t* __s)
	{
	  unsigned long __h = 0;
	  for ( ; *__s; ++__s)
		__h = 5 * __h + *__s;
	  return size_t(__h);
	}

  template<>
    struct hash<wchar_t*>
    {
      size_t
      operator()(const wchar_t* __s) const
      { return hash_wstring(__s); }
    };

  template<>
    struct hash<const wchar_t*>
    {
      size_t
      operator()(const wchar_t* __s) const
      { return hash_wstring(__s); }
    };

_GLIBCXX_END_NAMESPACE


namespace Oberon {

class Parser;
class Errors;

struct ModuleTable
{
	Errors *errors;

private:
	typedef __gnu_cxx::hash_map<const wchar_t*,Parser*> TModuleTable;
	TModuleTable moduleName2parser;

public:
	ModuleTable(Errors* errors);
	void Err(const wchar_t* msg);
	Parser* NewModule(Parser &moduleAST);
	Parser* Find (const wchar_t* name);
};

}; // namespace

#endif // !defined(ROD_SYMBOLTABLE_H)
