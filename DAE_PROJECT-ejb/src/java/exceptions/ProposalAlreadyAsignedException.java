package exceptions;

public class ProposalAlreadyAsignedException extends Exception {

    public ProposalAlreadyAsignedException() {
    }

    public ProposalAlreadyAsignedException(String msg) {
        super(msg);
    }
}
