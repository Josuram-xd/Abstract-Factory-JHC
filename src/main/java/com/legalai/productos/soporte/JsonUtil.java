package com.legalai.productos.soporte;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legalai.client.GroqClient;
import com.legalai.client.GroqResponseException;

public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {}

    /**
     * Pide una respuesta a Groq y la parsea como JSON. Si la primera respuesta no es JSON
     * válido, reintenta una vez pidiéndole al modelo explícitamente que corrija el formato.
     */
    public static JsonNode completarYParsear(GroqClient groq, String systemPrompt, String userPrompt)
            throws GroqResponseException {
        String respuesta = groq.completar(systemPrompt, userPrompt);
        try {
            return MAPPER.readTree(respuesta);
        } catch (Exception primerError) {
            String promptCorreccion = userPrompt
                    + "\n\nTu respuesta anterior no era JSON válido. Responde ÚNICAMENTE con un objeto JSON válido, "
                    + "sin texto adicional, sin comentarios y sin bloques de código markdown.";
            String reintento = groq.completar(systemPrompt, promptCorreccion);
            try {
                return MAPPER.readTree(reintento);
            } catch (Exception segundoError) {
                throw new GroqResponseException(
                        "La respuesta del modelo no es JSON válido tras reintentar. Última respuesta: " + reintento,
                        segundoError);
            }
        }
    }
}
