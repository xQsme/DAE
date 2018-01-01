package exceptions;

public class StudentHasNoProposalException extends Exception {

    public StudentHasNoProposalException() {
    }

    public StudentHasNoProposalException(String msg) {
        super(msg);
    }
}
