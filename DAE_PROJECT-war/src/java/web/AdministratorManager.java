/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import auxiliar.Estado;
import auxiliar.TipoDeInstituicao;
import auxiliar.TipoDeTrabalho;
import dtos.DocumentDTO;
import dtos.DocumentoDTO;
import dtos.EmailDTO;
import dtos.InstituicaoDTO;
import dtos.MembroCCPDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import dtos.TeacherDTO;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericEntity;
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
public class AdministratorManager implements Serializable {
    
    private Client client;
    private HttpAuthenticationFeature feature;
    
    @ManagedProperty(value = "#{uploadManager}")
    private UploadManager uploadManager;
    
    @ManagedProperty(value="#{userManager}")
    private UserManager userManager;
    
    @ManagedProperty(value="#{emailManager}")
    private EmailManager emailManager;
    
    private static final Logger logger = Logger.getLogger("web.AdministratorManager");
    
    private InstituicaoDTO currentInstituicao;
    private InstituicaoDTO newInstituicao;
   
    private TeacherDTO currentTeacher;
    private TeacherDTO newTeacher;
    
    private DocumentDTO document;
    private DocumentDTO currentDocumento;
    
    private PropostaDTO currentProposta;
    private PropostaDTO newProposta;
    
    private StudentDTO currentStudent;
    private StudentDTO newStudent;
    
    private UIComponent component;
    private MembroCCPDTO loggedMembroCCP;
    
    private String currentPropostaFinalCandidato;
    
    //Primefaces require a filterList to temporarly store the values, h5!
    public List<Object> filterList;
    public void setFilterList(List<Object> filter){
        filterList=filter;
    }
    public List<Object> getFilterList(){
        return filterList;
    }
    
    public AdministratorManager() {
        newStudent = new StudentDTO();
        newInstituicao = new InstituicaoDTO();
        newTeacher = new TeacherDTO();
        newProposta = new PropostaDTO();
        client = ClientBuilder.newClient();
    }
    
    @PostConstruct
    public void Init(){
        feature = HttpAuthenticationFeature.basic(userManager.getUsername(), userManager.getPassword());
        client.register(feature);
        setUpMembroCCP();
    }
  
    private void setUpMembroCCP() {
        try {
            loggedMembroCCP = client.target(URILookup.getBaseAPI())
                    .path("/admin")
                    .path(userManager.getUsername())
                    .request(MediaType.APPLICATION_XML)
                    .get(MembroCCPDTO.class);
        } catch (Exception e) {
            System.out.println("ERRO SETUP ADMIN");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! (" + e.toString() + ")", logger);
        }
    }
    
    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public EmailManager getEmailManager() {
        return emailManager;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }
    
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    
    public List<InstituicaoDTO> getAllInstitutions() {
        try {
            List<InstituicaoDTO> instituicao= client.target(URILookup.getBaseAPI())
                    .path("/instituicoes")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<InstituicaoDTO>>() {}); 
            
            filterList=(List<Object>)(List<?>)instituicao;
            return instituicao;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public List<TeacherDTO> getAllTeachers() {
        try {
            List<TeacherDTO> teachers=client.target(URILookup.getBaseAPI())
                    .path("/teachers")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<TeacherDTO>>() {});
            
            
            filterList=(List<Object>)(List<?>)teachers;
            return teachers;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public List<ProponenteDTO> getCurrentPropostaProponentes(){
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/proponente/proposta/"+currentProposta.getCode())
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ProponenteDTO>>() {});
           
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
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
    
    public Collection<DocumentDTO> getCurrentPropostaDocumentos(){
        try{
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
    
    
    public List<StudentDTO> getAllStudents() {
        try {
            List<StudentDTO> students=client.target(URILookup.getBaseAPI())
                                .path("/students")
                                .request(MediaType.APPLICATION_XML)
                                .get(new GenericType<List<StudentDTO>>() {});
            
            filterList=(List<Object>)(List<?>)students;
            return students;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public List<PropostaDTO> getAllProvas() {
        List<PropostaDTO> provas = new LinkedList<>();
        for(PropostaDTO p : getAllPropostas()){
            if(p.getEstado() > 1){
                provas.add(p);
            }
        }
        filterList=(List<Object>)(List<?>)provas;
        return provas;
   }
    
    public List<PropostaDTO> getPropostas() {
        List<PropostaDTO> propostas = new LinkedList<>();
        for(PropostaDTO p : getAllPropostas()){
            if(p.getEstado() < 2){
                propostas.add(p);
            }
        }
        filterList=(List<Object>)(List<?>)propostas;
        return propostas;
   }
    

    public List<PropostaDTO> getAllPropostas() {
            try {
                List<PropostaDTO> propostas = client.target(URILookup.getBaseAPI())
                                                    .path("/propostas")
                                                    //.queryParam("pattern", "Po")
                                                    .request(MediaType.APPLICATION_XML)
                                                    .get(new GenericType<List<PropostaDTO>>() {});         
                
                //Creates an "dynamic list" this done this way in order to only need to use
                //one filter; instead of create 1 filter for every single get's
                return propostas;
            } catch (Exception e) {
                throw new EJBException(e.getMessage());
            }
    }
    
    public Collection<PropostaDTO> getAllAccepted() {
        try {
            return client.target(URILookup.getBaseAPI())
                                                .path("/propostas/accepted")
                                                .request(MediaType.APPLICATION_XML)
                                                .get(new GenericType<List<PropostaDTO>>() {}); 
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public InstituicaoDTO getCurrentInstituicao() {
        return currentInstituicao;
    }

    public void setCurrentInstituicao(InstituicaoDTO currentInstituicao) {
        this.currentInstituicao = currentInstituicao;
    }

    public InstituicaoDTO getNewInstituicao() {
        return newInstituicao;
    }

    public void setNewInstituicao(InstituicaoDTO newInstituicao) {
        this.newInstituicao = newInstituicao;
    }

    public TeacherDTO getCurrentTeacher() {
        return currentTeacher;
    }

    public void setCurrentTeacher(TeacherDTO currentTeacher) {
        this.currentTeacher = currentTeacher;
    }

    public TeacherDTO getNewTeacher() {
        return newTeacher;
    }

    public void setNewTeacher(TeacherDTO newTeacher) {
        this.newTeacher = newTeacher;
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

    public StudentDTO getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(StudentDTO currentStudent) {
        this.currentStudent = currentStudent;
    }

    public StudentDTO getNewStudent() {
        return newStudent;
    }

    public void setNewStudent(StudentDTO newStudent) {
        this.newStudent = newStudent;
    }
    
    public Collection<String> getAllTiposInstituicao() {
        LinkedList<String> tipos = new LinkedList<>();
        tipos.add(TipoDeInstituicao.Associação.toString());
        tipos.add(TipoDeInstituicao.Empresa.toString());
        tipos.add(TipoDeInstituicao.Pública.toString());
        return tipos;
    }
    
    public Collection<String> getAllTiposTrabalho() {
        
        LinkedList<String> trabalhos = new LinkedList<>();
        
        trabalhos.add(TipoDeTrabalho.Dissertação.toString());
        trabalhos.add(TipoDeTrabalho.Estágio.toString());
        trabalhos.add(TipoDeTrabalho.Projeto.toString());
        
        return trabalhos;
    }
    
    public Collection<String> getAllPropostaEstados() {
        try {
            return Estado.getVerificationStates();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public void removeStudent(){ 
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/students/"+currentStudent.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.delete();
            System.out.println("Resposta: " +response.getStatus());
    
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public void removeTeacher(){ 
        try {          
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/teachers/"+currentTeacher.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.delete();
            System.out.println("Resposta: " +response.getStatus());        
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public void removeInstituicao(){ 
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/instituicoes/"+currentInstituicao.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.delete();
            System.out.println("Resposta: " +response.getStatus());
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public void removeProposta(){ 
        try {        
            if (currentProposta.getEstado() > 0 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: getCurrentPropostaProponentes()){
                    recipients.add(proponente.getEmail());
                }
                
                for(ProponenteDTO proponente: getCurrentPropostaProponentes()){
                    recipients.add(proponente.getEmail());
                } 
                
                if(currentProposta.getEstado()==2){
                    for(StudentDTO candidatos: (client.target(URILookup.getBaseAPI())
                                                .path("/propostas/students")
                                                .path(Integer.toString(currentProposta.getCode()))
                                                .request(MediaType.APPLICATION_XML)
                                                .get(new GenericType<List<StudentDTO>>() {}))){
                        recipients.add(candidatos.getEmail());
                    }
                }
                
                if(currentProposta.getEstado()==1){
                    EmailDTO email = emailManager.removeProposta(loggedMembroCCP, currentProposta, recipients);
                    Response r = client.target(URILookup.getBaseAPI())
                        .path("/email")
                        .request(MediaType.APPLICATION_XML)
                        .post(Entity.xml(email));
                    
                    logger.info("email code = " + r.getStatus());
                }
                if(currentProposta.getEstado()==2){
                    EmailDTO email = emailManager.removeProva(loggedMembroCCP, currentProposta, recipients);
                    Response r = client.target(URILookup.getBaseAPI())
                        .path("/email")
                        .request(MediaType.APPLICATION_XML)
                        .post(Entity.xml(email));
                    
                    logger.info("email code = " + r.getStatus());
                }
            }
            
            client.target(URILookup.getBaseAPI()).path("/propostas")
                                                .path(Integer.toString(currentProposta.getCode()))
                                                .request(MediaType.APPLICATION_XML)
                                                .delete();
        }catch (Exception e) {
            System.out.println(e);
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public String updateStudent() {
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/students/"+currentStudent.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.put(Entity.xml(currentStudent));
            System.out.println("Resposta: " +response.getStatus());

        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }
    
    public String updateTeacher() {
      
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/teachers/"+currentTeacher.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.put(Entity.xml(currentTeacher));
            System.out.println("Resposta: " +response.getStatus());
        
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        return "/admin/teachers/view.xhtml?faces-redirect=true";
    }
    
    public String updateInstituicao() {
        try {  
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/instituicoes/"+currentInstituicao.getUsername())
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.put(Entity.xml(currentInstituicao));
            System.out.println("Resposta: " +response.getStatus());
            
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        return "/admin/instituicoes/view.xhtml?faces-redirect=true";
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
        return "/admin/propostas/view.xhtml?faces-redirect=true";
    }
    
    public String validateProposta() throws AddressException, Exception {
        try {
            //propostas/{code}/validacao/{estado}/{observacao}
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/propostas/"+currentProposta.getCode()+"/validacao")
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.put(Entity.xml(currentProposta));
            System.out.println("Resposta: " +response.getStatus());
          
            
            if (currentProposta.getEstado() == 1 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: client.target(URILookup.getBaseAPI())
                                        .path("/propostas/proponentes")
                                        .path(currentProposta.getCode()+"")
                                        .request(MediaType.APPLICATION_XML)
                                        .get(new GenericType<List<ProponenteDTO>>() {})){
                    recipients.add(proponente.getEmail());
                } 
                
                EmailDTO email = emailManager.validateProposta(loggedMembroCCP, currentProposta, recipients);        
                Response r = client.target(URILookup.getBaseAPI())
                        .path("/email")
                        .request(MediaType.APPLICATION_XML)
                        .post(Entity.xml(email));

                logger.info("email code = " + r.getStatus());
            }
            
        } catch (Exception e) {
            System.out.println(e);
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
      
        return "/admin/propostas/view.xhtml?faces-redirect=true";
    }
    
    //Not full implmented yet

    public String serializarProposta() throws AddressException, Exception {
        try {

            if (currentProposta.getEstado() == 1 ){
                List<String> recipients= new LinkedList<>();
                for(ProponenteDTO proponente: getCurrentPropostaProponentes()){
                    recipients.add(proponente.getEmail());
                }
                
                for(StudentDTO candidatos: getCurrentPropostaCandidatos()) {
                    recipients.add(candidatos.getEmail());
                }

                emailManager.serializeProposta(loggedMembroCCP, currentProposta, recipients);     
            }
            
            currentProposta.setEstado(2);
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/propostas/"+currentProposta.getCode()+"/validacao")
                                                        .request(MediaType.APPLICATION_XML);
            invocationBuilder.put(Entity.xml(currentProposta));
            invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/propostas/candidato"+currentProposta.getCode()+"/"+currentPropostaFinalCandidato)
                                                        .request(MediaType.APPLICATION_XML);
            invocationBuilder.get();
            

        }catch (MessagingException e) {
            
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        
        return "/admin/propostas/view.xhtml?faces-redirect=true";
    }    
    
    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }
    
    public String createStudent() {    
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/students")
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.xml(newStudent));
            System.out.println("Resposta: " +response.getStatus());
        
            newStudent.reset();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }
    
    public String createTeacher() {
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/teachers")
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.xml(newTeacher));
            System.out.println("Resposta: " +response.toString());
        
            newTeacher.reset();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
            return null;
        }
        return "/admin/teachers/view.xhtml?faces-redirect=true";
    }
    
    public String createInstituicao() {
            
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/instituicoes")
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.xml(newInstituicao));
            System.out.println("Resposta: " +response.getStatus());
            
          
            newInstituicao.reset();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
            return null;
        }
        return "/admin/instituicoes/view.xhtml?faces-redirect=true";
    }
    
    
    public String createProposta() {
        try {
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                                        .path("/propostas")
                                                        .request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.xml(newProposta));
            System.out.println("Resposta: " +response.getStatus());
            
            newProposta.reset();
        }catch (Exception e) {
            logger.warning(e.toString());
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! " + e.toString(), component, logger);
            return null;
        }
        return "/admin/propostas/view.xhtml?faces-redirect=true";
    }

    
    public String addGuidingTeacher (){
        try {
            
            Response response = client.target(URILookup.getBaseAPI())
                .path("/admin/teacher/student")
                .path(newTeacher.getUsername())
                .path(currentStudent.getUsername())
                .request(MediaType.APPLICATION_XML)
                .get();
            
            if (response.getStatus() == 500) {
                FacesExceptionHandler.handleException(new Exception(), response.readEntity(String.class), logger);
                return null;
            }
            
            newTeacher.reset();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, e.toString(), logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }
    
    public String finalizarDocumento() {
        try {
            document = new DocumentDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType(), true);
            System.out.println(client.target(URILookup.getBaseAPI())
                    .path("/propostas/documento")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.xml(document)));
            currentProposta.setEstado(3);
            Invocation.Builder invocationBuilder = client.target(URILookup.getBaseAPI())
                                            .path("/propostas/"+currentProposta.getCode()+"/validacao")
                                            .request(MediaType.APPLICATION_XML);
            invocationBuilder.put(Entity.xml(currentProposta));
        } catch (Exception e) {
            System.out.println("ERRO FINALIZAR DOCUMENTO");
            System.out.println(e);
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }

        return "provas?faces-redirect=true";
    }

    public UploadManager getUploadManager() {
        return uploadManager;
    }

    public void setUploadManager(UploadManager uploadManager) {
        this.uploadManager = uploadManager;
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
    
    public String setProposalStudent(){
        try {
            logger.info(newProposta.getCode() + "\t" + currentStudent.getUsername());
            
            Response response = client.target(URILookup.getBaseAPI())
                .path("/students/proposta")
                .path(currentStudent.getUsername())
                .path(newProposta.getCode()+"")
                .request(MediaType.APPLICATION_XML)
                .get();
            
            logger.info(response.getStatus() + "");
            
            if (response.getStatus() == 500) {
                FacesExceptionHandler.handleException(new Exception(), response.readEntity(String.class), logger);
                return null;
            }
            
            newProposta.setCode(0);
        } catch (Exception e) {
            logger.info(e.toString());
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }

    public String getCurrentPropostaFinalCandidato() {
        return currentPropostaFinalCandidato;
    }

    public void setCurrentPropostaFinalCandidato(String currentPropostaFinalCandidato) {
        this.currentPropostaFinalCandidato = currentPropostaFinalCandidato;
    }
}
