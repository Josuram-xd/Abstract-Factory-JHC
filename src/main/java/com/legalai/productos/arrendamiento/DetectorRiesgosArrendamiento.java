package com.legalai.productos.arrendamiento;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseDetector;

public class DetectorRiesgosArrendamiento extends BaseDetector {

    public DetectorRiesgosArrendamiento(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado experto en contratos de arrendamiento en Colombia especializado en detectar "
                + "riesgos para el arrendatario. Presta especial atención a: renovación automática sin previo aviso, "
                + "depósitos no reembolsables o ambiguos, cláusulas de desalojo exprés, incrementos de renta por "
                + "encima del IPC, y responsabilidades de mantenimiento trasladadas injustamente al arrendatario. "
                + "Clasifica cada riesgo como ALTO, MEDIO o BAJO. Responde siempre en JSON, sin texto adicional.";
    }
}
