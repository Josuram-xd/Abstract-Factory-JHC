package com.legalai.ui.swing;

import com.formdev.flatlaf.FlatClientProperties;
import com.legalai.client.GroqResponseException;
import com.legalai.model.Clausula;
import com.legalai.model.NivelRiesgo;
import com.legalai.model.ReporteAnalisis;
import com.legalai.model.Riesgo;
import com.legalai.model.TipoDocumento;
import com.legalai.service.ProcesadorDocumento;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;

public class AnalizadorFrame extends JFrame {

    private static final Color FONDO = new Color(0xF3, 0xF4, 0xF6);
    private static final Color TARJETA = Color.WHITE;
    private static final Color BORDE = new Color(0xE2, 0xE5, 0xEA);
    private static final Color TEXTO_SECUNDARIO = new Color(0x6B, 0x72, 0x80);
    private static final Color AZUL = new Color(0x25, 0x63, 0xEB);
    private static final Color ROJO = new Color(0xDC, 0x26, 0x26);
    private static final Color AMBAR = new Color(0xD9, 0x77, 0x06);
    private static final Color VERDE = new Color(0x16, 0xA3, 0x4A);

    private final ProcesadorDocumento procesador = new ProcesadorDocumento();

    private final JComboBox<TipoDocumento> comboTipo = new JComboBox<>(TipoDocumento.values());
    private final JTextArea areaTexto = new JTextArea();
    private final JButton botonCargarArchivo = new JButton("Cargar archivo");
    private final JButton botonAnalizar = new JButton("Analizar documento");
    private final JButton botonExportar = new JButton("Exportar reporte");
    private final JLabel etiquetaEstado = new JLabel(" ");

    private final JTextArea areaClausulas = crearAreaResultado();
    private final JTextPane areaRiesgos = new JTextPane();
    private final JTextArea areaResumen = crearAreaResultado();

    private ReporteAnalisis ultimoReporte;

    public AnalizadorFrame() {
        super("Motor de Análisis Legal — Groq");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 720);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(construirTarjetaEntrada(), BorderLayout.NORTH);
        centro.add(construirPestañas(), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        raiz.add(construirPie(), BorderLayout.SOUTH);

        botonCargarArchivo.addActionListener(e -> cargarArchivo());
        botonAnalizar.addActionListener(e -> analizar());
        botonExportar.addActionListener(e -> exportar());
        botonExportar.setEnabled(false);
    }

    private JPanel construirEncabezado() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("⚖  Motor de Análisis Legal");
        titulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Extracción de cláusulas, riesgos y resumen ejecutivo con IA (Groq)");
        subtitulo.setForeground(TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitulo);
        return panel;
    }

    private JPanel construirTarjetaEntrada() {
        JPanel tarjeta = tarjeta();
        tarjeta.setLayout(new BorderLayout(0, 12));

        JPanel filaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaSuperior.setOpaque(false);

        JLabel etiquetaTipo = new JLabel("Tipo de documento:");
        etiquetaTipo.putClientProperty(FlatClientProperties.STYLE, "font:bold");

        comboTipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TipoDocumento tipo) {
                    setText(etiquetaTipoDocumento(tipo));
                }
                return this;
            }
        });
        comboTipo.putClientProperty(FlatClientProperties.STYLE, "focusWidth:1");

        botonCargarArchivo.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        filaSuperior.add(etiquetaTipo);
        filaSuperior.add(comboTipo);
        filaSuperior.add(botonCargarArchivo);

        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        areaTexto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Pega aquí el texto del documento, o cárgalo desde un archivo .txt...");
        JScrollPane scrollTexto = new JScrollPane(areaTexto);
        scrollTexto.setPreferredSize(new Dimension(0, 170));
        scrollTexto.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));

        JPanel filaBotonAnalizar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaBotonAnalizar.setOpaque(false);
        botonAnalizar.putClientProperty(FlatClientProperties.STYLE,
                "background:#2563EB;foreground:#FFFFFF;font:bold;focusWidth:0;borderWidth:0");
        botonAnalizar.setForeground(Color.WHITE);
        botonAnalizar.setBackground(AZUL);
        filaBotonAnalizar.add(botonAnalizar);

        tarjeta.add(filaSuperior, BorderLayout.NORTH);
        tarjeta.add(scrollTexto, BorderLayout.CENTER);
        tarjeta.add(filaBotonAnalizar, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JTabbedPane construirPestañas() {
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.putClientProperty(FlatClientProperties.STYLE, "tabHeight:34");
        pestañas.addTab("📄  Cláusulas", envolverConMargen(areaClausulas));
        pestañas.addTab("⚠️  Riesgos", envolverConMargen(areaRiesgos));
        pestañas.addTab("📝  Resumen ejecutivo", envolverConMargen(areaResumen));
        return pestañas;
    }

    private JScrollPane envolverConMargen(JTextComponent componente) {
        componente.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        return scroll;
    }

    private JPanel construirPie() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        etiquetaEstado.setForeground(TEXTO_SECUNDARIO);
        panel.add(etiquetaEstado, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botones.setOpaque(false);
        botonExportar.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        botones.add(botonExportar);
        panel.add(botones, BorderLayout.EAST);
        return panel;
    }

    private JPanel tarjeta() {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return tarjeta;
    }

    private static JTextArea crearAreaResultado() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        return area;
    }

    private String etiquetaTipoDocumento(TipoDocumento tipo) {
        return switch (tipo) {
            case ARRENDAMIENTO -> "Contrato de arrendamiento";
            case LABORAL -> "Contrato laboral";
            case TERMINOS_CONDICIONES -> "Términos y condiciones";
        };
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
                    etiquetaEstado.setText("✓ Análisis completado.");
                    etiquetaEstado.setForeground(VERDE);
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
        etiquetaEstado.setForeground(TEXTO_SECUNDARIO);
        etiquetaEstado.setText(cargando ? "⏳ Analizando documento, esto puede tardar unos segundos..." : " ");
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
            SimpleAttributeSet titulo = new SimpleAttributeSet();
            StyleConstants.setBold(titulo, true);
            doc.insertString(doc.getLength(),
                    "RIESGOS IDENTIFICADOS (" + riesgosOrdenados.size() + ")\n\n", titulo);

            for (Riesgo r : riesgosOrdenados) {
                SimpleAttributeSet etiqueta = new SimpleAttributeSet();
                StyleConstants.setForeground(etiqueta, colorNivel(r.nivel()));
                StyleConstants.setBold(etiqueta, true);
                doc.insertString(doc.getLength(), "● [" + r.nivel() + "] ", etiqueta);

                SimpleAttributeSet normal = new SimpleAttributeSet();
                StyleConstants.setBold(normal, true);
                doc.insertString(doc.getLength(), r.descripcion() + "\n", normal);

                SimpleAttributeSet detalle = new SimpleAttributeSet();
                StyleConstants.setForeground(detalle, TEXTO_SECUNDARIO);
                doc.insertString(doc.getLength(), "    Relacionado con: " + r.clausulaRelacionada() + "\n", detalle);
                doc.insertString(doc.getLength(), "    Recomendación: " + r.recomendacion() + "\n\n", detalle);
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
            sb.append("  • ").append(punto).append('\n');
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
            case ALTO -> ROJO;
            case MEDIO -> AMBAR;
            case BAJO -> VERDE;
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
