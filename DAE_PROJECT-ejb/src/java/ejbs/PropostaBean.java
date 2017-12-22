package ejbs;

import dtos.PropostaDTO;
import entities.Proposta;
import exceptions.BibliografiaIsFullException;
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

    public void create(int code, String titulo, String tipoDeTrabalho, String resumo, String planoDeTrabalho, String local,String orcamento, String apoios)
        throws EntityAlreadyExistsException, EntityDoesNotExistsException, MyConstraintViolationException {
        
        try {
            if (em.find(Proposta.class, code) != null) {
                throw new EntityAlreadyExistsException("A proposal with that code already exists.");
            }
            
            Proposta proposta = new Proposta(code, titulo, tipoDeTrabalho, resumo, planoDeTrabalho, local, orcamento, apoios);
            em.persist(proposta);
        
        } catch (EntityAlreadyExistsException e){//| EntityDoesNotExistsException e) {
            throw e;           
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<PropostaDTO> getAllPropostas() {
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
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }            
            em.remove(proposta);
            
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }    
    
    public void addAreaCientifica(int propostaCode, String areaCientifica) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.addAreaCientifica(areaCientifica);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    
    public void addObjetivo(int propostaCode, String objetivo) throws EntityDoesNotExistsException{
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.addObjetivo(objetivo);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void addReferencia(int propostaCode, String referencia) throws EntityDoesNotExistsException, BibliografiaIsFullException{
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            if (proposta.getBibliografia().size() > 4) {
                throw new BibliografiaIsFullException("A Bibliografia so pode ter 5 referencia no maximo!");
            }
            proposta.addReferencia(referencia);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void addRequsito(int propostaCode, String requisito) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.addRequisito(requisito);
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
