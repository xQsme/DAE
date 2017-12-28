package ejbs;

import auxiliar.TipoDeInstituicao;
import auxiliar.TipoDeTrabalho;
import dtos.PropostaDTO;
import entities.Instituicao;
import entities.Proponente;
import entities.Proposta;
import entities.Student;
import exceptions.BibliografiaIsFullException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.Utils;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

@Stateless
public class PropostaBean extends Bean<Proposta> {

    public Proposta create(int code, String titulo, String tipoDeTrabalho, String resumo, String planoDeTrabalho, String local,String orcamento, String apoios)
        throws EntityAlreadyExistsException, EntityDoesNotExistsException, MyConstraintViolationException {
        
        try {
            if (em.find(Proposta.class, code) != null) {
                throw new EntityAlreadyExistsException("A proposal with that code already exists.");
            }
            
            Proposta proposta = new Proposta(code, titulo, tipoDeTrabalho, resumo, planoDeTrabalho, local, orcamento, apoios);
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

    public Collection<PropostaDTO> getAllPropostas() {
        try {
            //return getAll(PropostaDTO.class);
            return toPropostaDTOcollection(getAll());
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
    public static Collection<PropostaDTO> toPropostaDTOcollection(Collection<Proposta> propostas) {
        LinkedList<PropostaDTO> dtos = new LinkedList<>();
        for(Proposta p : propostas){
            dtos.add(new PropostaDTO(
                p.getCode(),
                p.getTitulo(),
                p.getTipoDeTrabalho(),
                p.getAreasCientificas(),
                p.getResumo(),
                p.getProponentes(),
                p.getCandidatos(),
                p.getObjetivos(),
                p.getBibliografia(),
                p.getPlanoDeTrabalhos(),
                p.getLocal(),
                p.getRequisitos(),
                p.getOrcamento(),
                p.getApoios(),
                p.getEstado(),
                p.getObservacao()   
            ));
        }
        return dtos;
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
    
    public static Collection<String> getAllTiposTrabalhos() {
        LinkedList<String> tipos = new LinkedList<>();
        tipos.add(TipoDeTrabalho.Dissertação.toString());
        tipos.add(TipoDeTrabalho.Estágio.toString());
        tipos.add(TipoDeTrabalho.Projeto.toString());
        return tipos;
    }
    
    public void update(int code, String titulo, String tipoDeTrabalho, String resumo, String planoDeTrabalhos, String local, String orcamento, String apoios) 
            throws EntityDoesNotExistsException, MyConstraintViolationException {
        try {
            Proposta proposta = em.find(Proposta.class, code);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            proposta.setTitulo(titulo);
            proposta.setTipoDeTrabalho(tipoDeTrabalho);
            proposta.setResumo(resumo);
            proposta.setPlanoDeTrabalhos(planoDeTrabalhos);
            proposta.setLocal(local);
            proposta.setOrcamento(orcamento);
            proposta.setApoios(apoios);
            em.merge(proposta);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(Utils.getConstraintViolationMessages(e));
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
    
/*
    public Collection<PropostaDTO> getStudentSubjects(String username) throws EntityDoesNotExistsException {
        try {
            Student student = em.find(Student.class, username);
            
            if (student == null) {
                throw new EntityDoesNotExistsException("Student does not exists.");
            }

            return toDTOs(student.getSubjects(), PropostaDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection<PropostaDTO> getCourseSubjects(int courseCode) throws EntityDoesNotExistsException {
        try {
            Course course = em.find(Course.class, courseCode);
            
            if (course == null) {
                throw new EntityDoesNotExistsException("Course does not exists.");
            }
            
            return toDTOs(course.getSubjects(), PropostaDTO.class);
        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }
*/
}
