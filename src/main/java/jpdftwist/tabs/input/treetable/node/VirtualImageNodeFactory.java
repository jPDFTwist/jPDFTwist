package jpdftwist.tabs.input.treetable.node;

import com.itextpdf.text.Rectangle;
import jpdftwist.core.IntegerList;
import jpdftwist.core.UnitTranslator;
import jpdftwist.gui.components.treetable.Node;
import jpdftwist.gui.components.treetable.TreeTableColumn;
import jpdftwist.gui.components.treetable.row.FileTreeTableRow;
import jpdftwist.gui.components.treetable.row.VirtualFileTreeTableRow;
import jpdftwist.utils.JImageParser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class VirtualImageNodeFactory extends FileNodeFactory {
    private String filepath;
    private String srcFile;
    private int repeat;

    public Node getFileNode(String filepath) {
        this.filepath = filepath;

        VirtualFileTreeTableRow pdfUO = createVirtualPdfUserObject();

        try {
            Node file = new Node(pdfUO);
            insertPages(file);
            return file;
        } catch (IOException ex) {
            Logger.getLogger(VirtualImageNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
            // TODO throw excpetion
        }

        return null;
    }

    private VirtualFileTreeTableRow createVirtualPdfUserObject() {
        VirtualFileTreeTableRow imgUO = new VirtualFileTreeTableRow(filepath, FileTreeTableRow.SubType.IMAGE, "");

        imgUO.setValueAt(repeat, TreeTableColumn.PAGES);
        imgUO.setValueAt(1, TreeTableColumn.FROM);
        imgUO.setValueAt(repeat, TreeTableColumn.TO);
        imgUO.setValueAt(true, TreeTableColumn.EVEN);
        imgUO.setValueAt(true, TreeTableColumn.ODD);
        imgUO.setValueAt(new IntegerList("0"), TreeTableColumn.EMPTY_BEFORE);
        imgUO.setValueAt(0, TreeTableColumn.BOOKMARK_LEVEL);

        return imgUO;
    }

    private void insertPages(Node file) throws IOException {
        updateListenersPageCount(repeat);

        PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();
        JImageParser.ImageObject imageObj = JImageParser.tryToReadImage(srcFile);

        if (imageObj == null || imageObj.getImage() == null) {
            throw new IOException(String.format("Image %s\n not supported or corrupted!", srcFile));
        }

        Rectangle size = new Rectangle((float) UnitTranslator.pixelsToPoints(imageObj.getWidth(), 72),
            (float) UnitTranslator.pixelsToPoints(imageObj.getHeight(), 72));

        for (int i = 1; i <= repeat; i++) {
            updateListenersNextPage(i);

            pageNodeFactory.setSize(size);
            pageNodeFactory.setColorDepth(imageObj.getDepth());
            Node page = pageNodeFactory.getPageNode(i);

            file.insert(page, i - 1);
        }
    }

    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

}
