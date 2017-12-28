package dtos;

import entities.Proposta;
import java.io.Serializable;
import java.util.List;

public class TeacherDTO extends ProponenteDTO implements Serializable {
    
    private String office;

    public TeacherDTO() {
    }    
    
    public TeacherDTO(String username, String password, String name, String email, List<PropostaDTO> propostas, String office) {
        super(username, password, name, email, propostas);
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
