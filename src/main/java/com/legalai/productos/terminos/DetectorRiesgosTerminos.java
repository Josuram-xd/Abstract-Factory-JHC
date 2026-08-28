package com.legalai.productos.terminos;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseDetector;

public class DetectorRiesgosTerminos extends BaseDetector {

    public DetectorRiesgosTerminos(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado experto en derecho de consumo y protección de datos especializado en detectar "
                + "riesgos para el usuario que acepta términos y condiciones. Presta especial atención a: venta o "
                + "cesión de datos personales a terceros sin consentimiento claro, renuncia a acciones legales o "
                + "arbitraje obligatorio desfavorable, cambios unilaterales de los términos sin previo aviso, "
                + "y dificultad excesiva para cancelar suscripciones. Clasifica cada riesgo como ALTO, MEDIO o BAJO. "
                + "Responde siempre en JSON, sin texto adicional.";
    }
}
