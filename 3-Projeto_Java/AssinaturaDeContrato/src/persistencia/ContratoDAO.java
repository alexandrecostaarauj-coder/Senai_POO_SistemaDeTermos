package persistencia;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.ContatoDeEmergencia;
import model.Contrato;
import model.DeclaracaoDeSaude;
import model.Endereco;
import model.EstadoCivil;
import model.Medico;
import model.ModoAssinatura;
import model.OpcaoDeImagem;
import model.Paciente;
import util.Conversor;
import util.Validador;

/**
 * Guarda os termos em contratos.csv, UMA linha por termo, com uma CÓPIA dos dados
 * do paciente e do médico do dia da assinatura (se o cadastro do médico mudar ou for
 * excluído depois, o termo antigo continua igual ao que foi assinado).
 *
 * Regra de retenção: ficam no máximo MAXIMO_CONTRATOS termos. Ao passar disso, apaga
 * o mais antigo cuja cirurgia JÁ PASSOU. Termo de cirurgia de hoje ou futura nunca é apagado.
 *
 * Colunas (41):
 *  0 ID | 1 dataProcedimento | 2 dataAssinatura | 3 procedimento | 4 modoAssinatura | 5 opcaoImagem
 *  6-8   médico: nome, crm, rqe
 *  9-25  paciente: nome, estadoCivil, profissao, cpf, rg, nascimento, email, telefone, altura, peso,
 *        rua, numero, complemento, setor, cep, cidade, uf
 *  26-29 contato 1: nome, parentesco, telefone, email | 30-33 contato 2
 *  34-40 declaração: fumante, dependente, cardiopata, gravidez, diabetico, sedentario, hipertenso (S/N)
 */
public class ContratoDAO extends ArquivoCsvDAO<Contrato> {

    public static final int MAXIMO_CONTRATOS = 20;
    private static final int COLUNAS = 41;

    private List<Contrato> ultimosRemovidos = new ArrayList<>();

    public ContratoDAO() {
        this(".\\src\\dados\\contratos.csv");
    }

    public ContratoDAO(String caminho) {
        super(caminho);
    }

    // ------------------------------------------------------------------ regras extras

    /** Salva e, se passar do limite, apaga os termos antigos (cirurgia já realizada). */
    @Override
    public int salvar(Contrato contrato) throws IOException {
        int id = super.salvar(contrato);
        ultimosRemovidos = removerExcedentes(LocalDate.now());
        return id;
    }

    /** Termos apagados automaticamente no último salvar() (a tela apaga os arquivos Word/PDF deles). */
    public List<Contrato> getUltimosRemovidos() {
        return ultimosRemovidos;
    }

    /**
     * Apaga os termos mais antigos enquanto houver mais que o limite.
     * Só apaga termo com data do procedimento ANTERIOR a 'hoje'. Devolve os que foram apagados.
     */
    public List<Contrato> removerExcedentes(LocalDate hoje) throws IOException {
        List<Contrato> lista = listar();
        List<Contrato> removidos = new ArrayList<>();
        while (lista.size() > MAXIMO_CONTRATOS) {
            Contrato maisAntigo = null;
            for (Contrato c : lista) {
                boolean jaPassou = c.getDataDoProcedimento() == null || c.getDataDoProcedimento().isBefore(hoje);
                if (jaPassou && (maisAntigo == null || c.getID() < maisAntigo.getID())) {
                    maisAntigo = c;
                }
            }
            if (maisAntigo == null) {
                break; // todos são de hoje ou futuros: não apaga nenhum
            }
            lista.remove(maisAntigo);
            removidos.add(maisAntigo);
        }
        if (!removidos.isEmpty()) {
            reescrever(lista);
        }
        return removidos;
    }

    /** Último termo do paciente com este CPF (para reaproveitar os dados), ou null. */
    public Contrato buscarUltimoPorCpf(String cpf) throws IOException {
        String procurado = Validador.cpf(cpf);
        if (procurado == null) {
            return null;
        }
        Contrato encontrado = null;
        for (Contrato c : listar()) {
            if (procurado.equals(c.getPaciente().getCpf()) && (encontrado == null || c.getID() > encontrado.getID())) {
                encontrado = c;
            }
        }
        return encontrado;
    }

    // ------------------------------------------------------------------ objeto -> linha

    @Override
    protected int totalDeColunas() {
        return COLUNAS;
    }

    @Override
    protected String[] paraColunas(Contrato c) {
        Validador.contrato(c); // lança IllegalArgumentException se algo estiver errado

        String[] col = new String[COLUNAS];
        java.util.Arrays.fill(col, "");
        col[0] = String.valueOf(c.getID());
        col[1] = Conversor.paraTexto(c.getDataDoProcedimento());
        col[2] = Conversor.paraTexto(c.getDataDaAssinatura());
        col[3] = texto(c.getProcedimento());
        col[4] = Conversor.paraTexto(c.getModoAssinatura());
        col[5] = Conversor.paraTexto(c.getOpcaoDeImagem());

        Medico m = c.getMedico();
        col[6] = texto(m.getNome());
        col[7] = Conversor.paraTexto(m.getCrm());
        col[8] = Conversor.paraTexto(m.getRqe());

        Paciente p = c.getPaciente();
        col[9] = texto(p.getNome());
        col[10] = Conversor.paraTexto(p.getEstadoCivil());
        col[11] = texto(p.getProfissao());
        col[12] = Conversor.paraTexto(Validador.cpf(p.getCpf()));
        col[13] = texto(p.getRg());
        col[14] = Conversor.paraTexto(p.getDataDeNascimento());
        col[15] = texto(p.getEmail());
        col[16] = texto(p.getTelefone());
        col[17] = numero(p.getAltura());
        col[18] = numero(p.getPeso());
        Endereco e = p.getEndereco();
        if (e != null) {
            col[19] = texto(e.getRua());
            col[20] = texto(e.getNumero());
            col[21] = texto(e.getComplemento());
            col[22] = texto(e.getSetor());
            col[23] = Conversor.paraTexto(Validador.cep(e.getCep()));
            col[24] = texto(e.getCidade());
            col[25] = Conversor.paraTexto(Validador.uf(e.getEstado()));
        }

        List<ContatoDeEmergencia> contatos = c.getContatosDeEmergencia();
        for (int i = 0; i < contatos.size(); i++) {
            int inicio = 26 + i * 4;
            ContatoDeEmergencia contato = contatos.get(i);
            col[inicio] = texto(contato.getNome());
            col[inicio + 1] = texto(contato.getParentesco());
            col[inicio + 2] = texto(contato.getTelefone());
            col[inicio + 3] = texto(contato.getEmail());
        }

        DeclaracaoDeSaude d = c.getDeclaracaoDeSaude();
        if (d != null) {
            boolean[] respostas = {d.isFumante(), d.isDependenteQuimico(), d.isCardiopata(),
                d.isSuspeitaDeGravidez(), d.isDiabetico(), d.isSedentario(), d.isHipertenso()};
            for (int i = 0; i < respostas.length; i++) {
                if (respostas[i]) {
                    col[34 + i] = "S";
                } else {
                    col[34 + i] = "N";
                }
            }
        }
        return col;
    }

    // ------------------------------------------------------------------ linha -> objeto

    @Override
    protected Contrato deColunas(String[] c) {
        Medico medico = new Medico(0, Conversor.paraInteiro(c[7]), Conversor.paraInteiro(c[8]), c[6]);

        Endereco endereco = null;
        if (!(c[19] + c[20] + c[21] + c[22] + c[23] + c[24] + c[25]).isEmpty()) {
            endereco = new Endereco(nulo(c[19]), nulo(c[20]), nulo(c[21]), nulo(c[22]), nulo(c[23]), nulo(c[24]), nulo(c[25]));
        }
        Paciente paciente = new Paciente(0, Conversor.paraEnum(EstadoCivil.class, c[10]), nulo(c[11]), nulo(c[12]),
                nulo(c[13]), Conversor.paraData(c[14]), endereco, nulo(c[15]), nulo(c[16]),
                Conversor.paraDouble(c[17]), Conversor.paraDouble(c[18]), c[9]);

        List<ContatoDeEmergencia> contatos = new ArrayList<>();
        for (int i = 0; i < Contrato.MAXIMO_CONTATOS; i++) {
            int inicio = 26 + i * 4;
            if (!c[inicio].isEmpty()) {
                contatos.add(new ContatoDeEmergencia(c[inicio], nulo(c[inicio + 1]), c[inicio + 2], nulo(c[inicio + 3])));
            }
        }

        DeclaracaoDeSaude declaracao = null;
        if (!c[34].isEmpty()) {
            declaracao = new DeclaracaoDeSaude(simNao(c[34]), simNao(c[35]), simNao(c[36]),
                    simNao(c[37]), simNao(c[38]), simNao(c[39]), simNao(c[40]));
        }

        return new Contrato(Integer.parseInt(c[0]), paciente, medico, nulo(c[3]),
                Conversor.paraData(c[1]), Conversor.paraData(c[2]),
                Conversor.paraEnum(ModoAssinatura.class, c[4]), contatos,
                Conversor.paraEnum(OpcaoDeImagem.class, c[5]), declaracao);
    }

    // ------------------------------------------------------------------ ajudantes

    private String texto(String valor) {
        return Conversor.paraTexto(Validador.texto("Campo", valor));
    }

    private String numero(double valor) {
        if (valor == 0) {
            return "";
        }
        return String.valueOf(valor);
    }

    private String nulo(String s) {
        if (s.isEmpty()) {
            return null;
        }
        return s;
    }

    private boolean simNao(String valor) {
        if (valor.equals("S")) {
            return true;
        }
        if (valor.equals("N")) {
            return false;
        }
        throw new IllegalArgumentException("Resposta da declaração deve ser S ou N: " + valor);
    }
}
