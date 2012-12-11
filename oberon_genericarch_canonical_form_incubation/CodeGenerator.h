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
#include "ModuleTable.h"
#include "Parser.h"

namespace Oberon {

class CodeGenerator {
	ModuleTable *modules;
	void InterpretModuleDeclSeq(Parser::DeclSeqRecord &declSeq, Oberon::SymbolTable &tab);
	void InterpretModuleInit(Parser::ModuleRecord *modAST, Oberon::SymbolTable &tab);
	void InterpretStmtSeq(Parser::StatementSeqRecord& stmtSeq, Oberon::SymbolTable &tab);
public:
	void IMPORT(wchar_t* moduleName);
	void InterpretImport(Parser::ImportListRecord* ip);

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


	CodeGenerator(ModuleTable * modules);

	~CodeGenerator();

	//----- code generation methods -----

	void Emit (char op);

	void Emit (char op, short val);

	void Patch (int adr, int val);

	void Disassemble(Oberon::Parser* parser);

	void InterpretModule(Parser::ModuleRecord *moduleAST, Oberon::SymbolTable &tab);
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
