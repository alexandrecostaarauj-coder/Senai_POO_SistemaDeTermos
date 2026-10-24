package visao;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Cores da logo da Performance Place e o visual dos botões.
 * Fica tudo aqui para todas as telas ficarem iguais: mudou a cor, muda em um lugar só.
 *
 * @author Alexandre Costa Araujo
 */
public final class Estilo {

    public static final Color ROSA = new Color(253, 174, 177);        // rosa da logo
    public static final Color ROSA_ESCURO = new Color(200, 90, 100);  // botões e destaques
    public static final Color ROSA_CLARO = new Color(255, 240, 241);  // fundos
    public static final Color TEXTO = new Color(59, 47, 48);

    private Estilo() { }

    /** Botão cheio (ação principal): fundo rosa escuro, letra branca. */
    public static void botaoPrimario(JButton botao) {
        aplicar(botao, ROSA_ESCURO, Color.WHITE);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    /** Botão vazado (ação secundária): fundo branco, borda e letra rosa. */
    public static void botaoSecundario(JButton botao) {
        aplicar(botao, Color.WHITE, ROSA_ESCURO);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_ESCURO, 2),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
    }

    private static void aplicar(JButton botao, Color fundo, Color letra) {
        // O tema Nimbus ignora setBackground em botões; o BasicButtonUI respeita a cor.
        botao.setUI(new BasicButtonUI());
        botao.setBackground(fundo);
        botao.setForeground(letra);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // efeito ao passar o mouse
        Color fundoHover = misturar(fundo, ROSA, 0.35);
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(fundoHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(fundo);
            }
        });
    }

    private static Color misturar(Color a, Color b, double peso) {
        int r = (int) (a.getRed() * (1 - peso) + b.getRed() * peso);
        int g = (int) (a.getGreen() * (1 - peso) + b.getGreen() * peso);
        int bl = (int) (a.getBlue() * (1 - peso) + b.getBlue() * peso);
        return new Color(r, g, bl);
    }

    /** Coloca a logo como ícone da janela (barra de tarefas). */
    public static void aplicarIcone(JFrame tela) {
        URL caminho = Estilo.class.getResource("/imagens/icone.png");
        if (caminho != null) {
            tela.setIconImage(new ImageIcon(caminho).getImage());
        }
    }
}
