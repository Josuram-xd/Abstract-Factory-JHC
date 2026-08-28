# Motor de Resumen y Extracción para Documentos Legales — Especificación para implementación

## 1. Problema

Los despachos, áreas legales y de RRHH reciben constantemente documentos legales de distinta naturaleza (contratos de arrendamiento, contratos laborales, términos y condiciones) que deben ser leídos, interpretados y resumidos manualmente para identificar cláusulas relevantes, riesgos y puntos clave. Este proceso es:

- Lento: leer un contrato completo toma tiempo considerable.
- Propenso a errores humanos: cláusulas de riesgo pueden pasar desapercibidas.
- Inconsistente: cada persona extrae información distinta según su criterio.
- No especializado: un contrato de arrendamiento y uno laboral requieren buscar cosas completamente distintas (depósitos y desalojo vs. salario y causales de despido), pero muchas herramientas los tratan igual.

Se necesita un sistema que, a partir de un texto plano de un documento legal, produzca automáticamente:

1. Las cláusulas relevantes extraídas.
2. Los riesgos detectados (para quien firma el documento).
3. Un resumen ejecutivo en lenguaje claro.

Y que este análisis sea **específico según el tipo de documento**, sin mezclar lógica ni prompts entre familias de documentos.

## 2. Solución: Patrón Abstract Factory

Se usará **Abstract Factory** porque necesitamos crear **familias de objetos relacionados** (extractor + detector + generador) que deben ser **coherentes entre sí** según el tipo de documento, y el sistema debe poder **agregar nuevos tipos de documento** (ej. contrato de compraventa, acuerdo de confidencialidad) sin modificar el código cliente.

- **Fábrica Abstracta**: `ContratoFactory`
- **Fábricas Concretas**: `ContratoArrendamientoFactory`, `ContratoLaboralFactory`, `TerminosCondicionesFactory`
- **Productos Abstractos**: `ExtractorDeClausulas`, `DetectorDeRiesgos`, `GeneradorDeResumenEjecutivo`
- **Productos Concretos**: uno por cada producto abstracto, por cada familia (9 clases concretas en total)

Cada producto concreto usa internamente un cliente HTTP compartido (`GroqClient`) para llamar a la API de Groq, pero con **prompts distintos y especializados** según la familia a la que pertenece.

No es un chatbot: no hay historial conversacional ni turnos de diálogo. Es un pipeline de entrada (texto del documento) → salida (reporte estructurado).

## 3. Estructura de paquetes propuesta

```
src/main/java/com/legalai/
├── Main.java
├── config/
│   └── GroqConfig.java
├── client/
│   ├── GroqClient.java
│   └── GroqResponseException.java
├── model/
│   ├── TipoDocumento.java          (enum: ARRENDAMIENTO, LABORAL, TERMINOS_CONDICIONES)
│   ├── Clausula.java
│   ├── Riesgo.java
│   ├── NivelRiesgo.java            (enum: BAJO, MEDIO, ALTO)
│   ├── ResumenEjecutivo.java
│   └── ReporteAnalisis.java        (agrega clausulas + riesgos + resumen)
├── factory/
│   ├── ContratoFactory.java                    (interfaz - Abstract Factory)
│   ├── ContratoArrendamientoFactory.java        (concreta)
│   ├── ContratoLaboralFactory.java              (concreta)
│   ├── TerminosCondicionesFactory.java          (concreta)
│   └── ContratoFactoryProvider.java             (selecciona la fábrica según TipoDocumento)
├── productos/
│   ├── ExtractorDeClausulas.java        (interfaz)
│   ├── DetectorDeRiesgos.java           (interfaz)
│   ├── GeneradorDeResumenEjecutivo.java (interfaz)
│   ├── arrendamiento/
│   │   ├── ExtractorClausulasArrendamiento.java
│   │   ├── DetectorRiesgosArrendamiento.java
│   │   └── GeneradorResumenArrendamiento.java
│   ├── laboral/
│   │   ├── ExtractorClausulasLaboral.java
│   │   ├── DetectorRiesgosLaboral.java
│   │   └── GeneradorResumenLaboral.java
│   └── terminos/
│       ├── ExtractorClausulasTerminos.java
│       ├── DetectorRiesgosTerminos.java
│       └── GeneradorResumenTerminos.java
├── service/
│   └── ProcesadorDocumento.java     (orquestador: usa una ContratoFactory para producir el ReporteAnalisis)
└── ui/
    ├── ConsolaUI.java              (menú interactivo de consola)
    └── ReporteFormatter.java       (formatea el ReporteAnalisis para mostrarlo/exportarlo bonito)
```

## 4. Interfaces clave (contratos, sin implementación)

```java
// factory/ContratoFactory.java
public interface ContratoFactory {
    ExtractorDeClausulas crearExtractorClausulas();
    DetectorDeRiesgos crearDetectorRiesgos();
    GeneradorDeResumenEjecutivo crearGeneradorResumen();
}

// productos/ExtractorDeClausulas.java
public interface ExtractorDeClausulas {
    List<Clausula> extraer(String textoDocumento);
}

// productos/DetectorDeRiesgos.java
public interface DetectorDeRiesgos {
    List<Riesgo> detectar(String textoDocumento, List<Clausula> clausulas);
}

// productos/GeneradorDeResumenEjecutivo.java
public interface GeneradorDeResumenEjecutivo {
    ResumenEjecutivo generar(String textoDocumento, List<Clausula> clausulas, List<Riesgo> riesgos);
}
```

## 5. Modelos de datos (records de Java, inmutables)

```java
public record Clausula(String titulo, String textoOriginal, String categoria) {}

public enum NivelRiesgo { BAJO, MEDIO, ALTO }

public record Riesgo(String descripcion, NivelRiesgo nivel, String clausulaRelacionada, String recomendacion) {}

public record ResumenEjecutivo(String resumenGeneral, List<String> puntosClave, String recomendacionFinal) {}

public record ReporteAnalisis(
    TipoDocumento tipoDocumento,
    List<Clausula> clausulas,
    List<Riesgo> riesgos,
    ResumenEjecutivo resumen
) {}

public enum TipoDocumento { ARRENDAMIENTO, LABORAL, TERMINOS_CONDICIONES }
```

## 6. Cliente Groq (compartido por todos los productos concretos)

```java
// client/GroqClient.java
public class GroqClient {
    // Lee la API key desde variable de entorno GROQ_API_KEY (NUNCA hardcodeada)
    // Modelo sugerido: "llama-3.3-70b-versatile" (o el vigente en Groq)
    // Endpoint: https://api.groq.com/openai/v1/chat/completions
    public String completar(String systemPrompt, String userPrompt) throws GroqResponseException;
}
```

Cada producto concreto (ej. `ExtractorClausulasArrendamiento`) construye su propio `systemPrompt` especializado (p. ej. "Eres un experto en contratos de arrendamiento en Colombia... extrae cláusulas de depósito, plazo, desalojo, mantenimiento...") y le pide a `GroqClient` la respuesta, luego la parsea (idealmente pidiendo JSON estructurado al modelo y usando Jackson/Gson para deserializar a `Clausula`/`Riesgo`/`ResumenEjecutivo`).

## 7. Orquestador

```java
// service/ProcesadorDocumento.java
public class ProcesadorDocumento {
    public ReporteAnalisis procesar(TipoDocumento tipo, String textoDocumento) {
        ContratoFactory factory = ContratoFactoryProvider.obtenerFactory(tipo);
        var extractor = factory.crearExtractorClausulas();
        var detector = factory.crearDetectorRiesgos();
        var generador = factory.crearGeneradorResumen();

        List<Clausula> clausulas = extractor.extraer(textoDocumento);
        List<Riesgo> riesgos = detector.detectar(textoDocumento, clausulas);
        ResumenEjecutivo resumen = generador.generar(textoDocumento, clausulas, riesgos);

        return new ReporteAnalisis(tipo, clausulas, riesgos, resumen);
    }
}
```

## 8. Main y parte visual (consola)

`Main.java` debe:
1. Mostrar un menú de consola (vía `ConsolaUI`):
   - Seleccionar tipo de documento (1. Arrendamiento, 2. Laboral, 3. Términos y Condiciones).
   - Pedir la ruta de un archivo `.txt` (o pegar el texto directamente) con el contenido del documento.
2. Invocar `ProcesadorDocumento.procesar(...)`.
3. Mostrar el `ReporteAnalisis` en consola con formato legible usando `ReporteFormatter`:
   - Cláusulas en una tabla simple (usar separadores `=====`, colores ANSI opcionales para resaltar riesgos ALTO en rojo, MEDIO en amarillo, BAJO en verde).
   - Riesgos ordenados por nivel (ALTO primero).
   - Resumen ejecutivo al final, en un bloque destacado.
4. Ofrecer exportar el reporte a un archivo `.txt` o `.md` con el mismo formato.

Ejemplo de salida esperada en consola:

```
===================================================
 ANÁLISIS DE CONTRATO — TIPO: ARRENDAMIENTO
===================================================

>> CLÁUSULAS DETECTADAS (5)
---------------------------------------------------
[1] Depósito de garantía
    "El arrendatario deberá consignar un depósito de..."

[2] Plazo del contrato
    "El presente contrato tendrá una duración de 12 meses..."
...

>> RIESGOS IDENTIFICADOS (3)
---------------------------------------------------
[ALTO]  Cláusula de renovación automática sin previo aviso
        Relacionado con: Plazo del contrato
        Recomendación: Solicitar plazo mínimo de notificación de 30 días.

[MEDIO] Depósito no reembolsable especificado ambiguamente
        ...

>> RESUMEN EJECUTIVO
---------------------------------------------------
Este contrato de arrendamiento presenta condiciones estándar de mercado,
con dos puntos que requieren negociación antes de la firma...

Recomendación final: Revisar cláusula de renovación automática antes de firmar.

¿Deseas exportar este reporte? (s/n):
```

(Como mejora opcional, si se quiere algo más visual que consola, se puede ofrecer una versión con interfaz Swing simple: un `JFrame` con un combo para tipo de documento, un `JTextArea` para pegar el texto, un botón "Analizar" y un panel de resultados con pestañas para Cláusulas / Riesgos / Resumen. Esto es opcional y no afecta el patrón Abstract Factory, que vive en la capa de lógica.)

## 9. Configuración del proyecto

- Java 17+ (usar `record` para los modelos).
- Maven o Gradle.
- Dependencias sugeridas:
  - `com.fasterxml.jackson.core:jackson-databind` (parsear JSON de respuestas de Groq).
  - Cliente HTTP: `java.net.http.HttpClient` (incluido en JDK, no requiere dependencia extra).
- La API key de Groq se debe leer así:

```java
String apiKey = System.getenv("GROQ_API_KEY");
if (apiKey == null || apiKey.isBlank()) {
    throw new IllegalStateException("Falta la variable de entorno GROQ_API_KEY");
}
```

Y se ejecuta el programa así (nunca metiendo la key en el código):

```
export GROQ_API_KEY=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
mvn compile exec:java -Dexec.mainClass="com.legalai.Main"
```

> Nota de seguridad: la API key que compartiste en el chat debe considerarse expuesta. Revócala en la consola de Groq y genera una nueva antes de usar este proyecto en algo real.

## 10. Instrucciones para Claude Code (resumen ejecutable)

Implementar el proyecto Java descrito arriba, siguiendo exactamente la estructura de paquetes de la sección 3, las interfaces de la sección 4, los modelos de la sección 5, el cliente Groq de la sección 6 (leyendo `GROQ_API_KEY` de variable de entorno, endpoint `https://api.groq.com/openai/v1/chat/completions`, modelo `llama-3.3-70b-versatile`), el orquestador de la sección 7, y el `Main` + UI de consola de la sección 8 con el formato de salida de ejemplo mostrado. Usar Maven, Java 17+, Jackson para parseo JSON. Cada producto concreto debe construir un prompt de sistema especializado según su familia (arrendamiento / laboral / términos y condiciones) pidiéndole al modelo que responda en JSON estructurado para poder parsearlo a los records `Clausula`, `Riesgo` y `ResumenEjecutivo`. Incluir manejo de errores si la respuesta del modelo no es JSON válido (reintento simple o mensaje de error claro). Incluir un `pom.xml` funcional y un `README.md` corto con instrucciones de ejecución.
