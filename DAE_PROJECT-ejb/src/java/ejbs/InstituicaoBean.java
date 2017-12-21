package ejbs;

import dtos.InstituicaoDTO;
import dtos.TeacherDTO;
import entities.Instituicao;
import entities.Proposta;
import entities.Teacher;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.Utils;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

@Stateless
public class InstituicaoBean extends Bean<Instituicao> {

    @PersistenceContext
    private EntityManager em;

    public void create(String username, String password, String name, String email, String tipo)
            throws EntityAlreadyExistsException {
        try {
            if (em.find(User.class, username) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new Instituicao(username, password, name, email, tipo));
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void update(String username, String name, String email, String tipo)
            throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Instituicao instituicao = em.find(Instituicao.class, username);
            if (instituicao == null) {
                throw new EntityDoesNotExistsException("There is no institution with that username.");
            }
            instituicao.setName(name);
            instituicao.setEmail(email);
            instituicao.setTipo(tipo);
            em.merge(instituicao);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<InstituicaoDTO> getAllInstitutions() {
        try {
            return getAll(InstituicaoDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @Override
    protected Collection<Instituicao> getAll() {
        return em.createNamedQuery("getAllTeachers").getResultList();
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

    public void addPropostaInstituicao(int propostaCode, String username) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Instituicao instituicao = em.find(Instituicao.class, username);
            if (instituicao == null) {
                throw new EntityDoesNotExistsException("There is no institution with that username.");
            }
            proposta.addProponente(instituicao);
            instituicao.addProposta(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void removePropostaInstituicao(int propostaCode, String username) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Instituicao instituicao = em.find(Instituicao.class, username);
            if (instituicao == null) {
                throw new EntityDoesNotExistsException("There is no institution with that username.");
            }
            proposta.removeProponente(instituicao);
            instituicao.removeProposta(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }    
}