package dtos;

import java.io.Serializable;

public class TeacherDTO extends UserDTO implements Serializable {
    
    private String office;

    public TeacherDTO() {
    }    
    
    public TeacherDTO(String username, String password, String name, String email, String office) {
        super(username, password, name, email);
        this.office = office;
    }
    
    @Override
    public void reset() {
        super.reset();
        setOffice(null);
    }    

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }
}
