package taygalove_shepherd.util;

public class OSVersionUtil {
	public static boolean isMelkosoftOkna(){
		return (""+System.getProperty("os.version").toLowerCase()).contains("windows");
	}
}
