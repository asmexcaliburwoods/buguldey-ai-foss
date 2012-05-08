#ifndef XWINDOW_H_OB
#define XWINDOW_H_OB

//sleep()
#include <unistd.h>
#include <stdlib.h>
#include <vga.h>
//#include <X11/extensions/Xrandr.h>
//#include "X11/Xatom.h"
//#include <X11/Xlib.h>
#include <stdio.h>
#include <string.h>

typedef struct {
	unsigned long   flags;
	unsigned long   functions;
	unsigned long   decorations;
	long            inputMode;
	unsigned long   status;
  } Hints;

static const int OBXW_SUCCESS=0;
//static const int OBXW_ERR_XInternAtom_MOTIF_WM_HINTS_FAILED=1;
//static const int OBXW_ERR_XOpenDisplay_FAILED=2;
//static const int OBXW_ERR_XRANDR_NOT_SUPPORTED=3;
//static const int OBXW_ERR_XMoveResizeWindow_BadValue=4;
//static const int OBXW_ERR_XMoveResizeWindow_BadWindow=5;

void vga_shutdown_routine(){
	   vga_init();
	   vga_setmode(TEXT);
}

/**
 * @return OBXW_SUCCESS on success, OBXW_ERR_* on errors.
 */
int go_full_screen_and_print_prompt_and_readkey(){
   vga_safety_fork(&vga_shutdown_routine);w
   vga_init();
   vga_setmode(G320x200x256);
   vga_setcolor(4);
   vga_drawpixel(10, 10);

   vga_getch();
   vga_setmode(TEXT);

//  Display *display;
//  Window window;
//  XEvent e;
//  char msg[] = "Ok";
//  int s;
//
//  display = XOpenDisplay(NULL);
//  if (display == NULL) return OBXW_ERR_XOpenDisplay_FAILED;
//  s = DefaultScreen(display);
//  window = XCreateSimpleWindow(display, RootWindow(display, s), 10, 10, 100, 100, 1, BlackPixel(display, s), WhitePixel(display, s));
//  XSelectInput(display, window, ExposureMask | KeyPressMask);
//  XMapWindow(display, window);
//
//
//
//  //hide the window's borders
//  Hints   hints;
//  Atom    property;
//
//  hints.flags = 2;        // Specify that we're changing the window decorations.
//  hints.decorations = 0;  // 0 (false) means that window decorations should go bye-bye (be hidden).
//
//  //The "display" parameter is the X11 Display variable returned from XOpenDisplay, as you may have guessed
//  property = XInternAtom(display,"_MOTIF_WM_HINTS",True);
//  if(property==0)return OBXW_ERR_XInternAtom_MOTIF_WM_HINTS_FAILED;
//
//  //Again, "display" is your Display variable, while "window" is the Window variable that identifies your already-existing window.
//  XChangeProperty(display,window,property,property,32,PropModeReplace,(unsigned char *)&hints,5);
//
//
//
//  //change the display resolution
//  /*
//	XF86VidModeSwitchToMode(display,defaultscreen,video_mode);
//	XF86VidModeSetViewPort(display,DefaultScreen,0,0);
//  */
//  //XRRRootToScreen returns the screen number given a root window (for example, from an XRRScreenChangeNotifyEvent.
//  //int XRRRootToScreen(  Display *dpy,  Window root);
//  puts("1");
//  int screenNumber = XRRRootToScreen(display, window);
//  puts("2");
//
//  /*
//	typedef struct {
//		int width, height;
//		int mwidth, mheight;
//	} XRRScreenSize;
//  */
//
//  int nsizes=1;
//  XRRScreenSize * aScreenSize=XRRSizes(display, screenNumber, &nsizes);
//  if(aScreenSize==0)return OBXW_ERR_XRANDR_NOT_SUPPORTED;
//  puts("3");
//
//  Status result=XMoveResizeWindow(display,window,0,0,aScreenSize->width,aScreenSize->height);
//  if(result==BadValue)return OBXW_ERR_XMoveResizeWindow_BadValue;
//  if(result==BadWindow)return OBXW_ERR_XMoveResizeWindow_BadWindow;
//  //BadValue - values out of range
//  //BadWindow - ошибка указания идентификатора окна.
//
//  puts("4");
//  XMapRaised(display,window);
//  XGrabPointer(display,window,True,0,GrabModeAsync,GrabModeAsync,window,0L,CurrentTime);
//  XGrabKeyboard(display,window,False,GrabModeAsync,GrabModeAsync,CurrentTime);
//  /*
//    The only part of the above code block that isn't self-explanatory is the "video_mode" variable. This is one of the entries in the array returned by a call to XF86VidModeGetAllModeLines(), something which I'm not going to cover here. Grabbing the pointer and keyboard within the window is necessary to keep the virtual desktop from scrolling, and to make sure that the window has exclusive use of the keyboard and mouse.
//  */
//
//  while (1) {
//	      XNextEvent(display, &e);
//	      if (e.type == Expose) {
//	         XFillRectangle(display, window, DefaultGC(display, s), 20, 20, 10, 10);
//	         XDrawString(display, window, DefaultGC(display, s), 10, 50, msg, strlen(msg));
//	         continue;
//	      }
//	      if (e.type == KeyPress) break;
//  }
//
//  XCloseDisplay(display); display=0;
  return OBXW_SUCCESS;
}

int main(){
	return go_full_screen_and_print_prompt_and_readkey();
}

#endif /* XWINDOW_H_OB */
