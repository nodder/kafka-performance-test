@echo off

set thread_num=1
set number_per_batch=1
set bootstrap_servers=hadoop2:9092,hadoop3:9092,hadoop4:9092
set kafka_topic=pre_ss
set config_file=test.cdd

rem topic: test

:Set_Title
title Kafka Sender by CDD

:Set_JAVA_HOME
if not DEFINED JAVA_HOME (
    set JAVA_HOME=
)

:Set_classpath
set classpath=%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar;
set MAIN_LIB=.\target\kafka-pf-sender-1.0-jar-with-dependencies.jar
set "classpath=!classpath!;%MAIN_LIB%"


rem setlocal enabledelayedexpansion
rem set MAIN_LIB=.\lib
rem for /f  %%a in ('dir /b "%MAIN_LIB%"') do (
rem set "classpath=!classpath!;%MAIN_LIB%\%%a"
rem )

:Is_Need_JAVA_OPTS -- add/remove rem
rem set JAVA_OPTS=-Xdebug -Xnoagent -XX:+PrintGCDetails -verbose:gc -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8789,server=y,suspend=y %JAVA_OPTS%

:Set_Java_Process -- choose 1 in 2
set runProcess=%JAVA_HOME%\bin\java
rem set runProcess=start javaw

:Check_Before_Start
if not EXIST %JAVA_HOME% (
     echo Please specify JAVA_HOME before running
	 pause
     goto End
)

:Start_Java
%runProcess% %JAVA_OPTS% -Xms20m -Xmx1000m name.cdd.product.kafka.pftest.PFTestMain %thread_num% %number_per_batch% %bootstrap_servers% %kafka_topic% %config_file%
 
:End