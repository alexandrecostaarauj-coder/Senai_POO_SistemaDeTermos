package model;

/**
 * Regras de texto usadas pelos setters das classes do model. Fica no pacote
 * model (sem "public") porque só as entidades usam.
 *
 * Todo método lança IllegalArgumentException com uma mensagem pronta para a
 * tela mostrar num JOptionPane.
 */
final class ValidacaoDeCampos {

    private ValidacaoDeCampos() {
    }

    /**
     * Limpa o texto: tira espaços das pontas e devolve null se ficar vazio.
     * Rejeita ';' e quebra de linha, porque quebrariam o arquivo CSV.
     */
    static String texto(String campo, String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.isEmpty()) {
            return null;
        }
        if (limpo.contains(";") || limpo.contains("\n") || limpo.contains("\r")) {
            throw new IllegalArgumentException(campo + " não pode conter ';' nem quebra de linha.");
        }
        return limpo;
    }

    /** Igual ao texto(), mas não aceita vazio. */
    static String obrigatorio(String campo, String valor) {
        String limpo = texto(campo, valor);
        if (limpo == null) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return limpo;
    }

    /** E-mail opcional; se preenchido, precisa ter o formato nome@dominio.algo */
    static String email(String campo, String valor) {
        String t = texto(campo, valor);
        if (t != null && !t.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException(campo + " inválido: " + t);
        }
        return t;
    }
}
