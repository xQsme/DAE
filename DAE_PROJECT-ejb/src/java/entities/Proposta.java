package entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PROPOSTA")//, uniqueConstraints = @UniqueConstraint(columnNames = { "TITULO" }))//, "COURSE_CODE", "SCHOLAR_YEAR" }))
@NamedQuery(name = "getAllPropostas", query = "SELECT p FROM Proposta p ORDER BY p.titulo")//.name, s.courseYear DESC, s.scholarYear, s.name")
public class Proposta implements Serializable {
    
 /* 
    faltam adicionar os seguintes parametros:
    
    os requisitos considerados fundamentais para que o trabalho tenha sucesso,
    um orçamento (incluindo itens para bibliografia, aquisição de material, aquisição de equipamento ou outros) 
    e os apoios (financeiros ou de outro tipo) que Estudante poderá usufruir
*/
    
    @Id
    private int code;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false)
    private String tipoDeTrabalho;
    
    @ElementCollection
    @CollectionTable(name="PROPOSTA_AREA_CIENTIFICA", joinColumns=@JoinColumn(name="PROPOSTA_ID"))
    @Column(name="AREA_CIENTIFICA")
    private List<String> areasCientificas;
    
    @Column(nullable = false)
    private String resumo;
    
    @ManyToMany
    @JoinTable(name = "PROPOSTA_PROPONENTE", joinColumns = @JoinColumn(name = "PROPOSTA_CODE", referencedColumnName = "CODE"), inverseJoinColumns = @JoinColumn(name = "PROPONENTE_USERNAME", referencedColumnName = "USERNAME"))
    private List<Proponente> proponentes;
    
    @ManyToMany
    @JoinTable(name = "PROPOSTA_STUDENT", joinColumns = @JoinColumn(name = "PROPOSTA_CODE", referencedColumnName = "CODE"), inverseJoinColumns = @JoinColumn(name = "PROPONENTE_USERNAME", referencedColumnName = "USERNAME"))
    private List<Student> candidatos;
    
    @ElementCollection
    @CollectionTable(name="PROPOSTA_OBJETIVOS", joinColumns=@JoinColumn(name="PROPOSTA_ID"))
    @Column(name="OBJETIVO")
    private List<String> objetivos;
    
    @ElementCollection
    @CollectionTable(name="PROPOSTA_BIBLOGRAFIA", joinColumns=@JoinColumn(name="PROPOSTA_ID"))
    @Column(name="REFERENCIA")
    private List<String> bibliografia;
    
    @Column(nullable = false)
    private String planoDeTrabalhos;
    
    @Column(nullable = false)
    private String local;
    
    @ElementCollection
    @CollectionTable(name="PROPOSTA_REQUISITOS", joinColumns=@JoinColumn(name="PROPOSTA_ID"))
    @Column(name="REQUISITO")
    private List<String> requisitos;
    
    @Column(nullable = false)
    private String orcamento;
    
    @Column
    private String apoios;
	
	//Estado & observacao will be fill by MenbroCCP
    @Column
    private Boolean estado;
    
    @Column
    private String observacao;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
	

    public Proposta() {
        proponentes = new LinkedList<>();
        areasCientificas = new LinkedList<>();
        objetivos = new LinkedList<>();
        bibliografia = new LinkedList<>();
        requisitos = new LinkedList<>();
        candidatos = new LinkedList<>();
    }

    public Proposta(int code, String titulo, String tipoDeTrabalho, String resumo, String planoDeTrabalhos, String local, String orcamento, String apoios){
        this();
        this.code = code;
        this.titulo = titulo;
        this.tipoDeTrabalho = tipoDeTrabalho;
        this.resumo = resumo;
        this.planoDeTrabalhos = planoDeTrabalhos;
        this.local = local;
        this.orcamento = orcamento;
        this.apoios = apoios;
    }

    public String getTipoDeTrabalho() {
        return tipoDeTrabalho;
    }

    public void setTipoDeTrabalho(String tipoDeTrabalho) {
        this.tipoDeTrabalho = tipoDeTrabalho;
    }

    public int getCode() {
        return code;
    }

    public List<Student> getCandidatos() {
        return candidatos;
    }

    public void setCandidatos(List<Student> candidatos) {
        this.candidatos = candidatos;
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

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public List<Proponente> getProponentes() {
        return proponentes;
    }

    public void setProponentes(List<Proponente> proponentes) {
        this.proponentes = proponentes;
    }
    
    public void addProponente(Proponente proponente) {
        proponentes.add(proponente);
    }

    public void removeProponente(Proponente proponente) {
        proponentes.remove(proponente);
    }
    
    public List<String> getAreasCientificas() {
        return areasCientificas;
    }

    public void setAreasCientificas(List<String> areasCientificas) {
        this.areasCientificas = areasCientificas;
    }
    
    public void addAreaCientifica(String areaCientifica) {
        areasCientificas.add(areaCientifica);
    }

    public void removeAreaCientifica(String areaCientifica) {
        areasCientificas.remove(areaCientifica);
    }

    public List<String> getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(List<String> objetivos) {
        this.objetivos = objetivos;
    }
    
    public void addObjetivo(String objetivo){
        objetivos.add(objetivo);
    }
    
    public void removeObjetivo(String objetivo){
        objetivos.remove(objetivo);
    }

    public List<String> getBibliografia() {
        return bibliografia;
    }

    public void setBibliografia(List<String> bibliografia) {
        this.bibliografia = bibliografia;
    }
    
    public void addReferencia(String referencia){
        bibliografia.add(referencia);
    }
    
    public void removeReferencia(String referencia){
        bibliografia.remove(referencia);   
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
    
    public void addRequisito(String requisito){
        this.requisitos.add(requisito);
    }
    
    public void removeRequisito(String requisito){
        this.requisitos.remove(requisito);
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

    public List<Student> getStudentsCandidatos() {
        return candidatos;
    }

    public void setStudentsCandidatos(List<Student> studentsCandidatos) {
        this.candidatos = studentsCandidatos;
    }
    
    public void addStudent(Student student) {
        this.candidatos.add(student);
    }
    
    public void removeStudent(Student student){
        candidatos.remove(student);
    }   
}
