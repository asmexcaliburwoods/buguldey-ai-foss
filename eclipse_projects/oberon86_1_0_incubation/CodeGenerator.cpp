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
