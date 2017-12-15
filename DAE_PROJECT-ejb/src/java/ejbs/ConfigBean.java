package ejbs;

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
    private CourseBean courseBean;
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
            courseBean.create(1, "EI");
            courseBean.create(2, "IS");
            courseBean.create(3, "JDM");
            courseBean.create(4, "SIS");
            courseBean.create(5, "MEI-CM");
            courseBean.create(6, "MGSIM");

            studentBean.create("1111111", "Manuel", "Manuel", "dae.ei.ipleiria@gmail.com", 1);
            studentBean.create("2222222", "Antonio", "António", "dae.ei.ipleiria@gmail.com", 1);
            studentBean.create("3333333", "Ana", "Ana", "dae.ei.ipleiria@gmail.com", 2);
            studentBean.create("4444444", "Jose", "José", "dae.ei.ipleiria@gmail.com", 2);
            studentBean.create("5555555", "Maria", "Maria", "dae.ei.ipleiria@gmail.com", 3);
            studentBean.create("6666666", "Joaquim", "Joaquim", "dae.ei.ipleiria@gmail.com", 3);
            studentBean.create("7777777", "Alzira", "Alzira", "dae.ei.ipleiria@gmail.com", 4);
            studentBean.create("8888888", "Pedro", "Pedro", "dae.ei.ipleiria@gmail.com", 4);
            
            */

            propostaBean.create(1, "P1");//, 1, 1, "2015/2016");
            propostaBean.create(2, "PA");//, 1, 2, "2015/2016");
            propostaBean.create(3, "IA");//, 1, 2, "2015/2016");
            propostaBean.create(4, "DAE");//, 1, 3, "2015/2016");
 
            propostaBean.create(5, "ComputProg");//, 2, 1, "2015/2016");
            propostaBean.create(6, "ComplProg");//, 2, 1, "2015/2016");
            propostaBean.create(7, "PA");//, 2, 2, "2015/2016");

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

            teacherBean.create("t1", "t1", "t1", "t1@ipleiria.pt", "O1");
            teacherBean.create("t2", "t2", "t2", "t2@ipleiria.pt", "O2");
            teacherBean.create("t3", "t3", "t3", "t3@ipleiria.pt", "O3");
            
            /*

            administratorBean.create("foo", "bar", "FooBar", "foo@bar.com");
            
            administratorBean.create("a1", "a1", "a1", "a1@ipleiria.pt");
            administratorBean.create("a2", "a2", "a2", "a2@ipleiria.pt");
            administratorBean.create("a3", "a3", "a3", "a3@ipleiria.pt");

*/

            teacherBean.addSubjectTeacher(1, "t1");
            teacherBean.addSubjectTeacher(2, "t2");
            teacherBean.addSubjectTeacher(1, "t3");

        } catch(Exception e){
            logger.warning(e.getMessage());
        }
    }
}
