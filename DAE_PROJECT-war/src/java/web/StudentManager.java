/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import dtos.DocumentDTO;
import dtos.DocumentoDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import exceptions.AlreadyAppliedToProposalException;
import exceptions.NoDocumentsException;
import exceptions.ProposalStateDoesNotAllowException;
import exceptions.StudentCandidaturasFullException;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
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
public class StudentManager implements Serializable {
    
    private Client client;
    private HttpAuthenticationFeature feature;
    
    @ManagedProperty(value="#{userManager}")
    private UserManager userManager;
        
    @ManagedProperty(value = "#{uploadManager}")
    private UploadManager uploadManager;
    
    private static final Logger logger = Logger.getLogger("web.StudentManager");
    
    private StudentDTO student;
    private List<DocumentDTO> documents;
    private DocumentDTO document;
    private DocumentoDTO documento;
    private PropostaDTO currentProposta;
    private DocumentDTO currentDocument;
    private DocumentoDTO currentDocumento;
    
    
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
                    .path("/propostas")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<PropostaDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING PROPOSTAS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
        }
    }
    
    public List<PropostaDTO> getStudentPropostas(){
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/students/propostas/"+student.getUsername())
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<PropostaDTO>>() {});
            
        } catch (Exception e) {
            System.out.println("ERROR GETTING STUDENT PROPOSTAS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
        }
    }
    
    public List<DocumentDTO> getPropostaDocuments(){
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/propostas/documents")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<DocumentDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING PROPOSTA DOCUMENTS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
        }
    }
    
    public Boolean isStudentCandidatoProposta(int codeProposta){
        logger.info("codigo = " + codeProposta);
        try{
            for (PropostaDTO p : getStudentPropostas()){
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
        client.target(URILookup.getBaseAPI())
                    .path("/students/proposta")
                    .path(student.getUsername())
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .delete();
    }
    
    public void candidatar(){
        try {
            if(getStudentDocumentos().isEmpty()){
                throw new NoDocumentsException();
            }else if(getStudentPropostas().contains(currentProposta)){
                throw new AlreadyAppliedToProposalException();
            }else if(getStudentPropostas().size() >= 5){
                throw new StudentCandidaturasFullException();
            }else if(currentProposta.getEstado()!=1){
                throw new ProposalStateDoesNotAllowException();
            }
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/students/proposta/"+student.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.xml(currentProposta));
            
            System.out.println("Resposta: " +response.getStatus());
            
        }
        catch (AlreadyAppliedToProposalException | NoDocumentsException | ProposalStateDoesNotAllowException | StudentCandidaturasFullException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
        }
       
    }

    public List<ProponenteDTO> getCurrentPropostaProponentes(){
        try{
            return client.target(URILookup.getBaseAPI())
                .path("/propostas/proponentes")
                .path(currentProposta.getCode()+"")
                .request(MediaType.APPLICATION_XML)
                .get(new GenericType<List<ProponenteDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING PROPONENTES");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
        }
    }
    
    public List<StudentDTO> getCurrentPropostaCandidatos(){
        try{
            return client.target(URILookup.getBaseAPI())
                .path("/propostas/students")
                .path(currentProposta.getCode()+"")
                .request(MediaType.APPLICATION_XML)
                .get(new GenericType<List<StudentDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING CANDIDATOS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
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
                    .path("/students")
                    .path(userManager.getUsername())
                    .request(MediaType.APPLICATION_XML)
                    .get(StudentDTO.class);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
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

    public DocumentDTO getCurrentDocument() {
        return currentDocument;
    }

    public void setCurrentDocument(DocumentDTO currentDocument) {
        this.currentDocument = currentDocument;
    }
    
    public DocumentoDTO getCurrentDocumento() {
        return currentDocumento;
    }

    public void setCurrentDocumento(DocumentoDTO currentDocumento) {
        this.currentDocumento = currentDocumento;
    }
    
    public Collection<DocumentDTO> getCurrentPropostaDocumentos(){
        LinkedList<DocumentDTO> documents = new LinkedList<>();
        for(DocumentDTO d : getPropostaDocuments()){
            if(!d.isAta()){
                documents.add(d);
            }
        }
        return documents;
    }
    
    public DocumentDTO getCurrentPropostaAta(){
        for(DocumentDTO d : getPropostaDocuments()){
            if(d.isAta()){
                return d;
            }
        }
        return null;
    }
    
    public List<DocumentoDTO> getStudentDocumentos(){
        try{
            return client.target(URILookup.getBaseAPI())
                .path("/students/documentos/"+student.getUsername())
                .request(MediaType.APPLICATION_XML)
                .get(new GenericType<List<DocumentoDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING STUDENT DOCUMENTS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
        }
        //return null;
    }
    
    public void uploadDocument() {
        try {
            document = new DocumentDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType(), false);
           
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/propostas/documento/"+currentProposta.getCode())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.xml(document));
                   
            System.out.println("Resposta: " +response.getStatus());   
            
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public String atualizarDocument() {
        try {
            document = new DocumentDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType(), false);
            
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/propostas/documento/"+currentProposta.getCode())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.put(Entity.xml(document));
            
            System.out.println("Resposta: " +response.getStatus());     
            
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }

        return "details?faces-redirect=true";
    }
    
    public String removerDocument(){
        try{
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/propostas/documento/"+currentDocument.getId())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.delete();
            
            System.out.println("Resposta: " +response.getStatus());     
            
            File f = new File(currentDocument.getFilepath());
            f.delete();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return "details?faces-redirect=true";
    }
    
    public void uploadStudentDocumento() {
        try {
            documento = new DocumentoDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType());
            
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/students/documento/"+student.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.xml(documento));
                   
            System.out.println("Resposta: " +response.getStatus());         
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public String removerStudentDocumento(){
        try{
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/students/documento/"+currentDocumento.getId())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.delete();
            
            File f = new File(currentDocumento.getFilepath());
            f.delete();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return "details?faces-redirect=true";
    }
    
}
