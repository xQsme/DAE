package exceptions;

public class BibliografiaIsFullException extends Exception {

    public BibliografiaIsFullException() {
    }

    public BibliografiaIsFullException(String msg) {
        super(msg);
    }
}
