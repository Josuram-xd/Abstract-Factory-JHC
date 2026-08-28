package com.legalai.factory;

import com.legalai.client.GroqClient;
import com.legalai.model.TipoDocumento;

public final class ContratoFactoryProvider {

    private ContratoFactoryProvider() {}

    public static ContratoFactory obtenerFactory(TipoDocumento tipo) {
        GroqClient groqClient = new GroqClient();
        return switch (tipo) {
            case ARRENDAMIENTO -> new ContratoArrendamientoFactory(groqClient);
            case LABORAL -> new ContratoLaboralFactory(groqClient);
            case TERMINOS_CONDICIONES -> new TerminosCondicionesFactory(groqClient);
        };
    }
}
