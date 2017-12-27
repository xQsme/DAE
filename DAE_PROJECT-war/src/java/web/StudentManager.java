/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import auxiliar.TipoDeTrabalho;
import dtos.InstituicaoDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import dtos.TeacherDTO;
import dtos.UserDTO;
import ejbs.InstituicaoBean;
import ejbs.ProponenteBean;
import ejbs.PropostaBean;
import ejbs.TeacherBean;
import entities.Proponente;
import ejbs.StudentBean;
import ejbs.UserBean;
import entities.Proposta;
import entities.Student;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.StudentAlreadyHasAppliedException;
import exceptions.StudentCandidaturasFullException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

/**
 *
 * @author rick_sanchez
 */
@ManagedBean
@SessionScoped
public class StudentManager implements Serializable {
    
    @EJB
    private ProponenteBean proponenteBean;
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private StudentBean studentBean;
    
    @ManagedProperty(value="#{userManager}") // this references the @ManagedBean
    private UserManager userManager;
    
    private static final Logger logger = Logger.getLogger("web.StudentManager");
    
    private StudentDTO student;
    private PropostaDTO currentProposta;
    
    private UIComponent component;
    
    public StudentManager() {
        
    }
    
    @PostConstruct
    public void Init(){
        setUpStudent();
    }
    
    public Collection<PropostaDTO> getAllPropostas() {
        try {
            return propostaBean.getAllPropostas();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public List<PropostaDTO> getCandidaturas() {
        return student.getCandidaturas();
    }
    
    public Boolean isStudentCandidatoProposta(int codeProposta){
        logger.info("codigo = " + codeProposta);
        for (PropostaDTO p : student.getCandidaturas()){
            if (p.getCode() == codeProposta) {
                return true;
            }
        }
        return false;
    }
    
    public void removerCandidatura(){
        try {
            studentBean.removePropostaStudent(currentProposta.getCode(), student.getUsername());
            for(PropostaDTO p : student.getCandidaturas()){
                if (p.getCode() == currentProposta.getCode()) {
                    student.getCandidaturas().remove(p);                    
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
            studentBean.addCandidaturaStudent(currentProposta.getCode(), student.getUsername());
            student.getCandidaturas().add(currentProposta);
        }
        catch (Exception e) {
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
        logger.info(userManager.toString());
        String username = userManager.getUsername();
        logger.info(username);
        student = studentBean.getStudent( username );
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    
    
}
