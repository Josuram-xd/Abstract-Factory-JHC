package com.legalai.factory;

import com.legalai.client.GroqClient;
import com.legalai.productos.DetectorDeRiesgos;
import com.legalai.productos.ExtractorDeClausulas;
import com.legalai.productos.GeneradorDeResumenEjecutivo;
import com.legalai.productos.terminos.DetectorRiesgosTerminos;
import com.legalai.productos.terminos.ExtractorClausulasTerminos;
import com.legalai.productos.terminos.GeneradorResumenTerminos;

public class TerminosCondicionesFactory implements ContratoFactory {

    private final GroqClient groqClient;

    public TerminosCondicionesFactory(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public ExtractorDeClausulas crearExtractorClausulas() {
        return new ExtractorClausulasTerminos(groqClient);
    }

    @Override
    public DetectorDeRiesgos crearDetectorRiesgos() {
        return new DetectorRiesgosTerminos(groqClient);
    }

    @Override
    public GeneradorDeResumenEjecutivo crearGeneradorResumen() {
        return new GeneradorResumenTerminos(groqClient);
    }
}
