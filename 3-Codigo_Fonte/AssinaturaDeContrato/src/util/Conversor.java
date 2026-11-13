package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Converte entre os tipos do Java e o texto guardado no CSV / digitado nas
 * telas. Valor vazio (null) vira "" no arquivo, e "" volta como null.
 */
public class Conversor {

    // STRICT + "uuuu" rejeita datas que não existem, como 31/02/2026.
    private static final DateTimeFormatter FORMATO
            = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Conversor() {
    }

    public static String paraTexto(Object valor) {
        if (valor == null) {
            return "";
        }
        if (valor instanceof LocalDate) {
            return ((LocalDate) valor).format(FORMATO);
        }
        return valor.toString();
    }

    /**
     * "" ou null -> null. Data inexistente ou fora do padrão ->
     * IllegalArgumentException.
     */
    public static LocalDate paraData(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida (use dd/MM/aaaa): " + texto);
        }
    }

    /**
     * Aceita vírgula ou ponto. "" -> 0 (campo não informado).
     */
    public static double paraDouble(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido: " + texto);
        }
    }

    /**
     * "" -> null.
     */
    public static Integer paraInteiro(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inteiro inválido: " + texto);
        }
    }

    /**
     * Texto -> valor do enum, ou null se vazio.
     */
    public static <E extends Enum<E>> E paraEnum(Class<E> tipo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(tipo, texto.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para " + tipo.getSimpleName() + ": " + texto);
        }
    }
}
