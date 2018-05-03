@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.core.client.RemoteServerCP
cd tools\cp

@echo on
