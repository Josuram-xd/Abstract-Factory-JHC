# Motor de Resumen y Extracción para Documentos Legales

Analiza contratos de arrendamiento, laborales y términos y condiciones usando la API de Groq,
extrayendo cláusulas relevantes, riesgos (con nivel de severidad) y un resumen ejecutivo en
lenguaje claro.

Implementado con el patrón **Abstract Factory**: cada tipo de documento tiene su propia familia
de objetos (extractor de cláusulas + detector de riesgos + generador de resumen), cada uno con
un prompt especializado, sin mezclar lógica entre familias. Agregar un nuevo tipo de documento no
requiere tocar el código cliente, solo sumar una fábrica concreta nueva.

API grok "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"

Tiene dos formas de uso: una interfaz gráfica de escritorio (Swing) y una versión de consola.

## Requisitos

- **Java 17 o superior** (probado con JDK 25).
- **Maven** (si `mvn -v` no te funciona, ver [Instalar Maven](#instalar-maven-si-no-lo-tienes)).
- Una **API key de Groq**, gratis en [console.groq.com](https://console.groq.com).

## Configuración de la API key

**Nunca la escribas dentro del código.** El proyecto la lee siempre de la variable de entorno
`GROQ_API_KEY` (ver `GroqConfig.java`). Si no está configurada, el programa lanza un error claro
al arrancar en vez de fallar en silencio.

### Configurarla de forma permanente (Windows)

```powershell
setx GROQ_API_KEY "tu_api_key_aqui"
```

⚠️ **Importante:** `setx` solo la aplica a **terminales nuevas** abiertas *después* de ejecutar el
comando. Los programas que ya estaban abiertos (VS Code, una terminal existente, etc.) no la ven
hasta que los cierres por completo y los vuelvas a abrir — abrir una pestaña nueva dentro de la
misma ventana de VS Code **no alcanza**, porque hereda el entorno con el que arrancó VS Code. Si
no quieres reiniciar nada, la alternativa rápida es fijarla solo para la sesión actual de la
terminal en la que vas a correr el proyecto:

```powershell
$env:GROQ_API_KEY = "tu_api_key_aqui"
```

(esta dura solo mientras esa ventana de terminal esté abierta).

### macOS / Linux

```bash
export GROQ_API_KEY=tu_api_key_aqui        # sesión actual
echo 'export GROQ_API_KEY=tu_api_key_aqui' >> ~/.bashrc   # permanente
```

### Verificar que quedó bien puesta

```powershell
echo $env:GROQ_API_KEY
```
```bash
echo $GROQ_API_KEY
```

## Instalar Maven (si no lo tienes)

Windows no trae Maven instalado por defecto y no siempre está en `winget`. Instalación manual:

1. Descarga el binario desde [Apache Maven](https://maven.apache.org/download.cgi) (la versión
   "Binary zip archive").
2. Descomprímelo en una carpeta fija, por ejemplo `C:\Users\<tu_usuario>\tools\apache-maven-x.x.x`.
3. Agrega su carpeta `bin` al `PATH` de usuario:
   ```powershell
   $mavenBin = "C:\Users\<tu_usuario>\tools\apache-maven-x.x.x\bin"
   $currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
   [Environment]::SetEnvironmentVariable("Path", "$currentPath;$mavenBin", "User")
   ```
4. Igual que con la API key: esto solo se aplica a terminales nuevas. Si no quieres reiniciar,
   llama a Maven por su ruta completa mientras tanto:
   ```powershell
   & "C:\Users\<tu_usuario>\tools\apache-maven-x.x.x\bin\mvn.cmd" -v
   ```

## Ejecución

**Interfaz gráfica (por defecto):**

```bash
mvn compile exec:java
```

**Versión de consola:**

```bash
mvn compile exec:java -Dexec.mainClass="com.legalai.Main"
```

## Uso

1. Selecciona el tipo de documento (arrendamiento, laboral o términos y condiciones).
2. Ingresa el texto pegándolo directamente o cargando/dando la ruta a un archivo `.txt`.
3. Dale a "Analizar" (o espera en consola). El sistema muestra:
   - Las cláusulas detectadas.
   - Los riesgos, ordenados por severidad (ALTO primero) y coloreados (rojo/naranja/verde).
   - Un resumen ejecutivo con puntos clave y una recomendación final.
4. Opcionalmente exporta el reporte a un archivo `.txt`.

## Modelo usado

El cliente (`GroqConfig.java`) usa `openai/gpt-oss-120b` sobre el endpoint
`https://api.groq.com/openai/v1/chat/completions`, con `response_format: json_object` para forzar
salida JSON parseable. Groq deprecia y rota modelos con cierta frecuencia — si ves un error
`model_not_found` (HTTP 404), consulta los modelos activos de tu cuenta con:

```bash
curl https://api.groq.com/openai/v1/models -H "Authorization: Bearer $GROQ_API_KEY"
```

y actualiza la constante `MODELO` en `GroqConfig.java` por uno de los que tenga `"active": true`
y `"json_mode"` en `supported_features`.

## Estructura del proyecto

```
src/main/java/com/legalai/
├── Main.java              # entrada de la versión de consola
├── MainSwing.java          # entrada de la interfaz gráfica
├── config/                # lectura de GROQ_API_KEY y constantes del modelo
├── client/                # GroqClient: llamada HTTP compartida a la API de Groq
├── model/                 # records inmutables: Clausula, Riesgo, ResumenEjecutivo, etc.
├── factory/               # Abstract Factory: ContratoFactory + 3 fábricas concretas
├── productos/              # interfaces de producto + implementación por familia de documento
│   ├── arrendamiento/
│   ├── laboral/
│   ├── terminos/
│   └── soporte/           # parseo de JSON con reintento, compartido por las 9 clases concretas
├── service/               # ProcesadorDocumento: orquesta fábrica → productos → reporte
└── ui/                     # ConsolaUI/ReporteFormatter (consola) y swing/ (interfaz gráfica)
```

## Problemas comunes

- **"mvn no se reconoce" después de instalarlo** → abre una terminal nueva, o si sigue igual,
  cierra sesión de Windows (o reinicia) para que el `PATH` se actualice en todos los procesos.
- **"Falta la variable de entorno GROQ_API_KEY" aunque ya la configuraste** → mismo problema:
  la terminal/IDE que estás usando arrancó antes de configurarla. Usa `$env:GROQ_API_KEY = "..."`
  en esa sesión, o reinicia la terminal/IDE.
- **Errores de "package does not match" en VS Code** → el Java Language Server no importó el
  proyecto como Maven (puede pasar si abriste la carpeta antes de que existiera `pom.xml`).
  Soluciónalo con `Ctrl+Shift+P` → **"Java: Clean the Java Language Server Workspace"** → reiniciar.
- **`model_not_found` (HTTP 404) de Groq** → ver [Modelo usado](#modelo-usado).

## Seguridad

- La API key nunca debe pegarse en código, commits, ni compartirse en texto plano (chats, tickets,
  etc.). Si una key quedó expuesta en algún lugar, revócala en [console.groq.com](https://console.groq.com)
  y genera una nueva.
- `target/` (el build de Maven) y `dependency-reduced-pom.xml` están en `.gitignore` — no deberían
  subirse al repositorio.
