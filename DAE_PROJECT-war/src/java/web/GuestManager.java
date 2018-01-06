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
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
    
    private Client client;
    
    //Primefaces require a filterList to temporarly store the values, h5!
    public List<Object> filterList;
    public void setFilterList(List<Object> filter){
        filterList=filter;
    }
    public List<Object> getFilterList(){
        return filterList;
    }
    
    @PostConstruct
    public void Init(){
        client=ClientBuilder.newClient();
    };
    
    public Collection<PropostaDTO> getAllPropostas() {
        Collection<PropostaDTO> propostasFinalizadas = new LinkedList<>();
        try {
            Collection<PropostaDTO> propostas = client.target(URILookup.getBaseAPI())
                                                .path("/propostas")
                                                .request(MediaType.APPLICATION_XML)
                                                .get(new GenericType<List<PropostaDTO>>() {});         
            for(PropostaDTO p : propostas){
                System.out.println(p.getTitulo());
                if(p.getEstado() == 3){
                    propostasFinalizadas.add(p);
                }
            }
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unexpected error! Try again latter!", logger);
        }
        
        filterList=(List<Object>)(List<?>)propostasFinalizadas;
        return propostasFinalizadas; 
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
