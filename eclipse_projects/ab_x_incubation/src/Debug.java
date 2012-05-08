class Debug{
  static void error(Throwable tr){
    tr.printStackTrace();
  }
  static void debug(String s){
    System.err.println(s);
  }
}