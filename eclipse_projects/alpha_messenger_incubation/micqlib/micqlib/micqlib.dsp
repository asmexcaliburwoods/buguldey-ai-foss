# Microsoft Developer Studio Project File - Name="micqlib" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=micqlib - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "micqlib.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "micqlib.mak" CFG="micqlib - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "micqlib - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "micqlib - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "micqlib - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "MICQLIB_EXPORTS" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "MICQLIB_EXPORTS" /Yu"stdafx.h" /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x419 /d "NDEBUG"
# ADD RSC /l 0x419 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /machine:I386

!ELSEIF  "$(CFG)" == "micqlib - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "MICQLIB_EXPORTS" /Yu"stdafx.h" /FD /GZ /c
# ADD CPP /nologo /MTd /w /W0 /Gm /GX /ZI /Od /I "D:\devjoe\alpha\micqlib\include" /I "D:\devjoe\alpha\micqlib\micqlib" /I "D:\devjoe\alpha\micqlib\micqlib\micq" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "MICQLIB_EXPORTS" /FR /YX"stdafx.h" /FD /GZ /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x419 /d "_DEBUG"
# ADD RSC /l 0x419 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /debug /machine:I386 /pdbtype:sept
# ADD LINK32 Ws2_32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /debug /machine:I386 /out:"micq/Debug/micqlib.dll" /pdbtype:sept
# SUBTRACT LINK32 /incremental:no

!ENDIF 

# Begin Target

# Name "micqlib - Win32 Release"
# Name "micqlib - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Group "micqlib"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\src\buildmark.c
# End Source File
# Begin Source File

SOURCE=..\src\cmd_user.c
# End Source File
# Begin Source File

SOURCE=..\src\connection.c
# End Source File
# Begin Source File

SOURCE=..\src\contact.c
# End Source File
# Begin Source File

SOURCE=..\src\conv.c
# End Source File
# Begin Source File

SOURCE=..\src\file_util.c
# End Source File
# Begin Source File

SOURCE=..\src\i18n.c
# End Source File
# Begin Source File

SOURCE=..\src\icq_response.c
# End Source File
# Begin Source File

SOURCE=..\src\im_icq8.c
# End Source File
# Begin Source File

SOURCE=..\src\micq.c
# End Source File
# Begin Source File

SOURCE=..\src\mreadline.c
# End Source File
# Begin Source File

SOURCE=..\src\msg_queue.c
# End Source File
# Begin Source File

SOURCE=..\src\oldicq_base.c
# End Source File
# Begin Source File

SOURCE=..\src\oldicq_client.c
# End Source File
# Begin Source File

SOURCE=..\src\oldicq_compat.c
# End Source File
# Begin Source File

SOURCE=..\src\oldicq_server.c
# End Source File
# Begin Source File

SOURCE=..\src\oldicq_util.c
# End Source File
# Begin Source File

SOURCE=..\src\os.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_base.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_bos.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_contact.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_icbm.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_location.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_oldicq.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_register.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_roster.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_service.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_snac.c
# End Source File
# Begin Source File

SOURCE=..\src\oscar_tlv.c
# End Source File
# Begin Source File

SOURCE=..\src\packet.c
# End Source File
# Begin Source File

SOURCE=..\src\peer_file.c
# End Source File
# Begin Source File

SOURCE=..\src\preferences.c
# End Source File
# Begin Source File

SOURCE=..\src\remote.c
# End Source File
# Begin Source File

SOURCE=..\src\server.c
# End Source File
# Begin Source File

SOURCE=..\src\tabs.c
# End Source File
# Begin Source File

SOURCE=..\src\tcp.c
# End Source File
# Begin Source File

SOURCE=..\src\util.c
# End Source File
# Begin Source File

SOURCE=..\src\util_alias.c
# End Source File
# Begin Source File

SOURCE=..\src\util_io.c
# End Source File
# Begin Source File

SOURCE=..\src\util_opts.c
# End Source File
# Begin Source File

SOURCE=..\src\util_parse.c
# End Source File
# Begin Source File

SOURCE=..\src\util_rl.c
# End Source File
# Begin Source File

SOURCE=..\src\util_ssl.c
# End Source File
# Begin Source File

SOURCE=..\src\util_str.c
# End Source File
# Begin Source File

SOURCE=..\src\util_syntax.c
# End Source File
# Begin Source File

SOURCE=..\src\util_table.c
# End Source File
# Begin Source File

SOURCE=..\src\util_tcl.c
# End Source File
# Begin Source File

SOURCE=..\src\util_ui.c
# End Source File
# End Group
# Begin Source File

SOURCE=.\micqlib.cpp
# End Source File
# Begin Source File

SOURCE=.\StdAfx.cpp
# ADD CPP /Yc"stdafx.h"
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Group "micqlib h"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\include\buildmark.h
# End Source File
# Begin Source File

SOURCE=..\include\cmd_user.h
# End Source File
# Begin Source File

SOURCE=..\include\color.h
# End Source File
# Begin Source File

SOURCE=..\include\connection.h
# End Source File
# Begin Source File

SOURCE=..\include\contact.h
# End Source File
# Begin Source File

SOURCE=..\include\conv.h
# End Source File
# Begin Source File

SOURCE=..\include\datatype.h
# End Source File
# Begin Source File

SOURCE=..\include\file_util.h
# End Source File
# Begin Source File

SOURCE=..\include\i18n.h
# End Source File
# Begin Source File

SOURCE=..\include\icq_response.h
# End Source File
# Begin Source File

SOURCE=..\include\icq_tcp.h
# End Source File
# Begin Source File

SOURCE=..\include\icq_v2.h
# End Source File
# Begin Source File

SOURCE=..\include\icq_v4.h
# End Source File
# Begin Source File

SOURCE=..\include\icq_v5.h
# End Source File
# Begin Source File

SOURCE=..\include\im_icq8.h
# End Source File
# Begin Source File

SOURCE=..\include\micq.h
# End Source File
# Begin Source File

SOURCE=..\include\micqconfig.h
# End Source File
# Begin Source File

SOURCE=..\include\mreadline.h
# End Source File
# Begin Source File

SOURCE=..\include\msg_queue.h
# End Source File
# Begin Source File

SOURCE=..\include\oldicq_base.h
# End Source File
# Begin Source File

SOURCE=..\include\oldicq_client.h
# End Source File
# Begin Source File

SOURCE=..\include\oldicq_compat.h
# End Source File
# Begin Source File

SOURCE=..\include\oldicq_server.h
# End Source File
# Begin Source File

SOURCE=..\include\oldicq_util.h
# End Source File
# Begin Source File

SOURCE=..\include\os.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_base.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_bos.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_contact.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_icbm.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_location.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_oldicq.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_register.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_roster.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_service.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_snac.h
# End Source File
# Begin Source File

SOURCE=..\include\oscar_tlv.h
# End Source File
# Begin Source File

SOURCE=..\include\packet.h
# End Source File
# Begin Source File

SOURCE=..\include\peer_file.h
# End Source File
# Begin Source File

SOURCE=..\include\preferences.h
# End Source File
# Begin Source File

SOURCE=..\include\remote.h
# End Source File
# Begin Source File

SOURCE=..\include\server.h
# End Source File
# Begin Source File

SOURCE=..\include\tabs.h
# End Source File
# Begin Source File

SOURCE=..\include\tcp.h
# End Source File
# Begin Source File

SOURCE=..\include\util.h
# End Source File
# Begin Source File

SOURCE=..\include\util_alias.h
# End Source File
# Begin Source File

SOURCE=..\include\util_io.h
# End Source File
# Begin Source File

SOURCE=..\include\util_opts.h
# End Source File
# Begin Source File

SOURCE=..\include\util_parse.h
# End Source File
# Begin Source File

SOURCE=..\include\util_rl.h
# End Source File
# Begin Source File

SOURCE=..\include\util_ssl.h
# End Source File
# Begin Source File

SOURCE=..\include\util_str.h
# End Source File
# Begin Source File

SOURCE=..\include\util_syntax.h
# End Source File
# Begin Source File

SOURCE=..\include\util_table.h
# End Source File
# Begin Source File

SOURCE=..\include\util_tcl.h
# End Source File
# Begin Source File

SOURCE=..\include\util_ui.h
# End Source File
# End Group
# Begin Source File

SOURCE=.\micqlib.h
# End Source File
# Begin Source File

SOURCE=.\StdAfx.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# Begin Source File

SOURCE=.\ReadMe.txt
# End Source File
# End Target
# End Project
