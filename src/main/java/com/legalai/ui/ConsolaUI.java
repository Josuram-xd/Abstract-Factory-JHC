package com.legalai.ui;

import com.legalai.model.TipoDocumento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ConsolaUI {

    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    public TipoDocumento pedirTipoDocumento() {
        System.out.println("Selecciona el tipo de documento:");
        System.out.println("1. Contrato de arrendamiento");
        System.out.println("2. Contrato laboral");
        System.out.println("3. Términos y condiciones");
        while (true) {
            System.out.print("Opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1": return TipoDocumento.ARRENDAMIENTO;
                case "2": return TipoDocumento.LABORAL;
                case "3": return TipoDocumento.TERMINOS_CONDICIONES;
                default: System.out.println("Opción inválida, intenta de nuevo.");
            }
        }
    }

    public String pedirTextoDocumento() throws IOException {
        System.out.println();
        System.out.println("¿Cómo quieres ingresar el documento?");
        System.out.println("1. Ruta de un archivo .txt");
        System.out.println("2. Pegar el texto directamente");
        System.out.print("Opción: ");
        String opcion = scanner.nextLine().trim();

        if ("1".equals(opcion)) {
            System.out.print("Ruta del archivo: ");
            String ruta = scanner.nextLine().trim();
            return Files.readString(Path.of(ruta), StandardCharsets.UTF_8);
        }

        System.out.println("Pega el texto del documento. Termina con una línea que contenga solo 'FIN':");
        StringBuilder sb = new StringBuilder();
        String linea;
        while (!(linea = scanner.nextLine()).equals("FIN")) {
            sb.append(linea).append('\n');
        }
        return sb.toString();
    }

    public boolean preguntarExportar() {
        System.out.print("\n¿Deseas exportar este reporte? (s/n): ");
        String respuesta = scanner.nextLine().trim();
        return respuesta.equalsIgnoreCase("s");
    }

    public String pedirRutaExportacion() {
        System.out.print("Ruta del archivo de salida (.txt o .md): ");
        return scanner.nextLine().trim();
    }

    public boolean preguntarContinuar() {
        System.out.print("\n¿Deseas analizar otro documento? (s/n): ");
        String respuesta = scanner.nextLine().trim();
        return respuesta.equalsIgnoreCase("s");
    }
}
