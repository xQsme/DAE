package ejbs;

import auxiliar.TipoDeTrabalho;
import dtos.DocumentDTO;
import dtos.ProponenteDTO;
import dtos.PropostaDTO;
import dtos.StudentDTO;
import entities.Document;
import entities.Proponente;
import entities.Proposta;
import entities.Student;
import exceptions.BibliografiaIsFullException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.SortEvent;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@ManagedBean
@ViewScoped
@Path("/propostas")
//@DeclareRoles({"MembroCCP", "Instituicao", "Teacher"})
public class PropostaBean extends Bean<Proposta> {

    @POST
    @RolesAllowed({"Instituicao", "Teacher"})
    @Path("")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(PropostaDTO prop) throws EntityDoesNotExistsException, MyConstraintViolationException {
        
        try { 
            Proposta proposta = new Proposta(
                    prop.getTitulo(), 
                    prop.getTipoDeTrabalho(), 
                    prop.getResumo(), 
                    prop.getPlanoDeTrabalhos(),
                    prop.getLocal(),
                    prop.getOrcamento(), 
                    prop.getApoios());
            em.persist(proposta);
            
            return Response.status(201).entity(new GenericEntity<Proposta>(proposta) {}).
                    type(MediaType.APPLICATION_XML_TYPE).build();
        
        }catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public Proposta create(int code, String titulo, String tipoDeTrabalho, String resumo, String planoDeTrabalho, String local,String orcamento, String apoios)
        throws EntityAlreadyExistsException, EntityDoesNotExistsException, MyConstraintViolationException {
        
        try {
            if (em.find(Proposta.class, code) != null) {
                throw new EntityAlreadyExistsException("A proposal with that code already exists.");
            }
            
            Proposta proposta = new Proposta(titulo, tipoDeTrabalho, resumo, planoDeTrabalho, local, orcamento, apoios);
            em.persist(proposta);
            return proposta;
        
        } catch (EntityAlreadyExistsException e){//| EntityDoesNotExistsException e) {
            throw e;           
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public int getNextCode(){
        try {
            Query query = em.createNativeQuery("SELECT max(code) FROM Proposta p");
            int maxCode = ((Integer) query.getSingleResult()).intValue();
            return maxCode + 1;
            
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        } 
    }

    @GET
    @PermitAll
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<PropostaDTO> getAllPropostas() {
        try {
            return getAll(PropostaDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public Collection<PropostaDTO> getAllAccepted() {
        LinkedList<PropostaDTO> propostas = new LinkedList<>();
        try {
            for(PropostaDTO p :getAll(PropostaDTO.class)){
                if(p.getEstado() == 1){
                    propostas.add(p);
                }
            }
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return propostas;
    }
    
    public Collection<PropostaDTO> getAllProvas() {
        LinkedList<PropostaDTO> propostas = new LinkedList<>();
        try {
            for(PropostaDTO p :getAll(PropostaDTO.class)){
                if(p.getEstado() == 2){
                    propostas.add(p);
                }
            }
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return propostas;
    }
    
    public Collection<PropostaDTO> getAllFinalizado() {
        LinkedList<PropostaDTO> propostas = new LinkedList<>();
        try {
            for(PropostaDTO p :getAll(PropostaDTO.class)){
                if(p.getEstado() == 3){
                    propostas.add(p);
                }
            }
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return propostas;
    }
    
    @Override
    protected Collection<Proposta> getAll() {
        return em.createNamedQuery("getAllPropostas").getResultList();
    }
       
    public void remove(int code) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            for(Student s : proposta.getCandidatos()){
                s.removeCandidatura(proposta);
            }
            for(Proponente p : proposta.getProponentes()){
                p.removeProposta(proposta);
            }
            em.remove(proposta);
            
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }    
    
    public void addAreaCientifica(int propostaCode, String areaCientifica) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.addAreaCientifica(areaCientifica);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void addObjetivo(int propostaCode, String objetivo) throws EntityDoesNotExistsException{
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.addObjetivo(objetivo);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void addReferencia(int propostaCode, String referencia) throws EntityDoesNotExistsException, BibliografiaIsFullException{
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            if (proposta.getBibliografia().size() > 4) {
                throw new BibliografiaIsFullException("A Bibliografia so pode ter 5 referencia no maximo!");
            }
            proposta.addReferencia(referencia);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void addRequsito(int propostaCode, String requisito) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.addRequisito(requisito);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void addEstado(int propostaCode, int estado) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Estado de Proposta: Invalido ");
            }
            proposta.setEstado(estado);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public void addObservacao(int propostaCode, String observacao) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Proposta Invalida ");
            }
            proposta.setObservacao(observacao);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    
    /*@PUT
    @PermitAll
    @Path("/{code}/validacao")
    //@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
    
    @PUT
    @Path("/{code}/validacao")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response addValidacao(@PathParam("code") int propostaCode, PropostaDTO proposta) throws EntityDoesNotExistsException{   
        if(proposta==null){
            return Response.status(400).entity("Please provide the employee name !!").build();
        }
        addEstado(propostaCode, proposta.getEstado());
        addObservacao(propostaCode, proposta.getObservacao());
        
        return Response.ok().build();
    }
    
    public static Collection<Integer> getAllPropostaEstados() {
        LinkedList<Integer> tipos = new LinkedList<>();
        tipos.add(-1);
        tipos.add(1);
        return tipos;
    }
    
    public List<String> getAlterations(int code, String titulo, String tipoDeTrabalho, String resumo, String planoDeTrabalhos, String local, String orcamento, String apoios) 
            throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            
            Proposta newProposta = new Proposta();
            newProposta.setTitulo(titulo);
            newProposta.setTipoDeTrabalho(tipoDeTrabalho);
            newProposta.setResumo(resumo);
            newProposta.setPlanoDeTrabalhos(planoDeTrabalhos);
            newProposta.setLocal(local);
            newProposta.setOrcamento(orcamento);
            newProposta.setApoios(apoios);
            
            LinkedList<String> alteracoes= new LinkedList();
            if (!newProposta.getTitulo().equals(proposta.getTitulo()))
                alteracoes.add("Titulo: "+newProposta.getTitulo());
            
            if (!newProposta.getTipoDeTrabalho().equals(proposta.getTipoDeTrabalho()))
                alteracoes.add("Tipo De Trabalho: "+newProposta.getTipoDeTrabalho());
            
            if (!newProposta.getResumo().equals(proposta.getResumo()))
                alteracoes.add("Resumo: "+newProposta.getResumo());
            
            if (!newProposta.getPlanoDeTrabalhos().equals(proposta.getPlanoDeTrabalhos()))
                alteracoes.add("Plano de Trabalhos: "+newProposta.getPlanoDeTrabalhos());
            
            if (!newProposta.getLocal().equals(proposta.getLocal()))
                alteracoes.add("Local: "+newProposta.getLocal());
            
            if (!newProposta.getOrcamento().equals(proposta.getOrcamento()))
                alteracoes.add("Orcamento: "+newProposta.getOrcamento());
            
            if (!newProposta.getApoios().equals(proposta.getApoios())) 
                alteracoes.add("Apoios: "+newProposta.getApoios());
            
            return alteracoes;
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @PUT
    @RolesAllowed({"Student", "Teacher", "Instituicao"})
    @Path("{code}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void update(@PathParam("code") int code, PropostaDTO prop) 
            throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.setTitulo(prop.getTitulo());
            proposta.setTipoDeTrabalho(prop.getTipoDeTrabalho());
            proposta.setResumo(prop.getResumo());
            proposta.setPlanoDeTrabalhos(prop.getPlanoDeTrabalhos());
            proposta.setLocal(prop.getLocal());
            proposta.setOrcamento(prop.getOrcamento());
            proposta.setApoios(prop.getApoios());
            em.merge(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @GET
    @RolesAllowed({"Student", "Instituicao", "Teacher"})
    @Path("proponentes/{code}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<ProponenteDTO> getPropostaProponentes(@PathParam("code") int code) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Proposta does not exist.");
            }

            return toDTOs(proposta.getProponentes(), ProponenteDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public DocumentDTO getDocument(int documentId) throws EntityDoesNotExistsException {
        Document doc = em.find(Document.class, documentId);
            
        if (doc == null)
            throw new EntityDoesNotExistsException();

        return toDTO(doc, DocumentDTO.class);
    }
    
    @GET
    @RolesAllowed({"Student", "Instituicao", "Teacher"})
    @Path("documents/{code}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<DocumentDTO> getDocuments(@PathParam("code") int code) throws EntityDoesNotExistsException {
        try {
            List<Document> docs = em.createNamedQuery("getDocumentsOfProposta", Document.class).setParameter("code", code).getResultList();
            return toDTOs(docs, DocumentDTO.class);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @POST
    @RolesAllowed({"Student"})
    @Path("addDocument/{code}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void addDocument(@PathParam("code") int code, DocumentDTO doc) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Não existe proposta com o codigo " + code + ".");
            }

            Document document = new Document(doc.getFilepath(), doc.getDesiredName(), doc.getMimeType(), proposta, false);
            em.persist(document);
            proposta.addDocument(document);

        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    @PUT
    @RolesAllowed({"Student"})
    @Path("atualizarDocument/{code}/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void atualizarDocumento(@PathParam("code") int code, @PathParam("id") int id, DocumentDTO doc) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Não existe proposta com o codigo " + code + ".");
            }
            Document document = em.find(Document.class, id);
            if(document == null){
                throw new EntityDoesNotExistsException("Não existe documento com o id " + id + ".");
            }
            if(!document.getFilepath().equals(doc.getFilepath())){
                File f = new File(document.getFilepath());
                f.delete();
            }
            document.setFilepath(doc.getFilepath());
            document.setDesiredName(doc.getDesiredName());
            document.setMimeType(doc.getMimeType());
            em.persist(document);

        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    @DELETE
    @RolesAllowed({"Student"})
    @Path("deleteDocument/{code}/{id}")
    public void removerDocumento(@PathParam("code") int code, @PathParam("id") int id) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Não existe proposta com o codigo " + code + ".");
            }
            Document document = em.find(Document.class, id);
            if (document == null) {
                throw new EntityDoesNotExistsException("O documento com id " + id + " não existe.");
            }
            proposta.removeDocument(document);
            em.remove(document);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    } 
    
    public Collection<PropostaDTO> getStudentPropostas(String username) throws EntityDoesNotExistsException{
        try {
            Student student = em.find(Student.class, username);
            
            if (student == null) {
                throw new EntityDoesNotExistsException("Student does not exists.");
            }

            return toDTOs(student.getCandidaturas(), PropostaDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public void finalizarDocumento(int code, DocumentDTO doc) throws EntityDoesNotExistsException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("Não existe proposta com o codigo " + code + ".");
            }

            Document document = new Document(doc.getFilepath(), doc.getDesiredName(), doc.getMimeType(), proposta, true);
            em.persist(document);
            proposta.addDocument(document);
            proposta.setEstado(3);

        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    @GET
    @RolesAllowed({"Student", "Instituicao", "Teacher"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("students/{code}")
    public Collection<StudentDTO> getCandidatos(@PathParam("code") int code) throws EntityDoesNotExistsException {
        Proposta proposta = em.find(Proposta.class, code);
        if(proposta == null){
            throw new EntityDoesNotExistsException("Proposta com codigo " + code + " não existe.");
        }
        return toDTOs(proposta.getCandidatos(), StudentDTO.class);
    }
    
    
}
