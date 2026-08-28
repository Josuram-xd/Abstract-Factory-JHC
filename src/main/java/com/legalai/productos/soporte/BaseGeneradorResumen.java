package com.legalai.productos.soporte;

import com.fasterxml.jackson.databind.JsonNode;
import com.legalai.client.GroqClient;
import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;
import com.legalai.model.Riesgo;
import com.legalai.model.ResumenEjecutivo;
import com.legalai.productos.GeneradorDeResumenEjecutivo;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGeneradorResumen implements GeneradorDeResumenEjecutivo {

    protected final GroqClient groq;

    protected BaseGeneradorResumen(GroqClient groq) {
        this.groq = groq;
    }

    protected abstract String systemPrompt();

    @Override
    public ResumenEjecutivo generar(String textoDocumento, List<Clausula> clausulas, List<Riesgo> riesgos)
            throws GroqResponseException {
        StringBuilder clausulasTexto = new StringBuilder();
        for (Clausula c : clausulas) {
            clausulasTexto.append("- ").append(c.titulo()).append('\n');
        }
        StringBuilder riesgosTexto = new StringBuilder();
        for (Riesgo r : riesgos) {
            riesgosTexto.append("- [").append(r.nivel()).append("] ").append(r.descripcion()).append('\n');
        }

        String userPrompt = "Documento a analizar:\n\n" + textoDocumento
                + "\n\nCláusulas identificadas:\n" + clausulasTexto
                + "\nRiesgos identificados:\n" + riesgosTexto
                + "\nResponde en JSON con la forma exacta: "
                + "{\"resumenGeneral\": \"...\", \"puntosClave\": [\"...\"], \"recomendacionFinal\": \"...\"}";

        JsonNode root = JsonUtil.completarYParsear(groq, systemPrompt(), userPrompt);
        List<String> puntosClave = new ArrayList<>();
        for (JsonNode nodo : root.path("puntosClave")) {
            puntosClave.add(nodo.asText(""));
        }

        return new ResumenEjecutivo(
                root.path("resumenGeneral").asText(""),
                puntosClave,
                root.path("recomendacionFinal").asText("")
        );
    }
}
