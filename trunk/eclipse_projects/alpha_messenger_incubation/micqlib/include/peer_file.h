/* $Id: peer_file.h,v 1.4 2003/10/03 20:17:18 kuhlmann Exp $ */

void PeerFileUser (UDWORD seq, Contact *cont, const char *reason, Connection *serv);
void PeerFileTO (Event *event);
