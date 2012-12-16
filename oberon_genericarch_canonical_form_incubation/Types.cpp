#include <assert.h>
#include "Types.h"

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
