/*----------------------------------------------------------------------
Compiler Generator Coco/R,
Copyright (c) 1990, 2004 Hanspeter Moessenboeck, University of Linz
extended by M. Loeberbauer & A. Woess, Univ. of Linz
ported to C++ by Csaba Balazs, University of Szeged
with improvements by Pat Terry, Rhodes University

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by the 
Free Software Foundation; either version 2, or (at your option) any 
later version.

This program is distributed in the hope that it will be useful, but 
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
for more details.

You should have received a copy of the GNU General Public License along 
with this program; if not, write to the Free Software Foundation, Inc., 
59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

As an exception, it is allowed to write an extension of Coco/R that is
used as a plugin in non-free software.

If not otherwise stated, any source code generated by Coco/R (other than 
Coco/R itself) does not fall under the GNU General Public License.
-----------------------------------------------------------------------*/


#include <wchar.h>
#include "Parser.h"
#include "Scanner.h"


namespace Coco {


void Parser::SynErr(int n) {
	if (errDist >= minErrDist) errors->SynErr(la->line, la->col, n);
	errDist = 0;
}

void Parser::SemErr(const wchar_t* msg, const int line) {
	if (errDist >= minErrDist) errors->Error(line<0?t->line:line, t->col, msg);
	errDist = 0;
}

void Parser::Get() {
	for (;;) {
		t = la;
		la = scanner->Scan();
		if (la->kind <= maxT) { ++errDist; break; }
		if (la->kind == _ddtSym) {
				tab->SetDDT(la->val); 
		}
		if (la->kind == _optionSym) {
				tab->SetOption(la->val); 
		}

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

void Parser::Coco() {
		Symbol *sym; Graph *g, *g1, *g2; wchar_t* gramName = NULL; CharSet *s; int oldLine; 
		int beg = la->pos; int line = la->line; 
		while (StartOf(1)) {
			Get();
		}
		if (la->pos != beg) {
		 pgen->usingPos = new Position(beg, t->pos + coco_string_length(t->val), 0, line);
		}
		
		Expect(6 /* "COMPILER" */);
		genScanner = true; 
		tab->ignored = new CharSet(); 
		Expect(_ident);
		gramName = coco_string_create(t->val);
		beg = la->pos;
		line = la->line;
		
		while (StartOf(2)) {
			Get();
		}
		tab->semDeclPos = new Position(beg, la->pos, 0, line); 
		if (la->kind == 7 /* "IGNORECASE" */) {
			Get();
			dfa->ignoreCase = true; 
		}
		if (la->kind == 8 /* "CHARACTERS" */) {
			Get();
			while (la->kind == _ident) {
				SetDecl();
			}
		}
		if (la->kind == 9 /* "TOKENS" */) {
			Get();
			while (la->kind == _ident || la->kind == _string || la->kind == _char) {
				TokenDecl(Node::t);
			}
		}
		if (la->kind == 10 /* "PRAGMAS" */) {
			Get();
			while (la->kind == _ident || la->kind == _string || la->kind == _char) {
				TokenDecl(Node::pr);
			}
		}
		while (la->kind == 11 /* "COMMENTS" */) {
			Get();
			bool nested = false; 
			Expect(12 /* "FROM" */);
			TokenExpr(g1);
			Expect(13 /* "TO" */);
			TokenExpr(g2);
			if (la->kind == 14 /* "NESTED" */) {
				Get();
				nested = true; 
			}
			dfa->NewComment(g1->l, g2->l, nested); 
		}
		while (la->kind == 15 /* "IGNORE" */) {
			Get();
			Set(s);
			tab->ignored->Or(s); 
		}
		while (!(la->kind == _EOF || la->kind == 16 /* "PRODUCTIONS" */)) {SynErr(42); Get();}
		Expect(16 /* "PRODUCTIONS" */);
		if (genScanner) dfa->MakeDeterministic();
		tab->DeleteNodes();
		
		while (la->kind == _ident) {
			Get();
			oldLine=-1;
			sym = tab->FindSym(t->val);
			bool undef = (sym == NULL);//symbol not defined
			if (undef) sym = tab->NewSym(Node::nt, t->val, t->line);//define
			else {
			 if (sym->typ == Node::nt) {
			   if (sym->graph != NULL) SemErr(L"name declared twice");
			   else oldLine=sym->attrPos == NULL?-1:sym->attrPos->line;
			 } else SemErr(L"this symbol kind not allowed on left side of production");
			 sym->line = t->line;
			}
			bool noAttrs = (sym->attrPos == NULL);
			sym->attrPos = NULL;//TODO memory leak
			
			if (la->kind == 24 /* "<" */ || la->kind == 26 /* "<." */) {
				AttrDecl(sym);
			}
			if (!undef)//joe removed "!"
			 if (noAttrs != (sym->attrPos == NULL)){
			   if(oldLine>=0)SemErr(L"attribute mismatch between declaration and use of this symbol (triggered by previous use)", oldLine);
			   else SemErr(L"attribute mismatch between declaration and use of this symbol (triggered by previous use)");
			 }
			
			if (la->kind == 39 /* "(." */) {
				SemText(sym->semPos);
			}
			ExpectWeak(17 /* "=" */, 3);
			Expression(g);
			sym->graph = g->l;
			tab->Finish(g);
			
			ExpectWeak(18 /* "." */, 4);
		}
		Expect(19 /* "END" */);
		Expect(_ident);
		if (!coco_string_equal(gramName, t->val))
		 SemErr(L"name does not match grammar name");
		tab->gramSy = tab->FindSym(gramName);
		if (tab->gramSy == NULL)
		 SemErr(L"missing production for grammar name");
		else {
		 sym = tab->gramSy;
		 if (sym->attrPos != NULL)
		   SemErr(L"grammar symbol must not have attributes");
		}
		tab->noSym = tab->NewSym(Node::t, L"???", 0); // noSym gets highest number
		tab->SetupAnys();
		tab->RenumberPragmas();
		if (tab->ddt[2]) tab->PrintNodes();
		if (errors->count == 0) {
		 wprintf(L"checking\n");
		 tab->CompSymbolSets();
		 if (tab->ddt[7]) tab->XRef();
		 if (tab->GrammarOk()) {
		   wprintf(L"parser");
		   pgen->WriteParser();
		   if (genScanner) {
		     wprintf(L" + scanner");
		     dfa->WriteScanner();
		     if (tab->ddt[0]) dfa->PrintStates();
		   }
		   wprintf(L" generated\n");
		   if (tab->ddt[8]) pgen->WriteStatistics();
		 }
		}
		if (tab->ddt[6]) tab->PrintSymbolTable();
		
		Expect(18 /* "." */);
}

void Parser::SetDecl() {
		CharSet *s; 
		Expect(_ident);
		wchar_t *name = coco_string_create(t->val);
		CharClass *c = tab->FindCharClass(name);
		if (c != NULL) SemErr(L"name declared twice");
		
		Expect(17 /* "=" */);
		Set(s);
		if (s->Elements() == 0) SemErr(L"character set must not be empty");
		tab->NewCharClass(name, s);
		
		Expect(18 /* "." */);
}

void Parser::TokenDecl(int typ) {
		wchar_t* name = NULL; int kind; Symbol *sym; Graph *g; 
		Sym(name, kind);
		sym = tab->FindSym(name);
		if (sym != NULL) SemErr(L"name declared twice");
		else {
		 sym = tab->NewSym(typ, name, t->line);
		 sym->tokenKind = Symbol::fixedToken;
		}
		tokenString = NULL;
		
		while (!(StartOf(5))) {SynErr(43); Get();}
		if (la->kind == 17 /* "=" */) {
			Get();
			TokenExpr(g);
			Expect(18 /* "." */);
			if (kind == str) SemErr(L"a literal must not be declared with a structure");
			tab->Finish(g);
			if (tokenString == NULL || coco_string_equal(tokenString, noString))
			 dfa->ConvertToStates(g->l, sym);
			else { // TokenExpr is a single string
			 if ((*(tab->literals))[tokenString] != NULL)
			   SemErr(L"token string declared twice");
			 tab->literals->Set(tokenString, sym);
			 dfa->MatchLiteral(tokenString, sym);
			}
			
		} else if (StartOf(6)) {
			if (kind == id) genScanner = false;
			else dfa->MatchLiteral(sym->name, sym);
			
		} else SynErr(44);
		if (la->kind == 39 /* "(." */) {
			SemText(sym->semPos);
			if (typ != Node::pr) SemErr(L"semantic action not allowed here"); 
		}
}

void Parser::TokenExpr(Graph* &g) {
		Graph *g2; 
		TokenTerm(g);
		bool first = true; 
		while (WeakSeparator(28 /* "|" */,8,7) ) {
			TokenTerm(g2);
			if (first) { tab->MakeFirstAlt(g); first = false; }
			tab->MakeAlternative(g, g2);
			
		}
}

void Parser::Set(CharSet* &s) {
		CharSet *s2; 
		SimSet(s);
		while (la->kind == 20 /* "+" */ || la->kind == 21 /* "-" */) {
			if (la->kind == 20 /* "+" */) {
				Get();
				SimSet(s2);
				s->Or(s2); 
			} else {
				Get();
				SimSet(s2);
				s->Subtract(s2); 
			}
		}
}

void Parser::AttrDecl(Symbol *sym) {
		if (la->kind == 24 /* "<" */) {
			Get();
			int beg = la->pos; int col = la->col; int line = la->line; 
			while (StartOf(9)) {
				if (StartOf(10)) {
					Get();
				} else {
					Get();
					SemErr(L"bad string in attributes"); 
				}
			}
			Expect(25 /* ">" */);
			if (t->pos > beg)
			 sym->attrPos = new Position(beg, t->pos, col, line); 
		} else if (la->kind == 26 /* "<." */) {
			Get();
			int beg = la->pos; int col = la->col; int line = la->line; 
			while (StartOf(11)) {
				if (StartOf(12)) {
					Get();
				} else {
					Get();
					SemErr(L"bad string in attributes"); 
				}
			}
			Expect(27 /* ".>" */);
			if (t->pos > beg)
			 sym->attrPos = new Position(beg, t->pos, col, line); 
		} else SynErr(45);
}

void Parser::SemText(Position* &pos) {
		Expect(39 /* "(." */);
		int beg = la->pos; int col = la->col; int line = t->line; 
		while (StartOf(13)) {
			if (StartOf(14)) {
				Get();
			} else if (la->kind == _badString) {
				Get();
				SemErr(L"bad string in semantic action"); 
			} else {
				Get();
				SemErr(L"missing end of previous semantic action"); 
			}
		}
		Expect(40 /* ".)" */);
		pos = new Position(beg, t->pos, col, line); 
}

void Parser::Expression(Graph* &g) {
		Graph *g2; 
		Term(g);
		bool first = true; 
		while (WeakSeparator(28 /* "|" */,16,15) ) {
			Term(g2);
			if (first) { tab->MakeFirstAlt(g); first = false; }
			tab->MakeAlternative(g, g2);
			
		}
}

void Parser::SimSet(CharSet* &s) {
		int n1, n2; 
		s = new CharSet(); 
		if (la->kind == _ident) {
			Get();
			CharClass *c = tab->FindCharClass(t->val);
			if (c == NULL) SemErr(L"undefined name"); else s->Or(c->set);
			
		} else if (la->kind == _string) {
			Get();
			wchar_t *subName2 = coco_string_create(t->val, 1, coco_string_length(t->val)-2);
			wchar_t *name = tab->Unescape(subName2);
			coco_string_delete(subName2);
			                       wchar_t ch;
			                       int len = coco_string_length(name);
			                       for(int i=0; i < len; i++) {
			                         ch = name[i];
			                         if (dfa->ignoreCase) {
			                           if ((L'A' <= ch) && (ch <= L'Z')) ch = ch - (L'A' - L'a'); // ch.ToLower()
			                         }
			                         s->Set(ch);
			                       }
			coco_string_delete(name);
			                    
		} else if (la->kind == _char) {
			Char(n1);
			s->Set(n1); 
			if (la->kind == 22 /* ".." */) {
				Get();
				Char(n2);
				for (int i = n1; i <= n2; i++) s->Set(i); 
			}
		} else if (la->kind == 23 /* "ANY" */) {
			Get();
			s = new CharSet(); s->Fill(); 
		} else SynErr(46);
}

void Parser::Char(int &n) {
		Expect(_char);
		n = 0;
		wchar_t* subName = coco_string_create(t->val, 1, coco_string_length(t->val)-2);
		wchar_t* name = tab->Unescape(subName);
		coco_string_delete(subName);
		
		// "<= 1" instead of "== 1" to allow the escape sequence '\0' in c++
		if (coco_string_length(name) <= 1) n = name[0];
		else SemErr(L"unacceptable character value");
		coco_string_delete(name);
		if (dfa->ignoreCase && (((wchar_t) n) >= 'A') && (((wchar_t) n) <= 'Z')) n += 32;
		
}

void Parser::Sym(wchar_t* &name, int &kind) {
		name = coco_string_create(L"???"); kind = id; 
		if (la->kind == _ident) {
			Get();
			kind = id; coco_string_delete(name); name = coco_string_create(t->val); 
		} else if (la->kind == _string || la->kind == _char) {
			if (la->kind == _string) {
				Get();
				coco_string_delete(name); name = coco_string_create(t->val); 
			} else {
				Get();
				wchar_t *subName = coco_string_create(t->val, 1, coco_string_length(t->val)-2);
				coco_string_delete(name); 
				name = coco_string_create_append(L"\"", subName);
				coco_string_delete(subName);
				coco_string_merge(name, L"\""); 
				
			}
			kind = str;
			if (dfa->ignoreCase) {
			wchar_t *oldName = name;
			name = coco_string_create_lower(name);
			coco_string_delete(oldName);
			}
			if (coco_string_indexof(name, ' ') >= 0)
			 SemErr(L"literal tokens must not contain blanks"); 
		} else SynErr(47);
}

void Parser::Term(Graph* &g) {
		Graph *g2; Node *rslv = NULL; g = NULL; 
		if (StartOf(17)) {
			if (la->kind == 37 /* "IF" */) {
				rslv = tab->NewNode(Node::rslv, (Symbol*)NULL, la->line); 
				Resolver(rslv->pos);
				g = new Graph(rslv); 
			}
			Factor(g2);
			if (rslv != NULL) tab->MakeSequence(g, g2);
			else g = g2; 
			while (StartOf(18)) {
				Factor(g2);
				tab->MakeSequence(g, g2); 
			}
		} else if (StartOf(19)) {
			g = new Graph(tab->NewNode(Node::eps, (Symbol*)NULL, 0)); 
		} else SynErr(48);
		if (g == NULL) // invalid start of Term
		g = new Graph(tab->NewNode(Node::eps, (Symbol*)NULL, 0)); 
}

void Parser::Resolver(Position* &pos) {
		Expect(37 /* "IF" */);
		Expect(30 /* "(" */);
		int beg = la->pos; int col = la->col; int line = la->line; 
		Condition();
		pos = new Position(beg, t->pos, col, line); 
}

void Parser::Factor(Graph* &g) {
		wchar_t* name = NULL; int kind; Position *pos; bool weak = false; 
		 g = NULL;
		
		switch (la->kind) {
		case _ident: case _string: case _char: case 29 /* "WEAK" */: {
			if (la->kind == 29 /* "WEAK" */) {
				Get();
				weak = true; 
			}
			Sym(name, kind);
			Symbol *sym = tab->FindSym(name);
			 if (sym == NULL && kind == str)
			   sym = (Symbol*)((*(tab->literals))[name]);
			 bool undef = (sym == NULL);
			 if (undef) {
			   if (kind == id)
			     sym = tab->NewSym(Node::nt, name, t->line);  // forward nt ###2
			   else if (genScanner) { 
			     sym = tab->NewSym(Node::t, name, t->line);
			     dfa->MatchLiteral(sym->name, sym);
			   } else {  // undefined string in production
			     SemErr(L"undefined string in production");
			     sym = tab->eofSy;  // dummy
			   }
			 }
			 int typ = sym->typ;
			 if (typ != Node::t && typ != Node::nt)
			   SemErr(L"this symbol kind is not allowed in a production");
			 if (weak) {
			   if (typ == Node::t) typ = Node::wt;
			   else SemErr(L"only terminals may be weak");
			 }
			 Node *p = tab->NewNode(typ, sym, t->line);
			 g = new Graph(p);
			
			if (la->kind == 24 /* "<" */ || la->kind == 26 /* "<." */) {
				Attribs(p);
				if (kind != id) SemErr(L"a literal must not have attributes"); 
			}
			if (undef)
			 sym->attrPos = p->pos;  // dummy
			else if ((p->pos == NULL) != (sym->attrPos == NULL))
			 SemErr(L"attribute mismatch between declaration and use of this symbol (triggered by previous declaration)");
			
			break;
		}
		case 30 /* "(" */: {
			Get();
			Expression(g);
			Expect(31 /* ")" */);
			break;
		}
		case 32 /* "[" */: {
			Get();
			Expression(g);
			Expect(33 /* "]" */);
			tab->MakeOption(g); 
			break;
		}
		case 34 /* "{" */: {
			Get();
			Expression(g);
			Expect(35 /* "}" */);
			tab->MakeIteration(g); 
			break;
		}
		case 39 /* "(." */: {
			SemText(pos);
			Node *p = tab->NewNode(Node::sem, (Symbol*)NULL, 0);
			   p->pos = pos;
			   g = new Graph(p);
			 
			break;
		}
		case 23 /* "ANY" */: {
			Get();
			Node *p = tab->NewNode(Node::any, (Symbol*)NULL, 0);  // p.set is set in tab->SetupAnys
			g = new Graph(p);
			
			break;
		}
		case 36 /* "SYNC" */: {
			Get();
			Node *p = tab->NewNode(Node::sync, (Symbol*)NULL, 0);
			g = new Graph(p);
			
			break;
		}
		default: SynErr(49); break;
		}
		if (g == NULL) // invalid start of Factor
		 g = new Graph(tab->NewNode(Node::eps, (Symbol*)NULL, 0));
		
}

void Parser::Attribs(Node *p) {
		if (la->kind == 24 /* "<" */) {
			Get();
			int beg = la->pos; int col = la->col; int line = la->line; 
			while (StartOf(9)) {
				if (StartOf(10)) {
					Get();
				} else {
					Get();
					SemErr(L"bad string in attributes"); 
				}
			}
			Expect(25 /* ">" */);
			if (t->pos > beg) p->pos = new Position(beg, t->pos, col, line); 
		} else if (la->kind == 26 /* "<." */) {
			Get();
			int beg = la->pos; int col = la->col; int line = la->line; 
			while (StartOf(11)) {
				if (StartOf(12)) {
					Get();
				} else {
					Get();
					SemErr(L"bad string in attributes"); 
				}
			}
			Expect(27 /* ".>" */);
			if (t->pos > beg) p->pos = new Position(beg, t->pos, col, line); 
		} else SynErr(50);
}

void Parser::Condition() {
		while (StartOf(20)) {
			if (la->kind == 30 /* "(" */) {
				Get();
				Condition();
			} else {
				Get();
			}
		}
		Expect(31 /* ")" */);
}

void Parser::TokenTerm(Graph* &g) {
		Graph *g2; 
		TokenFactor(g);
		while (StartOf(8)) {
			TokenFactor(g2);
			tab->MakeSequence(g, g2); 
		}
		if (la->kind == 38 /* "CONTEXT" */) {
			Get();
			Expect(30 /* "(" */);
			TokenExpr(g2);
			tab->SetContextTrans(g2->l); dfa->hasCtxMoves = true;
			   tab->MakeSequence(g, g2); 
			Expect(31 /* ")" */);
		}
}

void Parser::TokenFactor(Graph* &g) {
		wchar_t* name = NULL; int kind; 
		g = NULL; 
		if (la->kind == _ident || la->kind == _string || la->kind == _char) {
			Sym(name, kind);
			if (kind == id) {
			   CharClass *c = tab->FindCharClass(name);
			   if (c == NULL) {
			     SemErr(L"undefined name");
			     c = tab->NewCharClass(name, new CharSet());
			   }
			   Node *p = tab->NewNode(Node::clas, (Symbol*)NULL, 0); p->val = c->n;
			   g = new Graph(p);
			   tokenString = coco_string_create(noString);
			 } else { // str
			   g = tab->StrToGraph(name);
			   if (tokenString == NULL) tokenString = coco_string_create(name);
			   else tokenString = coco_string_create(noString);
			 }
			
		} else if (la->kind == 30 /* "(" */) {
			Get();
			TokenExpr(g);
			Expect(31 /* ")" */);
		} else if (la->kind == 32 /* "[" */) {
			Get();
			TokenExpr(g);
			Expect(33 /* "]" */);
			tab->MakeOption(g); tokenString = coco_string_create(noString); 
		} else if (la->kind == 34 /* "{" */) {
			Get();
			TokenExpr(g);
			Expect(35 /* "}" */);
			tab->MakeIteration(g); tokenString = coco_string_create(noString); 
		} else SynErr(51);
		if (g == NULL) // invalid start of TokenFactor
		 g = new Graph(tab->NewNode(Node::eps, (Symbol*)NULL, 0)); 
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
	Coco();
	Expect(0);
}

Parser::Parser(Scanner *scanner) {
	maxT = 41;

	ParserInitCaller<Parser>::CallInit(this);
	dummyToken = NULL;
	t = la = NULL;
	minErrDist = 2;
	errDist = minErrDist;
	this->scanner = scanner;
	errors = new Errors();
}

bool Parser::StartOf(int s) {
	const bool T = true;
	const bool x = false;

	static bool set[21][43] = {
		{T,T,x,T, x,T,x,x, x,x,T,T, x,x,x,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
		{x,T,T,T, T,T,x,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{x,T,T,T, T,T,T,x, x,x,x,x, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{T,T,x,T, x,T,x,x, x,x,T,T, x,x,x,T, T,T,T,x, x,x,x,T, x,x,x,x, T,T,T,x, T,x,T,x, T,T,x,T, x,x,x},
		{T,T,x,T, x,T,x,x, x,x,T,T, x,x,x,T, T,T,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
		{T,T,x,T, x,T,x,x, x,x,T,T, x,x,x,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
		{x,T,x,T, x,T,x,x, x,x,T,T, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,T, x,T,T,T, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,T, x,T,x,T, x,x,x,x, x,x,x},
		{x,T,x,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,T,x, x,x,x,x, x,x,x},
		{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{x,T,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{x,T,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,x},
		{x,T,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,T,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,T, x,T,x,T, x,x,x,x, x,x,x},
		{x,T,x,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,T, x,x,x,x, T,T,T,T, T,T,T,T, T,T,x,T, x,x,x},
		{x,T,x,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,T,x, T,x,T,x, T,T,x,T, x,x,x},
		{x,T,x,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,T,x, T,x,T,x, T,x,x,T, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, T,x,x,T, x,T,x,T, x,x,x,x, x,x,x},
		{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, T,T,T,T, T,T,T,T, T,T,x}
	};



	return set[s][la->kind];
}

Parser::~Parser() {
	ParserDestroyCaller<Parser>::CallDestroy(this);
	delete errors;
	delete dummyToken;
}

Errors::Errors() {
	count = 0;
}

void Errors::SynErr(int line, int col, int n) {
	wchar_t* s;
	switch (n) {
			case 0: s = coco_string_create(L"EOF expected"); break;
			case 1: s = coco_string_create(L"ident expected"); break;
			case 2: s = coco_string_create(L"number expected"); break;
			case 3: s = coco_string_create(L"string expected"); break;
			case 4: s = coco_string_create(L"badString expected"); break;
			case 5: s = coco_string_create(L"char expected"); break;
			case 6: s = coco_string_create(L"\"COMPILER\" expected"); break;
			case 7: s = coco_string_create(L"\"IGNORECASE\" expected"); break;
			case 8: s = coco_string_create(L"\"CHARACTERS\" expected"); break;
			case 9: s = coco_string_create(L"\"TOKENS\" expected"); break;
			case 10: s = coco_string_create(L"\"PRAGMAS\" expected"); break;
			case 11: s = coco_string_create(L"\"COMMENTS\" expected"); break;
			case 12: s = coco_string_create(L"\"FROM\" expected"); break;
			case 13: s = coco_string_create(L"\"TO\" expected"); break;
			case 14: s = coco_string_create(L"\"NESTED\" expected"); break;
			case 15: s = coco_string_create(L"\"IGNORE\" expected"); break;
			case 16: s = coco_string_create(L"\"PRODUCTIONS\" expected"); break;
			case 17: s = coco_string_create(L"\"=\" expected"); break;
			case 18: s = coco_string_create(L"\".\" expected"); break;
			case 19: s = coco_string_create(L"\"END\" expected"); break;
			case 20: s = coco_string_create(L"\"+\" expected"); break;
			case 21: s = coco_string_create(L"\"-\" expected"); break;
			case 22: s = coco_string_create(L"\"..\" expected"); break;
			case 23: s = coco_string_create(L"\"ANY\" expected"); break;
			case 24: s = coco_string_create(L"\"<\" expected"); break;
			case 25: s = coco_string_create(L"\">\" expected"); break;
			case 26: s = coco_string_create(L"\"<.\" expected"); break;
			case 27: s = coco_string_create(L"\".>\" expected"); break;
			case 28: s = coco_string_create(L"\"|\" expected"); break;
			case 29: s = coco_string_create(L"\"WEAK\" expected"); break;
			case 30: s = coco_string_create(L"\"(\" expected"); break;
			case 31: s = coco_string_create(L"\")\" expected"); break;
			case 32: s = coco_string_create(L"\"[\" expected"); break;
			case 33: s = coco_string_create(L"\"]\" expected"); break;
			case 34: s = coco_string_create(L"\"{\" expected"); break;
			case 35: s = coco_string_create(L"\"}\" expected"); break;
			case 36: s = coco_string_create(L"\"SYNC\" expected"); break;
			case 37: s = coco_string_create(L"\"IF\" expected"); break;
			case 38: s = coco_string_create(L"\"CONTEXT\" expected"); break;
			case 39: s = coco_string_create(L"\"(.\" expected"); break;
			case 40: s = coco_string_create(L"\".)\" expected"); break;
			case 41: s = coco_string_create(L"??? expected"); break;
			case 42: s = coco_string_create(L"this symbol not expected in Coco"); break;
			case 43: s = coco_string_create(L"this symbol not expected in TokenDecl"); break;
			case 44: s = coco_string_create(L"invalid TokenDecl"); break;
			case 45: s = coco_string_create(L"invalid AttrDecl"); break;
			case 46: s = coco_string_create(L"invalid SimSet"); break;
			case 47: s = coco_string_create(L"invalid Sym"); break;
			case 48: s = coco_string_create(L"invalid Term"); break;
			case 49: s = coco_string_create(L"invalid Factor"); break;
			case 50: s = coco_string_create(L"invalid Attribs"); break;
			case 51: s = coco_string_create(L"invalid TokenFactor"); break;

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

} // namespace

