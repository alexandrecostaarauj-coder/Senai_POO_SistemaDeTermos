package documento;

import java.io.File;
import java.text.Normalizer;
import javax.swing.filechooser.FileSystemView;
import model.Contrato;

/**
 * Onde ficam os arquivos gerados: Documentos\Termos Performance Place\
 * Nome padrão: Termo_0002_Ana_Paula_Exemplo.docx / .pdf
 * O número do termo no começo permite achar e apagar os arquivos de um termo
 * mesmo que o nome do paciente tenha sido corrigido depois.
 */
public class PastaDeTermos {

    private final File pasta;

    /** Pasta padrão, dentro de "Documentos" do usuário do Windows. */
    public PastaDeTermos() {
        this(new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "Termos Performance Place"));
    }

    public PastaDeTermos(File pasta) {
        this.pasta = pasta;
    }

    public File getPasta() {
        pasta.mkdirs(); // cria se ainda não existir
        return pasta;
    }

    /** Arquivo de saída para o termo, com a extensão pedida (".docx" ou ".pdf"). */
    public File arquivo(Contrato contrato, String extensao) {
        return new File(getPasta(), prefixo(contrato.getID()) + semAcentos(contrato.getPaciente().getNome()) + extensao);
    }

    /** Apaga todos os arquivos (Word e PDF) de um termo. Devolve quantos apagou. */
    public int apagar(int idDoTermo) {
        int apagados = 0;
        File[] arquivos = getPasta().listFiles();
        if (arquivos == null) {
            return 0;
        }
        for (File f : arquivos) {
            if (f.getName().startsWith(prefixo(idDoTermo)) && f.delete()) {
                apagados++;
            }
        }
        return apagados;
    }

    private String prefixo(int id) {
        return String.format("Termo_%04d_", id);
    }

    /** "Ana Paula Conceição" -> "Ana_Paula_Conceicao" (nome de arquivo seguro). */
    private String semAcentos(String texto) {
        String s = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_|_$", "");
        if (s.length() > 60) {
            s = s.substring(0, 60);
        }
        return s;
    }
}
