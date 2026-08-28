package com.legalai;

import com.legalai.client.GroqResponseException;
import com.legalai.model.ReporteAnalisis;
import com.legalai.model.TipoDocumento;
import com.legalai.service.ProcesadorDocumento;
import com.legalai.ui.ConsolaUI;
import com.legalai.ui.ReporteFormatter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        ConsolaUI ui = new ConsolaUI();
        ProcesadorDocumento procesador = new ProcesadorDocumento();
        ReporteFormatter formatter = new ReporteFormatter();

        boolean continuar = true;
        while (continuar) {
            try {
                TipoDocumento tipo = ui.pedirTipoDocumento();
                String textoDocumento = ui.pedirTextoDocumento();

                if (textoDocumento.isBlank()) {
                    System.out.println("El documento está vacío. Intenta de nuevo.\n");
                    continuar = ui.preguntarContinuar();
                    continue;
                }

                System.out.println("\nAnalizando documento, esto puede tardar unos segundos...\n");
                ReporteAnalisis reporte = procesador.procesar(tipo, textoDocumento);
                String salida = formatter.formatear(reporte);

                System.out.println(salida);

                if (ui.preguntarExportar()) {
                    String ruta = ui.pedirRutaExportacion();
                    Files.writeString(Path.of(ruta), salida, StandardCharsets.UTF_8);
                    System.out.println("Reporte exportado a " + ruta);
                }
            } catch (GroqResponseException e) {
                System.out.println("Error al comunicarse con la API de Groq: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error de entrada/salida: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Error de configuración: " + e.getMessage());
                continuar = false;
                continue;
            }

            continuar = ui.preguntarContinuar();
        }

        System.out.println("Hasta luego.");
    }
}
