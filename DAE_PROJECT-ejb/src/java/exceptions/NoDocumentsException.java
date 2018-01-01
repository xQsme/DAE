/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author The Law
 */
public class NoDocumentsException extends Exception {

    public NoDocumentsException() {
        super("Para se candidatar tem primeiro de fazer upload de documentos.");
    }
    
}
