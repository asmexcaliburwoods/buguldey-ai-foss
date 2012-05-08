package taygalove_shepherd.addressbook.ab.datamodel;

public interface Human extends Organism{
    String getLastNameAscii();

    void setLastNameAscii(String lastNameAscii);

    String getFirstNameAscii();

    void setFirstNameAscii(String firstNameAscii);

    String getMiddleNameAscii();

    void setMiddleNameAscii(String middleNameAscii);

    String getLastNameNative();

    void setLastNameNative(String lastNameNative);

    String getFirstNameNative();

    void setFirstNameNative(String firstNameNative);

    String getMiddleNameNative();

    void setMiddleNameNative(String middleNameNative);

    String getNicks();

    void setNicks(String nicks);

    String getStreetAddress();

    void setStreetAddress(String streetAddress);
}
