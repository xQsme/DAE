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
import javax.persistence.Column;
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
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "getAllStudents", query = "SELECT s FROM Student s")
public class Student extends User  {

    @ManyToMany(mappedBy = "candidatos")
    private List<Proposta> candidaturas;
    
    //@Column
    //private Proposta projetoMestrado;

    public Student() {
    }
   
   public Student(String username, String password, String name, String email) {
        super(username, password, name, email, GROUP.Student);
        candidaturas = new LinkedList<>();
    }
    
   public List<Proposta> getCandidaturas() {
        return candidaturas;
    }

    public void setCandidaturas(List<Proposta> candidaturas) {
        this.candidaturas = candidaturas;
    }

    public void addCandidatura(Proposta candidatura) {
        candidaturas.add(candidatura);
    }    
    
    public void removeCandidatura(Proposta candidatura) {
        candidaturas.remove(candidatura);
    }    
}
