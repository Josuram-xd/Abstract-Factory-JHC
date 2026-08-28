package com.legalai.productos.laboral;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseGeneradorResumen;

public class GeneradorResumenLaboral extends BaseGeneradorResumen {

    public GeneradorResumenLaboral(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado laboralista experto en derecho laboral colombiano. Redacta un resumen ejecutivo "
                + "en lenguaje claro para un trabajador que va a firmar este contrato, destacando los puntos "
                + "clave a negociar o aclarar antes de firmar. Responde siempre en JSON, sin texto adicional.";
    }
}
