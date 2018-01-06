/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import entities.Documento;
import entities.Proponente;
import entities.Proposta;
import entities.Student;
import java.util.Calendar;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "Student")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProvaDTO {
    
    
    private String titulo;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
  
    private Calendar data;

    private String local;

    
    public ProvaDTO(){    
    }
     
    public ProvaDTO(String titulo, Calendar date, String local) {
        this();
        this.titulo=titulo;
        this.data = date;
        this.local = local;
    }

}
