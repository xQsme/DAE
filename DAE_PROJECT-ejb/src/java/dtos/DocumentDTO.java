package dtos;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentDTO implements Serializable {
    
    private int id;
    
    private String filepath;

    private String desiredName;
    
    private String mimeType;
    
    private boolean ata;
    

    public DocumentDTO() {
        
    }

    public DocumentDTO(int id, String filepath, String desiredName, String mimeType, boolean ata) {
        this.id = id;
        this.filepath = filepath;
        this.desiredName = desiredName;
        this.mimeType = mimeType;
        this.ata=ata;
    }
    
    public DocumentDTO(String filepath, String desiredName, String mimeType, boolean ata) {
        this(-1, filepath, desiredName, mimeType, ata);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getDesiredName() {
        return desiredName;
    }

    public void setDesiredName(String desiredName) {
        this.desiredName = desiredName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }    

    public boolean isAta() {
        return ata;
    }

    public void setAta(boolean ata) {
        this.ata = ata;
    }
    
    
}
