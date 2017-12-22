/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbs;

import dtos.ProponenteDTO;
import entities.Proponente;
import entities.Proposta;
import exceptions.EntityDoesNotExistsException;
import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

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
}
