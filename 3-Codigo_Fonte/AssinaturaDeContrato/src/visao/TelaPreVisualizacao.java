package visao;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Mostra as páginas do PDF gerado (como numa impressão) e permite imprimir,
 * abrir o Word para algum ajuste manual ou abrir a pasta para enviar o PDF para
 * assinatura digital.
 *
 * Usa a biblioteca Apache PDFBox para "desenhar" cada página como imagem e para
 * imprimir.
 *
 * @author Alexandre Costa Araujo
 */
public class TelaPreVisualizacao extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaPreVisualizacao.class.getName());
    private static final int LARGURA_PAGINA = 650; // pixels na tela

    private final File pdf;
    private final File docx;
    private PDDocument documento; // fica aberto enquanto a tela estiver aberta (para imprimir)

    /** titulo: "Termo nº X - Nome do paciente". */
    public TelaPreVisualizacao(File pdf, File docx, String titulo) throws IOException {
        this.pdf = pdf;
        this.docx = docx;
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
        Estilo.botaoPrimario(jButton_Imprimir);
        Estilo.botaoSecundario(jButton_AbrirWord);
        Estilo.botaoSecundario(jButton_AbrirPasta);
        Estilo.botaoSecundario(jButton_Fechar);
        Estilo.aplicarIcone(this);
        setTitle(titulo);
        jLabel_Subtitulo.setText(titulo);
        jLabel_Arquivos.setText("Arquivos salvos em: " + pdf.getParentFile().getAbsolutePath());
        jScrollPane_Paginas.getVerticalScrollBar().setUnitIncrement(24); // rolagem mais rápida
        mostrarPaginas();
        setLocationRelativeTo(null);
    }

    /**
     * Desenha cada página do PDF como uma imagem, uma embaixo da outra.
     */
    private void mostrarPaginas() throws IOException {
        documento = Loader.loadPDF(pdf);
        PDFRenderer renderizador = new PDFRenderer(documento);
        jPanel_Paginas.removeAll();
        jPanel_Paginas.setLayout(new BoxLayout(jPanel_Paginas, BoxLayout.Y_AXIS));
        jPanel_Paginas.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        for (int i = 0; i < documento.getNumberOfPages(); i++) {
            BufferedImage imagem = renderizador.renderImageWithDPI(i, 110);
            int altura = imagem.getHeight() * LARGURA_PAGINA / imagem.getWidth();
            Image reduzida = imagem.getScaledInstance(LARGURA_PAGINA, altura, Image.SCALE_SMOOTH);
            JLabel pagina = new JLabel(new ImageIcon(reduzida));
            pagina.setAlignmentX(0.5f);
            pagina.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 0, 12, 0),
                    BorderFactory.createLineBorder(new java.awt.Color(190, 185, 185))));
            jPanel_Paginas.add(pagina);
        }
        jPanel_Paginas.revalidate();
    }

    @Override
    public void dispose() {
        try {
            if (documento != null) {
                documento.close();
            }
        } catch (IOException e) {
            logger.log(java.util.logging.Level.WARNING, "Erro ao fechar o PDF", e);
        }
        super.dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Cabecalho = new javax.swing.JPanel();
        jLabel_LogoPequena = new javax.swing.JLabel();
        jLabel_Titulo = new javax.swing.JLabel();
        jLabel_Subtitulo = new javax.swing.JLabel();
        jPanel_Faixa = new javax.swing.JPanel();
        jScrollPane_Paginas = new javax.swing.JScrollPane();
        jPanel_Paginas = new javax.swing.JPanel();
        jLabel_Arquivos = new javax.swing.JLabel();
        jButton_Fechar = new javax.swing.JButton();
        jButton_AbrirPasta = new javax.swing.JButton();
        jButton_AbrirWord = new javax.swing.JButton();
        jButton_Imprimir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pré-visualização do termo");

        jPanel_Cabecalho.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_LogoPequena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/logo_pequena.png"))); // NOI18N

        jLabel_Titulo.setText("Pré-visualização do termo");
        jLabel_Titulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel_Titulo.setForeground(new java.awt.Color(59, 47, 48));

        jLabel_Subtitulo.setText("Confira o documento antes de imprimir ou enviar para assinatura");
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


        jPanel_Paginas.setBackground(new java.awt.Color(230, 226, 226));

        javax.swing.GroupLayout jPanel_PaginasLayout = new javax.swing.GroupLayout(jPanel_Paginas);
        jPanel_Paginas.setLayout(jPanel_PaginasLayout);
        jPanel_PaginasLayout.setHorizontalGroup(
            jPanel_PaginasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel_PaginasLayout.setVerticalGroup(
            jPanel_PaginasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
        );
        jScrollPane_Paginas.setViewportView(jPanel_Paginas);

        jLabel_Arquivos.setText("Arquivos salvos em:");
        jLabel_Arquivos.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel_Arquivos.setForeground(new java.awt.Color(120, 110, 111));

        jButton_Fechar.setText("Fechar");
        jButton_Fechar.addActionListener(this::jButton_FecharActionPerformed);

        jButton_AbrirPasta.setText("Abrir pasta");
        jButton_AbrirPasta.addActionListener(this::jButton_AbrirPastaActionPerformed);

        jButton_AbrirWord.setText("Abrir no Word");
        jButton_AbrirWord.addActionListener(this::jButton_AbrirWordActionPerformed);

        jButton_Imprimir.setText("Imprimir");
        jButton_Imprimir.addActionListener(this::jButton_ImprimirActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_Faixa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane_Paginas, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
                        .addComponent(jLabel_Arquivos, 0, 720, Short.MAX_VALUE))
                    .addGap(16, 16, 16))
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jButton_Fechar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 16, Short.MAX_VALUE)
                    .addComponent(jButton_AbrirPasta, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_AbrirWord, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jButton_Imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel_Cabecalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_Faixa, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addComponent(jScrollPane_Paginas, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addGap(6, 6, 6)
                    .addComponent(jLabel_Arquivos)
                    .addGap(10, 10, 10)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_Fechar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_AbrirPasta, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_AbrirWord, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_Imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_ImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ImprimirActionPerformed
        try {
            PrinterJob impressao = PrinterJob.getPrinterJob();
            impressao.setJobName(pdf.getName());
            impressao.setPageable(new PDFPageable(documento));
            if (impressao.printDialog()) { // janela do Windows para escolher impressora e cópias
                impressao.print();
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível imprimir:\n" + e.getMessage(),
                    "Erro de impressão", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_ImprimirActionPerformed

    private void jButton_AbrirWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AbrirWordActionPerformed
        GeracaoDeDocumentos.abrir(this, docx);
    }//GEN-LAST:event_jButton_AbrirWordActionPerformed

    private void jButton_AbrirPastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AbrirPastaActionPerformed
        GeracaoDeDocumentos.abrir(this, pdf.getParentFile());
    }//GEN-LAST:event_jButton_AbrirPastaActionPerformed

    private void jButton_FecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FecharActionPerformed
        dispose();
    }//GEN-LAST:event_jButton_FecharActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_AbrirPasta;
    private javax.swing.JButton jButton_AbrirWord;
    private javax.swing.JButton jButton_Fechar;
    private javax.swing.JButton jButton_Imprimir;
    private javax.swing.JLabel jLabel_Arquivos;
    private javax.swing.JLabel jLabel_LogoPequena;
    private javax.swing.JLabel jLabel_Subtitulo;
    private javax.swing.JLabel jLabel_Titulo;
    private javax.swing.JPanel jPanel_Cabecalho;
    private javax.swing.JPanel jPanel_Faixa;
    private javax.swing.JPanel jPanel_Paginas;
    private javax.swing.JScrollPane jScrollPane_Paginas;
    // End of variables declaration//GEN-END:variables
}
