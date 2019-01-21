@echo off
set VERSION=%1
if "%VERSION%"=="" goto usage
goto pack

:usage
echo usage: %0 (version)
echo e.g.   %0 1.0.4
goto q

:pack
if exist z-jcq2k.log del z-jcq2k.log 
if errorlevel 1 goto err
echo jcq2k-%VERSION% > VERSION.txt
if errorlevel 1 goto err
zip -9mrq jcq2k-%VERSION%-src.zip *
if errorlevel 1 goto err

echo Success
goto q

:err
echo Errors

:q
