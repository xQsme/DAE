/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.persistence.Entity;

/**
 *
 * @author Yvtq8
 */
@Entity
public class MenbroCCP extends User{
    
    public MenbroCCP(){
        
    }
    
    public MenbroCCP(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
    
    public void aceitarProposta(Proposta proposta, Boolean validar){
        //Proposta.valido        
    }
}
