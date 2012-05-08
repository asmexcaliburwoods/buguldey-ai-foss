::pexports.exe shlwapi.dll |sed 's/^_//' > shlwapi.def
dlltool --input-def shlwapi.def --dllname shlwapi.dll --output-lib libshlwapi.a -k
cp libshlwapi.a /usr/lib/w32api
ranlib /usr/lib/w32api/libshlwapi.a 