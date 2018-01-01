/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbs;

import entities.MembroCCP;
import entities.Proposta;
import entities.Student;
import entities.Teacher;
import entities.User;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.TeacherAlreadyAssignedException;
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
            throws EntityDoesNotExistsException{
              validarProposta(username, propostaCode, validar, null);
    }
    
    //In case the menber intends to leave a comment
    public void validarProposta(String username, int propostaCode, Integer validar, String observacao) 
            throws EntityDoesNotExistsException{
        try {      
            MembroCCP membroCCP = em.find(MembroCCP.class, username);
            if (membroCCP == null) {
                throw new EntityDoesNotExistsException("There is no MemberCCP with that username.");
            }
            
            Proposta proposta = em.find(Proposta.class, propostaCode);
            if (proposta == null) {
                throw new EntityDoesNotExistsException("There is no proposal with that code.");
            }
            
            if (validar< -1 || validar > 2){
                throw new NullPointerException("Invalid State Parameter");
            }
            
            proposta.setEstado(validar);
            proposta.setObservacao(observacao);//This can be null
            
            em.merge(proposta);
        }catch (EntityDoesNotExistsException e) {
            throw e;
        }catch (Exception e) {
            throw new EJBException(e.getMessage());
        }        
    }
    
    public void addProfessorOrientador(String teacherUsername, String studentUsername) throws EntityDoesNotExistsException, TeacherAlreadyAssignedException {
        try {
            Teacher teacher = em.find(Teacher.class, teacherUsername);
            if (teacher == null) {
                throw new EntityDoesNotExistsException("There is no teacher with such username.");
            }
            
            Student student = em.find(Student.class, studentUsername);
            if (student == null) {
                throw new EntityDoesNotExistsException("There is no student with such username.");
            }
            for(Teacher t : student.getGuidingTeachers()){
                if (t.getUsername().equalsIgnoreCase(teacherUsername)){
                    throw new TeacherAlreadyAssignedException("This Teacher is already guiding this Student.");
                }
            }
            
            
            teacher.addGuidedStudent(student);
            student.addGuidingTeacher(teacher);


        } catch (EntityDoesNotExistsException e) {
            throw e;
        } catch (Exception e) {
            if (e != null) {
                throw new EJBException(e.toString() +  "Exception");
            }
            throw new EJBException("Exception a excecão ou a mensagem dela vem a null");
        }
    }
}
