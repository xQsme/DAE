/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import dtos.DocumentDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import ejbs.ProponenteBean;
import ejbs.PropostaBean;
import ejbs.StudentBean;
import exceptions.EntityDoesNotExistsException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;

/**
 *
 * @author rick_sanchez
 */
@ManagedBean
@SessionScoped
public class GuestManager implements Serializable {
    
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private ProponenteBean proponenteBean;
    @EJB
    private StudentBean studentBean;
    
    private static final Logger logger = Logger.getLogger("web.GuestManager");
    
    private PropostaDTO currentProposta;
    
    private UIComponent component;
    
    public GuestManager() {
    }
    
    public Collection<PropostaDTO> getAllPropostas() {
        LinkedList<PropostaDTO> propostas = new LinkedList<>();
        try {
            for(PropostaDTO p : propostaBean.getAllPropostas()){
                if(p.getEstado() == 3){
                    propostas.add(p);
                }
            }
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        return propostas;
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
    
}
