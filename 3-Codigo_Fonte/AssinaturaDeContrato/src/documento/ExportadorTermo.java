package documento;

import java.io.File;
import java.io.IOException;
import model.Contrato;

/**
 * Qualquer formato de saída do termo (Word, PDF...). Cada classe que implementa
 * sabe gerar o arquivo do seu jeito; quem usa só chama exportar() (polimorfismo).
 */
public interface ExportadorTermo {

    /** Gera o arquivo do termo e devolve onde ele foi salvo. */
    File exportar(Contrato contrato) throws IOException;

    /** Nome amigável do formato, para mensagens ("Word", "PDF"). */
    String getFormato();
}
