package web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.UploadedFile;
import util.URILookup;

@ManagedBean
@SessionScoped
public class UploadManager implements Serializable{

    int currentPropostaId;
    String currentUsername;
    
    UploadedFile file;
    
    String completePathFile;
    String filename;

    public UploadManager() {
    }

    public void upload() {
        
        if (file != null) {
            try {

                filename = file.getFileName().substring(file.getFileName().lastIndexOf("\\") + 1);

                File mainDir = new File(URILookup.getServerDocumentsFolder());

                // if the directory does not exist, create it
                if (!mainDir.exists()) {
                    try{
                        mainDir.mkdir();
                    } 
                    catch(SecurityException ignored){}        
                }
                
                File subDir = new File(URILookup.getServerDocumentsFolder() + "propostas");

                // if the directory does not exist, create it
                if (!subDir.exists()) {
                    try{
                        subDir.mkdir();
                    } 
                    catch(SecurityException ignored){}        
                }
                
                completePathFile = URILookup.getServerDocumentsFolder() + "propostas/" + currentPropostaId;
                
                File theDir = new File(completePathFile);

                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    try{
                        theDir.mkdir();
                    } 
                    catch(SecurityException ignored){}        
                }
                
                completePathFile = completePathFile +  "/" + filename;

                InputStream in = file.getInputstream();
                FileOutputStream out = new FileOutputStream(completePathFile);

                byte[] b = new byte[1024];
                int readBytes = in.read(b);

                while (readBytes != -1) {
                    out.write(b, 0, readBytes);
                    readBytes = in.read(b);
                }

                in.close();
                out.close();

                FacesMessage message = new FacesMessage("File: " + file.getFileName() + " uploaded successfully!");
                FacesContext.getCurrentInstance().addMessage(null, message);

            } catch (IOException e) {
                FacesMessage message = new FacesMessage("ERROR :: File: " + file.getFileName() + " not uploaded!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
    }
    
    public void uploadUserDocument() {
        
        if (file != null) {
            try {

                filename = file.getFileName().substring(file.getFileName().lastIndexOf("\\") + 1);

                File mainDir = new File(URILookup.getServerDocumentsFolder());

                // if the directory does not exist, create it
                if (!mainDir.exists()) {
                    try{
                        mainDir.mkdir();
                    } 
                    catch(SecurityException ignored){}        
                }
                
                File subDir = new File(URILookup.getServerDocumentsFolder() + "users");

                // if the directory does not exist, create it
                if (!subDir.exists()) {
                    try{
                        subDir.mkdir();
                    } 
                    catch(SecurityException ignored){}        
                }
                
                completePathFile = URILookup.getServerDocumentsFolder() + "users/" + currentUsername;
                
                File theDir = new File(completePathFile);

                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    try{
                        theDir.mkdir();
                    } 
                    catch(SecurityException ignored){}        
                }
                
                completePathFile = completePathFile +  "/" + filename;

                InputStream in = file.getInputstream();
                FileOutputStream out = new FileOutputStream(completePathFile);

                byte[] b = new byte[1024];
                int readBytes = in.read(b);

                while (readBytes != -1) {
                    out.write(b, 0, readBytes);
                    readBytes = in.read(b);
                }

                in.close();
                out.close();

                FacesMessage message = new FacesMessage("File: " + file.getFileName() + " uploaded successfully!");
                FacesContext.getCurrentInstance().addMessage(null, message);

            } catch (IOException e) {
                FacesMessage message = new FacesMessage("ERROR :: File: " + file.getFileName() + " not uploaded!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getCompletePathFile() {
        return completePathFile;
    }

    public void setCompletePathFile(String completePathFile) {
        this.completePathFile = completePathFile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getCurrentPropostaId() {
        return currentPropostaId;
    }

    public void setCurrentPropostaId(int currentPropostaId) {
        this.currentPropostaId = currentPropostaId;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }
    
}
