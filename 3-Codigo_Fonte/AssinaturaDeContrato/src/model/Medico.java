package model;

import java.util.List;

/**
 * Médico do cadastro. Regras: nome obrigatório (Pessoa), CRM e RQE opcionais
 * mas positivos, e o CRM não pode se repetir entre os médicos cadastrados.
 *
 * @author Alexandre Costa Araujo
 */
public class Medico extends Pessoa implements Identificavel {

    private int ID;
    private Integer crm;
    private Integer rqe;

    public Medico(int ID, Integer crm, Integer rqe, String nome) {
        super(nome);
        this.ID = ID;
        setCrm(crm);
        setRqe(rqe);
    }

    @Override
    protected String rotuloDoNome() {
        return "Nome do médico";
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCrm(Integer crm) {
        this.crm = positivo("CRM", crm);
    }

    public void setRqe(Integer rqe) {
        this.rqe = positivo("RQE", rqe);
    }

    @Override
    public int getID() {
        return ID;
    }

    public Integer getCrm() {
        return crm;
    }

    public Integer getRqe() {
        return rqe;
    }

    /**
     * Regra do CRM único: compara com os médicos já cadastrados (menos ele
     * mesmo, para a edição funcionar). CRM em branco é permitido.
     */
    public void verificarCrmUnico(List<Medico> cadastrados) {
        if (crm == null) {
            return;
        }
        for (Medico outro : cadastrados) {
            if (outro.getID() != ID && crm.equals(outro.getCrm())) {
                throw new IllegalArgumentException("Já existe médico com o CRM " + crm
                        + ": " + outro.getNome() + " (nº " + outro.getID() + ").");
            }
        }
    }

    private static Integer positivo(String campo, Integer valor) {
        if (valor != null && valor <= 0) {
            throw new IllegalArgumentException(campo + " deve ser um número positivo.");
        }
        return valor;
    }

    @Override
    public String toString() {
        return "Medico{" + "ID=" + ID + ", nome=" + getNome() + ", crm=" + crm + ", rqe=" + rqe + '}';
    }
}
