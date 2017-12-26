package web;

import dtos.UserDTO;
import ejbs.UserBean;
import exceptions.EntityAlreadyExistsException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator
public class UsernameValidator implements Validator {
    
    @EJB
    private UserBean userBean;

    private static final Logger logger = Logger.getLogger("web.UserNameValidator");

    @Override
    public void validate(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
        try {
            //Your validation code goes here
            String username = (String) value;
            //If the validation fails
            ArrayList<UserDTO> users = (ArrayList<UserDTO>) userBean.getAllUsers();
            
            for (UserDTO user : users) {
                if (username.equalsIgnoreCase(user.getUsername())) {
                    FacesMessage message = new FacesMessage("Error: invalid username.");
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(toValidate.getClientId(context), message);
                    ((UIInput) toValidate).setValid(false);
                }
            }
        } catch (Exception e) {
            FacesExceptionHandler.handleException(e, "Unkown error.", logger);
        }
    }
}
