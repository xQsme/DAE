/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import entities.Proposta;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author The Law
 */
public class ProponenteDTO extends UserDTO implements Serializable {
    
    private List<Proposta> propostas;

    public ProponenteDTO() {
    }    
    
    public ProponenteDTO(String username, String password, String name, String email, List<Proposta> propostas) {
        super(username, password, name, email);
        this.propostas = propostas;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.propostas = new LinkedList<>();
    }    

    public List<Proposta> getPropostas() {
        return propostas;
    }

    public void addProposta(Proposta proposta) {
        propostas.add(proposta);
    }
}
