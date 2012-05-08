MODULE MahaBasic;

IMPORT Integer32:=SYSTEM.Integer32;

TYPE Color* = Integer32;

VAR cmos_theSetScreenColor: Color;

PROCEDURE SaveScreen();
BEGIN
END SaveScreen; 

PROCEDURE SetScreen*(aColor: Color);
BEGIN
	cmos_theSetScreenColor := aColor;
END SetScreen; 

PROCEDURE GoFullScreen();
BEGIN
END GoFullScreen; 

PROCEDURE PrintPrompt();
BEGIN
END PrintPrompt; 

PROCEDURE ReadKey();
BEGIN 
END ReadKey; 

PROCEDURE RestoreScreen();
BEGIN 
END RestoreScreen; 

CONST 
  SUCCESS=0;
  ERR_XInternAtom_MOTIF_WM_HINTS_FAILED=1;
  ERR_XOpenDisplay_FAILED=2;

PROCEDURE go_full_screen_and_print_prompt_and_readkey() : INTEGER;
BEGIN
END go_full_screen_and_print_prompt_and_readkey;

BEGIN
(*  SaveScreen; 
  SetScreen(Color_blue); 
  GoFullScreen; 
  PrintPrompt; 
  ReadKey; 
  RestoreScreen;
*)
  go_full_screen_and_print_prompt_and_readkey   
END MahaBasic.