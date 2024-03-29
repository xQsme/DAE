package ejbs;

import dtos.PropostaDTO;
import dtos.TeacherDTO;
import entities.Proposta;
import entities.Teacher;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.UserAlreadyHasAppliedException;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
@Path("/teachers")
@DeclareRoles({"MembroCCP", "Instituicao", "Teacher", "Student"})
public class TeacherBean extends Bean<Teacher> {

    @PersistenceContext
    private EntityManager em;
    
    @POST
    @RolesAllowed({"MembroCCP"})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML})
    public Response create(TeacherDTO newTeacher)
            throws EntityAlreadyExistsException {
        try {
            if (em.find(User.class, newTeacher.getUsername()) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new Teacher(newTeacher.getUsername(), newTeacher.getPassword(), newTeacher.getName(), 
                    newTeacher.getEmail(), newTeacher.getOffice()));
            return Response.ok().build();
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }    
    }

    @PUT
    @RolesAllowed({"MembroCCP"})
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces({MediaType.APPLICATION_XML})
    public Response update( @PathParam("username") String username, TeacherDTO teacherDTO)
            throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Teacher teacher = em.find(Teacher.class, username);
            if (teacher == null) {
                throw new EntityDoesNotExistsException("There is no teacher with that username.");
            }
            teacher.setName(teacherDTO.getName());
            teacher.setEmail(teacherDTO.getEmail());
            teacher.setOffice(teacherDTO.getOffice());
            em.merge(teacher);
            return Response.ok().build();
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }
    
    @DELETE
    @RolesAllowed({"MembroCCP"})
    @Path("{username}")
    @Consumes(MediaType.APPLICATION_XML)
    public void remove(@PathParam("username") String username) throws EntityDoesNotExistsException {
        try {
            Teacher teacher = em.find(Teacher.class, username);
            if (teacher == null) {
                throw new EntityDoesNotExistsException("There is no teacher with that username.");
            }
            for (Proposta p : teacher.getPropostas()) {
                p.removeProponente(teacher);
                em.persist(p);
            }
            em.remove(teacher);

        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    @GET
    @RolesAllowed({"MembroCCP"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<TeacherDTO> getAllTeachers() {
        try {
            return getAll(TeacherDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    
    @Override
    protected Collection<Teacher> getAll() {
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

    public void addPropostaTeacher(int propostaCode, String username) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Teacher teacher = em.find(Teacher.class, username);
            if (teacher == null) {
                throw new EntityDoesNotExistsException("There is no teacher with that username.");
            }
            for(Proposta p : teacher.getPropostas()){
                if (p.getCode() == propostaCode) {
                    throw new UserAlreadyHasAppliedException("O professor ja está aplicado a essa Proposta!");
                }
            }
            proposta.addProponente(teacher);
            teacher.addProposta(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @POST
    @RolesAllowed({"Teacher"})
    @Path("propostas/{username}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void addPropostaTeacherRest(@PathParam("username") String username, PropostaDTO prop) throws EntityDoesNotExistsException {
        try {
            int propostaCode = prop.getCode();
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Teacher teacher = em.find(Teacher.class, username);
            if (teacher == null) {
                throw new EntityDoesNotExistsException("There is no institution with that username.");
            }
            proposta.addProponente(teacher);
            teacher.addProposta(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    @DELETE
    @RolesAllowed({"MembroCCP", "Teacher"})
    @Path("remove/proposal/{username}/{propostaCode}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void removePropostaTeacher(@PathParam("username") String username, @PathParam("propostaCode") String propostaCode) throws EntityDoesNotExistsException {
        try {
            int propostaCodeInt = Integer.valueOf(propostaCode);
            Proposta proposta = em.find(Proposta.class, propostaCodeInt);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Teacher teacher = em.find(Teacher.class, username);
            if (teacher == null) {
                throw new EntityDoesNotExistsException("There is no teacher with that username.");
            }
            proposta.removeProponente(teacher);
            teacher.removeProposta(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }    
    
    @GET
    @RolesAllowed({"MembroCCP", "Teacher"})
    @Path("{username}")
    public TeacherDTO getTeacher(@PathParam("username") String username) {
        try {
            Query query = em.createQuery("SELECT t FROM Teacher t where t.username = '" + username + "'", Teacher.class);
            ArrayList<TeacherDTO> professores = (ArrayList<TeacherDTO>) toDTOs(query.getResultList(), TeacherDTO.class);
            return professores.get(0);            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        } 
    }
    
    @GET
    @RolesAllowed({"MembroCCP", "Teacher"})
    @Path("{username}/proposals")
    public Collection<PropostaDTO> getPropostasTeacher(@PathParam("username") String username) throws EntityDoesNotExistsException{
        try {
            Teacher teacher = em.find(Teacher.class, username);
            
            if (teacher == null) {
                throw new EntityDoesNotExistsException("Teacher does not exists.");
            }

            return toDTOs(teacher.getPropostas(), PropostaDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    //NOT REST//////////////////////////////////////////////////////////////////////////////////////
    
    public void create(String username, String password, String name, String email, String office)
            throws EntityAlreadyExistsException {
        try {
            if (em.find(User.class, username) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new Teacher(username, password, name, email, office));
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
}
