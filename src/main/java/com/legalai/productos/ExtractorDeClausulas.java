package com.legalai.productos;

import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;

import java.util.List;

public interface ExtractorDeClausulas {
    List<Clausula> extraer(String textoDocumento) throws GroqResponseException;
}
