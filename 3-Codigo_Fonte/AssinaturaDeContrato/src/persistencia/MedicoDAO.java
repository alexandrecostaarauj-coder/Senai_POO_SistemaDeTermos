package persistencia;

import java.io.IOException;
import model.Medico;
import util.Conversor;

/**
 * Grava os médicos em medicos.csv. Colunas: ID;nome;crm;rqe
 * Só converte objeto <-> linha: as regras (nome obrigatório, CRM positivo e
 * único) ficam na classe Medico.
 */
public class MedicoDAO extends ArquivoCsvDAO<Medico> {

    public MedicoDAO() {
        this(".\\src\\dados\\medicos.csv");
    }

    public MedicoDAO(String caminho) {
        super(caminho);
    }

    @Override
    protected int totalDeColunas() {
        return 4;
    }

    @Override
    protected String[] paraColunas(Medico m) {
        return new String[]{
            String.valueOf(m.getID()), m.getNome(), Conversor.paraTexto(m.getCrm()), Conversor.paraTexto(m.getRqe())
        };
    }

    @Override
    protected Medico deColunas(String[] c) throws IOException {
        return new Medico(Integer.parseInt(c[0]), Conversor.paraInteiro(c[2]), Conversor.paraInteiro(c[3]), c[1]);
    }
}
