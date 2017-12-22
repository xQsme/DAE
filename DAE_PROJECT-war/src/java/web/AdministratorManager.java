/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import dtos.InstituicaoDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import dtos.TeacherDTO;
import ejbs.InstituicaoBean;
import ejbs.PropostaBean;
import ejbs.TeacherBean;
import ejbs.StudentBean;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author rick_sanchez
 */
@ManagedBean
@SessionScoped
public class AdministratorManager implements Serializable {
    
    @EJB
    private InstituicaoBean instituicaoBean;
    @EJB
    private TeacherBean teacherBean;
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private StudentBean studentBean;
    
    private static final Logger logger = Logger.getLogger("web.AdministratorManager");
    
    private InstituicaoDTO currentInstituicao;
    private InstituicaoDTO newInstituicao;
    private TeacherDTO currentTeacher;
    private TeacherDTO newTeacher;
    private PropostaDTO currentProposta;
    private PropostaDTO newProposta;
    private StudentDTO currentStudent;
    private StudentDTO newStudent;
    
    public AdministratorManager() {
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
    
    public Collection<StudentDTO> getAllStudents() {
        try {
            return studentBean.getAllStudents();
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
        return null;
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

}
