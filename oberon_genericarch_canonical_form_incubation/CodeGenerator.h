#ifndef OBERON_ZH_CODEGENERATOR_H
#define OBERON_ZH_CODEGENERATOR_H

/*
Oberon ZH compiler for x86
Copyright (c) 2012 Evgeniy Grigorievitch Philippov
Distributed under the terms of GNU General Public License, v.3 or later
*/

#include <stdlib.h>
#include <stdio.h>
#include <wchar.h>

#include "common.h"
#include "SymbolTable.h"

namespace Oberon {

class Parser;

	typedef bool boolean;

	enum OperatorsEnum { 
	  illegal_operator, plus, minus, times, slash, equals, less, greater,
	  orOperation, notEquals, lessOrEqual, greaterOrEqual, in, is, divOp, 
	  modOp, ampersand
	};

	enum ObjectKindsEnum {
	  var, proc
	};

	enum OpcodesEnum { 
	  ADD,  SUB,  MUL,  DIV,   EQU,  LSS, GTR, NEG,
	  LOAD, LOADG, STO,   STOG,  CONST,
	  CALL, RET,   
	  ENTER, LEAVE,
	  JMP,  FJMP,  READ,  WRITE
	};

	typedef wchar_t* characterRecord; 
	typedef wchar_t* stringRecord; 
	typedef wchar_t* identRec; 

	struct QualidentRecord{
		identRec leftIdent;
		identRec rightIdent; /* rightIdent==0 if not specified */
	};

	enum NumTypeEnum {num_int, num_real};
	
	struct numberRecord{
		NumTypeEnum numtype;
		wchar_t* tokenString;
	};
	  
	struct identRecord{
		wchar_t* ident_;
	};
	
	enum FactorTypeEnum {
		ft_undef,
		ft_DesignatorMaybeWithExprList,
		ft_number,
		ft_character,
		ft_string,
		ft_NIL,
		ft_Set,
		ft_Expr,
		ft_tildeFactor
	};

	struct FactorRecord{
		virtual FactorTypeEnum getFactorType()=0; //ft_*
	};
	
	typedef struct TermMulOpRecord{
		int mulOp;
		FactorRecord* factorPtr;
		TermMulOpRecord* nullOrNextTermMulOpRecord;
	} *PtrToTermMulOpRecord;

	typedef struct TermRecord{
		FactorRecord* factorPtr;
		TermMulOpRecord* nullOrNextTermMulOpRecord;
	} *PtrToTermRecord;

	typedef struct SimpleExprAddOpRecord{
		int addOp;
	 	TermRecord term;
	 	SimpleExprAddOpRecord* nullOrNextSimpleExprAddOpRecord;
	} *PtrToSimpleExprAddOpRecord;
	
	struct SimpleExprRecord{
		boolean minus;
		TermRecord term;
	 	SimpleExprAddOpRecord* nullOrNextSimpleExprAddOpRecord;
	};
		  
	struct ExprRecord{
		SimpleExprRecord lhs;
		boolean opAndRhsPresent;
		int op;
		SimpleExprRecord rhs;
	};

	struct ElementRangeRecord{
		ExprRecord expr1;
		boolean isrange;
		ExprRecord expr2;
	};
	
	struct SetRecord{
		boolean emptySet;
		ElementRangeRecord range;
		SetRecord* nullOrPtrToNextSet;
	};

	struct ExprListRecord{
		ExprRecord expr;
		ExprListRecord *nullOrCommaExprList;
	};

	struct QualidentOrOptionalExprListRecord{
		boolean exprListPresent;
		ExprListRecord exprList;
	};

	struct FactorRecord_Expr: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_Expr;}
		ExprRecord expr;
	};

	enum ClauseEnum {cl1,cl2,cl3,cl4};
	
	struct DesignatorMaybeWithExprListRepeatingPartRecord{
		virtual ClauseEnum getClauseNumber()=0;
		DesignatorMaybeWithExprListRepeatingPartRecord* nullOrPtrToNextDesignatorMaybeWithExprListRepeatingPartRecord;
/*
	("." ident 			//clauseNumber==1
	| "[" ExprList "]" 	//clauseNumber==2
	| "^" 				//clauseNumber==3
	| "(" QualidentOrOptionalExprList ")" //clauseNumber==4
	)
*/
	};

	struct DesignatorMaybeWithExprListRepeatingPartRecordCL1 : public DesignatorMaybeWithExprListRepeatingPartRecord{
		virtual ClauseEnum getClauseNumber() {return cl1;}
		identRec clause1_identRec;
		//"." ident
	};
	struct DesignatorMaybeWithExprListRepeatingPartRecordCL2 : public DesignatorMaybeWithExprListRepeatingPartRecord{
		virtual ClauseEnum getClauseNumber() {return cl2;}
		ExprListRecord clause2_exprList;
		//"[" ExprList "]"
	};
	struct DesignatorMaybeWithExprListRepeatingPartRecordCL3 : public DesignatorMaybeWithExprListRepeatingPartRecord{
		virtual ClauseEnum getClauseNumber() {return cl3;}
	    //"^"
	};
	struct DesignatorMaybeWithExprListRepeatingPartRecordCL4 : public DesignatorMaybeWithExprListRepeatingPartRecord{
		virtual ClauseEnum getClauseNumber() {return cl4;}
		QualidentOrOptionalExprListRecord clause4_qualidentOrOptionalExprList;
		//"(" QualidentOrOptionalExprList ")"
	};

	struct DesignatorMaybeWithExprListRecord{
		identRecord identRec;
		DesignatorMaybeWithExprListRepeatingPartRecord* nullOrPtrToNextDesignatorMaybeWithExprListRepeatingPartRecord;
	};


	struct FactorRecord_DesignatorMaybeWithExprList: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_DesignatorMaybeWithExprList;}
		DesignatorMaybeWithExprListRecord r; 
	};
  
	struct FactorRecord_number: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_number;}
		numberRecord num; 
	};
  
	struct FactorRecord_character: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_character;}
		characterRecord ch; 
	};
  
	struct FactorRecord_string: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_string;}
		stringRecord s; 
	};
  
	struct FactorRecord_NIL: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_NIL;}
	};

	struct FactorRecord_Set: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_Set;}
		SetRecord set;
	};
 
	struct FactorRecord_tildeFactor: public FactorRecord{
		virtual FactorTypeEnum getFactorType(){return ft_tildeFactor;}
		FactorRecord* factorPtr;
	};

	enum ModifierEnum {modifier_none, modifier_star, modifier_minus};
	
	struct IdentDefRecord{
		identRec ident_;
		ModifierEnum modifier;
	};

	struct IdentListRecord{
		IdentDefRecord identDef;
		IdentListRecord* nullOrCommaIdentList;
	};
	struct IdentList2Record{
		identRec ident_;
		IdentList2Record* nullOrCommaIdentList;
	};

	enum StmtTypeNumberEnum {
		 stmtTypeNumber_EmptyStmt
		,stmtTypeNumber_EXPR_OR_ASSIGN
		,stmtTypeNumber_IF
		,stmtTypeNumber_CASE
		,stmtTypeNumber_WHILE
		,stmtTypeNumber_REPEAT
		,stmtTypeNumber_FOR
		,stmtTypeNumber_LOOP
		,stmtTypeNumber_WITH
		,stmtTypeNumber_EXIT
		,stmtTypeNumber_RETURN
	};
	
	struct StatementRecord{
		virtual StmtTypeNumberEnum getStatementTypeNumber()=0; 
	};

	struct StatementSeqRecord{
		StatementRecord *statementPtr;
		StatementSeqRecord* nullOrPtrToNextStatementSeq;
	};

	enum TypeNumberQARPPEnum { 
		 type_number_Qualident
		,type_number_ARRAY
		,type_number_RECORD
		,type_number_POINTER
		,type_number_PROCEDURE
	};
	
	struct TypeRecord{
		virtual TypeNumberQARPPEnum getTypeNumber()=0;
	};

	struct VarDeclRecord{
		IdentListRecord identList;
		TypeRecord *typePtr;
	};
	
	struct ValuePlaceholder{
		virtual int getValueType()=0;
	};
	
	struct ConstExprRecord{
		boolean valueHasBeenCalculated;
		ValuePlaceholder *constValuePtr;
		ExprRecord expr;
	};
	
	struct ConstDeclRecord{
		IdentDefRecord identDef;
		ConstExprRecord expr;
	};

	struct TypeDeclRecord{
		IdentDefRecord identDef;
		TypeRecord *typePtr;
	};
	
	struct ModuleImportEntryRecord{
		identRec lhs, rhs;
		boolean rhsPresent;
	};

	struct ImportListRecord{
		ModuleImportEntryRecord moduleImportEntry;
		ImportListRecord *nullOrPtrToNextModuleImportEntriesList;
	};

	struct ReceiverRecord{
		bool varSpecified;
		identRec leftIdent;
		identRec rightIdent;
	};
	
	struct OptionalReceiverRecord{
		bool receiverSpecified;
		ReceiverRecord receiver;
	};

	struct FPSectionRecord{
		bool var;
		IdentList2Record identList;
		TypeRecord *typePtr;
	};
	struct FPSectionsListMandatoryRecord{
		FPSectionRecord fpSection;
		FPSectionsListMandatoryRecord *next;
	};
	struct FormalParsRecord{
		FPSectionsListMandatoryRecord *optionalFPSectionsListPtr;
		QualidentRecord *optionalQualidentPtr;
	}; 

	struct OptionalFormalParsRecord{
		bool formalParsSpecified;
		FormalParsRecord formalPars;
	};

	struct ForwardDeclRecord{
		OptionalReceiverRecord optionalReceiver;
		IdentDefRecord identDef;
		OptionalFormalParsRecord optionalFormalPars;
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

	struct DeclSeqConstDeclListMandatoryRecord{
		ConstDeclRecord constDecl;
		DeclSeqConstDeclListMandatoryRecord *nullOrPtrToNextDeclSeqConstDeclListMandatory;
	};
	struct DeclSeqTypeDeclListMandatoryRecord{
		TypeDeclRecord typeDecl;
		DeclSeqTypeDeclListMandatoryRecord *nullOrPtrToNextDeclSeqTypeDeclListMandatory;
	};
	struct DeclSeqVarDeclListMandatoryRecord{
		VarDeclRecord varDecl;
		DeclSeqVarDeclListMandatoryRecord *nullOrPtrToNextDeclSeqVarDeclListMandatory;
	};
	
	struct DeclSeqConstDeclListRecord{
		bool specified;
		DeclSeqConstDeclListMandatoryRecord constDecls; /* undefined if specified==false*/
	};
	struct DeclSeqTypeDeclListRecord{
		bool specified;
		DeclSeqTypeDeclListMandatoryRecord typeDecls; /* undefined if specified==false*/
	};
	struct DeclSeqVarDeclListRecord{
		bool specified;
		DeclSeqVarDeclListMandatoryRecord varDecls; /* undefined if specified==false*/
	};

	enum DeclEnum {decl_const,decl_type,decl_var};
	
	struct DeclSeqConstTypeVarListMandatoryRecord{
		virtual DeclEnum get_decl_variant()=0;
		DeclSeqConstTypeVarListMandatoryRecord *next;
	}; 
	
	struct DeclSeqConst : public DeclSeqConstTypeVarListMandatoryRecord{
		virtual DeclEnum get_decl_variant() {return decl_const;}
		DeclSeqConstDeclListRecord constDeclList; 
	}; 

	struct DeclSeqType : public DeclSeqConstTypeVarListMandatoryRecord{
		virtual DeclEnum get_decl_variant() {return decl_type;}
		DeclSeqTypeDeclListRecord typeDeclList;
	}; 

	struct DeclSeqVar : public DeclSeqConstTypeVarListMandatoryRecord{
		virtual DeclEnum get_decl_variant() {return decl_var;}
		DeclSeqVarDeclListRecord varDeclList;
	}; 

	struct DeclSeqConstTypeVarListRecord{
		bool specified;
		DeclSeqConstTypeVarListMandatoryRecord *constTypeVarListPtr; /* undefined if specified==false */
	};

	struct DeclSeqRecord;
	
	struct ProcDeclRecord{
		OptionalReceiverRecord optionalReceiver;
		IdentDefRecord identDef;
		OptionalFormalParsRecord optionalFormalPars;
		DeclSeqRecord *declSeqPtr; 
	    bool procBodySpecifiedHere;
	    StatementSeqRecord procBodyStmtSeq; /* undefined if procBodySpecifiedHere==false */
	};

	enum DeclProcFwd {decl_proc,decl_fwd};
	
	struct DeclSeqProcDeclFwdDeclListMandatoryRecord{
		virtual DeclProcFwd get_decl_variant()=0;
		DeclSeqProcDeclFwdDeclListMandatoryRecord *next;
	}; 
	struct DeclSeqProcDecl : public DeclSeqProcDeclFwdDeclListMandatoryRecord{
		virtual DeclProcFwd get_decl_variant(){return decl_proc;}
		ProcDeclRecord procDecl; 
	}; 
	struct DeclSeqFwdDecl : public DeclSeqProcDeclFwdDeclListMandatoryRecord{
		virtual DeclProcFwd get_decl_variant(){return decl_fwd;}
		ForwardDeclRecord fwdDecl;
	}; 
	
	struct DeclSeqProcDeclFwdDeclListRecord{
		bool specified;
		DeclSeqProcDeclFwdDeclListMandatoryRecord *procDeclFwdDeclListPtr; /* undefined if specified==false */
	};
	
	struct DeclSeqRecord{
		DeclSeqConstTypeVarListRecord ctvList;
		DeclSeqProcDeclFwdDeclListRecord pfList;
	};

	struct TypeQualident: public TypeRecord{
		TypeNumberQARPPEnum getTypeNumber(){return type_number_Qualident;}
		QualidentRecord qualident;
	}; 
	struct TypeArrayConstExprListMandatoryRecord{
		ConstExprRecord dimensionConstExpr;
		TypeArrayConstExprListMandatoryRecord *next;
	};
	struct TypeARRAY: public TypeRecord{
		TypeNumberQARPPEnum getTypeNumber(){return type_number_ARRAY;}
		TypeArrayConstExprListMandatoryRecord *dimensionsConstExprsListPtr;
		TypeRecord *arrayElementTypePtr;
	}; 
	struct TypeRECORD: public TypeRecord{
		TypeNumberQARPPEnum getTypeNumber(){return type_number_RECORD;}
		QualidentRecord *optionalQualidentPtr;
		MandatoryFieldsListRecord fieldsList;
	}; 
	struct TypePOINTER: public TypeRecord{
		TypeNumberQARPPEnum getTypeNumber(){return type_number_POINTER;}
		TypeRecord *pointedTypePtr;
	}; 
	struct TypePROCEDURE: public TypeRecord{
		TypeNumberQARPPEnum getTypeNumber(){return type_number_PROCEDURE;}
		FormalParsRecord *optionalFormalParsPtr;
	}; 

	struct Stmt_EmptyStmt:public StatementRecord{
	  virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_EmptyStmt;} 
	};
	struct Stmt_EXPR_OR_ASSIGN:public StatementRecord{
	  	virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_EXPR_OR_ASSIGN;} 
	  	ExprRecord lhsExpr;
	  	bool assignment;
	  	ExprRecord rhsExpr; 
	};
	struct MandatoryELSIFsListRecord{
		ExprRecord expr;
		StatementSeqRecord thenStmtSeq; 
		MandatoryELSIFsListRecord *optionalElsifsListPtr;
	};
	struct Stmt_IF:public StatementRecord{
	 	virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_IF;} 
	    ExprRecord expr;
		StatementSeqRecord thenStmtSeq;
		MandatoryELSIFsListRecord *optionalElsifsListPtr;
	    StatementSeqRecord *optionalElsePtr;
	};
	struct CaseLabelsRecord{
		ConstExprRecord constExpr1; 
		bool secondConstExprPresent;
		ConstExprRecord constExpr2;
	};
	struct CaseLabelsListsRecord{
		CaseLabelsRecord caseLabelsNth;
		CaseLabelsListsRecord *optionalFurtherCaseLabelsListsPtr;
	};
	struct CaseRecord{
		bool emptyCase;
		CaseLabelsListsRecord caseLabelsLists;
		StatementSeqRecord stmtSeq;
	};
	struct CasesRecord{
		CaseRecord caseNth;
		CasesRecord *optionalOtherCasesPtr;
	};
	struct Stmt_CASE:public StatementRecord{
	  	virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_CASE;} 
		ExprRecord expr;
		CaseRecord caseFirst;
		CasesRecord *optionalOtherCasesPtr;
		StatementSeqRecord *optionalElsePtr;
	};
	struct Stmt_WHILE:public StatementRecord{
		virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_WHILE;} 
	    ExprRecord expr;
	    StatementSeqRecord whileBodyStatementSeq;
	};
	struct Stmt_REPEAT:public StatementRecord{
		virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_REPEAT;} 
	    StatementSeqRecord repeatBodyStatementSeq;
	    ExprRecord expr;
	};
	struct Stmt_FOR:public StatementRecord{
	  virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_FOR;} 
	  identRec forCounterVariableName;
	  ExprRecord forCounterVariableInitialValueExpr;
	  ExprRecord forCounterVariableToValueExpr;
	  bool bySpecified;
	  ConstExprRecord byValueConstExpr; 
	  StatementSeqRecord forStatementSeq; 
	};
	struct Stmt_LOOP:public StatementRecord{
		virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_LOOP;} 
	    StatementSeqRecord loopStatementSeq;
	};
	struct GuardRecord{
		QualidentRecord qualident1, qualident2;
	};
	struct FurtherWithClausesRecord{
		GuardRecord guard; 
		StatementSeqRecord statementSeq;
		FurtherWithClausesRecord *next;
	};
	struct Stmt_WITH:public StatementRecord{
		virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_WITH;} 
	    GuardRecord firstGuard;
	    StatementSeqRecord firstStatementSeq;
		FurtherWithClausesRecord *optionalFurtherWithClausesPtr;
		StatementSeqRecord *optionalElsePtr;
	};
	struct Stmt_EXIT:public StatementRecord{
		virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_EXIT;} 
	};
	struct Stmt_RETURN:public StatementRecord{
		virtual StmtTypeNumberEnum getStatementTypeNumber(){return stmtTypeNumber_RETURN;} 
		bool exprPresent;
		ExprRecord expr;
	};


struct ModuleRecord{
	wchar_t* moduleName;
	ImportListRecord *importListPtr;
	DeclSeqRecord declSeq;
	StatementSeqRecord stmtSeq;
};


class CodeGenerator {

public:
	// opcodes
	//static const int
	int
	  ADD,  SUB,   MUL,   DIV,   EQU,  LSS, GTR, NEG,
	  LOAD, LOADG, STO,   STOG,  CONST,
	  CALL, RET,   ENTER, LEAVE,
	  JMP,  FJMP,  READ,  WRITE;

	wchar_t* opcode[21];

	int progStart;		// address of first instruction of main program
	int pc;				// program counter
	char *code;
	int codeSize;

	// data for Interpret
	int *globals;
	int *stack;
	int top;	// top of stack
	int bp;		// base pointer


	CodeGenerator();

	~CodeGenerator();

	//----- code generation methods -----

	void Emit (char op);

	void Emit (char op, short val);

	void Patch (int adr, int val);

	void Disassemble(Oberon::Parser* parser);

	void GenerateCodeForModule(Oberon::ModuleRecord *moduleAST, Oberon::SymbolTable &tab);
	//void WriteObjFile(Oberon::ModuleRecord *moduleAST);

  //----- interpreter methods -----

	int Next () {
		return code[pc++];
	}

	int Next2 () {
		int x,y;
		x = (char)code[pc++]; y = code[pc++];
		return ((x << 8)&( ( (int)-1 ) ^ (int)0xff )) | y;
	}

	int Int (bool b) {
		if (b) return 1; else return 0;
	}

	void Push (int val) {
		stack[top++] = val;
	}

	int Pop() {
		return stack[--top];
	}

	int ReadInt(FILE* s) {
		int sign;
		char ch;
		do {fscanf(s, "%c", &ch);} while (!(ch >= '0' && ch <= '9' || ch == '-'));

		if (ch == '-') {sign = -1; fscanf(s, "%c", &ch);} else sign = 1;
		int n = 0;
		while (ch >= '0' && ch <= '9') {
			n = 10 * n + (ch - '0');
			if (fscanf(s, "%c", &ch) <= 0)
				break;
		}
		return n * sign;
	}
	void Interpret () {
		int val;
		pc = 0; stack[0] = 0; top = 1; bp = 0;
		wprintf(L"PC:=%d\n",pc);
		wprintf(L"INTERPRETING:\n");
		for (;;) {
			wprintf(L"PC:%3d; ",pc);
			if (pc == -1) {wprintf(L"NMI: EXIT"); return;}
			int nxt = Next();
			if (nxt == CONST)
				Push(Next2());
			else if (nxt == LOAD)
				Push(stack[bp+Next2()]);
			else if (nxt == LOADG)
				Push(globals[Next2()]);
			else if (nxt == STO)
				stack[bp+Next2()] = Pop();
			else if (nxt == STOG)
				globals[Next2()] = Pop();
			else if (nxt == ADD)
				Push(Pop()+Pop());
			else if (nxt == SUB)
				Push(-Pop()+Pop());
			else if (nxt == DIV)
				{val = Pop(); Push(Pop()/val);}
			else if (nxt == MUL)
				Push(Pop()*Pop());
			else if (nxt == NEG)
				Push(-Pop());
			else if (nxt == EQU)
				Push(Int(Pop()==Pop()));
			else if (nxt == LSS)
				Push(Int(Pop()>Pop()));
			else if (nxt == GTR)
				Push(Int(Pop()<Pop()));
			else if (nxt == JMP)
				{pc = Next2(); wprintf(L"JMP %d\n",pc);}
			else if (nxt == FJMP)
				{ val = Next2(); if (Pop()==0) pc = val;}
			else if (nxt == WRITE)
				printf("%d\n", Pop());
			else if (nxt == CALL)
				{Push(pc+2); pc = Next2();}
			else if (nxt == RET)
				{puts("RET");pc = Pop();}
			else if (nxt == ENTER)
				{Push(bp); bp = top; top = top + Next2();}
			else if (nxt == LEAVE)
				{top = bp; bp = Pop();}
			else {
				wprintf(L"NMI: illegal opcode\n");
				exit(1);
			}
		}
	}

};

}; // namespace

#endif // !defined(OBERON_ZH_CODEGENERATOR_H)
