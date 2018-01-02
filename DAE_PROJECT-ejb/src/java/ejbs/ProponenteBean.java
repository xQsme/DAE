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
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author The Law
 */
@Stateless
@Path("/proponenete")
public class ProponenteBean extends Bean<Proponente> {

    public Collection<ProponenteDTO> getAlProponentes() {
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
    
    @GET
    @PermitAll
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("proponenete/{code}")
    public Collection<ProponenteDTO> getPropostaProponentes(int code) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Proposta does not exists.");
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
