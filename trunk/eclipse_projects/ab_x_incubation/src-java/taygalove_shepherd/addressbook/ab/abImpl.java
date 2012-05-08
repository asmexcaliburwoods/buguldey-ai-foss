package taygalove_shepherd.addressbook.ab;


import javax.swing.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.addressbook.ab.datamodel.AddressBook;
import taygalove_shepherd.addressbook.ab.shepherd.Shepherd;
import taygalove_shepherd.addressbook.ab.ui.pane.AddressBookPane;
import taygalove_shepherd.i18n.m.M;
import taygalove_shepherd.i18n.m.MLoader;
import taygalove_shepherd.sachok.PreferencesForABProject;
import taygalove_shepherd.util.ExceptionUtil;
import taygalove_shepherd.util.LookAndFeelUtil;
import taygalove_shepherd.util.MsgBoxUtil;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class abImpl extends ab {
    public static void main(String[] args){
    	new abImpl().applicationLifecycle(args);
    }
    private void leavingApplicationLifecycle(NamedCaller nc, AddressBook addressBook) throws Throwable{//GTD Throwable is a wolf, work around this
    	Shepherd.moveAddressbookToHDD(nc, abImpl.this,addressBook);
        System.exit(0);
    }
    /**
     * abImpl life cycle.
     * If uninstall option given, uninstall and leave user data which is commanded to be left. 
     * Otherwise: if not installed, install. If installed, live. 9.
     * @param args command line arguments
     */
    private void applicationLifecycle(String[] args){
    	final NamedCaller nc=new NamedCaller() {
			@Override
			public String name() {
				try {
					return abImplConstants.APP_NAME;
				} catch (Throwable e) {
					e.printStackTrace();
					return M.UNNAMED_CALLER;
				}
			}
		}; 
    	while(true){
	        try {
//	        	try{
//	        		M.staticinit(nc);
//	        	}catch(Throwable tr){
//	        		tr.printStackTrace();
//	        		return;
//	        	}
	        	PreferencesForABProject.initLibrary(nc);
	            LookAndFeelUtil.initLAF();
	            final AddressBook addressBook = abPersistence.load(nc);
	            AddressBookPane.showNewABFrame(nc, abImpl.this, addressBook,M.CONTACTS,"ab",new WindowAdapter() {
	                public void windowClosing(WindowEvent e) {
	                    try {
	                    	leavingApplicationLifecycle(nc, addressBook);
	                    } catch (Throwable throwable) {
	                        ExceptionUtil.handleException(nc, throwable);
	                        switch(MsgBoxUtil.askYesNo(nc, (JFrame)e.getSource(),"Cannot save address book---exit now and discard changes?","Error occured while saving.\r\nExit application now and discard your addressbook changes?",new String[]{"Yes","No"}, 1)){//GTD get rid of such messages
	                            case 0:System.exit(1);break;
	                            case 1:
	                            default:throw new RuntimeException(throwable);
	                        }
	                    }
	                }
	            });
	            return;
	        } catch (Throwable throwable) {
	            ExceptionUtil.handleException(nc, throwable);
	        }
    	}
    }

}
