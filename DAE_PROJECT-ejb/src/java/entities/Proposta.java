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
    
    os seus proponentes, 
    um resumo do trabalho a desenvolver,
    os objetivos a cumprir, 
    uma bibliografia com, no máximo, 5 referências, 
    um plano de trabalhos,
    o local de realização do trabalho, 
    os requisitos considerados fundamentais para que o trabalho tenha sucesso,
    um orçamento (incluindo itens para bibliografia, 
    aquisição de material,
    aquisição de equipamento ou outros) e os apoios (financeiros ou de outro tipo) que Estudante poderá usufruir
*/
    @Id
    private int code;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false)
    private String tipoDeTrabalho;
    
    @ElementCollection
    @CollectionTable(
          name="AREA_CIENTIFICA",
          joinColumns=@JoinColumn(name="PROPOSTA_ID")
    )
    @Column(name="NOME")
    private List<String> areasCientificas;
    
    @Column(nullable = false)
    private String resumo;
    
    @ManyToMany
    @JoinTable(name = "PROPOSTA_PROPONENTE",
            joinColumns = @JoinColumn(name = "PROPOSTA_CODE", referencedColumnName = "CODE"),
            inverseJoinColumns = @JoinColumn(name = "PROPONENTE_USERNAME", referencedColumnName = "USERNAME"))
    private List<Proponente> proponentes;

    public Proposta() {
        //students = new LinkedList<>();
        proponentes = new LinkedList<>();
    }

    public Proposta(int code, String titulo, String tipoDeTrabalho, String resumo){
        this.code = code;
        this.titulo = titulo;
        this.tipoDeTrabalho = tipoDeTrabalho;
        this.resumo = resumo;
        proponentes = new LinkedList<>();
        areasCientificas = new LinkedList<>();
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
}
