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
public class ProposalStateDoesNotAllowException extends Exception {

    public ProposalStateDoesNotAllowException() {
        super("A proposta não foi aceite.");
    }
    
}
