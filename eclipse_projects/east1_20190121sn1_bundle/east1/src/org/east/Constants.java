package org.east;

public class Constants{
  public static final String TITLE="Project East";
  public static final String VERSION="001";
  public static final String TITLE_WITH_VERSION=TITLE+" v."+VERSION;
  public static final int LOGLEVEL_TRACE=1;
  public static final int LOGLEVEL_DEBUG=2;
  public static final int LOGLEVEL_INFO=3;
  public static final int LOGLEVEL_WARNING=4;
  public static final int LOGLEVEL_ERROR=5;
  public static final int LOGLEVEL=LOGLEVEL_DEBUG;
  public static boolean isLogLevelTrace(){
    return LOGLEVEL<=LOGLEVEL_TRACE;
  }
  public static boolean isLogLevelDebug(){
    return LOGLEVEL<=LOGLEVEL_DEBUG;
  }
  public static boolean isLogLevelInfo(){
    return LOGLEVEL<=LOGLEVEL_INFO;
  }
}
