package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Termo de internação.
 * - Paciente e Médico: agregação (o termo guarda uma cópia do dia da assinatura).
 * - Contatos de emergência e declaração de saúde: composição (só existem no termo).
 * - modoAssinatura decide o que é obrigatório (ver validar()).
 *
 * Aqui ficam as regras do termo: validação, limite de contatos, limite de termos
 * guardados (retenção) e a data sugerida para o procedimento.
 *
 * @author Alexandre Costa Araujo
 */
public class Contrato implements Identificavel {

    public static final int MAXIMO_CONTATOS = 2;
    public static final int MAXIMO_TERMOS_GUARDADOS = 20;

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
        setProcedimento(procedimento);
        this.dataDoProcedimento = dataDoProcedimento;
        this.dataDaAssinatura = dataDaAssinatura;
        this.modoAssinatura = modoAssinatura;
        setContatosDeEmergencia(contatosDeEmergencia);
        this.opcaoDeImagem = opcaoDeImagem;
        this.declaracaoDeSaude = declaracaoDeSaude;
    }

    // ------------------------------------------------------------------ regras do termo

    /**
     * Confere as regras que dependem de mais de um campo. Chamado antes de salvar.
     * Sempre: paciente, médico, data do procedimento e modo de assinatura.
     * DIGITAL: também pelo menos 1 contato, declaração de saúde e opção de imagem.
     * MANUAL: o resto pode ficar vazio (sai em branco para preencher à mão).
     */
    public void validar() {
        if (paciente == null) {
            throw new IllegalArgumentException("Preencha os dados do paciente.");
        }
        if (medico == null) {
            throw new IllegalArgumentException("Selecione o médico responsável.");
        }
        if (dataDoProcedimento == null) {
            throw new IllegalArgumentException("Informe a data do procedimento.");
        }
        if (modoAssinatura == null) {
            throw new IllegalArgumentException("Selecione o modo de assinatura.");
        }
        if (modoAssinatura == ModoAssinatura.DIGITAL) {
            if (contatosDeEmergencia.isEmpty()) {
                throw new IllegalArgumentException("Assinatura digital: informe pelo menos 1 contato de emergência.");
            }
            if (declaracaoDeSaude == null) {
                throw new IllegalArgumentException("Assinatura digital: preencha a declaração de saúde.");
            }
            if (opcaoDeImagem == null) {
                throw new IllegalArgumentException("Assinatura digital: escolha a opção de uso de imagem.");
            }
        }
    }

    /** Regra de retenção: só pode ser apagado o termo cuja cirurgia JÁ PASSOU (ou sem data). */
    public boolean podeSerApagado(LocalDate hoje) {
        return dataDoProcedimento == null || dataDoProcedimento.isBefore(hoje);
    }

    /**
     * Regra de retenção: ficam no máximo MAXIMO_TERMOS_GUARDADOS termos. Enquanto
     * passar disso, escolhe o mais antigo (menor número) que pode ser apagado.
     * Termo de cirurgia de hoje ou futura nunca é escolhido. Devolve os escolhidos.
     */
    public static List<Contrato> escolherExcedentes(List<Contrato> termos, LocalDate hoje) {
        List<Contrato> restantes = new ArrayList<>(termos);
        List<Contrato> escolhidos = new ArrayList<>();
        while (restantes.size() > MAXIMO_TERMOS_GUARDADOS) {
            Contrato maisAntigo = null;
            for (Contrato c : restantes) {
                if (c.podeSerApagado(hoje) && (maisAntigo == null || c.getID() < maisAntigo.getID())) {
                    maisAntigo = c;
                }
            }
            if (maisAntigo == null) {
                break; // todos são de hoje ou futuros: não apaga nenhum
            }
            restantes.remove(maisAntigo);
            escolhidos.add(maisAntigo);
        }
        return escolhidos;
    }

    /**
     * Os termos são preparados na véspera: a data sugerida é o próximo dia útil
     * (pula sábado e domingo; na sexta sugere a segunda).
     */
    public static LocalDate proximoDiaUtil(LocalDate hoje) {
        LocalDate dia = hoje.plusDays(1);
        while (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dia = dia.plusDays(1);
        }
        return dia;
    }

    // ------------------------------------------------------------------ getters e setters

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
        this.procedimento = ValidacaoDeCampos.texto("Procedimento", procedimento);
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

    /** Nunca guarda null (sem contatos = lista vazia) e aceita no máximo 2. */
    public void setContatosDeEmergencia(List<ContatoDeEmergencia> contatosDeEmergencia) {
        if (contatosDeEmergencia == null) {
            this.contatosDeEmergencia = new ArrayList<>();
            return;
        }
        if (contatosDeEmergencia.size() > MAXIMO_CONTATOS) {
            throw new IllegalArgumentException("Informe no máximo " + MAXIMO_CONTATOS + " contatos de emergência.");
        }
        this.contatosDeEmergencia = contatosDeEmergencia;
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
