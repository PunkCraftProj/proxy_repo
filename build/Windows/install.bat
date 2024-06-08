@echo off
REM COPY
cd ../../
cd target
copy /Y OneWorldProxy-0.1-SNAPSHOT.jar ..\..\ServerOneWorld\proxy_server\plugins

echo Файлы успешно скопированы.
exit