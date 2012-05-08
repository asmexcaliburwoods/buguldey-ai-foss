::javac -classpath ../jnistb -d . Util.java
::javah dts.netapp.util.Util

gcc -fvtable-thunks -mno-cygwin -Wl,--kill-at -shared prefs.cpp -oprefs.dll -ID:\java\jdk1.4.2_01\include -I../include -lshlwapi
::
::gcc -shared -Wl,--kill-at util.o
::-mno-cygwin
::-fvtable-thunks 
::dllwrap --dllname=util.dll --driver-name=gcc util.o 
::--def=util.def
::del util.o

::call r