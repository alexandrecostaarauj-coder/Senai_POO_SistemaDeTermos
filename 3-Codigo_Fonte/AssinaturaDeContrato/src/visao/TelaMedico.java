package visao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import controller.MedicoController;
import dto.MedicoDTO;

/**
 * Cadastro de médicos (CRUD numa tela só), no mesmo padrão da tela de
 * pacientes: - formulário em cima, tabela embaixo; - clicar numa linha carrega
 * o médico para editar ou excluir.
 *
 * idEmEdicao == 0 -> Salvar CADASTRA | idEmEdicao > 0 -> Salvar ATUALIZA
 *
 * Padrão MVC + DTO: a tela monta um MedicoDTO e chama o MedicoController.
 * Regras (ficam em model.Medico): nome obrigatório, CRM e RQE positivos,
 * CRM não pode repetir. Excluir um médico não afeta os termos já feitos: cada
 * termo guarda uma cópia do nome, CRM e RQE do dia da assinatura.
 *
 * @author Alexandre Costa Araujo
 */
public class TelaMedico extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaMedico.class.getName());

    private final MedicoController medicoController = new MedicoController();

    private List<MedicoDTO> listaMedicos = new ArrayList<>(); // mesma ordem das linhas da tabela
    private int idEmEdicao = 0;

    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"Nº", "Nome", "CRM-GO", "RQE"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public TelaMedico() {
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
        setResizable(false);
        Estilo.botaoPrimario(jButton_Salvar);
        Estilo.botaoSecundario(jButton_Novo);
        Estilo.botaoSecundario(jButton_Excluir);
        Estilo.botaoSecundario(jButton_Voltar);
        Estilo.aplicarIcone(this);
        FiltroNumerico.aplicar(jTextField_Crm, 8); // só números
        FiltroNumerico.aplicar(jTextField_Rqe, 8);
        configurarTabela();
        carregarTabela();
        limparCampos();
        setLocationRelativeTo(null);
    }

    private void configurarTabela() {
        jTable_Medicos.setModel(modeloTabela);
        jTable_Medicos.setRowHeight(24);
        jTable_Medicos.setFillsViewportHeight(true);
        jScrollPane_Medicos.getViewport().setBackground(java.awt.Color.WHITE);
        jTable_Medicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable_Medicos.getColumnModel().getColumn(0).setMaxWidth(50);
        jTable_Medicos.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                carregarSelecionado();
            }
        });
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            listaMedicos = medicoController.listar();
        } catch (IOException e) {
            listaMedicos = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Não foi possível ler os médicos:\n" + e.getMessage(),
                    "Erro ao ler arquivo", JOptionPane.ERROR_MESSAGE);
        }
        for (int i = 0; i < listaMedicos.size(); i++) {
            MedicoDTO m = listaMedicos.get(i);
            modeloTabela.addRow(new Object[]{m.getId(), m.getNome(), m.getCrm(), m.getRqe()});
        }
    }

    private void carregarSelecionado() {
        int linha = jTable_Medicos.getSelectedRow();
        if (linha < 0) {
            return;
        }
        MedicoDTO m = listaMedicos.get(linha);
        idEmEdicao = m.getId();
        jLabel_Situacao.setText("Editando médico nº " + m.getId());
        jTextField_Nome.setText(m.getNome());
        jTextField_Crm.setText(m.getCrm());
        jTextField_Rqe.setText(m.getRqe());
    }

    /** Copia os campos da tela para o DTO (sem regras: quem valida é o model). */
    private MedicoDTO montarMedico() {
        return new MedicoDTO(idEmEdicao, jTextField_Nome.getText(), jTextField_Crm.getText(), jTextField_Rqe.getText());
    }

    private void limparCampos() {
        idEmEdicao = 0;
        jLabel_Situacao.setText("Novo médico");
        jTable_Medicos.clearSelection();
        jTextField_Nome.setText("");
        jTextField_Crm.setText("");
        jTextField_Rqe.setText("");
        jTextField_Nome.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Cabecalho = new javax.swing.JPanel();
        jLabel_LogoPequena = new javax.swing.JLabel();
        jLabel_Titulo = new javax.swing.JLabel();
        jLabel_Subtitulo = new javax.swing.JLabel();
        jPanel_Faixa = new javax.swing.JPanel();
        jPanel_Formulario = new javax.swing.JPanel();
        jLabel_Situacao = new javax.swing.JLabel();
        jLabel_Nome = new javax.swing.JLabel();
        jTextField_Nome = new javax.swing.JTextField();
        jLabel_Crm = new javax.swing.JLabel();
        jTextField_Crm = new javax.swing.JTextField();
        jLabel_Rqe = new javax.swing.JLabel();
        jTextField_Rqe = new javax.swing.JTextField();
        jLabel_Obrigatorio = new javax.swing.JLabel();
        jLabel_Lista = new javax.swing.JLabel();
        jScrollPane_Medicos = new javax.swing.JScrollPane();
        jTable_Medicos = new javax.swing.JTable();
        jButton_Voltar = new javax.swing.JButton();
        jButton_Excluir = new javax.swing.JButton();
        jButton_Novo = new javax.swing.JButton();
        jButton_Salvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Médicos");

        jPanel_Cabecalho.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_LogoPequena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/logo_pequena.png"))); // NOI18N

        jLabel_Titulo.setText("Médicos");
        jLabel_Titulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel_Titulo.setForeground(new java.awt.Color(59, 47, 48));

        jLabel_Subtitulo.setText("Cadastre, consulte, altere ou exclua médicos");
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

        jPanel_Formulario.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_Situacao.setText("Novo médico");
        jLabel_Situacao.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_Situacao.setForeground(new java.awt.Color(200, 90, 100));

        jLabel_Nome.setText("Nome*:");
        jLabel_Nome.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Nome.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Nome.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Crm.setText("CRM-GO:");
        jLabel_Crm.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Crm.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Crm.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Rqe.setText("RQE nº:");
        jLabel_Rqe.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Rqe.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Rqe.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Obrigatorio.setText("* obrigatório. CRM e RQE: somente números.");
        jLabel_Obrigatorio.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel_Obrigatorio.setForeground(new java.awt.Color(120, 110, 111));

        javax.swing.GroupLayout jPanel_FormularioLayout = new javax.swing.GroupLayout(jPanel_Formulario);
        jPanel_Formulario.setLayout(jPanel_FormularioLayout);
        jPanel_FormularioLayout.setHorizontalGroup(
            jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_FormularioLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addGroup(jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel_Situacao)
                        .addGroup(jPanel_FormularioLayout.createSequentialGroup()
                            .addGroup(jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel_Nome)
                                .addComponent(jLabel_Crm)
                                .addComponent(jLabel_Rqe))
                            .addGap(10, 10, 10)
                            .addGroup(jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField_Nome, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                                .addComponent(jTextField_Crm, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Rqe, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jLabel_Obrigatorio))
                    .addGap(20, 20, 20))
        );
        jPanel_FormularioLayout.setVerticalGroup(
            jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_FormularioLayout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jLabel_Situacao)
                    .addGap(10, 10, 10)
                    .addGroup(jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Crm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Crm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel_FormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Rqe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Rqe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGap(2, 2, 2)
                    .addComponent(jLabel_Obrigatorio)
                    .addGap(12, 12, 12))
        );

        jLabel_Lista.setText("Médicos cadastrados (clique em um para editar ou excluir)");
        jLabel_Lista.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel_Lista.setForeground(new java.awt.Color(59, 47, 48));


        jTable_Medicos.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jScrollPane_Medicos.setViewportView(jTable_Medicos);

        jButton_Voltar.setText("Voltar");
        jButton_Voltar.addActionListener(this::jButton_VoltarActionPerformed);

        jButton_Excluir.setText("Excluir");
        jButton_Excluir.addActionListener(this::jButton_ExcluirActionPerformed);

        jButton_Novo.setText("Novo / Limpar");
        jButton_Novo.addActionListener(this::jButton_NovoActionPerformed);

        jButton_Salvar.setText("Salvar");
        jButton_Salvar.addActionListener(this::jButton_SalvarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_Faixa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel_Formulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel_Lista)
                        .addComponent(jScrollPane_Medicos, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE))
                    .addGap(16, 16, 16))
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jButton_Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 16, Short.MAX_VALUE)
                    .addComponent(jButton_Excluir, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_Novo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_Salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_Faixa, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jPanel_Formulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(jLabel_Lista)
                    .addGap(6, 6, 6)
                    .addComponent(jScrollPane_Medicos, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addGap(12, 12, 12)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Excluir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Novo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Salvar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_SalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SalvarActionPerformed
        try {
            MedicoDTO medico = montarMedico();
            if (idEmEdicao == 0) {
                int id = medicoController.cadastrar(medico);
                JOptionPane.showMessageDialog(this, "Médico nº " + id + " cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                medicoController.atualizar(medico);
                JOptionPane.showMessageDialog(this, "Médico nº " + idEmEdicao + " atualizado!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarTabela();
            limparCampos();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Verifique os dados", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar o arquivo:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro inesperado ao salvar médico", e);
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_SalvarActionPerformed

    private void jButton_ExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ExcluirActionPerformed
        if (idEmEdicao == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um médico na tabela para excluir.",
                    "Nenhum médico selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir o médico nº " + idEmEdicao + " - " + jTextField_Nome.getText() + "?",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resposta == JOptionPane.YES_OPTION) {
                medicoController.excluir(idEmEdicao);
                carregarTabela();
                limparCampos();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao acessar os arquivos:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro inesperado ao excluir médico", e);
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_ExcluirActionPerformed

    private void jButton_NovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NovoActionPerformed
        limparCampos();
    }//GEN-LAST:event_jButton_NovoActionPerformed

    private void jButton_VoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_VoltarActionPerformed
        dispose();
    }//GEN-LAST:event_jButton_VoltarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Excluir;
    private javax.swing.JButton jButton_Novo;
    private javax.swing.JButton jButton_Salvar;
    private javax.swing.JButton jButton_Voltar;
    private javax.swing.JLabel jLabel_Crm;
    private javax.swing.JLabel jLabel_Lista;
    private javax.swing.JLabel jLabel_LogoPequena;
    private javax.swing.JLabel jLabel_Nome;
    private javax.swing.JLabel jLabel_Obrigatorio;
    private javax.swing.JLabel jLabel_Rqe;
    private javax.swing.JLabel jLabel_Situacao;
    private javax.swing.JLabel jLabel_Subtitulo;
    private javax.swing.JLabel jLabel_Titulo;
    private javax.swing.JPanel jPanel_Cabecalho;
    private javax.swing.JPanel jPanel_Faixa;
    private javax.swing.JPanel jPanel_Formulario;
    private javax.swing.JScrollPane jScrollPane_Medicos;
    private javax.swing.JTable jTable_Medicos;
    private javax.swing.JTextField jTextField_Crm;
    private javax.swing.JTextField jTextField_Nome;
    private javax.swing.JTextField jTextField_Rqe;
    // End of variables declaration//GEN-END:variables
}
