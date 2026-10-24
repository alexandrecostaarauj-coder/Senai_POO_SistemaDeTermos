package visao;

import documento.GeradorPdf;
import documento.GeradorWord;
import documento.PastaDeTermos;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import model.Contrato;

/**
 * Gera o Word e o PDF de um termo SEM travar a tela (o Word pode levar alguns
 * segundos) e, quando termina, abre a pré-visualização. Uso:
 * GeracaoDeDocumentos.gerarEMostrar(this, contrato);
 */
public final class GeracaoDeDocumentos {

    private GeracaoDeDocumentos() {
    }

    public static void gerarEMostrar(Window pai, Contrato contrato) {
        PastaDeTermos pasta = new PastaDeTermos();
        GeradorWord word = new GeradorWord(java.nio.file.Paths.get("modelos", "termo_internacao.docx"), pasta);
        GeradorPdf pdf = new GeradorPdf(word, pasta);

        JDialog aguarde = criarAguarde(pai, "Gerando o termo nº " + contrato.getID() + " em Word e PDF...");

        // SwingWorker: o trabalho pesado roda em outra thread; done() volta para a tela
        new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                return pdf.exportar(contrato); // gera o Word e converte para PDF
            }

            @Override
            protected void done() {
                aguarde.dispose();
                File docx = pasta.arquivo(contrato, ".docx");
                try {
                    File arquivoPdf = get();
                    new TelaPreVisualizacao(arquivoPdf, docx, contrato).setVisible(true);
                } catch (InterruptedException | ExecutionException e) {
                    String motivo = e.getMessage();
                    if (e.getCause() != null) {
                        motivo = e.getCause().getMessage(); // o erro "de verdade" vem dentro
                    }
                    if (docx.exists()) {
                        int r = JOptionPane.showConfirmDialog(pai, "O arquivo Word foi gerado, mas o PDF não:\n" + motivo
                                + "\n\nDeseja abrir o Word agora?", "PDF não gerado", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (r == JOptionPane.YES_OPTION) {
                            abrir(pai, docx);
                        }
                    } else {
                        JOptionPane.showMessageDialog(pai, "Não foi possível gerar o termo:\n" + motivo,
                                "Erro ao gerar documentos", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(pai, "Erro ao abrir a pré-visualização:\n" + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
        aguarde.setVisible(true); // a janelinha fica aberta até o done() fechar
    }

    /**
     * Abre um arquivo ou pasta com o programa padrão do Windows.
     */
    public static void abrir(java.awt.Component pai, File arquivo) {
        try {
            Desktop.getDesktop().open(arquivo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(pai, "Não foi possível abrir:\n" + arquivo + "\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JDialog criarAguarde(Window pai, String mensagem) {
        JDialog d = new JDialog(pai, "Aguarde", JDialog.ModalityType.APPLICATION_MODAL);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        painel.setBackground(java.awt.Color.WHITE);
        JLabel texto = new JLabel(mensagem);
        texto.setForeground(Estilo.TEXTO);
        JProgressBar barra = new JProgressBar();
        barra.setIndeterminate(true);
        painel.add(texto, BorderLayout.NORTH);
        painel.add(barra, BorderLayout.CENTER);
        d.setContentPane(painel);
        d.pack();
        d.setLocationRelativeTo(pai);
        return d;
    }
}
