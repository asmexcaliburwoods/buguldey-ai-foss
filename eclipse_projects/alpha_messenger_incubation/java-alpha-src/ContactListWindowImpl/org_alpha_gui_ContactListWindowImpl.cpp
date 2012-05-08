#include "jni.h"
#include "org_alpha_gui_ContactListWindowImpl.h"
#include <windows.h>

void fatal(LPSTR lpszFunction) 
{ 
  DWORD lerr=GetLastError();
  LPVOID lpMsgBuf;
  if (!FormatMessage( 
      FORMAT_MESSAGE_ALLOCATE_BUFFER | 
      FORMAT_MESSAGE_FROM_SYSTEM | 
      FORMAT_MESSAGE_IGNORE_INSERTS,
      NULL,
      lerr,
      MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
      (LPTSTR) &lpMsgBuf,
      0,
      NULL ))
  {
    char buffer[256];
    _snprintf(
      buffer,sizeof(buffer),
      "%s failed: GetLastError=%d.",lpszFunction,lerr);
    buffer[sizeof(buffer)-1]=0;
    MessageBox( NULL, buffer, "Error", MB_OK | MB_ICONINFORMATION );
  }else{
    char buffer[256];
    _snprintf(
      buffer,sizeof(buffer),
      "%s failed: %s",lpszFunction,lpMsgBuf);
    buffer[sizeof(buffer)-1]=0;

    MessageBox( NULL, buffer, "Error", MB_OK | MB_ICONINFORMATION );
  }
  LocalFree(lpMsgBuf);
  //ExitProcess(1);
}


LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
  /*JavaVM *vms[2];
  jsize nVMs;
  jint err=JNI_GetCreatedJavaVMs((JavaVM **)&vms,2,&nVMs);
  if(err)return 0;
  if(nVMs>1)return 0;
  if(nVMs==0)return 0;
  JavaVM *vm=vms[0];

  JNIEnv *env;
  err=vm->AttachCurrentThread((void**)&env,NULL);
  if(err)return 0;

  LRESULT ret=0;//callcode();
  vm->DetachCurrentThread();
  return ret;
  */
  return DefWindowProc(hWnd, message, wParam, lParam);
}

/*
 * Class:     org_aplha_gui_ContactListWindowImpl
 * Method:    createWindow_getHWND
 * Signature: ()I
 */
extern "C" JNIEXPORT jint Java_org_alpha_gui_ContactListWindowImpl_createWindow_1getHWND
  (JNIEnv*env, jclass clazz){
  HINSTANCE hInstance=(HINSTANCE)GetModuleHandle(NULL);

  WNDCLASSEX wcex;

  wcex.cbSize = sizeof(WNDCLASSEX); 

  wcex.style      = 0;
  wcex.lpfnWndProc  = (WNDPROC)WndProc;
  wcex.cbClsExtra   = 0;
  wcex.cbWndExtra   = 0;
  wcex.hInstance    = hInstance;
  wcex.hIcon      = NULL;
  wcex.hCursor    = NULL;
  wcex.hbrBackground  = (HBRUSH)GetStockObject(WHITE_BRUSH);
  wcex.lpszMenuName = NULL;
  wcex.lpszClassName  = "org_alpha_gui_ContactListWindowImpl";
  wcex.hIconSm    = NULL;

  RegisterClassEx(&wcex);

  HWND hWnd;

  hWnd = CreateWindow("org_alpha_gui_ContactListWindowImpl", "", 
      0,//todo replace style with 0
      CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, NULL, NULL, hInstance, NULL);

  if (!hWnd){
    fatal("CreateWindow");
    return 0;
  }
  
  SetWindowLong(hWnd, GWL_EXSTYLE, WS_EX_TOOLWINDOW);//hide from taskbar & task switcher
  SetWindowLong(hWnd, GWL_STYLE,
    GetWindowLong(hWnd, GWL_STYLE) & ~(WS_CAPTION | WS_SYSMENU | WS_MINIMIZEBOX));//remove title bar
    
  return (jint)hWnd;
}

/*
 * Class:     org_alpha_gui_ContactListWindowImpl
 * Method:    hideWindow
 * Signature: (I)I
 */
JNIEXPORT void Java_org_alpha_gui_ContactListWindowImpl_hideWindow
  (JNIEnv *env, jclass c, jint hwnd){
  ShowWindow((HWND)hwnd,SW_HIDE);
}

/*
 * Class:     org_alpha_gui_ContactListWindowImpl
 * Method:    showWindow
 * Signature: (I)I
 */
JNIEXPORT void Java_org_alpha_gui_ContactListWindowImpl_showWindow
  (JNIEnv *env, jclass c, jint hwnd){
  ShowWindow((HWND)hwnd,SW_SHOW);
}

/*
 * Class:     org_alpha_gui_ContactListWindowImpl
 * Method:    setForeground0
 * Signature: (I)V
 */
JNIEXPORT void Java_org_alpha_gui_ContactListWindowImpl_setForeground0
  (JNIEnv *env, jclass c, jint hwnd){
  SetForegroundWindow((HWND)hwnd);
}

/*
 * Class:     org_alpha_gui_ContactListWindowImpl
 * Method:    getActiveWindowHWND
 * Signature: ()I
 */
JNIEXPORT jint Java_org_alpha_gui_ContactListWindowImpl_getActiveWindowHWND
  (JNIEnv * e, jclass c){
  return (jint)GetActiveWindow();
}

/*
 * Class:     org_alpha_gui_ContactListWindowImpl
 * Method:    getForegroundWindowHWND
 * Signature: ()I
 */
JNIEXPORT jint Java_org_alpha_gui_ContactListWindowImpl_getForegroundWindowHWND
  (JNIEnv * e, jclass c){
  return (jint)GetForegroundWindow();
}
