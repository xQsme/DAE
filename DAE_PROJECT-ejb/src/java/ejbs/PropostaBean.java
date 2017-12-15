package ejbs;

import dtos.PropostaDTO;
//import entities.Course;
import entities.Proposta;
//import entities.Student;
//import entities.Subject;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.Utils;
import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;

@Stateless
public class PropostaBean extends Bean<Proposta> {

    public void create(int code, String titulo)//, int courseCode, int courseYear, String scholarYear)
        throws EntityAlreadyExistsException, EntityDoesNotExistsException, MyConstraintViolationException {
        
        try {
            if (em.find(Proposta.class, code) != null) {
                throw new EntityAlreadyExistsException("A student with that username already exists.");
            }
            
            /*
            Course course = em.find(Course.class, courseCode);
            if (course == null) {
                throw new EntityDoesNotExistsException("There is no course with that code.");
            }*/
        
            Proposta proposta = new Proposta(code, titulo);//, course, courseYear, scholarYear);
            em.persist(proposta);
            //course.addSubject(subject);
        
        } catch (EntityAlreadyExistsException e){//| EntityDoesNotExistsException e) {
            throw e;           
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<PropostaDTO> getAllSubjects() {
        try {
            return getAll(PropostaDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @Override
    protected Collection<Proposta> getAll() {
        return em.createNamedQuery("getAllPropostas").getResultList();
    }
       
    public void remove(int code) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no propostas with that code.");
            }
          
            //proposta.getCourse().removeSubject(proposta);
            
            /*
            for (Student student : proposta.getStudents()) {
                student.removeSubject(proposta);
            }*/
            
            em.remove(proposta);
            
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }    
/*
    public Collection<PropostaDTO> getStudentSubjects(String username) throws EntityDoesNotExistsException {
        try {
            Student student = em.find(Student.class, username);
            
            if (student == null) {
                throw new EntityDoesNotExistsException("Student does not exists.");
            }

            return toDTOs(student.getSubjects(), PropostaDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<PropostaDTO> getCourseSubjects(int courseCode) throws EntityDoesNotExistsException {
        try {
            Course course = em.find(Course.class, courseCode);
            
            if (course == null) {
                throw new EntityDoesNotExistsException("Course does not exists.");
            }
            
            return toDTOs(course.getSubjects(), PropostaDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
*/
}
