/*
 * Types.h
 *
 *  Created on: 11.12.2012
 *      Author: egp
 */

#ifndef TYPES_H_
#define TYPES_H_

#include "assert.h"
#include "SymbolTable.h"

struct TypeRecord{
	virtual int getTypeNumber()=0;
	virtual ~TypeRecord(){};
	virtual void printToStdout(){
			wprintf(L"(type:%d)",getTypeNumber());}
};

static void PRINT_TYPE_AS_STRING(TypeRecord *tp){
	if (tp==0) wprintf(L"(TYPE:NULLPRTEXCEPTION)");
	else tp->printToStdout();
}



typedef wchar_t* identRec;

struct identRecord{
	wchar_t* ident_;
};

	class ValueMultipliedBySignedInt;

	struct Value{
		Value(){};
		virtual TypeRecord* getType(){return 0;};
		virtual ~Value(){};
		Value* multiply(signed int number);
	};

	struct ValueMultipliedBySignedInt: public Value{
	private:
		Value* v1;
		signed int number;
	public:
		virtual TypeRecord* getType(){return v1==0?0:v1->getType();}
		ValueMultipliedBySignedInt(Value* v1_, signed int number_):v1(v1_),number(number_){}
		virtual ~ValueMultipliedBySignedInt(){if(v1!=0)delete v1;}
	};

	struct ValueTBD: public Value{
		ValueTBD(){};
		virtual ~ValueTBD(){};
		virtual TypeRecord* getType(){return 0;}
	};

	class identRecord;

	struct ValueOfIdent: public Value{
	private:
		identRecord identRec;
	public:
		virtual TypeRecord* getType(){return 0;}//TODO determine type from symbol table
		ValueOfIdent(identRecord ident_):identRec(ident_){};
	};

	struct ValueOfIdentDotIdent: public Value{
	private:
		identRecord ident1;
		identRecord ident2;
	public:
		virtual TypeRecord* getType(){return 0;}//TODO determine type from symbol table
		ValueOfIdentDotIdent(identRecord ident1_, identRecord ident2_):ident1(ident1_), ident2(ident2_){};
	};

struct QualidentRecord{
	identRec leftIdent;
	identRec rightIdent; // rightIdent==0 if not specified
};

static const int
	 type_number_Qualident=1
	,type_number_ARRAY=2
	,type_number_RECORD=3
	,type_number_POINTER=4
	,type_number_PROCEDURE=5
	,type_number_MODULE=6
	;



	struct TypeQualident: public TypeRecord{
		int getTypeNumber(){return type_number_Qualident;}
		QualidentRecord qualident;
	};
	struct ValuePlaceholder{
		virtual int getValueType()=0;
		virtual ~ValuePlaceholder(){}
	};
	static const int ft_undef=0;
	static const int ft_DesignatorMaybeWithExprList=1;
	static const int ft_number=2;
	static const int ft_character=3;
	static const int ft_string=4;
	static const int ft_NIL=5;
	static const int ft_Set=6;
	static const int ft_Expr=7;
	static const int ft_tildeFactor=8;

	struct FactorRecord{
		virtual int getFactorType()=0; //ft_*
		virtual ~FactorRecord(){}
		virtual Value* calculate(Parser* parser, SymbolTable &tab)=0;
	};

	struct TermMulOpRecord{
		int mulOp;
		FactorRecord* factorPtr;
		TermMulOpRecord* nullOrNextTermMulOpRecord;
		Value* calculate(Value* v1, SymbolTable &tab){return new ValueTBD();}
	};

	/*
	Factor<FactorRecord *&factorPtr>
										(.
											FactorRecord_DesignatorMaybeWithExprList *fd;
											FactorRecord_number *fn;
											FactorRecord_character *fc;
											FactorRecord_string *fs;
											FactorRecord_Set *fset;
											FactorRecord_Expr *fexpr;
											FactorRecord_tildeFactor *ftf;
										.)
	=
										(. factorPtr=fd=new FactorRecord_DesignatorMaybeWithExprList(); abortIfNull(factorPtr); .)
	  DesignatorMaybeWithExprList<(*fd).r>
	|
										(. factorPtr=fn=new FactorRecord_number(); abortIfNull(factorPtr); .)
	  number<(*fn).num>
	|
										(. factorPtr=fc=new FactorRecord_character(); abortIfNull(factorPtr); .)
	  Character<(*fc).ch>
	|
										(. factorPtr=fs=new FactorRecord_string(); abortIfNull(factorPtr); .)
	  String<(*fs).s>
	|
										(. factorPtr=new FactorRecord_NIL(); abortIfNull(factorPtr); .)
	  "NIL"
	|
										(. factorPtr=fset=new FactorRecord_Set(); abortIfNull(factorPtr); .)
	  Set<(*fset).set>
	|
										(. factorPtr=fexpr=new FactorRecord_Expr(); abortIfNull(factorPtr); .)
	  "(" Expr<(*fexpr).expr> ")"
	|
										(. factorPtr=ftf=new FactorRecord_tildeFactor(); abortIfNull(factorPtr); .)
										(. ftf->factorPtr=0; .)
	  "~" Factor<(*ftf).factorPtr>
	.
	TermMulOpClause<TermMulOpRecord &r>
	=
		MulOp<r.mulOp>
							(. r.factorPtr=0; .)
		Factor<r.factorPtr>
		(			(. r.nullOrNextTermMulOpRecord=0; .)
	    |  			(. r.nullOrNextTermMulOpRecord=new TermMulOpRecord(); abortIfNull(r.nullOrNextTermMulOpRecord); .)
	    			TermMulOpClause<*r.nullOrNextTermMulOpRecord>
	    ).
	Term<TermRecord &t>
	=				(. t.factorPtr=0; .)
		Factor<t.factorPtr>

		(			(. t.nullOrNextTermMulOpRecord=0; .)
	    |  			(. t.nullOrNextTermMulOpRecord=new TermMulOpRecord(); abortIfNull(t.nullOrNextTermMulOpRecord); .)
	  			TermMulOpClause<*t.nullOrNextTermMulOpRecord>
	    ).
	*/
		struct TermRecord{
			FactorRecord* factorPtr;
			TermMulOpRecord* nullOrNextTermMulOpRecord;
			Value* calculate(Parser *parser, SymbolTable& tab);
		};
		struct SimpleExprAddOpRecord{
			int addOp;
		 	TermRecord term;
		 	SimpleExprAddOpRecord* nullOrNextSimpleExprAddOpRecord;
		 	Value* calculate(SymbolTable &tab, Value* v1){return new ValueTBD();}
		};
	/*
	AddOp<int &op>
	=                       (. op = illegal_operator; .)
	( '+'	                (. op = plus; .)
	| '-'        			(. op = minus; .)
	| "OR"		 			(. op = orOperation; .)
	).
	SimpleExprAddOpClause<SimpleExprAddOpRecord &r>
	=
		AddOp<r.addOp> Term<r.term>
		(			(. r.nullOrNextSimpleExprAddOpRecord=0; .)
	    |  			(. r.nullOrNextSimpleExprAddOpRecord=new SimpleExprAddOpRecord(); abortIfNull(r.nullOrNextSimpleExprAddOpRecord); .)
	    			SimpleExprAddOpClause<*r.nullOrNextSimpleExprAddOpRecord>
	    ).
	SimpleExpr<SimpleExprRecord &e>
	= (	(|"+") 		(. e.minus=false; .)
	  | "-"			(. e.minus=true; .)
	  )
	  Term<e.term>
	  (				(. e.nullOrNextSimpleExprAddOpRecord=0; .)
	  |  			(. e.nullOrNextSimpleExprAddOpRecord=new SimpleExprAddOpRecord(); abortIfNull(e.nullOrNextSimpleExprAddOpRecord); .)
	  			SimpleExprAddOpClause<*e.nullOrNextSimpleExprAddOpRecord>
	  ).
	*/	struct SimpleExprRecord{
			bool signum;
			bool minus;
			TermRecord term;
		 	SimpleExprAddOpRecord* nullOrNextSimpleExprAddOpRecord;
			virtual Value* calculate(Parser *parser, SymbolTable &tab){
				signed int sgn = minus?-1:+1;
				Value* v1 = term.calculate(parser, tab);
				Value* v1a = v1==0?0:!signum?v1:v1->multiply(sgn);
			    if(nullOrNextSimpleExprAddOpRecord!=0){
			      v1a = nullOrNextSimpleExprAddOpRecord->calculate(tab, v1a);
			    }
			    return v1a;
			}
			virtual ~SimpleExprRecord(){}
		};
	struct ExprRecord{
		SimpleExprRecord lhs;
		bool opAndRhsPresent;
		int op;
//Relation<int &op>
//=					(. op = illegal_operator; .)
//( "="				(. op = equals; .)
//| "#" 				(. op = notEquals; .)
//| "<"				(. op = less; .)
//| "<=" 				(. op = lessOrEqual; .)
//| ">"				(. op = greater; .)
//| ">=" 				(. op = greaterOrEqual; .)
//| "IN" 				(. op = in; .)
//| "IS" 				(. op = is; .)

		SimpleExprRecord rhs;
		virtual Value* calculate(Parser *parser, SymbolTable &tab){
			if(opAndRhsPresent){
			   wprintf(L"BINARY_EXPR ");
			   assert(0);
			}else{
				return lhs.calculate(parser, tab);
			}
		}
		virtual ~ExprRecord(){}
	};

	struct ConstExprRecord{
		bool valueHasBeenCalculated;
		ValuePlaceholder *constValuePtr;
		ExprRecord expr;
	};
	struct TypeArrayConstExprListMandatoryRecord{
		ConstExprRecord dimensionConstExpr;
		TypeArrayConstExprListMandatoryRecord *next;
	};
	struct TypeARRAY: public TypeRecord{
		int getTypeNumber(){return type_number_ARRAY;}
		TypeArrayConstExprListMandatoryRecord *dimensionsConstExprsListPtr;
		TypeRecord *arrayElementTypePtr;
	};
	struct IdentDefRecord{
		identRec ident_;
		int modifier;
	};

	struct IdentListRecord{
		IdentDefRecord identDef;
		IdentListRecord* nullOrCommaIdentList;
	};
	struct FieldListRecord{
		bool fieldsPresent;
		IdentListRecord identList;
		TypeRecord *typePtr;
	};

	struct MandatoryFieldsListRecord{
		FieldListRecord recordFieldsList;
		MandatoryFieldsListRecord *next;
	};

	struct TypeRECORD: public TypeRecord{
		int getTypeNumber(){return type_number_RECORD;}
		QualidentRecord *optionalQualidentPtr;
		MandatoryFieldsListRecord fieldsList;
	};
	struct TypePOINTER: public TypeRecord{
		int getTypeNumber(){return type_number_POINTER;}
		TypeRecord *pointedTypePtr;
	};
	struct IdentList2Record{
		identRec ident_;
		IdentList2Record* nullOrCommaIdentList;
	};

	class TypeMODULE : public TypeRecord{
	public:
		TypeMODULE(){}
		virtual int getTypeNumber(){return type_number_MODULE;}
	};



#endif /* TYPES_H_ */
