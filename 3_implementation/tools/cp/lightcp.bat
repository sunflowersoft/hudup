@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.core.client.LightRemoteServerCP
cd tools\cp

@echo on
