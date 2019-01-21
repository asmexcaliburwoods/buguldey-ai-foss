package org.jcq2k.icq2k;

import org.jcq2k.*;
import org.jcq2k.util.*;
import org.jcq2k.util.joe.*;

/**
Provides functionality for working with
lists of TLV (Type/Length/Value) tuples, and their
byte array representation.
<p>
As the input, takes a byte array which represents
several TLVs concatenated.
<p>
See {@link Aim_tlv_t} type and
<a href=http://www.zigamorph.net/faim/protocol/>the OSCAR
protocol documentation</a> for more info on TLVs.
*/
public class Aim_tlvlist_t
{
  private java.util.Vector elements = new java.util.Vector();
/**
 * Aim_tlvlist_t constructor comment.
 */
public Aim_tlvlist_t() {
  super();
}
/**
 * Aim_tlvlist_t constructor comment.
 */
public Aim_tlvlist_t(Session ses, byte[] data) throws MessagingNetworkException
{
  this(ses, data, 0, data.length);
}
/**
 * Aim_tlvlist_t constructor comment.
 */
public Aim_tlvlist_t(Session ses, final byte[] data, final int ofs, final int len) throws MessagingNetworkException
{
  Lang.ASSERT_NOT_NULL(data, "data");
  int pos = ofs;
  while (pos < ofs + len)
  {
    int type = Session.aimutil_get16(data, pos);
    pos += 2;
    if (pos < ofs + len)
    {
      int length;
      //Okay, so now AOL has decided that any TLV of
      //type 0x0013 can only be two bytes, despite
      //what the actual given length is.  So here
      //we dump any invalid TLVs of that sort.  Hopefully
      //there's no special cases to this special case.
      //-mid (30jun2000)
      if (type == 0x0013)
      {
        length = 2;
        pos += 2;
      }
      else
      {
        length = Session.aimutil_get16(data, pos);
        pos += 2;
        MLang.EXPECT_NON_NEGATIVE(length, "length of tlv (of type " + type + " decimal)", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
        if (pos + length <= ofs + len)
        {
          Aim_tlv_t tlv = new Aim_tlv_t();
          tlv.type = type;
          tlv.length = length;
          if (length != 0)
          {
            tlv.value = AllocUtil.createByteArray(ses, length);
            System.arraycopy(data, pos, tlv.value, 0, length);
          }
          addTlv(tlv);
        }
      }
      pos += length;
    }
  }
}
private void addTlv(Aim_tlv_t tlv)
{
  elements.addElement(tlv);
}
/**
 * aim_gettlv - Grab the Nth TLV of type type in the TLV list list.
 * list: Source chain
 * type: Requested TLV type
 * nth: Index of TLV of type to get
 *
 * Returns a pointer to an aim_tlv_t of the specified type;
 * %null on error.  The @nth parameter is specified starting at %1.
 * In most cases, there will be no more than one TLV of any type
 * in a chain.
 *
 */
public Aim_tlv_t getNthTlvOfType(int n, int type)
{
  int count = 0;
  for (int i = 0; i < elements.size(); i++)
  {
    Aim_tlv_t tlv = getTlvAt(i);
    if (tlv.type == type)
      count++;
    if (count >= n)
      return tlv;
  }
  return null;
}
/**
 * Grab the Nth TLV of type type in the TLV list list.
 */
public int getNthTlvOfTypeAs16Bit(int n, int type)
{
  Aim_tlv_t tlv = getNthTlvOfType(n, type);
  if (tlv == null || tlv.value == null)
    return 0;
  return Session.aimutil_get16(tlv.value, 0);
}
/**
 * Grab the Nth TLV of type type in the TLV list list.
 */
public byte getNthTlvOfTypeAs8Bit(int n, int type)
{
  Aim_tlv_t tlv = getNthTlvOfType(n, type);
  if (tlv == null || tlv.value == null)
    return 0;
  return tlv.value[0];
}
/**
 * aim_gettlv - Grab the Nth TLV of type type in the TLV list list.
 * list: Source chain
 * type: Requested TLV type
 * nth: Index of TLV of type to get
 *
 * Returns a pointer to an aim_tlv_t of the specified type;
 * %null on error.  The @nth parameter is specified starting at %1.
 * In most cases, there will be no more than one TLV of any type
 * in a chain.
 *
 */
public byte[] getNthTlvOfTypeAsByteArray(int n, int type)
{
  Aim_tlv_t tlv = getNthTlvOfType(n, type);
  if (tlv == null)
    return null;
  return tlv.value;
}
/**
 * aim_gettlv - Grab the Nth TLV of type type in the TLV list list.
 * list: Source chain
 * type: Requested TLV type
 * nth: Index of TLV of type to get
 *
 * Returns a pointer to an aim_tlv_t of the specified type;
 * %null on error.  The @nth parameter is specified starting at %1.
 * In most cases, there will be no more than one TLV of any type
 * in a chain.
 *
 */
public String getNthTlvOfTypeAsString(int n, int type)
{
  byte[] ba = getNthTlvOfTypeAsByteArray(n, type);
  if (ba == null)
    return null;
  return Session.byteArray2string(ba);
}
public int getNumberOfBytes()
{
  int size = 0;
  for (int i = 0; i < elements.size(); i++)
  {
    Aim_tlv_t tlv = getTlvAt(i);
    size += 4 + tlv.length;
  }
  return size;
}
private Aim_tlv_t getTlvAt(int i)
{
  return (Aim_tlv_t) elements.get(i);
}
public byte[] skipNtlvsGetTlvOfTypeAsByteArray(int n, int type)
{
  Aim_tlv_t tlv = skipNtlvsGetTlvOfType(n, type);
  if (tlv == null)
    return null;
  return tlv.value;
}
public Aim_tlv_t skipNtlvsGetTlvOfType(int n, int type)
{
  int count = 0;
  for (int i = 0; i < elements.size(); i++)
  {
    Aim_tlv_t tlv = getTlvAt(i);
    count++;
    if (count > n && tlv.type == type)
      return tlv;
  }
  return null;
}
public int length()
{
  return elements.size();
}
}