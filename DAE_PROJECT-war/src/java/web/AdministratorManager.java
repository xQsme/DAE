/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import auxiliar.Estado;
import dtos.DocumentDTO;
import dtos.InstituicaoDTO;
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
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.TeacherAlreadyAssignedException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.mail.internet.AddressException;

/**
 *
 * @author rick_sanchez
 */
@ManagedBean
@SessionScoped
public class AdministratorManager implements Serializable {
    
    @ManagedProperty(value = "#{uploadManager}")
    private UploadManager uploadManager;
    
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
    @EJB
    private EmailBean emailBean;
    
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
    
    public AdministratorManager() {
        newStudent = new StudentDTO();
        newInstituicao = new InstituicaoDTO();
        newTeacher = new TeacherDTO();
        newProposta = new PropostaDTO();
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
            return teacherBean.getAllTeachers();
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
        try {
            return propostaBean.getAllPropostas();
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
            teacherBean.remove(currentTeacher.getUsername());
        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
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
            teacherBean.update(currentTeacher.getUsername(),
                    currentTeacher.getName(),
                    currentTeacher.getEmail(),
                    currentTeacher.getOffice());

        } catch (EntityDoesNotExistsException | MyConstraintViolationException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
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
    
    public String validateProposta(String username) throws AddressException, Exception {
        try {
            propostaBean.addValidacao(
                    currentProposta.getCode(),
                    currentProposta.getIntEstado(),
                    currentProposta.getObservacao());
            
            MembroCCP membro=membroCCPBean.find(username);
         
            String titulo = currentProposta.getTitulo();
            String message = buildMessage(username);
             
            List<String> recipients= new LinkedList<String>();
        
            for(ProponenteDTO proponente: proponenteBean.getPropostaProponentes(currentProposta.getCode())){
                recipients.add(proponente.getEmail());
            }            
            
            emailBean.send(membro.getEmail(), membro.getPassword(), membro.getEmail(), titulo, message, recipients);
            
        } catch (EntityDoesNotExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
      
        return "/admin/propostas/view.xhtml?faces-redirect=true";
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
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
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
            teacherBean.create(
                    newTeacher.getUsername(),
                    newTeacher.getPassword(),
                    newTeacher.getName(),
                    newTeacher.getEmail(),
                    newTeacher.getOffice());
            newTeacher.reset();
        } catch (EntityAlreadyExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), component, logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
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

    private String buildMessage(String username) {
        String msg = "<strong>A proposta:</strong> "+currentProposta.getTitulo()
                    +".<br><br><strong>Com descricao:</strong> "+ currentProposta.getResumo()+"."
                    +".<br><br><strong>Foi avaliada por:</strong> "+ username+".";
                  
        if (currentProposta.getIntEstado()==1) msg+= "<br><br>Sendo esta <strong>" + currentProposta.getEstado() +"</strong>."; 
        else if (currentProposta.getIntEstado()==-1) msg+= "<br><br>Sendo esta infelizmente <strong>"+ currentProposta.getEstado() +"</strong>.";
            
        msg+= (currentProposta.getObservacao()!=null && !currentProposta.getObservacao().isEmpty())?
            ("<br><strong>Observação:</strong> "+ currentProposta.getObservacao())+".": "<br><br>Não deixou Observação.";
        
        return msg;
    }
    
    public String addGuidingTeacher (){
        try {
            membroCCPBean.addProfessorOrientador(newTeacher.getUsername(), currentStudent.getUsername());
            newTeacher.reset();
        } catch (EntityDoesNotExistsException | TeacherAlreadyAssignedException e) {
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
        }
        return "/admin/students/view.xhtml?faces-redirect=true";
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
        
        
}
