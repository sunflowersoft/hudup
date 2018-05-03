@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.listener.ui.BalancerCP
cd tools\cp

@echo on
