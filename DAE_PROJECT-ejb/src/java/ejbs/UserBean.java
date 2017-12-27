package ejbs;

import dtos.StudentDTO;
import dtos.TeacherDTO;
import dtos.UserDTO;
import entities.Proposta;
import entities.Student;
import entities.Teacher;
import entities.User;
import exceptions.BibliografiaIsFullException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityDoesNotExistsException;
import exceptions.MyConstraintViolationException;
import exceptions.StudentCandidaturasFullException;
import exceptions.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

@Stateless
public class UserBean extends Bean<User> {
    
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
    
    @Override
    protected Collection<User> getAll() {
        return em.createNamedQuery("getAllUsers").getResultList();
    }
}
