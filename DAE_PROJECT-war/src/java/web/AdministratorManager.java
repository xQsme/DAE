/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import exceptions.CannotFinalizeException;
import auxiliar.Estado;
import auxiliar.TipoDeInstituicao;
import auxiliar.TipoDeTrabalho;
import dtos.DocumentDTO;
import dtos.InstituicaoDTO;
import dtos.MembroCCPDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import dtos.TeacherDTO;
import ejbs.EmailBean;
import ejbs.InstituicaoBean;
import ejbs.MembroCCPBean;
import ejbs.ProponenteBean;
import ejbs.PropostaBean;
import ejbs.TeacherBean;
import ejbs.StudentBean;
import entities.MembroCCP;
import entities.Student;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.ProposalWasNotSubmittedByAnInstitutionException;
import exceptions.StudentHasNoProposalException;
import exceptions.TeacherAlreadyAssignedException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.mail.internet.AddressException;
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
public class AdministratorManager implements Serializable {
    
    private Client client;
    private HttpAuthenticationFeature feature;
    
    @ManagedProperty(value = "#{uploadManager}")
    private UploadManager uploadManager;
    
    @ManagedProperty(value="#{userManager}")
    private UserManager userManager;
    
    @ManagedProperty(value="#{emailManager}")
    private EmailManager emailManager;
    
    @EJB
    private InstituicaoBean instituicaoBean;
    @EJB
    private TeacherBean teacherBean;
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private MembroCCPBean membroCCPBean;
    @EJB
    private ProponenteBean proponenteBean;
    @EJB
    private StudentBean studentBean;
    
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
    private MembroCCP loggedMembroCCP;
    
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
        logger.info(userManager.toString());
        String username = userManager.getUsername();
        logger.info(username);
        loggedMembroCCP = membroCCPBean.find(username );
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
    
    public Collection<InstituicaoDTO> getAllInstitutions() {
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/instituicoes")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<InstituicaoDTO>>() {});        
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public List<TeacherDTO> getAllTeachers() {
        try {
            ///teachers
            return client.target(URILookup.getBaseAPI())
                    .path("/teachers")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<TeacherDTO>>() {});
            //return teacherBean.getAllTeachers();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public List<ProponenteDTO> getCurrentPropostaProponentes(){
        try {
            System.out.println("1");
            return client.target(URILookup.getBaseAPI())
                    .path("/proponente/proposta/"+currentProposta.getCode())
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ProponenteDTO>>() {});
           
        } catch (Exception e) {
             System.out.println("2");
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
    
    
    public Collection<StudentDTO> getAllStudents() {
        try {
            return client.target(URILookup.getBaseAPI())
                                .path("/students")
                                .request(MediaType.APPLICATION_XML)
                                .get(new GenericType<List<StudentDTO>>() {});  
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }

    
    public Collection<PropostaDTO> getAllPropostas() {
        List<PropostaDTO> propostasEmAndamento = new LinkedList<>();
        try {
            Collection<PropostaDTO> propostas = client.target(URILookup.getBaseAPI())
                                                .path("/propostas")
                                                .request(MediaType.APPLICATION_XML)
                                                .get(new GenericType<List<PropostaDTO>>() {});         
            for(PropostaDTO p : propostas){
                System.out.println(p.getTitulo());
                if(p.getEstado()>=-1 && p.getEstado()<2){
                    propostasEmAndamento.add(p);
                }
            }
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return propostasEmAndamento;
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
    
    public Collection<PropostaDTO> getAllProvas() {
       try{
           return client.target(URILookup.getBaseAPI())
                                                .path("/propostas/provas")
                                                .request(MediaType.APPLICATION_XML)
                                                .get(new GenericType<List<PropostaDTO>>() {}); 
       } catch (Exception e) {
           FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
           return null;
       }
    }
    
    //Not yet Rest
    public Collection<PropostaDTO> getAllFinalizado() {
        try{
            return client.target(URILookup.getBaseAPI())
                                                .path("/propostas/finalizados")
                                                .request(MediaType.APPLICATION_XML)
                                                .get(new GenericType<List<PropostaDTO>>() {}); 
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
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
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                }
                
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
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
                
                if(currentProposta.getEstado()==1)emailManager.removeProposta(loggedMembroCCP, currentProposta, recipients);
                if(currentProposta.getEstado()==2)emailManager.removeProva(loggedMembroCCP, currentProposta, recipients);
            }
            
            client.target(URILookup.getBaseAPI()).path("/propostas")
                                                .path(Integer.toString(currentProposta.getCode()))
                                                .request(MediaType.APPLICATION_XML)
                                                .delete();
        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
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
    
    public String updateProva() {
        try {
            //Checks what changed
            List<String> alterations=propostaBean.getAlterations(currentProposta.getCode(), currentProposta.getTitulo(),               
                    currentProposta.getTipoDeTrabalho(), currentProposta.getResumo(),
                    currentProposta.getPlanoDeTrabalhos(),currentProposta.getLocal(),
                    currentProposta.getOrcamento(), currentProposta.getApoios());
            
            client.target(URILookup.getBaseAPI())
                .path("/propostas")
                .path(currentProposta.getCode()+"")
                .request(MediaType.APPLICATION_XML)
                .put(Entity.xml(currentProposta));
            
            if (currentProposta.getEstado() == 2 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                } 
               
                for(StudentDTO candidatos:client.target(URILookup.getBaseAPI())
                                        .path("/propostas/students")
                                        .path(currentProposta.getCode()+"")
                                        .request(MediaType.APPLICATION_XML)
                                        .get(new GenericType<List<StudentDTO>>() {})){
                    recipients.add(candidatos.getEmail());
                }
   
                emailManager.updateProva(loggedMembroCCP, currentProposta, alterations, recipients);        
            }
        } catch (EntityDoesNotExistsException | MyConstraintViolationException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        return "/admin/provas/view.xhtml?faces-redirect=true";
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
              
                emailManager.validateProposta(loggedMembroCCP, currentProposta, recipients);        
            }
            
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
      
        return "/admin/propostas/view.xhtml?faces-redirect=true";
    }
    
    //Not full implmented yet
    public String serializarProposta() throws AddressException, Exception {
        /*try {
            
            propostaBean.addSerialição(
                    currentProposta.getCode(),
                    currentProposta.getCandidatos();
                    );*/

            /*if (currentProposta.getIntEstado() == 2 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                }
                
                for(Student candidatos: currentProposta.getCandidatos()) {
                    recipients.add(candidatos.getEmail());
                }

                emailManager.serializeProposta(loggedMembroCCP, currentProposta, recipients);     
            }

        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        */   
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
            System.out.println("Resposta: " +response.getStatus());
        
            newStudent.reset();
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
            
            Response response = client.target(URILookup.getBaseAPI())
                .path("/propostas")
                .request(MediaType.APPLICATION_XML)
                .post(Entity.xml(newProposta));
            
        }catch (Exception e) {
            logger.warning(e.toString());
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! " + e.toString(), component, logger);
            return null;
        }
        return "/admin/propostas/view.xhtml?faces-redirect=true";
    }
    

    
    public String addGuidingTeacher (){
        try {
            membroCCPBean.addProfessorOrientador(newTeacher.getUsername(), currentStudent.getUsername());
            newTeacher.reset();
        } catch (EntityDoesNotExistsException | TeacherAlreadyAssignedException | NullPointerException | 
                ProposalWasNotSubmittedByAnInstitutionException | StudentHasNoProposalException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }
    
    public String finalizar(){
        /*try {
            if(currentProposta.getIntEstado() != 2){
                throw new CannotFinalizeException();
            }
        }
        catch (CannotFinalizeException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        }*/
        return "finalize";
    }
    
    public String finalizarDocumento() {
        try {
            document = new DocumentDTO(uploadManager.getCompletePathFile(), uploadManager.getFilename(), uploadManager.getFile().getContentType(), true);
            /*System.out.println(client.target(URILookup.getBaseAPI())
                    .path("/propostas/addDocument")
                    .path(currentProposta.getCode()+"")
                    .request(MediaType.APPLICATION_XML)
                    .put(Entity.xml(document)));*/
            propostaBean.finalizarDocumento(currentProposta.getCode(), document);
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }

        return "view?faces-redirect=true";
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
    
    public Collection<DocumentDTO> getStudentDocumentos(){
        /*try {
            return studentBean.getDocuments(currentStudent.getUsername());
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }*/
        return null;
    }
        
    public Collection<PropostaDTO> getAllAvailableProposals() {
        try {
            return propostaBean.getAllAccepted();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public String setProposalStudent(){
        try {
            logger.info("proposta -> " + newProposta.getCode());
            logger.info("username -> " + currentStudent.getUsername());
            studentBean.setProposta(currentStudent.getUsername(), newProposta.getCode());
            
            newProposta.setCode(0);
        } catch (Exception e) {
            logger.info(e.toString());
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }
}
