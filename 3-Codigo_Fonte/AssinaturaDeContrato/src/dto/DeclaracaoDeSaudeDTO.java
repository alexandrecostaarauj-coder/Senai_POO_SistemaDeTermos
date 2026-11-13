package dto;

/**
 * Respostas da declaração de saúde (marcado = SIM).
 */
public class DeclaracaoDeSaudeDTO {

    private boolean fumante;
    private boolean dependenteQuimico;
    private boolean cardiopata;
    private boolean suspeitaDeGravidez;
    private boolean diabetico;
    private boolean sedentario;
    private boolean hipertenso;

    public DeclaracaoDeSaudeDTO() {
    }

    public boolean isFumante() {
        return fumante;
    }

    public void setFumante(boolean fumante) {
        this.fumante = fumante;
    }

    public boolean isDependenteQuimico() {
        return dependenteQuimico;
    }

    public void setDependenteQuimico(boolean dependenteQuimico) {
        this.dependenteQuimico = dependenteQuimico;
    }

    public boolean isCardiopata() {
        return cardiopata;
    }

    public void setCardiopata(boolean cardiopata) {
        this.cardiopata = cardiopata;
    }

    public boolean isSuspeitaDeGravidez() {
        return suspeitaDeGravidez;
    }

    public void setSuspeitaDeGravidez(boolean suspeitaDeGravidez) {
        this.suspeitaDeGravidez = suspeitaDeGravidez;
    }

    public boolean isDiabetico() {
        return diabetico;
    }

    public void setDiabetico(boolean diabetico) {
        this.diabetico = diabetico;
    }

    public boolean isSedentario() {
        return sedentario;
    }

    public void setSedentario(boolean sedentario) {
        this.sedentario = sedentario;
    }

    public boolean isHipertenso() {
        return hipertenso;
    }

    public void setHipertenso(boolean hipertenso) {
        this.hipertenso = hipertenso;
    }
}
