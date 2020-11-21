set CLASSPATH=".;.\CoopPainEJB.jar;%GLASSFISH_ROOT%\glassfish\lib\javaee.jar;%GLASSFISH_ROOT%\glassfish\lib\appserv-rt.jar;%GLASSFISH_ROOT%\glassfish\lib\gf-client.jar"

java -classpath %CLASSPATH% Client
pause