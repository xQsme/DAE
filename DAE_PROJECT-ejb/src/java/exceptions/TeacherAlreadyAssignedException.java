package exceptions;

public class TeacherAlreadyAssignedException extends Exception {

    public TeacherAlreadyAssignedException() {
    }

    public TeacherAlreadyAssignedException(String msg) {
        super(msg);
    }
}
