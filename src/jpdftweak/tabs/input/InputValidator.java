package jpdftweak.tabs.input;

import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import jpdftweak.core.PageRange;

/**
 *
 * @author Vasilis Naskos
 */
public class InputValidator {
    
    private boolean isModelEmpty, interleave;
    private int interleaveSize;
    private List<PageRange> pageRanges;
    
    public void setIsModelEmpty(boolean isModelEmpty) {
        this.isModelEmpty = isModelEmpty;
    }

    public void setInterleave(boolean interleave) {
        this.interleave = interleave;
    }

    public void setInterleaveSize(int interleaveSize) {
        this.interleaveSize = interleaveSize;
    }
    
    public void setPageRanges(List<PageRange> ranges) {
        this.pageRanges = ranges;
    }
    
    public void checkValidity() {
        try {
            check();
        } catch(NumberFormatException ex) {
            showException(ex, "Invalid interleave value");
        } catch(IOException ex) {
            showException(ex);
        }
    }
    
    private void check() throws IOException {
        checkModel();
        
        if(interleave)
            checkInterleave();
        
        checkPageRanges();
    }
    
    private void checkModel() throws IOException{
        checkIsModelEmpty();
    }
    
    private void checkIsModelEmpty() throws IOException {
        if (isModelEmpty)
            throw new IOException("No input file selected");
    }
    
    private void checkInterleave() throws NumberFormatException {
        checkInterleaveSize();
    }

    private void checkInterleaveSize() throws NumberFormatException {
        int size = interleaveSize;
            
        if (size < 1)
            throw new NumberFormatException();
    }
    
    private void checkPageRanges() throws IOException {
        int pagesBefore = 0;

        for (PageRange range : pageRanges) {
            int[] pages = range.getPages(pagesBefore);
            
            if (pages.length == 0)
                throw new IOException("At least one input file contains no pages");

            if (!interleave)
                pagesBefore += pages.length;
        }
    }
    
    private void showException(Exception ex) {
        showException(ex, ex.getMessage());
    }
    
    private void showException(Exception ex, String message) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    
}
