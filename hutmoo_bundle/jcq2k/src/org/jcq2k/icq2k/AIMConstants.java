package org.jcq2k.icq2k;

/**
Contains miscellaneous constants stolen from the
original C source.
*/
public interface AIMConstants
{
		/**
		 * USE_SNAC_FOR_IMS is an old feature that allowed better
		 * tracking of error messages by caching SNAC IDs of outgoing
		 * ICBMs and comparing them to incoming errors.  However,
		 * its a helluvalot of overhead for something that should
		 * rarely happen.
		 *
		 * Default: defined.
		 */
		public final int CFG_USE_SNAC_FOR_IMS = 1;

		/**
		 * Default auth server hostname and TCP port for the OSCAR farm.
		 *
		 * Note that only one server is needed to start the whole
		 * AIM process.  The later server addresses come from
		 * the authorizer service.
		 *
		 * This is only here for convenience.  Its still up to
		 * the client to connect to it.
		 */
		public final String CFG_AIM_LOGIN_SERVER_DEFAULT = "login.oscar.aol.com";
		/**
		 * Default auth server TCP port for the OSCAR farm.
		 *
		 * Note that only one server is needed to start the whole
		 * AIM process.  The later server addresses come from
		 * the authorizer service.
		 *
		 * This is only here for convenience.  Its still up to
		 * the client to connect to it.
		 */
		public final int CFG_AIM_LOGIN_PORT_DEFAULT = 5190;

		/**
		 * Size of the SNAC caching hash.
		 *
		 * Default: 16
		 *
		 */
		public final int CFG_AIM_SNAC_HASH_SIZE = 16;


		/**
		 * Current Maximum Length for Screen Names (not including NULL)
		 *
		 * Currently only names up to 16 characters can be registered
		 * however it is aparently legal for them to be larger.
		 */
		public final int MAXSNLEN = 32;

	/**
		 * Current Maximum Length for Instant Messages
		 *
		 * This was found basically by experiment, but not wholly
		 * accurate experiment.  It should not be regarded
		 * as completely correct.  But its a decent approximation.
		 *
		 * Note that although we can send this much, its impossible
		 * for WinAIM clients (up through the latest (4.0.1957)) to
		 * send any more than 1kb.  Amaze all your windows friends
		 * with utterly oversized instant messages!
		 *
		 * XXX: the real limit is the total SNAC size at 8192. Fix this.
		 *
		 */
		//public final int MAXMSGLEN =7987; //from libfaim
		//public final int MAXMSGLEN =7000; //from icq2000x
		//public final int MAXMSGLEN =1024; //winaim
		public final int MAXMSGLEN = 7000;

		/**
		 * Maximum size of a Buddy Icon.
		 */
		public final int MAXICONLEN = 7168;
		public final String AIM_ICONIDENT = "AVT1picture.id";

		/**
		 * Current Maximum Length for Chat Room Messages
		 *
		 * This is actuallypublic final intd by the protocol to be
		 * dynamic, but I have yet to see due cause to
		 * public final int it dynamically here.  Maybe later.
		 */
		public final int MAXCHATMSGLEN = 512;

		/**
		 * Standard size of an AIM authorization cookie
		 */
		public final int AIM_COOKIELEN = 0x100;
		public final String AIM_MD5_STRING = "AOL Instant Messenger (SM)";

		///**
		 //Utility class for storing login sequence clientinfo constants.
		 //*/
		//public final class ClientInfo
		//{
				//public final byte clientstring[]; //[100];
				///** arbitrary size */
				//public final short major;
				//public final short minor;
				//public final short build;
				//public final byte country[]; //[3];
				//public final byte lang[]; //[3];
				//public final short major2;
				//public final short minor2;
				//public final int unknown;
				//public ClientInfo(String clientstring, int major, int minor,
				///**/ int build, String country, String lang, int major2, int minor2,
				///**/ int unknown)
				//{
						//this.clientstring = new byte[100];
						//byte[] x = clientstring.getBytes();
						//System.arraycopy(x, 0, this.clientstring, 0, x.length);
						//this.major = (short) major;
						//this.minor = (short) minor;
						//this.build = (short) build;
						//this.country = new byte[3];
						//x = country.getBytes();
						//System.arraycopy(x, 0, this.country, 0, x.length);
						//this.lang = new byte[3];
						//x = lang.getBytes();
						//System.arraycopy(x, 0, this.lang, 0, x.length);
						//this.major2 = (short) major2;
						//this.minor2 = (short) minor2;
						//this.unknown = unknown;
				//}
		//}

		////
		//public final ClientInfo AIM_CLIENTINFO_KNOWNGOOD_3_5_1670 = new ClientInfo(//
		//"AOL Instant Messenger (SM), version 3.5.1670/WIN32", //
		//0x0003, 0x0005, 0x0686, "us", "en", 0x0004, 0x0000, 0x0000002a);

		////
		//public final ClientInfo AIM_CLIENTINFO_KNOWNGOOD_4_1_2010 = new ClientInfo(//
		//"AOL Instant Messenger (SM), version 4.1.2010/WIN32", //
		//0x0004, 0x0001, 0x07da, "us", "en", 0x0004, 0x0000, 0x0000004b);

		///**
		 //* I would make 4.1.2010 the default, but they seem to have found
		 //* an alternate way of breaking that one.
		 //*
		 //* 3.5.1670 should work fine, however, you will be subjected to the
		 //* memory test, which may require you to have a WinAIM binary laying
		 //* around. (see login.c::memrequest())
		 //*/
		//public final ClientInfo AIM_CLIENTINFO_KNOWNGOOD = AIM_CLIENTINFO_KNOWNGOOD_3_5_1670;


		//
		/**
		 * These could be arbitrary, but its easier to use the actual AIM values
		 */
		public final int AIM_CONN_TYPE_AUTH = 0x0007;
		public final int AIM_CONN_TYPE_ADS = 0x0005;
		public final int AIM_CONN_TYPE_BOS = 0x0002;
		public final int AIM_CONN_TYPE_CHAT = 0x000e;
		public final int AIM_CONN_TYPE_CHATNAV = 0x000d;
		/** they start getting arbitrary in rendezvous stuff =) */
		// these do not speak OSCAR!
		public final int AIM_CONN_TYPE_RENDEZVOUS = 0x0101;
		public final int AIM_CONN_TYPE_RENDEZVOUS_OUT = 0x0102 /** soket waiting for accept() */

		/**
		 * Subtypes, we need these for OFT stuff.
		 */
		;
		public final int AIM_CONN_SUBTYPE_OFT_DIRECTIM = 0x0001;
		public final int AIM_CONN_SUBTYPE_OFT_GETFILE = 0x0002;
		public final int AIM_CONN_SUBTYPE_OFT_SENDFILE = 0x0003;
		public final int AIM_CONN_SUBTYPE_OFT_BUDDYICON = 0x0004;
		public final int AIM_CONN_SUBTYPE_OFT_VOICE = 0x0005

		/**
		 * Status values returned from aim_conn_new().  ORed together.
		 */
		;
		public final int AIM_CONN_STATUS_READY = 0x0001;
		public final int AIM_CONN_STATUS_INTERNALERR = 0x0002;
		public final int AIM_CONN_STATUS_RESOLVERR = 0x0040;
		public final int AIM_CONN_STATUS_CONNERR = 0x0080;
		public final int AIM_CONN_STATUS_INPROGRESS = 0x0100;
		public final short AIM_FRAMETYPE_OSCAR = (short) 0x0000;
		public final short AIM_FRAMETYPE_OFT = (short) 0x0001;

		/** Values for sess->flags */
		public final int AIM_SESS_FLAGS_SNACLOGIN = 0x00000001;
		public final int AIM_SESS_FLAGS_XORLOGIN = 0x00000002;
		public final int AIM_SESS_FLAGS_NONBLOCKCONNECT = 0x00000004;
		/** damned transients */
		public final int AIM_FLAG_UNCONFIRMED = 0x0001;
		public final int AIM_FLAG_ADMINISTRATOR = 0x0002;
		public final int AIM_FLAG_AOL = 0x0004;
		public final int AIM_FLAG_OSCAR_PAY = 0x0008;
		public final int AIM_FLAG_FREE = 0x0010;
		public final int AIM_FLAG_AWAY = 0x0020;
		public final int AIM_FLAG_UNKNOWN40 = 0x0040;
		public final int AIM_FLAG_UNKNOWN80 = 0x0080;
		public final int AIM_FLAG_ALLUSERS = 0x001f;

		/** default */
		public final int AIM_TX_QUEUED = 0;
		public final int AIM_TX_IMMEDIATE = 1;
		public final int AIM_TX_USER = 2;
		public final int AIM_VISIBILITYCHANGE_PERMITADD = 0x05;
		public final int AIM_VISIBILITYCHANGE_PERMITREMOVE = 0x06;
		public final int AIM_VISIBILITYCHANGE_DENYADD = 0x07;
		public final int AIM_VISIBILITYCHANGE_DENYREMOVE = 0x08;
		public final int AIM_PRIVFLAGS_ALLOWIDLE = 0x01;
		public final int AIM_PRIVFLAGS_ALLOWMEMBERSINCE = 0x02;
		public final int AIM_WARN_ANON = 0x01;
		public final int AIM_CLIENTTYPE_UNKNOWN = 0x0000;
		public final int AIM_CLIENTTYPE_MC = 0x0001;
		public final int AIM_CLIENTTYPE_WINAIM = 0x0002;
		public final int AIM_CLIENTTYPE_WINAIM41 = 0x0003;
		public final int AIM_CLIENTTYPE_AOL_TOC = 0x0004;
		public final int AIM_RATE_CODE_CHANGE = 0x0001;
		public final int AIM_RATE_CODE_WARNING = 0x0002;
		public final int AIM_RATE_CODE_LIMIT = 0x0003;
		public final int AIM_RATE_CODE_CLEARLIMIT = 0x0004;

		/** sendMessage0() flag: message encoding */
		public final int AIM_IMFLAGS_ASCII7 = 0;
		/** sendMessage0() flag: message encoding */
		public final int AIM_IMFLAGS_UNICODE = 0x04;
		/** sendMessage0() flag: message encoding */
		public final int AIM_IMFLAGS_ISO_8859_1 = 0x08;
		/** sendMessage0() flag: mark as an autoreply. */
		public final int AIM_IMFLAGS_AWAY = 0x01;
		/** sendMessage0() flag: request a receipt notice */
		public final int AIM_IMFLAGS_ACK = 0x02;
		/** sendMessage0() flag: buddy icon requested */
		public final int AIM_IMFLAGS_BUDDYREQ = 0x10;
		/** sendMessage0() flag: already has icon (timestamp included) */
		public final int AIM_IMFLAGS_HASICON = 0x20;

		//
		public final int AIM_CAPS_BUDDYICON = 0x0001;
		public final int AIM_CAPS_VOICE = 0x0002;
		public final int AIM_CAPS_IMIMAGE = 0x0004;
		public final int AIM_CAPS_CHAT = 0x0008;
		public final int AIM_CAPS_GETFILE = 0x0010;
		public final int AIM_CAPS_SENDFILE = 0x0020;
		public final int AIM_CAPS_GAMES = 0x0040;
		public final int AIM_CAPS_SAVESTOCKS = 0x0080;
		public final int AIM_CAPS_SENDBUDDYLIST = 0x0100;
		public final int AIM_CAPS_GAMES2 = 0x0200;
		public final int AIM_CAPS_LAST = 0x8000;
		public final int AIM_SENDMEMBLOCK_FLAG_ISREQUEST = 0;
		public final int AIM_SENDMEMBLOCK_FLAG_ISHASH = 1;
		public final int AIM_GETINFO_GENERALINFO = 0x00001;
		public final int AIM_GETINFO_AWAYMESSAGE = 0x00003;
		public final int AIM_COOKIETYPE_UNKNOWN = 0x00;
		public final int AIM_COOKIETYPE_ICBM = 0x01;
		public final int AIM_COOKIETYPE_ADS = 0x02;
		public final int AIM_COOKIETYPE_BOS = 0x03;
		public final int AIM_COOKIETYPE_IM = 0x04;
		public final int AIM_COOKIETYPE_CHAT = 0x05;
		public final int AIM_COOKIETYPE_CHATNAV = 0x06;
		public final int AIM_COOKIETYPE_INVITE = 0x07;
		/** we'll move OFT up a bit to give breathing room.
		not like it really matters. */
		public final int AIM_COOKIETYPE_OFTIM = 0x10;
		public final int AIM_COOKIETYPE_OFTGET = 0x11;
		public final int AIM_COOKIETYPE_OFTSEND = 0x12;
		public final int AIM_COOKIETYPE_OFTVOICE = 0x13;
		public final int AIM_COOKIETYPE_OFTIMAGE = 0x14;
		public final int AIM_COOKIETYPE_OFTICON = 0x15;
		public final int AIM_TRANSFER_DENY_NOTSUPPORTED = 0x0000;
		public final int AIM_TRANSFER_DENY_DECLINE = 0x0001;
		public final int AIM_TRANSFER_DENY_NOTACCEPTING = 0x0002;
		public final int AIM_CHATFLAGS_NOREFLECT = 0x0001;
		public final int AIM_CHATFLAGS_AWAY = 0x0002;

		//
		//internal constants
		//

		//public final int AIM_MODULENAME_MAXLEN 16;
		public final int AIM_MODFLAG_MULTIFAMILY = 0x0001;

		/* these are used by aim_*_clientready */
		public final int AIM_TOOL_JAVA = 0x0001;
		public final int AIM_TOOL_MAC = 0x0002;
		public final int AIM_TOOL_WIN16 = 0x0003;
		public final int AIM_TOOL_WIN32 = 0x0004;
		public final int AIM_TOOL_MAC68K = 0x0005;
		public final int AIM_TOOL_MACPPC = 0x0006;
		public final int AIM_TOOL_NEWWIN = 0x0010;
}