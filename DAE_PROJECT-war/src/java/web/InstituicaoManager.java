/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import dtos.DocumentDTO;
import dtos.InstituicaoDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import ejbs.InstituicaoBean;
import ejbs.ProponenteBean;
import ejbs.PropostaBean;
import ejbs.StudentBean;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;

/**
 *
 * @author rick_sanchez
 */
@ManagedBean
@SessionScoped
public class InstituicaoManager implements Serializable {
    
    @EJB
    private ProponenteBean proponenteBean;
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private InstituicaoBean instituicaoBean;
    @EJB
    private StudentBean studentBean;
    
    @ManagedProperty(value="#{userManager}") // this references the @ManagedBean
    private UserManager userManager;
    
    private static final Logger logger = Logger.getLogger("web.StudentManager");
    
    private InstituicaoDTO instituicao;
    
    private PropostaDTO currentProposta;
    private DocumentDTO currentDocumento;
    private PropostaDTO newProposta;
    
    private UIComponent component;
    
    public InstituicaoManager() {
        newProposta = new PropostaDTO();
    }
    
    @PostConstruct
    public void Init(){
        setUpInstituicao();
    }
    
    public Collection<PropostaDTO> getAllPropostas() {
        try {
            return propostaBean.getAllPropostas();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public Collection<PropostaDTO> getInstituicaoPropostas() {
        try{
            return instituicaoBean.getInstituicaoPropostas(instituicao.getUsername());
        }
        catch(Exception e){
            FacesExceptionHandler.handleException(e, e.getMessage(), logger);
            return null;
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
        try {
            return propostaBean.getAllTiposTrabalhos();
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
            return null;
        }
    }
    
    public void removerProposta(){
        try {
            instituicaoBean.removePropostaInstituicao(currentProposta.getCode(), instituicao.getUsername());
            for(PropostaDTO p : getInstituicaoPropostas()){
                if (p.getCode() == currentProposta.getCode()) {
                    instituicaoBean.removePropostaInstituicao(p.getCode(), instituicao.getUsername());                    
                }
            }

        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }
    
    public void adicionarProposta(){
        try {
            instituicaoBean.addPropostaInstituicao(currentProposta.getCode(), instituicao.getUsername());
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
        logger.info(userManager.toString());
        String username = userManager.getUsername();
        logger.info(username);
        instituicao = instituicaoBean.getInstituicao( username );
        logger.info(instituicao.toString());
                /*
        ProponenteDTO proponente  = proponenteBean.getProponente(username );
        logger.info(proponente.toString());
        logger.info(" " + proponente.getPropostas().size());

        proponente.s
        teacher.set*/
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
        return "/instituicao/propostas/mine.xhtml?faces-redirect=true";
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
            instituicaoBean.addPropostaInstituicao(code, instituicao.getUsername());
            newProposta.reset();
            //teacher.addProposta(new PropostaDTO (p.getCode(), p.getTitulo(), p.getTipoDeTrabalho(), p.getAreasCientificas(), p.getResumo(), p.getProponentes().) );
            setUpInstituicao();
        } catch (EntityAlreadyExistsException e) {
            FacesExceptionHandler.handleException(e, e.getMessage() + "\t" + code , component, logger);
            return null;
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", component, logger);
            return null;
        }
        return "/instituicao/propostas/mine.xhtml?faces-redirect=true";
    }
    
    public void removeProposta(){ 
        try {
            propostaBean.remove(currentProposta.getCode());
            setUpInstituicao();
        } catch (EntityDoesNotExistsException ex) {
            Logger.getLogger(AdministratorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
    }

    public DocumentDTO getCurrentDocumento() {
        return currentDocumento;
    }

    public void setCurrentDocumento(DocumentDTO currentDocumento) {
        this.currentDocumento = currentDocumento;
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
    
}
