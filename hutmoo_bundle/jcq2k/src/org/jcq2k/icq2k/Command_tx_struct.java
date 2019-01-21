package org.jcq2k.icq2k;

/**
Simple datatype that represents an outcoming packet.
*/
public class Command_tx_struct
{
	short hdrtype;
	/* defines which piece of the union to use */
	//union
	//{
	//struct
	//{
	short hdr_oscar_type;
	int hdr_oscar_seqnum; //word
	//}
	//oscar;
	//struct
	//{
	int hdr_oft_type;
	/* ODC2 OFT2 */
	byte[] hdr_oft_magic; //byte[4]
	/* rest of bloated header */
	byte[] hdr_oft_hdr2;
	/* packet data (from 7 byte on) */
	byte[] data;
	/* 0 = free data on purge, 1 = only unlink */
	//byte nofree;
	/* the connection it came in on... */
	Aim_conn_t conn;
/**
 * Command_rx_struct constructor comment.
 */
public Command_tx_struct() {
	super();
}
}