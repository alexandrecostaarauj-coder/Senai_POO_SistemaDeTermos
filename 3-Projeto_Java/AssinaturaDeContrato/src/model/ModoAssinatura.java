package model;

/**
 * Como o termo será assinado.
 * MANUAL: o paciente assina no papel e preenche à mão contato de emergência e
 *         declaração de saúde (esses campos saem em branco no documento).
 * DIGITAL: tudo precisa vir preenchido, pois ninguém vai escrever no papel.
 *
 * @author Alexandre Costa Araujo
 */
public enum ModoAssinatura {
    MANUAL("Manual (recepção)"),
    DIGITAL("Digital");

    private final String descricao;

    ModoAssinatura(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
