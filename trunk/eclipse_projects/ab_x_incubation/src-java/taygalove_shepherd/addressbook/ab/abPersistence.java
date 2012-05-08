package taygalove_shepherd.addressbook.ab;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.addressbook.ab.datamodel.AddressBook;
import taygalove_shepherd.addressbook.ab.datamodel.AddressBookImpl;
import taygalove_shepherd.exception.SAXParseWrapperException;
import taygalove_shepherd.util.ExceptionUtil;
import taygalove_shepherd.util.XmlEncoderUtil;
import taygalove_shepherd.util.XmlUtil;

public class abPersistence {
    private static final String AB_FILE_NAME = "ab.xml";
    private static final String AB_FILE_NAME_BACKUP = "ab.backup.xml";

    public static void save(NamedCaller nc, ab app, AddressBook ab) throws Throwable {
        File bak = new File(AB_FILE_NAME_BACKUP);
        if(bak.exists()&&!bak.delete()){
            throw new Exception("Cannot delete backup "+bak.getAbsolutePath());//gtd ask user what to do and repeat, attempt different ways to handle the situation
        }
        File abFile=new File(AB_FILE_NAME);
        if(abFile.exists()){
            if(!abFile.renameTo(bak)){
                throw new Exception("Cannot rename "+abFile.getAbsolutePath()+" to "+AB_FILE_NAME_BACKUP);//gtd ask user what to do and repeat, attempt different ways to handle the situation
            }
        }
        OutputStream os=new FileOutputStream(abFile);
        try{
            BufferedOutputStream bos=new BufferedOutputStream(os,64*1024);
            XmlEncoderUtil.encodeAsXml(ab.encodeAsDomDocument().getDocumentElement(),bos);
            bos.flush();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                ExceptionUtil.handleException(nc, e);
            }
        }

    }

    public static AddressBook load(NamedCaller nc)throws Throwable {
        File abFile=new File(AB_FILE_NAME);
        if(!abFile.exists())return AddressBookImpl.createNewAddressBook();
        DataInputStream dis;
        byte[] buf;
        FileInputStream in = new FileInputStream(abFile);
        try{
            dis=new DataInputStream(in);
            buf=new byte[(int)abFile.length()];
            dis.readFully(buf);
            in.close();
            in=null;
            try{
                ByteArrayInputStream bis=new ByteArrayInputStream(buf);
                Document d= XmlUtil.newDocumentBuilder().parse(bis);
                Element e=d.getDocumentElement();
                return AddressBookImpl.decodeFromDomDocumentElement(e);
            }catch(SAXParseException e1){
                throw new SAXParseWrapperException(e1,buf);
            }
        }finally{
            if(in!=null)try{in.close();}catch(Exception e){
                ExceptionUtil.handleException(nc, e);
            }
        }
    }
}
