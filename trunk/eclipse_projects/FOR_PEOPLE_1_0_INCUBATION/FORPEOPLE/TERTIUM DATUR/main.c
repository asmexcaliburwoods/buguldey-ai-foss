/* aspect: non-normative file */
/* file: main.c */

void install_on_read_do_eval();
void trigger_read_command_line(int argc, char** argv);
int get_main_exit_code();

int main(int argc, char** argv){
  install_on_read_do_eval();
  //trigger_read_command_line(argc, argv);
  return get_main_exit_code();
}

/* on input from all input devices, do eval(input). */
void install_on_read_do_eval(){
  //...
}
//void new_thread_trigger_read_command_line(int argc, char** argv);
//void trigger_read_command_line(int argc, char** argv){
  //new_thread_trigger_read_command_line(argc,argv);
//}

/* no OS signals means Ok */
int get_main_exit_code(){return 0;}

//void new_thread_trigger_read_command_line(int argc, char** argv){
  //...
//}

