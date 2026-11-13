package util;

/**
 * Cálculo dos dígitos verificadores do CPF (mesma ideia do CpfUtil do exemplo
 * do professor). Quem decide o que é um CPF válido é o model (Paciente.setCpf);
 * aqui fica só a conta.
 */
public class CpfUtil {

    private CpfUtil() {
    }

    /** Recebe 11 dígitos e confere os dois dígitos verificadores. */
    public static boolean digitosConferem(String d) {
        if (d == null || !d.matches("\\d{11}") || d.matches("(\\d)\\1{10}")) {
            return false;
        }
        return digitoVerificador(d, 9) == d.charAt(9) - '0'
                && digitoVerificador(d, 10) == d.charAt(10) - '0';
    }

    private static int digitoVerificador(String d, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (d.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        if (resto == 10) {
            return 0;
        }
        return resto;
    }
}
