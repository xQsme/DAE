package exceptions;

public class StudentAlreadyHasAppliedException extends Exception {

    public StudentAlreadyHasAppliedException() {
    }

    public StudentAlreadyHasAppliedException(String msg) {
        super(msg);
    }
}
