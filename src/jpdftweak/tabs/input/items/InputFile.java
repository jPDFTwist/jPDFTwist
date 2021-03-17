package jpdftweak.tabs.input.items;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Vasilis Naskos
 */
public interface InputFile {
    
    public File getFile();
    public String getParentDir();
    public PdfImportedPage getImportedPage(PdfWriter destination, int page);
    public Rectangle getPageSize(int page);
    public void open() throws IOException;
    public void close();
    public int getPageCount();
    public Map<String, String> getInfoDictionary();
    public PdfPageLabels.PdfPageLabelFormat[] getPageLabels();
    public int getCryptoMode();
    public boolean isMetadataEncrypted();
    public int getPermissions();
    public String getOwnerPassword();
    public String getUserPassword();
    public PdfReader getReader();
    public void reopen() throws IOException;
    public String getDepth();
    
}
