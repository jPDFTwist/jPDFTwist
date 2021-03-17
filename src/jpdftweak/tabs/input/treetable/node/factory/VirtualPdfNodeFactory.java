package jpdftweak.tabs.input.treetable.node.factory;

import com.itextpdf.text.pdf.PdfReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpdftweak.core.IntegerList;
import jpdftweak.tabs.input.treetable.UserObjectValue;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.VirtualFileUserObject;
import jpdftweak.utils.PdfParser;

/**
 *
 * @author Vasilis Naskos
 */
public class VirtualPdfNodeFactory extends FileNodeFactory {
    
    private String filepath;
    private String srcFile;
    private int repeat;
    private PdfReader reader;

    public Node getEmptyFileNode(String filepath) {
        this.filepath = filepath;
        
        return new Node(createVirtualPdfUserObject());
    }
    
    @Override
    public Node getFileNode(String filepath) {
        this.filepath = filepath;
        try {
            this.reader = PdfParser.open(srcFile, optimize);
        } catch (IOException ex) {
            Logger.getLogger(VirtualPdfNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
            //TODO throw Exception
        }

        VirtualFileUserObject pdfUO = createVirtualPdfUserObject();

        Node file = new Node(pdfUO);
        insertPages(file);

        reader.close();

        return file;
    }

    private VirtualFileUserObject createVirtualPdfUserObject() {
        VirtualFileUserObject pdfUO = new VirtualFileUserObject(
                filepath,
                FileUserObject.SubType.PDF, 
                "");
        
        int numberOfPages = getPageCount();
        
        pdfUO.setValueAt(numberOfPages, UserObjectValue.PAGES);
        pdfUO.setValueAt(1, UserObjectValue.FROM);
        pdfUO.setValueAt(numberOfPages, UserObjectValue.TO);
        pdfUO.setValueAt(true, UserObjectValue.EVEN);
        pdfUO.setValueAt(true, UserObjectValue.ODD);
        pdfUO.setValueAt(new IntegerList("0"), UserObjectValue.EMPTY_BEFORE);
        pdfUO.setValueAt(0, UserObjectValue.BOOKMARK_LEVEL);

        return pdfUO;
    }
    
    private int getPageCount() {
        int numberOfPages = 0;
        
        if(reader != null) {
            numberOfPages = reader.getNumberOfPages()*repeat;
        }
        
        return numberOfPages;
    }

    private void insertPages(Node file) {
        int pageCount = reader.getNumberOfPages();
        updateListenersPageCount(pageCount);

        PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();

        for (int i = 0; i < repeat; i++) {
            for (int j = 1; j <= pageCount; j++) {
                updateListenersNextPage(i);
                
                int index = (i * pageCount) + j;
                
                pageNodeFactory.setSize(reader.getPageSizeWithRotation(j));
                Node page = pageNodeFactory.getPageNode(index);
                
                file.insert(page, index-1);
            }
        }
    }
    
    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
}
