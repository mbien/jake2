@echo off
SET PATH=lib\windows;%PATH%
SET CP=lib/jake2.jar;lib/lwjgl.jar;lib/lwjgl_util.jar
java -Xmx80M -Dsun.java2d.noddraw=true -Djava.library.path=lib/windows -cp %CP% jake2.Jake2