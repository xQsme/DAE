package ejbs;

import auxiliar.TipoDeInstituicao;
import dtos.InstituicaoDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import entities.Instituicao;
import entities.Proposta;
import entities.Student;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/instituicoes")
@DeclareRoles({"MembroCCP", "Instituicao", "Teacher", "Student"})
public class InstituicaoBean extends Bean<Instituicao> {

    @PersistenceContext
    private EntityManager em;
    
    @POST
    @RolesAllowed({"MembroCCP"})
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response create(InstituicaoDTO instituicao)
            throws EntityAlreadyExistsException {
        try {
            if (em.find(User.class, instituicao.getUsername()) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new Instituicao(instituicao.getUsername(), instituicao.getPassword(), 
                    instituicao.getName(), instituicao.getEmail(), instituicao.getTipo()));
            return Response.ok().build();
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }  
    
    @PUT
    @Path("/{username}")
    @RolesAllowed({"MembroCCP"})
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response update(@PathParam("username") String username, InstituicaoDTO instituicaoDTO)
            throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Instituicao instituicao = em.find(Instituicao.class, username);
            if (instituicao == null) {
                throw new EntityDoesNotExistsException("There is no institution with that username.");
            }
            instituicao.setName(instituicaoDTO.getName());
            instituicao.setEmail(instituicaoDTO.getEmail());
            instituicao.setTipo(instituicaoDTO.getTipo());
            em.merge(instituicao);
            return Response.ok().build();
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }
    
    @DELETE
    @RolesAllowed({"MembroCCP"})
    @Path("/{username}")
    @Consumes({MediaType.APPLICATION_XML})
    public Response remove(@PathParam("username") String username) throws EntityDoesNotExistsException {
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
            return Response.ok().build();
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }

    
    @GET
    @RolesAllowed({"MembroCCP"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
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

    @POST
    @RolesAllowed({"Instituicao"})
    @Path("propostas/{username}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void addPropostaInstituicao(@PathParam("username") String username, PropostaDTO prop) throws EntityDoesNotExistsException {
        try {
            int propostaCode = prop.getCode();
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

    @DELETE
    @RolesAllowed({"Instituicao"})
    @Path("removerProposta/{username}/{id}")
    public void removePropostaInstituicao(@PathParam("id") int propostaCode, @PathParam("username") String username) throws EntityDoesNotExistsException {
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
    
    @GET
    @RolesAllowed({"Instituicao"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{username}")
    public InstituicaoDTO getInstituicao(@PathParam("username") String username) {
        try {
            Query query = em.createQuery("SELECT i FROM Instituicao i where i.username = '" + username + "'", Instituicao.class);
            ArrayList<InstituicaoDTO> instituicoes = (ArrayList<InstituicaoDTO>) toDTOs(query.getResultList(), InstituicaoDTO.class);
            return instituicoes.get(0);            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        } 
    }

    @GET
    @RolesAllowed({"Instituicao"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("propostas/{username}")
    public Collection<PropostaDTO> getInstituicaoPropostas(@PathParam("username") String username) throws EntityDoesNotExistsException {
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
    
    
    //NOT REST//////////////////////////////////////////////////////////////////////////////////////
    
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
}
