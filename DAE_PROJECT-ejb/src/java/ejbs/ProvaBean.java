/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbs;

import dtos.UserDTO;
import entities.Documento;
import entities.Proponente;
import entities.Proposta;
import entities.Prova;
import entities.Student;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Yvtq8
 */
@Stateless
public class ProvaBean extends Bean<Prova> {
    
    private static final Logger logger = Logger.getLogger("ejb.userBean");

    @PersistenceContext
    private EntityManager em;

    public Collection<UserDTO> getAllUsers() {
        try {
            return getAll(UserDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void create(String titulo, Calendar data, String local, 
            Integer proposta, String student, List<String> juizes, 
            List<String> documentos) throws EntityAlreadyExistsException, EntityDoesNotExistsException {
        try {
            
            Proposta currentProposta = em.find(Proposta.class, proposta);
            if (currentProposta == null) {
                throw new EntityDoesNotExistsException("A user with that username not exists.");
            }
            
            Student currentStudent = em.find(Student.class, student);
            if (currentStudent == null) {
                throw new EntityDoesNotExistsException("A user with that username not exists.");
            }
            
            List<Proponente> currentProponentes = new LinkedList();
            for(String juiz:juizes){
                Proponente tempProponente=em.find(Proponente.class, juiz);
                if (tempProponente != null) {
                    currentProponentes.add(em.find(Proponente.class, juiz));   
                }else throw new EntityDoesNotExistsException("A user with that username not exists.");
            }
            
            
            List<Documento> currentDocumentos = new LinkedList();
            for(String doc:documentos){
                Documento tempDocumentos=em.find(Documento.class, doc);
                if (tempDocumentos != null) {
                    currentDocumentos.add(em.find(Documento.class, doc));   
                }else throw new EntityDoesNotExistsException("A user with that username already exists.");
            }
            
         em.persist(new Prova(titulo, data, local, currentProposta, currentStudent, currentProponentes, currentDocumentos));
        } catch (EntityDoesNotExistsException e){
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @Override
    protected Collection<Prova> getAll() {
        return em.createNamedQuery("getAllProvas").getResultList();
    }
}