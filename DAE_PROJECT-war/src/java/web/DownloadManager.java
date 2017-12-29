package web;

import dtos.DocumentDTO;
import ejbs.PropostaBean;
import exceptions.EntityDoesNotExistsException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
public class DownloadManager implements Serializable {

    private static final Logger logger = Logger.getLogger("web.downloadManager");
    
    private int documentId;

    @EJB
    private PropostaBean pb;
    
    public DownloadManager() {
        
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
    
    public StreamedContent getFile() {
        try {
            DocumentDTO document = pb.getDocument(documentId);
            InputStream in = new FileInputStream(document.getFilepath());

            return new DefaultStreamedContent(in, document.getMimeType(), document.getDesiredName());
        } catch (EntityDoesNotExistsException | FileNotFoundException  e) {
            FacesExceptionHandler.handleException(e, "Could not download the file", logger);
            return null;
        }
    }    
}
