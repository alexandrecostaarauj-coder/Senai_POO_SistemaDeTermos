package model;

import java.time.LocalDate;

/**
 *
 * @author Alexandre Costa Araujo
 */
public class Paciente extends Pessoa implements Identificavel {

    int ID;
    EstadoCivil estadoCivil;
    String profissao;
    String cpf;
    String rg;
    LocalDate dataDeNascimento;
    Endereco endereco;
    String email;
    String telefone;
    double altura;
    double peso;

    public Paciente(int ID, EstadoCivil estadoCivil, String profissao, String cpf, String rg, LocalDate dataDeNascimento, Endereco endereco, String email, String telefone, double altura, double peso, String nome) {
        super(nome);
        this.ID = ID;
        this.estadoCivil = estadoCivil;
        this.profissao = profissao;
        this.cpf = cpf;
        this.rg = rg;
        this.dataDeNascimento = dataDeNascimento;
        this.endereco = endereco;
        this.email = email;
        this.telefone = telefone;
        this.altura = altura;
        this.peso = peso;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public void setPeso(double peso) {
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
        return "Paciente{" + "ID=" + ID + ", estadoCivil=" + estadoCivil + ", profissao=" + profissao + ", cpf=" + cpf + ", rg=" + rg + ", dataDeNascimento=" + dataDeNascimento + ", endereco=" + endereco + ", email=" + email + ", telefone=" + telefone + ", altura=" + altura + ", peso=" + peso + '}';
    }

    
}
