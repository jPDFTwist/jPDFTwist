package jpdftwist.tabs.input.treetable.node;

import com.itextpdf.text.pdf.PdfReader;
import jpdftwist.core.IntegerList;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableColumn;
import jpdftwist.gui.component.treetable.row.VirtualFileTreeTableRow;
import jpdftwist.utils.PdfParser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
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


    public Node getFileNode(String filepath) {
        this.filepath = filepath;
        try {
            this.reader = PdfParser.open(srcFile, optimize);
        } catch (IOException ex) {
            Logger.getLogger(VirtualPdfNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
            // TODO throw Exception
        }

        VirtualFileTreeTableRow pdfUO = createVirtualPdfUserObject();

        Node file = new Node(pdfUO);
        insertPages(file);

        reader.close();

        return file;
    }

    private VirtualFileTreeTableRow createVirtualPdfUserObject() {
        VirtualFileTreeTableRow pdfUO = new VirtualFileTreeTableRow(filepath, FileTreeTableRow.SubType.PDF, "");

        int numberOfPages = getPageCount();

        pdfUO.setValueAt(numberOfPages, TreeTableColumn.PAGES);
        pdfUO.setValueAt(1, TreeTableColumn.FROM);
        pdfUO.setValueAt(numberOfPages, TreeTableColumn.TO);
        pdfUO.setValueAt(true, TreeTableColumn.EVEN);
        pdfUO.setValueAt(true, TreeTableColumn.ODD);
        pdfUO.setValueAt(new IntegerList("0"), TreeTableColumn.EMPTY_BEFORE);
        pdfUO.setValueAt(0, TreeTableColumn.BOOKMARK_LEVEL);
        pdfUO.setSrcFilePath(this.srcFile);

        return pdfUO;
    }

    private int getPageCount() {
        int numberOfPages = 0;

        if (reader != null) {
            numberOfPages = reader.getNumberOfPages() * repeat;
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

                file.insert(page, index - 1);
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
