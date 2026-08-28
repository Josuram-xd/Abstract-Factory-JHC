package com.legalai.productos.soporte;

import com.fasterxml.jackson.databind.JsonNode;
import com.legalai.client.GroqClient;
import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;
import com.legalai.model.NivelRiesgo;
import com.legalai.model.Riesgo;
import com.legalai.productos.DetectorDeRiesgos;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDetector implements DetectorDeRiesgos {

    protected final GroqClient groq;

    protected BaseDetector(GroqClient groq) {
        this.groq = groq;
    }

    protected abstract String systemPrompt();

    @Override
    public List<Riesgo> detectar(String textoDocumento, List<Clausula> clausulas) throws GroqResponseException {
        StringBuilder clausulasTexto = new StringBuilder();
        for (Clausula c : clausulas) {
            clausulasTexto.append("- ").append(c.titulo()).append(": ").append(c.textoOriginal()).append('\n');
        }

        String userPrompt = "Documento a analizar:\n\n" + textoDocumento
                + "\n\nCláusulas ya extraídas:\n" + clausulasTexto
                + "\nResponde en JSON con la forma exacta: "
                + "{\"riesgos\": [{\"descripcion\": \"...\", \"nivel\": \"ALTO|MEDIO|BAJO\", "
                + "\"clausulaRelacionada\": \"...\", \"recomendacion\": \"...\"}]}";

        JsonNode root = JsonUtil.completarYParsear(groq, systemPrompt(), userPrompt);
        List<Riesgo> riesgos = new ArrayList<>();
        for (JsonNode nodo : root.path("riesgos")) {
            NivelRiesgo nivel;
            try {
                nivel = NivelRiesgo.valueOf(nodo.path("nivel").asText("MEDIO").trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                nivel = NivelRiesgo.MEDIO;
            }
            riesgos.add(new Riesgo(
                    nodo.path("descripcion").asText(""),
                    nivel,
                    nodo.path("clausulaRelacionada").asText(""),
                    nodo.path("recomendacion").asText("")
            ));
        }
        return riesgos;
    }
}
