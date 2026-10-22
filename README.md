# Práctica 5: Generador de Menús con SWI-Prolog y Java Swing

Aplicación de escritorio desarrollada en Java Swing que se comunica con una base de conocimiento en SWI-Prolog (a través de la biblioteca JPL) para calcular la Tasa Metabólica Basal (TMB) de una persona adulta y generar alternativas de menús diarios que cumplan con restricciones calóricas por tiempo de comida.

---

## Requisitos Previos

Antes de ejecutar el proyecto, es necesario tener instaladas las siguientes herramientas en Windows:

1. **Java Development Kit (JDK 17 o superior)**
   - Comprueba que tienes Java instalado abriendo una terminal (`cmd` o `PowerShell`) y escribiendo:
     ```cmd
     java -version
     javac -version
     ```
   - Si no lo tienes, puedes descargarlo desde [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [Adoptium Eclipse Temurin](https://adoptium.net/).

2. **SWI-Prolog (64 bits)**
   - Descarga el instalador desde la página oficial: [SWI-Prolog Downloads](https://www.swi-prolog.org/download/stable).
   - Durante la instalación en Windows:
     - Instálalo en la ruta por defecto: `C:\Program Files\swipl`
     - Asegúrate de marcar la casilla para agregar SWI-Prolog al `PATH` del sistema.
     - Verifica que la biblioteca JPL quede instalada (por defecto se incluye en `C:\Program Files\swipl\lib\jpl.jar`).

---

## Estructura del Proyecto

```text
Practica5/
├── MenuApp.java         # Interfaz gráfica de usuario (Java Swing)
├── PrologManager.java   # Conector entre Java y SWI-Prolog mediante JPL
├── menu_kb.pl           # Base de conocimiento (hechos y reglas de restricciones)
├── run.bat              # Script para compilar y ejecutar automáticamente
├── bin/                 # Carpeta donde se almacenan las clases compiladas (.class)
└── README.md            # Documentación del proyecto
```

---

## Cómo Ejecutarlo

### Opción 1: Ejecución Rápida con `run.bat` (Recomendada)

Haz doble clic en el archivo `run.bat` o ejecútalo desde la terminal:

```cmd
run.bat
```

Este script se encarga de:
1. Agregar temporalmente `C:\Program Files\swipl\bin` a la sesión de trabajo.
2. Crear la carpeta `bin/` si no existe.
3. Compilar los archivos fuente `PrologManager.java` y `MenuApp.java` dentro de `bin/`.
4. Iniciar la aplicación enlazando la biblioteca nativa de Prolog.

---

### Opción 2: Compilación y Ejecución Manual

Si deseas compilar y ejecutar paso a paso desde la terminal de Windows (`cmd.exe`):

1. **Configurar la ruta de las DLLs de Prolog:**
   ```cmd
   set "PATH=%PATH%;C:\Program Files\swipl\bin"
   ```

2. **Compilar las clases en la carpeta `bin`:**
   ```cmd
   if not exist bin mkdir bin
   javac -d bin -cp "C:\Program Files\swipl\lib\jpl.jar" PrologManager.java MenuApp.java
   ```

3. **Ejecutar la aplicación:**
   ```cmd
   java --enable-native-access=ALL-UNNAMED -Djava.library.path="C:\Program Files\swipl\bin" -cp "bin;C:\Program Files\swipl\lib\jpl.jar" MenuApp
   ```

---

## Funcionamiento del Sistema

1. **Cálculo de la TMB (Fórmula de Mifflin-St Jeor):**
   - **Hombres:** `(10 * peso_kg) + (6.25 * altura_cm) - (5 * edad) + 5`
   - **Mujeres:** `(10 * peso_kg) + (6.25 * altura_cm) - (5 * edad) - 161`

2. **Cálculo del IMC:**
   - `IMC = peso_kg / (estatura_m)^2` con clasificación en tiempo real (Bajo peso, Normal, Sobrepeso, Obesidad).

3. **Restricciones Calóricas Evaluadas en Prolog (`menu_kb.pl`):**
   - **Desayuno:** 20% a 30% de la TMB
   - **Almuerzo (Colación matutina):** 10% a 15% de la TMB
   - **Comida:** 30% a 40% de la TMB
   - **Merienda (Colación vespertina):** 10% a 15% de la TMB
   - **Cena:** 20% a 25% de la TMB
   - **Total del Menú:** 90% a 110% de la TMB diaria

4. **Navegación y Exportación:**
   - Los botones **Anterior** y **Siguiente** permiten recorrer las alternativas encontradas por Prolog.
   - El botón **Copiar Menú** copia el desglose completo del menú seleccionado al portapapeles.

---

## Solución de Problemas Frecuentes

* **Error: `UnsatisfiedLinkError: jpl.dll` o `no jpl in java.library.path`:**
  - Ocurre cuando Java no encuentra las DLLs de SWI-Prolog.
  - Asegúrate de que `C:\Program Files\swipl\bin` existe y está en el `PATH`, o ejecuta siempre el proyecto a través de `run.bat`.

* **Error: `ClassNotFoundException: org.jpl7.Query`:**
  - Asegúrate de que la ruta `C:\Program Files\swipl\lib\jpl.jar` sea correcta en tu equipo.

* **Sin menús encontrados:**
  - Verifica que los datos ingresados sean razonables para una persona adulta (por ejemplo: Edad: 25, Estatura: 1.75, Peso: 70).
