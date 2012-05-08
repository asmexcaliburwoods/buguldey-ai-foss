/* $Id: util_table.h,v 1.7 2002/10/10 23:40:16 kuhlmann Exp $ */

#ifndef MICQ_UTIL_TABLE
#define MICQ_UTIL_TABLE

const char *TableGetMonth (int code);
const char *TableGetLang (UBYTE code);
void        TablePrintLang (void);
const char *TableGetCountry (UWORD code);
const char *TableGetAffiliation (UWORD code);
const char *TableGetPast (UWORD code);
const char *TableGetOccupation (UWORD code);
const char *TableGetInterest (UWORD code);

#endif /* MICQ_UTIL_TABLE */
