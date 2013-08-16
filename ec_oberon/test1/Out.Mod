(** Output on the standard output *)
MODULE Out;

(** Int -- output an integer with specified width *)
PROCEDURE Int(n, w: INTEGER);
BEGIN END Int;

(** LongInt -- output a long integer with specified width *)
PROCEDURE LongInt(n: LONGINT; w: INTEGER);
BEGIN END LongInt;

(** Real -- output a real number in scientific notation *)
PROCEDURE Real(x: REAL);
BEGIN END Real;

(** LongReal -- output a long real in scientific notation *)
PROCEDURE LongReal(x: LONGREAL);
BEGIN END LongReal;

(** Fixed -- output a long real in fixed decimal notation *)
PROCEDURE Fixed(x: LONGREAL; width, dec: INTEGER);
BEGIN END Fixed;

(** Char -- output a character *)
PROCEDURE Char(c: CHAR);
BEGIN END Char;

(** String -- output a null-terminated string *)
PROCEDURE String(s: ARRAY OF CHAR);
BEGIN END String;

(** Ln -- output a newline *)
PROCEDURE Ln;
BEGIN END Ln;

END Out.
