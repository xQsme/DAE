package entities;

//import entities.UserGroup.GROUP;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "getAllTeachers", query = "SELECT t FROM Teacher t ORDER BY t.name")
public class Teacher extends Proponente {

    private String office;
    
    //@ManyToMany(mappedBy = "teachers")
    //private List<Proposta> propostas;

    public Teacher() {
        //super(null, null, null, null);   
    }
       
    public Teacher(String username, String password, String name, String email, String office) {
        super(username, password,// GROUP.Teacher,
                name, email);
        this.office = office;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }
}
