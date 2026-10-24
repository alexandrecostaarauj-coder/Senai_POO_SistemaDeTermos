package persistencia;

import java.io.IOException;
import model.Medico;
import util.Conversor;
import util.Validador;

/** Colunas: ID;nome;crm;rqe */
public class MedicoDAO extends ArquivoCsvDAO<Medico> {

    public MedicoDAO() {
        this(".\\src\\dados\\medicos.csv");
    }

    public MedicoDAO(String caminho) {
        super(caminho);
    }

    /** Regra extra do médico: não pode haver dois médicos com o mesmo CRM. */
    @Override
    public int salvar(Medico medico) throws IOException {
        verificarCrmRepetido(medico);
        return super.salvar(medico);
    }

    @Override
    public boolean atualizar(Medico medico) throws IOException {
        verificarCrmRepetido(medico);
        return super.atualizar(medico);
    }

    private void verificarCrmRepetido(Medico medico) throws IOException {
        if (medico.getCrm() == null) {
            return; // CRM em branco é permitido
        }
        for (Medico outro : listar()) {
            if (outro.getID() != medico.getID() && medico.getCrm().equals(outro.getCrm())) {
                throw new IllegalArgumentException("Já existe médico com o CRM " + medico.getCrm()
                        + ": " + outro.getNome() + " (nº " + outro.getID() + ").");
            }
        }
    }

    @Override
    protected int totalDeColunas() {
        return 4;
    }

    @Override
    protected String[] paraColunas(Medico m) {
        String nome = Validador.obrigatorio("Nome do médico", m.getNome());
        Validador.positivo("CRM", m.getCrm());
        Validador.positivo("RQE", m.getRqe());
        return new String[]{
            String.valueOf(m.getID()), nome, Conversor.paraTexto(m.getCrm()), Conversor.paraTexto(m.getRqe())
        };
    }

    @Override
    protected Medico deColunas(String[] c) {
        return new Medico(Integer.parseInt(c[0]), Conversor.paraInteiro(c[2]), Conversor.paraInteiro(c[3]), c[1]);
    }
}
