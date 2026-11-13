package model;

/**
 * Contato de emergência do paciente. Pertence ao contrato (composição):
 * só existe dentro dele. Regras: nome e telefone obrigatórios, e-mail válido
 * se preenchido. O contrato aceita no máximo 2.
 *
 * @author Alexandre Costa Araujo
 */
public class ContatoDeEmergencia {

    private String nome;
    private String parentesco;
    private String telefone;
    private String email;

    public ContatoDeEmergencia(String nome, String parentesco, String telefone) {
        this(nome, parentesco, telefone, null);
    }

    public ContatoDeEmergencia(String nome, String parentesco, String telefone, String email) {
        setNome(nome);
        setParentesco(parentesco);
        setTelefone(telefone);
        setEmail(email);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = ValidacaoDeCampos.obrigatorio("Nome do contato de emergência", nome);
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = ValidacaoDeCampos.texto("Parentesco do contato", parentesco);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = ValidacaoDeCampos.obrigatorio("Telefone do contato de emergência", telefone);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = ValidacaoDeCampos.email("E-mail do contato", email);
    }

    @Override
    public String toString() {
        return nome + " (" + parentesco + ") - " + telefone;
    }
}
