package org.autocoder.ac1;

import java.util.*;
import org.autocoder.ac1.semaction.*;
import org.autocoder.ac1.semaction.expr.*;

public class Parser {
	static final int _EOF = 0;
	static final int _ident = 1;
	static final int _intLit = 2;
	static final int _floatLit = 3;
	static final int _charLit = 4;
	static final int _stringLit = 5;
	static final int _boolean = 6;
	static final int _byte = 7;
	static final int _char = 8;
	static final int _class = 9;
	static final int _double = 10;
	static final int _false = 11;
	static final int _final = 12;
	static final int _float = 13;
	static final int _int = 14;
	static final int _long = 15;
	static final int _new = 16;
	static final int _null = 17;
	static final int _short = 18;
	static final int _static = 19;
	static final int _super = 20;
	static final int _this = 21;
	static final int _true = 22;
	static final int _void = 23;
	static final int _colon = 24;
	static final int _comma = 25;
	static final int _dec = 26;
	static final int _dot = 27;
	static final int _inc = 28;
	static final int _lbrace = 29;
	static final int _lbrack = 30;
	static final int _lpar = 31;
	static final int _minus = 32;
	static final int _not = 33;
	static final int _plus = 34;
	static final int _rbrace = 35;
	static final int _rbrack = 36;
	static final int _rpar = 37;
	static final int _tilde = 38;
	static final int maxT = 46;

        static final boolean T = true;
        static final boolean x = false;
        static final int minErrDist = 2;

        public Token t;    // last recognized token
        public Token la;   // lookahead token
        int errDist = minErrDist;
        
        Scanner scanner;
        Errors errors;

        

        public Parser(Scanner scanner) {
                this.scanner = scanner;
                errors = new Errors();
        }

        void SynErr (int n) {
                if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
                errDist = 0;
        }

        public void SemErr (String msg) {
                if (errDist >= minErrDist) errors.Error(t.line, t.col, msg);
                errDist = 0;
        }
        
        void Get () {
                for (;;) {
                        t = la;
                        la = scanner.Scan();
                        if (la.kind <= maxT) { ++errDist; break; }

                        la = t;
                }
        }
        
        void Expect (int n) {
                if (la.kind==n) Get(); else { SynErr(n); }
        }
        
        boolean StartOf (int s) {
                return set[s][la.kind];
        }
        
        void ExpectWeak (int n, int follow) {
                if (la.kind == n) Get();
                else {
                        SynErr(n);
                        while (!StartOf(follow)) Get();
                }
        }
        
        boolean WeakSeparator (int n, int syFol, int repFol) {
                boolean[] s = new boolean[maxT+1];
                if (la.kind == n) { Get(); return true; }
                else if (StartOf(repFol)) return false;
                else {
                        for (int i=0; i <= maxT; i++) {
                                s[i] = set[syFol][i] || set[repFol][i] || set[0][i];
                        }
                        SynErr(n);
                        while (!s[la.kind]) Get();
                        return StartOf(syFol);
                }
        }
        
	Ac1Language Ac1() {
		Ac1Language ac1;
		ac1=new Ac1Language(); 
		while (la.kind == 1) {
			Get();
			String ruleId=t.val; 
			if (la.kind == 27) {
				Get();
				ac1.setTopLevelRuleId(ruleId); 
			} else if (la.kind == 31) {
				Ac1Rule rule=new Ac1Rule(ruleId); 
				RuleRest(rule);
				ac1.addRule(rule); 
			} else SynErr(47);
		}
		return ac1;
	}

	void RuleRest(Ac1Rule rule) {
		Ac1Args args; 
		Expect(31);
		args = formalArgs();
		Expect(37);
		Expect(24);
		LHSSequenceElement seq; 
		seq = LHSSequence();
		rule.setLHS(seq); rule.setFormalArgs(args); 
		Expect(27);
	}

	Ac1Args formalArgs() {
		Ac1Args args;
		args = args();
		return args;
	}

	LHSSequenceElement LHSSequence() {
		LHSSequenceElement e;
		LHSSeq s1=new LHSSeq();
		e=s1;
		LHSSequenceElement e1; 
		e1 = LHSSequenceElement();
		s1.addElem(e1); 
		while (StartOf(1)) {
			e1 = LHSSequenceElement();
			s1.addElem(e1); 
		}
		if (la.kind == 39) {
			Get();
			LHSSeqElementOr sout=new LHSSeqElementOr(); 
			LHSSequenceElement s2; 
			s2 = LHSSequence();
			sout.setElem1(s1); sout.setElem2(s2); e=sout; 
		}
		return e;
	}

	Ac1Args args() {
		Ac1Args args;
		args=new Ac1Args(); 
		Expect(1);
		args.getArgs().add(t.val); 
		while (la.kind == 25) {
			Get();
			Expect(1);
			args.getArgs().add(t.val); 
		}
		return args;
	}

	LHSSequenceElement LHSSequenceElement() {
		LHSSequenceElement e;
		LHSSequenceElement seq; e=null; 
		if (la.kind == 5) {
			e = WordLit();
		} else if (StartOf(2)) {
			e = RuleLabelReferenceOrWordClass();
		} else if (la.kind == 29) {
			Get();
			seq = LHSSequence();
			Expect(35);
			e=new Repeatable(seq); 
		} else if (la.kind == 30) {
			Get();
			seq = LHSSequence();
			Expect(36);
			e=new Deletable(seq); 
		} else SynErr(48);
		if (la.kind == 40) {
			Get();
			SemanticalAction sa; 
			sa = SemanticalAction();
			e.setSemanticalAction(sa); 
			Expect(41);
		}
		return e;
	}

	WordLit WordLit() {
		WordLit e;
		Expect(5);
		e=new WordLit(t.val); 
		return e;
	}

	LHSSequenceElement RuleLabelReferenceOrWordClass() {
		LHSSequenceElement e;
		List wordForms=null; 
		if (la.kind == 24) {
			Get();
			WordForm();
			wordForms=new ArrayList();
			wordForms.add(WordFormFactory.newInstance(t.val)); 
			while (la.kind == 25) {
				Get();
				WordForm();
				wordForms.add(WordFormFactory.newInstance(t.val)); 
			}
			Expect(24);
		}
		boolean cyc=false;
		boolean nart=false;
		String literal=null;
		int col=-1;
		int line=-1;
		String fileName=null; 
		if (la.kind == 1 || la.kind == 42) {
			if (la.kind == 42) {
				Get();
				cyc=true; 
			}
			Expect(1);
			literal=cyc?"#$"+t.val:t.val;
			col=t.col;
			line=t.line;
			fileName=scanner.buffer.fileName;
			nart=false; 
		} else if (la.kind == 43) {
			Get();
			Expect(5);
			literal=t.val.substring(1,t.val.length()-1);
			col=t.col;
			line=t.line;
			fileName=scanner.buffer.fileName;
			nart=true; 
		} else SynErr(49);
		Ac1Args args; 
		Expect(31);
		args = args();
		Expect(37);
		e=new RuleLabelReferenceOrWordClass(
		 wordForms,line,col,fileName,literal,nart,args); 
		return e;
	}

	SemanticalAction SemanticalAction() {
		SemanticalAction sa;
		sa=null; Expr e; 
		while (la.kind == 1 || la.kind == 5 || la.kind == 16) {
			e = Expr1();
			Expect(44);
			if(sa==null)sa=new SemanticalAction(); 
			sa.addStatement(new ExpressionStatement(e)); 
		}
		return sa;
	}

	void WordForm() {
		Expect(1);
	}

	Expr Expr1() {
		Expr e;
		e = Expr2();
		while (la.kind == 27) {
			Get();
			Expect(1);
			String memberName=t.val; List args; 
			Expect(31);
			args = ExprArgs();
			Expect(37);
			e=new MemberExpr(e,memberName,args); 
		}
		return e;
	}

	Expr Expr2() {
		Expr e;
		e = Expr3();
		while (la.kind == 45) {
			Get();
			Expr e2; 
			e2 = Expr3();
			e=new LinkExpr(e,e2); 
		}
		return e;
	}

	List ExprArgs() {
		List args;
		args=new ArrayList(1); Expr e; 
		e = Expr1();
		args.add(e); 
		while (la.kind == 25) {
			Get();
			e = Expr1();
			args.add(e); 
		}
		return args;
	}

	Expr Expr3() {
		Expr e;
		e=null; 
		if (la.kind == 1) {
			Get();
			e=new IdentExpr(t.val); 
		} else if (la.kind == 5) {
			Get();
			e=new StringLitExpr(t.val); 
		} else if (la.kind == 16) {
			Get();
			String className=null; boolean isNameable=false;
			String instanceName=null; 
			if (la.kind == 1) {
				Get();
				className=t.val; 
			} else if (la.kind == 5) {
				Get();
				className=t.val; isNameable=true; 
			} else SynErr(50);
			if (la.kind == 31) {
				Get();
				Expect(1);
				instanceName=t.val; 
				Expect(37);
			}
			e=new NewInstanceExpr(className,isNameable);
			if(instanceName!=null)
			  e=new AssignToNameExpr(instanceName,e); 
		} else SynErr(51);
		return e;
	}



        public Ac1Language Parse() {
                la = new Token();
                la.val = "";            
                Get();
                Ac1Language ac1=
		Ac1();

                Expect(0);
                scanner.buffer.Close();
                return ac1;
        }

        private boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,T,T,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x}

        };
} // end Parser


class Errors {
        public int count = 0;
        public String errMsgFormat = "-- line {0} col {1}: {2}";

        private void printMsg(int line, int column, String msg) {
                StringBuffer b = new StringBuffer(errMsgFormat);
                int pos = b.indexOf("{0}");
                if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
                pos = b.indexOf("{1}");
                if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
                pos = b.indexOf("{2}");
                if (pos >= 0) b.replace(pos, pos+3, msg);
                System.out.println(b.toString());
        }
        
        public void SynErr (int line, int col, int n) {
                        String s;
                        switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "intLit expected"; break;
			case 3: s = "floatLit expected"; break;
			case 4: s = "charLit expected"; break;
			case 5: s = "stringLit expected"; break;
			case 6: s = "boolean expected"; break;
			case 7: s = "byte expected"; break;
			case 8: s = "char expected"; break;
			case 9: s = "class expected"; break;
			case 10: s = "double expected"; break;
			case 11: s = "false expected"; break;
			case 12: s = "final expected"; break;
			case 13: s = "float expected"; break;
			case 14: s = "int expected"; break;
			case 15: s = "long expected"; break;
			case 16: s = "new expected"; break;
			case 17: s = "null expected"; break;
			case 18: s = "short expected"; break;
			case 19: s = "static expected"; break;
			case 20: s = "super expected"; break;
			case 21: s = "this expected"; break;
			case 22: s = "true expected"; break;
			case 23: s = "void expected"; break;
			case 24: s = "colon expected"; break;
			case 25: s = "comma expected"; break;
			case 26: s = "dec expected"; break;
			case 27: s = "dot expected"; break;
			case 28: s = "inc expected"; break;
			case 29: s = "lbrace expected"; break;
			case 30: s = "lbrack expected"; break;
			case 31: s = "lpar expected"; break;
			case 32: s = "minus expected"; break;
			case 33: s = "not expected"; break;
			case 34: s = "plus expected"; break;
			case 35: s = "rbrace expected"; break;
			case 36: s = "rbrack expected"; break;
			case 37: s = "rpar expected"; break;
			case 38: s = "tilde expected"; break;
			case 39: s = "\"|\" expected"; break;
			case 40: s = "\"(.\" expected"; break;
			case 41: s = "\".)\" expected"; break;
			case 42: s = "\"#$\" expected"; break;
			case 43: s = "\"NART\" expected"; break;
			case 44: s = "\";\" expected"; break;
			case 45: s = "\"->\" expected"; break;
			case 46: s = "??? expected"; break;
			case 47: s = "invalid Ac1"; break;
			case 48: s = "invalid LHSSequenceElement"; break;
			case 49: s = "invalid RuleLabelReferenceOrWordClass"; break;
			case 50: s = "invalid Expr3"; break;
			case 51: s = "invalid Expr3"; break;
                                default: s = "error " + n; break;
                        }
                        printMsg(line, col, s);
                        count++;
        }

        public void SemErr (int line, int col, int n) {
                printMsg(line, col, "error " + n);
                count++;
        }

        public void Error (int line, int col, String s) {       
                printMsg(line, col, s);
                count++;
        }

        public void Exception (String s) {
                System.out.println(s); 
                System.exit(1);
        }
} // Errors

