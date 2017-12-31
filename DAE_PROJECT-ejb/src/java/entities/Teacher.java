package entities;

//import entities.UserGroup.GROUP;
import entities.UserGroup.GROUP;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "getAllTeachers", query = "SELECT t FROM Teacher t ORDER BY t.name")
public class Teacher extends Proponente {

    private String office;
    
    //@ManyToMany(mappedBy = "teachers")
    //private List<Proposta> propostas;
    
    @ManyToMany
    @JoinTable(name = "TEACHER_ORIENTADOR_STUDENT", joinColumns = @JoinColumn(name = "TEACHER_USERNAME", referencedColumnName = "USERNAME"), inverseJoinColumns = @JoinColumn(name = "STUDENT_USERNAME", referencedColumnName = "USERNAME"))
    private Collection<Student> guidedStudents;

    public Teacher() {
        guidedStudents = new LinkedList<>();
    }
       
    public Teacher(String username, String password, String name, String email, String office) {
        super(username, password, name, email, GROUP.Teacher);
        this.office = office;
        guidedStudents = new LinkedList<>();
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }
    
    public Collection<Student> getGuidedStudents() {
        return guidedStudents;
    }

    public void setGuidedStudents(List<Student> guidedStudents) {
        this.guidedStudents = guidedStudents;
    }
    
    public void addGuidedStudent(Student student) {
        this.guidedStudents.add(student);
    }
    
    public void removeGuidedStudent(Student student) {
        this.guidedStudents.remove(student);
    }
}
