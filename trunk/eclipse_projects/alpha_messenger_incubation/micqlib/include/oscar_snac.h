/* $Id: oscar_snac.h,v 1.2 2003/10/11 20:21:58 kuhlmann Exp $ */

#ifndef MICQ_OSCAR_SNAC_H
#define MICQ_OSCAR_SNAC_H

void SnacCallback (Event *event);
const char *SnacName (UWORD fam, UWORD cmd);
void SnacPrint (Packet *pak);

typedef void (jump_snac_f)(Event *);
typedef struct { UWORD fam; UWORD cmd; const char *name; jump_snac_f *f; } SNAC;
#define JUMP_SNAC_F(f) void f (Event *event)
#define SnacSend FlapSend

Packet *SnacC (Connection *serv, UWORD fam, UWORD cmd, UWORD flags, UDWORD ref);
jump_snac_f SnacSrvUnknown;

#endif
