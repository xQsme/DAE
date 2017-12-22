package ejbs;

import dtos.StudentDTO;
import dtos.TeacherDTO;
import entities.Proposta;
import entities.Student;
import entities.Teacher;
import entities.User;
import exceptions.BibliografiaIsFullException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.StudentCandidaturasFullException;
import exceptions.Utils;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

@Stateless
public class StudentBean extends Bean<Student> {

    @PersistenceContext
    private EntityManager em;

    public void create(String username, String password, String name, String email) throws EntityAlreadyExistsException {
        try {
            if (em.find(User.class, username) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new Student(username, password, name, email));
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void update(String username, String name, String email)
            throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with that username.");
            }
            student.setName(name);
            student.setEmail(email);
            em.merge(student);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<StudentDTO> getAllTeachers() {
        try {
            return getAll(StudentDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @Override
    protected Collection<Student> getAll() {
        return em.createNamedQuery("getAllStudents").getResultList();
    }
/*
    public Collection<TeacherDTO> getSujectTeachers(int subjectCode) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, subjectCode);
            
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no subject with that code.");
            }
            
            return toDTOs(proposta.getProponente(), TeacherDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<TeacherDTO> getTeachersNotInSubject(int subjectCode) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, subjectCode);
            
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no subject with that code.");
            }
            
            List<Teacher> teachers = (List<Teacher>) em.createNamedQuery("getAllTeachers").getResultList();
            List<Teacher> teacher = proposta.getProponente();
            teachers.removeAll(teacher);
            
            return toDTOs(teachers, TeacherDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }*/
    
    public void addCandidaturaStudent(int propostaCode, String username) throws EntityDoesNotExistsException, StudentCandidaturasFullException{
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with that username.");
            }
            if (student.getCandidaturas().size() > 4) {
                throw new StudentCandidaturasFullException ("O aluno so se pode candidatar a 5 candidaturas no máximo!");
            }
            proposta.addStudent(student);
            student.addCandidatura(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void removePropostaStudent(int propostaCode, String username) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with that username.");
            }
            proposta.removeStudent(student);
            student.removeCandidatura(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }    
}
