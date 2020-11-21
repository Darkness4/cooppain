set CLASSPATH=".;.\CoopPainEJB.jar;%GLASSFISH_ROOT%\glassfish\lib\javaee.jar;%GLASSFISH_ROOT%\glassfish\lib\appserv-rt.jar;%GLASSFISH_ROOT%\glassfish\lib\gf-client.jar"

xcopy /Y ..\Serveur\build\CoopPainEJB.jar .\
javac -classpath %CLASSPATH% Client.java
pause