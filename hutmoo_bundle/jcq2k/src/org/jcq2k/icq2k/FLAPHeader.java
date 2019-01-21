package org.jcq2k.icq2k;

import org.jcq2k.*;
import org.jcq2k.util.*;
import org.jcq2k.util.joe.*;

/**
Simple datatype that represents a FLAP packet.
*/
public final class FLAPHeader
{
  public final int channel;
  public final int seqnum;
  public final int data_field_length;
  public final byte[] byteArray;
/**
 * FLAPHeader constructor comment.
 */
public FLAPHeader(byte[] flap_header) throws MessagingNetworkException
{
  Lang.ASSERT_NOT_NULL(flap_header, "flap_header");
  Lang.ASSERT_EQUAL(flap_header.length, 6, "flap_header.length", "6");
  //Command Start (byte: 0x2a)
  //Channel ID (byte)
  //Sequence Number (word)
  //Data Field Length (word)
  MLang.EXPECT_EQUAL(flap_header[0], 0x2a, "flap_header[0]", "flap header magic ('*')", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
  channel = 0xffff & (int) flap_header[1];
  seqnum = Session.aimutil_get16(flap_header, 2);
  data_field_length = Session.aimutil_get16(flap_header, 4);
  MLang.EXPECT(data_field_length >= 0 && data_field_length <= 32*1024, "data field length must be >= 0 && <= 32*1024, but it is "+data_field_length, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
  byteArray = flap_header;
}
/**
 * FLAPHeader constructor comment.
 */
public FLAPHeader(int channel, int seqnum, int data_field_length) throws MessagingNetworkException
{
  //Command Start (byte: 0x2a)
  //Channel ID (byte)
  //Sequence Number (word)
  //Data Field Length (word)
  MLang.EXPECT(data_field_length >= 0 && data_field_length <= 32 * 1024, "data field length must be >= 0 && <= 32*1024, but it is " + data_field_length, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
  this.data_field_length = data_field_length;
  this.seqnum = seqnum;
  this.channel = (byte) channel;
  byteArray = new byte[] {//
  (byte) 0x2a, (byte) channel, //
  (byte) ((seqnum >> 8) & 0xff), //
  (byte) ((seqnum) & 0xff), //
  (byte) ((data_field_length >> 8) & 0xff), //
  (byte) ((data_field_length) & 0xff) //
  };
}
}