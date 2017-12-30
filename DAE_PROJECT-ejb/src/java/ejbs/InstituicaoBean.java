package ejbs;

import auxiliar.TipoDeInstituicao;
import dtos.InstituicaoDTO;
import dtos.PropostaDTO;
import dtos.TeacherDTO;
import entities.Instituicao;
import entities.Proposta;
import entities.Student;
import entities.Teacher;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
    
    public void remove(String username) throws EntityDoesNotExistsException {
        try {
            Instituicao instituicao = em.find(Instituicao.class, username);
            if (instituicao == null) {
                throw new EntityDoesNotExistsException("There is no institution with that username.");
            }
            for (Proposta p : instituicao.getPropostas()) {
                p.removeProponente(instituicao);
                em.persist(p);
            }
            em.remove(instituicao);

        } catch (EntityDoesNotExistsException e) {
            throw e;
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
        return em.createNamedQuery("getAllInstituicoes").getResultList();
    }

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
    
    public static Collection<String> getAllTiposInstituicao() {
        LinkedList<String> tipos = new LinkedList<>();
        tipos.add(TipoDeInstituicao.Associação.toString());
        tipos.add(TipoDeInstituicao.Empresa.toString());
        tipos.add(TipoDeInstituicao.Pública.toString());
        return tipos;
    }
    
    public InstituicaoDTO getInstituicao(String username) {
        try {
            Query query = em.createQuery("SELECT i FROM Instituicao i where i.username = '" + username + "'", Instituicao.class);
            ArrayList<InstituicaoDTO> instituicoes = (ArrayList<InstituicaoDTO>) toDTOs(query.getResultList(), InstituicaoDTO.class);
            return instituicoes.get(0);            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        } 
    }

    public Collection<PropostaDTO> getInstituicaoPropostas(String username) throws EntityDoesNotExistsException {
        try {
            Instituicao instituicao = em.find(Instituicao.class, username);
            
            if (instituicao == null) {
                throw new EntityDoesNotExistsException("Instituição does not exists.");
            }

            return toDTOs(instituicao.getPropostas(), PropostaDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
}
