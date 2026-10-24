package visao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JOptionPane;

/**
 * Tela inicial (menu) do sistema. Cores e logo seguem a identidade da
 * Performance Place (ver classe Estilo).
 *
 * @author Alexandre Costa Araujo
 */
public class TelaInicial extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaInicial.class.getName());
    private TelaContrato telaDeContrato;
    private TelaMedico telaDeMedicos;
    private TelaContratosSalvos telaDeTermosSalvos;

    public TelaInicial() {
        setResizable(false);
        initComponents();
        configurarAparencia();
        setLocationRelativeTo(null); // abre no centro da tela
    }

    /**
     * Coisas que o editor visual não faz sozinho: botões coloridos, ícone e
     * data de hoje.
     */
    private void configurarAparencia() {
        Estilo.botaoPrimario(jButton_NovoContrato);
        Estilo.botaoPrimario(jButton_Medicos);
        Estilo.botaoPrimario(jButton_Contratos);
        Estilo.botaoSecundario(jButton_Sair);
        Estilo.aplicarIcone(this);

        // RF: data/ano sempre atualizados automaticamente
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.of("pt", "BR"));
        jLabel_Data.setText("Goiânia, " + LocalDate.now().format(formato));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Logo = new javax.swing.JPanel();
        jLabel_Logo = new javax.swing.JLabel();
        jPanel_Menu = new javax.swing.JPanel();
        jLabel_Titulo = new javax.swing.JLabel();
        jLabel_Subtitulo = new javax.swing.JLabel();
        jButton_NovoContrato = new javax.swing.JButton();
        jButton_Contratos = new javax.swing.JButton();
        jButton_Medicos = new javax.swing.JButton();
        jButton_Sair = new javax.swing.JButton();
        jLabel_Data = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Performance Place - Termo de Internação");

        jPanel_Logo.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_Logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/logo_inicial.png"))); // NOI18N

        javax.swing.GroupLayout jPanel_LogoLayout = new javax.swing.GroupLayout(jPanel_Logo);
        jPanel_Logo.setLayout(jPanel_LogoLayout);
        jPanel_LogoLayout.setHorizontalGroup(
            jPanel_LogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_LogoLayout.createSequentialGroup()
                    .addGap(0, 36, Short.MAX_VALUE)
                    .addComponent(jLabel_Logo)
                    .addGap(0, 36, Short.MAX_VALUE))
        );
        jPanel_LogoLayout.setVerticalGroup(
            jPanel_LogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_LogoLayout.createSequentialGroup()
                    .addGap(0, 36, Short.MAX_VALUE)
                    .addComponent(jLabel_Logo)
                    .addGap(0, 36, Short.MAX_VALUE))
        );

        jPanel_Menu.setBackground(new java.awt.Color(255, 240, 241));

        jLabel_Titulo.setText("Termo de Internação Hospitalar");
        jLabel_Titulo.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jLabel_Titulo.setForeground(new java.awt.Color(59, 47, 48));

        jLabel_Subtitulo.setText("Performance Place  •  Centro de Cirurgia Plástica");
        jLabel_Subtitulo.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel_Subtitulo.setForeground(new java.awt.Color(200, 90, 100));

        jButton_NovoContrato.setText("Novo termo");
        jButton_NovoContrato.addActionListener(this::jButton_NovoContratoActionPerformed);

        jButton_Contratos.setText("Termos salvos");
        jButton_Contratos.addActionListener(this::jButton_ContratosActionPerformed);

        jButton_Medicos.setText("Médicos");
        jButton_Medicos.addActionListener(this::jButton_MedicosActionPerformed);

        jButton_Sair.setText("Sair");
        jButton_Sair.addActionListener(this::jButton_SairActionPerformed);

        jLabel_Data.setText("Goiânia, data de hoje");
        jLabel_Data.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel_Data.setForeground(new java.awt.Color(120, 110, 111));

        javax.swing.GroupLayout jPanel_MenuLayout = new javax.swing.GroupLayout(jPanel_Menu);
        jPanel_Menu.setLayout(jPanel_MenuLayout);
        jPanel_MenuLayout.setHorizontalGroup(
            jPanel_MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_MenuLayout.createSequentialGroup()
                    .addGap(48, 48, 48)
                    .addGroup(jPanel_MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel_Titulo)
                        .addComponent(jLabel_Subtitulo)
                        .addComponent(jButton_NovoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Contratos, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Medicos, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Sair, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel_Data))
                    .addGap(0, 48, Short.MAX_VALUE))
        );
        jPanel_MenuLayout.setVerticalGroup(
            jPanel_MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_MenuLayout.createSequentialGroup()
                    .addGap(0, 48, Short.MAX_VALUE)
                    .addComponent(jLabel_Titulo)
                    .addGap(4, 4, 4)
                    .addComponent(jLabel_Subtitulo)
                    .addGap(34, 34, 34)
                    .addComponent(jButton_NovoContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(jButton_Contratos, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(jButton_Medicos, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(jButton_Sair, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(34, 34, 34)
                    .addComponent(jLabel_Data)
                    .addGap(0, 30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_Menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_Logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_Menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_NovoContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NovoContratoActionPerformed
        try {
            if (telaDeContrato == null || !telaDeContrato.isVisible()) {
                telaDeContrato = new TelaContrato();
                telaDeContrato.setVisible(true);
            } else {
                telaDeContrato.toFront();
                telaDeContrato.requestFocus();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir a tela de contrato:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_NovoContratoActionPerformed

    private void jButton_MedicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_MedicosActionPerformed
        try {
            if (telaDeMedicos == null || !telaDeMedicos.isVisible()) {
                telaDeMedicos = new TelaMedico();
                telaDeMedicos.setVisible(true);
            } else {
                telaDeMedicos.toFront();
                telaDeMedicos.requestFocus();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir a tela de médicos:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_MedicosActionPerformed

    private void jButton_ContratosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ContratosActionPerformed
        try {
            if (telaDeTermosSalvos == null || !telaDeTermosSalvos.isVisible()) {
                telaDeTermosSalvos = new TelaContratosSalvos();
                telaDeTermosSalvos.setVisible(true);
            } else {
                telaDeTermosSalvos.carregar();
                telaDeTermosSalvos.toFront();
                telaDeTermosSalvos.requestFocus();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir os termos salvos:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_ContratosActionPerformed

    private void jButton_SairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SairActionPerformed
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja sair do sistema?", "Sair",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_jButton_SairActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaInicial().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Contratos;
    private javax.swing.JButton jButton_Medicos;
    private javax.swing.JButton jButton_NovoContrato;
    private javax.swing.JButton jButton_Sair;
    private javax.swing.JLabel jLabel_Data;
    private javax.swing.JLabel jLabel_Logo;
    private javax.swing.JLabel jLabel_Subtitulo;
    private javax.swing.JLabel jLabel_Titulo;
    private javax.swing.JPanel jPanel_Logo;
    private javax.swing.JPanel jPanel_Menu;
    // End of variables declaration//GEN-END:variables
}
