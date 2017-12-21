package ejbs;

import auxiliar.TipoDeInstituicao;
import auxiliar.TipoDeTrabalho;
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
    @EJB
    private StudentBean studentBean;
*/
    @EJB
    private PropostaBean propostaBean;
    @EJB
    private TeacherBean teacherBean;
    @EJB
    private InstituicaoBean instituicaoBean;

    @PostConstruct
    public void populateBD() {

        try {

            propostaBean.create(1, "Titulo adequado a uma dissertação", TipoDeTrabalho.Dissertação.toString(), "Resumo adequado a uma dissertação", "Plano adequado a uma dissertação", "Pombal", "500€", null);
            propostaBean.create(2, "Titulo adequado a um estágio", TipoDeTrabalho.Estágio.toString(), "Resumo adequado a um estágio", "Plano adequado a um estágio", "Pombal", "30€", "Computador da Razer");
            propostaBean.create(3, "Titulo adequado a um projeto", TipoDeTrabalho.Projeto.toString(), "Resumo adequado a um projeto", "Plano adequado a um projeto", "Ramalhais", "1600€", null);

            teacherBean.create("teacherNick1", "secret", "teacher1", "t1@ipleiria.pt", "G1");
            teacherBean.create("teacherNick2", "secret", "teacher2", "t2@ipleiria.pt", "G2");
            teacherBean.create("teacherNick3", "secret", "teacher3", "t3@ipleiria.pt", "G3");
            
            teacherBean.addPropostaTeacher(1, "teacherNick1");
            teacherBean.addPropostaTeacher(2, "teacherNick2");
            teacherBean.addPropostaTeacher(1, "teacherNick3");
            
            instituicaoBean.create("InstituicaoNick1", "secret", "Instituicao1", "I1@ipleiria.pt", TipoDeInstituicao.Associação.toString());
            instituicaoBean.create("InstituicaoNick2", "secret", "Instituicao2", "I2@ipleiria.pt", TipoDeInstituicao.Empresa.toString());
            instituicaoBean.create("InstituicaoNick3", "secret", "Instituicao3", "I3@ipleiria.pt", TipoDeInstituicao.Pública.toString());

            instituicaoBean.addPropostaInstituicao(3, "InstituicaoNick1");

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
            
            //propostaBean.addReferencia(1, "Este ja não entra (> 5)");  // ao descomentar esta linha uma excepção vai ser mandada e o codigo neste try passa a não ser executadopropostaBean.addReferencia(1, "1984");
            
            
        } catch(Exception e){
            logger.warning("logger -> " + e.getMessage());
        }        
    }
}
