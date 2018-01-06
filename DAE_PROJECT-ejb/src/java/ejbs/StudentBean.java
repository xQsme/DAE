package ejbs;

import dtos.DocumentoDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import entities.Documento;
import entities.Proposta;
import entities.Student;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.ProposalAlreadyAsignedException;
import exceptions.ProposalNotAcceptedException;
import exceptions.StudentAlreadyHasAProposalAssignedException;
import exceptions.UserAlreadyHasAppliedException;
import exceptions.StudentCandidaturasFullException;
import exceptions.Utils;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/students")
@DeclareRoles({"MembroCCP", "Instituicao", "Teacher", "Student"})
public class StudentBean extends Bean<Student> {
    
    private static final Logger logger = Logger.getLogger("ejb.StudentBean");

    @PersistenceContext
    private EntityManager em;
 
    @POST
    @RolesAllowed("MembroCCP")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response create(StudentDTO student) throws EntityAlreadyExistsException {
        try {     
            if (em.find(User.class, student.getUsername()) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new Student(student.getUsername(), student.getPassword(), student.getName(), student.getEmail()));
            return Response.ok().build();
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }
    
    
    
    @PUT
    @Path("/{username}")
    @RolesAllowed({"MembroCCP","Student"})
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response update(@PathParam("username") String username, StudentDTO studentDTO) throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with that username.");
            }
            student.setName(studentDTO.getName());
            student.setEmail(studentDTO.getEmail());
            em.merge(student);
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
    @Path("/{username}")
    @RolesAllowed({"MembroCCP"})
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response remove(@PathParam("username") String username) throws EntityDoesNotExistsException {
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
    
   
    
    @POST
    @RolesAllowed("Student")
    @Path("proposta/{username}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addCandidatura(@PathParam("username") String username, PropostaDTO prop) throws EntityDoesNotExistsException, StudentCandidaturasFullException, UserAlreadyHasAppliedException{
        try {
            Proposta proposta = em.find(Proposta.class, prop.getCode());
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
                if (p.getCode() == prop.getCode()) {
                    throw new UserAlreadyHasAppliedException("O aluno ja se candidatou a essa Proposta!");
                }
            }
            proposta.addStudent(student);
            student.addCandidatura(proposta);
            return Response.ok().build();
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }

    @DELETE
    @RolesAllowed({"Student"})
    @Path("proposta/{username}/{code}")
    public void removerCandidatura(@PathParam("code") int propostaCode, @PathParam("username") String username) throws EntityDoesNotExistsException {
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



    @GET   //got error 500, because i was also allowing memberCCP(not sure if was because he doesnt have permitions yet)
    @RolesAllowed("Student")    // avoid do @RolesAllowed({a,b}) for now
    @Path("propostas/{username}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<PropostaDTO> getPropostas(@PathParam("username") String username) {
        try {
            Query query = em.createNativeQuery("SELECT * FROM DAE.PROPOSTA p WHERE p.code in (Select proposta_code FROM DAE.PROPOSTA_STUDENT where proponente_username = '" + username + "' )", Proposta.class);
            return toDTOs(query.getResultList(), PropostaDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    @GET
    @RolesAllowed({"Student"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{username}")
    public StudentDTO findStudent(@PathParam("username") String username) throws EntityDoesNotExistsException {
        try {
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no user with such username. " + username );
            }
            return toDTO(student, StudentDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void removeCandidatura(String username, int code) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @GET
    @RolesAllowed({"MembroCCP"})
    @Path("proposta/{username}/{code}")
    public void setProposta(@PathParam("username") String username, @PathParam("code") int propostaCode) throws EntityDoesNotExistsException, ProposalAlreadyAsignedException, 
            ProposalNotAcceptedException, StudentAlreadyHasAProposalAssignedException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with that username.");
            }
            if(proposta.getEstado() != 1){
                throw new ProposalNotAcceptedException("The proposal has not been accepted yet.");
            }
            if (proposta.getStudent() != null) {
                throw new ProposalAlreadyAsignedException("The proposal has already been assigned!");
            }
            if (student.getProposal() != null) {
                throw new StudentAlreadyHasAProposalAssignedException("The student already has a Proposal Assigned!");
            }
            proposta.setStudent(student);
            student.setProposal(proposta);

            em.merge(student);
            em.merge(proposta);
            
        } catch (EntityDoesNotExistsException | ProposalAlreadyAsignedException | ProposalNotAcceptedException | StudentAlreadyHasAProposalAssignedException | NullPointerException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    @GET
    @RolesAllowed({"Student", "Instituicao", "Teacher"})
    @Path("documentos/{username}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<DocumentoDTO> getDocumentos(@PathParam("username") String username) throws EntityDoesNotExistsException {
        try {
            List<Documento> docs = em.createNamedQuery("getDocumentosOfUser", Documento.class).setParameter("username", username).getResultList();
            return toDTOs(docs, DocumentoDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    @POST
    @RolesAllowed({"Student"})
    @Path("documento/{username}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addDocumento(@PathParam("username") String username, DocumentoDTO doc) throws EntityDoesNotExistsException {
        try {
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("Não existe estudante com o username \"" + username + "\".");
            }
            Documento documento = new Documento(doc.getFilepath(), doc.getDesiredName(), doc.getMimeType(), student);
            em.persist(documento);
            student.addDocumento(documento);
            return Response.ok().build();
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }

    @DELETE
    @RolesAllowed({"Student"})
    @Path("documento/{id}")
    public Response removerDocumento(@PathParam("id") int id) throws EntityDoesNotExistsException {
        try{
            Documento documento = em.find(Documento.class, id);
            if(documento == null){
                throw new EntityDoesNotExistsException("O documento com id " + id + " nao existe.");
            }

            documento.getStudent().removeDocumento(documento);
            em.remove(documento);
            return Response.ok().build();
        }catch(Exception e){
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
    }
    
    
    //NOT REST//////////////////////////////////////////////////////////////////////////////////////
    
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
     
     public void addCandidatura(@PathParam("username") String username, int propCode) throws EntityDoesNotExistsException, StudentCandidaturasFullException, UserAlreadyHasAppliedException{
        try {
            Proposta proposta = em.find(Proposta.class, propCode);
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
                if (p.getCode() == propCode) {
                    throw new UserAlreadyHasAppliedException("O aluno ja se candidatou a essa Proposta!");
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
     
     
}
