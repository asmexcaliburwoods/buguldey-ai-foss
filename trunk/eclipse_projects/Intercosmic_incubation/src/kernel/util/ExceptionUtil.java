package kernel.util;

import java.text.MessageFormat;

public class ExceptionUtil {
    public static void handleException(Throwable tr) {
        tr.printStackTrace();
        Thread.dumpStack();
    }

    public static void handleException(String s, Throwable throwable) {
        System.err.println(s);
        handleException(throwable);
    }
    public static String getExceptionMessage(String error1, Throwable tr) {
        return StringUtil.isEmptyTrimmed(error1)?""+tr:""+MessageFormat.format("{0}: {1}", error1.trim(), ""+tr);
    }
}
