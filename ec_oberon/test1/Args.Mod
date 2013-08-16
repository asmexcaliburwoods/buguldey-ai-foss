(** Args -- access to program arguments and evironment *)
MODULE Args;

(** argc -- number of arguments *)
VAR argc-: INTEGER;

(** GetArg -- fetch an argument into a string buffer *)
PROCEDURE GetArg(n: INTEGER; VAR s: ARRAY OF CHAR);
BEGIN
END GetArg;

(** GetEnv -- fetch an environment variable into a string buffer *)
PROCEDURE GetEnv(name: ARRAY OF CHAR; VAR s: ARRAY OF CHAR);
BEGIN
END GetEnv;

BEGIN
END Args.
