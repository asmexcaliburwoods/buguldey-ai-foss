package gui.util;
import java.text.MessageFormat;

import util.NamedCaller;
import util.StringUtil;

public class ExceptionUtilGUI {
    public static void handleException(NamedCaller nc, Throwable throwable) {
    	handleExceptionImpl(nc, throwable, true, null);
    }
    public static void handleException_dontReportToUser(NamedCaller nc, Throwable throwable){
    	handleExceptionImpl(nc, throwable, false, null);
    }
    public static void handleException_dontReportToUser(NamedCaller nc, String message,Throwable throwable){
    	handleExceptionImpl(nc, throwable, false, message);
    }
    private static void handleExceptionImpl(NamedCaller nc, Throwable throwable, boolean reportToUser, String message) {
    	if(message!=null)System.err.println("Exception handler triggered with message: "+message);
    	throwable.printStackTrace();
    	Thread.dumpStack();    	
        if(reportToUser)MsgBoxUtil.showError(nc, "", throwable);
    }
}
