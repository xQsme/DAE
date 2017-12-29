/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import entities.Proposta;
import java.io.Serializable;
import java.util.List;

public class MembroCCPDTO extends UserDTO implements Serializable {
    
    public MembroCCPDTO() {
    }    
    
    public MembroCCPDTO(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
    
    @Override
    public void reset() {
        super.reset();
    }      
}
