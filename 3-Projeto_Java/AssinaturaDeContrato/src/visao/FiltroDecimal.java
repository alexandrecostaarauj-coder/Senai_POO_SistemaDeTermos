package visao;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Número com vírgula: aceita só dígitos e UMA vírgula (ponto vira vírgula),
 * limitando a parte inteira e as casas decimais.
 * FiltroDecimal.aplicar(jTextField_Peso, 3, 2); -> 72,5 / 110,25 (no máximo
 * 999,99)
 */
public class FiltroDecimal extends DocumentFilter {

    private final int inteiros;
    private final int decimais;

    private FiltroDecimal(int inteiros, int decimais) {
        this.inteiros = inteiros;
        this.decimais = decimais;
    }

    public static void aplicar(JTextField campo, int digitosInteiros, int casasDecimais) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroDecimal(digitosInteiros, casasDecimais));
    }

    @Override
    public void insertString(FilterBypass fb, int posicao, String texto, AttributeSet atributos) throws BadLocationException {
        replace(fb, posicao, 0, texto, atributos);
    }

    @Override
    public void replace(FilterBypass fb, int posicao, int tamanho, String texto, AttributeSet atributos) throws BadLocationException {
        if (texto == null) {
            texto = "";
        }
        String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String novo = atual.substring(0, posicao) + texto.replace('.', ',') + atual.substring(posicao + tamanho);
        if (novo.matches("\\d{0," + inteiros + "}(,\\d{0," + decimais + "})?")) {
            super.replace(fb, posicao, tamanho, texto.replace('.', ','), atributos);
        }
        // se não bate com o formato, simplesmente ignora a tecla
    }
}
