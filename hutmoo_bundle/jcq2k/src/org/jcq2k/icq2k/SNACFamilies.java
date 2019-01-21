package org.jcq2k.icq2k;

/**
Contains integer SNAC families and subtypes constants.
*/

public interface SNACFamilies
{
	public final int AIM_CB_FAM_ACK = 0x0000;
	public final int AIM_CB_FAM_GEN = 0x0001;
	public final int AIM_CB_FAM_LOC = 0x0002;
	public final int AIM_CB_FAM_BUD = 0x0003;
	public final int AIM_CB_FAM_MSG = 0x0004;
	public final int AIM_CB_FAM_ADS = 0x0005;
	public final int AIM_CB_FAM_INV = 0x0006;
	public final int AIM_CB_FAM_ADM = 0x0007;
	public final int AIM_CB_FAM_POP = 0x0008;
	public final int AIM_CB_FAM_BOS = 0x0009;
	public final int AIM_CB_FAM_LOK = 0x000a;
	public final int AIM_CB_FAM_STS = 0x000b;
	public final int AIM_CB_FAM_TRN = 0x000c;
	/** ChatNav */
	public final int AIM_CB_FAM_CTN = 0x000d;
	/** Chat */
	public final int AIM_CB_FAM_CHT = 0x000e;
	public final int AIM_CB_FAM_ATH = 0x0017;
	/** OFT/Rvous */
	public final int AIM_CB_FAM_OFT = 0xfffe;
	/** Internal libfaim use */
	public final int AIM_CB_FAM_SPECIAL = 0xffff;

	/**
	 * SNAC Family: Ack.
	 *
	 * Not really a family, but treating it as one really
	 * helps it fit into the libfaim callback structure better.
	 *
	 */
	public final int AIM_CB_ACK_ACK = 0x0001;

	/**
	 * SNAC Family: General.
	 */
	public final int AIM_CB_GEN_ERROR = 0x0001;
	public final int AIM_CB_GEN_CLIENTREADY = 0x0002;
	public final int AIM_CB_GEN_SERVERREADY = 0x0003;
	public final int AIM_CB_GEN_SERVICEREQ = 0x0004;
	public final int AIM_CB_GEN_REDIRECT = 0x0005;
	public final int AIM_CB_GEN_RATEINFOREQ = 0x0006;
	public final int AIM_CB_GEN_RATEINFO = 0x0007;
	public final int AIM_CB_GEN_RATEINFOACK = 0x0008;
	public final int AIM_CB_GEN_RATECHANGE = 0x000a;
	public final int AIM_CB_GEN_SERVERPAUSE = 0x000b;
	public final int AIM_CB_GEN_SERVERRESUME = 0x000d;
	public final int AIM_CB_GEN_REQSELFINFO = 0x000e;
	public final int AIM_CB_GEN_SELFINFO = 0x000f;
	public final int AIM_CB_GEN_EVIL = 0x0010;
	public final int AIM_CB_GEN_SETIDLE = 0x0011;
	public final int AIM_CB_GEN_MIGRATIONREQ = 0x0012;
	public final int AIM_CB_GEN_MOTD = 0x0013;
	public final int AIM_CB_GEN_SETPRIVFLAGS = 0x0014;
	public final int AIM_CB_GEN_WELLKNOWNURL = 0x0015;
	public final int AIM_CB_GEN_NOP = 0x0016;
	public final int AIM_CB_GEN_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Location Services.
	 */
	public final int AIM_CB_LOC_ERROR = 0x0001;
	public final int AIM_CB_LOC_REQRIGHTS = 0x0002;
	public final int AIM_CB_LOC_RIGHTSINFO = 0x0003;
	public final int AIM_CB_LOC_SETUSERINFO = 0x0004;
	public final int AIM_CB_LOC_REQUSERINFO = 0x0005;
	public final int AIM_CB_LOC_USERINFO = 0x0006;
	public final int AIM_CB_LOC_WATCHERSUBREQ = 0x0007;
	public final int AIM_CB_LOC_WATCHERNOT = 0x0008;
	public final int AIM_CB_LOC_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Buddy List Management Services.
	 */
	public final int AIM_CB_BUD_ERROR = 0x0001;
	public final int AIM_CB_BUD_REQRIGHTS = 0x0002;
	public final int AIM_CB_BUD_RIGHTSINFO = 0x0003;
	public final int AIM_CB_BUD_ADDBUDDY = 0x0004;
	public final int AIM_CB_BUD_REMBUDDY = 0x0005;
	public final int AIM_CB_BUD_REJECT = 0x000a;
	public final int AIM_CB_BUD_ONCOMING = 0x000b;
	public final int AIM_CB_BUD_OFFGOING = 0x000c;
	public final int AIM_CB_BUD_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Messeging Services.
	 */
	public final int AIM_CB_MSG_ERROR = 0x0001;
	public final int AIM_CB_MSG_PARAMINFO = 0x0005;
	public final int AIM_CB_MSG_INCOMING = 0x0007;
	public final int AIM_CB_MSG_EVIL = 0x0009;
	public final int AIM_CB_MSG_MISSEDCALL = 0x000a;
	public final int AIM_CB_MSG_CLIENTERROR = 0x000b;
	public final int AIM_CB_MSG_ACK = 0x000c;
	public final int AIM_CB_MSG_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Advertisement Services
	 */
	public final int AIM_CB_ADS_ERROR = 0x0001;
	public final int AIM_CB_ADS_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Invitation Services.
	 */
	public final int AIM_CB_INV_ERROR = 0x0001;
	public final int AIM_CB_INV_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Administrative Services.
	 */
	public final int AIM_CB_ADM_ERROR = 0x0001;
	public final int AIM_CB_ADM_INFOCHANGE_REPLY = 0x0005;
	public final int AIM_CB_ADM_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Popup Messages
	 */
	public final int AIM_CB_POP_ERROR = 0x0001;
	public final int AIM_CB_POP_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Misc BOS Services.
	 */
	public final int AIM_CB_BOS_ERROR = 0x0001;
	public final int AIM_CB_BOS_RIGHTSQUERY = 0x0002;
	public final int AIM_CB_BOS_RIGHTS = 0x0003;
	public final int AIM_CB_BOS_DEFAULT = 0xffff;

	/**
	 * SNAC Family: User Lookup Services
	 */
	public final int AIM_CB_LOK_ERROR = 0x0001;
	public final int AIM_CB_LOK_DEFAULT = 0xffff;

	/**
	 * SNAC Family: User Status Services
	 */
	public final int AIM_CB_STS_ERROR = 0x0001;
	public final int AIM_CB_STS_SETREPORTINTERVAL = 0x0002;
	public final int AIM_CB_STS_REPORTACK = 0x0004;
	public final int AIM_CB_STS_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Translation Services
	 */
	;
	public final int AIM_CB_TRN_ERROR = 0x0001;
	public final int AIM_CB_TRN_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Chat Navigation Services
	 */
	public final int AIM_CB_CTN_ERROR = 0x0001;
	public final int AIM_CB_CTN_CREATE = 0x0008;
	public final int AIM_CB_CTN_INFO = 0x0009;
	public final int AIM_CB_CTN_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Chat Services
	 */
	public final int AIM_CB_CHT_ERROR = 0x0001;
	public final int AIM_CB_CHT_ROOMINFOUPDATE = 0x0002;
	public final int AIM_CB_CHT_USERJOIN = 0x0003;
	public final int AIM_CB_CHT_USERLEAVE = 0x0004;
	public final int AIM_CB_CHT_OUTGOINGMSG = 0x0005;
	public final int AIM_CB_CHT_INCOMINGMSG = 0x0006;
	public final int AIM_CB_CHT_DEFAULT = 0xffff;

	/**
	 * SNAC Family: Authorizer
	 *
	 * Used only in protocol versions three and above.
	 *
	 */
	public final int AIM_CB_ATH_ERROR = 0x0001;
	public final int AIM_CB_ATH_LOGINREQEST = 0x0002;
	public final int AIM_CB_ATH_LOGINRESPONSE = 0x0003;
	public final int AIM_CB_ATH_AUTHREQ = 0x0006;
	public final int AIM_CB_ATH_AUTHRESPONSE = 0x0007;

	/**
	 * OFT Services
	 *
	 * See non-SNAC note below.
	 */
	/** connect request -- actually an OSCAR CAP*/
	public final int AIM_CB_OFT_DIRECTIMCONNECTREQ = 0x0001;
	public final int AIM_CB_OFT_DIRECTIMINCOMING = 0x0002;
	public final int AIM_CB_OFT_DIRECTIMDISCONNECT = 0x0003;
	public final int AIM_CB_OFT_DIRECTIMTYPING = 0x0004;
	public final int AIM_CB_OFT_DIRECTIMINITIATE = 0x0005;

	/** connect request -- actually an OSCAR CAP*/
	public final int AIM_CB_OFT_GETFILECONNECTREQ = 0x0006;
	/** OFT listing.txt request */
	public final int AIM_CB_OFT_GETFILELISTINGREQ = 0x0007;
	/** received file request */
	public final int AIM_CB_OFT_GETFILEFILEREQ = 0x0008;
	/** received file request confirm -- send data */
	public final int AIM_CB_OFT_GETFILEFILESEND = 0x0009;
	/** received file send complete*/
	public final int AIM_CB_OFT_GETFILECOMPLETE = 0x000a;
	/** request for file get acknowledge */
	public final int AIM_CB_OFT_GETFILEINITIATE = 0x000b;
	/** OFT connection disconnected.*/
	public final int AIM_CB_OFT_GETFILEDISCONNECT = 0x000c;
	/** OFT listing.txt received.*/
	public final int AIM_CB_OFT_GETFILELISTING = 0x000d;
	/** OFT file incoming.*/
	public final int AIM_CB_OFT_GETFILERECEIVE = 0x000e;
	public final int AIM_CB_OFT_GETFILELISTINGRXCONFIRM = 0x000f;
	public final int AIM_CB_OFT_GETFILESTATE4 = 0x0010;

	/** OFT connection disconnected.*/
	public final int AIM_CB_OFT_SENDFILEDISCONNECT = 0x0020;



	/**
	 * SNAC Family: Internal Messages
	 *
	 * This isn't truely a SNAC family either, but using
	 * these, we can integrated non-SNAC services into
	 * the SNAC-centered libfaim callback structure.
	 *
	 */
	public final int AIM_CB_SPECIAL_AUTHSUCCESS = 0x0001;
	public final int AIM_CB_SPECIAL_AUTHOTHER = 0x0002;
	public final int AIM_CB_SPECIAL_CONNERR = 0x0003;
	public final int AIM_CB_SPECIAL_CONNCOMPLETE = 0x0004;
	public final int AIM_CB_SPECIAL_FLAPVER = 0x0005;
	public final int AIM_CB_SPECIAL_DEBUGCONN_CONNECT = 0xe001;
	public final int AIM_CB_SPECIAL_UNKNOWN = 0xffff;
	public final int AIM_CB_SPECIAL_DEFAULT = AIM_CB_SPECIAL_UNKNOWN;
}