import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class SistemaSolar extends JPanel implements ActionListener {
    private static final int LARGURA = 1800; // Largura da janela
    private static final int ALTURA = 1400; // Altura da janela
    private static final int DELAY = 10; // Intervalo de atualização da animação (ms)

    // Ângulos para cada planeta e suas luas
    private double[] angulosPlanetas = new double[8]; // 8 planetas
    private double[] angulosLuas = new double[8]; // Luas dos planetas
    private double[] velocidadesPlanetas = {0.1, 0.08, 0.06, 0.05, 0.04, 0.03, 0.02, 0.016}; // Velocidades orbitais dos planetas
    private double[] velocidadesLuas = {0.05, 0.04, 0.03, 0.02, 0.01, 0.008, 0.006, 0.004}; // Velocidades orbitais das luas
    private int[] distanciasPlanetas = {100, 150, 200, 250, 350, 450, 550, 650}; // Distâncias dos planetas ao Sol
    private int[] distanciasLuas = {20, 25, 30, 35, 40, 45, 50, 55}; // Distâncias das luas aos planetas

    // Nomes dos planetas e luas
    private String[] nomesPlanetas = {
            "Mercúrio", "Vênus", "Terra", "Marte", "Júpiter", "Saturno", "Urano", "Netuno"
    };
    private String[][] nomesLuas = {
            {}, {}, {"Lua"}, {"Fobos", "Deimos"}, {"Europa", "Ganimedes", "Io", "Calisto"}, {"Titã", "Reia", "Jápeto"}, {"Titânia", "Oberon"}, {"Tritão"}
    };

    // Cores dos planetas e luas
    private Color[] coresPlanetas = {
            Color.GRAY,    // Mercúrio
            Color.ORANGE,  // Vênus
            Color.BLUE,    // Terra
            Color.RED,     // Marte
            new Color(255, 165, 0), // Júpiter (laranja)
            new Color(210, 180, 140), // Saturno (marrom claro)
            Color.CYAN,    // Urano
            Color.BLUE     // Netuno
    };

    // Tempo orbital dos planetas (em dias terrestres)
    private int[] temposOrbitais = {88, 225, 365, 687, 4333, 10759, 30687, 60190};

    // Contadores de dias para cada planeta
    private int[] diasDecorridos = new int[8];

    // Informações adicionais sobre os planetas
    private String[] informacoesPlanetas = {
            "Mercúrio: O menor planeta do Sistema Solar. Temperatura média: 167°C.",
            "Vênus: O planeta mais quente, com uma atmosfera densa. Temperatura média: 464°C.",
            "Terra: Nosso lar, com uma lua chamada Lua. Temperatura média: 15°C.",
            "Marte: O planeta vermelho, com duas luas: Fobos e Deimos. Temperatura média: -65°C.",
            "Júpiter: O maior planeta, com 79 luas conhecidas. Temperatura média: -110°C.",
            "Saturno: Conhecido por seus anéis impressionantes. Temperatura média: -140°C.",
            "Urano: Um gigante gelado com uma inclinação axial extrema. Temperatura média: -195°C.",
            "Netuno: O planeta mais distante do Sol, com ventos supersônicos. Temperatura média: -200°C."
    };

    // Painel de informações
    private JLabel infoLabel;

    // Planeta selecionado (para feedback visual)
    private int planetaSelecionado = -1;

    // Velocidade da simulação
    private double velocidadeSimulacao = 1.0;
    private JSlider sliderVelocidade;

    // Estrelas no fundo
    private int[][] estrelas;

    public SistemaSolar() {
        // Configura o painel de informações
        infoLabel = new JLabel("Clique em um planeta para ver informações.", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBackground(Color.BLACK);
        infoLabel.setOpaque(true);
        this.setLayout(new BorderLayout());
        this.add(infoLabel, BorderLayout.SOUTH);

        // Adiciona interatividade com o mouse
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                verificarClique(x, y);
            }
        });

        // Configura o slider de velocidade
        sliderVelocidade = new JSlider(1, 10, 1);
        sliderVelocidade.setMajorTickSpacing(1);
        sliderVelocidade.setPaintTicks(true);
        sliderVelocidade.setPaintLabels(true);
        sliderVelocidade.addChangeListener(e -> velocidadeSimulacao = sliderVelocidade.getValue());
        JPanel painelControle = new JPanel();
        painelControle.setBackground(Color.BLACK);
        painelControle.add(new JLabel("Velocidade: "));
        painelControle.add(sliderVelocidade);
        this.add(painelControle, BorderLayout.NORTH);

        // Gera estrelas no fundo
        Random random = new Random();
        estrelas = new int[200][2];
        for (int i = 0; i < estrelas.length; i++) {
            estrelas[i][0] = random.nextInt(LARGURA);
            estrelas[i][1] = random.nextInt(ALTURA);
        }

        Timer timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Fundo preto com estrelas
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, LARGURA, ALTURA);
        g2d.setColor(Color.WHITE);
        for (int[] estrela : estrelas) {
            g2d.fillOval(estrela[0], estrela[1], 2, 2);
        }

        // Desenha o Sol
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(LARGURA / 2 - 50, ALTURA / 2 - 50, 100, 100);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Sol", LARGURA / 2 - 15, ALTURA / 2 + 70);

        // Desenha as órbitas elípticas dos planetas
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 8; i++) {
            Ellipse2D orbita = new Ellipse2D.Double(
                    LARGURA / 2 - distanciasPlanetas[i] * 1.2,
                    ALTURA / 2 - distanciasPlanetas[i],
                    distanciasPlanetas[i] * 2.4,
                    distanciasPlanetas[i] * 2
            );
            g2d.draw(orbita);
        }

        // Desenha os planetas e suas luas
        for (int i = 0; i < 8; i++) {
            // Posição do planeta (órbita elíptica)
            int planetaX = (int) (LARGURA / 2 + distanciasPlanetas[i] * 1.2 * Math.cos(Math.toRadians(angulosPlanetas[i])));
            int planetaY = (int) (ALTURA / 2 + distanciasPlanetas[i] * Math.sin(Math.toRadians(angulosPlanetas[i])));

            // Desenha o planeta
            g2d.setColor(coresPlanetas[i]);
            g2d.fillOval(planetaX - 10, planetaY - 10, 20, 20);

            // Desenha os anéis de Saturno
            if (i == 5) { // Saturno
                g2d.setColor(new Color(210, 180, 140, 100));
                g2d.fillOval(planetaX - 20, planetaY - 15, 40, 30);
            }

            // Destaca o planeta selecionado
            if (i == planetaSelecionado) {
                g2d.setColor(Color.YELLOW);
                g2d.drawOval(planetaX - 12, planetaY - 12, 24, 24);
            }

            // Calcula o tempo decorrido e zera ao completar a órbita
            diasDecorridos[i] = (int) ((angulosPlanetas[i] / 360.0) * temposOrbitais[i]) % temposOrbitais[i];

            // Desenha o nome do planeta, o tempo decorrido e o tempo total da órbita
            g2d.setColor(Color.WHITE);
            g2d.drawString(nomesPlanetas[i], planetaX + 15, planetaY + 5);
            g2d.drawString(diasDecorridos[i] + " / " + temposOrbitais[i] + " dias", planetaX + 15, planetaY + 20);

            // Desenha as luas (se existirem)
            for (int j = 0; j < nomesLuas[i].length; j++) {
                int luaX = (int) (planetaX + distanciasLuas[i] * Math.cos(Math.toRadians(angulosLuas[i] + j * 90)));
                int luaY = (int) (planetaY + distanciasLuas[i] * Math.sin(Math.toRadians(angulosLuas[i] + j * 90)));
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(luaX - 5, luaY - 5, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString(nomesLuas[i][j], luaX + 10, luaY + 5);

                // Atualiza o ângulo da lua
                angulosLuas[i] += velocidadesLuas[i] / (j + 1) * velocidadeSimulacao;
            }

            // Atualiza o ângulo do planeta
            angulosPlanetas[i] += velocidadesPlanetas[i] * velocidadeSimulacao;
        }
    }

    // Verifica se o usuário clicou em um planeta
    private void verificarClique(int x, int y) {
        for (int i = 0; i < 8; i++) {
            int planetaX = (int) (LARGURA / 2 + distanciasPlanetas[i] * 1.2 * Math.cos(Math.toRadians(angulosPlanetas[i])));
            int planetaY = (int) (ALTURA / 2 + distanciasPlanetas[i] * Math.sin(Math.toRadians(angulosPlanetas[i])));

            // Verifica se o clique foi próximo ao planeta
            if (Math.abs(x - planetaX) < 15 && Math.abs(y - planetaY) < 15) {
                infoLabel.setText(informacoesPlanetas[i]);
                planetaSelecionado = i; // Destaca o planeta selecionado
                break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema Solar com Melhorias");
        SistemaSolar sistemaSolar = new SistemaSolar();
        frame.add(sistemaSolar);
        frame.setSize(LARGURA, ALTURA);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}