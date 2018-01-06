/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author joaoz
 */

@Entity
@Table(name="Provas")
@NamedQuery(name="getAllProvas", query="SELECT e FROM Prova e") //ver aqui
public class Prova implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int code;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false)
    private Calendar data;
    
    @Column(nullable = false)
    private String local;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "prova")
    private Proposta proposta;//one to one
  

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "STUDENT_CODE", referencedColumnName="CODE")
    private Student student;
    
    @ManyToMany
    @JoinTable(name = "PROPONENTE_JUIZ", joinColumns = @JoinColumn(name = "PROPONENTE_CODE", referencedColumnName = "CODE"), inverseJoinColumns = @JoinColumn(name = "PROPONENTE_USERNAME", referencedColumnName = "USERNAME"))
    private List<Proponente> juizes;//Many to Many

    @OneToMany(mappedBy = "prova")
    private List<Documento> documentos;

    
    public Prova(){
        this.data = new GregorianCalendar();
        this.juizes = new LinkedList<>();
    }

    public Prova(int code, String titulo, Calendar data, String local, Proposta proposta, Student student, List<Proponente> juizes, List<Documento> documentos) {
        this.code = code;
        this.titulo = titulo;
        this.data = data;
        this.local = local;
        this.proposta = proposta;
        //this.student = student;
        this.juizes = juizes;
        this.documentos = documentos;
    }

   
    
    
    
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

    public Proposta getProposta() {
        return proposta;
    }

    public void setProposta(Proposta proposta) {
        this.proposta = proposta;
    }

    public List<Proponente> getJuizes() {
        return juizes;
    }

    public void setJuizes(List<Proponente> juizes) {
        this.juizes = juizes;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }
     
    
    public int getCode() {
        return code;
    }

    public void setId(int id) {
        this.code = id;
    }

    public Calendar getDate() {
        return data;
    }

    public void setDate(int day, int month, int year) {
        this.data = new GregorianCalendar(year, month, day);
    }

 
    /*public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }*/
}
