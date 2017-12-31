/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbs;

import dtos.MembroCCPDTO;
import dtos.TeacherDTO;
import entities.MembroCCP;
import entities.Proposta;
import entities.Teacher;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.ProposalStateAlreadyDefineException;
import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Yvtq8
 */


@Stateless
public class MembroCCPBean{
    
    @PersistenceContext
    private EntityManager em;
    
    public void create(String username, String password, String name, String email)
            throws EntityAlreadyExistsException {
        try {
            if (em.find(User.class, username) != null) {
                throw new EntityAlreadyExistsException("A user with that username already exists.");
            }
            em.persist(new MembroCCP(username, password, name, email));
        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public MembroCCP find(String username){
        try {
            MembroCCP membroCCP = em.find(MembroCCP.class, username);
            if (membroCCP == null) {
                throw new EntityDoesNotExistsException("Invalid MembroCPP Username");
            }
            return membroCCP; 
        } catch (EntityDoesNotExistsException e) {
            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return null;
    }
    
    //In case the Menber doesnt leave a comment
    public void validarProposta(String username, int propostaCode, Integer validar) 
            throws EntityDoesNotExistsException, ProposalStateAlreadyDefineException{
              validarProposta(username, propostaCode, validar, null);
    }
    
    //In case the menber intends to leave a comment
    public void validarProposta(String username, int propostaCode, Integer validar, String observacao) 
            throws EntityDoesNotExistsException, ProposalStateAlreadyDefineException{
        try {      
            MembroCCP menbroCCP = em.find(MembroCCP.class, username);
            if (username == null) {
                throw new EntityDoesNotExistsException("There is no MenberCCP with that username.");
            }
            
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            
            if (validar==null){
                throw new NullPointerException("Invalid State Parameter");
            }
            
            Integer estado = proposta.getEstado();
            if (estado != null){
                throw new ProposalStateAlreadyDefineException("The current proposal already has state");
            }
            
            proposta.setEstado(validar);
            proposta.setObservacao(observacao);//This can be null
            
            em.merge(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (ProposalStateAlreadyDefineException e){
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }        
    }
}
