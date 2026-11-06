package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Termo de internação.
 * - Paciente e Médico: agregação (existem sozinhos e são reaproveitados).
 * - Contatos de emergência e declaração de saúde: composição (só existem no contrato).
 * - modoAssinatura decide o que é obrigatório (ver Validador.contrato).
 *
 * @author Alexandre Costa Araujo
 */
public class Contrato implements Identificavel {

    public static final int MAXIMO_CONTATOS = 2;

    private int ID;
    private Paciente paciente;
    private Medico medico;
    private String procedimento;
    private LocalDate dataDoProcedimento;
    private LocalDate dataDaAssinatura;
    private ModoAssinatura modoAssinatura;
    private List<ContatoDeEmergencia> contatosDeEmergencia;
    private OpcaoDeImagem opcaoDeImagem;
    private DeclaracaoDeSaude declaracaoDeSaude;

    public Contrato(int ID, Paciente paciente, Medico medico, String procedimento, LocalDate dataDoProcedimento,
            LocalDate dataDaAssinatura, ModoAssinatura modoAssinatura, List<ContatoDeEmergencia> contatosDeEmergencia,
            OpcaoDeImagem opcaoDeImagem, DeclaracaoDeSaude declaracaoDeSaude) {
        this.ID = ID;
        this.paciente = paciente;
        this.medico = medico;
        this.procedimento = procedimento;
        this.dataDoProcedimento = dataDoProcedimento;
        this.dataDaAssinatura = dataDaAssinatura;
        this.modoAssinatura = modoAssinatura;
        setContatosDeEmergencia(contatosDeEmergencia);
        this.opcaoDeImagem = opcaoDeImagem;
        this.declaracaoDeSaude = declaracaoDeSaude;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public void setProcedimento(String procedimento) {
        this.procedimento = procedimento;
    }

    public void setDataDoProcedimento(LocalDate dataDoProcedimento) {
        this.dataDoProcedimento = dataDoProcedimento;
    }

    public void setDataDaAssinatura(LocalDate dataDaAssinatura) {
        this.dataDaAssinatura = dataDaAssinatura;
    }

    public void setModoAssinatura(ModoAssinatura modoAssinatura) {
        this.modoAssinatura = modoAssinatura;
    }

    /** Nunca guarda null: sem contatos = lista vazia. */
    public void setContatosDeEmergencia(List<ContatoDeEmergencia> contatosDeEmergencia) {
        if (contatosDeEmergencia == null) {
            this.contatosDeEmergencia = new ArrayList<>();
        } else {
            this.contatosDeEmergencia = contatosDeEmergencia;
        }
    }

    public void setOpcaoDeImagem(OpcaoDeImagem opcaoDeImagem) {
        this.opcaoDeImagem = opcaoDeImagem;
    }

    public void setDeclaracaoDeSaude(DeclaracaoDeSaude declaracaoDeSaude) {
        this.declaracaoDeSaude = declaracaoDeSaude;
    }

    @Override
    public int getID() {
        return ID;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public LocalDate getDataDoProcedimento() {
        return dataDoProcedimento;
    }

    public LocalDate getDataDaAssinatura() {
        return dataDaAssinatura;
    }

    public ModoAssinatura getModoAssinatura() {
        return modoAssinatura;
    }

    public List<ContatoDeEmergencia> getContatosDeEmergencia() {
        return contatosDeEmergencia;
    }

    public OpcaoDeImagem getOpcaoDeImagem() {
        return opcaoDeImagem;
    }

    public DeclaracaoDeSaude getDeclaracaoDeSaude() {
        return declaracaoDeSaude;
    }
}
