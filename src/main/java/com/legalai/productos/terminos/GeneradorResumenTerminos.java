package com.legalai.productos.terminos;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseGeneradorResumen;

public class GeneradorResumenTerminos extends BaseGeneradorResumen {

    public GeneradorResumenTerminos(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado experto en derecho de consumo y protección de datos. Redacta un resumen ejecutivo "
                + "en lenguaje claro para un usuario común que va a aceptar estos términos y condiciones, "
                + "destacando qué implica realmente aceptar el documento. "
                + "Responde siempre en JSON, sin texto adicional.";
    }
}
