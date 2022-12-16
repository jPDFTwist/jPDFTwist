package jpdftwist.tabs.input.treetable.node;

import com.itextpdf.text.Rectangle;
import jpdftwist.core.IntegerList;
import jpdftwist.core.input.FileInputElement;
import jpdftwist.core.input.TreeTableColumn;
import jpdftwist.core.input.VirtualFileInputElement;
import jpdftwist.gui.component.treetable.Node;

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

        VirtualFileInputElement pdfUO = createVirtualBlankUserObject();

        Node file = new Node(pdfUO);
        insertPages(file);

        return file;
    }

    private VirtualFileInputElement createVirtualBlankUserObject() {
        VirtualFileInputElement pdfUO = new VirtualFileInputElement(filepath, FileInputElement.SubType.BLANK, "");

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
