package org.autocoder.ac1;

import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;

class Token {
        public int kind;    // token kind
        public int pos;     // token position in the source text (starting at 0)
        public int col;     // token column (starting at 0)
        public int line;    // token line (starting at 1)
        public String val;  // token value
        public Token next;  // ML 2005-03-11 Peek tokens are kept in linked list
}

class Buffer {
        public static final char EOF = (char) 256;
        static final int MAX_BUFFER_LENGTH = 64 * 1024; // 64KB
        byte[] buf;   // input buffer
        int bufStart; // position of first byte in buffer relative to input stream
        int bufLen;   // length of buffer
        int fileLen;  // length of input stream
        int pos;      // current position in buffer
        RandomAccessFile file; // input stream (seekable)
        public final String fileName;

        public Buffer(InputStream s) {
                fileName=s.toString();
                try {
                        fileLen = bufLen = s.available();
                        buf = new byte[bufLen];
                        s.read(buf, 0, bufLen);
                        pos = 0;
                        bufStart = 0;
                } catch (IOException e){
                        System.out.println("--- error on filling the buffer ");
                        System.exit(1);
                }
        }

        public Buffer(String fileName) {
                this.fileName=fileName;
                try {
                        file = new RandomAccessFile(fileName, "r");
                        fileLen = bufLen = (int) file.length();
                        if (bufLen > MAX_BUFFER_LENGTH) bufLen = MAX_BUFFER_LENGTH;
                        buf = new byte[bufLen];
                        bufStart = Integer.MAX_VALUE; // nothing in buffer so far
                        setPos(0); // setup buffer to position 0 (start)
                        if (bufLen == fileLen) Close();
                } catch (IOException e) {
                        System.out.println("--- could not open file " + fileName);
                        System.exit(1);
                }
        }
        
        protected void finalize() throws Throwable {
                super.finalize();
                Close();
        }

        public void Close() {
                if (file != null) {
                        try {
                                file.close();
                                file = null;
                        } catch (IOException e) {
                                e.printStackTrace(); System.exit(1);
                        }
                }
        }

        public int Read() {
                if (pos < bufLen) {
                        return buf[pos++] & 0xff;  // mask out sign bits
                } else if (getPos() < fileLen) {
                        setPos(getPos());         // shift buffer start to pos
                        return buf[pos++] & 0xff; // mask out sign bits
                } else {
                        return EOF;
                }
        }

        public int Peek() {
                if (pos < bufLen) {
                        return buf[pos] & 0xff;  // mask out sign bits
                } else if (getPos() < fileLen) {
                        setPos(getPos());       // shift buffer start to pos
                        return buf[pos] & 0xff; // mask out sign bits
                } else {
                        return EOF;
                }
        }

        public String GetString(int beg, int end) {
            int len = end - beg;
            char[] buf = new char[len];
            int oldPos = getPos();
            setPos(beg);
            for (int i = 0; i < len; ++i) buf[i] = (char) Read();
            setPos(oldPos);
            return new String(buf);
        }

        public int getPos() {
                return pos + bufStart;
        }

        public void setPos(int value) {
                if (value < 0) value = 0;
                else if (value > fileLen) value = fileLen;
                if (value >= bufStart && value < bufStart + bufLen) { // already in buffer
                        pos = value - bufStart;
                } else if (file != null) { // must be swapped in
                        try {
                                file.seek(value);
                                bufLen = file.read(buf);
                                bufStart = value; pos = 0;
                        } catch(IOException e) {
                                e.printStackTrace(); System.exit(1);
                        }
                } else {
                        pos = fileLen - bufStart; // make getPos() return fileLen
                }
        }

}

public class Scanner {
        static final char EOL = '\n';
        static final int  eofSym = 0;
	static final int charSetSize = 256;
	static final int maxT = 46;
	static final int noSym = 46;
	short[] start = {
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0, 41, 27, 57,  1,  0,  0, 18, 63, 44,  0, 48, 36, 62, 61,  0,
	 46, 47, 47, 47, 47, 47, 47, 47, 47, 47, 35, 59,  0,  0,  0,  0,
	  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 40,  0, 43,  0,  1,
	  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 39, 54, 42, 45,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  -1};


        public Buffer buffer; // scanner buffer

        Token t;         // current token
        char ch;         // current input character
        int pos;         // column number of current character
        int line;        // line number of current character
        int lineStart;   // start position of current line
        int oldEols;     // EOLs that appeared in a comment;
        BitSet ignore;   // set of characters to be ignored by the scanner

        Token tokens;    // list of tokens already peeked (first token is a dummy)
        Token pt;        // current peek token
        
        char[] tokenText = new char[16]; // token text used in NextToken(), dynamically enlarged
        
        public Scanner (String fileName) {
                buffer = new Buffer(fileName);
                Init();
        }
        
        public Scanner(InputStream s) {
                buffer = new Buffer(s);
                Init();
        }
        
        void Init () {
                pos = -1; line = 1; lineStart = 0;
                oldEols = 0;
                NextCh();
                ignore = new BitSet(charSetSize+1);
                ignore.set(' '); // blanks are always white space
		ignore.set(9); ignore.set(10); ignore.set(13); 
                pt = tokens = new Token();  // first token is a dummy
        }
        
        void NextCh() {
                if (oldEols > 0) { ch = EOL; oldEols--; } 
                else {
                        ch = (char)buffer.Read(); pos++;
                        // replace isolated '\r' by '\n' in order to make
                        // eol handling uniform across Windows, Unix and Mac
                        if (ch == '\r' && buffer.Peek() != '\n') ch = EOL;
                        if (ch == EOL) { line++; lineStart = pos + 1; }
                }

        }
        

	boolean Comment0() {
		int level = 1, line0 = line, lineStart0 = lineStart;
		NextCh();
		if (ch == '/') {
			NextCh();
			for(;;) {
				if (ch == 10) {
					level--;
					if (level == 0) { oldEols = line - line0; NextCh(); return true; }
					NextCh();
				} else if (ch == Buffer.EOF) return false;
				else NextCh();
			}
		} else {
			if (ch==EOL) {line--; lineStart = lineStart0;}
			pos = pos - 2; buffer.setPos(pos + 1); NextCh();
		}
		return false;
	}

	boolean Comment1() {
		int level = 1, line0 = line, lineStart0 = lineStart;
		NextCh();
		if (ch == '*') {
			NextCh();
			for(;;) {
				if (ch == '*') {
					NextCh();
					if (ch == '/') {
						level--;
						if (level == 0) { oldEols = line - line0; NextCh(); return true; }
						NextCh();
					}
				} else if (ch == Buffer.EOF) return false;
				else NextCh();
			}
		} else {
			if (ch==EOL) {line--; lineStart = lineStart0;}
			pos = pos - 2; buffer.setPos(pos + 1); NextCh();
		}
		return false;
	}

        
        void CheckLiteral() {
                String lit = t.val;
		if (lit.compareTo("boolean") == 0) t.kind = 6;
		else if (lit.compareTo("byte") == 0) t.kind = 7;
		else if (lit.compareTo("char") == 0) t.kind = 8;
		else if (lit.compareTo("class") == 0) t.kind = 9;
		else if (lit.compareTo("double") == 0) t.kind = 10;
		else if (lit.compareTo("false") == 0) t.kind = 11;
		else if (lit.compareTo("final") == 0) t.kind = 12;
		else if (lit.compareTo("float") == 0) t.kind = 13;
		else if (lit.compareTo("int") == 0) t.kind = 14;
		else if (lit.compareTo("long") == 0) t.kind = 15;
		else if (lit.compareTo("new") == 0) t.kind = 16;
		else if (lit.compareTo("null") == 0) t.kind = 17;
		else if (lit.compareTo("short") == 0) t.kind = 18;
		else if (lit.compareTo("static") == 0) t.kind = 19;
		else if (lit.compareTo("super") == 0) t.kind = 20;
		else if (lit.compareTo("this") == 0) t.kind = 21;
		else if (lit.compareTo("true") == 0) t.kind = 22;
		else if (lit.compareTo("void") == 0) t.kind = 23;
		else if (lit.compareTo("NART") == 0) t.kind = 43;
        }

        Token NextToken() {
                while (ignore.get(ch)) NextCh();
		if (ch == '/' && Comment0() ||ch == '/' && Comment1()) return NextToken();
                t = new Token();
                t.pos = pos; t.col = pos - lineStart + 1; t.line = line; 
                int state = start[ch];
                char[] tval = tokenText; // local variables are more efficient
                int tlen = 0;
		tval[tlen++] = ch; NextCh();
                
                boolean done = false;
                while (!done) {
                        if (tlen >= tval.length) {
                                char[] newBuf = new char[2 * tval.length];
                                System.arraycopy(tval, 0, newBuf, 0, tval.length);
                                tokenText = tval = newBuf;
                        }
                        switch (state) {
                                case -1: { t.kind = eofSym; done = true; break; } // NextCh already done 
                                case 0: { t.kind = noSym; done = true; break; }   // NextCh already done
				case 1:
					if ((ch == '$' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z')) {tval[tlen++] = ch; NextCh(); state = 1; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 2:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 3; break;}
					else {t.kind = noSym; done = true; break;}
				case 3:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 3; break;}
					else if ((ch == 'L' || ch == 'l')) {tval[tlen++] = ch; NextCh(); state = 4; break;}
					else {t.kind = 2; done = true; break;}
				case 4:
					{t.kind = 2; done = true; break;}
				case 5:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 5; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else if ((ch == 'E' || ch == 'e')) {tval[tlen++] = ch; NextCh(); state = 6; break;}
					else {t.kind = 3; done = true; break;}
				case 6:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 8; break;}
					else if ((ch == '+' || ch == '-')) {tval[tlen++] = ch; NextCh(); state = 7; break;}
					else {t.kind = noSym; done = true; break;}
				case 7:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 8; break;}
					else {t.kind = noSym; done = true; break;}
				case 8:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 8; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else {t.kind = 3; done = true; break;}
				case 9:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 9; break;}
					else if (ch == '.') {tval[tlen++] = ch; NextCh(); state = 10; break;}
					else if ((ch == 'E' || ch == 'e')) {tval[tlen++] = ch; NextCh(); state = 14; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else {t.kind = noSym; done = true; break;}
				case 10:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 10; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else if ((ch == 'E' || ch == 'e')) {tval[tlen++] = ch; NextCh(); state = 11; break;}
					else {t.kind = 3; done = true; break;}
				case 11:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 13; break;}
					else if ((ch == '+' || ch == '-')) {tval[tlen++] = ch; NextCh(); state = 12; break;}
					else {t.kind = noSym; done = true; break;}
				case 12:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 13; break;}
					else {t.kind = noSym; done = true; break;}
				case 13:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 13; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else {t.kind = 3; done = true; break;}
				case 14:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 16; break;}
					else if ((ch == '+' || ch == '-')) {tval[tlen++] = ch; NextCh(); state = 15; break;}
					else {t.kind = noSym; done = true; break;}
				case 15:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 16; break;}
					else {t.kind = noSym; done = true; break;}
				case 16:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 16; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else {t.kind = 3; done = true; break;}
				case 17:
					{t.kind = 3; done = true; break;}
				case 18:
					if ((ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 255)) {tval[tlen++] = ch; NextCh(); state = 19; break;}
					else if (ch == 92) {tval[tlen++] = ch; NextCh(); state = 20; break;}
					else {t.kind = noSym; done = true; break;}
				case 19:
					if (ch == 39) {tval[tlen++] = ch; NextCh(); state = 26; break;}
					else {t.kind = noSym; done = true; break;}
				case 20:
					if ((ch >= '0' && ch <= '3')) {tval[tlen++] = ch; NextCh(); state = 49; break;}
					else if ((ch >= '4' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 25; break;}
					else if ((ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't')) {tval[tlen++] = ch; NextCh(); state = 19; break;}
					else if (ch == 'u') {tval[tlen++] = ch; NextCh(); state = 21; break;}
					else {t.kind = noSym; done = true; break;}
				case 21:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 22; break;}
					else if (ch == 'u') {tval[tlen++] = ch; NextCh(); state = 21; break;}
					else {t.kind = noSym; done = true; break;}
				case 22:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 23; break;}
					else {t.kind = noSym; done = true; break;}
				case 23:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 24; break;}
					else {t.kind = noSym; done = true; break;}
				case 24:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 19; break;}
					else {t.kind = noSym; done = true; break;}
				case 25:
					if ((ch >= '0' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 19; break;}
					else if (ch == 39) {tval[tlen++] = ch; NextCh(); state = 26; break;}
					else {t.kind = noSym; done = true; break;}
				case 26:
					{t.kind = 4; done = true; break;}
				case 27:
					if ((ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 255)) {tval[tlen++] = ch; NextCh(); state = 27; break;}
					else if (ch == '"') {tval[tlen++] = ch; NextCh(); state = 34; break;}
					else if (ch == 92) {tval[tlen++] = ch; NextCh(); state = 28; break;}
					else {t.kind = noSym; done = true; break;}
				case 28:
					if ((ch >= '0' && ch <= '3')) {tval[tlen++] = ch; NextCh(); state = 51; break;}
					else if ((ch >= '4' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 33; break;}
					else if ((ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't')) {tval[tlen++] = ch; NextCh(); state = 27; break;}
					else if (ch == 'u') {tval[tlen++] = ch; NextCh(); state = 29; break;}
					else {t.kind = noSym; done = true; break;}
				case 29:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 30; break;}
					else if (ch == 'u') {tval[tlen++] = ch; NextCh(); state = 29; break;}
					else {t.kind = noSym; done = true; break;}
				case 30:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 31; break;}
					else {t.kind = noSym; done = true; break;}
				case 31:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 32; break;}
					else {t.kind = noSym; done = true; break;}
				case 32:
					if ((ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')) {tval[tlen++] = ch; NextCh(); state = 27; break;}
					else {t.kind = noSym; done = true; break;}
				case 33:
					if ((ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 255)) {tval[tlen++] = ch; NextCh(); state = 27; break;}
					else if (ch == '"') {tval[tlen++] = ch; NextCh(); state = 34; break;}
					else if (ch == 92) {tval[tlen++] = ch; NextCh(); state = 28; break;}
					else {t.kind = noSym; done = true; break;}
				case 34:
					{t.kind = 5; done = true; break;}
				case 35:
					{t.kind = 24; done = true; break;}
				case 36:
					{t.kind = 25; done = true; break;}
				case 37:
					{t.kind = 26; done = true; break;}
				case 38:
					{t.kind = 28; done = true; break;}
				case 39:
					{t.kind = 29; done = true; break;}
				case 40:
					{t.kind = 30; done = true; break;}
				case 41:
					{t.kind = 33; done = true; break;}
				case 42:
					{t.kind = 35; done = true; break;}
				case 43:
					{t.kind = 36; done = true; break;}
				case 44:
					{t.kind = 37; done = true; break;}
				case 45:
					{t.kind = 38; done = true; break;}
				case 46:
					if ((ch >= '0' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 53; break;}
					else if ((ch >= '8' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 9; break;}
					else if ((ch == 'L' || ch == 'l')) {tval[tlen++] = ch; NextCh(); state = 4; break;}
					else if ((ch == 'X' || ch == 'x')) {tval[tlen++] = ch; NextCh(); state = 2; break;}
					else if (ch == '.') {tval[tlen++] = ch; NextCh(); state = 10; break;}
					else if ((ch == 'E' || ch == 'e')) {tval[tlen++] = ch; NextCh(); state = 14; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else {t.kind = 2; done = true; break;}
				case 47:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 47; break;}
					else if ((ch == 'L' || ch == 'l')) {tval[tlen++] = ch; NextCh(); state = 4; break;}
					else if (ch == '.') {tval[tlen++] = ch; NextCh(); state = 10; break;}
					else if ((ch == 'E' || ch == 'e')) {tval[tlen++] = ch; NextCh(); state = 14; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else {t.kind = 2; done = true; break;}
				case 48:
					if (ch == '+') {tval[tlen++] = ch; NextCh(); state = 38; break;}
					else {t.kind = 34; done = true; break;}
				case 49:
					if ((ch >= '0' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 50; break;}
					else if (ch == 39) {tval[tlen++] = ch; NextCh(); state = 26; break;}
					else {t.kind = noSym; done = true; break;}
				case 50:
					if ((ch >= '0' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 19; break;}
					else if (ch == 39) {tval[tlen++] = ch; NextCh(); state = 26; break;}
					else {t.kind = noSym; done = true; break;}
				case 51:
					if ((ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 255)) {tval[tlen++] = ch; NextCh(); state = 27; break;}
					else if ((ch >= '0' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 52; break;}
					else if (ch == '"') {tval[tlen++] = ch; NextCh(); state = 34; break;}
					else if (ch == 92) {tval[tlen++] = ch; NextCh(); state = 28; break;}
					else {t.kind = noSym; done = true; break;}
				case 52:
					if ((ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 255)) {tval[tlen++] = ch; NextCh(); state = 27; break;}
					else if (ch == '"') {tval[tlen++] = ch; NextCh(); state = 34; break;}
					else if (ch == 92) {tval[tlen++] = ch; NextCh(); state = 28; break;}
					else {t.kind = noSym; done = true; break;}
				case 53:
					if ((ch >= '0' && ch <= '7')) {tval[tlen++] = ch; NextCh(); state = 53; break;}
					else if ((ch >= '8' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 9; break;}
					else if ((ch == 'L' || ch == 'l')) {tval[tlen++] = ch; NextCh(); state = 4; break;}
					else if (ch == '.') {tval[tlen++] = ch; NextCh(); state = 10; break;}
					else if ((ch == 'E' || ch == 'e')) {tval[tlen++] = ch; NextCh(); state = 14; break;}
					else if ((ch == 'D' || ch == 'F' || ch == 'd' || ch == 'f')) {tval[tlen++] = ch; NextCh(); state = 17; break;}
					else {t.kind = 2; done = true; break;}
				case 54:
					{t.kind = 39; done = true; break;}
				case 55:
					{t.kind = 40; done = true; break;}
				case 56:
					{t.kind = 41; done = true; break;}
				case 57:
					if (ch == '$') {tval[tlen++] = ch; NextCh(); state = 58; break;}
					else {t.kind = noSym; done = true; break;}
				case 58:
					{t.kind = 42; done = true; break;}
				case 59:
					{t.kind = 44; done = true; break;}
				case 60:
					{t.kind = 45; done = true; break;}
				case 61:
					if ((ch >= '0' && ch <= '9')) {tval[tlen++] = ch; NextCh(); state = 5; break;}
					else if (ch == ')') {tval[tlen++] = ch; NextCh(); state = 56; break;}
					else {t.kind = 27; done = true; break;}
				case 62:
					if (ch == '-') {tval[tlen++] = ch; NextCh(); state = 37; break;}
					else if (ch == '>') {tval[tlen++] = ch; NextCh(); state = 60; break;}
					else {t.kind = 32; done = true; break;}
				case 63:
					if (ch == '.') {tval[tlen++] = ch; NextCh(); state = 55; break;}
					else {t.kind = 31; done = true; break;}

                        }
                }
                t.val = new String(tval, 0, tlen);
                return t;
        }
        
        // get the next token (possibly a token already seen during peeking)
        public Token Scan () {
                if (tokens.next == null) {
                        return NextToken();
                } else {
                        pt = tokens = tokens.next;
                        return tokens;
                }
        }

        // get the next token, ignore pragmas
        public Token Peek () {
                if (pt.next == null) {
                        do {
                                pt = pt.next = NextToken();
                        } while (pt.kind > maxT); // skip pragmas
                } else {
                        do {
                                pt = pt.next;
                        } while (pt.kind > maxT);
                }
                return pt;
        }

        // make sure that peeking starts at current scan position
        public void ResetPeek () { pt = tokens; }

} // end Scanner

