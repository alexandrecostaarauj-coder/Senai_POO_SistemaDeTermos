package controller;

import documento.GeradorPdf;
import documento.GeradorWord;
import documento.PastaDeTermos;
import dto.ContatoDeEmergenciaDTO;
import dto.ContratoDTO;
import dto.DeclaracaoDeSaudeDTO;
import dto.EnderecoDTO;
import dto.MedicoDTO;
import dto.PacienteDTO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import persistencia.ContratoDAO;
import util.Conversor;

/**
 * Liga as telas de termo ao arquivo e aos geradores de Word/PDF. As telas só
 * conhecem ContratoDTO; aqui ele vira model.Contrato (onde ficam as regras) e
 * volta a ser DTO para mostrar na tela.
 */
public class ContratoController {

    private static final Path MODELO_DO_TERMO = Paths.get("modelos", "termo_internacao.docx");

    private final ContratoDAO contratoDAO;
    private final PastaDeTermos pasta;
    private List<Integer> ultimosRemovidos = new ArrayList<>();

    public ContratoController() {
        this(new ContratoDAO(), new PastaDeTermos());
    }

    /** Usado nos testes, para gravar em pastas temporárias. */
    public ContratoController(ContratoDAO contratoDAO, PastaDeTermos pasta) {
        this.contratoDAO = contratoDAO;
        this.pasta = pasta;
    }

    // ------------------------------------------------------------------ CRUD

    /**
     * Salva um termo novo e devolve o número (ID). Se passar do limite de termos
     * guardados, apaga os mais antigos que o model permitir (e os arquivos deles).
     */
    public int cadastrar(ContratoDTO dto) throws IOException {
        Contrato contrato = paraModel(dto);
        contrato.setID(0);
        contrato.validar();
        int id = contratoDAO.salvar(contrato);
        dto.setId(id);

        ultimosRemovidos = new ArrayList<>();
        for (Contrato removido : contratoDAO.removerExcedentes(LocalDate.now())) {
            ultimosRemovidos.add(removido.getID());
            pasta.apagar(removido.getID());
        }
        return id;
    }

    public boolean atualizar(ContratoDTO dto) throws IOException {
        Contrato contrato = paraModel(dto);
        contrato.validar();
        return contratoDAO.atualizar(contrato);
    }

    /** Exclui o termo e também os arquivos Word/PDF dele. */
    public boolean excluir(int id) throws IOException {
        boolean excluiu = contratoDAO.excluir(id);
        pasta.apagar(id);
        return excluiu;
    }

    public List<ContratoDTO> listar() throws IOException {
        List<ContratoDTO> lista = new ArrayList<>();
        for (Contrato c : contratoDAO.listar()) {
            lista.add(paraDTO(c));
        }
        return lista;
    }

    public ContratoDTO buscarPorId(int id) throws IOException {
        Contrato c = contratoDAO.buscarPorId(id);
        if (c == null) {
            return null;
        }
        return paraDTO(c);
    }

    /** Último termo do paciente com este CPF, ou null. CPF inválido -> IllegalArgumentException. */
    public ContratoDTO buscarUltimoPorCpf(String cpf) throws IOException {
        Contrato c = contratoDAO.buscarUltimoPorCpf(Paciente.normalizarCpf(cpf));
        if (c == null) {
            return null;
        }
        return paraDTO(c);
    }

    /** Números dos termos apagados automaticamente no último cadastrar(). */
    public List<Integer> getUltimosRemovidos() {
        return ultimosRemovidos;
    }

    public int getMaximoDeTermos() {
        return Contrato.MAXIMO_TERMOS_GUARDADOS;
    }

    /** Data sugerida para o procedimento e a assinatura (próximo dia útil). */
    public String sugerirData() {
        return Conversor.paraTexto(Contrato.proximoDiaUtil(LocalDate.now()));
    }

    // ------------------------------------------------------------------ documentos

    /** Gera o Word do termo salvo e devolve o arquivo. */
    public File gerarWord(int id) throws IOException {
        return new GeradorWord(MODELO_DO_TERMO, pasta).exportar(buscarModel(id));
    }

    /** Gera o Word e converte para PDF. Devolve o PDF. */
    public File gerarDocumentos(int id) throws IOException {
        GeradorWord word = new GeradorWord(MODELO_DO_TERMO, pasta);
        return new GeradorPdf(word, pasta).exportar(buscarModel(id));
    }

    /** Onde fica (ou ficaria) o Word deste termo. */
    public File arquivoWord(int id) throws IOException {
        return pasta.arquivo(buscarModel(id), ".docx");
    }

    private Contrato buscarModel(int id) throws IOException {
        Contrato c = contratoDAO.buscarPorId(id);
        if (c == null) {
            throw new IOException("Termo nº " + id + " não encontrado.");
        }
        return c;
    }

    // ------------------------------------------------------------------ opções dos combos

    /** Nomes dos valores do enum, na mesma ordem dos combos da tela. */
    public static String[] estadosCivis() {
        return nomes(EstadoCivil.values());
    }

    public static String[] modosDeAssinatura() {
        return nomes(ModoAssinatura.values());
    }

    public static String[] opcoesDeImagem() {
        return nomes(OpcaoDeImagem.values());
    }

    /** "DIGITAL" -> "Digital" (texto para mostrar na tabela). */
    public static String descricaoDoModo(String nome) {
        ModoAssinatura modo = Conversor.paraEnum(ModoAssinatura.class, nome);
        if (modo == null) {
            return "";
        }
        return modo.getDescricao();
    }

    private static String[] nomes(Enum<?>[] valores) {
        String[] nomes = new String[valores.length];
        for (int i = 0; i < valores.length; i++) {
            nomes[i] = valores[i].name();
        }
        return nomes;
    }

    // ------------------------------------------------------------------ DTO -> model

    /** Monta o model a partir do DTO. Os setters do model validam cada campo. */
    static Contrato paraModel(ContratoDTO dto) {
        Paciente paciente = paraModel(dto.getPaciente());

        Medico medico = null;
        if (dto.getMedico() != null) {
            medico = MedicoController.paraModel(dto.getMedico());
            medico.setID(0); // o termo guarda uma cópia do médico
        }

        List<ContatoDeEmergencia> contatos = new ArrayList<>();
        for (ContatoDeEmergenciaDTO c : dto.getContatosDeEmergencia()) {
            if (!vazio(c.getNome(), c.getParentesco(), c.getTelefone(), c.getEmail())) {
                contatos.add(new ContatoDeEmergencia(c.getNome(), c.getParentesco(), c.getTelefone(), c.getEmail()));
            }
        }

        DeclaracaoDeSaude declaracao = null;
        DeclaracaoDeSaudeDTO d = dto.getDeclaracaoDeSaude();
        if (d != null) {
            declaracao = new DeclaracaoDeSaude(d.isFumante(), d.isDependenteQuimico(), d.isCardiopata(),
                    d.isSuspeitaDeGravidez(), d.isDiabetico(), d.isSedentario(), d.isHipertenso());
        }

        return new Contrato(dto.getId(), paciente, medico, dto.getProcedimento(),
                Conversor.paraData(dto.getDataDoProcedimento()), Conversor.paraData(dto.getDataDaAssinatura()),
                Conversor.paraEnum(ModoAssinatura.class, dto.getModoAssinatura()), contatos,
                Conversor.paraEnum(OpcaoDeImagem.class, dto.getOpcaoDeImagem()), declaracao);
    }

    private static Paciente paraModel(PacienteDTO p) {
        if (p == null) {
            return null;
        }
        Endereco endereco = null;
        EnderecoDTO e = p.getEndereco();
        if (e != null && !vazio(e.getRua(), e.getNumero(), e.getComplemento(), e.getSetor(), e.getCep(), e.getCidade(), e.getEstado())) {
            endereco = new Endereco(e.getRua(), e.getNumero(), e.getComplemento(), e.getSetor(), e.getCep(), e.getCidade(), e.getEstado());
        }
        return new Paciente(0, Conversor.paraEnum(EstadoCivil.class, p.getEstadoCivil()), p.getProfissao(), p.getCpf(),
                p.getRg(), Conversor.paraData(p.getDataDeNascimento()), endereco, p.getEmail(), p.getTelefone(),
                Conversor.paraDouble(p.getAltura()), Conversor.paraDouble(p.getPeso()), p.getNome());
    }

    /** true se todos os textos estiverem vazios (campo não preenchido na tela). */
    private static boolean vazio(String... textos) {
        for (String t : textos) {
            if (t != null && !t.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------ model -> DTO

    static ContratoDTO paraDTO(Contrato c) {
        ContratoDTO dto = new ContratoDTO();
        dto.setId(c.getID());
        dto.setPaciente(paraDTO(c.getPaciente()));
        dto.setMedico(MedicoController.paraDTO(c.getMedico()));
        dto.setProcedimento(c.getProcedimento());
        dto.setDataDoProcedimento(Conversor.paraTexto(c.getDataDoProcedimento()));
        dto.setDataDaAssinatura(Conversor.paraTexto(c.getDataDaAssinatura()));
        dto.setModoAssinatura(nome(c.getModoAssinatura()));
        dto.setOpcaoDeImagem(nome(c.getOpcaoDeImagem()));
        for (ContatoDeEmergencia contato : c.getContatosDeEmergencia()) {
            dto.getContatosDeEmergencia().add(new ContatoDeEmergenciaDTO(contato.getNome(), contato.getParentesco(),
                    contato.getTelefone(), contato.getEmail()));
        }
        DeclaracaoDeSaude d = c.getDeclaracaoDeSaude();
        if (d != null) {
            DeclaracaoDeSaudeDTO dd = new DeclaracaoDeSaudeDTO();
            dd.setFumante(d.isFumante());
            dd.setDependenteQuimico(d.isDependenteQuimico());
            dd.setCardiopata(d.isCardiopata());
            dd.setSuspeitaDeGravidez(d.isSuspeitaDeGravidez());
            dd.setDiabetico(d.isDiabetico());
            dd.setSedentario(d.isSedentario());
            dd.setHipertenso(d.isHipertenso());
            dto.setDeclaracaoDeSaude(dd);
        }
        return dto;
    }

    private static PacienteDTO paraDTO(Paciente p) {
        PacienteDTO dto = new PacienteDTO();
        dto.setNome(p.getNome());
        dto.setCpf(p.getCpf());
        dto.setRg(p.getRg());
        dto.setDataDeNascimento(Conversor.paraTexto(p.getDataDeNascimento()));
        dto.setEstadoCivil(nome(p.getEstadoCivil()));
        dto.setProfissao(p.getProfissao());
        dto.setAltura(numero(p.getAltura()));
        dto.setPeso(numero(p.getPeso()));
        dto.setTelefone(p.getTelefone());
        dto.setEmail(p.getEmail());
        Endereco e = p.getEndereco();
        if (e != null) {
            EnderecoDTO ed = dto.getEndereco();
            ed.setRua(e.getRua());
            ed.setNumero(e.getNumero());
            ed.setComplemento(e.getComplemento());
            ed.setSetor(e.getSetor());
            ed.setCep(e.getCep());
            ed.setCidade(e.getCidade());
            ed.setEstado(e.getEstado());
        }
        return dto;
    }

    private static String nome(Enum<?> valor) {
        if (valor == null) {
            return null;
        }
        return valor.name();
    }

    /** 0 -> "" ; 60.0 -> "60" ; 1.75 -> "1,75" */
    private static String numero(double numero) {
        if (numero == 0) {
            return "";
        }
        if (numero == Math.floor(numero)) {
            return String.valueOf((long) numero);
        }
        return String.valueOf(numero).replace('.', ',');
    }
}
