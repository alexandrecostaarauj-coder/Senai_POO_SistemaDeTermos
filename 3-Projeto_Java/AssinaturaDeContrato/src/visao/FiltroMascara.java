package visao;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Formata o campo ENQUANTO a pessoa digita. Cada '#' da máscara é um dígito; o
 * resto (ponto, traço, barra, parênteses, vírgula) entra sozinho.
 *
 * FiltroMascara.aplicar(jTextField_Cpf, "###.###.###-##"); digita 52998224725
 * -> 529.982.247-25 FiltroMascara.aplicar(jTextField_Data, "##/##/####");
 * digita 20051990 -> 20/05/1990 FiltroMascara.aplicar(jTextField_Altura,
 * "#,##"); digita 175 -> 1,75 FiltroMascara.aplicar(jTextField_Tel, "(##)
 * ####-####", "(##) #####-####"); fixo ou celular
 *
 * Com mais de uma máscara, usa a primeira em que os dígitos cabem. Campo vazio
 * continua "" (diferente do JFormattedTextField, que devolve "___.___.___-__").
 */
public class FiltroMascara extends DocumentFilter {

    private final String[] mascaras;
    private final int maximoDeDigitos;

    private FiltroMascara(String[] mascaras) {
        this.mascaras = mascaras;
        int maior = 0;
        for (String m : mascaras) {
            maior = Math.max(maior, capacidade(m));
        }
        this.maximoDeDigitos = maior;
    }

    public static void aplicar(JTextField campo, String... mascaras) {

        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroMascara(mascaras));
    }

    @Override

    public void insertString(FilterBypass fb, int posicao, String texto, AttributeSet atributos) throws BadLocationException {
        replace(fb, posicao, 0, texto, atributos);
    }

    @Override
    public void remove(FilterBypass fb, int posicao, int tamanho) throws BadLocationException {

        String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String depois = atual.substring(0, posicao) + atual.substring(posicao + tamanho);
        // apagou só um separador ("/", ".", "-")? então apaga também o dígito antes dele,
        // senão o Backspace parece "travado"
        if (depois.replaceAll("\\D", "").equals(atual.replaceAll("\\D", "")) && posicao > 0) {
            remove(fb, posicao - 1, tamanho + 1);
            return;
        }
        replace(fb, posicao, tamanho, "", null);
    }

    @Override
    public void replace(FilterBypass fb, int posicao, int tamanho, String texto, AttributeSet atributos) throws BadLocationException {
        String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
        if (texto == null) {
            texto = "";
        }
        String novo = atual.substring(0, posicao) + texto + atual.substring(posicao + tamanho);
        String digitos = novo.replaceAll("\\D", "");
        if (digitos.length() > maximoDeDigitos) {
            digitos = digitos.substring(0, maximoDeDigitos);
        }
        String formatado = formatar(digitos);
        fb.replace(0, fb.getDocument().getLength(), formatado, atributos);
    }

    private String formatar(String digitos) {
        if (digitos.isEmpty()) {
            return "";
        }
        String mascara = mascaras[mascaras.length - 1];
        for (String m : mascaras) {
            if (digitos.length() <= capacidade(m)) {
                mascara = m;
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (char c : mascara.toCharArray()) {
            if (i >= digitos.length()) {
                break; // para no último dígito digitado (não deixa "/" sobrando no fim)
            }
            if (c == '#') {
                sb.append(digitos.charAt(i));
                i++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static int capacidade(String mascara) {
        int n = 0;
        for (char c : mascara.toCharArray()) {
            if (c == '#') {
                n++;
            }
        }
        return n;
    }
}
