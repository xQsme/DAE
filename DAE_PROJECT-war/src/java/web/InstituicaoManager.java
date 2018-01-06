/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import auxiliar.TipoDeTrabalho;
import dtos.DocumentDTO;
import dtos.DocumentoDTO;
import dtos.InstituicaoDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
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
public class InstituicaoManager implements Serializable {
    
    private Client client;
    private HttpAuthenticationFeature feature;
    
    @ManagedProperty(value="#{userManager}") // this references the @ManagedBean
    private UserManager userManager;
    
    private static final Logger logger = Logger.getLogger("web.StudentManager");
    
    private InstituicaoDTO instituicao;
    
    private PropostaDTO currentProposta;
    private DocumentDTO currentDocumento;
    private PropostaDTO newProposta;
    private StudentDTO currentStudent;
    
    private UIComponent component;
    
    public InstituicaoManager() {
        newProposta = new PropostaDTO();
        client = ClientBuilder.newClient();
    }
    
    @PostConstruct
    public void Init(){
        feature = HttpAuthenticationFeature.basic(userManager.getUsername(), userManager.getPassword());
        client.register(feature);
        setUpInstituicao();
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
    
    
    public List<PropostaDTO> getInstituicaoPropostas() {
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/instituicoes/propostas")
                    .path(instituicao.getUsername())
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<PropostaDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING INSTITUTION PROPOSTAS");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
            return new LinkedList<>();
        }
    }
    
    public Boolean isInstituicaoProponenteProposta(int codeProposta){
        logger.info("codigo = " + codeProposta);
        for (PropostaDTO p : getInstituicaoPropostas()){
            if (p.getCode() == codeProposta) {
                return true;
            }
        }
        return false;
    }
    
    public Collection<String> getAllTiposTrabalho() {
        LinkedList<String> tipos = new LinkedList<>();
        tipos.add(TipoDeTrabalho.Dissertação.toString());
        tipos.add(TipoDeTrabalho.Estágio.toString());
        tipos.add(TipoDeTrabalho.Projeto.toString());
        return tipos;
    }

    public void removerProposta(){
        try {
            client.target(URILookup.getBaseAPI())
                    .path("/instituicoes/removerProposta")
                    .path(instituicao.getUsername())
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .delete();
        }catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public void adicionarProposta(){
        try {
            client.target(URILookup.getBaseAPI())
                .path("/instituicoes/propostas")
                .path(instituicao.getUsername())
                .request(MediaType.APPLICATION_XML)
                .post(Entity.xml(currentProposta));
        }
        catch (Exception e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
        }
    }

    public Collection<ProponenteDTO> getCurrentPropostaProponentes(){
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/propostas/proponentes")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ProponenteDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERROR GETTING PROPONENTES");
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

    private void setUpInstituicao() {
        try {
            instituicao = client.target(URILookup.getBaseAPI())
                    .path("/instituicoes")
                    .path(userManager.getUsername())
                    .request(MediaType.APPLICATION_XML)
                    .get(InstituicaoDTO.class);
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

    public InstituicaoDTO getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(InstituicaoDTO instituicao) {
        this.instituicao = instituicao;
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
        return "/instituicao/propostas/mine.xhtml?faces-redirect=true";
    }
    
    public String createProposta() {
        try {
            Response response = client.target(URILookup.getBaseAPI())
                .path("/propostas")
                .path(instituicao.getUsername())
                .request(MediaType.APPLICATION_XML)
                .post(Entity.xml(newProposta));
        }catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
            return null;
        }
        return "/instituicao/propostas/mine.xhtml?faces-redirect=true";
    }

    public DocumentDTO getCurrentDocumento() {
        return currentDocumento;
    }

    public void setCurrentDocumento(DocumentDTO currentDocumento) {
        this.currentDocumento = currentDocumento;
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
