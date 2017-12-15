package dtos;

import java.io.Serializable;

public class PropostaDTO  implements Serializable{

    private int code;
    private String titulo;;   

    public PropostaDTO() {
    }

    public PropostaDTO(int code, String name){//, int courseCode, String courseName, int courseYear, String scholarYear) {
        this.code = code;
        this.titulo = titulo;
        /*
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseYear = courseYear;
        this.scholarYear = scholarYear;
*/
    }
    
    public void reset(){
        this.code = 0;
        this.titulo = null;
        /*
        this.courseCode = 0;
        this.courseName = null;
        this.courseYear = 0;
        this.scholarYear = null;  */      
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

    public void setName(String titulo) {
        this.titulo = titulo;
    }
/*
    public int getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(int courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(int courseYear) {
        this.courseYear = courseYear;
    }

    public String getScholarYear() {
        return scholarYear;
    }

    public void setScholarYear(String scholarYear) {
        this.scholarYear = scholarYear;
    }*/
}
