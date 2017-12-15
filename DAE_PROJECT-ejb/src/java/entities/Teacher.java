package entities;

//import entities.UserGroup.GROUP;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "getAllTeachers", query = "SELECT t FROM Teacher t ORDER BY t.name")
public class Teacher extends User {

    private String office;
    
    @ManyToMany(mappedBy = "teachers")
    private List<Proposta> propostas;

    protected Teacher() {
        propostas = new LinkedList<>();
    }

    public Teacher(String username, String password, String name, String email, String office) {
        super(username, password,// GROUP.Teacher,
                name, email);
        this.office = office;
        propostas = new LinkedList<>();
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }
    
    public List<Proposta> getPropostas() {
        return propostas;
    }

    public void setPropostas(List<Proposta> propostas) {
        this.propostas = propostas;
    }

    public void addProposta(Proposta proposta) {
        propostas.add(proposta);
    }    
    
    public void removeProposta(Proposta proposta) {
        propostas.remove(proposta);
    }    
    
}
