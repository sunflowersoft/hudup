@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.listener.ui.ListenerCP
cd tools\cp

@echo on
