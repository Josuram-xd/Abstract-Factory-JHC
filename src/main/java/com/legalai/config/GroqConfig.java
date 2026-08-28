package com.legalai.config;

public final class GroqConfig {

    public static final String ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    public static final String MODELO = "llama-3.3-70b-versatile";

    private GroqConfig() {}

    public static String obtenerApiKey() {
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno GROQ_API_KEY");
        }
        return apiKey;
    }
}
