package taygalove_shepherd.util;

import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

public class XmlUtil {
    private static final DocumentBuilderFactory documentBuilderFactory=
            DocumentBuilderFactory.newInstance();
    static{
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setExpandEntityReferences(true);
    }
    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return documentBuilderFactory.newDocumentBuilder();
    }
    public static String getTextNodeContent(Element e){
        return e.getFirstChild()==null?"":e.getFirstChild().getNodeValue();
    }
}
