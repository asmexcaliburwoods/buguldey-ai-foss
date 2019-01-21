package org.jcq2k.icq2k;

/**
Used to define handlers for handling any particular
SNAC family/subtype.  When some handler is registered
for SNACs of type A subtype B, and the incoming stream contains
such SNAC packet, the handler is triggered to handle the SNAC.
*/
public interface RxHandler
{
	void triggered(Session sess, Command_rx_struct rxframe, SNAC snac) throws org.jcq2k.MessagingNetworkException, java.io.IOException;
}