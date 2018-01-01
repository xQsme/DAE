package exceptions;

public class StudentAlreadyHasAProposalAssignedException extends Exception {

    public StudentAlreadyHasAProposalAssignedException() {
    }

    public StudentAlreadyHasAProposalAssignedException(String msg) {
        super(msg);
    }
}
