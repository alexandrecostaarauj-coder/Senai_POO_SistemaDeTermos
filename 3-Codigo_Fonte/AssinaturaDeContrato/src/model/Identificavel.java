package model;

/**
 * Contrato (interface) de tudo que tem um ID e pode ser salvo em arquivo.
 * Paciente, Medico e Contrato implementam; os DAOs usam só isto para gerar IDs.
 */
public interface Identificavel {

    int getID();

    void setID(int ID);
}
