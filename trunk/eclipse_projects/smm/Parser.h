
#if !defined(smm_COCO_PARSER_H)
#define smm_COCO_PARSER_H


#include "Scanner.h"

/* namespace smm {
 */
class Errors {
public:
	int count;			// number of errors detected

	Errors();
	void SynErr(int line, int col, int n);
	void Error(int line, int col, const wchar_t *s);
	void Warning(int line, int col, const wchar_t *s);
	void Warning(const wchar_t *s);
	void Exception(const wchar_t *s);

}; // Errors

class Parser {
private:
	enum {
		_EOF=0,
		_symbol=1,
		_index=2,
		_fileName=3
	};
	int maxT;

	Token *dummyToken;
	int errDist;
	int minErrDist;

	void SynErr(int n);
	void Get();
	void Expect(int n);
	bool StartOf(int s);
	void ExpectWeak(int n, int follow);
	bool WeakSeparator(int n, int syFol, int repFol);

public:
	Scanner *scanner;
	Errors  *errors;

	Token *t;			// last recognized token
	Token *la;			// lookahead token

typedef bool boolean;
	


	Parser(Scanner *scanner, Errors* errors_);
	~Parser();
	void SemErr(const wchar_t* msg);

	void smm();
	void SOURCE();
	void CONSTANT();
	void ASSERTION();
	void INCLUSION();
	void VARIABLE();
	void DISJOINTED();
	void ESSENTIAL();
	void FLOATING();
	void AXIOMATIC();
	void PROVABLE();
	void PROOF();
	void PREFIX();

	void Parse();

}; // end Parser

/* } // namespace
 */

#endif

