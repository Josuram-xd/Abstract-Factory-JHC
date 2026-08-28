package com.legalai.factory;

import com.legalai.client.GroqClient;
import com.legalai.productos.DetectorDeRiesgos;
import com.legalai.productos.ExtractorDeClausulas;
import com.legalai.productos.GeneradorDeResumenEjecutivo;
import com.legalai.productos.laboral.DetectorRiesgosLaboral;
import com.legalai.productos.laboral.ExtractorClausulasLaboral;
import com.legalai.productos.laboral.GeneradorResumenLaboral;

public class ContratoLaboralFactory implements ContratoFactory {

    private final GroqClient groqClient;

    public ContratoLaboralFactory(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public ExtractorDeClausulas crearExtractorClausulas() {
        return new ExtractorClausulasLaboral(groqClient);
    }

    @Override
    public DetectorDeRiesgos crearDetectorRiesgos() {
        return new DetectorRiesgosLaboral(groqClient);
    }

    @Override
    public GeneradorDeResumenEjecutivo crearGeneradorResumen() {
        return new GeneradorResumenLaboral(groqClient);
    }
}
