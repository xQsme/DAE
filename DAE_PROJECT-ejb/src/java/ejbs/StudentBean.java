package ejbs;

import dtos.PropostaDTO;
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
import exceptions.StudentAlreadyHasAppliedException;
import exceptions.StudentCandidaturasFullException;
import exceptions.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

@Stateless
public class StudentBean extends Bean<Student> {
    
    private static final Logger logger = Logger.getLogger("ejb.StudentBean");

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

    public void update(String username, String name, String email) throws EntityDoesNotExistsException, MyConstraintViolationException {
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
   
    public void remove(String username) throws EntityDoesNotExistsException {
        try {
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with that username.");
            }
            for (Proposta p : student.getCandidaturas()) {
                p.removeStudent(student);
                em.persist(p);
            }
            em.remove(student);

        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<StudentDTO> getAllStudents() {
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
    
    public void addCandidaturaStudent(int propostaCode, String username) throws EntityDoesNotExistsException, StudentCandidaturasFullException, StudentAlreadyHasAppliedException{
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
            for(Proposta p : student.getCandidaturas()){
                if (p.getCode() == propostaCode) {
                    throw new StudentAlreadyHasAppliedException("O aluno ja se candidatou a essa Proposta!");
                }
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

    public Collection<StudentDTO> getPropostaCandidatos(int code) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Student does not exists.");
            }

            return toDTOs(proposta.getCandidatos(), StudentDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<PropostaDTO> getCandidaturas(String username) {
        try {
            Query query = em.createNativeQuery("SELECT * FROM DAE.PROPOSTA p WHERE p.code in (Select proposta_code FROM DAE.PROPOSTA_STUDENT where proponente_username = '" + username + "' )", Proposta.class);
            List<Proposta> candidaturas = query.getResultList();
            return PropostaBean.toPropostaDTOcollection(query.getResultList());
            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        } 
    }

    public StudentDTO getStudent(String username) {
        try {
            Query query = em.createQuery("SELECT s FROM Student s where s.username = '" + username + "'", Student.class);
            ArrayList<StudentDTO> estudantes = (ArrayList<StudentDTO>) toDTOs(query.getResultList(), StudentDTO.class);
            return estudantes.get(0);            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        } 
    }

    public void removeCandidatura(String username, int code) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
