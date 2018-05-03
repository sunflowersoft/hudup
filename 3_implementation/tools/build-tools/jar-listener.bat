@echo off

cd ..\..
if not "%1" == "" goto include-runtime-lib
goto none-include-runtime-lib

:include-runtime-lib
call .\build.bat -Dinclude-runtime-lib=%1 jar-listener
goto end

:none-include-runtime-lib
call .\build.bat jar-listener

:end
cd tools\build-tools

@echo on
