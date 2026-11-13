package controller;

import dto.MedicoDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Medico;
import persistencia.MedicoDAO;
import persistencia.Persistivel;
import util.Conversor;

/**
 * Liga a TelaMedico ao arquivo. Recebe e devolve MedicoDTO, converte para
 * model.Medico (onde ficam as regras) e usa o DAO pela interface Persistivel.
 */
public class MedicoController {

    private final Persistivel<Medico> medicoDAO;

    public MedicoController() {
        this(new MedicoDAO());
    }

    /** Usado nos testes, para gravar numa pasta temporária. */
    public MedicoController(Persistivel<Medico> medicoDAO) {
        this.medicoDAO = medicoDAO;
    }

    /** Cadastra e devolve o número (ID) gerado. */
    public int cadastrar(MedicoDTO dto) throws IOException {
        Medico medico = paraModel(dto);
        medico.setID(0);
        medico.verificarCrmUnico(medicoDAO.listar());
        int id = medicoDAO.salvar(medico);
        dto.setId(id);
        return id;
    }

    public List<MedicoDTO> listar() throws IOException {
        List<MedicoDTO> lista = new ArrayList<>();
        for (Medico m : medicoDAO.listar()) {
            lista.add(paraDTO(m));
        }
        return lista;
    }

    public boolean atualizar(MedicoDTO dto) throws IOException {
        Medico medico = paraModel(dto);
        medico.verificarCrmUnico(medicoDAO.listar());
        return medicoDAO.atualizar(medico);
    }

    public boolean excluir(int id) throws IOException {
        return medicoDAO.excluir(id);
    }

    // ------------------------------------------------------------------ conversões

    /** DTO -> model. Os setters do Medico validam (lançam IllegalArgumentException). */
    static Medico paraModel(MedicoDTO dto) {
        return new Medico(dto.getId(), Conversor.paraInteiro(dto.getCrm()),
                Conversor.paraInteiro(dto.getRqe()), dto.getNome());
    }

    static MedicoDTO paraDTO(Medico m) {
        return new MedicoDTO(m.getID(), m.getNome(), Conversor.paraTexto(m.getCrm()), Conversor.paraTexto(m.getRqe()));
    }
}
