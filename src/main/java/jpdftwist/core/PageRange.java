package jpdftwist.core;

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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class PageRange {

    private final FileInputElement fileInputElement;
    private final List<Integer> pageOrder;

    public PageRange(Node fileNode) {
        FileTreeTableRow fileTreeTableRow = (FileTreeTableRow) fileNode.getUserObject();
        fileInputElement = new FileInputElement(
            fileNode.getUserObject().getKey(),
            ((Node) fileNode.getParent()).getUserObject().getKey(),
            fileTreeTableRow.getType() == TreeTableRowType.VIRTUAL_FILE,
            convertTreeTableRowTypeToFileInputElementType(fileTreeTableRow.getSubType()),
            fileTreeTableRow.getValueAt(TreeTableColumn.EMPTY_BEFORE, IntegerList.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.FROM, Integer.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.TO, Integer.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.EVEN, Boolean.class),
            fileTreeTableRow.getValueAt(TreeTableColumn.ODD, Boolean.class)
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
        this.pageOrder = new ArrayList<>();

        Enumeration<? extends MutableTreeTableNode> e = fileNode.children();
        while (e.hasMoreElements()) {
            Node n = (Node) e.nextElement();
            PageTreeTableRow puo = (PageTreeTableRow) n.getUserObject();

            int position = Integer.parseInt(puo.getKey()) - 1;
            pageOrder.add(position);
        }
    }

    public int[] getPages(int pagesBefore) {
        IntegerList emptyBefore = fileInputElement.getEmptyBefore();
        Integer from = fileInputElement.getFrom();
        Integer to = fileInputElement.getTo();
        Boolean includeEven = fileInputElement.getIncludeEven();
        Boolean includeOdd = fileInputElement.getIncludeOdd();

        int emptyPagesBefore = emptyBefore.getValue()[pagesBefore % emptyBefore.getValue().length];
        int[] pages = new int[emptyPagesBefore + Math.abs(to - from) + 1];
        Arrays.fill(pages, 0, emptyPagesBefore, -1);
        int length = emptyPagesBefore;
        for (int i = from; ; i += from > to ? -1 : 1) {
            if ((i % 2 == 0 && includeEven) || (i % 2 == 1 && includeOdd)) {
                if (pageOrder.size() >= i)
                    pages[length++] = pageOrder.get(i - 1) + 1;
            }
            if (i == to) {
                break;
            }
        }
        if (length != pages.length) {
            int[] newPages = new int[length];
            System.arraycopy(pages, 0, newPages, 0, length);
            return newPages;
        }
        return pages;
    }

    public String getFilename() {
        return fileInputElement.getFileName();
    }

    public String getName() {
        return fileInputElement.getFilePath();
    }

    public String getParentName() {
        return fileInputElement.getParentFilePath();
    }

    public boolean isPDF() {
        return fileInputElement.isPDF();
    }

    public boolean isImage() {
        return fileInputElement.isImage();
    }

    public boolean isVirtualFile() {
        return fileInputElement.isVirtual();
    }

    public VirtualBlankPage getVirtualBlankPageTemplate() {
        return fileInputElement.getVirtualBlankPage();
    }

    public PageDimensions getImageSize() {
        return fileInputElement.getImageSize();
    }

    public Integer getVirtualFilePageCount() {
        return fileInputElement.getVirtualFilePageCount();
    }

    public String getVirtualFileSrcFilePath() {
        return fileInputElement.getVirtualFileSrcFilePath();
    }

    public String getFileSize() {
        return fileInputElement.getFileSize();
    }

    public String getImageColorDepth() {
        return fileInputElement.getColorDepth();
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
                throw new RuntimeException("Cannot parse unknown type " + fileRowSubType);
        }
    }
}
