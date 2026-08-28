# Motor de Resumen y Extracción para Documentos Legales

Analiza contratos de arrendamiento, laborales y términos y condiciones usando la API de Groq,
extrayendo cláusulas, riesgos y un resumen ejecutivo. Implementado con el patrón **Abstract Factory**
para mantener la lógica de cada tipo de documento aislada.

## Requisitos

- Java 17+
- Maven
- Una API key de Groq (https://console.groq.com)

## Configuración

Nunca pongas la API key en el código. Expórtala como variable de entorno:

```bash
export GROQ_API_KEY=tu_api_key_aqui
```

En PowerShell:

```powershell
$env:GROQ_API_KEY = "tu_api_key_aqui"
```

## Ejecución

```bash
mvn compile exec:java -Dexec.mainClass="com.legalai.Main"
```

## Uso

1. Selecciona el tipo de documento (arrendamiento, laboral o términos y condiciones).
2. Ingresa el texto pegándolo directamente o dando la ruta a un archivo `.txt`.
3. El sistema muestra cláusulas, riesgos (ordenados por severidad) y un resumen ejecutivo.
4. Opcionalmente exporta el reporte a un archivo `.txt` o `.md`.
