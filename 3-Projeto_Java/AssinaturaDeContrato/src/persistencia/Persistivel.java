package persistencia;

import java.io.IOException;
import java.util.List;

/**
 * Operações de qualquer "guardador de dados" (CRUD). As telas dependem desta
 * interface, não de CSV: se um dia trocar por banco de dados, só muda a classe.
 */
public interface Persistivel<T> {

    /** Gera o ID (maior existente + 1), grava e devolve o ID. */
    int salvar(T objeto) throws IOException;

    /** Lê tudo. Se o arquivo não existir, devolve lista vazia. Se estiver corrompido, lança IOException. */
    List<T> listar() throws IOException;

    /** Devolve o objeto ou null se não existir. */
    T buscarPorId(int id) throws IOException;

    /** Troca o registro de mesmo ID. Devolve false se não achou. */
    boolean atualizar(T objeto) throws IOException;

    /** Remove o registro. Devolve false se não achou. */
    boolean excluir(int id) throws IOException;
}
