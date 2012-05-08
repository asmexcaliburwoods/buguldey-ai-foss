package taygalove_shepherd.addressbook.ab.datamodel;

import java.util.Date;

public interface Organism extends Merkaba {
    String getCompaniesAndTitles();

    void setCompaniesAndTitles(String companiesAndTitles);

    String getEmails();

    void setEmails(String emails);

    String getWebUrls();

    void setWebUrls(String webUrls);

    String getBlogs();

    void setBlogs(String blogs);

    Date getLastUpdated();

    void setLastUpdated(Date lastUpdated);

    String getHomePhones();

    void setHomePhones(String homePhones);

    String getCellPhones();

    void setCellPhones(String cellPhones);

    String getOfficePhones();

    void setOfficePhones(String officePhones);

    String getIcqNumbers();

    void setIcqNumbers(String icqNumbers);

    String getJabberIds();

    void setJabberIds(String jabberIds);

    String getSkypeLogins();

    void setSkypeLogins(String skypeLogins);

    String getNotes();

    void setNotes(String notes);

    String getMsnLogins();

    void setMsnLogins(String MSNLogins);
}
