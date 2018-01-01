package dtos;

import auxiliar.Estado;
import entities.Proponente;
import entities.Student;
import java.io.Serializable;
import java.util.List;

public class PropostaDTO  implements Serializable{

    private int code;
    private String titulo;
    private String tipoDeTrabalho;
    private List<String> areasCientificas;
    private String resumo;
    private List<Student> candidatos;
    private List<String> objetivos;
    private List<String> bibliografia;
    private String planoDeTrabalhos;
    private String local;
    private List<String> requisitos;
    private String orcamento;
    private String apoios;
    
    
    private Estado estado;

    public final void setEstado(Integer estado) {//Must be Integer because it supports null
        if(estado != null){
            switch(estado){
                case -1:this.estado=Estado.Não_Aceite; 
                        break;
                case 0: this.estado=Estado.Pendente; 
                        break;
                case 1: this.estado=Estado.Aceite; 
                        break;
                case 2: this.estado=Estado.Prova; 
                        break;
                case 3: this.estado=Estado.Finalizado;
                        break;
            }
        }else{
            this.estado=Estado.Pendente; 
        }
    }
    
    public final void setStringEstado(String estado) {//Must be Integer because it supports null
        switch(estado){
            case "Não_Aceite":this.estado=Estado.Não_Aceite; 
                    break;
            case "Pendente": this.estado=Estado.Pendente; 
                    break;
            case "Aceite": this.estado=Estado.Aceite; 
                    break;
            case "Prova": this.estado=Estado.Prova;
                    break;
            case "Finalizado": this.estado=Estado.Finalizado; 
                    break;
        }
    }

    public void setEnumEstado(Estado estado){
        this.estado=estado;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    private String observacao;
    
    public PropostaDTO() {
    }

    public PropostaDTO(int code, String titulo, String tipoDeTrabalho, List<String> areasCientificas, String resumo, List<Student> candidatos, List<String> objetivos, List<String> bibliografia, String planoDeTrabalhos, String local, List<String> requisitos, String orcamento, String apoios, int estado, String observacao) {
        this.code = code;
        this.titulo = titulo;
        this.tipoDeTrabalho = tipoDeTrabalho;
        this.areasCientificas = areasCientificas;
        this.resumo = resumo;
        this.candidatos = candidatos;
        this.objetivos = objetivos;
        this.bibliografia = bibliografia;
        this.planoDeTrabalhos = planoDeTrabalhos;
        this.local = local;
        this.requisitos = requisitos;
        this.orcamento = orcamento;
        this.apoios = apoios;
        setEstado(estado);
        this.observacao = observacao;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipoDeTrabalho() {
        return tipoDeTrabalho;
    }

    public void setTipoDeTrabalho(String tipoDeTrabalho) {
        this.tipoDeTrabalho = tipoDeTrabalho;
    }

    public List<String> getAreasCientificas() {
        return areasCientificas;
    }

    public void setAreasCientificas(List<String> areasCientificas) {
        this.areasCientificas = areasCientificas;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public List<Student> getCandidatos() {
        return candidatos;
    }

    public void setCandidatos(List<Student> candidatos) {
        this.candidatos = candidatos;
    }

    public List<String> getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(List<String> objetivos) {
        this.objetivos = objetivos;
    }

    public List<String> getBibliografia() {
        return bibliografia;
    }

    public void setBibliografia(List<String> bibliografia) {
        this.bibliografia = bibliografia;
    }

    public String getPlanoDeTrabalhos() {
        return planoDeTrabalhos;
    }

    public void setPlanoDeTrabalhos(String planoDeTrabalhos) {
        this.planoDeTrabalhos = planoDeTrabalhos;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public List<String> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<String> requisitos) {
        this.requisitos = requisitos;
    }

    public String getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(String orcamento) {
        this.orcamento = orcamento;
    }

    public String getApoios() {
        return apoios;
    }

    public void setApoios(String apoios) {
        this.apoios = apoios;
    }
    
    public String getEstado(){
        return estado.name().equals("Não_Aceite") ? "Não Aceite" : estado.name() ;
    }
        
    public String getStringEstado(){
        return estado.name();
    }
    
    public Integer getIntEstado(){
        switch(estado){
            case Não_Aceite: return -1;
                             
            case Pendente: return 0;
                           
            case Aceite: return 1;
            
            case Prova: return 2;
                         
            case Finalizado: return 3;
            
            default: return null;
        }
    }
    
    
    public String getObservacao(){
        return observacao;
    }

    public void reset() {
        setCode(0);
        setTitulo(null);
        setTipoDeTrabalho(null);
        setAreasCientificas(null);
        setResumo(null);
        setCandidatos(null);
        setObjetivos(null);
        setBibliografia(null);
        setPlanoDeTrabalhos(null);
        setLocal(null);
        setRequisitos(null);
        setOrcamento(null);
        setApoios(null);
        setEstado(null);
        setObservacao(null);
    }

}
