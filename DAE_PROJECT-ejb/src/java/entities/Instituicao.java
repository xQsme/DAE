package entities;

//import entities.UserGroup.GROUP;
import entities.UserGroup.GROUP;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "getAllInstituicoes", query = "SELECT i FROM Instituicao i ORDER BY i.name")
public class Instituicao extends Proponente {

    private String tipo;
    
    public Instituicao() {  
    }
       
    public Instituicao(String username, String password, String name, String email, String tipo) {
        super(username, password, name, email, GROUP.Instituicao);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
