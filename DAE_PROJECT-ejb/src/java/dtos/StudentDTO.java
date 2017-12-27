package dtos;

import entities.Proposta;
import java.io.Serializable;
import java.util.List;

public class StudentDTO extends UserDTO implements Serializable {

    private List<PropostaDTO> candidaturas;
    
    public StudentDTO() {
    }    
    
    public StudentDTO(String username, String password, String name, String email, List<PropostaDTO> candidaturas) {
        super(username, password, name, email);
        setCandidaturas(candidaturas);
    }
    
    @Override
    public void reset() {
        super.reset();
    }    

    public List<PropostaDTO> getCandidaturas() {
        return candidaturas;
    }

    public void setCandidaturas(List<PropostaDTO> candidaturas) {
        this.candidaturas = candidaturas;
    }
    
    
}
