package jpdftwist.tabs.input.treetable.node;

import com.itextpdf.text.Rectangle;
import jpdftwist.core.IntegerList;
import jpdftwist.core.UnitTranslator;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.RealFileTreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableColumn;
import jpdftwist.utils.JImageParser;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class RealImageNodeFactory extends FileNodeFactory {

    private String filepath;

    public Node getFileNode(String filepath) {
        this.filepath = filepath;

        RealFileTreeTableRow pdfUO = createRealPdfUserObject();

        try {
            Node file = new Node(pdfUO);
            insertPages(file);
            return file;
        } catch (IOException ex) {
            Logger.getLogger(RealImageNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
            // TODO throw exception
        }

        return null;
    }

    private RealFileTreeTableRow createRealPdfUserObject() {
        long fileSize = new File(filepath).length();

        RealFileTreeTableRow imgUO = new RealFileTreeTableRow(filepath, FileTreeTableRow.SubType.IMAGE);

        imgUO.setValueAt(fileSize, TreeTableColumn.SIZE);
        imgUO.setValueAt(1, TreeTableColumn.PAGES);
        imgUO.setValueAt(1, TreeTableColumn.FROM);
        imgUO.setValueAt(1, TreeTableColumn.TO);
        imgUO.setValueAt(true, TreeTableColumn.EVEN);
        imgUO.setValueAt(true, TreeTableColumn.ODD);
        imgUO.setValueAt(new IntegerList("0"), TreeTableColumn.EMPTY_BEFORE);
        imgUO.setValueAt(0, TreeTableColumn.BOOKMARK_LEVEL);

        return imgUO;
    }

    private void insertPages(Node file) throws IOException {
        updateListenersPageCount(1);

        PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();
        JImageParser.ImageObject imageObj = JImageParser.tryToReadImage(filepath);

        if (imageObj == null || imageObj.getImage() == null) {
            throw new IOException(String.format("Image %s\n not supported or corrupted!", filepath));
        }

        Rectangle size = new Rectangle((float) UnitTranslator.pixelsToPoints(imageObj.getWidth(), 72),
            (float) UnitTranslator.pixelsToPoints(imageObj.getHeight(), 72));

        updateListenersNextPage(1);

        pageNodeFactory.setSize(size);
        Node page = pageNodeFactory.getPageNode(1);

        file.insert(page, 0);
    }
}
