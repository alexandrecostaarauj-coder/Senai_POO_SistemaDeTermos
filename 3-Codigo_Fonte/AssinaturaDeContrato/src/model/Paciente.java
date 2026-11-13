package model;

import java.time.LocalDate;
import util.Conversor;
import util.CpfUtil;

/**
 * Paciente do termo. Regras (nos setters): só o nome é obrigatório; os outros
 * campos podem ficar vazios, mas, se preenchidos, têm que ser válidos.
 *
 * @author Alexandre Costa Araujo
 */
public class Paciente extends Pessoa implements Identificavel {

    private int ID;
    private EstadoCivil estadoCivil;
    private String profissao;
    private String cpf;
    private String rg;
    private LocalDate dataDeNascimento;
    private Endereco endereco;
    private String email;
    private String telefone;
    private double altura;
    private double peso;

    public Paciente(int ID, EstadoCivil estadoCivil, String profissao, String cpf, String rg, LocalDate dataDeNascimento, Endereco endereco, String email, String telefone, double altura, double peso, String nome) {
        super(nome);
        this.ID = ID;
        setEstadoCivil(estadoCivil);
        setProfissao(profissao);
        setCpf(cpf);
        setRg(rg);
        setDataDeNascimento(dataDeNascimento);
        setEndereco(endereco);
        setEmail(email);
        setTelefone(telefone);
        setAltura(altura);
        setPeso(peso);
    }

    @Override
    protected String rotuloDoNome() {
        return "Nome do paciente";
    }

    /**
     * Regra do CPF: 11 números (aceita pontos e traço) e dígitos verificadores
     * corretos. Devolve só os números, ou null se estiver vazio. Também é usada
     * na busca por CPF.
     */
    public static String normalizarCpf(String valor) {
        String t = ValidacaoDeCampos.texto("CPF", valor);
        if (t == null) {
            return null;
        }
        String d = t.replaceAll("[.\\-]", "");
        if (!d.matches("\\d{11}") || d.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF inválido: " + t);
        }
        if (!CpfUtil.digitosConferem(d)) {
            throw new IllegalArgumentException("CPF inválido (dígito verificador): " + t);
        }
        return d;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public void setProfissao(String profissao) {
        this.profissao = ValidacaoDeCampos.texto("Profissão", profissao);
    }

    public void setCpf(String cpf) {
        this.cpf = normalizarCpf(cpf);
    }

    public void setRg(String rg) {
        this.rg = ValidacaoDeCampos.texto("RG", rg);
    }

    /** Não pode ser no futuro nem antes de 1900. */
    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        if (dataDeNascimento != null
                && (dataDeNascimento.isAfter(LocalDate.now()) || dataDeNascimento.getYear() < 1900)) {
            throw new IllegalArgumentException("Data de nascimento inválida: " + Conversor.paraTexto(dataDeNascimento));
        }
        this.dataDeNascimento = dataDeNascimento;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void setEmail(String email) {
        this.email = ValidacaoDeCampos.email("E-mail", email);
    }

    public void setTelefone(String telefone) {
        this.telefone = ValidacaoDeCampos.texto("Telefone", telefone);
    }

    /** Altura em metros. 0 = não informada. */
    public void setAltura(double altura) {
        if (altura != 0 && (altura < 0.30 || altura > 2.80)) {
            throw new IllegalArgumentException("Altura deve estar entre 0,30 m e 2,80 m (em metros).");
        }
        this.altura = altura;
    }

    /** Peso em kg. 0 = não informado. */
    public void setPeso(double peso) {
        if (peso != 0 && (peso < 1 || peso > 700)) {
            throw new IllegalArgumentException("Peso deve estar entre 1 kg e 700 kg.");
        }
        this.peso = peso;
    }

    @Override
    public int getID() {
        return ID;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public String getProfissao() {
        return profissao;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRg() {
        return rg;
    }

    public LocalDate getDataDeNascimento() {
        return dataDeNascimento;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public double getAltura() {
        return altura;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return "Paciente{" + "ID=" + ID + ", nome=" + getNome() + ", estadoCivil=" + estadoCivil + ", profissao=" + profissao + ", cpf=" + cpf + ", rg=" + rg + ", dataDeNascimento=" + dataDeNascimento + ", endereco=" + endereco + ", email=" + email + ", telefone=" + telefone + ", altura=" + altura + ", peso=" + peso + '}';
    }
}
