package documento;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.Contrato;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * Gera o termo em Word a partir do MODELO (modelos\termo_internacao.docx), trocando cada
 * marcador {{NOME}} pelo dado do contrato. A formatação é a do próprio modelo, então
 * quando o jurídico mandar uma versão nova, basta trocar o arquivo do modelo.
 *
 * Campo sem dado:
 *  - dentro de tabela (quadros de identificação) -> fica vazio;
 *  - no meio do texto -> vira uma linha "______" para preencher à mão;
 *  - marcações de "X" (imagem, SIM/NÃO) -> fica um espaço.
 */
public class GeradorWord implements ExportadorTermo {

    private static final Pattern MARCADOR = Pattern.compile("\\{\\{([A-Z0-9_]+)\\}\\}");

    static {
        // O Apache POI usa o Log4j para mensagens internas; sem isto ele avisa no console
        // que "não achou implementação de log". Usamos o logger simples e silencioso.
        if (System.getProperty("log4j2.loggerContextFactory") == null) {
            System.setProperty("log4j2.loggerContextFactory", "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");
        }
    }

    private final Path modelo;
    private final PastaDeTermos pasta;

    public GeradorWord() {
        this(Paths.get("modelos", "termo_internacao.docx"), new PastaDeTermos());
    }

    public GeradorWord(Path modelo, PastaDeTermos pasta) {
        this.modelo = modelo;
        this.pasta = pasta;
    }

    @Override
    public String getFormato() {
        return "Word";
    }

    @Override
    public File exportar(Contrato contrato) throws IOException {
        File destino = pasta.arquivo(contrato, ".docx");
        gerar(contrato, destino);
        return destino;
    }

    /** Preenche o modelo e grava em 'destino'. */
    public void gerar(Contrato contrato, File destino) throws IOException {
        if (!Files.exists(modelo)) {
            throw new IOException("Modelo do termo não encontrado em: " + modelo.toAbsolutePath());
        }
        Map<String, String> valores = ValoresDoTermo.montar(contrato);
        try (InputStream in = Files.newInputStream(modelo); XWPFDocument doc = new XWPFDocument(in)) {
            for (XWPFParagraph p : doc.getParagraphs()) {
                substituir(p, valores, false);
            }
            for (XWPFTable t : doc.getTables()) {
                substituirNaTabela(t, valores);
            }
            try (OutputStream out = new FileOutputStream(destino)) {
                doc.write(out);
            }
        }
    }

    private void substituirNaTabela(XWPFTable tabela, Map<String, String> valores) {
        for (XWPFTableRow linha : tabela.getRows()) {
            for (XWPFTableCell celula : linha.getTableCells()) {
                for (XWPFParagraph p : celula.getParagraphs()) {
                    substituir(p, valores, true);
                }
                for (XWPFTable interna : celula.getTables()) {
                    substituirNaTabela(interna, valores);
                }
            }
        }
    }

    /**
     * Troca os marcadores de um parágrafo. O Word às vezes "quebra" um texto em vários
     * pedaços (runs), por exemplo "{{PACIENTE" + "_NOME}}"; por isso o marcador é procurado
     * no texto inteiro do parágrafo e depois devolvido aos pedaços certos.
     */
    private void substituir(XWPFParagraph paragrafo, Map<String, String> valores, boolean emTabela) {
        while (true) {
            List<XWPFRun> runs = paragrafo.getRuns();
            StringBuilder texto = new StringBuilder();
            int[] inicio = new int[runs.size()];
            for (int i = 0; i < runs.size(); i++) {
                inicio[i] = texto.length();
                texto.append(textoDo(runs.get(i)));
            }
            Matcher m = MARCADOR.matcher(texto);
            if (!m.find()) {
                return;
            }
            int rIni = runQueContem(inicio, m.start());
            int rFim = runQueContem(inicio, m.end() - 1);
            String antes = textoDo(runs.get(rIni)).substring(0, m.start() - inicio[rIni]);
            String depois = textoDo(runs.get(rFim)).substring(m.end() - inicio[rFim]);
            String valor = valorPara(m.group(1), valores, emTabela);
            if (rIni == rFim) {
                runs.get(rIni).setText(antes + valor + depois, 0);
            } else {
                runs.get(rIni).setText(antes + valor, 0);
                for (int k = rIni + 1; k < rFim; k++) {
                    runs.get(k).setText("", 0);
                }
                runs.get(rFim).setText(depois, 0);
            }
        }
    }

    private int runQueContem(int[] inicio, int posicao) {
        int r = 0;
        for (int i = 0; i < inicio.length; i++) {
            if (inicio[i] <= posicao) {
                r = i;
            }
        }
        return r;
    }

    private String textoDo(XWPFRun run) {
        String t = run.getText(0);
        if (t == null) {
            return "";
        }
        return t;
    }

    /** Valor do marcador; se não houver dado, devolve o "branco" adequado. */
    static String valorPara(String marcador, Map<String, String> valores, boolean emTabela) {
        String v = valores.get(marcador);
        if (v != null && !v.trim().isEmpty()) {
            return v;
        }
        if (marcador.startsWith("IMG_") || marcador.endsWith("_SIM") || marcador.endsWith("_NAO")) {
            return " "; // caixinha vazia: ( ) ou [ ]
        }
        if (emTabela) {
            return "";
        }
        switch (marcador) {
            case "DATA_ASSINATURA_EXTENSO":
                return "Goiânia-GO, ______ de ____________________ de ________";
            case "DATA_ASSINATURA":
            case "DATA_PROCEDIMENTO":
            case "DATA_INTERNACAO":
                return "____/____/________";
            case "PACIENTE_NOME":
                return "________________________________________";
            case "MEDICO_CRM":
            case "MEDICO_RQE":
                return "__________";
            default:
                return "______________________________";
        }
    }
}
