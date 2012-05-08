/* $Id: server.h,v 1.19 2005/03/31 17:28:17 kuhlmann Exp $ */

#ifndef MICQ_IM_CLI_H
#define MICQ_IM_CLI_H

UBYTE IMCliMsg (Connection *conn, Contact *cont, Opt *opt);
UBYTE IMCliReMsg (Connection *conn, Contact *cont, Opt *opt); /* no log */
void IMCliInfo (Connection *conn, Contact *cont, int group);

#endif /* MICQ_IM_CLI_H */
