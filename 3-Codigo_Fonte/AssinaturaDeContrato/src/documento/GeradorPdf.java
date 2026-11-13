package documento;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import model.Contrato;

/**
 * Gera o termo em PDF: primeiro gera o Word e depois CONVERTE o Word em PDF usando um
 * programa de escritório instalado no computador (a formatação fica idêntica à do Word):
 *   1) LibreOffice (gratuito) - se estiver instalado;
 *   2) Microsoft Word - precisa estar ATIVADO (licença válida), senão ele trava ao salvar.
 */
public class GeradorPdf implements ExportadorTermo {

    private static final int TEMPO_LIBREOFFICE = 90;  // segundos
    private static final int TEMPO_WORD = 45;         // segundos

    private final GeradorWord word;
    private final PastaDeTermos pasta;

    public GeradorPdf() {
        this(new GeradorWord(), new PastaDeTermos());
    }

    public GeradorPdf(GeradorWord word, PastaDeTermos pasta) {
        this.word = word;
        this.pasta = pasta;
    }

    @Override
    public String getFormato() {
        return "PDF";
    }

    @Override
    public File exportar(Contrato contrato) throws IOException {
        File docx = word.exportar(contrato);
        File pdf = pasta.arquivo(contrato, ".pdf");
        converter(docx, pdf);
        return pdf;
    }

    /** Converte um .docx em .pdf, tentando LibreOffice e depois Word. */
    public void converter(File docx, File pdf) throws IOException {
        pdf.delete(); // garante que não sobra a versão antiga
        String soffice = localizarLibreOffice();
        if (soffice != null && converterComLibreOffice(soffice, docx, pdf)) {
            return;
        }
        if (windows() && converterComWord(docx, pdf)) {
            return;
        }
        throw new IOException("Não foi possível gerar o PDF neste computador.\n"
                + "Instale o LibreOffice (gratuito: libreoffice.org) ou use um Microsoft Word ativado.\n"
                + "O arquivo Word do termo foi gerado normalmente.");
    }

    // ------------------------------------------------------------------ LibreOffice

    private String localizarLibreOffice() {
        String[] caminhos = {"C:\\Program Files\\LibreOffice\\program\\soffice.exe",
            "C:\\Program Files (x86)\\LibreOffice\\program\\soffice.exe", "/usr/bin/soffice", "/usr/local/bin/soffice",
            "/Applications/LibreOffice.app/Contents/MacOS/soffice"};
        for (String c : caminhos) {
            if (new File(c).exists()) {
                return c;
            }
        }
        return null;
    }

    private boolean converterComLibreOffice(String soffice, File docx, File pdf) {
        try {
            executar(new ProcessBuilder(soffice, "--headless", "--norestore", "--convert-to", "pdf", "--outdir",
                    pdf.getParentFile().getAbsolutePath(), docx.getAbsolutePath()), TEMPO_LIBREOFFICE);
            File gerado = new File(pdf.getParentFile(), docx.getName().replaceAll("\\.docx$", ".pdf"));
            if (gerado.exists() && !gerado.equals(pdf)) {
                gerado.renameTo(pdf);
            }
            return pdf.exists();
        } catch (IOException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ Microsoft Word

    /** Abre o .docx no Word escondido (automação pelo PowerShell) e salva como PDF. */
    private boolean converterComWord(File docx, File pdf) {
        String script = "$w = New-Object -ComObject Word.Application; $w.Visible = $false; $w.DisplayAlerts = 0; "
                + "try { $d = $w.Documents.Open('" + aspas(docx) + "', $false, $true); "
                + "$d.SaveAs2('" + aspas(pdf) + "', 17); $d.Close(0) } finally { $w.Quit() }";
        try {
            executar(new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", script),
                    TEMPO_WORD);
        } catch (IOException e) {
            encerrarWordTravado(); // Word sem licença fica preso esperando uma janela; fecha ele
            return false;
        }
        return pdf.exists();
    }

    /** Fecha apenas o Word aberto pela automação (não mexe nos documentos que a pessoa está usando). */
    private void encerrarWordTravado() {
        String script = "Get-CimInstance Win32_Process -Filter \"Name='WINWORD.EXE'\" | "
                + "Where-Object { $_.CommandLine -match 'Automation' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }";
        try {
            executar(new ProcessBuilder("powershell.exe", "-NoProfile", "-Command", script), 20);
        } catch (IOException e) {
            // se não conseguir, não há mais nada a fazer aqui
        }
    }

    // ------------------------------------------------------------------ ajudantes

    private void executar(ProcessBuilder pb, int segundos) throws IOException {
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process processo = pb.start();
        try {
            if (!processo.waitFor(segundos, TimeUnit.SECONDS)) {
                processo.destroyForcibly();
                throw new IOException("A conversão demorou demais e foi cancelada.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Conversão interrompida.");
        }
    }

    private boolean windows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /** No PowerShell, aspas simples dentro de texto viram duas aspas simples. */
    private String aspas(File f) {
        return f.getAbsolutePath().replace("'", "''");
    }
}
