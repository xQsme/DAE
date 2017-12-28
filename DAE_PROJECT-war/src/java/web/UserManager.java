package web;

import dtos.UserDTO;
import ejbs.StudentBean;
import ejbs.UserBean;
import entities.UserGroup;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import util.Security;

@ManagedBean
@SessionScoped
public class UserManager implements Serializable {

    private String username;
    private String password;
        
    private static final Logger logger = Logger.getLogger("web.UserManager");
        
    public UserManager() {
        
    }
    
    public String redirect() {
        if (isUserInRole(UserGroup.GROUP.Instituicao)) {
            logger.info("Instituicao");
            return "/faces/instituicao/index.xhtml?faces-redirect=true";
        }
        
        if (isUserInRole(UserGroup.GROUP.MembroCCP)) {
            logger.info("Admin");
            return "/faces/admin/index.xhtml?faces-redirect=true";
        }
        
        if (isUserInRole(UserGroup.GROUP.Student)) {
            logger.info("Estudante");
            return "/faces/student/index.xhtml?faces-redirect=true";
        }
        
        if (isUserInRole(UserGroup.GROUP.Teacher)) {
            logger.info("Professor");
            return "/faces/teacher/index.xhtml?faces-redirect=true";
        }
        logger.info("erro");
        return "error?faces-redirect=true";
    }
    
    

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request
                = (HttpServletRequest) context.getExternalContext().getRequest();
        
        try {
            request.login(username, password);
        } catch (ServletException e) {
            logger.log(Level.WARNING, e.getMessage());
            return "error?faces-redirect=true";
        }
        
        return redirect();
    }
    
    

    public boolean isUserInRole(UserGroup.GROUP group) {
        return isUserRole(group.toString());
    }
    
    public boolean isUserRole(String role) {
        logger.info("isUserInRole ("+ role +")");
        logger.info("isSomeUserAuthenticated = " + isSomeUserAuthenticated());
        logger.info("isUserInRole = " + FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role));
        return (isSomeUserAuthenticated() && FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role));
    }

    public boolean isSomeUserAuthenticated() {
        return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null;
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        // remove data from beans:
        for (String bean : context.getExternalContext().getSessionMap().keySet()) {
            context.getExternalContext().getSessionMap().remove(bean);
        }
        // destroy session:
        HttpSession session
                = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        // using faces-redirect to initiate a new request:
        return "/login.xhtml?faces-redirect=true";
    }

    public String clearLogin() {
        if (isSomeUserAuthenticated()) {
            logout();
        }
        return "login.xhtml?faces-redirect=true";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
