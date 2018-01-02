/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author The Law
 */
@XmlRootElement(name = "Proponente")
@XmlAccessorType(XmlAccessType.FIELD)
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
