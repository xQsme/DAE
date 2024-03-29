package dtos;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Instituicao")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstituicaoDTO extends ProponenteDTO implements Serializable {
    
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
        //return "ola mundo";
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
