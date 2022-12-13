package jpdftwist.tabs.input.treetable.node;

import com.itextpdf.text.Rectangle;
import jpdftwist.core.IntegerList;
import jpdftwist.gui.components.treetable.Node;
import jpdftwist.gui.components.treetable.TreeTableColumn;
import jpdftwist.gui.components.treetable.row.FileTreeTableRow;
import jpdftwist.gui.components.treetable.row.VirtualFileTreeTableRow;

/**
 * @author Vasilis Naskos
 */
public class VirtualBlankNodeFactory extends FileNodeFactory {
    private String filepath;
    private int pageCount;
    private int color;
    private Rectangle size;


    public Node getFileNode(String filepath) {
        this.filepath = filepath;

        VirtualFileTreeTableRow pdfUO = createVirtualBlankUserObject();

        Node file = new Node(pdfUO);
        insertPages(file);

        return file;
    }

    private VirtualFileTreeTableRow createVirtualBlankUserObject() {
        VirtualFileTreeTableRow pdfUO = new VirtualFileTreeTableRow(filepath, FileTreeTableRow.SubType.BLANK, "");

        pdfUO.setValueAt(pageCount, TreeTableColumn.PAGES);
        pdfUO.setValueAt(1, TreeTableColumn.FROM);
        pdfUO.setValueAt(pageCount, TreeTableColumn.TO);
        pdfUO.setValueAt(true, TreeTableColumn.EVEN);
        pdfUO.setValueAt(true, TreeTableColumn.ODD);
        pdfUO.setValueAt(new IntegerList("0"), TreeTableColumn.EMPTY_BEFORE);
        pdfUO.setValueAt(0, TreeTableColumn.BOOKMARK_LEVEL);

        return pdfUO;
    }

    private void insertPages(Node file) {
        updateListenersPageCount(pageCount);

        PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();

        for (int i = 1; i <= pageCount; i++) {
            pageNodeFactory.setColor(color);
            pageNodeFactory.setSize(size);

            Node page = pageNodeFactory.getPageNode(i);

            file.insert(page, i - 1);
        }
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setSize(Rectangle size) {
        this.size = size;
    }

}
