package org.jcq2k.icq2k;

import java.util.*;
import org.jcq2k.util.joe.*;

/**
Simple datatype that represents a TLV (Type/Length/Value) tuple.
<p>
In a byte array, TLV is represented by
<ul>
<li>2-byte type,
<li>2-byte length, and
<li>N-byte value,
</ul>
where N is equal to the length specified.
<p>Actually, "type" is
wrong word.  "Key" would be a better term, but historically, it's
called "type".  It is not a datatype, but usually a numeric tag to
distinguish different TLVs in the packet.  Though, there can be
several TLVs with identical type in the packet
(this situation is rare).
<p>
In each SNAC packet body, there are several TLVs often used.
Some example packet could contain four TLVs:
<ul>
<li>a TLV of type 1 for the BOS hostname (string value),
<li>a TLV of type 2 with user's email (string value),
<li>a TLV of type 5 with user's online status (2-byte value), and
<li>a TLV of type 11 with some flag (byte value).
</ul>
<p>
SNAC packets can contain TLVs and other data entities though.
<p>
See {@link Aim_tlvlist_t} type and
<a href=http://www.zigamorph.net/faim/protocol/>the OSCAR
protocol documentation</a> for more info on TLVs.
*/
public class Aim_tlv_t
{
	public int type = -1;
	public int length = -1;
	public byte[] value;
/**
 * Aim_tlv_t constructor comment.
 */
public Aim_tlv_t() {
	super();
}
}