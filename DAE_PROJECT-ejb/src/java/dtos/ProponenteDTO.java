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
    
    public ProponenteDTO() {
    }    
    
    public ProponenteDTO(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
    
    @Override
    public void reset() {
        super.reset();
    }    

}
