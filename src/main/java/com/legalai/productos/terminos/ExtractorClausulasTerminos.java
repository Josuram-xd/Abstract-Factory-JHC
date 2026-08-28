package com.legalai.productos.terminos;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseExtractor;

public class ExtractorClausulasTerminos extends BaseExtractor {

    public ExtractorClausulasTerminos(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado experto en derecho de consumo y protección de datos. "
                + "Tu tarea es extraer del texto de términos y condiciones las cláusulas relevantes, priorizando: "
                + "tratamiento y venta de datos personales, limitación de responsabilidad, renuncia a acciones "
                + "colectivas o arbitraje forzoso, cambios unilaterales de los términos, cancelación de cuenta, "
                + "y renovación automática de suscripciones. "
                + "Responde siempre en JSON, sin texto adicional fuera del JSON.";
    }
}
