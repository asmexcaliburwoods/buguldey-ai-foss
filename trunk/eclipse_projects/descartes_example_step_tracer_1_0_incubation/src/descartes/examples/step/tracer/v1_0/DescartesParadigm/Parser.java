package descartes.examples.step.tracer.v1_0.DescartesParadigm;

import descartes.examples.step.tracer.v1_0.DescartesParadigm.ast.DescartesParadigmFileASTImpl;

public class Parser {
	static final int _EOF = 0;
	static final int _ident = 1;
	static final int _number = 2;
	static final int _string = 3;
	static final int _badString = 4;
	static final int _char = 5;
	static final int maxT = 39;

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
	
	DescartesParadigmFileASTImpl DescartesParadigm() {
		DescartesParadigmFileASTImpl ast;
		ast=null; 
		Expect(6);
		Expect(7);
		Expect(8);
		Expect(9);
		Expect(10);
		Expect(11);
		Expect(12);
		Expect(7);
		Expect(8);
		Expect(9);
		Expect(12);
		Expect(13);
		Expect(12);
		Expect(14);
		Expect(11);
		Expect(15);
		Expect(7);
		Expect(8);
		Expect(9);
		Expect(16);
		Expect(11);
		Expect(6);
		Expect(7);
		Expect(8);
		Expect(7);
		Expect(12);
		Expect(7);
		Expect(8);
		Expect(7);
		Expect(15);
		Expect(11);
		Expect(6);
		Expect(9);
		Expect(8);
		Expect(9);
		Expect(12);
		Expect(9);
		Expect(8);
		Expect(9);
		Expect(15);
		Expect(11);
		while (StartOf(1)) {
			if (la.kind == 6) {
				Get();
				Expect(7);
				Expect(8);
				Expect(9);
				Expect(17);
				Expect(13);
				inner_paradigm_id_declaration();
				Expect(14);
				Expect(11);
				Expect(12);
				Expect(7);
				Expect(8);
				Expect(9);
				Expect(17);
				Expect(13);
				etc_paradigm_id_declaration();
				Expect(14);
				Expect(11);
				Expect(15);
				Expect(7);
				Expect(8);
				Expect(9);
				Expect(17);
				Expect(13);
				outer_paradigm_id_declaration();
				Expect(14);
				Expect(11);
			} else if (la.kind == 17) {
				Get();
				Expect(13);
				paradigm_id_reference();
				Expect(14);
				Expect(18);
				Expect(3);
				Expect(11);
			} else if (la.kind == 19) {
				Get();
				Expect(13);
				Expect(17);
				Expect(13);
				paradigm_id_reference();
				Expect(14);
				Expect(14);
				Expect(18);
				weight();
				Expect(11);
			} else if (la.kind == 20) {
				Get();
				Expect(10);
				Expect(21);
				Expect(12);
				Expect(21);
				Expect(16);
				Expect(22);
				Expect(19);
				Expect(13);
				Expect(17);
				Expect(13);
				paradigm_id_reference();
				Expect(14);
				Expect(14);
				Expect(21);
				Expect(19);
				Expect(13);
				Expect(17);
				Expect(13);
				paradigm_id_reference();
				Expect(14);
				Expect(14);
				Expect(21);
				Expect(19);
				Expect(13);
				Expect(17);
				Expect(13);
				paradigm_id_reference();
				Expect(14);
				Expect(14);
				Expect(18);
				weight();
				Expect(21);
				weight();
				Expect(21);
				weight();
				Expect(11);
			} else {
				Get();
				Expect(13);
				Expect(17);
				Expect(13);
				etc_paradigm_id_reference();
				Expect(14);
				Expect(24);
				Expect(17);
				Expect(13);
				etc_subparadigm_id_reference();
				Expect(14);
				Expect(11);
				Expect(17);
				Expect(13);
				outer_paradigm_id_reference();
				Expect(14);
				Expect(24);
				Expect(17);
				Expect(13);
				outer_subparadigm_id_reference();
				Expect(14);
				Expect(11);
				Expect(14);
				Expect(11);
			}
		}
		Expect(13);
		Expect(25);
		Expect(26);
		Expect(27);
		Expect(14);
		Expect(11);
		Expect(6);
		Expect(24);
		Expect(28);
		Expect(29);
		Expect(25);
		Expect(30);
		Expect(31);
		Expect(25);
		Expect(28);
		Expect(32);
		Expect(33);
		Expect(34);
		Expect(11);
		Expect(35);
		Expect(36);
		Expect(37);
		Expect(13);
		Expect(28);
		Expect(29);
		Expect(25);
		Expect(30);
		Expect(31);
		Expect(25);
		Expect(28);
		Expect(32);
		Expect(33);
		Expect(34);
		Expect(14);
		Expect(11);
		return ast;
	}

	void inner_paradigm_id_declaration() {
		paradigm_id_declaration();
	}

	void etc_paradigm_id_declaration() {
		paradigm_id_declaration();
	}

	void outer_paradigm_id_declaration() {
		paradigm_id_declaration();
	}

	void paradigm_id_reference() {
		paradigm_id();
	}

	void weight() {
		if (la.kind == 38) {
			Get();
		} else if (la.kind == 3) {
			Get();
		} else if (la.kind == 2) {
			Get();
			if (la.kind == 3) {
				Get();
			}
		} else SynErr(40);
	}

	void etc_paradigm_id_reference() {
		paradigm_id_reference();
	}

	void etc_subparadigm_id_reference() {
		etc_paradigm_id_reference();
	}

	void outer_paradigm_id_reference() {
		paradigm_id_reference();
	}

	void outer_subparadigm_id_reference() {
		outer_paradigm_id_reference();
	}

	void paradigm_id_declaration() {
		paradigm_id();
	}

	void paradigm_id() {
		Expect(3);
	}



	public DescartesParadigmFileASTImpl Parse() {
		la = new Token();
		la.val = "";		
		Get();
		DescartesParadigmFileASTImpl ast=
		DescartesParadigm();

		Expect(0);
		return ast;
	}

	private boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,T,x,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x}

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
			case 2: s = "number expected"; break;
			case 3: s = "string expected"; break;
			case 4: s = "badString expected"; break;
			case 5: s = "char expected"; break;
			case 6: s = "\"INNER\" expected"; break;
			case 7: s = "\"\u00e2\u0086\u0090\" expected"; break;
			case 8: s = "\"\u00e2\u0080\u00a2\" expected"; break;
			case 9: s = "\"\u00e2\u0086\u0092\" expected"; break;
			case 10: s = "\"+1\" expected"; break;
			case 11: s = "\".\" expected"; break;
			case 12: s = "\"ETC\" expected"; break;
			case 13: s = "\"(\" expected"; break;
			case 14: s = "\")\" expected"; break;
			case 15: s = "\"OUTER\" expected"; break;
			case 16: s = "\"-1\" expected"; break;
			case 17: s = "\"paradigm\" expected"; break;
			case 18: s = "\"=\" expected"; break;
			case 19: s = "\"weight\" expected"; break;
			case 20: s = "\"ratio\" expected"; break;
			case 21: s = "\"/\" expected"; break;
			case 22: s = "\":\" expected"; break;
			case 23: s = "\"pair\" expected"; break;
			case 24: s = "\"includes\" expected"; break;
			case 25: s = "\"and\" expected"; break;
			case 26: s = "\"so\" expected"; break;
			case 27: s = "\"on\" expected"; break;
			case 28: s = "\"the\" expected"; break;
			case 29: s = "\"above\" expected"; break;
			case 30: s = "\"this\" expected"; break;
			case 31: s = "\"line\" expected"; break;
			case 32: s = "\"below\" expected"; break;
			case 33: s = "\"formal\" expected"; break;
			case 34: s = "\"description\" expected"; break;
			case 35: s = "\"DO\" expected"; break;
			case 36: s = "\"CALL\" expected"; break;
			case 37: s = "\"COINTERPRET\" expected"; break;
			case 38: s = "\"?\" expected"; break;
			case 39: s = "??? expected"; break;
			case 40: s = "invalid weight"; break;
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

