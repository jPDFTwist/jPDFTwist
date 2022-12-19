package jpdftwist.tabs.input.treetable.node;

import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableRowType;

/**
 * @author Vasilis Naskos
 */
public class NodeFactory {

    public static FileNodeFactory getFileNodeFactory(TreeTableRowType type, FileTreeTableRow.SubType subType) {
        switch (type) {
            case REAL_FILE:
                return getRealFileNodeFactory(subType);
            case VIRTUAL_FILE:
                return getVirtualFileNodeFactory(subType);
            default:
                return null;
        }
    }

    private static FileNodeFactory getRealFileNodeFactory(FileTreeTableRow.SubType subType) {
        switch (subType) {
            case PDF:
                return new RealPdfNodeFactory();
            case IMAGE:
                return new RealImageNodeFactory();
            default:
                return null;
        }
    }

    private static FileNodeFactory getVirtualFileNodeFactory(FileTreeTableRow.SubType subType) {
        switch (subType) {
            case PDF:
                return new VirtualPdfNodeFactory();
            case IMAGE:
                return new VirtualImageNodeFactory();
            case BLANK:
                return new VirtualBlankNodeFactory();
            default:
                return null;
        }
    }

    public static PageNodeFactory getPageNodeFactory() {
        return new PageNodeFactory();
    }

}
