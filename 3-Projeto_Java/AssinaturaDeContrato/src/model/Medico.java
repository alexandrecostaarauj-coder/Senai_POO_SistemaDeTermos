
package model;

/**
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
        this.crm = crm;
        this.rqe = rqe;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCrm(Integer crm) {
        this.crm = crm;
    }

    public void setRqe(Integer rqe) {
        this.rqe = rqe;
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

    @Override
    public String toString() {
        return "Medico{" + "ID=" + ID + ", crm=" + crm + ", rqe=" + rqe + '}';
    }
    
    
}
