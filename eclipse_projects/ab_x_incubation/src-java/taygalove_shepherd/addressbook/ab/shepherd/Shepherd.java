package taygalove_shepherd.addressbook.ab.shepherd;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.addressbook.ab.ab;
import taygalove_shepherd.addressbook.ab.abPersistence;
import taygalove_shepherd.addressbook.ab.datamodel.AddressBook;

public class Shepherd {
	public static void moveAddressbookToHDD(NamedCaller nc, ab app, AddressBook addressBook) throws Throwable {
		abPersistence.save(nc, app, addressBook);
	}
}
