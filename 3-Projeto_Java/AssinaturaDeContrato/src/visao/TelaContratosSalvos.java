package visao;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.Contrato;
import persistencia.ContratoDAO;
import util.Conversor;

/**
 * Lista dos termos salvos (no máximo 20). Daqui dá para: - buscar por nome ou
 * CPF (filtra enquanto digita); - abrir um termo para corrigir (botão ou duplo
 * clique); - gerar de novo o Word/PDF e abrir a pré-visualização (para
 * reimprimir); - excluir um termo feito por engano.
 *
 * @author Alexandre Costa Araujo
 */
public class TelaContratosSalvos extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaContratosSalvos.class.getName());

    private final ContratoDAO contratoDAO = new ContratoDAO();
    private List<Contrato> todos = new ArrayList<>();      // tudo que está no arquivo
    private List<Contrato> visiveis = new ArrayList<>();   // o que aparece na tabela (depois do filtro), mesma ordem das linhas

    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"Nº", "Paciente", "CPF", "Médico", "Procedimento", "Data proc.", "Modo"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public TelaContratosSalvos() {
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
        setResizable(false);
        Estilo.botaoPrimario(jButton_Abrir);
        Estilo.botaoSecundario(jButton_Excluir);
        Estilo.botaoSecundario(jButton_Documentos);
        Estilo.botaoSecundario(jButton_Voltar);
        Estilo.aplicarIcone(this);
        configurarTabela();
        carregar();
        setLocationRelativeTo(null);
    }

    private void configurarTabela() {
        jTable_Termos.setModel(modeloTabela);
        jTable_Termos.setRowHeight(24);
        jTable_Termos.setFillsViewportHeight(true);
        jScrollPane_Termos.getViewport().setBackground(java.awt.Color.WHITE);
        jTable_Termos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] larguras = {40, 220, 120, 160, 150, 90, 90};
        for (int i = 0; i < larguras.length; i++) {
            jTable_Termos.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        // duplo clique abre o termo
        jTable_Termos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirSelecionado();
                }
            }
        });
        // filtra enquanto digita
        jTextField_Busca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
    }

    /**
     * Lê o arquivo (mais recentes primeiro) e aplica o filtro.
     */
    public void carregar() {
        try {
            todos = contratoDAO.listar();
        } catch (IOException e) {
            todos = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Não foi possível ler os termos:\n" + e.getMessage(),
                    "Erro ao ler arquivo", JOptionPane.ERROR_MESSAGE);
        }
        java.util.Collections.reverse(todos); // o último salvo aparece em cima
        filtrar();
    }

    private void filtrar() {
        String busca = jTextField_Busca.getText().trim().toLowerCase();
        String buscaNumeros = busca.replaceAll("\\D", "");
        visiveis = new ArrayList<>();
        modeloTabela.setRowCount(0);
        for (int i = 0; i < todos.size(); i++) {
            Contrato c = todos.get(i);
            String nome = c.getPaciente().getNome().toLowerCase();
            String cpf = textoOuVazio(c.getPaciente().getCpf());
            boolean combina = busca.isEmpty() || nome.contains(busca)
                    || (!buscaNumeros.isEmpty() && cpf.contains(buscaNumeros));
            if (combina) {
                visiveis.add(c);
                String modo = "";
                if (c.getModoAssinatura() != null) {
                    modo = c.getModoAssinatura().getDescricao();
                }
                modeloTabela.addRow(new Object[]{c.getID(), c.getPaciente().getNome(), formatarCpf(cpf),
                    c.getMedico().getNome(), textoOuVazio(c.getProcedimento()),
                    Conversor.paraTexto(c.getDataDoProcedimento()), modo});
            }
        }
        jLabel_Quantidade.setText(visiveis.size() + " de " + todos.size() + " termos (máximo " + ContratoDAO.MAXIMO_CONTRATOS + ")");
    }

    private Contrato selecionado() {
        int linha = jTable_Termos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um termo na tabela.", "Nenhum termo selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return visiveis.get(linha);
    }

    private void abrirSelecionado() {
        Contrato c = selecionado();
        if (c == null) {
            return;
        }
        try {
            new TelaContrato(c, this::carregar).setVisible(true); // ao salvar, a lista se atualiza
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro ao abrir termo", e);
            JOptionPane.showMessageDialog(this, "Não foi possível abrir o termo:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String textoOuVazio(String texto) {
        if (texto == null) {
            return "";
        }
        return texto;
    }

    /**
     * 52998224725 -> 529.982.247-25
     */
    private String formatarCpf(String cpf) {
        if (cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Cabecalho = new javax.swing.JPanel();
        jLabel_LogoPequena = new javax.swing.JLabel();
        jLabel_Titulo = new javax.swing.JLabel();
        jLabel_Subtitulo = new javax.swing.JLabel();
        jPanel_Faixa = new javax.swing.JPanel();
        jLabel_Busca = new javax.swing.JLabel();
        jTextField_Busca = new javax.swing.JTextField();
        jLabel_Quantidade = new javax.swing.JLabel();
        jScrollPane_Termos = new javax.swing.JScrollPane();
        jTable_Termos = new javax.swing.JTable();
        jButton_Voltar = new javax.swing.JButton();
        jButton_Excluir = new javax.swing.JButton();
        jButton_Documentos = new javax.swing.JButton();
        jButton_Abrir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Termos salvos");

        jPanel_Cabecalho.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_LogoPequena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/logo_pequena.png"))); // NOI18N

        jLabel_Titulo.setText("Termos salvos");
        jLabel_Titulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel_Titulo.setForeground(new java.awt.Color(59, 47, 48));

        jLabel_Subtitulo.setText("Ficam os últimos 20 termos; os de cirurgias já realizadas saem sozinhos");
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

        jLabel_Busca.setText("Buscar por nome ou CPF:");
        jLabel_Busca.setForeground(new java.awt.Color(59, 47, 48));
        jLabel_Busca.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jTextField_Busca.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jLabel_Quantidade.setText("0 termos");
        jLabel_Quantidade.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel_Quantidade.setForeground(new java.awt.Color(120, 110, 111));


        jTable_Termos.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jScrollPane_Termos.setViewportView(jTable_Termos);

        jButton_Voltar.setText("Voltar");
        jButton_Voltar.addActionListener(this::jButton_VoltarActionPerformed);

        jButton_Excluir.setText("Excluir");
        jButton_Excluir.addActionListener(this::jButton_ExcluirActionPerformed);

        jButton_Documentos.setText("Word / PDF");
        jButton_Documentos.addActionListener(this::jButton_DocumentosActionPerformed);

        jButton_Abrir.setText("Abrir / Editar");
        jButton_Abrir.addActionListener(this::jButton_AbrirActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_Faixa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel_Busca)
                            .addGap(10, 10, 10)
                            .addComponent(jTextField_Busca, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 10, Short.MAX_VALUE)
                            .addComponent(jLabel_Quantidade))
                        .addComponent(jScrollPane_Termos, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE))
                    .addGap(16, 16, 16))
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jButton_Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 16, Short.MAX_VALUE)
                    .addComponent(jButton_Excluir, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_Documentos, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_Abrir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_Faixa, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(14, 14, 14)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_Busca)
                        .addComponent(jTextField_Busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Quantidade))
                    .addGap(10, 10, 10)
                    .addComponent(jScrollPane_Termos, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGap(12, 12, 12)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Excluir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Documentos, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Abrir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_AbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AbrirActionPerformed
        abrirSelecionado();
    }//GEN-LAST:event_jButton_AbrirActionPerformed

    private void jButton_DocumentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DocumentosActionPerformed
        Contrato c = selecionado();
        if (c != null) {
            GeracaoDeDocumentos.gerarEMostrar(this, c);
        }
    }//GEN-LAST:event_jButton_DocumentosActionPerformed

    private void jButton_ExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ExcluirActionPerformed
        Contrato c = selecionado();
        if (c == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir o termo nº " + c.getID() + " de " + c.getPaciente().getNome() + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            contratoDAO.excluir(c.getID());
            new documento.PastaDeTermos().apagar(c.getID()); // apaga também o Word/PDF desse termo
            carregar();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar o arquivo:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro inesperado ao excluir termo", e);
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_ExcluirActionPerformed

    private void jButton_VoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_VoltarActionPerformed
        dispose();
    }//GEN-LAST:event_jButton_VoltarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Abrir;
    private javax.swing.JButton jButton_Documentos;
    private javax.swing.JButton jButton_Excluir;
    private javax.swing.JButton jButton_Voltar;
    private javax.swing.JLabel jLabel_Busca;
    private javax.swing.JLabel jLabel_LogoPequena;
    private javax.swing.JLabel jLabel_Quantidade;
    private javax.swing.JLabel jLabel_Subtitulo;
    private javax.swing.JLabel jLabel_Titulo;
    private javax.swing.JPanel jPanel_Cabecalho;
    private javax.swing.JPanel jPanel_Faixa;
    private javax.swing.JScrollPane jScrollPane_Termos;
    private javax.swing.JTable jTable_Termos;
    private javax.swing.JTextField jTextField_Busca;
    // End of variables declaration//GEN-END:variables
}
