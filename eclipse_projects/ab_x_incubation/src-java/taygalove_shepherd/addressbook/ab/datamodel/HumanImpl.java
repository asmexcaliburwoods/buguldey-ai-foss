package taygalove_shepherd.addressbook.ab.datamodel;

import java.util.Date;

public class HumanImpl implements Human{
    public static Human createNewUnlinkedHuman() {
        return new HumanImpl();
    }
    private String LastNameAscii="";
    private String FirstNameAscii="";
    private String MiddleNameAscii="";
    private String LastNameNative="";
    private String FirstNameNative="";
    private String MiddleNameNative="";
    private String Nicks="";
    private String CompaniesAndTitles="";
    private String Emails="";
    private String WebUrls="";
    private String Blogs="";
    private String BlogsRss="";
    private String StreetAddress="";
    private Date lastUpdated=new Date();
    private String HomePhones="";
    private String CellPhones="";
    private String OfficePhones="";
    private String IcqNumbers="";
    private String JabberIds="";
    private String SkypeLogins="";
    private String MsnLogins ="";
    private String Notes="";

    public String getLastNameAscii() {
        return LastNameAscii;
    }

    public void setLastNameAscii(String lastNameAscii) {
        LastNameAscii = lastNameAscii;
    }

    public String getFirstNameAscii() {
        return FirstNameAscii;
    }

    public void setFirstNameAscii(String firstNameAscii) {
        FirstNameAscii = firstNameAscii;
    }

    public String getMiddleNameAscii() {
        return MiddleNameAscii;
    }

    public void setMiddleNameAscii(String middleNameAscii) {
        MiddleNameAscii = middleNameAscii;
    }

    public String getLastNameNative() {
        return LastNameNative;
    }

    public void setLastNameNative(String lastNameNative) {
        LastNameNative = lastNameNative;
    }

    public String getFirstNameNative() {
        return FirstNameNative;
    }

    public void setFirstNameNative(String firstNameNative) {
        FirstNameNative = firstNameNative;
    }

    public String getMiddleNameNative() {
        return MiddleNameNative;
    }

    public void setMiddleNameNative(String middleNameNative) {
        MiddleNameNative = middleNameNative;
    }

    public String getNicks() {
        return Nicks;
    }

    public void setNicks(String nicks) {
        Nicks = nicks;
    }

    public String getCompaniesAndTitles() {
        return CompaniesAndTitles;
    }

    public void setCompaniesAndTitles(String companiesAndTitles) {
        CompaniesAndTitles = companiesAndTitles;
    }

    public String getEmails() {
        return Emails;
    }

    public void setEmails(String emails) {
        Emails = emails;
    }

    public String getWebUrls() {
        return WebUrls;
    }

    public void setWebUrls(String webUrls) {
        WebUrls = webUrls;
    }

    public String getBlogs() {
        return Blogs;
    }

    public void setBlogs(String blogs) {
        Blogs = blogs;
    }

    public String getBlogsRss() {
        return BlogsRss;
    }

    public void setBlogsRss(String blogsRss) {
        BlogsRss = blogsRss;
    }

    public String getStreetAddress() {
        return StreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        StreetAddress = streetAddress;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getHomePhones() {
        return HomePhones;
    }

    public void setHomePhones(String homePhones) {
        HomePhones = homePhones;
    }

    public String getCellPhones() {
        return CellPhones;
    }

    public void setCellPhones(String cellPhones) {
        CellPhones = cellPhones;
    }

    public String getOfficePhones() {
        return OfficePhones;
    }

    public void setOfficePhones(String officePhones) {
        OfficePhones = officePhones;
    }

    public String getIcqNumbers() {
        return IcqNumbers;
    }

    public void setIcqNumbers(String icqNumbers) {
        IcqNumbers = icqNumbers;
    }

    public String getJabberIds() {
		return JabberIds;
	}

	public void setJabberIds(String jabberIds) {
		JabberIds = jabberIds;
	}

	public String getSkypeLogins() {
        return SkypeLogins;
    }

    public void setSkypeLogins(String skypeLogins) {
        SkypeLogins = skypeLogins;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getMsnLogins() {
        return MsnLogins;
    }

    public void setMsnLogins(String MSNLogins) {
        this.MsnLogins = MSNLogins;
    }
}
