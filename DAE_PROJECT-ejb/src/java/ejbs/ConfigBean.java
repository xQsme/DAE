package ejbs;

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

    @PostConstruct
    public void populateBD() {

        try {
/*
            studentBean.create("1111111", "Manuel", "Manuel", "dae.ei.ipleiria@gmail.com", 1);
            studentBean.create("2222222", "Antonio", "António", "dae.ei.ipleiria@gmail.com", 1);
            studentBean.create("3333333", "Ana", "Ana", "dae.ei.ipleiria@gmail.com", 2);
            studentBean.create("4444444", "Jose", "José", "dae.ei.ipleiria@gmail.com", 2);
            studentBean.create("5555555", "Maria", "Maria", "dae.ei.ipleiria@gmail.com", 3);
            studentBean.create("6666666", "Joaquim", "Joaquim", "dae.ei.ipleiria@gmail.com", 3);
            studentBean.create("7777777", "Alzira", "Alzira", "dae.ei.ipleiria@gmail.com", 4);
            studentBean.create("8888888", "Pedro", "Pedro", "dae.ei.ipleiria@gmail.com", 4);
            
            */

            propostaBean.create(1, "Titulo adequado a uma dissertação", TipoDeTrabalho.Dissertação.toString(), "Resumo adquado a uma dissertação");//, 1, 1, "2015/2016");
            propostaBean.create(2, "Titulo adequado a um estágio", TipoDeTrabalho.Estágio.toString(), "Resumo adquado a um estágio");//, 1, 2, "2015/2016");
            propostaBean.create(3, "Titulo adequado a um projeto", TipoDeTrabalho.Projeto.toString(), "Resumo adquado a um projeto");//, 1, 2, "2015/2016");

            /*
            
            studentBean.enrollStudent("1111111", 1);
            studentBean.enrollStudent("1111111", 2);
            studentBean.enrollStudent("2222222", 3);
            studentBean.enrollStudent("2222222", 4);

            studentBean.enrollStudent("3333333", 5);
            studentBean.enrollStudent("3333333", 6);
            studentBean.enrollStudent("4444444", 6);
            studentBean.enrollStudent("4444444", 7);

*/

            teacherBean.create("teacherNick1", "secret", "teacher1", "t1@ipleiria.pt", "G1");
            teacherBean.create("teacherNick2", "secret", "teacher2", "t2@ipleiria.pt", "G2");
            teacherBean.create("teacherNick3", "secret", "teacher3", "t3@ipleiria.pt", "G3");
            
            /*

            administratorBean.create("foo", "bar", "FooBar", "foo@bar.com");
            
            administratorBean.create("a1", "a1", "a1", "a1@ipleiria.pt");
            administratorBean.create("a2", "a2", "a2", "a2@ipleiria.pt");
            administratorBean.create("a3", "a3", "a3", "a3@ipleiria.pt");

*/

            teacherBean.addPropostaTeacher(1, "teacherNick1");
            teacherBean.addPropostaTeacher(2, "teacherNick2");
            teacherBean.addPropostaTeacher(1, "teacherNick3");
            
            propostaBean.addAreaCientifica(1, "Informática");
            propostaBean.addAreaCientifica(1, "Saúde");
            
            propostaBean.addAreaCientifica(2, "Física");
            propostaBean.addAreaCientifica(3, "Mecanica");

        } catch(Exception e){
            logger.warning(e.getMessage());
        }
    }
}
