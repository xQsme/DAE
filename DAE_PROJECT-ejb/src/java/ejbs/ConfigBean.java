package ejbs;

import auxiliar.TipoDeInstituicao;
import auxiliar.TipoDeTrabalho;
import ejbs.InstituicaoBean;
import ejbs.MembroCCPBean;
import ejbs.PropostaBean;
import ejbs.StudentBean;
import ejbs.TeacherBean;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class ConfigBean {

    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");    
    /*
    @EJB
    private AdministratorBean administratorBean;
*/
    @EJB
    private StudentBean studentBean;
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private TeacherBean teacherBean;
    @EJB
    private InstituicaoBean instituicaoBean;
    @EJB
    private MembroCCPBean membroCCPBean;
    @EJB
    private ProvaBean provaBean;

    @PostConstruct
    public void populateBD() {

        try {   //todo verificar se o aluno ou proponente ja têm a proposta associada antes de associar (a verificação deve ser feita no bean do student e proponente, não aqui);
            studentBean.create("2151474", "secret", "João Pedro Borges Ferreira Castanheira Marques", "2151474@my.ipleiria.pt");
            studentBean.create("2151159", "secret", "Gonçalo Graça Ribeiro", "2151159@my.ipleiria.pt");
            studentBean.create("2151163", "secret", "Pedro Henrique Gaspar Cordeiro Ferreira", "2151163@my.ipleiria.pt");

            propostaBean.create(1, "Titulo adequado a uma dissertação", TipoDeTrabalho.Dissertação.toString(), "Resumo adequado a uma dissertação", "Plano adequado a uma dissertação", "Pombal", 500, null);
            propostaBean.create(2, "Titulo adequado a um estágio", TipoDeTrabalho.Estágio.toString(), "Resumo adequado a um estágio", "Plano adequado a um estágio", "Pombal", 30, "Computador da Razer");
            propostaBean.create(3, "Titulo adequado a um projeto", TipoDeTrabalho.Projeto.toString(), "Resumo adequado a um projeto", "Plano adequado a um projeto", "Ramalhais", 1600, null);
            propostaBean.create(4, "Titulo adequado a uma prova", TipoDeTrabalho.Projeto.toString(), "Resumo adequado a uma prova", "Plano adequado a uma prova", "Leiria", 1337, null);
            
            teacherBean.create("teacherNick1", "secret", "teacher Pedro", "2151163@my.ipleiria.pt", "G1");
            teacherBean.create("teacherNick2", "secret", "teacher  João", "2151474@my.ipleiria.pt", "G2");
            teacherBean.create("teacherNick3", "secret", "teacher Gonçalo", "2151159@my.ipleiria.pt", "G3");
            
            studentBean.addCandidatura("2151474", 1);
            studentBean.addCandidatura("2151159", 2);
            studentBean.addCandidatura("2151159", 1);
            
            teacherBean.addPropostaTeacher(1, "teacherNick1");
            teacherBean.addPropostaTeacher(2, "teacherNick2");
            teacherBean.addPropostaTeacher(1, "teacherNick3");
            
            instituicaoBean.create("InstituicaoNick1", "secret", "Instituicao Pedro", "2151163@my.ipleiria.pt", "Associação");
            instituicaoBean.create("InstituicaoNick2", "secret", "Instituicao João", "2151474@my.ipleiria.pt", TipoDeInstituicao.Empresa.toString());
            instituicaoBean.create("InstituicaoNick3", "secret", "Instituicao Gonçalo", "2151159@my.ipleiria.pt", TipoDeInstituicao.Pública.toString());

            instituicaoBean.addPropostaInstituicao(3, "InstituicaoNick2");
            instituicaoBean.addPropostaInstituicao(2, "InstituicaoNick1");

            propostaBean.addAreaCientifica(1, "Informática");
            propostaBean.addAreaCientifica(1, "Saúde");
            propostaBean.addAreaCientifica(2, "Física");
            propostaBean.addAreaCientifica(3, "Mecanica");
            
            propostaBean.addObjetivo(1, "Acabar a Dissertação");
            propostaBean.addObjetivo(2, "Acabar o Estágio");
            propostaBean.addObjetivo(3, "Acabar o Projeto");
            propostaBean.addObjetivo(3, "Vender o Projeto");
            
            propostaBean.addReferencia(1, "To kill a Mockingbird");
            propostaBean.addReferencia(1, "Catcher in the Rye");
            propostaBean.addReferencia(2, "The Lord of Flies");
            propostaBean.addReferencia(3, "Brave new World");
            
            propostaBean.addReferencia(1, "Animal Farm");
            propostaBean.addReferencia(1, "100 Anos de Solidão");
            propostaBean.addReferencia(1, "1984");
            
            propostaBean.addRequsito(1, "Terminar a dissertação em 3 meses");
            propostaBean.addRequsito(2, "Terminar a estágio em com aprovação positiva");
            propostaBean.addRequsito(2, "Preencher um relatório que descreva o estagio");
            
            //Adding a menbroCCP
            membroCCPBean.create("membroCCP", "secret", "membroCCP", "menbroCPPdae@gmail.com");
            membroCCPBean.create("membroCCP2", "secret", "membroCCP", "menbroCPPdae@gmail.com");
            membroCCPBean.validarProposta("membroCCP", 1, -1, "Because i Can muahahaha");
            membroCCPBean.validarProposta("membroCCP", 2, 1);
            membroCCPBean.validarProposta("membroCCP", 4, 2);
            
            
            List<String> juizes = new LinkedList();
            juizes.add("membroCCP");
            juizes.add("teacherNick1");//Acho que tem de ter documents not 100% sure
            provaBean.create("Show your Strength", new GregorianCalendar(), "S.João Da Madeira", 
                    2, "2151474", juizes, null);
                    
                    
            //membroCCPBean.addProfessorOrientador("teacherNick1", "2151159");
            
            //studentBean.setProposta("2151159", 2);
            
            //propostaBean.addReferencia(1, "Este ja não entra (> 5)");  // ao descomentar esta linha uma excepção vai ser mandada e o codigo neste try passa a não ser executadopropostaBean.addReferencia(1, "1984");
            
            
        } catch(Exception e){
            logger.warning("logger -> " + e.getMessage());
        }        
    }
}
