package taygalove_shepherd.i18n.m;

import java.util.Properties;
import java.util.Locale;
import java.text.MessageFormat;

import taygalove_shepherd.NamedCaller;

public class M {
	private static Properties p;
    static{
    	staticinit(new NamedCaller() {
			@Override
			public String name() {
				return "i18n.m.M.staticinit";
			}
		});
    }
    private static void staticinit(NamedCaller nc){
    	p=MLoader.load(nc);
    }

    private static String get(String key){
    	if(p==null)throw new AssertionError("p is null");
    	return (String)p.get(key);
    }

    public static String JAVA_LOCALE_LANGUAGE;//initialized by MLoader.load()
    public static String JAVA_LOCALE_COUNTRY; //initialized by MLoader.load()
    public static String JAVA_LOCALE_VARIANT; //initialized by MLoader.load()
    public static Locale DEFAULT_LOCALE;       //initialized by MLoader.load()
    public static Locale getDefaultLocale(){return DEFAULT_LOCALE;}

    public static String format(String pattern,String[] args){
        return MessageFormat.format(pattern,(/*nowarn*/Object[])args);
    }

    public static String JAVA_DATE_FORMAT = get("JAVA_DATE_FORMAT"); // "MMMM dd, yyyy";
    public static String EXCEPTION_MSG_FMT = get("EXCEPTION_MSG_FMT"); //{0}: {1}
    public static String OK = get("OK"); //OK
    public static String MORE1=get("MORE1"); //More...
    public static String LESS1 = get("LESS1"); //Less...
    public static String INFORMATION = get("INFORMATION"); //Information
    public static String ERROR = get("ERROR"); //Error
	public static String UNNAMED_CALLER = get("UNNAMED_CALLER"); //Unnamed caller
	public static String SAYS = get("SAYS"); //{0} says: {1}
	public static String SILENTLY_SAYS1 = get("SILENTLY_SAYS1"); //{1} - {0}
	public static String UNKNOWN_ERROR = get("UNKNOWN_ERROR"); //Unknown error.
	public static String NAME_AND_VERSION1 = get("NAME_AND_VERSION1"); //{0} v.{1}
	public static String UNKNOWN = get("UNKNOWN");//Unknown
	public static String MERKABA = get("MERKABA");//Merkaba
    public static String CONTACTS = get("CONTACTS");//Contacts
}
