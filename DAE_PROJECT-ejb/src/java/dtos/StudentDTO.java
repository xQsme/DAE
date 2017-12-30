package dtos;

import java.io.Serializable;

public class StudentDTO extends UserDTO implements Serializable {
    
    public StudentDTO() {
    }    
    
    public StudentDTO(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
    
    @Override
    public void reset() {
        super.reset();
    }    
    
}
