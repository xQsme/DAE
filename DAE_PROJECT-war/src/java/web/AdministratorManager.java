/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import exceptions.CannotFinalizeException;
import auxiliar.Estado;
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
    
    private String pattern;
    
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
                    .path("/proponenete/proposta/"+currentProposta.getCode())
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ProponenteDTO>>() {});
           
        } catch (Exception e) {
             System.out.println("2");
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    
    public Collection<StudentDTO> getCurrentPropostaCandidatos(){
        try {
            return client.target(URILookup.getBaseAPI())
                    .path("/candidaturas/"+currentProposta.getCode())
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<StudentDTO>>() {});
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    //Not yet Rest
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
    
    //Not yet Rest
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

    public List<PropostaDTO> getAllPropostas() {
            List<PropostaDTO> propostasEmAndamento = new LinkedList<>();
            try {
                Collection<PropostaDTO> propostas = client.target(URILookup.getBaseAPI())
                                                    .path("/propostas/")
                                                    //.queryParam("pattern", "Po")
                                                    .request(MediaType.APPLICATION_XML)
                                                    .get(new GenericType<List<PropostaDTO>>() {});         
                for(PropostaDTO p : propostas){
                    System.out.println(p.getTitulo());
                    if(p.getEstado()>=-1 && p.getEstado()<2){
                        propostasEmAndamento.add(p);
                    }
                }
                
                //Creates an "dynamic list" this done this way in order to only need to use
                //one filter; instead of create 1 filter for every single get's
                filterList=(List<Object>)(List<?>)propostasEmAndamento;
                return propostasEmAndamento;
            } catch (Exception e) {
                throw new EJBException(e.getMessage());
            }
    }
    
    public Collection<PropostaDTO> getAllAccepted() {
        try {
            return propostaBean.getAllAccepted();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    //Not yet Rest
    public Collection<PropostaDTO> getAllProvas() {
       try{
           return propostaBean.getAllProvas();
       } catch (Exception e) {
           FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
           return null;
       }
    }
    
    //Not yet Rest
    public Collection<PropostaDTO> getAllFinalizado() {
        try{
            return propostaBean.getAllFinalizado();
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
        try {
            return InstituicaoBean.getAllTiposInstituicao();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public Collection<String> getAllTiposTrabalho() {
        
        /*Collection<PropostaDTO> propostas = client.target(URILookup.getBaseAPI())
                                                .path("/propostas")
                                                .request(MediaType.APPLICATION_XML)
                                                .get(new GenericType<List<PropostaDTO>>() {})*/
        /*try {
            return PropostaBean.getAllTiposTrabalhos();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }*/
        return null;
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
        /*try {        
            if (currentProposta.getIntEstado() > 0 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                }
                
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                } 
                
                if(currentProposta.getIntEstado()==2){
                    for(Student candidatos: currentProposta.getCandidatos()) {
                        recipients.add(candidatos.getEmail());
                    }
                }
                
                if(currentProposta.getIntEstado()==1)emailManager.removeProposta(loggedMembroCCP, currentProposta, recipients);
                if(currentProposta.getIntEstado()==2)emailManager.removeProva(loggedMembroCCP, currentProposta, recipients);
            }
            propostaBean.remove(currentProposta.getCode());
        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }*/
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
        /*try {    
            propostaBean.update(
                    currentProposta.getCode(),
                    currentProposta.getTitulo(),
                    currentProposta.getTipoDeTrabalho(),
                    currentProposta.getResumo(),
                    currentProposta.getPlanoDeTrabalhos(),
                    currentProposta.getLocal(),
                    currentProposta.getOrcamento(),
                    currentProposta.getApoios());
           
        } catch (EntityDoesNotExistsException | MyConstraintViolationException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }*/
        return "/admin/propostas/view.xhtml?faces-redirect=true";
    }
    
    public String updateProva() {
        /*try {
            //Checks what changed
            List<String> alterations=propostaBean.getAlterations(currentProposta.getCode(), currentProposta.getTitulo(),               
                    currentProposta.getTipoDeTrabalho(), currentProposta.getResumo(),
                    currentProposta.getPlanoDeTrabalhos(),currentProposta.getLocal(),
                    currentProposta.getOrcamento(), currentProposta.getApoios());
            
            propostaBean.update(
                    currentProposta.getCode(),
                    currentProposta.getTitulo(),
                    currentProposta.getTipoDeTrabalho(),
                    currentProposta.getResumo(),
                    currentProposta.getPlanoDeTrabalhos(),
                    currentProposta.getLocal(),
                    currentProposta.getOrcamento(),
                    currentProposta.getApoios());
            
            if (currentProposta.getIntEstado() == 2 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                } 
               
                for(Student candidatos: currentProposta.getCandidatos()) {
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
        }*/
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
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                } 
              
                emailManager.validateProposta(loggedMembroCCP, currentProposta, recipients);        
            }
            
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
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
    
    /* esta função nao esta a ser utilizada, esta antes a ser usada a classe usernamevalidator
    public void validateUsername(FacesContext context, UIComponent toValidate, Object value) {
        try {
            //Your validation code goes here
            String username = (String) value;
            //If the validation fails
            ArrayList<UserDTO> users = (ArrayList<UserDTO>) userBean.getAllUsers();
            
            for (UserDTO user : users) {
                if (username.equalsIgnoreCase(user.getUsername())) {
                    FacesMessage message = new FacesMessage("Error: invalid username.");
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(toValidate.getClientId(context), message);
                    ((UIInput) toValidate).setValid(false);
                }
            }
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unkown error.", logger);
        }
    }*/
    
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
        int code = propostaBean.getNextCode();
        logger.info("" + code);
        try {
            propostaBean.create(
                    code,
                    newProposta.getTitulo(),
                    newProposta.getTipoDeTrabalho(), 
                    newProposta.getResumo(),
                    newProposta.getPlanoDeTrabalhos(), 
                    newProposta.getLocal(), 
                    newProposta.getOrcamento(), 
                    newProposta.getApoios());
            newProposta.reset();
        } catch (EntityAlreadyExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage() + "\t" + code , component, logger);
            return null;
        } catch (EntityDoesNotExistsException | MyConstraintViolationException e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
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
