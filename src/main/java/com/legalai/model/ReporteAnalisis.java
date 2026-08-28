package com.legalai.model;

import java.util.List;

public record ReporteAnalisis(
    TipoDocumento tipoDocumento,
    List<Clausula> clausulas,
    List<Riesgo> riesgos,
    ResumenEjecutivo resumen
) {}
