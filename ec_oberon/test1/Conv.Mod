MODULE Conv;

(** IntVal -- convert string to integer *)
PROCEDURE IntVal(s: ARRAY OF CHAR): INTEGER;
BEGIN END IntVal;

(** LongIntVal -- convert string to long integer *)
PROCEDURE LongIntVal(s: ARRAY OF CHAR): LONGINT;
BEGIN END LongIntVal;

(** RealVal -- convert string to real *)
PROCEDURE RealVal(s: ARRAY OF CHAR): REAL;
BEGIN END RealVal;

(** LongRealVal -- convert string to long real *)
PROCEDURE LongRealVal(s: ARRAY OF CHAR): LONGREAL;
BEGIN END LongRealVal;

(** ConvInt -- convert integer to string and store in buffer *)
PROCEDURE ConvInt(n: INTEGER; VAR s: ARRAY OF CHAR);
BEGIN END ConvInt;

END Conv.
