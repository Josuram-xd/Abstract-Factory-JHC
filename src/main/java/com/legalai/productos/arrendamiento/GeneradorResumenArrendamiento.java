package com.legalai.productos.arrendamiento;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseGeneradorResumen;

public class GeneradorResumenArrendamiento extends BaseGeneradorResumen {

    public GeneradorResumenArrendamiento(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado experto en contratos de arrendamiento en Colombia. Redacta un resumen ejecutivo "
                + "en lenguaje claro para una persona sin formación legal que va a firmar este contrato como "
                + "arrendatario, destacando los puntos clave a negociar antes de firmar. "
                + "Responde siempre en JSON, sin texto adicional.";
    }
}
