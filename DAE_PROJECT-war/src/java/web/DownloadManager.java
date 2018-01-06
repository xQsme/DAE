package web;

import dtos.DocumentDTO;
import dtos.DocumentoDTO;
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
    
    private DocumentDTO document;
    private DocumentoDTO documento;
    
    public DownloadManager() {
        
    }
    
    public StreamedContent getDocumentFile() {
        try {
            InputStream in = new FileInputStream(document.getFilepath());
            return new DefaultStreamedContent(in, document.getMimeType(), document.getDesiredName());
        } catch (FileNotFoundException  e) {
            FacesExceptionHandler.handleException(e, "Could not download the file", logger);
            return null;
        }
    }    
    
    public StreamedContent getDocumentoFile() {
        try {
            InputStream in = new FileInputStream(documento.getFilepath());
            return new DefaultStreamedContent(in, documento.getMimeType(), documento.getDesiredName());
        } catch (FileNotFoundException  e) {
            FacesExceptionHandler.handleException(e, "Could not download the file", logger);
            return null;
        }
    }   

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }

    public DocumentoDTO getDocumento() {
        return documento;
    }

    public void setDocumento(DocumentoDTO documento) {
        this.documento = documento;
    }
    
    
}
