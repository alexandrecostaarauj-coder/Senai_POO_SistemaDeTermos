
package model;

/**
 *
 * @author Alexandre Costa Araujo
 */
public class Endereco {
    String rua;
    String numero;
    String setor;
    String cep;
    String cidade;
    String estado;
    String complemento; 

    public Endereco(String rua, String numero, String setor, String cep, String cidade, String estado) {
        this(rua, numero, null, setor, cep, cidade, estado);
    }

    public Endereco(String rua, String numero, String complemento, String setor, String cep, String cidade, String estado) {
        this.complemento = complemento;
        this.rua = rua;
        this.numero = numero;
        this.setor = setor;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getRua() {
        return rua;
    }

    public String getNumero() {
        return numero;
    }

    public String getSetor() {
        return setor;
    }

    public String getCep() {
        return cep;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "Endereco: " 
                + "Rua: " + rua 
                + "Numero: " + numero 
                + "Setor: " + setor 
                + "CEP: " + cep 
                + "Cidade: " + cidade 
                + "Estado: " + estado + '.';
    }
    
    
    
}
