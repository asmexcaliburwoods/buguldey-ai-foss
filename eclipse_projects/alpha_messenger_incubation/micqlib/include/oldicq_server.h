/* $Id: oldicq_server.h,v 1.1 2003/10/11 20:21:58 kuhlmann Exp $ */

#ifndef MICQ_ICQV5_SERVER_H
#define MICQ_ICQV5_SERVER_H

typedef void (jump_srv_f)(Connection *conn, Contact *cont, Packet *pak, UWORD cmd, UWORD ver, UDWORD seq);
#define JUMP_SRV_F(f) void f (Connection *conn, Contact *cont, Packet *pak, UWORD cmd, UWORD ver, UDWORD seq)

struct jumpsrvstr {
    int cmd;
    jump_srv_f *f;
    const char *cmdname;
};

typedef struct jumpsrvstr jump_srv_t;

const char *CmdPktSrvName (int cmd);
void CmdPktSrvRead (Connection *conn);
JUMP_SRV_F(CmdPktSrvProcess);

#endif /* MICQ_ICQV5_SERVER_H */
