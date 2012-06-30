#include "CodeGenerator.h"
#include "Parser.h"

void Oberon::CodeGenerator::Disassemble(Parser* parser) {
	int maxPc = pc;
	wprintf(L"TOP PC: %3d\n", pc);
	wprintf(L"DISASSEMBLING:\n");
	pc = 0;
	while (pc < maxPc) {
		int code = Next();
		wprintf(L"%3x:\t%2x\t%ls\t", pc-1, code, (code==-1?L"-1":opcode[code]));
		if (code == LOAD || code == LOADG || code == CONST || code == STO || code == STOG ||
			code == CALL || code == ENTER || code == JMP   || code == FJMP)
				wprintf(L"%d\n", Next2());
		else
			if (code == ADD  || code == SUB || code == MUL || code == DIV || code == NEG ||
				code == EQU  || code == LSS || code == GTR || code == RET || code == LEAVE ||
				code == READ || code == WRITE)
					wprintf(L"\n");
			else
				wprintf(L"\n");
	}
}

namespace Oberon {

/*void CodeGenerator::WriteObjFile(Oberon::Parser::ModuleRecord &moduleAST){
	int len=wstrlen(moduleAST.moduleName);
	wchar_t *objFileName=new wchar_t[4+len+4+1];
	swprintf(objFileName, L"Obj/%ls.Obj", moduleAST.moduleName);
	wmkdir(L"Obj");
	FILE* objFile=wfopen(objFileName, "wb");
	fwrite(objFile, code, pc);
	fclose(objFile);
}*/
void CodeGenerator::GenerateCodeForModule(Oberon::Parser::ModuleRecord &moduleAST, Oberon::SymbolTable &tab){
	tab.OpenScope();
	/*
	  "MODULE" Ident<r.moduleName> ";"
	  ( 					(. r.importListPtr=0; .)
	  |						(. r.importListPtr=new ImportListRecord(); abortIfNull(r.importListPtr); .)
		ImportList<*(r.importListPtr)>
	  )
	  DeclSeq<r.declSeq>
	  ["BEGIN" StatementSeq<r.stmtSeq>]
	  "END" ident "."
	 */

	tab.CloseScope();
	//WriteObjFile(moduleAST);
}


CodeGenerator::CodeGenerator() {
		// opcodes
		ADD  =  0; SUB   =  1; MUL   =  2; DIV   =  3; EQU   =  4; LSS = 5; GTR = 6; NEG = 7;
		LOAD =  8; LOADG =  9; STO   = 10; STOG  = 11; CONST = 12;
		CALL = 13; RET   = 14; ENTER = 15; LEAVE = 16;
		JMP  = 17; FJMP  = 18; READ  = 19; WRITE = 20;

		opcode[ 0] = coco_string_create("ADD  ");
		opcode[ 1] = coco_string_create("SUB  ");
		opcode[ 2] = coco_string_create("MUL  ");
		opcode[ 3] = coco_string_create("DIV  ");
		opcode[ 4] = coco_string_create("EQU  ");
		opcode[ 5] = coco_string_create("LSS  ");
		opcode[ 6] = coco_string_create("GTR  ");
		opcode[ 7] = coco_string_create("NEG  ");
		opcode[ 8] = coco_string_create("LOAD ");
		opcode[ 9] = coco_string_create("LOADG");
		opcode[10] = coco_string_create("STO  ");
		opcode[11] = coco_string_create("STOG ");
		opcode[12] = coco_string_create("CONST");
		opcode[13] = coco_string_create("CALL ");
		opcode[14] = coco_string_create("RET  ");
		opcode[15] = coco_string_create("ENTER");
		opcode[16] = coco_string_create("LEAVE");
		opcode[17] = coco_string_create("JMP  ");
		opcode[18] = coco_string_create("FJMP ");
		opcode[19] = coco_string_create("READ ");
		opcode[20] = coco_string_create("WRITE");

		codeSize=32*1024;
		code    = new char[codeSize];
		globals = new int[100];
		stack   = new int[100];

		progStart = 0;

		pc = 0;
	}

CodeGenerator::~CodeGenerator() {
		coco_string_delete(opcode[ 0]);
		coco_string_delete(opcode[ 1]);
		coco_string_delete(opcode[ 2]);
		coco_string_delete(opcode[ 3]);
		coco_string_delete(opcode[ 4]);
		coco_string_delete(opcode[ 5]);
		coco_string_delete(opcode[ 6]);
		coco_string_delete(opcode[ 7]);
		coco_string_delete(opcode[ 8]);
		coco_string_delete(opcode[ 9]);
		coco_string_delete(opcode[10]);
		coco_string_delete(opcode[11]);
		coco_string_delete(opcode[12]);
		coco_string_delete(opcode[13]);
		coco_string_delete(opcode[14]);
		coco_string_delete(opcode[15]);
		coco_string_delete(opcode[16]);
		coco_string_delete(opcode[17]);
		coco_string_delete(opcode[18]);
		coco_string_delete(opcode[19]);
		coco_string_delete(opcode[20]);
	}

	//----- code generation methods -----

void CodeGenerator::Emit (char op) {
		code[pc++] = op;
		if(pc==codeSize){
			int newCodeSize=codeSize+32*1024;
			char *newCode=new char[newCodeSize];
			if(newCode==0){
				wprintf(L"Not enough memory.\n");
				exit(2);
			}
			char*cp=code;
			char*ncp=newCode;
			int cnt=codeSize;
			while(cnt-- > 0){
				*ncp++=*cp++;
			}
			char*oldCode=code;
			code=newCode;
			codeSize=newCodeSize;
			delete[] oldCode;
		}
	}

void CodeGenerator::Emit (char op, short val) {
		Emit(op); Emit((char)(val>>8)); Emit((char)val);
	}

void CodeGenerator::Patch (int adr, int val) {
		code[adr] = (char)(val>>8); code[adr+1] = (char)val;
	}

void CodeGenerator::GenerateCodeForModule(Oberon::Parser::ModuleRecord &moduleAST, Oberon::SymbolTable &tab);
	//void WriteObjFile(Oberon::Parser::ModuleRecord &moduleAST);

/*
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
*/

}; // namespace
