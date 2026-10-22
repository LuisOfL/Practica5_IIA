import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;

/**
 * MenuApp - Interfaz gráfica para la Práctica 5 (Generador de Menús con Prolog)
 * Conecta Java Swing con SWI-Prolog a través de la biblioteca JPL.
 */
public class MenuApp extends JFrame {

    // Paleta de colores limpia y moderna
    private static final Color COLOR_BG             = new Color(248, 250, 252);
    private static final Color COLOR_SURFACE        = new Color(255, 255, 255);
    private static final Color COLOR_BORDER         = new Color(226, 232, 240);
    private static final Color COLOR_BORDER_FOCUS   = new Color(13, 148, 136);
    
    private static final Color COLOR_TEXT_PRIMARY   = new Color(15, 23, 42);
    private static final Color COLOR_TEXT_SECONDARY = new Color(71, 85, 105);
    private static final Color COLOR_TEXT_MUTED     = new Color(148, 163, 184);

    private static final Color COLOR_PRIMARY        = new Color(13, 148, 136);
    private static final Color COLOR_PRIMARY_HOVER  = new Color(15, 118, 110);
    private static final Color COLOR_PRIMARY_LIGHT  = new Color(240, 253, 250);
    
    private static final Color COLOR_SUCCESS        = new Color(16, 185, 129);
    private static final Color COLOR_SUCCESS_LIGHT  = new Color(236, 253, 245);
    private static final Color COLOR_WARNING        = new Color(245, 158, 11);
    private static final Color COLOR_DANGER         = new Color(239, 68, 68);

    // Tipografías estándar compatibles con Windows / Linux / Mac
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 17);
    private static final Font FONT_SUB     = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BADGE   = new Font("Segoe UI", Font.BOLD, 11);

    // Motor Prolog y datos
    private PrologManager prologManager;
    private List<Map<String, Object>> menusEncontrados;
    private int indiceActual = 0;
    private double ultimaTmb = 0.0;

    // Entradas
    private char sexoSeleccionado = 'M';
    private ModernButton btnSexoM, btnSexoF;
    private ModernTextField txtEdad, txtEstatura, txtPeso;
    private JLabel lblImcLive;
    private JPanel cardTmbResult;
    private JLabel lblTmbValor, lblTmbRango;

    // Visualizador de resultados
    private JPanel panelCards;
    private JLabel lblContadorBadge;
    private JLabel lblCaloriasBadge;
    private JLabel lblEstadoMensaje;
    private ModernButton btnAnterior, btnSiguiente, btnCopiar;

    public MenuApp() {
        setTitle("Generador de Menus - Practica 5 (Prolog + Java)");
        setSize(1060, 740);
        setMinimumSize(new Dimension(920, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        // Conectar a Prolog
        try {
            prologManager = new PrologManager("menu_kb.pl");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar menu_kb.pl en SWI-Prolog:\n" + e.getMessage(), 
                "Error de Prolog", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        actualizarImcLive();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));

        // Barra superior
        add(crearHeader(), BorderLayout.NORTH);

        // Contenedor principal de dos columnas
        JPanel mainContent = new JPanel(new BorderLayout(16, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(16, 20, 16, 20));

        mainContent.add(crearPanelEntrada(), BorderLayout.WEST);
        mainContent.add(crearPanelResultados(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        header.setBackground(COLOR_SURFACE);
        header.setBorder(new EmptyBorder(12, 24, 12, 24));

        // Título del proyecto
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        // Icono dibujado con gráficos vectoriales (sin emojis rotos)
        JPanel iconPlate = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(204, 251, 241));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Dibujo de plato sutil
                g2.setColor(COLOR_PRIMARY);
                g2.setStroke(new BasicStroke(2.0f));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawOval(cx - 10, cy - 10, 20, 20);
                g2.drawOval(cx - 5, cy - 5, 10, 10);
                g2.dispose();
            }
        };
        iconPlate.setPreferredSize(new Dimension(38, 38));
        iconPlate.setOpaque(false);

        JPanel textBrand = new JPanel(new GridLayout(2, 1, 0, 2));
        textBrand.setOpaque(false);
        JLabel lblTitulo = new JLabel("Generador de Menus");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel("Calculo de TMB y restricciones caloricas con SWI-Prolog");
        lblSubtitulo.setFont(FONT_SUB);
        lblSubtitulo.setForeground(COLOR_TEXT_SECONDARY);

        textBrand.add(lblTitulo);
        textBrand.add(lblSubtitulo);

        brandPanel.add(iconPlate);
        brandPanel.add(textBrand);
        header.add(brandPanel, BorderLayout.WEST);

        // Indicador de conexión con Prolog
        JPanel statusBadge = new RoundedPanel(10, COLOR_SUCCESS_LIGHT, new Color(167, 243, 208));
        statusBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
        
        JLabel dot = new JLabel("\u2022");
        dot.setForeground(COLOR_SUCCESS);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel lblStatus = new JLabel("Prolog Conectado");
        lblStatus.setFont(FONT_BADGE);
        lblStatus.setForeground(new Color(6, 95, 70));

        statusBadge.add(dot);
        statusBadge.add(lblStatus);

        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        rightWrapper.setOpaque(false);
        rightWrapper.add(statusBadge);
        header.add(rightWrapper, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelEntrada() {
        RoundedPanel card = new RoundedPanel(14, COLOR_SURFACE, COLOR_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(330, 0));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Título de la sección
        JLabel lblSeccion = new JLabel("Datos del Usuario");
        lblSeccion.setFont(FONT_HEADER);
        lblSeccion.setForeground(COLOR_TEXT_PRIMARY);
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblSeccion);

        JLabel lblSeccionDesc = new JLabel("Personas adultas (edad mayor o igual a 18 anos)");
        lblSeccionDesc.setFont(FONT_SMALL);
        lblSeccionDesc.setForeground(COLOR_TEXT_SECONDARY);
        lblSeccionDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblSeccionDesc);

        card.add(Box.createVerticalStrut(16));

        // Selector de Sexo
        JLabel lblSexo = new JLabel("Sexo:");
        lblSexo.setFont(FONT_LABEL);
        lblSexo.setForeground(COLOR_TEXT_PRIMARY);
        lblSexo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblSexo);
        card.add(Box.createVerticalStrut(6));

        JPanel panelSexo = new JPanel(new GridLayout(1, 2, 8, 0));
        panelSexo.setOpaque(false);
        panelSexo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panelSexo.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSexoM = new ModernButton("Masculino", ModernButton.Style.SEGMENTED_ACTIVE);
        btnSexoF = new ModernButton("Femenino", ModernButton.Style.SEGMENTED_INACTIVE);

        btnSexoM.addActionListener(e -> seleccionarSexo('M'));
        btnSexoF.addActionListener(e -> seleccionarSexo('F'));

        panelSexo.add(btnSexoM);
        panelSexo.add(btnSexoF);
        card.add(panelSexo);

        card.add(Box.createVerticalStrut(14));

        // Campos numéricos
        card.add(crearGrupoCampo("Edad (anos):", txtEdad = new ModernTextField("25", "anos")));
        card.add(Box.createVerticalStrut(10));

        card.add(crearGrupoCampo("Estatura (metros, ej. 1.75):", txtEstatura = new ModernTextField("1.75", "metros")));
        card.add(Box.createVerticalStrut(10));

        card.add(crearGrupoCampo("Peso (kg):", txtPeso = new ModernTextField("70.0", "kg")));
        card.add(Box.createVerticalStrut(14));

        // Cálculo reactivo del IMC
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarImcLive(); }
            public void removeUpdate(DocumentEvent e) { actualizarImcLive(); }
            public void changedUpdate(DocumentEvent e) { actualizarImcLive(); }
        };
        txtEstatura.getDocument().addDocumentListener(docListener);
        txtPeso.getDocument().addDocumentListener(docListener);

        JPanel panelImc = new RoundedPanel(8, new Color(241, 245, 249), COLOR_BORDER);
        panelImc.setLayout(new BorderLayout(8, 0));
        panelImc.setBorder(new EmptyBorder(8, 12, 8, 12));
        panelImc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panelImc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblImcTitulo = new JLabel("IMC:");
        lblImcTitulo.setFont(FONT_BADGE);
        lblImcTitulo.setForeground(COLOR_TEXT_SECONDARY);

        lblImcLive = new JLabel("22.9 (Normal)");
        lblImcLive.setFont(FONT_BADGE);
        lblImcLive.setForeground(COLOR_SUCCESS);
        lblImcLive.setHorizontalAlignment(SwingConstants.RIGHT);

        panelImc.add(lblImcTitulo, BorderLayout.WEST);
        panelImc.add(lblImcLive, BorderLayout.EAST);
        card.add(panelImc);

        card.add(Box.createVerticalStrut(16));

        // Botón principal
        ModernButton btnGenerar = new ModernButton("Calcular TMB y Generar Menus", ModernButton.Style.PRIMARY);
        btnGenerar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnGenerar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGenerar.addActionListener(this::onGenerarClick);
        card.add(btnGenerar);

        getRootPane().setDefaultButton(btnGenerar);

        card.add(Box.createVerticalStrut(14));

        // Tarjeta de TMB
        cardTmbResult = new RoundedPanel(10, COLOR_PRIMARY_LIGHT, new Color(153, 246, 228));
        cardTmbResult.setLayout(new BoxLayout(cardTmbResult, BoxLayout.Y_AXIS));
        cardTmbResult.setBorder(new EmptyBorder(10, 12, 10, 12));
        cardTmbResult.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        cardTmbResult.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTmbTitulo = new JLabel("Tasa Metabolica Basal (TMB)");
        lblTmbTitulo.setFont(FONT_BADGE);
        lblTmbTitulo.setForeground(COLOR_PRIMARY_HOVER);

        lblTmbValor = new JLabel("Sin calcular");
        lblTmbValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTmbValor.setForeground(COLOR_PRIMARY_HOVER);

        lblTmbRango = new JLabel("Rango del menu: 90% a 110% de TMB");
        lblTmbRango.setFont(FONT_SMALL);
        lblTmbRango.setForeground(COLOR_TEXT_SECONDARY);

        cardTmbResult.add(lblTmbTitulo);
        cardTmbResult.add(Box.createVerticalStrut(2));
        cardTmbResult.add(lblTmbValor);
        cardTmbResult.add(Box.createVerticalStrut(2));
        cardTmbResult.add(lblTmbRango);
        card.add(cardTmbResult);

        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel crearGrupoCampo(String labelText, ModernTextField textField) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(COLOR_TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        group.add(lbl);
        group.add(Box.createVerticalStrut(4));
        group.add(textField);
        return group;
    }

    private void seleccionarSexo(char sexo) {
        this.sexoSeleccionado = sexo;
        if (sexo == 'M') {
            btnSexoM.setStyle(ModernButton.Style.SEGMENTED_ACTIVE);
            btnSexoF.setStyle(ModernButton.Style.SEGMENTED_INACTIVE);
        } else {
            btnSexoM.setStyle(ModernButton.Style.SEGMENTED_INACTIVE);
            btnSexoF.setStyle(ModernButton.Style.SEGMENTED_ACTIVE);
        }
    }

    private void actualizarImcLive() {
        try {
            double estatura = Double.parseDouble(txtEstatura.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim());
            if (estatura > 0.5 && estatura < 2.5 && peso > 20 && peso < 300) {
                double imc = peso / (estatura * estatura);
                String estado;
                Color col;
                if (imc < 18.5) {
                    estado = "Bajo peso";
                    col = COLOR_WARNING;
                } else if (imc < 25.0) {
                    estado = "Normal";
                    col = COLOR_SUCCESS;
                } else if (imc < 30.0) {
                    estado = "Sobrepeso";
                    col = new Color(234, 88, 12);
                } else {
                    estado = "Obesidad";
                    col = COLOR_DANGER;
                }
                lblImcLive.setText(String.format(java.util.Locale.US, "%.1f (%s)", imc, estado));
                lblImcLive.setForeground(col);
                return;
            }
        } catch (Exception ignored) {}
        lblImcLive.setText("-");
        lblImcLive.setForeground(COLOR_TEXT_MUTED);
    }

    private JPanel crearPanelResultados() {
        RoundedPanel card = new RoundedPanel(14, COLOR_SURFACE, COLOR_BORDER);
        card.setLayout(new BorderLayout(0, 0));

        // Toolbar superior
        JPanel toolbar = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        toolbar.setBackground(COLOR_SURFACE);
        toolbar.setBorder(new EmptyBorder(12, 18, 12, 18));

        JPanel leftTool = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftTool.setOpaque(false);

        lblContadorBadge = new JLabel("Menus: 0");
        lblContadorBadge.setFont(FONT_BADGE);
        lblContadorBadge.setForeground(COLOR_TEXT_PRIMARY);
        lblContadorBadge.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, COLOR_BORDER),
            new EmptyBorder(5, 10, 5, 10)
        ));

        lblCaloriasBadge = new JLabel("Total: - kcal");
        lblCaloriasBadge.setFont(FONT_BADGE);
        lblCaloriasBadge.setForeground(COLOR_PRIMARY);
        lblCaloriasBadge.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, new Color(204, 251, 241)),
            new EmptyBorder(5, 10, 5, 10)
        ));

        leftTool.add(lblContadorBadge);
        leftTool.add(lblCaloriasBadge);
        toolbar.add(leftTool, BorderLayout.WEST);

        // Botones de navegación y acción
        JPanel rightTool = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightTool.setOpaque(false);

        btnCopiar = new ModernButton("Copiar Menu", ModernButton.Style.OUTLINE);
        btnCopiar.setEnabled(false);
        btnCopiar.addActionListener(e -> copiarMenuAlPortapapeles());

        btnAnterior = new ModernButton("Anterior", ModernButton.Style.NAV);
        btnSiguiente = new ModernButton("Siguiente", ModernButton.Style.NAV);
        btnAnterior.setEnabled(false);
        btnSiguiente.setEnabled(false);

        btnAnterior.addActionListener(e -> cambiarMenu(-1));
        btnSiguiente.addActionListener(e -> cambiarMenu(1));

        rightTool.add(btnCopiar);
        rightTool.add(btnAnterior);
        rightTool.add(btnSiguiente);
        toolbar.add(rightTool, BorderLayout.EAST);

        card.add(toolbar, BorderLayout.NORTH);

        // Área central desplazable
        panelCards = new JPanel();
        panelCards.setLayout(new BoxLayout(panelCards, BoxLayout.Y_AXIS));
        panelCards.setOpaque(false);
        panelCards.setBorder(new EmptyBorder(16, 18, 16, 18));

        mostrarEstadoVacio(panelCards);

        JScrollPane scrollPane = new JScrollPane(panelCards);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        card.add(scrollPane, BorderLayout.CENTER);

        // Barra de estado inferior
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(250, 250, 250));
        footer.setBorder(new EmptyBorder(8, 18, 8, 18));

        lblEstadoMensaje = new JLabel("Ingrese sus datos y presione 'Calcular TMB y Generar Menus'.");
        lblEstadoMensaje.setFont(FONT_SMALL);
        lblEstadoMensaje.setForeground(COLOR_TEXT_SECONDARY);
        footer.add(lblEstadoMensaje, BorderLayout.CENTER);

        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private void mostrarEstadoVacio(JPanel target) {
        target.removeAll();
        target.add(Box.createVerticalGlue());

        JPanel emptyCard = new JPanel();
        emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
        emptyCard.setOpaque(false);
        emptyCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icono dibujado con líneas limpias (sin emojis rotos)
        JPanel plateIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(2.5f));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawOval(cx - 26, cy - 26, 52, 52);
                g2.drawOval(cx - 15, cy - 15, 30, 30);
                g2.dispose();
            }
        };
        plateIcon.setPreferredSize(new Dimension(64, 64));
        plateIcon.setMaximumSize(new Dimension(64, 64));
        plateIcon.setOpaque(false);
        plateIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Sin menus generados");
        lblTitulo.setFont(FONT_HEADER);
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>Ingresa tus datos a la izquierda y presiona el boton para consultar las alternativas validas en Prolog.</center></html>");
        lblDesc.setFont(FONT_BODY);
        lblDesc.setForeground(COLOR_TEXT_SECONDARY);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyCard.add(plateIcon);
        emptyCard.add(Box.createVerticalStrut(10));
        emptyCard.add(lblTitulo);
        emptyCard.add(Box.createVerticalStrut(4));
        emptyCard.add(lblDesc);
        emptyCard.add(Box.createVerticalStrut(16));

        // Tabla de restricciones calóricas
        JPanel rulesCard = new RoundedPanel(10, new Color(248, 250, 252), COLOR_BORDER);
        rulesCard.setLayout(new GridLayout(2, 3, 10, 6));
        rulesCard.setBorder(new EmptyBorder(10, 14, 10, 14));
        rulesCard.setMaximumSize(new Dimension(500, 70));
        rulesCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        rulesCard.add(crearReglaBadge("Desayuno", "20% - 30%"));
        rulesCard.add(crearReglaBadge("Almuerzo", "10% - 15%"));
        rulesCard.add(crearReglaBadge("Comida", "30% - 40%"));
        rulesCard.add(crearReglaBadge("Merienda", "10% - 15%"));
        rulesCard.add(crearReglaBadge("Cena", "20% - 25%"));
        rulesCard.add(crearReglaBadge("Total", "90% - 110%"));

        emptyCard.add(rulesCard);

        target.add(emptyCard);
        target.add(Box.createVerticalGlue());
        target.revalidate();
        target.repaint();
    }

    private JPanel crearReglaBadge(String titulo, String pct) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(FONT_SMALL);
        t.setForeground(COLOR_TEXT_SECONDARY);
        JLabel v = new JLabel(pct);
        v.setFont(FONT_BADGE);
        v.setForeground(COLOR_PRIMARY_HOVER);
        p.add(t, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    private void onGenerarClick(ActionEvent e) {
        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            double estatura = Double.parseDouble(txtEstatura.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim());
            char sexo = sexoSeleccionado;

            if (edad < 18) {
                JOptionPane.showMessageDialog(this, 
                    "La practica considera unicamente a personas adultas (edad mayor o igual a 18 anos).", 
                    "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (estatura <= 0.5 || estatura > 2.5) {
                JOptionPane.showMessageDialog(this, 
                    "Ingrese una estatura valida en metros (ej. 1.75).", 
                    "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (peso <= 20 || peso > 350) {
                JOptionPane.showMessageDialog(this, 
                    "Ingrese un peso valido en kilogramos (ej. 70.0).", 
                    "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ultimaTmb = prologManager.calcularTMB(sexo, edad, estatura, peso);
            lblTmbValor.setText(String.format(java.util.Locale.US, "%.1f kcal / dia", ultimaTmb));
            lblTmbRango.setText(String.format(java.util.Locale.US, "Meta (90%% - 110%%): %.0f - %.0f kcal", 
                ultimaTmb * 0.90, ultimaTmb * 1.10));

            lblEstadoMensaje.setText("Consultando restricciones en SWI-Prolog...");

            menusEncontrados = prologManager.buscarMenusValidos(ultimaTmb, 25);
            indiceActual = 0;

            actualizarVistaMenu();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese valores numericos validos.", 
                "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error en Prolog: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarVistaMenu() {
        panelCards.removeAll();

        if (menusEncontrados == null || menusEncontrados.isEmpty()) {
            lblEstadoMensaje.setText("No se encontraron menus que cumplan simultaneamente todas las restricciones caloricas.");
            lblContadorBadge.setText("Menus: 0");
            lblCaloriasBadge.setText("Total: 0 kcal");
            btnAnterior.setEnabled(false);
            btnSiguiente.setEnabled(false);
            btnCopiar.setEnabled(false);

            JPanel noRes = new JPanel();
            noRes.setLayout(new BoxLayout(noRes, BoxLayout.Y_AXIS));
            noRes.setOpaque(false);
            noRes.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel title = new JLabel("Sin combinaciones validas");
            title.setFont(FONT_HEADER);
            title.setForeground(COLOR_TEXT_PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel desc = new JLabel(String.format(java.util.Locale.US, 
                "<html><center>Para una TMB de <b>%.1f kcal</b> no se encontraron combinaciones en la base de hechos.<br>" +
                "Prueba ajustando los valores de peso/estatura o agregando mas alimentos a menu_kb.pl.</center></html>", ultimaTmb));
            desc.setFont(FONT_BODY);
            desc.setForeground(COLOR_TEXT_SECONDARY);
            desc.setAlignmentX(Component.CENTER_ALIGNMENT);

            noRes.add(Box.createVerticalStrut(30));
            noRes.add(title);
            noRes.add(Box.createVerticalStrut(6));
            noRes.add(desc);

            panelCards.add(noRes);
            panelCards.revalidate();
            panelCards.repaint();
            return;
        }

        int total = menusEncontrados.size();
        lblContadorBadge.setText(String.format("Menu %d de %d", indiceActual + 1, total));
        lblEstadoMensaje.setText(String.format("Se encontraron %d alternativas validas.", total));

        btnAnterior.setEnabled(indiceActual > 0);
        btnSiguiente.setEnabled(indiceActual < total - 1);
        btnCopiar.setEnabled(true);

        Map<String, Object> menu = menusEncontrados.get(indiceActual);
        double totalKcal = (double) menu.get("total_kcal");
        double pctTmb = (totalKcal / ultimaTmb) * 100.0;

        lblCaloriasBadge.setText(String.format(java.util.Locale.US, "Total: %.1f kcal (%.1f%%)", totalKcal, pctTmb));

        // Resumen superior del menú
        JPanel summaryCard = new RoundedPanel(10, COLOR_SUCCESS_LIGHT, new Color(167, 243, 208));
        summaryCard.setLayout(new BorderLayout(12, 0));
        summaryCard.setBorder(new EmptyBorder(8, 14, 8, 14));
        summaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSumLeft = new JLabel(String.format(java.util.Locale.US, "Menu #%d  -  Total: %.1f kcal", indiceActual + 1, totalKcal));
        lblSumLeft.setFont(FONT_BOLD);
        lblSumLeft.setForeground(new Color(6, 95, 70));

        JLabel lblSumRight = new JLabel(String.format(java.util.Locale.US, "%.1f%% de TMB (Rango 90%% - 110%%)", pctTmb));
        lblSumRight.setFont(FONT_BADGE);
        lblSumRight.setForeground(COLOR_SUCCESS);

        summaryCard.add(lblSumLeft, BorderLayout.WEST);
        summaryCard.add(lblSumRight, BorderLayout.EAST);
        panelCards.add(summaryCard);
        panelCards.add(Box.createVerticalStrut(10));

        // Tarjetas individuales sin emojis
        agregarTarjetaTiempo("Desayuno", "20% - 30% de TMB", (Map<String, Object>) menu.get("desayuno"), 
            new Color(217, 119, 6), new Color(254, 243, 199));
            
        agregarTarjetaTiempo("Almuerzo / Colacion Matutina", "10% - 15% de TMB", (Map<String, Object>) menu.get("almuerzo"), 
            new Color(5, 150, 105), new Color(236, 253, 245));
            
        agregarTarjetaTiempo("Comida", "30% - 40% de TMB", (Map<String, Object>) menu.get("comida"), 
            new Color(37, 99, 235), new Color(239, 246, 255));
            
        agregarTarjetaTiempo("Merienda / Colacion Vespertina", "10% - 15% de TMB", (Map<String, Object>) menu.get("merienda"), 
            new Color(124, 58, 237), new Color(245, 243, 255));
            
        agregarTarjetaTiempo("Cena", "20% - 25% de TMB", (Map<String, Object>) menu.get("cena"), 
            new Color(13, 148, 136), new Color(240, 253, 250));

        panelCards.revalidate();
        panelCards.repaint();
    }

    private void agregarTarjetaTiempo(String titulo, String metaTexto, Map<String, Object> item, 
                                     Color accentColor, Color bgBadge) {
        RoundedPanel card = new RoundedPanel(10, COLOR_SURFACE, COLOR_BORDER);
        card.setLayout(new BorderLayout(10, 6));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_HEADER);
        lblTitulo.setForeground(accentColor);

        JLabel lblMeta = new JLabel(metaTexto);
        lblMeta.setFont(FONT_SMALL);
        lblMeta.setForeground(COLOR_TEXT_SECONDARY);

        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(lblMeta, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);

        String nombre = item != null ? (String) item.getOrDefault("nombre", "-") : "-";
        String porcion = item != null ? (String) item.getOrDefault("porcion", "") : "";
        double kcal = item != null ? (double) item.getOrDefault("kcal", 0.0) : 0.0;
        double pctTmb = ultimaTmb > 0 ? (kcal / ultimaTmb) * 100.0 : 0.0;

        JPanel bodyPanel = new JPanel(new BorderLayout(10, 0));
        bodyPanel.setOpaque(false);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        namePanel.setOpaque(false);

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(FONT_BOLD);
        lblNombre.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblPorcion = new JLabel("- " + porcion);
        lblPorcion.setFont(FONT_BODY);
        lblPorcion.setForeground(COLOR_TEXT_SECONDARY);

        namePanel.add(lblNombre);
        namePanel.add(lblPorcion);
        bodyPanel.add(namePanel, BorderLayout.WEST);

        JPanel pillKcal = new RoundedPanel(8, bgBadge, accentColor);
        pillKcal.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 3));
        JLabel lblKcal = new JLabel(String.format(java.util.Locale.US, "%.0f kcal (%.1f%%)", kcal, pctTmb));
        lblKcal.setFont(FONT_BADGE);
        lblKcal.setForeground(accentColor);
        pillKcal.add(lblKcal);

        bodyPanel.add(pillKcal, BorderLayout.EAST);
        card.add(bodyPanel, BorderLayout.CENTER);

        panelCards.add(card);
        panelCards.add(Box.createVerticalStrut(6));
    }

    private void cambiarMenu(int delta) {
        indiceActual += delta;
        actualizarVistaMenu();
    }

    private void copiarMenuAlPortapapeles() {
        if (menusEncontrados == null || menusEncontrados.isEmpty()) return;

        Map<String, Object> menu = menusEncontrados.get(indiceActual);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(java.util.Locale.US, "MENU SELECCIONADO (Alternativa %d de %d)\n", indiceActual + 1, menusEncontrados.size()));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format(java.util.Locale.US, "TMB: %.1f kcal/dia | Total del menu: %.1f kcal\n\n", ultimaTmb, (double) menu.get("total_kcal")));

        agregarItemTexto(sb, "Desayuno", (Map<String, Object>) menu.get("desayuno"));
        agregarItemTexto(sb, "Almuerzo", (Map<String, Object>) menu.get("almuerzo"));
        agregarItemTexto(sb, "Comida", (Map<String, Object>) menu.get("comida"));
        agregarItemTexto(sb, "Merienda", (Map<String, Object>) menu.get("merienda"));
        agregarItemTexto(sb, "Cena", (Map<String, Object>) menu.get("cena"));

        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
            lblEstadoMensaje.setText("Menu copiado al portapapeles.");
        } catch (Exception ex) {
            lblEstadoMensaje.setText("No se pudo copiar.");
        }
    }

    private void agregarItemTexto(StringBuilder sb, String tiempo, Map<String, Object> item) {
        if (item != null) {
            sb.append(String.format(java.util.Locale.US, "- %s: %s (%s) - %.1f kcal\n",
                tiempo, item.get("nombre"), item.get("porcion"), (double) item.get("kcal")));
        }
    }

    // Componentes personalizados
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;
        private final Color borderColor;

        public RoundedPanel(int radius, Color bg, Color border) {
            this.radius = radius;
            this.backgroundColor = bg;
            this.borderColor = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundColor != null) {
                g2.setColor(backgroundColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            }

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedBorder implements javax.swing.border.Border {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() { return false; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
    }

    static class ModernTextField extends JTextField {
        private boolean isFocused = false;

        public ModernTextField(String text, String tooltip) {
            super(text);
            setFont(FONT_BODY);
            setForeground(COLOR_TEXT_PRIMARY);
            setCaretColor(COLOR_PRIMARY);
            setToolTipText(tooltip);
            setOpaque(false);
            setBorder(new EmptyBorder(7, 10, 7, 10));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(COLOR_SURFACE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            if (isFocused) {
                g2.setColor(COLOR_BORDER_FOCUS);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(COLOR_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ModernButton extends JButton {
        public enum Style {
            PRIMARY,
            NAV,
            OUTLINE,
            SEGMENTED_ACTIVE,
            SEGMENTED_INACTIVE
        }

        private Style style;
        private boolean isHovered = false;

        public ModernButton(String text, Style style) {
            super(text);
            this.style = style;
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            actualizarColores();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setStyle(Style style) {
            this.style = style;
            actualizarColores();
            repaint();
        }

        private void actualizarColores() {
            switch (style) {
                case PRIMARY:
                    setForeground(Color.WHITE);
                    break;
                case NAV:
                    setForeground(COLOR_TEXT_PRIMARY);
                    break;
                case OUTLINE:
                    setForeground(COLOR_TEXT_SECONDARY);
                    break;
                case SEGMENTED_ACTIVE:
                    setForeground(Color.WHITE);
                    break;
                case SEGMENTED_INACTIVE:
                    setForeground(COLOR_TEXT_SECONDARY);
                    break;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = COLOR_SURFACE;
            Color border = COLOR_BORDER;
            int radius = 8;

            if (!isEnabled()) {
                bg = new Color(241, 245, 249);
                border = new Color(226, 232, 240);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
                super.paintComponent(g);
                return;
            }

            switch (style) {
                case PRIMARY:
                    bg = isHovered ? COLOR_PRIMARY_HOVER : COLOR_PRIMARY;
                    border = bg;
                    radius = 8;
                    break;
                case NAV:
                    bg = isHovered ? new Color(241, 245, 249) : COLOR_SURFACE;
                    border = COLOR_BORDER;
                    break;
                case OUTLINE:
                    bg = isHovered ? new Color(248, 250, 252) : COLOR_SURFACE;
                    border = isHovered ? COLOR_TEXT_SECONDARY : COLOR_BORDER;
                    break;
                case SEGMENTED_ACTIVE:
                    bg = COLOR_PRIMARY;
                    border = COLOR_PRIMARY;
                    radius = 6;
                    break;
                case SEGMENTED_INACTIVE:
                    bg = isHovered ? new Color(241, 245, 249) : new Color(248, 250, 252);
                    border = COLOR_BORDER;
                    radius = 6;
                    break;
            }

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            if (border != null) {
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(203, 213, 225);
            this.trackColor = COLOR_BG;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) { return createInvisibleButton(); }

        @Override
        protected JButton createIncreaseButton(int orientation) { return createInvisibleButton(); }

        private JButton createInvisibleButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MenuApp().setVisible(true);
        });
    }
}