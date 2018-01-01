package ejbs;

import dtos.DocumentDTO;
import dtos.DocumentoDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import dtos.TeacherDTO;
import entities.Document;
import entities.Documento;
import entities.Proposta;
import entities.Student;
import entities.Teacher;
import entities.User;
import exceptions.BibliografiaIsFullException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.ProposalAlreadyAsignedException;
import exceptions.ProposalNotAcceptedException;
import exceptions.StudentAlreadyHasAProposalAssignedException;
import exceptions.UserAlreadyHasAppliedException;
import exceptions.StudentCandidaturasFullException;
import exceptions.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("/students")
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
    
    public void addCandidaturaStudent(int propostaCode, String username) throws EntityDoesNotExistsException, StudentCandidaturasFullException, UserAlreadyHasAppliedException{
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

    @GET
    @RolesAllowed({"Student"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("findStudent/{username}")
    public StudentDTO findStudent(@PathParam("username") String username) {
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
    
    public void setProposta(String username, int propostaCode) throws EntityDoesNotExistsException, ProposalAlreadyAsignedException, 
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

    public Collection<DocumentDTO> getDocuments(String username) throws EntityDoesNotExistsException {
        try {
            List<Document> docs = em.createNamedQuery("getDocumentsOfUser", Document.class).setParameter("username", username).getResultList();
            return toDTOs(docs, DocumentDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void addDocument(String username, DocumentoDTO doc) throws EntityDoesNotExistsException {
        try {
            Student student = em.find(Student.class, username);
            if (student == null) {
                throw new EntityDoesNotExistsException("Não existe estudante com o username \"" + username + "\".");
            }
            Documento documento = new Documento(doc.getFilepath(), doc.getDesiredName(), doc.getMimeType(), student);
            em.persist(documento);
            student.addDocumento(documento);

        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void removerDocumento(String username, int id) throws EntityDoesNotExistsException {
        Student student = em.find(Student.class, username);
        if(student == null){
            throw new EntityDoesNotExistsException("O estudante com username \"" + username + "\" nao existe.");
        }
        Documento documento = em.find(Documento.class, id);
        if(documento == null){
            throw new EntityDoesNotExistsException("O documento dom id " + id + " nao existe.");
        }
        student.removeDocumento(documento);
        em.remove(documento);
    }
}
