package model;

/**
 * Classe mãe de Paciente e Medico. Regra: toda pessoa precisa ter nome.
 *
 * @author Alexandre Costa Araujo
 */
public class Pessoa {

    private String nome;

    public Pessoa(String nome) {
        setNome(nome);
    }

    public String getNome() {
        return nome;
    }

    /** Nome obrigatório; o texto da mensagem vem de rotuloDoNome() (polimorfismo). */
    public void setNome(String nome) {
        this.nome = ValidacaoDeCampos.obrigatorio(rotuloDoNome(), nome);
    }

    /** Como o campo aparece na mensagem de erro. As filhas sobrescrevem. */
    protected String rotuloDoNome() {
        return "Nome";
    }

    @Override
    public String toString() {
        return "Pessoa: " + "\nNome: " + nome;
    }
}
