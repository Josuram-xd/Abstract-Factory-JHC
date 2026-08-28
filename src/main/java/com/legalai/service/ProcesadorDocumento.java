package com.legalai.service;

import com.legalai.client.GroqResponseException;
import com.legalai.factory.ContratoFactory;
import com.legalai.factory.ContratoFactoryProvider;
import com.legalai.model.Clausula;
import com.legalai.model.ReporteAnalisis;
import com.legalai.model.ResumenEjecutivo;
import com.legalai.model.Riesgo;
import com.legalai.model.TipoDocumento;
import com.legalai.productos.DetectorDeRiesgos;
import com.legalai.productos.ExtractorDeClausulas;
import com.legalai.productos.GeneradorDeResumenEjecutivo;

import java.util.List;

public class ProcesadorDocumento {

    public ReporteAnalisis procesar(TipoDocumento tipo, String textoDocumento) throws GroqResponseException {
        ContratoFactory factory = ContratoFactoryProvider.obtenerFactory(tipo);
        ExtractorDeClausulas extractor = factory.crearExtractorClausulas();
        DetectorDeRiesgos detector = factory.crearDetectorRiesgos();
        GeneradorDeResumenEjecutivo generador = factory.crearGeneradorResumen();

        List<Clausula> clausulas = extractor.extraer(textoDocumento);
        List<Riesgo> riesgos = detector.detectar(textoDocumento, clausulas);
        ResumenEjecutivo resumen = generador.generar(textoDocumento, clausulas, riesgos);

        return new ReporteAnalisis(tipo, clausulas, riesgos, resumen);
    }
}
