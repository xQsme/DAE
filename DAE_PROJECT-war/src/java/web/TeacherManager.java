/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import auxiliar.TipoDeTrabalho;
import dtos.DocumentDTO;
import dtos.DocumentoDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import dtos.TeacherDTO;
import ejbs.ProponenteBean;
import ejbs.PropostaBean;
import ejbs.TeacherBean;
import ejbs.StudentBean;
import entities.Proposta;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import util.URILookup;

/**
 *
 * @author rick_sanchez
 */
@ManagedBean
@SessionScoped
public class TeacherManager implements Serializable {
    
    @EJB
    private ProponenteBean proponenteBean;
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private TeacherBean teacherBean;
    @EJB
    private StudentBean studentBean;
    
    @ManagedProperty(value="#{userManager}") // this references the @ManagedBean
    private UserManager userManager;
    
    private static final Logger logger = Logger.getLogger("web.TeacherManager");
    
    private TeacherDTO teacher;
    
    private PropostaDTO currentProposta;
    private PropostaDTO newProposta;
    private StudentDTO currentStudent;
    
    private UIComponent component;
    
    private Client client;

    private HttpAuthenticationFeature feature;    
    
    public TeacherManager() {
        newProposta = new PropostaDTO();
        client = ClientBuilder.newClient();
    }
    
    @PostConstruct
    public void Init(){
        feature = HttpAuthenticationFeature.basic(userManager.getUsername(), userManager.getPassword());
        client.register(feature);
        setUpTeacher();
    }
    
    public List<PropostaDTO> getAllPropostas() {
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/propostas")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<PropostaDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING PROPOSTAS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
        }
    }
    
    public Collection<PropostaDTO> getTeacherPropostas(){
        try{
            return client.target(URILookup.getBaseAPI())
                .path("/teachers")
                .path(teacher.getUsername())
                .path("proposals")
                .request(MediaType.APPLICATION_XML)
                .get(new GenericType<List<PropostaDTO>>() {});
        }catch(Exception e){
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
        }
        return null;
    }
    
    public Boolean isTeacherProponenteProposta(int codeProposta){
        for (PropostaDTO p : getTeacherPropostas()){
            if (p.getCode() == codeProposta) {
                return true;
            }
        }
        return false;
    }
    
    public Collection<String> getAllTiposTrabalho() {
        try {
            LinkedList<String> tiposTrabalho = new LinkedList<>();
            tiposTrabalho.add(TipoDeTrabalho.Dissertação.toString());
            tiposTrabalho.add(TipoDeTrabalho.Estágio.toString());
            tiposTrabalho.add(TipoDeTrabalho.Projeto.toString());
            return tiposTrabalho;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public void removerProposta(){
        try {
            client.target(URILookup.getBaseAPI())
                .path("/teachers/remove/proposal")
                .path(teacher.getUsername())
                .path(Integer.toString(currentProposta.getCode()))
                .request(MediaType.APPLICATION_XML)
                .delete();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! " + e.toString(), logger);
        }
    }
    /*
    public void adicionarProposta(){
        try {
            //teacherBean.addPropostaTeacher(currentProposta.getCode(), teacher.getUsername());
            
            client.target(URILookup.getBaseAPI())
                .path("/teachers/add/proposal")
                .path(Integer.toString(currentProposta.getCode()))
                .request(MediaType.APPLICATION_XML)                
                .post(Entity.xml(teacher.getUsername()));
        }
        catch (Exception e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
        }
    }*/

    public Collection<ProponenteDTO> getCurrentPropostaProponentes(){
        try {
            return client.target(URILookup.getBaseAPI())
                .path("/propostas/proponentes")
                .path(Integer.toString(currentProposta.getCode()))
                .request(MediaType.APPLICATION_XML)
                .get(new GenericType<List<ProponenteDTO>>() {});
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public Collection<StudentDTO> getCurrentPropostaCandidatos(){
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/propostas/students")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<StudentDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING STUDENTS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }

    public PropostaDTO getCurrentProposta() {
        return currentProposta;
    }

    public void setCurrentProposta(PropostaDTO currentProposta) {
        this.currentProposta = currentProposta;
    }    

    public PropostaDTO getNewProposta() {
        return newProposta;
    }

    public void setNewProposta(PropostaDTO newProposta) {
        this.newProposta = newProposta;
    }
    
    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    private void setUpTeacher() {
        try {
            teacher = client.target(URILookup.getBaseAPI())
                    .path("/teachers")
                    .path(userManager.getUsername())
                    .request(MediaType.APPLICATION_XML)
                    .get(TeacherDTO.class);
        } catch (Exception e) {
            logger.info(e.toString());
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO teacher) {
        this.teacher = teacher;
    }
    
    public String updateProposta() {
        try {
            client.target(URILookup.getBaseAPI())
                    .path("/propostas")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .put(Entity.xml(currentProposta));
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        return "/teacher/propostas/mine.xhtml?faces-redirect=true";
    }
    
    
    public String createProposta() {
        try {
            logger.info(newProposta.getCode() + "");
            Response response = client.target(URILookup.getBaseAPI())
                .path("/propostas")
                .request(MediaType.APPLICATION_XML)
                .post(Entity.xml(newProposta));
            
            Proposta p = new Proposta();
            if (response.hasEntity()) {
                logger.info("has entity");
                p = response.readEntity(Proposta.class);
                logger.info(response.readEntity(Proposta.class) +  "");
            }
            logger.info("has no entity");
            /*Object o = 
            logger.info(o.toString());
            
            
            */
            newProposta.setCode(p.getCode());
            client.target(URILookup.getBaseAPI())
                .path("/teachers/propostas")
                .path(teacher.getUsername())
                .request(MediaType.APPLICATION_XML)
                .post(Entity.xml(newProposta));
            newProposta.reset();
        }catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
            return null;
        }
        return "/teacher/propostas/mine.xhtml?faces-redirect=true";
    }
    
    public void removeProposta(){ 
        try {
            client.target(URILookup.getBaseAPI())
                .path("/teachers/remove/proposal")
                .path(teacher.getUsername())
                .path(currentProposta.getCode()+"")
                .request(MediaType.APPLICATION_XML)
                .delete();
            setUpTeacher();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! " + e, logger);
        }
    }
    
    public DocumentDTO getCurrentPropostaAta(){
        try {
            for(DocumentDTO d : getCurrentPropostaDocumentos()){
                if(d.isAta()){
                    return d;
                }
            }
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return null;
    }
    
    public List<DocumentDTO> getCurrentPropostaDocumentos(){
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/propostas/documents")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<DocumentDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING PROPOSTA DOCUMENTS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return new LinkedList<>();
        }
    }
    
    public List<DocumentoDTO> getStudentDocumentos(){
        try {
            return client.target(URILookup.getBaseAPI())
                .path("/students/documentos")
                .path(currentStudent.getUsername())
                .request(MediaType.APPLICATION_XML)
                .get(new GenericType<List<DocumentoDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING STUDENT DOCUMENTS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return new LinkedList<>();
        }
    }
    public StudentDTO getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(StudentDTO currentStudent) {
        this.currentStudent = currentStudent;
    }
    
}
