package taygalove_shepherd.exception;

import org.xml.sax.SAXParseException;

public class SAXParseWrapperException extends Exception{
    private final byte[] text;
    private final SAXParseException ex;

    public SAXParseWrapperException(SAXParseException cause, byte[] text) {
        super(cause);
        this.ex=cause;
        this.text=text;
    }

    public byte[] getText() {
        return text;
    }

    public SAXParseException getSAXParseException() {
        return ex;
    }
}
