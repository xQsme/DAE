package web;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Yvtq8
 */
import dtos.PropostaDTO;
import ejbs.EmailBean;
import entities.MembroCCP;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.MessagingException;


@ManagedBean
@SessionScoped
public class EmailManager implements Serializable{
    
    @EJB
    private EmailBean email;
    
    public void removeProposta(MembroCCP memberCCP, PropostaDTO proposta, List<String> recipients) throws MessagingException{
        String msg = "<strong>A proposta:</strong> "+proposta.getTitulo()
                    +".<br><br><strong>Com descricao:</strong> "+ proposta.getResumo()+"."
                    +".<br><br><strong>Foi removida por:</strong> "+ memberCCP.getName()+".";
        email.send(memberCCP.getEmail(), memberCCP.getPassword(), "Remoção da Proposta "+proposta.getTitulo(), msg, recipients);               
    }
    
    public void removeProva(MembroCCP memberCCP, PropostaDTO proposta, List<String> recipients) throws MessagingException{
        String msg = "<strong>A prova:</strong> "+proposta.getTitulo()
                    +".<br><br><strong>Com descricao:</strong> "+ proposta.getResumo()+"."
                    +".<br><br><strong>Foi removida por:</strong> "+ memberCCP.getName()+".";
        email.send(memberCCP.getEmail(), memberCCP.getPassword(), "Remoção da Prova "+proposta.getTitulo(), msg, recipients);               
    }
    
    
    public void updateProva(MembroCCP memberCCP, PropostaDTO proposta, List<String> alterations, List<String> recipients) throws MessagingException{
        String msg = "<strong>A prova:</strong> "+proposta.getTitulo()
                    +".<br><br><strong>Com descricao:</strong> "+ proposta.getResumo()+"."
                    +".<br><br><strong>Foi atualizada por:</strong> "+ memberCCP.getName()+".";
                            
                    if (alterations!=null){
                        msg+="<br><br><strong>Com as seguintes alteracoes:</strong>";
                        for(String alteration: alterations){
                            msg+="<br>"+alteration;
                        }
                    } 
        
        email.send(memberCCP.getEmail(), memberCCP.getPassword(), "Alteração da Prova "+proposta.getTitulo(), msg, recipients);               
    }
    
    public void validateProposta(MembroCCP memberCCP, PropostaDTO proposta, List<String> recipients) throws MessagingException{
        String msg = "<strong>A proposta:</strong> "+proposta.getTitulo()
                    +".<br><br><strong>Com descricao:</strong> "+ proposta.getResumo()+"."
                    +".<br><br><strong>Foi avaliada por:</strong> "+ memberCCP.getName()+".";
                  
        if (proposta.getIntEstado()==1) msg+= "<br><br>Sendo esta <strong>" + proposta.getEstado() +"</strong>."; 
        else if (proposta.getIntEstado()==-1) msg+= "<br><br>Sendo esta infelizmente <strong>"+ proposta.getEstado() +"</strong>.";
            
        msg+= (proposta.getObservacao()!=null && !proposta.getObservacao().isEmpty())?
            ("<br><strong>Observação:</strong> "+ proposta.getObservacao())+".": "<br><br>Não deixou Observação.";
        
        email.send(memberCCP.getEmail(), memberCCP.getPassword(), "Validação da Proposta "+proposta.getTitulo(), msg, recipients);               
    }     
    
    public void serializeProposta(MembroCCP memberCCP, PropostaDTO proposta, List<String> recipients) throws MessagingException{
        String msg = "<strong>A proposta:</strong> "+proposta.getTitulo()
                    +".<br><br><strong>Com descricao:</strong> "+ proposta.getResumo()+"."
                    +".<br><br><strong>Foi serializada por:</strong> "+ memberCCP.getName()+".";
                  
        msg+= "<br><br>Sendo esta assim passada a estado <strong>" + proposta.getEstado() +"</strong>.";
        
       
        email.send(memberCCP.getEmail(), memberCCP.getPassword(), "Serialização da Proposta "+proposta.getTitulo(), msg, recipients);               
    }     
}
