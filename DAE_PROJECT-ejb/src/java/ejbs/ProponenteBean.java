/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbs;

import dtos.ProponenteDTO;
import dtos.StudentDTO;
import entities.Proponente;
import entities.Proposta;
import entities.Student;
import exceptions.EntityDoesNotExistsException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author The Law
 */
@Stateless
@LocalBean
public class ProponenteBean extends Bean<Proponente> {

    public Collection<ProponenteDTO> getAllInstitutions() {
        try {
            return getAll(ProponenteDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @Override
    protected Collection<Proponente> getAll() {
        return em.createNamedQuery("getAllProponentes").getResultList();
    }
    
    public Collection<ProponenteDTO> getPropostaProponentes(int code) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Student does not exists.");
            }

            return toDTOs(proposta.getProponentes(), ProponenteDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public ProponenteDTO getProponente(String username) {
        try {
            Query query = em.createQuery("SELECT p FROM Proponente p where p.username = '" + username + "'", Proponente.class);
            ArrayList<ProponenteDTO> proponentes = (ArrayList<ProponenteDTO>) toDTOs(query.getResultList(), ProponenteDTO.class);
            return proponentes.get(0);            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        } 
    }
}
