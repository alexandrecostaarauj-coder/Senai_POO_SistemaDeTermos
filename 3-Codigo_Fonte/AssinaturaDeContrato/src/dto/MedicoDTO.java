package dto;

/**
 * Dados do médico entre a tela e o controller. Só transporta: os números
 * vêm como texto, do jeito que foram digitados. As regras ficam em model.Medico.
 */
public class MedicoDTO {

    private int id;
    private String nome;
    private String crm;
    private String rqe;

    public MedicoDTO() {
    }

    public MedicoDTO(int id, String nome, String crm, String rqe) {
        this.id = id;
        this.nome = nome;
        this.crm = crm;
        this.rqe = rqe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getRqe() {
        return rqe;
    }

    public void setRqe(String rqe) {
        this.rqe = rqe;
    }
}
