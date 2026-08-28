package com.legalai.productos.soporte;

import com.fasterxml.jackson.databind.JsonNode;
import com.legalai.client.GroqClient;
import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;
import com.legalai.productos.ExtractorDeClausulas;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseExtractor implements ExtractorDeClausulas {

    protected final GroqClient groq;

    protected BaseExtractor(GroqClient groq) {
        this.groq = groq;
    }

    protected abstract String systemPrompt();

    @Override
    public List<Clausula> extraer(String textoDocumento) throws GroqResponseException {
        String userPrompt = "Documento a analizar:\n\n" + textoDocumento
                + "\n\nResponde en JSON con la forma exacta: "
                + "{\"clausulas\": [{\"titulo\": \"...\", \"textoOriginal\": \"...\", \"categoria\": \"...\"}]}";

        JsonNode root = JsonUtil.completarYParsear(groq, systemPrompt(), userPrompt);
        List<Clausula> clausulas = new ArrayList<>();
        for (JsonNode nodo : root.path("clausulas")) {
            clausulas.add(new Clausula(
                    nodo.path("titulo").asText(""),
                    nodo.path("textoOriginal").asText(""),
                    nodo.path("categoria").asText("")
            ));
        }
        return clausulas;
    }
}
