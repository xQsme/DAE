package exceptions;

public class UserAlreadyHasAppliedException extends Exception {

    public UserAlreadyHasAppliedException() {
    }

    public UserAlreadyHasAppliedException(String msg) {
        super(msg);
    }
}
