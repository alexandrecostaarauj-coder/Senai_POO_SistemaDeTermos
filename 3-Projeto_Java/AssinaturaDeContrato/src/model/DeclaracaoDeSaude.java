
package model;

/**
 *
 * Declaração de saúde do Anexo do termo. Pertence ao contrato (composição).
 *
 * @author Alexandre Costa Araujo
 */
public class DeclaracaoDeSaude {
    boolean fumante;
    boolean dependenteQuimico;
    boolean cardiopata;
    boolean suspeitaDeGravidez;
    boolean diabetico;
    boolean sedentario;
    boolean hipertenso;

    public DeclaracaoDeSaude(boolean fumante, boolean dependenteQuimico, boolean cardiopata, boolean suspeitaDeGravidez, boolean diabetico, boolean sedentario, boolean hipertenso) {
        this.fumante = fumante;
        this.dependenteQuimico = dependenteQuimico;
        this.cardiopata = cardiopata;
        this.suspeitaDeGravidez = suspeitaDeGravidez;
        this.diabetico = diabetico;
        this.sedentario = sedentario;
        this.hipertenso = hipertenso;
    }

    public void setFumante(boolean fumante) {
        this.fumante = fumante;
    }

    public void setDependenteQuimico(boolean dependenteQuimico) {
        this.dependenteQuimico = dependenteQuimico;
    }

    public void setCardiopata(boolean cardiopata) {
        this.cardiopata = cardiopata;
    }

    public void setSuspeitaDeGravidez(boolean suspeitaDeGravidez) {
        this.suspeitaDeGravidez = suspeitaDeGravidez;
    }

    public void setDiabetico(boolean diabetico) {
        this.diabetico = diabetico;
    }

    public void setSedentario(boolean sedentario) {
        this.sedentario = sedentario;
    }

    public void setHipertenso(boolean hipertenso) {
        this.hipertenso = hipertenso;
    }

    public boolean isFumante() {
        return fumante;
    }

    public boolean isDependenteQuimico() {
        return dependenteQuimico;
    }

    public boolean isCardiopata() {
        return cardiopata;
    }

    public boolean isSuspeitaDeGravidez() {
        return suspeitaDeGravidez;
    }

    public boolean isDiabetico() {
        return diabetico;
    }

    public boolean isSedentario() {
        return sedentario;
    }

    public boolean isHipertenso() {
        return hipertenso;
    }
    
    
}
