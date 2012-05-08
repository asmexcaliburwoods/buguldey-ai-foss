package taygalove_shepherd.addressbook.ab.listmodel;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

import taygalove_shepherd.addressbook.ab.datamodel.AddressBook;
import taygalove_shepherd.addressbook.ab.datamodel.Human;
import taygalove_shepherd.addressbook.ab.datamodel.Merkaba;
import taygalove_shepherd.addressbook.ab.datamodel.Organism;
import taygalove_shepherd.i18n.m.M;

import java.util.*;

public class AddressBookListModel implements ListModel {
    private AddressBook addressBook;
    private static class Entry{
        String displayName;
        String sortKey;
        Merkaba merkaba;
    }
    private ArrayList<Entry> entries=new ArrayList<Entry>();

    public AddressBookListModel(AddressBook addressBook) {
        this.addressBook=addressBook;
        addressBook.addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                refresh();
            }

            public void intervalAdded(ListDataEvent e) {
                refresh();
            }

            public void intervalRemoved(ListDataEvent e) {
                refresh();
            }
        });
        refresh();
    }

    private void refresh() {
        entries.clear();
        entries.ensureCapacity(addressBook.getSize());
        Iterator<Merkaba> it=addressBook.iterateEntries();
        while (it.hasNext()) {
        	Merkaba merkaba=it.next();
            Entry e = new Entry();
            e.merkaba=merkaba;
            e.displayName=merkaba instanceof Human?(((Human)merkaba).getFirstNameNative()+" "+((Human)merkaba).getLastNameNative()).trim():M.MERKABA;
            e.sortKey=merkaba instanceof Human?((Human)merkaba).getLastNameNative()+" "+((Human)merkaba).getFirstNameNative():"";
            if(e.displayName.equals("")){
                e.displayName=merkaba instanceof Human?(((Human)merkaba).getFirstNameAscii()+" "+((Human)merkaba).getLastNameAscii()).trim():M.MERKABA;
                e.sortKey=merkaba instanceof Human?((Human)merkaba).getLastNameAscii()+" "+((Human)merkaba).getFirstNameAscii():M.MERKABA;
            }
            if(e.displayName.equals("")){
            	StringTokenizer st=new StringTokenizer(((Human)merkaba).getNicks(),",;|");
            	String first="";
            	if(st.hasMoreTokens())first=st.nextToken();
                e.displayName=first.trim();
                e.sortKey=e.displayName;
            }
            if(e.displayName.equals("")){
                e.displayName="Guess Who";
                e.sortKey="\uffff";
            }

            entries.add(e);
        }
        Collections.sort(entries,new Comparator() {
            public int compare(Object o1, Object o2) {
                Entry h1=(Entry) o1;
                Entry h2=(Entry) o2;
                return h1.sortKey.compareToIgnoreCase(h2.sortKey);
            }
        });
        Iterator<ListDataListener> itt=listeners.iterator();
        ListDataEvent ev = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, entries.size() - 1);
        while (itt.hasNext()) {
            ListDataListener listener = itt.next();
            listener.contentsChanged(ev);
        }
    }

    public int getSize() {
        return entries.size();
    }

    public Object getElementAt(int index) {
        Entry entry = getEntryAt(index);
        return entry.displayName;
    }

    public Merkaba getAgentAt(int index) {
        Entry entry = getEntryAt(index);
        return entry.merkaba;
    }

    public int indexOf(Merkaba a){
        Iterator<Entry> it=entries.iterator();
        int i=-1;
        while (it.hasNext()) {
            Entry entry = it.next();
            i++;
            if(entry.merkaba==a)break;
        }
        return i;
    }

    private Entry getEntryAt(int index) {
        Entry entry = entries.get(index);
        return entry;
    }

    private List<ListDataListener> listeners=new LinkedList<ListDataListener>();
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
