/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbs;

import java.io.Serializable;
import javax.faces.bean.ViewScoped;
import javax.swing.SortOrder;

/**
 *
 * @author Yvtq8
 */

@ManagedBean
@ViewScoped
public class SomeSortBean implements Serializable {
    private SortOrder titulo = SortOrder.UNSORTED;
  
    public void sortByCapitals() {
        titulo = SortOrder.UNSORTED;
        if (titulo.equals(SortOrder.ASCENDING)) {
            setCapitalsOrder(SortOrder.DESCENDING);
        } else {
            setCapitalsOrder(SortOrder.ASCENDING);
        }
    }
   
    public SortOrder getCapitalsOrder() {
        return titulo;
    }
 
    public void setCapitalsOrder(SortOrder titulo) {
        this.titulo = titulo;
    }
 
 }
