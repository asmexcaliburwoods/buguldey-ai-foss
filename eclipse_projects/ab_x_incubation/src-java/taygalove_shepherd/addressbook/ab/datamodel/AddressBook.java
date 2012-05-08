package taygalove_shepherd.addressbook.ab.datamodel;

import org.w3c.dom.Document;

import javax.swing.event.ListDataListener;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;

public interface AddressBook {
    Iterator<Merkaba> iterateEntries();
    Document encodeAsDomDocument() throws ParserConfigurationException;
    Human createUnlinkedHuman();
    void addmerkaba(Merkaba Merkaba);
    void hidemerkaba(Merkaba Merkaba);
    void showmerkaba(Merkaba Merkaba);
    AddressBook hidden();
    int getSize();
    void addListDataListener(ListDataListener l);
    void removeListDataListener(ListDataListener l);
}
