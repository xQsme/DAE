/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import dtos.InstituicaoDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.TeacherDTO;
import ejbs.InstituicaoBean;
import ejbs.ProponenteBean;
import ejbs.PropostaBean;
import ejbs.TeacherBean;
import entities.Proponente;
import java.io.Serializable;
import java.util.Collection;
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
    private ProponenteBean proponenteBean;
    
    private static final Logger logger = Logger.getLogger("web.AdministratorManager");
    
    private InstituicaoDTO currentInstituicaoDTO;
    private InstituicaoDTO newInstituicaoDTO;
    private TeacherDTO currentTeacherDTO;
    private TeacherDTO newTeacherDTO;
    private PropostaDTO currentPropostaDTO;
    private PropostaDTO newPropostaDTO;
    
    public AdministratorManager() {
        newInstituicaoDTO = new InstituicaoDTO();
        newTeacherDTO = new TeacherDTO();
        newPropostaDTO = new PropostaDTO();
    }
    
    public Collection<InstituicaoDTO> getAllInstitutions() {
        try {
            return instituicaoBean.getAllInstitutions();
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
    
        public InstituicaoDTO getCurrentInstituicaoDTO() {
        return currentInstituicaoDTO;
    }

    public void setCurrentInstituicaoDTO(InstituicaoDTO currentInstituicaoDTO) {
        this.currentInstituicaoDTO = currentInstituicaoDTO;
    }

    public InstituicaoDTO getNewInstituicaoDTO() {
        return newInstituicaoDTO;
    }

    public void setNewInstituicaoDTO(InstituicaoDTO newInstituicaoDTO) {
        this.newInstituicaoDTO = newInstituicaoDTO;
    }

    public TeacherDTO getCurrentTeacherDTO() {
        return currentTeacherDTO;
    }

    public void setCurrentTeacherDTO(TeacherDTO currentTeacherDTO) {
        this.currentTeacherDTO = currentTeacherDTO;
    }

    public TeacherDTO getNewTeacherDTO() {
        return newTeacherDTO;
    }

    public void setNewTeacherDTO(TeacherDTO newTeacherDTO) {
        this.newTeacherDTO = newTeacherDTO;
    }

    public PropostaDTO getCurrentPropostaDTO() {
        return currentPropostaDTO;
    }

    public void setCurrentPropostaDTO(PropostaDTO currentPropostaDTO) {
        this.currentPropostaDTO = currentPropostaDTO;
    }

    public PropostaDTO getNewPropostaDTO() {
        return newPropostaDTO;
    }

    public void setNewPropostaDTO(PropostaDTO newPropostaDTO) {
        this.newPropostaDTO = newPropostaDTO;
    }
    
    public Collection<ProponenteDTO> getCurrentPropostaProponentes(){
        try {
            return proponenteBean.getPropostaProponentes(currentPropostaDTO.getCode());
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
}
