package persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import model.Identificavel;

/**
 * Parte comum a todos os DAOs que gravam em CSV (separador ';', uma linha por
 * registro, sem cabeçalho). Cada filha só diz COMO transformar o objeto em linha
 * e a linha em objeto (herança + métodos abstratos).
 */
public abstract class ArquivoCsvDAO<T extends Identificavel> implements Persistivel<T> {

    private final Path caminho;

    protected ArquivoCsvDAO(String caminho) {
        this.caminho = Paths.get(caminho);
    }

    // ---- o que cada filha precisa informar ----
    /** Quantas colunas cada linha tem. */
    protected abstract int totalDeColunas();

    /** Converte o objeto em colunas (ordem fixa). As regras já foram conferidas no model. */
    protected abstract String[] paraColunas(T objeto);

    /** Converte colunas em objeto. Pode lançar IllegalArgumentException (dado ruim) ou IOException. */
    protected abstract T deColunas(String[] colunas) throws IOException;

    // ---- CRUD ----
    @Override
    public int salvar(T objeto) throws IOException {
        // 1) gera o ID (maior existente + 1) e monta a linha
        int novoId = proximoId();
        objeto.setID(novoId);
        String[] colunas = paraColunas(objeto);

        // 2) cria a pasta se não existir (mkdirs) e acrescenta a linha no fim
        criarPasta();
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(caminho, StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND))) {
            pw.println(juntar(colunas));
        }
        return novoId;
    }

    @Override
    public List<T> listar() throws IOException {
        List<T> lista = new ArrayList<>();
        if (!Files.exists(caminho)) {
            return lista; // ainda não há dados: não é erro
        }
        try (BufferedReader br = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = br.readLine()) != null) {
                numero++;
                if (linha.trim().isEmpty()) {
                    continue;
                }
                // -1 mantém colunas vazias no fim ("a;;" vira 3 colunas)
                String[] colunas = linha.split(";", -1);
                if (colunas.length < totalDeColunas()) {
                    throw new IOException("Arquivo " + caminho + ", linha " + numero
                            + ": esperadas " + totalDeColunas() + " colunas, encontradas " + colunas.length + ".");
                }
                try {
                    lista.add(deColunas(colunas));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Arquivo " + caminho + ", linha " + numero + ": " + e.getMessage(), e);
                }
            }
        }
        return lista; // só chega aqui se leu tudo: nunca devolve lista pela metade
    }

    @Override
    public T buscarPorId(int id) throws IOException {
        for (T item : listar()) {
            if (item.getID() == id) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean atualizar(T objeto) throws IOException {
        List<T> lista = listar();
        boolean achou = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getID() == objeto.getID()) {
                lista.set(i, objeto);
                achou = true;
            }
        }
        if (achou) {
            reescrever(lista);
        }
        return achou;
    }

    @Override
    public boolean excluir(int id) throws IOException {
        List<T> lista = listar();
        boolean removeu = lista.removeIf(item -> item.getID() == id);
        if (removeu) {
            reescrever(lista);
        }
        return removeu;
    }

    // ---- apoio ----
    private int proximoId() throws IOException {
        int maior = 0;
        for (T item : listar()) {
            if (item.getID() > maior) {
                maior = item.getID();
            }
        }
        return maior + 1;
    }

    private void criarPasta() throws IOException {
        Path pasta = caminho.toAbsolutePath().getParent();
        if (pasta != null) {
            Files.createDirectories(pasta); // equivale ao mkdirs()
        }
    }

    /** Grava tudo num arquivo temporário e troca: se der erro no meio, o original continua intacto. */
    protected void reescrever(List<T> lista) throws IOException {
        criarPasta();
        Path temporario = Paths.get(caminho.toString() + ".tmp");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(temporario, StandardCharsets.UTF_8))) {
            for (T item : lista) {
                pw.println(juntar(paraColunas(item)));
            }
        }
        Files.move(temporario, caminho, StandardCopyOption.REPLACE_EXISTING);
    }

    private String juntar(String[] colunas) {
        return String.join(";", colunas);
    }
}
