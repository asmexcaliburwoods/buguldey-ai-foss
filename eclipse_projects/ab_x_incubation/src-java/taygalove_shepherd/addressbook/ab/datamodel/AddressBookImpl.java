package taygalove_shepherd.addressbook.ab.datamodel;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import taygalove_shepherd.addressbook.ab.ui.pane.HumanPane;
import taygalove_shepherd.util.GoogleCsvDocument;
import taygalove_shepherd.util.GoogleCsvRow;
import taygalove_shepherd.util.GoogleCsvUtil;
import taygalove_shepherd.util.StringUtil;
import taygalove_shepherd.util.XmlUtil;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.xml.parsers.ParserConfigurationException;

import java.text.SimpleDateFormat;
import java.util.*;

public class AddressBookImpl implements AddressBook{
    public static AddressBook createNewAddressBook() {
        return new AddressBookImpl();
    }

    private List<Merkaba> entries=new ArrayList<Merkaba>();

    public Iterator<Merkaba> iterateEntries() {
        return entries.iterator();
    }

    
    public Document encodeAsDomDocument() throws ParserConfigurationException {
        Document d=XmlUtil.newDocumentBuilder().newDocument();
        Element ab=d.createElement("ab");
        d.appendChild(ab);
        Iterator<Merkaba> it=entries.iterator();
        while (it.hasNext()) {
        	Object o=it.next();
            String kind="null";
            if(o!=null){
            	if(o instanceof Human)kind="human";
            	else
            		kind=o.getClass().getName();
            }
            Element e=d.createElement(kind);
            ab.appendChild(e);
        	if(o instanceof Human){
	            Human h = (Human) o;
	            Element he=e;
	            copyAttr(h,he,"Notes");
	            copyAttr(h,he,"LastNameAscii");
	            copyAttr(h,he,"FirstNameAscii");
	            copyAttr(h,he,"MiddleNameAscii");
	            copyAttr(h,he,"LastNameNative");
	            copyAttr(h,he,"FirstNameNative");
	            copyAttr(h,he,"MiddleNameNative");
	            copyAttr(h,he,"Nicks");
	            copyAttr(h,he,"CompaniesAndTitles");
	            copyAttr(h,he,"Emails");
	            copyAttr(h,he,"WebUrls");
	            copyAttr(h,he,"Blogs");
	            copyAttr(h,he,"StreetAddress");
	            he.setAttribute("LastUpdated",""+h.getLastUpdated().getTime());
	            copyAttr(h,he,"HomePhones");
	            copyAttr(h,he,"CellPhones");
	            copyAttr(h,he,"OfficePhones");
	            copyAttr(h,he,"IcqNumbers");
	            copyAttr(h,he,"JabberIds");
	            copyAttr(h,he,"SkypeLogins");
	            copyAttr(h,he,"MsnLogins");
        	}
        }
        return d;
    }

    private static void copyAttr(Human h, Element he, String attrName) {
        String value= StringUtil.makeEmpty(taygalove_shepherd.addressbook.ab.ui.pane.HumanPane.getStringViaReflection(h,attrName)).trim();
        if(!StringUtil.isEmpty(value))he.setAttribute(attrName,value);
    }

    public String encodeAsGoogleCsv() throws Exception {
        GoogleCsvDocument d=GoogleCsvUtil.newDocument();
        Iterator<Merkaba> it=entries.iterator();
        while (it.hasNext()) {
        	Object o=it.next();
//            String kind="null";
//            if(o!=null){
//            	if(o instanceof Human)kind="human";
//            	else
//            		kind=o.getClass().getName();
//            }
            GoogleCsvRow e=d.createRow();
            d.appendRow(e);
        	if(o instanceof Human){
	            Human h = (Human) o;
	            GoogleCsvRow he=e;
	            
	            copyAttr(h,he,"Notes");

	            //Given Name
	            //Additional Name
	            //Family Name
	            String lastn=h.getLastNameNative();
	            String firstn=h.getFirstNameNative();
	            String midn=h.getMiddleNameNative();

	            if(StringUtil.isEmptyTrimmed(lastn))lastn=h.getLastNameAscii();
	            if(StringUtil.isEmptyTrimmed(firstn))firstn=h.getFirstNameAscii();
	            if(StringUtil.isEmptyTrimmed(midn))midn=h.getMiddleNameAscii();
	            
	            String GivenName=firstn;
	            String AdditionalName=midn;
	            String FamilyName=lastn;
	            he.setColumn("Given Name", GivenName);
	            he.setColumn("Additional Name", AdditionalName);
	            he.setColumn("Family Name", FamilyName);
	            
	            he.setColumn("Nickname", h.getNicks());
	            
	            List<String> nickList=StringUtil.split(StringUtil.makeEmpty(h.getNicks()),",;");
	            String firstNick=nickList.isEmpty()?"":nickList.get(0);
	            firstNick=firstNick.trim();
	            firstNick=StringUtil.makeNull(firstNick);
	            String passportName=GivenName+" "+(AdditionalName==null?"":AdditionalName+" ")+FamilyName;
	            passportName=passportName.trim();
	            passportName=StringUtil.makeNull(passportName);
	            String name=passportName==null?firstNick:passportName+(firstNick==null?"":" ("+firstNick+")");
	            name=name.trim();
	            he.setColumn("Name", name);
	            
	            //Organization 1 - Title
	            String CompaniesAndTitles=h.getCompaniesAndTitles();
	            List<String> CompaniesAndTitlesList=StringUtil.split(CompaniesAndTitles, "\r\n");
	            int i=1;
	            for(String c_and_t:CompaniesAndTitlesList){
	            	String googleColumnName="Organization "+i+" - Title";
	            	he.setColumn(googleColumnName, c_and_t);
	            	i++;
	            }
	            
	            //E-mail 1 - Type
	            //* Home
	            //E-mail 1 - Value
	            String Emails=h.getEmails();
	            List<String> EmailsList=StringUtil.split(Emails, "\r\n");
	            i=1;
	            boolean first=true;
	            for(String eml:EmailsList){
	            	String googleColumnNameType="E-mail "+i+" - Type";
	            	String googleColumnNameValue="E-mail "+i+" - Value";
	            	String googleTypeColumnValue=(first?"* ":"")+"Home";
	            	he.setColumn(googleColumnNameType, googleTypeColumnValue);
	            	he.setColumn(googleColumnNameValue, eml);
	            	i++;
	            	first=false;
	            }
	            
	            
	            //Website 1 - Type
	            //Home
	            //Website 1 - Value
	            {
		            String WebUrls=h.getWebUrls();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, "\r\n");
		            i=1;
		            first=true;
		            for(String w:WebUrlsList){
		            	String googleColumnNameType="Website "+i+" - Type";
		            	String googleColumnNameValue="Website "+i+" - Value";
		            	String googleTypeColumnValue=(first?"":"")+"Home";
		            	he.setColumn(googleColumnNameType, googleTypeColumnValue);
		            	he.setColumn(googleColumnNameValue, w);
		            	i++;
		            	first=false;
		            }
	            }
	            
	            //Website 2 - Type
	            //Blog
	            //Website 2 - Value
	            {
		            String WebUrls=h.getBlogs();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, "\r\n");
		            //i=1;//continue numbering
		            //first=true;//continue numbering
		            for(String w:WebUrlsList){
		            	String googleColumnNameType="Website "+i+" - Type";
		            	String googleColumnNameValue="Website "+i+" - Value";
		            	String googleTypeColumnValue=(first?"":"")+"Blog";
		            	he.setColumn(googleColumnNameType, googleTypeColumnValue);
		            	he.setColumn(googleColumnNameValue, w);
		            	i++;
		            	first=false;
		            }
		            }
	            
	            //Address 1 - Type
	            //Home
	            //Address 1 - Formatted
	            he.setColumn("Address 1 - Type", "Home");
	            he.setColumn("Address 1 - Formatted", h.getStreetAddress());
	            
	            //Custom Field 1 - Type
	            //Date Updated
	            //Custom Field 1 - Value
	            //anything
	            SimpleDateFormat df=new SimpleDateFormat("MMM/dd/yyyy"); 
	            he.setColumn("Custom Field 1 - Type", "Date Updated");
	            he.setColumn("Custom Field 1 - Value",df.format(h.getLastUpdated()));

	            //Phone 1 - Type
	            //Home
	            //Phone 1 - Value
	            {
		            String WebUrls=h.getHomePhones();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, ",;");
		            i=1;
		            first=true;
		            for(String w:WebUrlsList){
		            	String googleColumnNameType="Phone "+i+" - Type";
		            	String googleColumnNameValue="Phone "+i+" - Value";
		            	String googleTypeColumnValue=(first?"":"")+"Home";
		            	he.setColumn(googleColumnNameType, googleTypeColumnValue);
		            	he.setColumn(googleColumnNameValue, w);
		            	i++;
		            	first=false;
		            }
	            }

	            //Phone 2 - Type
	            //Mobile
	            //Phone 2 - Value
	            {
		            String WebUrls=h.getCellPhones();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, ",;");
		            //i=1;//continue numbering
		            //first=true;//continue numbering
		            for(String w:WebUrlsList){
		            	String googleColumnNameType="Phone "+i+" - Type";
		            	String googleColumnNameValue="Phone "+i+" - Value";
		            	String googleTypeColumnValue=(first?"":"")+"Mobile";
		            	he.setColumn(googleColumnNameType, googleTypeColumnValue);
		            	he.setColumn(googleColumnNameValue, w);
		            	i++;
		            	first=false;
		            }
	            }

	            //Phone 3 - Type
	            //Work
	            //Phone 3 - Value
	            {
		            String WebUrls=h.getOfficePhones();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, ",;");
		            //i=1;//continue numbering
		            //first=true;//continue numbering
		            for(String w:WebUrlsList){
		            	String googleColumnNameType="Phone "+i+" - Type";
		            	String googleColumnNameValue="Phone "+i+" - Value";
		            	String googleTypeColumnValue=(first?"":"")+"Work";
		            	he.setColumn(googleColumnNameType, googleTypeColumnValue);
		            	he.setColumn(googleColumnNameValue, w);
		            	i++;
		            	first=false;
		            }
	            }
	            
	            //IM 1 - Type
	            //Other
	            //IM 1 - Service
	            //Skype ::: ICQ ::: Jabber ::: MSN
	            //IM 1 - Value
	            //im-sk ::: im-icq ::: im-jab ::: im-msn
	            he.setColumn("IM 1 - Type", "Other");
	            StringBuilder service=new StringBuilder();
	            StringBuilder value=new StringBuilder();
	            
	            {
	            	String serviceName="ICQ";
		            String WebUrls=h.getIcqNumbers();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, ",;");
		            first=true;
		            for(String w:WebUrlsList){
		            	if(service.length()>0)service.append(" ::: ");
		            	service.append(serviceName);
		            	if(value.length()>0)value.append(" ::: ");
		            	value.append(w);
		            	first=false;
		            }
	            }
	            
	            {
	            	String serviceName="Jabber";
		            String WebUrls=h.getJabberIds();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, ",;");
		            //first=true;//not first
		            for(String w:WebUrlsList){
		            	if(service.length()>0)service.append(" ::: ");
		            	service.append(serviceName);
		            	if(value.length()>0)value.append(" ::: ");
		            	value.append(w);
		            	first=false;
		            }
	            }
	            
	            {
		            //Skype ::: ICQ ::: Jabber ::: MSN
	            	String serviceName="Skype";
		            String WebUrls=h.getSkypeLogins();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, ",;");
		            //first=true;//not first
		            for(String w:WebUrlsList){
		            	if(service.length()>0)service.append(" ::: ");
		            	service.append(serviceName);
		            	if(value.length()>0)value.append(" ::: ");
		            	value.append(w);
		            	first=false;
		            }
	            }

	            {
		            //Skype ::: ICQ ::: Jabber ::: MSN
	            	String serviceName="MSN";
		            String WebUrls=h.getMsnLogins();
		            List<String> WebUrlsList=StringUtil.split(WebUrls, ",;");
		            //first=true;//not first
		            for(String w:WebUrlsList){
		            	if(service.length()>0)service.append(" ::: ");
		            	service.append(serviceName);
		            	if(value.length()>0)value.append(" ::: ");
		            	value.append(w);
		            	first=false;
		            }
	            }
	            
	            //IM 1 - Service
	            //Skype ::: ICQ ::: Jabber ::: MSN
	            //IM 1 - Value
	            //im-sk ::: im-icq ::: im-jab ::: im-msn
	            he.setColumn("IM 1 - Service", service.toString());
	            he.setColumn("IM 1 - Value", value.toString());
        	}
        }
        return d.exportAsString();
    }

    private static void copyAttr(Human h, GoogleCsvRow he, String attrName) {
        String value= StringUtil.makeEmpty(taygalove_shepherd.addressbook.ab.ui.pane.HumanPane.getStringViaReflection(h,attrName)).trim();
        if(!StringUtil.isEmpty(value))he.setColumn(attrName,value);
    }

    private static void decodeAttr(Human h, Element he, String attrName) {
        String value= StringUtil.makeEmpty(he.getAttribute(attrName)).trim();
        if(!StringUtil.isEmpty(value))HumanPane.setStringViaReflection(h,attrName,value);
    }

    public Human createUnlinkedHuman() {
        return HumanImpl.createNewUnlinkedHuman();
    }

    public void addmerkaba(Merkaba agent) {
        entries.add(agent);
        fire();
    }

    //GTD
    public void hidemerkaba(Merkaba agent) {
        entries.remove(agent);
        fire();
    }

    public int getSize() {
        return entries.size();
    }

    private void fire() {
        Iterator<ListDataListener> itt=listeners.iterator();
        ListDataEvent ev = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, entries.size() - 1);
        while (itt.hasNext()) {
            ListDataListener listener = itt.next();
            listener.contentsChanged(ev);
        }
    }

    private List<ListDataListener> listeners=new LinkedList<ListDataListener>();
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public static AddressBook decodeFromDomDocumentElement(Element abe) {
        AddressBook ab=createNewAddressBook();
        NodeList nl=abe.getElementsByTagName("human");
        for(int i=0;i<nl.getLength();i++){
            Element he =((Element)nl.item(i));
            Human h = ab.createUnlinkedHuman();
            decodeAttr(h,he,"LastNameAscii");
            decodeAttr(h,he,"FirstNameAscii");
            decodeAttr(h,he,"MiddleNameAscii");
            decodeAttr(h,he,"LastNameNative");
            decodeAttr(h,he,"FirstNameNative");
            decodeAttr(h,he,"MiddleNameNative");
            decodeAttr(h,he,"Nicks");
            decodeAttr(h,he,"CompaniesAndTitles");
            decodeAttr(h,he,"Emails");
            decodeAttr(h,he,"WebUrls");
            decodeAttr(h,he,"Blogs");
            decodeAttr(h,he,"StreetAddress");
            h.setLastUpdated(new Date(Long.parseLong(he.getAttribute("LastUpdated"))));
            decodeAttr(h,he,"HomePhones");
            decodeAttr(h,he,"CellPhones");
            decodeAttr(h,he,"OfficePhones");
            decodeAttr(h,he,"IcqNumbers");
            decodeAttr(h,he,"JabberIds");
            decodeAttr(h,he,"SkypeLogins");
            decodeAttr(h,he,"MsnLogins");
            decodeAttr(h,he,"Notes");
            ab.addmerkaba(h);
        }
        return ab;
    }


	@Override
	public AddressBook hidden() {
		// GTD Auto-generated method stub
		return createNewAddressBook();
	}


	//GTD ....... remove this method. The architecture must return addressbooks as entries of AddressBook, or as in google contacts or TB.
	@Override
	public void showmerkaba(Merkaba merkaba) {
		addmerkaba(merkaba);		
	}
}
