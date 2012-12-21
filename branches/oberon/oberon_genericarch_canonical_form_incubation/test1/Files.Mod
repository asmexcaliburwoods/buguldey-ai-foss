(** General input-output on files *)
MODULE Files;

(** File -- type of open files *)
TYPE File = POINTER TO FileDesc;

(** SeekSet -- "whence" argument for Seek to set absolute postition *)
CONST SeekSet = 0;

(** SeekCur -- "whence" argument to set position relative to current pos *)
CONST SeekCur = 1;

(** SeekEnd -- "whence" argument to set position relative to end of file *)
CONST SeekEnd = 2;

(** stdin -- standard input file *)
VAR stdin: File;

(** stdout -- standard output file *)
VAR stdout: File;

(** stderr -- standard error file *)
VAR stderr: File;

(** Open -- open a file by name; return NIL if not found *)
PROCEDURE Open(name, mode: ARRAY OF CHAR): File;
BEGIN END Open;

(** FDOpen -- open a file given a file descriptor, or return NIL *)
PROCEDURE FDOpen(fd: INTEGER; mode: ARRAY OF CHAR): File;
BEGIN END FDOpen;

(** Close -- close a file *)
PROCEDURE Close(fp: File);
BEGIN END Close;

(** Eof -- test of end of file *)
PROCEDURE Eof(fp: File): BOOLEAN;
BEGIN END Eof;

(** Flush -- ensure buffered output has been written out *)
PROCEDURE Flush(fp: File);
BEGIN END Flush;

(** ReadChar -- read a character *)
PROCEDURE ReadChar(f: File; VAR c: CHAR);
BEGIN END ReadChar;

(** WriteInt -- output an integer with a specified width *)
PROCEDURE WriteInt(f: File; n, width: INTEGER);
BEGIN END WriteInt;

(** WriteLongInt -- output a long integer with specified width *)
PROCEDURE WriteLongInt(f: File; n: LONGINT; width: INTEGER);
BEGIN END WriteLongInt;

(** WriteReal -- output a real in scientific notation *)
PROCEDURE WriteReal(f: File; x: REAL);
BEGIN END WriteReal;

(** WriteLongReal -- output a long real in scientific notation *)
PROCEDURE WriteLongReal(f: File; x: LONGREAL);
BEGIN END WriteLongReal;

(** WriteFixed -- output a long real in fixed decimal notation *)
PROCEDURE WriteFixed(f: File; x: LONGREAL; width, dec: INTEGER);
BEGIN END WriteFixed;

(** WriteChar -- output a character *)
PROCEDURE WriteChar(f: File; c: CHAR);
BEGIN END WriteChar;

(** WriteString -- output a null-terminated string *)
PROCEDURE WriteString(f: File; s: ARRAY OF CHAR);
BEGIN END WriteString;

(** WriteLn -- output a newline *)
PROCEDURE WriteLn(f: File);
BEGIN END WriteLn;

(** Read -- read an arbitary binary object *)
PROCEDURE Read(f: File; VAR buf: ARRAY OF SYSTEM.BYTE);
BEGIN END Read;

(** Write -- write an arbitary binary object *)
PROCEDURE Write(f: File; VAR buf: ARRAY OF SYSTEM.BYTE);
BEGIN END Write;

(** Seek -- set the file pointer to a specified offset *)
PROCEDURE Seek(f: File; offset, whence: INTEGER);
BEGIN END Seek;

(** Tell -- return current file postion *)
PROCEDURE Tell(f: File): INTEGER;
BEGIN END Tell;

END Files.
