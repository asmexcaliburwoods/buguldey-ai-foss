package kernel.util;


import java.text.MessageFormat;

public class ToString {
	public static String callerToNameOfCaller(Caller caller){
		if (caller instanceof NamedCaller){
			NamedCaller namedCaller=(NamedCaller) caller;
			return namedCaller.name();
		}
		return "UNNAMED_CALLER";
	}
	public static String callerToNameAndVersionOfCaller(Caller caller){
		String version="Unknown";
		if(caller instanceof NamedVersionedCaller){ 
			NamedVersionedCaller nvcaller=(NamedVersionedCaller) caller;
			version=nvcaller.versionString();
		}
		return MessageFormat.format("{0} v.{1}", callerToNameOfCaller(caller), version);
	}
}
