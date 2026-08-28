package com.legalai.ui.swing;

import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;
import com.legalai.model.NivelRiesgo;
import com.legalai.model.ReporteAnalisis;
import com.legalai.model.Riesgo;
import com.legalai.model.TipoDocumento;
import com.legalai.service.ProcesadorDocumento;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class AnalizadorFrame extends JFrame {

    private final ProcesadorDocumento procesador = new ProcesadorDocumento();

    private final JComboBox<TipoDocumento> comboTipo = new JComboBox<>(TipoDocumento.values());
    private final JTextArea areaTexto = new JTextArea();
    private final JButton botonCargarArchivo = new JButton("Cargar archivo...");
    private final JButton botonAnalizar = new JButton("Analizar");
    private final JButton botonExportar = new JButton("Exportar reporte...");
    private final JLabel etiquetaEstado = new JLabel(" ");

    private final JTextArea areaClausulas = crearAreaResultado();
    private final JTextPane areaRiesgos = new JTextPane();
    private final JTextArea areaResumen = crearAreaResultado();

    private ReporteAnalisis ultimoReporte;

    public AnalizadorFrame() {
        super("Motor de Análisis Legal — Groq");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirPanelCentral(), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);

        botonCargarArchivo.addActionListener(e -> cargarArchivo());
        botonAnalizar.addActionListener(e -> analizar());
        botonExportar.addActionListener(e -> exportar());
        botonExportar.setEnabled(false);
    }

    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel selectorTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorTipo.add(new JLabel("Tipo de documento:"));
        selectorTipo.add(comboTipo);
        selectorTipo.add(botonCargarArchivo);

        panel.add(selectorTipo, BorderLayout.NORTH);

        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(0, 180));
        scroll.setBorder(BorderFactory.createTitledBorder("Texto del documento"));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(botonAnalizar);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private JTabbedPane construirPanelCentral() {
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Cláusulas", new JScrollPane(areaClausulas));
        pestañas.addTab("Riesgos", new JScrollPane(areaRiesgos));
        pestañas.addTab("Resumen ejecutivo", new JScrollPane(areaResumen));
        pestañas.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        return pestañas;
    }

    private JPanel construirPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
        panel.add(etiquetaEstado, BorderLayout.WEST);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(botonExportar);
        panel.add(botones, BorderLayout.EAST);
        return panel;
    }

    private static JTextArea crearAreaResultado() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return area;
    }

    private void cargarArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Texto (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String contenido = Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8);
                areaTexto.setText(contenido);
            } catch (IOException e) {
                mostrarError("No se pudo leer el archivo: " + e.getMessage());
            }
        }
    }

    private void analizar() {
        String texto = areaTexto.getText();
        if (texto == null || texto.isBlank()) {
            mostrarError("El documento está vacío.");
            return;
        }
        TipoDocumento tipo = (TipoDocumento) comboTipo.getSelectedItem();

        establecerCargando(true);

        new SwingWorker<ReporteAnalisis, Void>() {
            @Override
            protected ReporteAnalisis doInBackground() throws Exception {
                return procesador.procesar(tipo, texto);
            }

            @Override
            protected void done() {
                establecerCargando(false);
                try {
                    ultimoReporte = get();
                    mostrarReporte(ultimoReporte);
                    botonExportar.setEnabled(true);
                    etiquetaEstado.setText("Análisis completado.");
                } catch (Exception e) {
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    String mensaje = causa instanceof GroqResponseException
                            ? "Error al comunicarse con la API de Groq: " + causa.getMessage()
                            : "Error inesperado: " + causa.getMessage();
                    mostrarError(mensaje);
                }
            }
        }.execute();
    }

    private void establecerCargando(boolean cargando) {
        botonAnalizar.setEnabled(!cargando);
        botonCargarArchivo.setEnabled(!cargando);
        etiquetaEstado.setText(cargando ? "Analizando documento, esto puede tardar unos segundos..." : " ");
        setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private void mostrarReporte(ReporteAnalisis reporte) {
        areaClausulas.setText(formatearClausulas(reporte));
        areaClausulas.setCaretPosition(0);

        pintarRiesgos(reporte);

        areaResumen.setText(formatearResumen(reporte));
        areaResumen.setCaretPosition(0);
    }

    private String formatearClausulas(ReporteAnalisis reporte) {
        StringBuilder sb = new StringBuilder();
        sb.append("CLÁUSULAS DETECTADAS (").append(reporte.clausulas().size()).append(")\n\n");
        int i = 1;
        for (Clausula c : reporte.clausulas()) {
            sb.append('[').append(i++).append("] ").append(c.titulo())
                    .append(" (").append(c.categoria()).append(")\n");
            sb.append("    \"").append(c.textoOriginal()).append("\"\n\n");
        }
        return sb.toString();
    }

    private void pintarRiesgos(ReporteAnalisis reporte) {
        areaRiesgos.setText("");
        StyledDocument doc = areaRiesgos.getStyledDocument();

        var riesgosOrdenados = reporte.riesgos().stream()
                .sorted(Comparator.comparingInt(this::ordenNivel))
                .toList();

        try {
            doc.insertString(doc.getLength(), "RIESGOS IDENTIFICADOS (" + riesgosOrdenados.size() + ")\n\n", null);
            for (Riesgo r : riesgosOrdenados) {
                SimpleAttributeSet estilo = new SimpleAttributeSet();
                StyleConstants.setForeground(estilo, colorNivel(r.nivel()));
                StyleConstants.setBold(estilo, true);
                doc.insertString(doc.getLength(), "[" + r.nivel() + "] ", estilo);

                SimpleAttributeSet normal = new SimpleAttributeSet();
                doc.insertString(doc.getLength(), r.descripcion() + "\n", normal);
                doc.insertString(doc.getLength(), "    Relacionado con: " + r.clausulaRelacionada() + "\n", normal);
                doc.insertString(doc.getLength(), "    Recomendación: " + r.recomendacion() + "\n\n", normal);
            }
        } catch (BadLocationException e) {
            areaRiesgos.setText("Error mostrando riesgos: " + e.getMessage());
        }
    }

    private String formatearResumen(ReporteAnalisis reporte) {
        StringBuilder sb = new StringBuilder();
        sb.append("RESUMEN EJECUTIVO\n\n");
        sb.append(reporte.resumen().resumenGeneral()).append("\n\n");
        sb.append("Puntos clave:\n");
        for (String punto : reporte.resumen().puntosClave()) {
            sb.append("  - ").append(punto).append('\n');
        }
        sb.append("\nRecomendación final: ").append(reporte.resumen().recomendacionFinal()).append('\n');
        return sb.toString();
    }

    private int ordenNivel(Riesgo r) {
        return switch (r.nivel()) {
            case ALTO -> 0;
            case MEDIO -> 1;
            case BAJO -> 2;
        };
    }

    private Color colorNivel(NivelRiesgo nivel) {
        return switch (nivel) {
            case ALTO -> new Color(200, 0, 0);
            case MEDIO -> new Color(180, 130, 0);
            case BAJO -> new Color(0, 130, 0);
        };
    }

    private void exportar() {
        if (ultimoReporte == null) {
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("reporte_" + ultimoReporte.tipoDocumento() + ".txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String contenido = formatearClausulas(ultimoReporte) + "\n"
                    + areaRiesgos.getText() + "\n"
                    + formatearResumen(ultimoReporte);
            try {
                Files.writeString(chooser.getSelectedFile().toPath(), contenido, StandardCharsets.UTF_8);
                JOptionPane.showMessageDialog(this, "Reporte exportado correctamente.",
                        "Exportar", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                mostrarError("No se pudo exportar el reporte: " + e.getMessage());
            }
        }
    }

    private void mostrarError(String mensaje) {
        etiquetaEstado.setText(" ");
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
