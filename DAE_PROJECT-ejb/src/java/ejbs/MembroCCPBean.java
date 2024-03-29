/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbs;

import dtos.MembroCCPDTO;
import entities.Instituicao;
import entities.MembroCCP;
import entities.Proponente;
import entities.Proposta;
import entities.Student;
import entities.Teacher;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.ProposalWasNotSubmittedByAnInstitutionException;
import exceptions.StudentHasNoProposalException;
import exceptions.TeacherAlreadyAssignedException;
import java.util.Collection;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Yvtq8
 */


@Stateless
@Path("/admin")
@DeclareRoles({"MembroCCP"})
public class MembroCCPBean extends Bean<MembroCCP>{
    @PersistenceContext
    private EntityManager em;
    
    public void create(String username, String password, String name, String email)
            throws EntityAlreadyExistsException {
        try {
            if (em.find(User.class, username) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new MembroCCP(username, password, name, email));
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @GET
    @RolesAllowed({"MembroCCP"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{username}")
    public MembroCCPDTO find(@PathParam("username") String username) throws EntityDoesNotExistsException{
        try {
            MembroCCP membroCCP = em.find(MembroCCP.class, username);
            if (membroCCP == null) {
                throw new EntityDoesNotExistsException("Invalid MembroCPP Username");
            }
            return toDTO(membroCCP, MembroCCPDTO.class); 
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    //In case the Menber doesnt leave a comment
    public void validarProposta(String username, int propostaCode, Integer validar) 
            throws EntityDoesNotExistsException{
              validarProposta(username, propostaCode, validar, null);
    }
    
    //In case the menber intends to leave a comment
    public void validarProposta(String username, int propostaCode, Integer validar, String observacao) 
            throws EntityDoesNotExistsException{
        try {      
            MembroCCP membroCCP = em.find(MembroCCP.class, username);
            if (membroCCP == null) {
                throw new EntityDoesNotExistsException("There is no MemberCCP with that username.");
            }
            
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            
            if (validar< -1 || validar > 3){
                throw new NullPointerException("Invalid State Parameter");
            }
            
            proposta.setEstado(validar);
            proposta.setObservacao(observacao);//This can be null
            
            em.merge(proposta);
        }catch (EntityDoesNotExistsException e) {
            throw e;
        }catch (Exception e) {
            throw new EJBException(e.getMessage());
        }       
    }
    
    @GET
    @RolesAllowed({"MembroCCP"})
    @Path("teacher/student/{teacher}/{student}")
    public Response addProfessorOrientador(@PathParam("teacher") String teacherUsername, @PathParam("student") String studentUsername) throws EntityDoesNotExistsException, TeacherAlreadyAssignedException, NullPointerException, ProposalWasNotSubmittedByAnInstitutionException, StudentHasNoProposalException, StudentHasNoProposalException, StudentHasNoProposalException {
        try {
            Teacher teacher = em.find(Teacher.class, teacherUsername);
            if (teacher == null) {
                throw new EntityDoesNotExistsException("There is no teacher with such username.");
            }
            
            Student student = em.find(Student.class, studentUsername);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with such username.");
            }
            for(Teacher t : student.getGuidingTeachers()){
                if (t.getUsername().equalsIgnoreCase(teacherUsername)){
                    throw new TeacherAlreadyAssignedException("This Teacher is already guiding this Student.");
                }
            }
            if(student.getProposal() == null){
                throw new StudentHasNoProposalException("This Student doesn't have a Proposal yet");
            }
            if (student.getProposal().getProponentes() == null || student.getProposal().getProponentes().size() == 0){
                throw new NullPointerException("This Proposal has no proponent");
            }
            
            Boolean proposalSubmittedByInstitution = false;
            for(Proponente prop : student.getProposal().getProponentes()){
                if (em.find(Instituicao.class, prop.getUsername()) != null){
                    proposalSubmittedByInstitution = true;
                }
            }
            if (! proposalSubmittedByInstitution) {
                throw new ProposalWasNotSubmittedByAnInstitutionException("Esta Proposta não foi submetida por uma instituição");

            }
            
            teacher.addGuidedStudent(student);
            student.addGuidingTeacher(teacher);
            
            em.merge(teacher);
            em.merge(student);

            return Response.status(200).build();
            
        } catch (EntityDoesNotExistsException | NullPointerException | ProposalWasNotSubmittedByAnInstitutionException |
                StudentHasNoProposalException | TeacherAlreadyAssignedException e) {
            //throw e;
            return Response.status(500).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    protected Collection<MembroCCP> getAll() {
        return null;
    }
}
