/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import dtos.InstituicaoDTO;
import dtos.PropostaDTO;
import dtos.TeacherDTO;
import ejbs.InstituicaoBean;
import ejbs.PropostaBean;
import ejbs.TeacherBean;
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
    private PropostaBean PropostaBean;
    
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
}
