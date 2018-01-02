/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import exceptions.NoDocumentsException;
import exceptions.AlreadyAppliedToProposalException;
import exceptions.CannotApplyToProposalException;
import exceptions.ProposalStateDoesNotAllowException;
import dtos.DocumentDTO;
import dtos.DocumentoDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import ejbs.ProponenteBean;
import ejbs.PropostaBean;
import ejbs.StudentBean;
import exceptions.EntityDoesNotExistsException;
import exceptions.StudentCandidaturasFullException;
import exceptions.UserAlreadyHasAppliedException;
import java.io.File;
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
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import util.URILookup;

/**
 *
 * @author rick_sanchez
 */
@ManagedBean
@SessionScoped
public class StudentManager implements Serializable {
    
    private Client client;
    private HttpAuthenticationFeature feature;
    
    @ManagedProperty(value="#{userManager}")
    private UserManager userManager;
        
    @ManagedProperty(value = "#{uploadManager}")
    private UploadManager uploadManager;
    
    @EJB
    private ProponenteBean proponenteBean;
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private StudentBean studentBean;
    
    private static final Logger logger = Logger.getLogger("web.StudentManager");
    
    private StudentDTO student;
    private List<DocumentDTO> documents;
    private DocumentDTO document;
    private DocumentoDTO documento;
    private PropostaDTO currentProposta;
    private DocumentDTO currentDocumento;
    
    private UIComponent component;
    
    public StudentManager() {
        client = ClientBuilder.newClient();
    }
    
    @PostConstruct
    public void Init(){
        feature = HttpAuthenticationFeature.basic(userManager.getUsername(), userManager.getPassword());
        client.register(feature);
        setUpStudent();
    }
    
    public List<PropostaDTO> getAllPropostas() {
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/propostas/allPropostas")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<PropostaDTO>>() {});
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public Collection<PropostaDTO> getCandidaturas() {
        return studentBean.getCandidaturas(student.getUsername());
    }
    
    public Boolean isStudentCandidatoProposta(int codeProposta){
        logger.info("codigo = " + codeProposta);
        try{
            for (PropostaDTO p : propostaBean.getStudentPropostas(student.getUsername())){
                if (p.getCode() == codeProposta) {
                    return true;
                }
            }
        }catch(Exception e){
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
        }
        return false;
    }
    
    public void removerCandidatura(){
        try {
            studentBean.removePropostaStudent(currentProposta.getCode(), student.getUsername());
            for(PropostaDTO p : getCandidaturas()){
                if (p.getCode() == currentProposta.getCode()) {
                    studentBean.removePropostaStudent(p.getCode(), student.getUsername());                    
                }
            }

        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public void candidatar(){
        try {
            if(studentBean.getDocuments(student.getUsername()).isEmpty()){
                throw new NoDocumentsException();
            }else if(studentBean.getCandidaturas(student.getUsername()).contains(currentProposta)){
                throw new AlreadyAppliedToProposalException();
            }else if(studentBean.getCandidaturas(student.getUsername()).size() >= 5){
                throw new CannotApplyToProposalException();
            }else if(currentProposta.getIntEstado()!=1){
                throw new ProposalStateDoesNotAllowException();
            }
            studentBean.addCandidaturaStudent(currentProposta.getCode(), student.getUsername());
        }
        catch (AlreadyAppliedToProposalException | CannotApplyToProposalException | EntityDoesNotExistsException | NoDocumentsException | ProposalStateDoesNotAllowException | StudentCandidaturasFullException | UserAlreadyHasAppliedException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
        }
    }

    public Collection<ProponenteDTO> getCurrentPropostaProponentes(){
        try {
            return proponenteBean.getPropostaProponentes(currentProposta.getCode());
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public Collection<StudentDTO> getCurrentPropostaCandidatos(){
        try {
            return studentBean.getPropostaCandidatos(currentProposta.getCode());
        } catch (Exception e) {
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
    
    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    private void setUpStudent() {
        try {
            student = client.target(URILookup.getBaseAPI())
                    .path("/students/findStudent")
                    .path(userManager.getUsername())
                    .request(MediaType.APPLICATION_XML)
                    .get(StudentDTO.class);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public UploadManager getUploadManager() {
        return uploadManager;
    }

    public void setUploadManager(UploadManager uploadManager) {
        this.uploadManager = uploadManager;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }
    
    public DocumentDTO getCurrentDocumento() {
        return currentDocumento;
    }

    public void setCurrentDocumento(DocumentDTO currentDocumento) {
        this.currentDocumento = currentDocumento;
    }
    
    public Collection<DocumentDTO> getCurrentPropostaDocumentos(){
        LinkedList<DocumentDTO> documents = new LinkedList<>();
        try {
            for(DocumentDTO d : propostaBean.getDocuments(currentProposta.getCode())){
                if(!d.isAta()){
                    documents.add(d);
                }
            }
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return documents;
    }
    
    public Collection<DocumentDTO> getUserDocumentos(){
        try {
            return studentBean.getDocuments(student.getUsername());
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public DocumentDTO getCurrentPropostaAta(){
        try {
            for(DocumentDTO d : propostaBean.getDocuments(currentProposta.getCode())){
                if(d.isAta()){
                    return d;
                }
            }
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return null;
    }
    
    public void uploadDocument() {
        try {
            document = new DocumentDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType(), false);

            System.out.println(client.target(URILookup.getBaseAPI())
                    .path("/propostas/addDocument")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .put(Entity.xml(document)));
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public String atualizarDocumento() {
        try {
            document = new DocumentDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType(), false);

            System.out.println(client.target(URILookup.getBaseAPI())
                    .path("/propostas/atualizarDocument")
                    .path(currentProposta.getCode()+"")
                    .path(currentDocumento.getId()+"")
                    .request(MediaType.APPLICATION_XML)
                    .put(Entity.xml(document)));
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }

        return "details?faces-redirect=true";
    }
    
    public String removerDocumento(){
        try{
            propostaBean.removerDocumento(currentProposta.getCode(), currentDocumento.getId());
            File f = new File(currentDocumento.getFilepath());
            f.delete();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return "details?faces-redirect=true";
    }
    
    public void uploadUserDocument() {
        try {
            documento = new DocumentoDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType());
            System.out.println(client.target(URILookup.getBaseAPI())
                    .path("/students/addDocumento")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .put(Entity.xml(document)));
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public String removerUserDocumento(){
        try{
            studentBean.removerDocumento(student.getUsername(), currentDocumento.getId());
            File f = new File(currentDocumento.getFilepath());
            f.delete();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return "details?faces-redirect=true";
    }
    
}
