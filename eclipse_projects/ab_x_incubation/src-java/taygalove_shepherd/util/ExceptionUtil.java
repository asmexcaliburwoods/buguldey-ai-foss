package taygalove_shepherd.util;

import taygalove_shepherd.NamedCaller;

public class ExceptionUtil {
    public static void handleException(NamedCaller nc, Throwable throwable) {
        MsgBoxUtil.showError(nc, "", throwable);
    }

    public static String getExceptionMessage(String error1, Throwable tr) {
        return StringUtil.isEmptyTrimmed(error1)?""+tr:taygalove_shepherd.i18n.m.M.format(taygalove_shepherd.i18n.m.M.EXCEPTION_MSG_FMT, new String[]{error1.trim(), ""+tr});
    }
}
