package com.legalai.factory;

import com.legalai.client.GroqClient;
import com.legalai.productos.DetectorDeRiesgos;
import com.legalai.productos.ExtractorDeClausulas;
import com.legalai.productos.GeneradorDeResumenEjecutivo;
import com.legalai.productos.arrendamiento.DetectorRiesgosArrendamiento;
import com.legalai.productos.arrendamiento.ExtractorClausulasArrendamiento;
import com.legalai.productos.arrendamiento.GeneradorResumenArrendamiento;

public class ContratoArrendamientoFactory implements ContratoFactory {

    private final GroqClient groqClient;

    public ContratoArrendamientoFactory(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public ExtractorDeClausulas crearExtractorClausulas() {
        return new ExtractorClausulasArrendamiento(groqClient);
    }

    @Override
    public DetectorDeRiesgos crearDetectorRiesgos() {
        return new DetectorRiesgosArrendamiento(groqClient);
    }

    @Override
    public GeneradorDeResumenEjecutivo crearGeneradorResumen() {
        return new GeneradorResumenArrendamiento(groqClient);
    }
}
