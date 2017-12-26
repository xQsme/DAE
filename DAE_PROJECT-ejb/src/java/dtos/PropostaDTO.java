package dtos;

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
    private List<String> objetivos;
    private List<String> bibliografia;
    private String planoDeTrabalhos;
    private String local;
    private List<String> requisitos;
    private String orcamento;
    private String apoios;
    
    public PropostaDTO() {
    }

    public PropostaDTO(int code, String titulo, String tipoDeTrabalho, List<String> areasCientificas, String resumo, List<String> objetivos, List<String> bibliografia, String planoDeTrabalhos, String local, List<String> requisitos, String orcamento, String apoios) {
        this.code = code;
        this.titulo = titulo;
        this.tipoDeTrabalho = tipoDeTrabalho;
        this.areasCientificas = areasCientificas;
        this.resumo = resumo;
        this.objetivos = objetivos;
        this.bibliografia = bibliografia;
        this.planoDeTrabalhos = planoDeTrabalhos;
        this.local = local;
        this.requisitos = requisitos;
        this.orcamento = orcamento;
        this.apoios = apoios;
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
}
