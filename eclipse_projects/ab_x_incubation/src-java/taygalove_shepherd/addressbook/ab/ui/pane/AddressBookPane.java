package taygalove_shepherd.addressbook.ab.ui.pane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.addressbook.ab.ab;
import taygalove_shepherd.addressbook.ab.datamodel.AddressBook;
import taygalove_shepherd.addressbook.ab.datamodel.AddressBookImpl;
import taygalove_shepherd.addressbook.ab.datamodel.Human;
import taygalove_shepherd.addressbook.ab.datamodel.Merkaba;
import taygalove_shepherd.addressbook.ab.listmodel.AddressBookListModel;
import taygalove_shepherd.addressbook.ab.shepherd.Shepherd;
import taygalove_shepherd.sachok.PreferencesForABProject;
import taygalove_shepherd.ui.CFrame;
import taygalove_shepherd.util.ExceptionUtil;
import taygalove_shepherd.util.MsgBoxUtil;
import taygalove_shepherd.util.ScreenBoundsUtil;

public class AddressBookPane extends JPanel {
	private static final long serialVersionUID = -4055882123326564831L;
	private static final String AB_PANE_SPLITTER_REG_KEY = "AbPaneSplitter";
    private JPanel rightPane;
    private JButton buttonDelete;
	private Merkaba currentAgent;
	private final JList list;
	private final AddressBookListModel listModel;
	
    public AddressBookPane(final NamedCaller nc, final ab app, final AddressBook addressBook, final CFrame f){
        setLayout(new BorderLayout());
        listModel = new AddressBookListModel(addressBook);
        JPanel lc=new JPanel(new BorderLayout());
        list=new JList(listModel);
        JPanel buttonPane=new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton buttonNew = new JButton(new AbstractAction("New") {
			private static final long serialVersionUID = 6599171750437341286L;

			public void actionPerformed(ActionEvent e) {
                HumanPane.showNewHumanFrame(nc, "ab new entry", "NewAbEntry",
                        new HumanPane.ActionListener() {
                            public boolean addPressed(final Human human,CFrame f) {
                                addressBook.addmerkaba(human);
                                saveAll(nc, app, addressBook,f, new Runnable(){
                					@Override
                					public void run() {
                                        select(human);
                					}
                                });
                                return true;
                            }

                            public boolean cancelPressed() {
                                return true;
                            }
                        });
            }
        });
        buttonNew.setMnemonic('N');
        buttonPane.add(buttonNew);
        buttonDelete = new JButton(new AbstractAction("Delete") {
			private static final long serialVersionUID = 4152642399201682840L;

			public void actionPerformed(ActionEvent e) {
                int i=list.getSelectedIndex();
                String displayName=(String) list.getSelectedValue();
                if(0==MsgBoxUtil.askYesNo(nc, f,"Confirm delete","Are you sure to delete "+displayName+"?",new String[]{"Yes","No"},1)){
                    addressBook.hidemerkaba(listModel.getAgentAt(i));
                    buttonDelete.setEnabled(false);
                    list.setSelectedIndices(new int[0]);
                    setRightPane(new Container(),null);
                }
            }
        });
        buttonDelete.setMnemonic('D');
        buttonDelete.setEnabled(false);
        buttonPane.add(buttonDelete);
        JButton buttonSaveAll = new JButton(new AbstractAction("Save All") {
			private static final long serialVersionUID = 2376630563022692452L;

			public void actionPerformed(ActionEvent e) {
                saveAll(nc, app, addressBook, f, new Runnable(){
					@Override
					public void run() {
	                    select(currentAgent);
					}
                });
            }
        });
        buttonSaveAll.setMnemonic('S');
        buttonPane.add(buttonSaveAll);

        JButton buttonExport = new JButton(new AbstractAction("Export") {
			private static final long serialVersionUID = 8753145246156659941L;

			public void actionPerformed(ActionEvent e) {
                export(nc, app, addressBook, f);
            }
        });
        buttonExport.setMnemonic('E');
        buttonExport.setToolTipText("Export to Google Contacts");
        buttonPane.add(buttonExport);

        lc.add(BorderLayout.CENTER,new JScrollPane(list));
        lc.add(BorderLayout.SOUTH,buttonPane);
        final JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                true,
                lc,
                rightPane=new JPanel(new BorderLayout()));
        add(BorderLayout.CENTER,split);
        int dl=PreferencesForABProject.userNodeForABProject.getInt(AB_PANE_SPLITTER_REG_KEY,300);
        dl=Math.max(dl, lc.getPreferredSize().width);
        dl=Math.max(dl, buttonPane.getPreferredSize().width);
        split.setDividerLocation(dl);
        split.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                try {
                	PreferencesForABProject.userNodeForABProject.putInt(nc, AB_PANE_SPLITTER_REG_KEY,
                            split.getDividerLocation());
                } catch (Exception e1) {
                    ExceptionUtil.handleException(nc, e1);
                }
            }
        });
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(list.getSelectedIndices().length==0)return;
                buttonDelete.setEnabled(true);
                Merkaba agent = listModel.getAgentAt(list.getSelectedIndex());
				HumanPane c = new HumanPane(nc, (Human) agent, null, null, list);
                setRightPane(c,agent);
            }
        });
        if(addressBook.getSize()>0)list.setSelectedIndex(0);
    }

	private void select(Merkaba agent) {
		if(agent!=null)list.setSelectedIndex(listModel.indexOf(agent));
		else list.setSelectedIndices(new int[0]);
	}

    public static void saveAll(NamedCaller nc, ab app, AddressBook addressBook, CFrame f, Runnable lastStep) {
        try {
            Shepherd.moveAddressbookToHDD/*save*/(nc, app, addressBook);
            final Human fake = addressBook.createUnlinkedHuman();
            fake.setFirstNameAscii("...Refreshing...");
            addressBook.addmerkaba(fake);
            addressBook.hidemerkaba(fake);
            if(lastStep!=null)lastStep.run();
            MsgBoxUtil.showMessage(nc, f,"Saved.");
        } catch (Throwable throwable) {
            ExceptionUtil.handleException(nc, throwable);
        }
    }

    public static void export(NamedCaller nc, ab app, AddressBook addressBook, CFrame f) {
    	String fileName=null;
        try {
        	FileDialog saveAs=new FileDialog(f, "Save Google Contacts CSV file as...");
        	saveAs.setModal(true);
        	saveAs.setMode(FileDialog.SAVE);
        	saveAs.setFile("google-contacts.csv");
        	saveAs.setFilenameFilter(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					File f=new File(dir,name);
					return f.isDirectory()||name.toLowerCase().endsWith(".csv");
				}
			});
        	saveAs.setVisible(true);
        	fileName=saveAs.getFile();
        	boolean canceled=fileName==null;
        	if(canceled)return;
        	fileName=new File(saveAs.getDirectory(), fileName).getAbsolutePath();
        	File ff=new File(fileName);
        	if(ff.exists()){
        		int code=MsgBoxUtil.askYesNo(nc, f, "Overwrite?", 
        				"File "+ff.getAbsolutePath()+" exists. Overwrite it?", 
        				new String[]{"Overwrite","Cancel"}, 1);
        		if(code==1)canceled=true;
        		if(canceled)return;
        	}
        	String csvContent=exportToGoogleContacts(addressBook);
        	FileOutputStream fos=null;
        	try{
        		fos=new FileOutputStream(fileName);
        		Writer w=new BufferedWriter(new OutputStreamWriter(fos), 128*1024);
        		w.append(csvContent);
        		w.close();
        	}finally{
        		if(fos!=null){
        			try{
        				fos.close();
        			}catch(Throwable tr){
        				if(fileName!=null)new File(fileName).delete();
        				ExceptionUtil.handleException(nc, tr);
        			}
        		}
        	}
        } catch (Throwable throwable) {
        	if(fileName!=null)new File(fileName).delete();
            ExceptionUtil.handleException(nc, throwable);
        }
    }

    private static String exportToGoogleContacts(AddressBook addressBook) throws Exception {
    	AddressBookImpl addressBookImpl=(AddressBookImpl) addressBook;
		return addressBookImpl.encodeAsGoogleCsv();
	}

	private void setRightPane(Component c, Merkaba agent) {
        currentAgent=agent;
        rightPane.removeAll();
        rightPane.add(BorderLayout.CENTER,c);
        rightPane.invalidate();
        rightPane.validate();
    }

    public static void showNewABFrame(NamedCaller nc, ab app, AddressBook addressBook,String title, String preferencesKey, final WindowListener actionListener) {
        CFrame f=new CFrame(nc, title,preferencesKey){
			private static final long serialVersionUID = -5225306661350595805L;

			public Dimension getPreferredSize() {
                return ScreenBoundsUtil.getScreenBounds().getSize();
            }

            protected int getDefaultExtendedState() {
                return Frame.MAXIMIZED_BOTH;
            }
        };
        AddressBookPane c=new AddressBookPane(nc, app, addressBook, f);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(c);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.addWindowListener(actionListener);
        f.setVisible(true);
    }}
