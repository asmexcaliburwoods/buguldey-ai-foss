/* aspect: non-normative file */
/* file: Main.java */

public class Main{
  private static void unhandledThrowable(Throwable thr){
    thr.printStackTrace();
  }

  /** on input from all input devices, do eval(input). */
  private static void install_on_read_do_eval(){
    ProcessingPool.installOnEventDoEval();
  }

  private static void fireReadEvent(ReadEvent ev){
    ProcessingPool.addEvent(ev);
  }

  private static void read_command_line(String[] args){
    fireReadEvent(new CommandLineSentEvent(args));
  }

  private static void new_thread_trigger_read_command_line(final String[] args){
    new Thread("trigger_read_command_line"){
      public void run(){
        try{
          read_command_line(args);
        }catch(Throwable thr){
          unhandledThrowable(thr);
        }
      }
    }.start();
  }
  private static void trigger_read_command_line(String[] args){
    new_thread_trigger_read_command_line(args);
  }
  /** no OS signals means Ok. */
  private static int get_main_exit_code(){
    return 0;
  }
  /**
   * @param args the incoming command line
   */
  public static int main(String[] args){
    install_on_read_do_eval();
    trigger_read_command_line(args);
    return get_main_exit_code();
  }
}
