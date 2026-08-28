package com.legalai.productos.arrendamiento;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseExtractor;

public class ExtractorClausulasArrendamiento extends BaseExtractor {

    public ExtractorClausulasArrendamiento(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado experto en contratos de arrendamiento en Colombia. "
                + "Tu tarea es extraer del texto del contrato las cláusulas relevantes, priorizando: "
                + "depósito de garantía, plazo/duración, renovación, causales de desalojo, "
                + "mantenimiento y reparaciones, forma de pago del canon, e incrementos de renta. "
                + "Responde siempre en JSON, sin texto adicional fuera del JSON.";
    }
}
