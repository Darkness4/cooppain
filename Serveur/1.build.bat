set CLASSPATH=".;%GLASSFISH_ROOT%\glassfish\lib\javaee.jar;%GLASSFISH_ROOT%\glassfish\lib\appserv-rt.jar;%GLASSFISH_ROOT%\glassfish\lib\gf-client.jar"

del /F /Q /S build\*

javac -classpath %CLASSPATH% -d build cooppain\entities\*.java cooppain\exceptions\*.java cooppain\sessions\*.java cooppain\sessions\impl\*.java
del build\cooppain\entities\*_.class build\cooppain\exceptions\*_.class build\cooppain\sessions\*_.class build\cooppain\sessions\impl\*_.class

xcopy /s /i ".\META-INF" ".\build\META-INF"

cd build
jar cvf CoopPainEJB.jar META-INF\*.xml cooppain\entities\*.class cooppain\exceptions\*.class cooppain\sessions\*.class cooppain\sessions\impl\*.class

pause