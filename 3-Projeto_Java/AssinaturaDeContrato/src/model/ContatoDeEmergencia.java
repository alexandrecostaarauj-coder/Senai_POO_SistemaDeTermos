package model;

/**
 * Contato de emergência do paciente. Pertence ao contrato (composição):
 * só existe dentro dele. O contrato aceita no máximo 2.
 *
 * @author Alexandre Costa Araujo
 */
public class ContatoDeEmergencia {

    String nome;
    String parentesco;
    String telefone;
    String email;

    public ContatoDeEmergencia(String nome, String parentesco, String telefone) {
        this(nome, parentesco, telefone, null);
    }

    public ContatoDeEmergencia(String nome, String parentesco, String telefone, String email) {
        this.email = email;
        this.nome = nome;
        this.parentesco = parentesco;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return nome + " (" + parentesco + ") - " + telefone;
    }
}
