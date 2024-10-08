package jpdftwist.tabs.input.pagerange;

import jpdftwist.core.IntegerList;
import jpdftwist.core.OutputPdfProcessor;
import jpdftwist.core.PageRange;
import jpdftwist.core.input.FileInputElement;
import jpdftwist.core.input.FileInputElementType;
import jpdftwist.core.input.PageDimensions;
import jpdftwist.core.input.VirtualBlankPage;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.PageTreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableColumn;
import jpdftwist.gui.component.treetable.row.TreeTableRowType;
import jpdftwist.gui.component.treetable.row.VirtualFileTreeTableRow;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public abstract class PageRangeGenerator {

    public static PageRangeGenerator initGenerator(Node root, boolean batch, boolean mergeByDir) {
        if (batch)
            return new BatchPageRangeGenerator(root);
        else if (mergeByDir)
            return new MergePageRangeGenerator(root);
        else
            return new MultiPageRangeGenerator(root);
    }

    public abstract List<PageRange> generate(int taskIndex);

    public PageRange getPageRange(Node fileNode) {
        if (fileNode.getUserObject() == null) {
            return null;
        }
        // FIXME: check if it's file node

        FileTreeTableRow fileTreeTableRow = (FileTreeTableRow) fileNode.getUserObject();

        List<Integer> pageOrder = new ArrayList<>();

        Enumeration<? extends MutableTreeTableNode> e = fileNode.children();
        while (e.hasMoreElements()) {
            Node n = (Node) e.nextElement();
            PageTreeTableRow puo = (PageTreeTableRow) n.getUserObject();

            int position = Integer.parseInt(puo.getKey()) - 1;
            pageOrder.add(position);
        }

        FileInputElement fileInputElement = new FileInputElement(
            fileNode.getUserObject().getKey(),
            ((Node) fileNode.getParent()).getUserObject().getKey(),
            fileTreeTableRow.getType() == TreeTableRowType.VIRTUAL_FILE,
            convertTreeTableRowTypeToFileInputElementType(fileTreeTableRow.getSubType()),
            fileTreeTableRow.getValueAt(TreeTableColumn.EMPTY_BEFORE, IntegerList.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.FROM, Integer.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.TO, Integer.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.EVEN, Boolean.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.ODD, Boolean.class),
            pageOrder
        );
        if (fileInputElement.isVirtual()) {
            fileInputElement.setPageCount(fileTreeTableRow.getValueAt(TreeTableColumn.PAGES, Integer.class));
            fileInputElement.setSrcFilePath(((VirtualFileTreeTableRow) fileTreeTableRow).getSrcFilePath());
            if (fileInputElement.isBlank()) {
                PageTreeTableRow page = (PageTreeTableRow) fileNode.children().nextElement().getUserObject();
                fileInputElement.setVirtualBlankPage(new VirtualBlankPage(page.getBackgroundColor(), page.getWidth(), page.getHeight()));
            }
        } else {
            fileInputElement.setFileSize(fileTreeTableRow.getValueAt(TreeTableColumn.SIZE, String.class));
        }
        if (fileInputElement.isImage()) {
            PageTreeTableRow page = (PageTreeTableRow) fileNode.children().nextElement().getUserObject();
            fileInputElement.setImageSize(new PageDimensions(page.getWidth(), page.getHeight()));
            fileInputElement.setColorDepth(fileTreeTableRow.getValueAt(TreeTableColumn.COLOR_DEPTH, String.class));
        }

        return new PageRange(fileInputElement);
    }

    private FileInputElementType convertTreeTableRowTypeToFileInputElementType(FileTreeTableRow.SubType fileRowSubType) {
        switch (fileRowSubType) {
            case PDF:
                return FileInputElementType.PDF;
            case IMAGE:
                return FileInputElementType.IMAGE;
            case BLANK:
                return FileInputElementType.BLANK;
            default:
                //Logger.getLogger(PageRangeGenerator.class.getName()).log(Level.SEVERE, "Ex138");
                throw new RuntimeException("Cannot parse unknown type " + fileRowSubType);
        }
    }
}
