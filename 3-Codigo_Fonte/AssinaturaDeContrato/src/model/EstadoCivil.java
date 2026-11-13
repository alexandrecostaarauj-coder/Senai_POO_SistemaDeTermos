package model;

/**
 *
 * @author Alexandre Costa Araujo
 */
public enum EstadoCivil {
    
    SOLTEIRO("Solteiro(a)"),
    CASADO("Casado(a)"),
    DIVORCIADO("Divorciado(a)"),
    VIUVO("Viúvo(a)"),
    UNIAOESTAVEL("União Estavel");
    
    private final String descricao;

    EstadoCivil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
