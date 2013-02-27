

#include "Parser.h"
#include "Scanner.h"


/* namespace smm {
 */

void Parser::SynErr(int n) {
	//if (errDist >= minErrDist)
		errors->SynErr(la->line, la->col, n);
	errDist = 0;
}

void Parser::SemErr(const wchar_t* msg) {
	//if (errDist >= minErrDist)
		errors->Error(t->line, t->col, msg);
	errDist = 0;
}

void Parser::Get() {
	for (;;) {
		t = la;
		la = scanner->Scan();
		if (la->kind <= maxT) { ++errDist; break; }

		if (dummyToken != t) {
			dummyToken->kind = t->kind;
			dummyToken->pos = t->pos;
			dummyToken->col = t->col;
			dummyToken->line = t->line;
			dummyToken->next = NULL;
			coco_string_delete(dummyToken->val);
			dummyToken->val = coco_string_create(t->val);
			t = dummyToken;
		}
		la = t;
	}
}

void Parser::Expect(int n) {
	if (la->kind==n) Get(); else { SynErr(n); }
}

void Parser::ExpectWeak(int n, int follow) {
	if (la->kind == n) Get();
	else {
		SynErr(n);
		while (!StartOf(follow)) Get();
	}
}

bool Parser::WeakSeparator(int n, int syFol, int repFol) {
	if (la->kind == n) {Get(); return true;}
	else if (StartOf(repFol)) {return false;}
	else {
		SynErr(n);
		while (!(StartOf(syFol) || StartOf(repFol) || StartOf(0))) {
			Get();
		}
		return StartOf(syFol);
	}
}

void Parser::smm() {
		SOURCE();
}

void Parser::SOURCE() {
		while (la->kind == 4 /* "$[" */ || la->kind == 6 /* "$c" */ || la->kind == 8 /* "${" */) {
			if (la->kind == 6 /* "$c" */) {
				CONSTANT();
			} else if (la->kind == 8 /* "${" */) {
				ASSERTION();
			} else {
				INCLUSION();
			}
		}
}

void Parser::CONSTANT() {
		Expect(6 /* "$c" */);
		Expect(_symbol);
		while (la->kind == _symbol) {
			Get();
		}
		Expect(7 /* "$." */);
}

void Parser::ASSERTION() {
		Expect(8 /* "${" */);
		while (la->kind == 10 /* "$v" */) {
			VARIABLE();
		}
		while (la->kind == 11 /* "$d" */) {
			DISJOINTED();
		}
		while (la->kind == _symbol || la->kind == 14 /* "e" */) {
			ESSENTIAL();
		}
		while (la->kind == 12 /* "f" */) {
			FLOATING();
		}
		if (la->kind == 16 /* "a" */) {
			AXIOMATIC();
		} else if (la->kind == 18 /* "p" */) {
			PROVABLE();
		} else SynErr(22);
		Expect(9 /* "$}" */);
}

void Parser::INCLUSION() {
		Expect(4 /* "$[" */);
		Expect(_fileName);
		Expect(5 /* "$]" */);
}

void Parser::VARIABLE() {
		Expect(10 /* "$v" */);
		Expect(_symbol);
		while (la->kind == _symbol) {
			Get();
		}
		Expect(7 /* "$." */);
}

void Parser::DISJOINTED() {
		Expect(11 /* "$d" */);
		Expect(_symbol);
		Expect(_symbol);
		while (la->kind == _symbol) {
			Get();
		}
		Expect(7 /* "$." */);
}

void Parser::ESSENTIAL() {
		if (la->kind == 14 /* "e" */) {
			Get();
			Expect(_index);
		} else if (la->kind == _symbol) {
			Get();
		} else SynErr(23);
		Expect(15 /* "$e" */);
		Expect(_symbol);
		while (la->kind == _symbol) {
			Get();
		}
		Expect(7 /* "$." */);
}

void Parser::FLOATING() {
		Expect(12 /* "f" */);
		Expect(_index);
		Expect(13 /* "$f" */);
		Expect(_symbol);
		Expect(_symbol);
		Expect(7 /* "$." */);
}

void Parser::AXIOMATIC() {
		Expect(16 /* "a" */);
		Expect(_index);
		Expect(17 /* "$a" */);
		Expect(_symbol);
		while (la->kind == _symbol) {
			Get();
		}
		Expect(7 /* "$." */);
}

void Parser::PROVABLE() {
		Expect(18 /* "p" */);
		Expect(_index);
		Expect(19 /* "$p" */);
		Expect(_symbol);
		while (la->kind == _symbol) {
			Get();
		}
		Expect(20 /* "$=" */);
		PROOF();
}

void Parser::PROOF() {
		if (StartOf(1)) {
			PREFIX();
			Expect(_index);
		} else if (la->kind == _symbol) {
			Get();
		} else SynErr(24);
		while (StartOf(2)) {
			if (StartOf(1)) {
				PREFIX();
				Expect(_index);
			} else {
				Get();
			}
		}
		Expect(7 /* "$." */);
}

void Parser::PREFIX() {
		if (la->kind == 12 /* "f" */) {
			Get();
		} else if (la->kind == 14 /* "e" */) {
			Get();
		} else if (la->kind == 16 /* "a" */) {
			Get();
		} else if (la->kind == 18 /* "p" */) {
			Get();
		} else SynErr(25);
}




// If the user declared a method Init and a mehtod Destroy they should
// be called in the contructur and the destructor respctively.
//
// The following templates are used to recognize if the user declared
// the methods Init and Destroy.

template<typename T>
struct ParserInitExistsRecognizer {
	template<typename U, void (U::*)() = &U::Init>
	struct ExistsIfInitIsDefinedMarker{};

	struct InitIsMissingType {
		char dummy1;
	};
	
	struct InitExistsType {
		char dummy1; char dummy2;
	};

	// exists always
	template<typename U>
	static InitIsMissingType is_here(...);

	// exist only if ExistsIfInitIsDefinedMarker is defined
	template<typename U>
	static InitExistsType is_here(ExistsIfInitIsDefinedMarker<U>*);

	enum { InitExists = (sizeof(is_here<T>(NULL)) == sizeof(InitExistsType)) };
};

template<typename T>
struct ParserDestroyExistsRecognizer {
	template<typename U, void (U::*)() = &U::Destroy>
	struct ExistsIfDestroyIsDefinedMarker{};

	struct DestroyIsMissingType {
		char dummy1;
	};
	
	struct DestroyExistsType {
		char dummy1; char dummy2;
	};

	// exists always
	template<typename U>
	static DestroyIsMissingType is_here(...);

	// exist only if ExistsIfDestroyIsDefinedMarker is defined
	template<typename U>
	static DestroyExistsType is_here(ExistsIfDestroyIsDefinedMarker<U>*);

	enum { DestroyExists = (sizeof(is_here<T>(NULL)) == sizeof(DestroyExistsType)) };
};

// The folloing templates are used to call the Init and Destroy methods if they exist.

// Generic case of the ParserInitCaller, gets used if the Init method is missing
template<typename T, bool = ParserInitExistsRecognizer<T>::InitExists>
struct ParserInitCaller {
	static void CallInit(T *t) {
		// nothing to do
	}
};

// True case of the ParserInitCaller, gets used if the Init method exists
template<typename T>
struct ParserInitCaller<T, true> {
	static void CallInit(T *t) {
		t->Init();
	}
};

// Generic case of the ParserDestroyCaller, gets used if the Destroy method is missing
template<typename T, bool = ParserDestroyExistsRecognizer<T>::DestroyExists>
struct ParserDestroyCaller {
	static void CallDestroy(T *t) {
		// nothing to do
	}
};

// True case of the ParserDestroyCaller, gets used if the Destroy method exists
template<typename T>
struct ParserDestroyCaller<T, true> {
	static void CallDestroy(T *t) {
		t->Destroy();
	}
};

void Parser::Parse() {
	t = NULL;
	la = dummyToken = new Token();
	la->val = coco_string_create(L"Dummy Token");
	Get();
	smm();
	Expect(0);
}

Parser::Parser(Scanner *scanner, Errors* errors_) {
	//parserListener=0;
	maxT = 21;

	ParserInitCaller<Parser>::CallInit(this);
	dummyToken = NULL;
	t = la = NULL;
	minErrDist = 2;
	errDist = minErrDist;
	this->scanner = scanner;
	errors = errors_;
}

bool Parser::StartOf(int s) {
	const bool T = true;
	const bool x = false;

	static bool set[3][23] = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, T,x,T,x, x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, T,x,T,x, T,x,T,x, x,x,x}
	};



	return set[s][la->kind];
}

Parser::~Parser() {
	ParserDestroyCaller<Parser>::CallDestroy(this);
	//delete errors;
	delete dummyToken;
}

Errors::Errors() {
	count = 0;
}

void Errors::SynErr(int line, int col, int n) {
	wchar_t* s;
	switch (n) {
			case 0: s = coco_string_create(L"EOF expected"); break;
			case 1: s = coco_string_create(L"symbol expected"); break;
			case 2: s = coco_string_create(L"index expected"); break;
			case 3: s = coco_string_create(L"fileName expected"); break;
			case 4: s = coco_string_create(L"\"$[\" expected"); break;
			case 5: s = coco_string_create(L"\"$]\" expected"); break;
			case 6: s = coco_string_create(L"\"$c\" expected"); break;
			case 7: s = coco_string_create(L"\"$.\" expected"); break;
			case 8: s = coco_string_create(L"\"${\" expected"); break;
			case 9: s = coco_string_create(L"\"$}\" expected"); break;
			case 10: s = coco_string_create(L"\"$v\" expected"); break;
			case 11: s = coco_string_create(L"\"$d\" expected"); break;
			case 12: s = coco_string_create(L"\"f\" expected"); break;
			case 13: s = coco_string_create(L"\"$f\" expected"); break;
			case 14: s = coco_string_create(L"\"e\" expected"); break;
			case 15: s = coco_string_create(L"\"$e\" expected"); break;
			case 16: s = coco_string_create(L"\"a\" expected"); break;
			case 17: s = coco_string_create(L"\"$a\" expected"); break;
			case 18: s = coco_string_create(L"\"p\" expected"); break;
			case 19: s = coco_string_create(L"\"$p\" expected"); break;
			case 20: s = coco_string_create(L"\"$=\" expected"); break;
			case 21: s = coco_string_create(L"??? expected"); break;
			case 22: s = coco_string_create(L"invalid ASSERTION"); break;
			case 23: s = coco_string_create(L"invalid ESSENTIAL"); break;
			case 24: s = coco_string_create(L"invalid PROOF"); break;
			case 25: s = coco_string_create(L"invalid PREFIX"); break;

		default:
		{
			wchar_t format[20];
			coco_swprintf(format, 20, L"error %d", n);
			s = coco_string_create(format);
		}
		break;
	}
	wprintf(L"-- line %d col %d: %ls\n", line, col, s);
	coco_string_delete(s);
	count++;
}

void Errors::Error(int line, int col, const wchar_t *s) {
	wprintf(L"-- line %d col %d: %ls\n", line, col, s);
	count++;
}

void Errors::Warning(int line, int col, const wchar_t *s) {
	wprintf(L"-- line %d col %d: %ls\n", line, col, s);
}

void Errors::Warning(const wchar_t *s) {
	wprintf(L"%ls\n", s);
}

void Errors::Exception(const wchar_t* s) {
	wprintf(L"%ls", s); 
	exit(1);
}

/* } // namespace
 */
