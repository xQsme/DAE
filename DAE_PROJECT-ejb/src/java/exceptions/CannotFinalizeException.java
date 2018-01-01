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
public class CannotFinalizeException extends Exception {

    public CannotFinalizeException() {
        super("A prova selecionada já se encontra finalizada.");
    }
    
}
