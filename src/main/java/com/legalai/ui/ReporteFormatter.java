package com.legalai.ui;

import com.legalai.model.Clausula;
import com.legalai.model.NivelRiesgo;
import com.legalai.model.ReporteAnalisis;
import com.legalai.model.Riesgo;

import java.util.Comparator;
import java.util.List;

public class ReporteFormatter {

    private static final String ANSI_RESET = "[0m";
    private static final String ANSI_ROJO = "[31m";
    private static final String ANSI_AMARILLO = "[33m";
    private static final String ANSI_VERDE = "[32m";

    public String formatear(ReporteAnalisis reporte) {
        StringBuilder sb = new StringBuilder();

        sb.append("===================================================\n");
        sb.append(" ANÁLISIS DE CONTRATO — TIPO: ").append(reporte.tipoDocumento()).append('\n');
        sb.append("===================================================\n\n");

        sb.append(">> CLÁUSULAS DETECTADAS (").append(reporte.clausulas().size()).append(")\n");
        sb.append("---------------------------------------------------\n");
        int i = 1;
        for (Clausula c : reporte.clausulas()) {
            sb.append('[').append(i++).append("] ").append(c.titulo()).append('\n');
            sb.append("    \"").append(c.textoOriginal()).append("\"\n\n");
        }

        List<Riesgo> riesgosOrdenados = reporte.riesgos().stream()
                .sorted(Comparator.comparingInt(r -> ordenNivel(r.nivel())))
                .toList();

        sb.append(">> RIESGOS IDENTIFICADOS (").append(riesgosOrdenados.size()).append(")\n");
        sb.append("---------------------------------------------------\n");
        for (Riesgo r : riesgosOrdenados) {
            sb.append(colorNivel(r.nivel())).append('[').append(r.nivel()).append(']').append(ANSI_RESET)
                    .append("  ").append(r.descripcion()).append('\n');
            sb.append("        Relacionado con: ").append(r.clausulaRelacionada()).append('\n');
            sb.append("        Recomendación: ").append(r.recomendacion()).append("\n\n");
        }

        sb.append(">> RESUMEN EJECUTIVO\n");
        sb.append("---------------------------------------------------\n");
        sb.append(reporte.resumen().resumenGeneral()).append("\n\n");
        for (String punto : reporte.resumen().puntosClave()) {
            sb.append("  - ").append(punto).append('\n');
        }
        sb.append("\nRecomendación final: ").append(reporte.resumen().recomendacionFinal()).append('\n');

        return sb.toString();
    }

    private int ordenNivel(NivelRiesgo nivel) {
        return switch (nivel) {
            case ALTO -> 0;
            case MEDIO -> 1;
            case BAJO -> 2;
        };
    }

    private String colorNivel(NivelRiesgo nivel) {
        return switch (nivel) {
            case ALTO -> ANSI_ROJO;
            case MEDIO -> ANSI_AMARILLO;
            case BAJO -> ANSI_VERDE;
        };
    }
}
