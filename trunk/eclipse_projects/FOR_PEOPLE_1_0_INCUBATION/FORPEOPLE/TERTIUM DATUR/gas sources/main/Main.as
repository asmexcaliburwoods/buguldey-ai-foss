/* aspect: non-normative file */
/* file: Main.as */

	tbd

/package forpeople.main;

/import forpeople.events.KeyboardsReadEventGenerator;
/import forpeople.machinebrain.CommandLineArgumentsMachineBrainImpl;
/import forpeople.machinebrain.KeyboardDevicesMachineBrainImpl;
/import forpeople.processingpools.ReadEventProcessingPool;

/**
 * @author E.G.Philippov
 */
/public class Main {
Main:
	jmp	main
/  private static ReadEventProcessingPool commandLineArgumentsProcessingPool=
/	  new ReadEventProcessingPool("ProcessingPool for command line arguments", new CommandLineArgumentsMachineBrainImpl());
commandLineArgumentsProcessingPool:
/type:	ptr to ReadEventProcessingPool
	ptr
lit1:
	StringLit "ProcessingPool for command line arguments"
commandLineArgumentsProcessingPool_init:
	mov		%eds:%ebx,lit1 
	call	CommandLineArgumentsMachineBrainImpl_new
	call	ReadEventProcessingPool_new
	mov		[commandLineArgumentsProcessingPool],%eds:%ebx
	ret
	
	further is tbd
	
  private static void unhandledThrowable(Throwable thr){
    thr.printStackTrace();
  }

  /** on input from all input devices, do eval(input).  this is just an aspect of the entire system, so the implementation is dispersed over all code. */
  private static void install_on_read_do_eval(String[] commandLineArgs){
    trigger_read_command_line(commandLineArgs);
    install_keyboard_devices_read_event_generator();
  }

  private static void install_keyboard_devices_read_event_generator() {
	KeyboardsReadEventGenerator.installAllKeyboardDevices(new KeyboardDevicesMachineBrainImpl());	
  }

  private static void read_command_line(final String[] args){
	  commandLineArgumentsProcessingPool.addReadEvent(new CommandLineSentEvent(){
		@Override
		public String[] getCommandLine() {
			return args;
		}

		@Override
		public void debugToStdOut() {
			StringBuilder sb=new StringBuilder();
			sb.append("Command line: ");
			boolean first=true;
			for(String arg:args){
				if(!first)sb.append(" ");
				sb.append(arg);
				first=false;
			}
			sb.append("\n");
			System.out.print(sb);
		}    	
    });
  }

  private static void new_thread_trigger_read_command_line(final String[] args){
    Thread t=new Thread("trigger_read_command_line"){
      public void run(){
        try{
          read_command_line(args);
        }catch(Throwable thr){
          unhandledThrowable(thr);
        }
      }
    };
    //t.setDaemon(true);
    t.start();
  }
  private static void trigger_read_command_line(String[] args){
    new_thread_trigger_read_command_line(args);
  }
//  /** "No OS signals" means "O.k.". */
//  private static int get_main_exit_code(){
//    return 0;
//  }
  /**
   * @param args the incoming command line
   */
  public static void main(String[] args){
    install_on_read_do_eval(args);
  }
}