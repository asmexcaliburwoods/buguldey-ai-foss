package org.jcq2k.icq2k;

/**
Simple datatype that represents an incoming packet.
*/
public class Command_rx_struct
{
	short hdrtype;
	/* defines which piece of the union to use */
	//union
	//{
	//struct
	//{
	byte hdr_oscar_type;
	int hdr_oscar_seqnum; //byte[4]
	//}
	//oscar;
	//struct
	//{
	int hdr_oft_type;
	/* ODC2 OFT2 */
	String hdr_oft_magic; //byte[4]
	/* rest of bloated header */
	byte[] hdr_oft_hdr2;
	/* packet data (from 7 byte on) */
	byte[] data;
	/* 0 = free data on purge, 1 = only unlink */
	//byte nofree; //C refcounting stuff
	/* the connection it came in on... */
	Aim_conn_t conn;
/**
 * Command_rx_struct constructor comment.
 */
public Command_rx_struct() {
	super();
}
}