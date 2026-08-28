package com.legalai.productos;

import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;
import com.legalai.model.Riesgo;

import java.util.List;

public interface DetectorDeRiesgos {
    List<Riesgo> detectar(String textoDocumento, List<Clausula> clausulas) throws GroqResponseException;
}
