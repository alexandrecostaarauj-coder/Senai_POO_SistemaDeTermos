package model;

/**
 * Endereço do paciente. Regras: todos os campos são opcionais; CEP com 8
 * números e UF com 2 letras (guardada em maiúsculas).
 *
 * @author Alexandre Costa Araujo
 */
public class Endereco {

    private String rua;
    private String numero;
    private String setor;
    private String cep;
    private String cidade;
    private String estado;
    private String complemento;

    public Endereco(String rua, String numero, String setor, String cep, String cidade, String estado) {
        this(rua, numero, null, setor, cep, cidade, estado);
    }

    public Endereco(String rua, String numero, String complemento, String setor, String cep, String cidade, String estado) {
        setRua(rua);
        setNumero(numero);
        setComplemento(complemento);
        setSetor(setor);
        setCep(cep);
        setCidade(cidade);
        setEstado(estado);
    }

    public void setRua(String rua) {
        this.rua = ValidacaoDeCampos.texto("Endereço", rua);
    }

    public void setNumero(String numero) {
        this.numero = ValidacaoDeCampos.texto("Número", numero);
    }

    public void setSetor(String setor) {
        this.setor = ValidacaoDeCampos.texto("Setor", setor);
    }

    /** CEP: 8 números (aceita traço). Guarda só os números. */
    public void setCep(String cep) {
        String t = ValidacaoDeCampos.texto("CEP", cep);
        if (t != null) {
            t = t.replace("-", "");
            if (!t.matches("\\d{8}")) {
                throw new IllegalArgumentException("CEP deve ter 8 dígitos: " + cep.trim());
            }
        }
        this.cep = t;
    }

    public void setCidade(String cidade) {
        this.cidade = ValidacaoDeCampos.texto("Cidade", cidade);
    }

    /** UF: 2 letras, guardada em maiúsculas. */
    public void setEstado(String estado) {
        String t = ValidacaoDeCampos.texto("Estado (UF)", estado);
        if (t != null) {
            if (!t.matches("[A-Za-z]{2}")) {
                throw new IllegalArgumentException("UF deve ter 2 letras: " + t);
            }
            t = t.toUpperCase();
        }
        this.estado = t;
    }

    public void setComplemento(String complemento) {
        this.complemento = ValidacaoDeCampos.texto("Complemento", complemento);
    }

    public String getComplemento() {
        return complemento;
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
