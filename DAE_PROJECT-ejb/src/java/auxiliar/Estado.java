/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auxiliar;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Yvtq8
 */
public enum Estado {
    Não_Aceite, Pendente , Aceite, Finalizado, Prova;

    public static Collection<String> getVerificationStates() {
        LinkedList<String> array = new LinkedList<>();
        array.add(Estado.Aceite.name());
        array.add(Estado.Não_Aceite.name());
        return array;
    }
    
    
}
