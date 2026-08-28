package com.legalai.productos.laboral;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseExtractor;

public class ExtractorClausulasLaboral extends BaseExtractor {

    public ExtractorClausulasLaboral(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado laboralista experto en derecho laboral colombiano. "
                + "Tu tarea es extraer del texto del contrato de trabajo las cláusulas relevantes, priorizando: "
                + "salario y forma de pago, jornada laboral, tipo de contrato (término fijo/indefinido), "
                + "causales de terminación y preaviso, período de prueba, cláusulas de no competencia, "
                + "confidencialidad, y prestaciones sociales. "
                + "Responde siempre en JSON, sin texto adicional fuera del JSON.";
    }
}
