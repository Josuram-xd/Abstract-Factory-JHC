package com.legalai.productos.laboral;

import com.legalai.client.GroqClient;
import com.legalai.productos.soporte.BaseDetector;

public class DetectorRiesgosLaboral extends BaseDetector {

    public DetectorRiesgosLaboral(GroqClient groq) {
        super(groq);
    }

    @Override
    protected String systemPrompt() {
        return "Eres un abogado laboralista experto en derecho laboral colombiano especializado en detectar "
                + "riesgos para el trabajador. Presta especial atención a: causales de despido ambiguas o sin "
                + "justa causa, cláusulas de no competencia excesivamente amplias, jornadas que excedan el máximo "
                + "legal sin compensación, salario variable mal definido, y periodos de prueba extendidos "
                + "irregularmente. Clasifica cada riesgo como ALTO, MEDIO o BAJO. "
                + "Responde siempre en JSON, sin texto adicional.";
    }
}
