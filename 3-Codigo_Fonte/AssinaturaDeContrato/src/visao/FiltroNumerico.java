package visao;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;

/**
 * "Porteiro" do campo de texto: só deixa entrar dígitos e respeita um tamanho
 * máximo. Uso: FiltroNumerico.aplicar(jTextField_Crm, 8);
 */
public class FiltroNumerico extends DocumentFilter {

    private final int maximo;

    private FiltroNumerico(int maximo) {
        this.maximo = maximo;
    }

    public static void aplicar(JTextField campo, int maximoDeDigitos) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroNumerico(maximoDeDigitos));
    }

    @Override
    public void insertString(FilterBypass fb, int posicao, String texto, AttributeSet atributos) throws BadLocationException {
        replace(fb, posicao, 0, texto, atributos);
    }

    @Override
    public void replace(FilterBypass fb, int posicao, int tamanho, String texto, AttributeSet atributos) throws BadLocationException {
        if (texto == null) {
            return;
        }
        String soDigitos = texto.replaceAll("\\D", ""); // tira tudo que não é número (inclusive ao colar)
        int espacoLivre = maximo - (fb.getDocument().getLength() - tamanho);
        if (soDigitos.length() > espacoLivre) {
            soDigitos = soDigitos.substring(0, Math.max(0, espacoLivre));
        }
        super.replace(fb, posicao, tamanho, soDigitos, atributos);
    }
}
