package visao;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
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
import persistencia.MedicoDAO;
import util.Conversor;

/**
 * Tela do termo de internação (novo ou edição).
 *
 * Aba 1 "Paciente": os dados do paciente ficam DENTRO do termo (não há cadastro
 * de pacientes). Buscar por CPF reaproveita os dados do último termo desse
 * paciente. Aba 2 "Internação": médico (do cadastro), procedimento, datas e
 * modo de assinatura. Abas 3 e 4: só liberadas no modo DIGITAL; no MANUAL saem
 * em branco para preencher à mão.
 *
 * As regras ficam em util.Validador.contrato() (chamado pelo ContratoDAO); aqui
 * a tela só monta o objeto e mostra a mensagem do erro num pop-up.
 *
 * @author Alexandre Costa Araujo
 */
public class TelaContrato extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaContrato.class.getName());

    private static final int ABA_PACIENTE = 0;
    private static final int ABA_INTERNACAO = 1;
    private static final int ABA_CONTATOS = 2;
    private static final int ABA_SAUDE = 3;

    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final ContratoDAO contratoDAO = new ContratoDAO();

    private List<Medico> listaMedicos = new ArrayList<>(); // mesma ordem do combo (posição 0 = "SELECIONE...")
    private int idEmEdicao = 0;            // 0 = termo novo
    private Runnable aoSalvar;             // avisa a tela de termos salvos para atualizar a lista

    /**
     * Termo novo.
     */
    public TelaContrato() {
        initComponents();
        configurar();
        limparCampos();
    }

    /**
     * Abre um termo já salvo para corrigir.
     */
    public TelaContrato(Contrato contrato, Runnable aoSalvar) {
        initComponents();
        configurar();
        this.aoSalvar = aoSalvar;
        carregarContrato(contrato);
    }

    private void configurar() {
        getContentPane().setBackground(java.awt.Color.WHITE);
        setResizable(false);
        Estilo.botaoPrimario(jButton_Salvar);
        Estilo.botaoSecundario(jButton_Limpar);
        Estilo.botaoSecundario(jButton_Voltar);
        Estilo.botaoSecundario(jButton_BuscarCpf);
        Estilo.aplicarIcone(this);

        // máscaras: o campo se formata enquanto digita
        FiltroMascara.aplicar(jTextField_Cpf, "###.###.###-##");
        FiltroMascara.aplicar(jTextField_Nascimento, "##/##/####");
        FiltroMascara.aplicar(jTextField_DataProcedimento, "##/##/####");
        FiltroMascara.aplicar(jTextField_DataAssinatura, "##/##/####");
        FiltroMascara.aplicar(jTextField_Cep, "#####-###");
        FiltroMascara.aplicar(jTextField_Altura, "#,##");
        FiltroDecimal.aplicar(jTextField_Peso, 3, 2);
        FiltroMascara.aplicar(jTextField_Telefone, "(##) ####-####", "(##) #####-####");
        FiltroMascara.aplicar(jTextField_TelefoneContato1, "(##) ####-####", "(##) #####-####");
        FiltroMascara.aplicar(jTextField_TelefoneContato2, "(##) ####-####", "(##) #####-####");
        FiltroLetras.aplicar(jTextField_Uf, 2, true);          // UF: só 2 letras, maiúsculas
        FiltroLetras.aplicar(jTextField_Cidade, 60, false);    // cidade: só letras e espaço

        carregarMedicos();
        setLocationRelativeTo(null);
    }

    // =================================================================== médicos
    private void carregarMedicos() {
        try {
            listaMedicos = medicoDAO.listar();
        } catch (IOException e) {
            listaMedicos = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Não foi possível ler os médicos:\n" + e.getMessage(),
                    "Erro ao ler arquivo", JOptionPane.ERROR_MESSAGE);
        }
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("SELECIONE...");
        for (int i = 0; i < listaMedicos.size(); i++) {
            modelo.addElement(descreverMedico(listaMedicos.get(i)));
        }
        jComboBox_Medico.setModel(modelo);
    }

    private String descreverMedico(Medico m) {
        String texto = m.getNome();
        if (m.getCrm() != null) {
            texto += "  (CRM " + m.getCrm() + ")";
        }
        return texto;
    }

    /**
     * Seleciona no combo o médico do termo. Se ele saiu do cadastro, coloca a
     * cópia guardada no termo.
     */
    private void selecionarMedico(Medico doTermo) {
        for (int i = 0; i < listaMedicos.size(); i++) {
            Medico m = listaMedicos.get(i);
            boolean mesmoCrm = m.getCrm() != null && m.getCrm().equals(doTermo.getCrm());
            if (mesmoCrm || m.getNome().equalsIgnoreCase(doTermo.getNome())) {
                jComboBox_Medico.setSelectedIndex(i + 1);
                return;
            }
        }
        listaMedicos.add(doTermo);
        ((DefaultComboBoxModel<String>) jComboBox_Medico.getModel()).addElement(descreverMedico(doTermo) + " - fora do cadastro");
        jComboBox_Medico.setSelectedIndex(listaMedicos.size());
    }

    // =================================================================== busca por CPF
    private void buscarPorCpf() {
        String cpf = jTextField_Cpf.getText();
        if (cpf.replaceAll("\\D", "").length() != 11) {
            JOptionPane.showMessageDialog(this, "Digite o CPF completo (11 números) para buscar.",
                    "CPF incompleto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Contrato anterior = contratoDAO.buscarUltimoPorCpf(cpf);
            if (anterior == null) {
                jLabel_InfoBusca.setText("Paciente sem termo salvo. Preencha os dados abaixo.");
                jTextField_Nome.requestFocus();
                return;
            }
            if (!jTextField_Nome.getText().trim().isEmpty()) {
                int resposta = JOptionPane.showConfirmDialog(this,
                        "Encontrado: " + anterior.getPaciente().getNome() + ".\nSubstituir os dados já digitados?",
                        "Paciente encontrado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resposta != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            preencherPaciente(anterior.getPaciente());
            preencherContatos(anterior.getContatosDeEmergencia());
            jLabel_InfoBusca.setText("Dados do termo nº " + anterior.getID() + ". Confira se algo mudou.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "CPF inválido", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível ler os termos salvos:\n" + e.getMessage(),
                    "Erro ao ler arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =================================================================== modo de assinatura
    /**
     * Libera ou bloqueia as abas de contatos e saúde conforme o modo escolhido
     * (0 = Manual, 1 = Digital).
     */
    private void atualizarModo() {
        int indice = jComboBox_ModoAssinatura.getSelectedIndex();
        boolean digital = indice == 1;
        jTabbedPane_Contrato.setEnabledAt(ABA_CONTATOS, digital);
        jTabbedPane_Contrato.setEnabledAt(ABA_SAUDE, digital);
        int abaAtual = jTabbedPane_Contrato.getSelectedIndex();
        if (!digital && (abaAtual == ABA_CONTATOS || abaAtual == ABA_SAUDE)) {
            jTabbedPane_Contrato.setSelectedIndex(ABA_INTERNACAO);
        }
        if (digital) {
            jLabel_InfoModo.setText("Digital: preencha também as abas 3 e 4.");
        } else {
            jLabel_InfoModo.setText("Manual: contatos, saúde e imagem saem em branco para preencher à mão.");
        }
    }

    // =================================================================== tela -> objeto
    private Contrato montarContrato() {
        Medico medico = null;
        if (jComboBox_Medico.getSelectedIndex() > 0) {
            medico = listaMedicos.get(jComboBox_Medico.getSelectedIndex() - 1);
        }
        // a ordem do combo é a mesma do enum: 0 = MANUAL, 1 = DIGITAL
        ModoAssinatura modo = ModoAssinatura.values()[jComboBox_ModoAssinatura.getSelectedIndex()];
        LocalDate dataProcedimento = Conversor.paraData(jTextField_DataProcedimento.getText());
        LocalDate dataAssinatura = Conversor.paraData(jTextField_DataAssinatura.getText());

        List<ContatoDeEmergencia> contatos = new ArrayList<>();
        DeclaracaoDeSaude declaracao = null;
        OpcaoDeImagem opcaoImagem = null;
        if (modo == ModoAssinatura.DIGITAL) {
            adicionarContatoSePreenchido(contatos, jTextField_NomeContato1, jTextField_ParentescoContato1,
                    jTextField_TelefoneContato1, jTextField_EmailContato1);
            adicionarContatoSePreenchido(contatos, jTextField_NomeContato2, jTextField_ParentescoContato2,
                    jTextField_TelefoneContato2, jTextField_EmailContato2);
            declaracao = new DeclaracaoDeSaude(jCheckBox_Fumante.isSelected(), jCheckBox_DependenteQuimico.isSelected(),
                    jCheckBox_Cardiopata.isSelected(), jCheckBox_SuspeitaGravidez.isSelected(),
                    jCheckBox_Diabetico.isSelected(), jCheckBox_Sedentario.isSelected(), jCheckBox_Hipertenso.isSelected());
            if (jComboBox_OpcaoImagem.getSelectedIndex() > 0) {
                opcaoImagem = OpcaoDeImagem.values()[jComboBox_OpcaoImagem.getSelectedIndex() - 1];
            }
        }
        return new Contrato(idEmEdicao, montarPaciente(), medico, jTextField_Procedimento.getText(),
                dataProcedimento, dataAssinatura, modo, contatos, opcaoImagem, declaracao);
    }

    private Paciente montarPaciente() {
        EstadoCivil estadoCivil = null;
        if (jComboBox_EstadoCivil.getSelectedIndex() > 0) {
            estadoCivil = EstadoCivil.values()[jComboBox_EstadoCivil.getSelectedIndex() - 1];
        }
        Endereco endereco = null;
        String enderecoTodo = jTextField_Rua.getText() + jTextField_Numero.getText() + jTextField_Complemento.getText()
                + jTextField_Setor.getText() + jTextField_Cep.getText() + jTextField_Cidade.getText() + jTextField_Uf.getText();
        if (!enderecoTodo.trim().isEmpty()) {
            endereco = new Endereco(jTextField_Rua.getText(), jTextField_Numero.getText(), jTextField_Complemento.getText(),
                    jTextField_Setor.getText(), jTextField_Cep.getText(), jTextField_Cidade.getText(), jTextField_Uf.getText());
        }
        return new Paciente(0, estadoCivil, jTextField_Profissao.getText(), jTextField_Cpf.getText(),
                jTextField_Rg.getText(), Conversor.paraData(jTextField_Nascimento.getText()), endereco,
                jTextField_Email.getText(), jTextField_Telefone.getText(),
                Conversor.paraDouble(jTextField_Altura.getText()), Conversor.paraDouble(jTextField_Peso.getText()),
                jTextField_Nome.getText());
    }

    /**
     * Só cria o contato se pelo menos um dos campos tiver algo digitado.
     */
    private void adicionarContatoSePreenchido(List<ContatoDeEmergencia> contatos, JTextField nome,
            JTextField parentesco, JTextField telefone, JTextField email) {
        String textoTodo = nome.getText() + parentesco.getText() + telefone.getText() + email.getText();
        if (!textoTodo.trim().isEmpty()) {
            contatos.add(new ContatoDeEmergencia(nome.getText(), parentesco.getText(), telefone.getText(), email.getText()));
        }
    }

    // =================================================================== objeto -> tela
    private void carregarContrato(Contrato c) {
        limparCampos();
        idEmEdicao = c.getID();
        setTitle("Termo de internação nº " + c.getID());
        jLabel_Titulo.setText("Editando termo nº " + c.getID());
        jLabel_Subtitulo.setText("Corrija o que for preciso e clique em Salvar termo");

        preencherPaciente(c.getPaciente());
        jLabel_InfoBusca.setText(" ");
        selecionarMedico(c.getMedico());
        jTextField_Procedimento.setText(textoOuVazio(c.getProcedimento()));
        jTextField_DataProcedimento.setText(Conversor.paraTexto(c.getDataDoProcedimento()));
        jTextField_DataAssinatura.setText(Conversor.paraTexto(c.getDataDaAssinatura()));
        if (c.getModoAssinatura() != null) {
            jComboBox_ModoAssinatura.setSelectedIndex(c.getModoAssinatura().ordinal());
        }
        preencherContatos(c.getContatosDeEmergencia());
        DeclaracaoDeSaude d = c.getDeclaracaoDeSaude();
        if (d != null) {
            jCheckBox_Fumante.setSelected(d.isFumante());
            jCheckBox_DependenteQuimico.setSelected(d.isDependenteQuimico());
            jCheckBox_Cardiopata.setSelected(d.isCardiopata());
            jCheckBox_SuspeitaGravidez.setSelected(d.isSuspeitaDeGravidez());
            jCheckBox_Diabetico.setSelected(d.isDiabetico());
            jCheckBox_Sedentario.setSelected(d.isSedentario());
            jCheckBox_Hipertenso.setSelected(d.isHipertenso());
        }
        if (c.getOpcaoDeImagem() != null) {
            jComboBox_OpcaoImagem.setSelectedIndex(c.getOpcaoDeImagem().ordinal() + 1);
        }
        atualizarModo();
    }

    private void preencherPaciente(Paciente p) {
        jTextField_Cpf.setText(textoOuVazio(p.getCpf()));
        jTextField_Nome.setText(textoOuVazio(p.getNome()));
        jTextField_Rg.setText(textoOuVazio(p.getRg()));
        jTextField_Nascimento.setText(Conversor.paraTexto(p.getDataDeNascimento()));
        if (p.getEstadoCivil() == null) {
            jComboBox_EstadoCivil.setSelectedIndex(0);
        } else {
            jComboBox_EstadoCivil.setSelectedIndex(p.getEstadoCivil().ordinal() + 1);
        }
        jTextField_Profissao.setText(textoOuVazio(p.getProfissao()));
        jTextField_Altura.setText(numeroOuVazio(p.getAltura()));
        jTextField_Peso.setText(numeroOuVazio(p.getPeso()));
        jTextField_Telefone.setText(textoOuVazio(p.getTelefone()));
        jTextField_Email.setText(textoOuVazio(p.getEmail()));
        Endereco e = p.getEndereco();
        if (e == null) {
            e = new Endereco(null, null, null, null, null, null, null);
        }
        jTextField_Rua.setText(textoOuVazio(e.getRua()));
        jTextField_Numero.setText(textoOuVazio(e.getNumero()));
        jTextField_Complemento.setText(textoOuVazio(e.getComplemento()));
        jTextField_Setor.setText(textoOuVazio(e.getSetor()));
        jTextField_Cep.setText(textoOuVazio(e.getCep()));
        jTextField_Cidade.setText(textoOuVazio(e.getCidade()));
        jTextField_Uf.setText(textoOuVazio(e.getEstado()));
    }

    private void preencherContatos(List<ContatoDeEmergencia> contatos) {
        JTextField[][] campos = {
            {jTextField_NomeContato1, jTextField_ParentescoContato1, jTextField_TelefoneContato1, jTextField_EmailContato1},
            {jTextField_NomeContato2, jTextField_ParentescoContato2, jTextField_TelefoneContato2, jTextField_EmailContato2}};
        for (int i = 0; i < campos.length; i++) {
            ContatoDeEmergencia c = null;
            if (i < contatos.size()) {
                c = contatos.get(i);
            }
            if (c == null) {
                c = new ContatoDeEmergencia(null, null, null, null);
            }
            campos[i][0].setText(textoOuVazio(c.getNome()));
            campos[i][1].setText(textoOuVazio(c.getParentesco()));
            campos[i][2].setText(textoOuVazio(c.getTelefone()));
            campos[i][3].setText(textoOuVazio(c.getEmail()));
        }
    }

    private void limparCampos() {
        idEmEdicao = 0;
        preencherPaciente(new Paciente(0, null, null, null, null, null, null, null, null, 0, 0, null));
        preencherContatos(new ArrayList<>());
        jLabel_InfoBusca.setText("Digite o CPF e clique em Buscar (ou Enter) para reaproveitar os dados.");
        jComboBox_Medico.setSelectedIndex(0);
        jTextField_Procedimento.setText("");
        // os termos são preparados na véspera: as duas datas já vêm com o PRÓXIMO DIA ÚTIL
        // (na sexta já sugere a segunda-feira). Dá para alterar.
        String proximoDia = Conversor.paraTexto(proximoDiaUtil(LocalDate.now()));
        jTextField_DataProcedimento.setText(proximoDia);
        jTextField_DataAssinatura.setText(proximoDia);
        jComboBox_ModoAssinatura.setSelectedIndex(0); // Manual (recepção)
        jCheckBox_Fumante.setSelected(false);
        jCheckBox_DependenteQuimico.setSelected(false);
        jCheckBox_Cardiopata.setSelected(false);
        jCheckBox_SuspeitaGravidez.setSelected(false);
        jCheckBox_Diabetico.setSelected(false);
        jCheckBox_Sedentario.setSelected(false);
        jCheckBox_Hipertenso.setSelected(false);
        jComboBox_OpcaoImagem.setSelectedIndex(0);
        jTabbedPane_Contrato.setSelectedIndex(ABA_PACIENTE);
        atualizarModo();
        jTextField_Cpf.requestFocus();
    }

    /**
     * Dia seguinte, pulando sábado e domingo. Sexta -> segunda.
     */
    public static LocalDate proximoDiaUtil(LocalDate hoje) {
        LocalDate dia = hoje.plusDays(1);
        while (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dia = dia.plusDays(1);
        }
        return dia;
    }

    /**
     * Apaga os arquivos Word/PDF dos termos que saíram da lista (limite de 20).
     */
    private void apagarArquivosRemovidos() {
        documento.PastaDeTermos pasta = new documento.PastaDeTermos();
        for (Contrato removido : contratoDAO.getUltimosRemovidos()) {
            pasta.apagar(removido.getID());
        }
    }

    private String textoOuVazio(String texto) {
        if (texto == null) {
            return "";
        }
        return texto;
    }

    private String numeroOuVazio(double numero) {
        if (numero == 0) {
            return "";
        }
        if (numero == Math.floor(numero)) {
            return String.valueOf((long) numero); // 60.0 -> "60"
        }
        return String.valueOf(numero).replace('.', ',');
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Cabecalho = new javax.swing.JPanel();
        jLabel_LogoPequena = new javax.swing.JLabel();
        jLabel_Titulo = new javax.swing.JLabel();
        jLabel_Subtitulo = new javax.swing.JLabel();
        jPanel_Faixa = new javax.swing.JPanel();
        jTabbedPane_Contrato = new javax.swing.JTabbedPane();
        jPanel_Paciente = new javax.swing.JPanel();
        jLabel_Cpf = new javax.swing.JLabel();
        jTextField_Cpf = new javax.swing.JTextField();
        jButton_BuscarCpf = new javax.swing.JButton();
        jLabel_InfoBusca = new javax.swing.JLabel();
        jLabel_Nome = new javax.swing.JLabel();
        jTextField_Nome = new javax.swing.JTextField();
        jLabel_Rg = new javax.swing.JLabel();
        jTextField_Rg = new javax.swing.JTextField();
        jLabel_Nascimento = new javax.swing.JLabel();
        jTextField_Nascimento = new javax.swing.JTextField();
        jLabel_EstadoCivil = new javax.swing.JLabel();
        jComboBox_EstadoCivil = new javax.swing.JComboBox<>();
        jLabel_Profissao = new javax.swing.JLabel();
        jTextField_Profissao = new javax.swing.JTextField();
        jLabel_Altura = new javax.swing.JLabel();
        jTextField_Altura = new javax.swing.JTextField();
        jLabel_Peso = new javax.swing.JLabel();
        jTextField_Peso = new javax.swing.JTextField();
        jLabel_Telefone = new javax.swing.JLabel();
        jTextField_Telefone = new javax.swing.JTextField();
        jLabel_Email = new javax.swing.JLabel();
        jTextField_Email = new javax.swing.JTextField();
        jLabel_Rua = new javax.swing.JLabel();
        jTextField_Rua = new javax.swing.JTextField();
        jLabel_Numero = new javax.swing.JLabel();
        jTextField_Numero = new javax.swing.JTextField();
        jLabel_Complemento = new javax.swing.JLabel();
        jTextField_Complemento = new javax.swing.JTextField();
        jLabel_Setor = new javax.swing.JLabel();
        jTextField_Setor = new javax.swing.JTextField();
        jLabel_Cep = new javax.swing.JLabel();
        jTextField_Cep = new javax.swing.JTextField();
        jLabel_Cidade = new javax.swing.JLabel();
        jTextField_Cidade = new javax.swing.JTextField();
        jLabel_Uf = new javax.swing.JLabel();
        jTextField_Uf = new javax.swing.JTextField();
        jLabel_ObrigatorioPaciente = new javax.swing.JLabel();
        jPanel_Internacao = new javax.swing.JPanel();
        jLabel_Medico = new javax.swing.JLabel();
        jComboBox_Medico = new javax.swing.JComboBox<>();
        jLabel_Procedimento = new javax.swing.JLabel();
        jTextField_Procedimento = new javax.swing.JTextField();
        jLabel_DataProcedimento = new javax.swing.JLabel();
        jTextField_DataProcedimento = new javax.swing.JTextField();
        jLabel_DataAssinatura = new javax.swing.JLabel();
        jTextField_DataAssinatura = new javax.swing.JTextField();
        jLabel_ModoAssinatura = new javax.swing.JLabel();
        jComboBox_ModoAssinatura = new javax.swing.JComboBox<>();
        jLabel_InfoModo = new javax.swing.JLabel();
        jPanel_Contatos = new javax.swing.JPanel();
        jLabel_Contato1 = new javax.swing.JLabel();
        jLabel_NomeContato1 = new javax.swing.JLabel();
        jTextField_NomeContato1 = new javax.swing.JTextField();
        jLabel_ParentescoContato1 = new javax.swing.JLabel();
        jTextField_ParentescoContato1 = new javax.swing.JTextField();
        jLabel_TelefoneContato1 = new javax.swing.JLabel();
        jTextField_TelefoneContato1 = new javax.swing.JTextField();
        jLabel_EmailContato1 = new javax.swing.JLabel();
        jTextField_EmailContato1 = new javax.swing.JTextField();
        jLabel_Contato2 = new javax.swing.JLabel();
        jLabel_NomeContato2 = new javax.swing.JLabel();
        jTextField_NomeContato2 = new javax.swing.JTextField();
        jLabel_ParentescoContato2 = new javax.swing.JLabel();
        jTextField_ParentescoContato2 = new javax.swing.JTextField();
        jLabel_TelefoneContato2 = new javax.swing.JLabel();
        jTextField_TelefoneContato2 = new javax.swing.JTextField();
        jLabel_EmailContato2 = new javax.swing.JLabel();
        jTextField_EmailContato2 = new javax.swing.JTextField();
        jPanel_Saude = new javax.swing.JPanel();
        jLabel_Declaracao = new javax.swing.JLabel();
        jCheckBox_Fumante = new javax.swing.JCheckBox();
        jCheckBox_DependenteQuimico = new javax.swing.JCheckBox();
        jCheckBox_Cardiopata = new javax.swing.JCheckBox();
        jCheckBox_SuspeitaGravidez = new javax.swing.JCheckBox();
        jCheckBox_Diabetico = new javax.swing.JCheckBox();
        jCheckBox_Sedentario = new javax.swing.JCheckBox();
        jCheckBox_Hipertenso = new javax.swing.JCheckBox();
        jLabel_Imagem = new javax.swing.JLabel();
        jLabel_OpcaoImagem = new javax.swing.JLabel();
        jComboBox_OpcaoImagem = new javax.swing.JComboBox<>();
        jButton_Voltar = new javax.swing.JButton();
        jButton_Limpar = new javax.swing.JButton();
        jButton_Salvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Termo de internação");

        jPanel_Cabecalho.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_LogoPequena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/logo_pequena.png"))); // NOI18N

        jLabel_Titulo.setText("Novo termo de internação");
        jLabel_Titulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel_Titulo.setForeground(new java.awt.Color(59, 47, 48));

        jLabel_Subtitulo.setText("Preencha o paciente, a internação e escolha como o termo será assinado");
        jLabel_Subtitulo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel_Subtitulo.setForeground(new java.awt.Color(200, 90, 100));

        javax.swing.GroupLayout jPanel_CabecalhoLayout = new javax.swing.GroupLayout(jPanel_Cabecalho);
        jPanel_Cabecalho.setLayout(jPanel_CabecalhoLayout);
        jPanel_CabecalhoLayout.setHorizontalGroup(
            jPanel_CabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_CabecalhoLayout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jLabel_LogoPequena)
                    .addGap(14, 14, 14)
                    .addGroup(jPanel_CabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel_Titulo)
                        .addComponent(jLabel_Subtitulo))
                    .addGap(0, 16, Short.MAX_VALUE))
        );
        jPanel_CabecalhoLayout.setVerticalGroup(
            jPanel_CabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_CabecalhoLayout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addGroup(jPanel_CabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel_LogoPequena)
                        .addGroup(jPanel_CabecalhoLayout.createSequentialGroup()
                            .addComponent(jLabel_Titulo)
                            .addGap(2, 2, 2)
                            .addComponent(jLabel_Subtitulo)))
                    .addGap(10, 10, 10))
        );

        jPanel_Faixa.setBackground(new java.awt.Color(253, 174, 177));

        javax.swing.GroupLayout jPanel_FaixaLayout = new javax.swing.GroupLayout(jPanel_Faixa);
        jPanel_Faixa.setLayout(jPanel_FaixaLayout);
        jPanel_FaixaLayout.setHorizontalGroup(
            jPanel_FaixaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel_FaixaLayout.setVerticalGroup(
            jPanel_FaixaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 6, Short.MAX_VALUE)
        );

        jTabbedPane_Contrato.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jPanel_Paciente.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_Cpf.setText("CPF:");
        jLabel_Cpf.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Cpf.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Cpf.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jTextField_Cpf.addActionListener(this::jTextField_CpfActionPerformed);

        jButton_BuscarCpf.setText("Buscar");
        jButton_BuscarCpf.setToolTipText("Procura o último termo deste CPF e preenche os dados");
        jButton_BuscarCpf.addActionListener(this::jButton_BuscarCpfActionPerformed);

        jLabel_InfoBusca.setText("Digite o CPF e clique em Buscar (ou Enter) para reaproveitar os dados.");
        jLabel_InfoBusca.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel_InfoBusca.setForeground(new java.awt.Color(200, 90, 100));

        jLabel_Nome.setText("Nome*:");
        jLabel_Nome.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Nome.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Nome.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Rg.setText("RG:");
        jLabel_Rg.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Rg.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Rg.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Nascimento.setText("Nascimento:");
        jLabel_Nascimento.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Nascimento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Nascimento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_EstadoCivil.setText("Estado civil:");
        jLabel_EstadoCivil.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_EstadoCivil.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jComboBox_EstadoCivil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE...", "Solteiro(a)", "Casado(a)", "Divorciado(a)", "Viúvo(a)", "União estável" }));
        jComboBox_EstadoCivil.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Profissao.setText("Profissão:");
        jLabel_Profissao.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Profissao.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Profissao.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Altura.setText("Altura (m):");
        jLabel_Altura.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Altura.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Altura.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Peso.setText("Peso (kg):");
        jLabel_Peso.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Peso.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Peso.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Telefone.setText("Telefone:");
        jLabel_Telefone.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Telefone.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Telefone.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Email.setText("E-mail:");
        jLabel_Email.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Email.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Email.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Rua.setText("Endereço:");
        jLabel_Rua.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Rua.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Rua.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Numero.setText("Número:");
        jLabel_Numero.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Numero.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Numero.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Complemento.setText("Complemento:");
        jLabel_Complemento.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Complemento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Complemento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Setor.setText("Setor/Bairro:");
        jLabel_Setor.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Setor.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Setor.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Cep.setText("CEP:");
        jLabel_Cep.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Cep.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Cep.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Cidade.setText("Cidade:");
        jLabel_Cidade.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Cidade.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Cidade.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Uf.setText("UF:");
        jLabel_Uf.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Uf.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Uf.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_ObrigatorioPaciente.setText("* obrigatório. Os demais campos podem ficar em branco (saem com linha para preencher à mão).");
        jLabel_ObrigatorioPaciente.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel_ObrigatorioPaciente.setForeground(new java.awt.Color(120, 110, 111));

        javax.swing.GroupLayout jPanel_PacienteLayout = new javax.swing.GroupLayout(jPanel_Paciente);
        jPanel_Paciente.setLayout(jPanel_PacienteLayout);
        jPanel_PacienteLayout.setHorizontalGroup(
            jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_PacienteLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_PacienteLayout.createSequentialGroup()
                            .addComponent(jLabel_Cpf)
                            .addGap(10, 10, 10)
                            .addComponent(jTextField_Cpf, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(8, 8, 8)
                            .addComponent(jButton_BuscarCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)
                            .addComponent(jLabel_InfoBusca, 0, 330, Short.MAX_VALUE))
                        .addGroup(jPanel_PacienteLayout.createSequentialGroup()
                            .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel_Nome)
                                .addComponent(jLabel_Rg)
                                .addComponent(jLabel_Nascimento)
                                .addComponent(jLabel_EstadoCivil)
                                .addComponent(jLabel_Profissao)
                                .addComponent(jLabel_Altura)
                                .addComponent(jLabel_Peso)
                                .addComponent(jLabel_Telefone))
                            .addGap(10, 10, 10)
                            .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField_Nome, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addComponent(jTextField_Rg, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Nascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBox_EstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Profissao, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addComponent(jTextField_Altura, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Peso, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Telefone, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(26, 26, 26)
                            .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel_Email)
                                .addComponent(jLabel_Rua)
                                .addComponent(jLabel_Numero)
                                .addComponent(jLabel_Complemento)
                                .addComponent(jLabel_Setor)
                                .addComponent(jLabel_Cep)
                                .addComponent(jLabel_Cidade)
                                .addComponent(jLabel_Uf))
                            .addGap(10, 10, 10)
                            .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField_Email, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addComponent(jTextField_Rua, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addComponent(jTextField_Numero, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Complemento, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addComponent(jTextField_Setor, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addComponent(jTextField_Cep, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Cidade, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addComponent(jTextField_Uf, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jLabel_ObrigatorioPaciente))
                    .addGap(20, 20, 20))
        );
        jPanel_PacienteLayout.setVerticalGroup(
            jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_PacienteLayout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Cpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Cpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_BuscarCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_InfoBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Rg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Rg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Rua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Rua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Nascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Nascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Numero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Numero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_EstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox_EstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Complemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Complemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Profissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Profissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Setor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Setor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Altura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Altura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Cep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Cep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Peso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Peso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Cidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Cidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Telefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Telefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Uf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Uf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGap(4, 4, 4)
                    .addComponent(jLabel_ObrigatorioPaciente)
                    .addGap(0, 14, Short.MAX_VALUE))
        );

        jPanel_Internacao.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_Medico.setText("Médico*:");
        jLabel_Medico.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Medico.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jComboBox_Medico.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE..." }));
        jComboBox_Medico.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Procedimento.setText("Procedimento:");
        jLabel_Procedimento.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Procedimento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Procedimento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_DataProcedimento.setText("Data do procedimento*:");
        jLabel_DataProcedimento.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_DataProcedimento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_DataProcedimento.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_DataAssinatura.setText("Data da assinatura:");
        jLabel_DataAssinatura.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_DataAssinatura.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_DataAssinatura.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_ModoAssinatura.setText("Modo de assinatura:");
        jLabel_ModoAssinatura.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_ModoAssinatura.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jComboBox_ModoAssinatura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Manual (recepção)", "Digital" }));
        jComboBox_ModoAssinatura.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jComboBox_ModoAssinatura.addActionListener(this::jComboBox_ModoAssinaturaActionPerformed);

        jLabel_InfoModo.setText("Manual: contatos, saúde e imagem saem em branco para preencher à mão.");
        jLabel_InfoModo.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel_InfoModo.setForeground(new java.awt.Color(200, 90, 100));

        javax.swing.GroupLayout jPanel_InternacaoLayout = new javax.swing.GroupLayout(jPanel_Internacao);
        jPanel_Internacao.setLayout(jPanel_InternacaoLayout);
        jPanel_InternacaoLayout.setHorizontalGroup(
            jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_InternacaoLayout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel_Medico)
                        .addComponent(jLabel_Procedimento)
                        .addComponent(jLabel_DataProcedimento)
                        .addComponent(jLabel_DataAssinatura)
                        .addComponent(jLabel_ModoAssinatura))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jComboBox_Medico, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addComponent(jTextField_Procedimento, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addComponent(jTextField_DataProcedimento, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_DataAssinatura, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox_ModoAssinatura, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_InfoModo, 0, 440, Short.MAX_VALUE))
                    .addGap(24, 24, 24))
        );
        jPanel_InternacaoLayout.setVerticalGroup(
            jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_InternacaoLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addGroup(jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Medico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox_Medico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Procedimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Procedimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_DataProcedimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_DataProcedimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_DataAssinatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_DataAssinatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_InternacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_ModoAssinatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox_ModoAssinatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addComponent(jLabel_InfoModo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 20, Short.MAX_VALUE))
        );

        jPanel_Contatos.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_Contato1.setText("Contato 1  (obrigatório na assinatura digital)");
        jLabel_Contato1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_Contato1.setForeground(new java.awt.Color(200, 90, 100));

        jLabel_NomeContato1.setText("Nome:");
        jLabel_NomeContato1.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_NomeContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_NomeContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_ParentescoContato1.setText("Parentesco:");
        jLabel_ParentescoContato1.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_ParentescoContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_ParentescoContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_TelefoneContato1.setText("Telefone:");
        jLabel_TelefoneContato1.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_TelefoneContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_TelefoneContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_EmailContato1.setText("E-mail:");
        jLabel_EmailContato1.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_EmailContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_EmailContato1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Contato2.setText("Contato 2  (opcional)");
        jLabel_Contato2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_Contato2.setForeground(new java.awt.Color(200, 90, 100));

        jLabel_NomeContato2.setText("Nome:");
        jLabel_NomeContato2.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_NomeContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_NomeContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_ParentescoContato2.setText("Parentesco:");
        jLabel_ParentescoContato2.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_ParentescoContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_ParentescoContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_TelefoneContato2.setText("Telefone:");
        jLabel_TelefoneContato2.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_TelefoneContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_TelefoneContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_EmailContato2.setText("E-mail:");
        jLabel_EmailContato2.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_EmailContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_EmailContato2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel_ContatosLayout = new javax.swing.GroupLayout(jPanel_Contatos);
        jPanel_Contatos.setLayout(jPanel_ContatosLayout);
        jPanel_ContatosLayout.setHorizontalGroup(
            jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_ContatosLayout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel_NomeContato1)
                        .addComponent(jLabel_ParentescoContato1)
                        .addComponent(jLabel_TelefoneContato1)
                        .addComponent(jLabel_EmailContato1)
                        .addComponent(jLabel_NomeContato2)
                        .addComponent(jLabel_ParentescoContato2)
                        .addComponent(jLabel_TelefoneContato2)
                        .addComponent(jLabel_EmailContato2))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel_Contato1)
                        .addComponent(jTextField_NomeContato1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addComponent(jTextField_ParentescoContato1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_TelefoneContato1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_EmailContato1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addComponent(jLabel_Contato2)
                        .addComponent(jTextField_NomeContato2, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addComponent(jTextField_ParentescoContato2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_TelefoneContato2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_EmailContato2, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
                    .addGap(24, 24, 24))
        );
        jPanel_ContatosLayout.setVerticalGroup(
            jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_ContatosLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(jLabel_Contato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_NomeContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_NomeContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_ParentescoContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_ParentescoContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_TelefoneContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_TelefoneContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_EmailContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_EmailContato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addComponent(jLabel_Contato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_NomeContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_NomeContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_ParentescoContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_ParentescoContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_TelefoneContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_TelefoneContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(jPanel_ContatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_EmailContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_EmailContato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 20, Short.MAX_VALUE))
        );

        jPanel_Saude.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_Declaracao.setText("Declaração de saúde: marque o que se aplica ao paciente");
        jLabel_Declaracao.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_Declaracao.setForeground(new java.awt.Color(200, 90, 100));

        jCheckBox_Fumante.setText("Fumante");
        jCheckBox_Fumante.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox_Fumante.setForeground(new java.awt.Color(59, 47, 48));
        jCheckBox_Fumante.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox_DependenteQuimico.setText("Dependente químico");
        jCheckBox_DependenteQuimico.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox_DependenteQuimico.setForeground(new java.awt.Color(59, 47, 48));
        jCheckBox_DependenteQuimico.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox_Cardiopata.setText("Cardiopata");
        jCheckBox_Cardiopata.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox_Cardiopata.setForeground(new java.awt.Color(59, 47, 48));
        jCheckBox_Cardiopata.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox_SuspeitaGravidez.setText("Suspeita de gravidez");
        jCheckBox_SuspeitaGravidez.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox_SuspeitaGravidez.setForeground(new java.awt.Color(59, 47, 48));
        jCheckBox_SuspeitaGravidez.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox_Diabetico.setText("Diabético");
        jCheckBox_Diabetico.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox_Diabetico.setForeground(new java.awt.Color(59, 47, 48));
        jCheckBox_Diabetico.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox_Sedentario.setText("Sedentário");
        jCheckBox_Sedentario.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox_Sedentario.setForeground(new java.awt.Color(59, 47, 48));
        jCheckBox_Sedentario.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox_Hipertenso.setText("Hipertenso");
        jCheckBox_Hipertenso.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox_Hipertenso.setForeground(new java.awt.Color(59, 47, 48));
        jCheckBox_Hipertenso.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_Imagem.setText("Uso de imagem");
        jLabel_Imagem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_Imagem.setForeground(new java.awt.Color(200, 90, 100));

        jLabel_OpcaoImagem.setText("O paciente autoriza o uso de imagem?");
        jLabel_OpcaoImagem.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_OpcaoImagem.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jComboBox_OpcaoImagem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE...", "Anônima", "Identificada", "Não autoriza" }));
        jComboBox_OpcaoImagem.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel_SaudeLayout = new javax.swing.GroupLayout(jPanel_Saude);
        jPanel_Saude.setLayout(jPanel_SaudeLayout);
        jPanel_SaudeLayout.setHorizontalGroup(
            jPanel_SaudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_SaudeLayout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(jPanel_SaudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel_Declaracao)
                        .addGroup(jPanel_SaudeLayout.createSequentialGroup()
                            .addGroup(jPanel_SaudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jCheckBox_Fumante)
                                .addComponent(jCheckBox_DependenteQuimico)
                                .addComponent(jCheckBox_Cardiopata)
                                .addComponent(jCheckBox_SuspeitaGravidez))
                            .addGap(60, 60, 60)
                            .addGroup(jPanel_SaudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jCheckBox_Diabetico)
                                .addComponent(jCheckBox_Sedentario)
                                .addComponent(jCheckBox_Hipertenso)))
                        .addComponent(jLabel_Imagem)
                        .addGroup(jPanel_SaudeLayout.createSequentialGroup()
                            .addComponent(jLabel_OpcaoImagem)
                            .addGap(12, 12, 12)
                            .addComponent(jComboBox_OpcaoImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 24, Short.MAX_VALUE))
        );
        jPanel_SaudeLayout.setVerticalGroup(
            jPanel_SaudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_SaudeLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(jLabel_Declaracao)
                    .addGap(10, 10, 10)
                    .addGroup(jPanel_SaudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_SaudeLayout.createSequentialGroup()
                            .addComponent(jCheckBox_Fumante)
                            .addGap(6, 6, 6)
                            .addComponent(jCheckBox_DependenteQuimico)
                            .addGap(6, 6, 6)
                            .addComponent(jCheckBox_Cardiopata)
                            .addGap(6, 6, 6)
                            .addComponent(jCheckBox_SuspeitaGravidez))
                        .addGroup(jPanel_SaudeLayout.createSequentialGroup()
                            .addComponent(jCheckBox_Diabetico)
                            .addGap(6, 6, 6)
                            .addComponent(jCheckBox_Sedentario)
                            .addGap(6, 6, 6)
                            .addComponent(jCheckBox_Hipertenso)))
                    .addGap(24, 24, 24)
                    .addComponent(jLabel_Imagem)
                    .addGap(10, 10, 10)
                    .addGroup(jPanel_SaudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_OpcaoImagem)
                        .addComponent(jComboBox_OpcaoImagem))
                    .addGap(0, 20, Short.MAX_VALUE))
        );
        jTabbedPane_Contrato.addTab("1. Paciente", jPanel_Paciente);
        jTabbedPane_Contrato.addTab("2. Internação", jPanel_Internacao);
        jTabbedPane_Contrato.addTab("3. Contatos de emergência", jPanel_Contatos);
        jTabbedPane_Contrato.addTab("4. Saúde e imagem", jPanel_Saude);

        jButton_Voltar.setText("Voltar");
        jButton_Voltar.addActionListener(this::jButton_VoltarActionPerformed);

        jButton_Limpar.setText("Limpar");
        jButton_Limpar.addActionListener(this::jButton_LimparActionPerformed);

        jButton_Salvar.setText("Salvar termo");
        jButton_Salvar.addActionListener(this::jButton_SalvarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_Faixa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jTabbedPane_Contrato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(16, 16, 16))
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 16, Short.MAX_VALUE)
                    .addComponent(jButton_Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_Limpar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_Salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_Faixa, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(jTabbedPane_Contrato, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(14, 14, 14)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Limpar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_CpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_CpfActionPerformed
        buscarPorCpf(); // Enter no campo CPF
    }//GEN-LAST:event_jTextField_CpfActionPerformed

    private void jButton_BuscarCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_BuscarCpfActionPerformed
        buscarPorCpf();
    }//GEN-LAST:event_jButton_BuscarCpfActionPerformed

    private void jComboBox_ModoAssinaturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_ModoAssinaturaActionPerformed
        atualizarModo();
    }//GEN-LAST:event_jComboBox_ModoAssinaturaActionPerformed

    private void jButton_SalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SalvarActionPerformed
        try {
            Contrato contrato = montarContrato();
            if (idEmEdicao == 0) {
                contratoDAO.salvar(contrato);      // aqui o contrato recebe o número (ID)
                apagarArquivosRemovidos();
                GeracaoDeDocumentos.gerarEMostrar(this, contrato); // Word + PDF + pré-visualização
                limparCampos();
            } else {
                contratoDAO.atualizar(contrato);
                if (aoSalvar != null) {
                    aoSalvar.run();
                }
                GeracaoDeDocumentos.gerarEMostrar(this, contrato); // gera de novo, substituindo os arquivos
                dispose();
            }
        } catch (IllegalArgumentException e) {
            // dado digitado errado ou faltando (mensagem vem do Validador/Conversor)
            JOptionPane.showMessageDialog(this, e.getMessage(), "Verifique os dados", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar o arquivo:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro inesperado ao salvar termo", e);
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_SalvarActionPerformed

    private void jButton_LimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_LimparActionPerformed
        if (idEmEdicao == 0) {
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Na edição não é possível limpar. Use Voltar para descartar as alterações.",
                    "Editando termo", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_LimparActionPerformed

    private void jButton_VoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_VoltarActionPerformed
        dispose(); // fecha só esta janela
    }//GEN-LAST:event_jButton_VoltarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_BuscarCpf;
    private javax.swing.JButton jButton_Limpar;
    private javax.swing.JButton jButton_Salvar;
    private javax.swing.JButton jButton_Voltar;
    private javax.swing.JCheckBox jCheckBox_Cardiopata;
    private javax.swing.JCheckBox jCheckBox_DependenteQuimico;
    private javax.swing.JCheckBox jCheckBox_Diabetico;
    private javax.swing.JCheckBox jCheckBox_Fumante;
    private javax.swing.JCheckBox jCheckBox_Hipertenso;
    private javax.swing.JCheckBox jCheckBox_Sedentario;
    private javax.swing.JCheckBox jCheckBox_SuspeitaGravidez;
    private javax.swing.JComboBox<String> jComboBox_EstadoCivil;
    private javax.swing.JComboBox<String> jComboBox_Medico;
    private javax.swing.JComboBox<String> jComboBox_ModoAssinatura;
    private javax.swing.JComboBox<String> jComboBox_OpcaoImagem;
    private javax.swing.JLabel jLabel_Altura;
    private javax.swing.JLabel jLabel_Cep;
    private javax.swing.JLabel jLabel_Cidade;
    private javax.swing.JLabel jLabel_Complemento;
    private javax.swing.JLabel jLabel_Contato1;
    private javax.swing.JLabel jLabel_Contato2;
    private javax.swing.JLabel jLabel_Cpf;
    private javax.swing.JLabel jLabel_DataAssinatura;
    private javax.swing.JLabel jLabel_DataProcedimento;
    private javax.swing.JLabel jLabel_Declaracao;
    private javax.swing.JLabel jLabel_Email;
    private javax.swing.JLabel jLabel_EmailContato1;
    private javax.swing.JLabel jLabel_EmailContato2;
    private javax.swing.JLabel jLabel_EstadoCivil;
    private javax.swing.JLabel jLabel_Imagem;
    private javax.swing.JLabel jLabel_InfoBusca;
    private javax.swing.JLabel jLabel_InfoModo;
    private javax.swing.JLabel jLabel_LogoPequena;
    private javax.swing.JLabel jLabel_Medico;
    private javax.swing.JLabel jLabel_ModoAssinatura;
    private javax.swing.JLabel jLabel_Nascimento;
    private javax.swing.JLabel jLabel_Nome;
    private javax.swing.JLabel jLabel_NomeContato1;
    private javax.swing.JLabel jLabel_NomeContato2;
    private javax.swing.JLabel jLabel_Numero;
    private javax.swing.JLabel jLabel_ObrigatorioPaciente;
    private javax.swing.JLabel jLabel_OpcaoImagem;
    private javax.swing.JLabel jLabel_ParentescoContato1;
    private javax.swing.JLabel jLabel_ParentescoContato2;
    private javax.swing.JLabel jLabel_Peso;
    private javax.swing.JLabel jLabel_Procedimento;
    private javax.swing.JLabel jLabel_Profissao;
    private javax.swing.JLabel jLabel_Rg;
    private javax.swing.JLabel jLabel_Rua;
    private javax.swing.JLabel jLabel_Setor;
    private javax.swing.JLabel jLabel_Subtitulo;
    private javax.swing.JLabel jLabel_Telefone;
    private javax.swing.JLabel jLabel_TelefoneContato1;
    private javax.swing.JLabel jLabel_TelefoneContato2;
    private javax.swing.JLabel jLabel_Titulo;
    private javax.swing.JLabel jLabel_Uf;
    private javax.swing.JPanel jPanel_Cabecalho;
    private javax.swing.JPanel jPanel_Contatos;
    private javax.swing.JPanel jPanel_Faixa;
    private javax.swing.JPanel jPanel_Internacao;
    private javax.swing.JPanel jPanel_Paciente;
    private javax.swing.JPanel jPanel_Saude;
    private javax.swing.JTabbedPane jTabbedPane_Contrato;
    private javax.swing.JTextField jTextField_Altura;
    private javax.swing.JTextField jTextField_Cep;
    private javax.swing.JTextField jTextField_Cidade;
    private javax.swing.JTextField jTextField_Complemento;
    private javax.swing.JTextField jTextField_Cpf;
    private javax.swing.JTextField jTextField_DataAssinatura;
    private javax.swing.JTextField jTextField_DataProcedimento;
    private javax.swing.JTextField jTextField_Email;
    private javax.swing.JTextField jTextField_EmailContato1;
    private javax.swing.JTextField jTextField_EmailContato2;
    private javax.swing.JTextField jTextField_Nascimento;
    private javax.swing.JTextField jTextField_Nome;
    private javax.swing.JTextField jTextField_NomeContato1;
    private javax.swing.JTextField jTextField_NomeContato2;
    private javax.swing.JTextField jTextField_Numero;
    private javax.swing.JTextField jTextField_ParentescoContato1;
    private javax.swing.JTextField jTextField_ParentescoContato2;
    private javax.swing.JTextField jTextField_Peso;
    private javax.swing.JTextField jTextField_Procedimento;
    private javax.swing.JTextField jTextField_Profissao;
    private javax.swing.JTextField jTextField_Rg;
    private javax.swing.JTextField jTextField_Rua;
    private javax.swing.JTextField jTextField_Setor;
    private javax.swing.JTextField jTextField_Telefone;
    private javax.swing.JTextField jTextField_TelefoneContato1;
    private javax.swing.JTextField jTextField_TelefoneContato2;
    private javax.swing.JTextField jTextField_Uf;
    // End of variables declaration//GEN-END:variables
}
