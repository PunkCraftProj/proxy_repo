@echo off
REM RUN start.bat
cd ..\..\
cd ..\ServerOneWorld
cd Server1
start start.bat
cd ..\Server2
start start.bat
cd ..\proxy_server
start start.bat
cd ..\hub
start start.bat

echo Cерверы запущены.
exit