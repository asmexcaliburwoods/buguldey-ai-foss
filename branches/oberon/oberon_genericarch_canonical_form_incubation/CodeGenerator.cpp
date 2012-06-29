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

/*void Oberon::CodeGenerator::WriteObjFile(Oberon::Parser::ModuleRecord &moduleAST){
	int len=wstrlen(moduleAST.moduleName);
	wchar_t *objFileName=new wchar_t[4+len+4+1];
	swprintf(objFileName, L"Obj/%ls.Obj", moduleAST.moduleName);
	wmkdir(L"Obj");
	FILE* objFile=wfopen(objFileName, "wb");
	fwrite(objFile, code, pc);
	fclose(objFile);
}*/
void Oberon::CodeGenerator::GenerateCodeForModule(Oberon::Parser::ModuleRecord &moduleAST, Oberon::SymbolTable &tab){
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
