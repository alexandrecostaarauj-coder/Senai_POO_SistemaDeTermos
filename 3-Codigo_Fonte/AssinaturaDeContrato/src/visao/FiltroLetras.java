package visao;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Só deixa entrar letras (com acento) e espaço, com tamanho máximo.
 * Opcionalmente transforma em MAIÚSCULAS (ex.: UF).
 * FiltroLetras.aplicar(jTextField_Uf, 2, true); -> "go" vira "GO", números são
 * ignorados FiltroLetras.aplicar(jTextField_Cidade, 60, false);
 */
public class FiltroLetras extends DocumentFilter {

    private final int maximo;
    private final boolean maiusculas;

    private FiltroLetras(int maximo, boolean maiusculas) {
        this.maximo = maximo;
        this.maiusculas = maiusculas;
    }

    public static void aplicar(JTextField campo, int maximo, boolean maiusculas) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroLetras(maximo, maiusculas));
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
        String soLetras = texto.replaceAll("[^\\p{L} ]", ""); // \p{L} = qualquer letra, inclusive acentuada
        if (maiusculas) {
            soLetras = soLetras.toUpperCase();
        }
        int espacoLivre = maximo - (fb.getDocument().getLength() - tamanho);
        if (soLetras.length() > espacoLivre) {
            soLetras = soLetras.substring(0, Math.max(0, espacoLivre));
        }
        super.replace(fb, posicao, tamanho, soLetras, atributos);
    }
}
