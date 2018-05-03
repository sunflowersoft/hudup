@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.server.ui.PowerServerCP
cd tools\cp

@echo on
