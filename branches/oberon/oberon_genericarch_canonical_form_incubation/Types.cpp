#include <assert.h>
#include "Types.h"
#include "Parser.h"

Value* TermRecord::calculate(Parser *parser, SymbolTable& tab){
	assert(factorPtr!=0);
	Value* v1 = factorPtr->calculate(parser, tab);
	if(nullOrNextTermMulOpRecord!=0){
		v1 = nullOrNextTermMulOpRecord->calculate(v1, tab);
	}
	return v1;
};

Value* Value::multiply(signed int number){
	return new ValueMultipliedBySignedInt(this, number);
}

void ValueNumber::printToStdout(){
	const wchar_t* numtypestr=L"UNKNOWN_NUM";
	if(num->numtype==num_int)numtypestr=L"INT_NUM";
	else
		if(num->numtype==num_real)numtypestr=L"REAL_NUM";
	wprintf(L"(value number %ls; type:%ls)",num->tokenString, numtypestr);
}
