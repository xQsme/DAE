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
public class AdministratorManager implements Serializable {
    
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
    private Client client;
    private MembroCCP loggedMembroCCP;
    
    private HttpAuthenticationFeature feature;
    
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
            return instituicaoBean.getAllInstitutions();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public Collection<TeacherDTO> getAllTeachers() {
        try {
            List<TeacherDTO> returnedTeachers = client.target(URILookup.getBaseAPI())
                .path("/teachers/all")
                .request(MediaType.APPLICATION_XML)
                .get(new GenericType<List<TeacherDTO>>() {
                });
            return returnedTeachers;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
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
            return studentBean.getAllStudents();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }

    public Collection<PropostaDTO> getAllPropostas() {
        LinkedList<PropostaDTO> propostas = new LinkedList<>();
        try {
            for(PropostaDTO p : propostaBean.getAllPropostas()){
                if(p.getEstado() < 2){
                    propostas.add(p);
                }
            }
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return propostas;
    }
    
    public Collection<PropostaDTO> getAllAccepted() {
        try {
            return propostaBean.getAllAccepted();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
     public Collection<PropostaDTO> getAllProvas() {
        try{
            return propostaBean.getAllProvas();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
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
        try {
            return PropostaBean.getAllTiposTrabalhos();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
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
            studentBean.remove(currentStudent.getUsername());
        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public void removeTeacher(){ 
        try {
            client.target(URILookup.getBaseAPI())
                .path("/teachers/remove")
                .path(currentTeacher.getUsername())
                .request(MediaType.APPLICATION_XML)
                .delete();
        } catch (Exception e) {
            logger.info(e.toString());
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! " + e.toString(), logger);
        }
    }
    
    public void removeInstituicao(){ 
        try {
            instituicaoBean.remove(currentInstituicao.getUsername());
        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
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
                    for(StudentDTO candidatos: propostaBean.getCandidatos(currentProposta.getCode())) {
                        recipients.add(candidatos.getEmail());
                    }
                }
                
                if(currentProposta.getEstado()==1)emailManager.removeProposta(loggedMembroCCP, currentProposta, recipients);
                if(currentProposta.getEstado()==2)emailManager.removeProva(loggedMembroCCP, currentProposta, recipients);
            }
            propostaBean.remove(currentProposta.getCode());
        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public String updateStudent() {
        try {
            studentBean.update(currentStudent.getUsername(),
                    currentStudent.getName(),
                    currentStudent.getEmail());

        } catch (EntityDoesNotExistsException | MyConstraintViolationException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }
    
    public String updateTeacher() {
        try {
            client.target(URILookup.getBaseAPI())
                .path("/teachers/update")
                .path(currentTeacher.getUsername())
                .path(currentTeacher.getPassword())
                .path(currentTeacher.getName())
                .path(currentTeacher.getEmail())
                .request(MediaType.APPLICATION_XML)
                .put(Entity.xml((currentTeacher.getOffice())));
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! " +e.toString() , logger);
            return null;
        }
        return "/admin/teachers/view.xhtml?faces-redirect=true";
    }
    
    public String updateInstituicao() {
        try {
            instituicaoBean.update(currentInstituicao.getUsername(),
                    currentInstituicao.getName(),
                    currentInstituicao.getEmail(),
                    currentInstituicao.getTipo());

        } catch (EntityDoesNotExistsException | MyConstraintViolationException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
        return "/admin/instituicoes/view.xhtml?faces-redirect=true";
    }
    
    public String updateProposta() {
        try {    
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
            
            propostaBean.update(
                    currentProposta.getCode(),
                    currentProposta.getTitulo(),
                    currentProposta.getTipoDeTrabalho(),
                    currentProposta.getResumo(),
                    currentProposta.getPlanoDeTrabalhos(),
                    currentProposta.getLocal(),
                    currentProposta.getOrcamento(),
                    currentProposta.getApoios());
            
            if (currentProposta.getEstado() == 2 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                } 
               
                for(StudentDTO candidatos: propostaBean.getCandidatos(currentProposta.getCode())) {
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
            propostaBean.addValidacao(
                    currentProposta.getCode(),
                    currentProposta.getEstado(),
                    currentProposta.getObservacao());
            
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
    
    public String serializarProposta() throws AddressException, Exception {
        try {
            
            /*propostaBean.addSerialição(
                    currentProposta.getCode(),
                    currentProposta.getCandidatos();
                    );*/

            if (currentProposta.getEstado() == 2 ){
                List<String> recipients= new LinkedList<String>();
                for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                    recipients.add(proponente.getEmail());
                }
                
                for(StudentDTO candidatos: propostaBean.getCandidatos(currentProposta.getCode())) {
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
            studentBean.create(
                    newStudent.getUsername(),
                    newStudent.getPassword(),
                    newStudent.getName(),
                    newStudent.getEmail());
            newStudent.reset();
        } catch (EntityAlreadyExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), component, logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
    }
    
    public String createTeacher() {
        
        try {
            TeacherDTO teacher = new TeacherDTO(newTeacher.getUsername(), newTeacher.getPassword(), newTeacher.getName(),newTeacher.getEmail(),newTeacher.getOffice());
            client.target(URILookup.getBaseAPI())
                .path("/teachers/create")
                .request(MediaType.APPLICATION_XML)
                .post(Entity.xml(teacher));
                //.put(Entity.xml((newTeacher.getUsername())));
            newTeacher.reset();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter! " + e.toString() , component, logger);
            return null;
        }
        return "/admin/teachers/view.xhtml?faces-redirect=true";
    }
    
    public String createInstituicao() {
            
        try {
            instituicaoBean.create(
                    newInstituicao.getUsername(),
                    newInstituicao.getPassword(),
                    newInstituicao.getName(),
                    newInstituicao.getEmail(),
                    newInstituicao.getTipo());
            newTeacher.reset();
        } catch (EntityAlreadyExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), component, logger);
            return null;
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
        try {
            if(currentProposta.getEstado() != 2){
                throw new CannotFinalizeException();
            }
        }
        catch (CannotFinalizeException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        }
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
        try {
            return studentBean.getDocuments(currentStudent.getUsername());
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
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
