package entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PROPOSTAS")//, uniqueConstraints = @UniqueConstraint(columnNames = { "TITULO" }))//, "COURSE_CODE", "SCHOLAR_YEAR" }))
@NamedQuery(name = "getAllPropostas", query = "SELECT p FROM Proposta p ORDER BY p.titulo")//.name, s.courseYear DESC, s.scholarYear, s.name")
public class Proposta implements Serializable {
    
 /* 
    Uma proposta de trabalho de Mestrado deve inclui, entre outros:
    um tipo de trabalho (dissertação, projeto ou estágio), 
    um título, 
    as áreas científicas em que o trabalho incide, 
    os seus proponentes, 
    um resumo do trabalho a desenvolver,
    os objetivos a cumprir, 
    uma bibliografia com, no máximo, 5 referências, 
    um plano de trabalhos,
    o local de realização do trabalho, 
    os requisitos considerados fundamentais para que o trabalho tenha sucesso,
    um orçamento (incluindo itens para bibliografia, 
    aquisição de material,
    aquisição de equipamento ou outros) e os apoios (financeiros ou de outro tipo) que Estudante poderá usufruir
*/
    @Id
    private int code;
    
    @Column(nullable = false)
    private String titulo;
    
    /*
    @ManyToOne
    @JoinColumn(name = "COURSE_CODE", nullable = false)
    private Course course;
    
    @Column(name = "COURSE_YEAR")
    private int courseYear;
    
    @Column(name = "SCHOLAR_YEAR", nullable = false)
    private String scholarYear;
    
    @ManyToMany
    @JoinTable(name = "SUBJECT_STUDENT",
            joinColumns = @JoinColumn(name = "SUBJECT_CODE", referencedColumnName = "CODE"),
            inverseJoinColumns = @JoinColumn(name = "STUDENT_USERNAME", referencedColumnName = "USERNAME"))
    private List<Student> students;
*/
    
    @ManyToMany
    @JoinTable(name = "PROPOSTA_TEACHER",
            joinColumns = @JoinColumn(name = "PROPOSTA_CODE", referencedColumnName = "CODE"),
            inverseJoinColumns = @JoinColumn(name = "TEACHER_USERNAME", referencedColumnName = "USERNAME"))
    private List<Teacher> teachers;

    public Proposta() {
        //students = new LinkedList<>();
        teachers = new LinkedList<>();
    }

    public Proposta(int code, String titulo){//, Course course, int courseYear, String scholarYear) {
        this.code = code;
        this.titulo = titulo;
        /*
        this.course = course;
        this.courseYear = courseYear;
        this.scholarYear = scholarYear;
        students = new LinkedList<>();
*/
        teachers = new LinkedList<>();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
/*
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(int courseYear) {
        this.courseYear = courseYear;
    }

    public String getScholarYear() {
        return scholarYear;
    }

    public void setScholarYear(String scholarYear) {
        this.scholarYear = scholarYear;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
    
    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }
    */
    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
    
    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
    }
}
