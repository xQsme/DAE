package exceptions;

public class ProposalNotAcceptedException extends Exception {

    public ProposalNotAcceptedException() {
    }

    public ProposalNotAcceptedException(String msg) {
        super(msg);
    }
}
