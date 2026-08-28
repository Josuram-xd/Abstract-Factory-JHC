package com.legalai.productos;

import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;
import com.legalai.model.Riesgo;
import com.legalai.model.ResumenEjecutivo;

import java.util.List;

public interface GeneradorDeResumenEjecutivo {
    ResumenEjecutivo generar(String textoDocumento, List<Clausula> clausulas, List<Riesgo> riesgos)
            throws GroqResponseException;
}
