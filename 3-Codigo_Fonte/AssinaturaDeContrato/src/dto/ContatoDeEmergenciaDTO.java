package dto;

/**
 * Dados de um contato de emergência (só transporte).
 */
public class ContatoDeEmergenciaDTO {

    private String nome;
    private String parentesco;
    private String telefone;
    private String email;

    public ContatoDeEmergenciaDTO() {
    }

    public ContatoDeEmergenciaDTO(String nome, String parentesco, String telefone, String email) {
        this.nome = nome;
        this.parentesco = parentesco;
        this.telefone = telefone;
        this.email = email;
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
}
