package dtos;

import java.io.Serializable;

public class InstituicaoDTO extends UserDTO implements Serializable {
    
    private String tipo;

    public InstituicaoDTO() {
    }    
    
    public InstituicaoDTO(String username, String password, String name, String email, String tipo) {
        super(username, password, name, email);
        this.tipo = tipo;
    }
    
    @Override
    public void reset() {
        super.reset();
        setTipo(null);
    }    

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
