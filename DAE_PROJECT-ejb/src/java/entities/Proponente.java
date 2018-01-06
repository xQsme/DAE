/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entities.UserGroup.GROUP;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author rick_sanchez
 */
@Entity
@Table(name = "PROPONENTES")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "getAllProponentes", query = "SELECT p FROM Proponente p")
public class Proponente extends User  {

    @ManyToMany(mappedBy = "proponentes")
    private List<Proposta> propostas;
    
    @ManyToMany(mappedBy = "juizes")
    private List<Prova> prova;

    public List<Prova> getProva() {
        return prova;
    }

    public void setProva(List<Prova> prova) {
        this.prova = prova;
    }
    
    public Proponente() {
    }
   
   public Proponente(String username, String password, String name, String email, GROUP group) {
        super(username, password, name, email, group);
        propostas = new LinkedList<>();
    }
    
   public List<Proposta> getPropostas() {
        return propostas;
    }

    public void setPropostas(List<Proposta> propostas) {
        this.propostas = propostas;
    }

    public void addProposta(Proposta proposta) {
        propostas.add(proposta);
    }    
    
    public void removeProposta(Proposta proposta) {
        propostas.remove(proposta);
    }    
}
