package taygalove_shepherd.tostring;

import taygalove_shepherd.Caller;
import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.NamedVersionedCaller;
import taygalove_shepherd.i18n.m.M;

public class ToString {
	public static String callerToNameOfCaller(Caller caller){
		if (caller instanceof NamedCaller){
			NamedCaller namedCaller=(NamedCaller) caller;
			return namedCaller.name();
		}
		return M.UNNAMED_CALLER;
	}
	public static String callerToNameAndVersionOfCaller(Caller caller){
		String version=M.UNKNOWN;
		if(caller instanceof NamedVersionedCaller){ 
			NamedVersionedCaller nvcaller=(NamedVersionedCaller) caller;
			version=nvcaller.versionString();
		}
		return M.format(M.NAME_AND_VERSION1, new String[]{callerToNameOfCaller(caller),version});
	}
}
