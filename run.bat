@echo off
:: =====================================================================
:: NutriPlan - Script de Ejecución con SWI-Prolog y JPL
:: =====================================================================

:: Agregamos la ruta de SWI-Prolog al PATH de la sesión para que encuentre las dependencias DLL
set "PATH=%PATH%;C:\Program Files\swipl\bin"

:: Crear directorio de compilados si no existe para mantener el proyecto ordenado
if not exist "bin" mkdir bin

:: Limpiar archivos .class sueltos de la raiz si existieran
del /f /q *.class 2>nul

echo Compilando clases en la carpeta bin...
javac -d bin -cp "C:\Program Files\swipl\lib\jpl.jar" PrologManager.java MenuApp.java
if %errorlevel% neq 0 (
    echo Error durante la compilacion.
    pause
    exit /b %errorlevel%
)

echo Iniciando aplicacion y conectando con SWI-Prolog...
java --enable-native-access=ALL-UNNAMED -Djava.library.path="C:\Program Files\swipl\bin" -cp "bin;C:\Program Files\swipl\lib\jpl.jar" MenuApp

pause