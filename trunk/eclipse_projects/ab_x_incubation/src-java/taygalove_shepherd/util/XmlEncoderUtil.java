package taygalove_shepherd.util;

import org.w3c.dom.Node;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.i18n.m.M;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.OutputStream;

public class XmlEncoderUtil {
    public static void encodeAsXml(Node dom, OutputStream os) throws Throwable{
        try{
            DOMSource source=new DOMSource(dom);
            StreamResult result=new StreamResult(os);
            Transformer transformer;
            transformer= TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(source, result);
        } catch (TransformerConfigurationException tce){
            Throwable x=tce;
            if(tce.getException()!=null)x=tce.getException();
            throw x;
        } catch (TransformerException te){
            Throwable x=te;
            if(te.getException()!=null)x=te.getException();
            throw x;
        }
    }
}
