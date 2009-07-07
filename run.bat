@echo off
rem Starterscript for semantic precision and recall calculator

rem we try some auto detection to find the java.exe
rem JAVA_HOME has to be set correctly

if exist "%SYSTEMROOT%\system32\java.exe" (
	set JAVA="%SYSTEMROOT%\system32\java.exe"
	goto found
)

if defined JAVA_HOME (
	if not exist "%JAVA_HOME%\bin\java.exe" (
		echo No "java.exe" found. Please set the environment variable JAVA_HOME
		echo to point to the Java installation directory.
		echo We will try to run the tool nevertheless maybe the java.exe is in your PATH.
		echo Good luck!
		set JAVA="java.exe"
	)
	set JAVA="%JAVA_HOME%\bin\java.exe"
)
:found
%JAVA% -jar spart.jar %*

@echo on
