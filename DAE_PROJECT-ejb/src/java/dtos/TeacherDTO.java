package dtos;

import entities.Proposta;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Student")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeacherDTO extends ProponenteDTO implements Serializable {
    
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
