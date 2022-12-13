package jpdftwist.tabs.input.treetable.node;

import com.itextpdf.text.pdf.PdfReader;
import jpdftwist.core.IntegerList;
import jpdftwist.gui.components.treetable.Node;
import jpdftwist.gui.components.treetable.TreeTableColumn;
import jpdftwist.gui.components.treetable.row.FileTreeTableRow;
import jpdftwist.gui.components.treetable.row.RealFileTreeTableRow;
import jpdftwist.utils.PdfParser;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class RealPdfNodeFactory extends FileNodeFactory {

    private String filepath;
    private PdfReader reader;
    private boolean autoRestrictionsOverwrite;
    private boolean autoRestrictionsNew;

    public RealPdfNodeFactory() {
        autoRestrictionsNew = false;
        autoRestrictionsOverwrite = false;
    }


    public Node getFileNode(String filepath) {
        this.filepath = filepath;
        try {
            this.reader = PdfParser.open(filepath, optimize, autoRestrictionsOverwrite, autoRestrictionsNew);
        } catch (IOException ex) {
            Logger.getLogger(RealPdfNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
            // TODO throw Exception
        }

        RealFileTreeTableRow pdfUO = createRealPdfUserObject();

        Node file = new Node(pdfUO);
        insertPages(file);

        //        reader.close();

        return file;
    }

    private RealFileTreeTableRow createRealPdfUserObject() {
        // System.out.println("filepath:"+this.filepath);
        long fileSize = new File(filepath).length();

        RealFileTreeTableRow pdfUO = new RealFileTreeTableRow(filepath, FileTreeTableRow.SubType.PDF);

        pdfUO.setValueAt(fileSize, TreeTableColumn.SIZE);
        pdfUO.setValueAt(reader.getNumberOfPages(), TreeTableColumn.PAGES);
        pdfUO.setValueAt(1, TreeTableColumn.FROM);
        pdfUO.setValueAt(reader.getNumberOfPages(), TreeTableColumn.TO);
        pdfUO.setValueAt(true, TreeTableColumn.EVEN);
        pdfUO.setValueAt(true, TreeTableColumn.ODD);
        pdfUO.setValueAt(new IntegerList("0"), TreeTableColumn.EMPTY_BEFORE);
        pdfUO.setValueAt(0, TreeTableColumn.BOOKMARK_LEVEL);

        return pdfUO;
    }

    private void insertPages(Node file) {
        int pageCount = reader.getNumberOfPages();
        updateListenersPageCount(pageCount);

        PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();

        for (int i = 1; i <= pageCount; i++) {
            updateListenersNextPage(i);

            pageNodeFactory.setSize(reader.getPageSizeWithRotation(i));
            Node page = pageNodeFactory.getPageNode(i);

            file.insert(page, i - 1);
        }
    }

    public void setAutoRestrictionsNew(boolean autoRestrictionsNew) {
        this.autoRestrictionsNew = autoRestrictionsNew;
    }

    public void setAutoRestrictionsOverwrite(boolean autoRestrictionsOverwrite) {
        this.autoRestrictionsOverwrite = autoRestrictionsOverwrite;
    }

}
