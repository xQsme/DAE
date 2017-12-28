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
    
    private List<PropostaDTO> propostas;

    public ProponenteDTO() {
    }    
    
    public ProponenteDTO(String username, String password, String name, String email, List<PropostaDTO> propostas) {
        super(username, password, name, email);
        this.propostas = propostas;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.propostas = new LinkedList<>();
    }    

    public List<PropostaDTO> getPropostas() {
        return propostas;
    }

    public void addProposta(PropostaDTO proposta) {
        propostas.add(proposta);
    }

    public void setPropostas(List<PropostaDTO> propostas) {
        this.propostas = propostas;
    }
}
