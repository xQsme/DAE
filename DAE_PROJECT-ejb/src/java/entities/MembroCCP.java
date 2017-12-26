/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entities.UserGroup.GROUP;
import javax.persistence.Entity;

/**
 *
 * @author Yvtq8
 */
@Entity
public class MembroCCP extends User{
    
    public MembroCCP(){
        
    }
    
    public MembroCCP(String username, String password, String name, String email) {
        super(username, password, name, email, GROUP.MembroCCP);
    }    
}
