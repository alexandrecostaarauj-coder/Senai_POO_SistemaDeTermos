package dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Dados do termo de internação entre as telas e o ContratoController.
 * Só transporta: datas em dd/MM/aaaa, modo de assinatura e opção de imagem pelo
 * nome do enum (ex.: "DIGITAL"). declaracaoDeSaude fica null no modo MANUAL.
 * As regras ficam em model.Contrato.
 */
public class ContratoDTO {

    private int id;
    private PacienteDTO paciente = new PacienteDTO();
    private MedicoDTO medico;
    private String procedimento;
    private String dataDoProcedimento;
    private String dataDaAssinatura;
    private String modoAssinatura;
    private String opcaoDeImagem;
    private List<ContatoDeEmergenciaDTO> contatosDeEmergencia = new ArrayList<>();
    private DeclaracaoDeSaudeDTO declaracaoDeSaude;

    public ContratoDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PacienteDTO getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteDTO paciente) {
        this.paciente = paciente;
    }

    public MedicoDTO getMedico() {
        return medico;
    }

    public void setMedico(MedicoDTO medico) {
        this.medico = medico;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public void setProcedimento(String procedimento) {
        this.procedimento = procedimento;
    }

    public String getDataDoProcedimento() {
        return dataDoProcedimento;
    }

    public void setDataDoProcedimento(String dataDoProcedimento) {
        this.dataDoProcedimento = dataDoProcedimento;
    }

    public String getDataDaAssinatura() {
        return dataDaAssinatura;
    }

    public void setDataDaAssinatura(String dataDaAssinatura) {
        this.dataDaAssinatura = dataDaAssinatura;
    }

    public String getModoAssinatura() {
        return modoAssinatura;
    }

    public void setModoAssinatura(String modoAssinatura) {
        this.modoAssinatura = modoAssinatura;
    }

    public String getOpcaoDeImagem() {
        return opcaoDeImagem;
    }

    public void setOpcaoDeImagem(String opcaoDeImagem) {
        this.opcaoDeImagem = opcaoDeImagem;
    }

    public List<ContatoDeEmergenciaDTO> getContatosDeEmergencia() {
        return contatosDeEmergencia;
    }

    public void setContatosDeEmergencia(List<ContatoDeEmergenciaDTO> contatosDeEmergencia) {
        this.contatosDeEmergencia = contatosDeEmergencia;
    }

    public DeclaracaoDeSaudeDTO getDeclaracaoDeSaude() {
        return declaracaoDeSaude;
    }

    public void setDeclaracaoDeSaude(DeclaracaoDeSaudeDTO declaracaoDeSaude) {
        this.declaracaoDeSaude = declaracaoDeSaude;
    }
}
