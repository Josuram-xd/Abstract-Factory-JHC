package com.legalai.factory;

import com.legalai.productos.DetectorDeRiesgos;
import com.legalai.productos.ExtractorDeClausulas;
import com.legalai.productos.GeneradorDeResumenEjecutivo;

public interface ContratoFactory {
    ExtractorDeClausulas crearExtractorClausulas();
    DetectorDeRiesgos crearDetectorRiesgos();
    GeneradorDeResumenEjecutivo crearGeneradorResumen();
}
