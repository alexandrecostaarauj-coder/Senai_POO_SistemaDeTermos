
package model;

/**
 *
 * @author user
 */
public enum OpcaoDeImagem {
    ANONIMA("Anónima"),
    IDENTIFICADA("Identificada"),
    NAO_AUTORIZA("Não Autoriza");

    private final String descricao;

    OpcaoDeImagem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}