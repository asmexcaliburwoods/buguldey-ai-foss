/* $Id: util.h,v 1.27 2003/10/11 20:21:58 kuhlmann Exp $ */

#ifndef MICQ_UTIL_H
#define MICQ_UTIL_H

int putlog (Connection *conn, time_t stamp, Contact *cont, 
            UDWORD status, enum logtype level, UWORD type, const char *str);

void EventExec (Contact *cont, const char *script, UBYTE type, UDWORD msgtype, const char *text);

#endif /* MICQ_UTIL_H */
