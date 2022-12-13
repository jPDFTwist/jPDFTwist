package jpdftwist.gui.components.treetable.row;

import jpdftwist.gui.components.treetable.TreeTableColumn;
import jpdftwist.gui.components.treetable.TreeTableRowType;

import java.io.File;

/**
 * @author Vasilis Naskos
 */
public class VirtualFileTreeTableRow extends FileTreeTableRow {

    private String parent;
    private String srcFilePath; //TODO: Check how do we use that

    public VirtualFileTreeTableRow() {
    }

    public VirtualFileTreeTableRow(String key, SubType subType, String parent) {
        super(key, TreeTableRowType.VIRTUAL_FILE, subType);
        this.parent = parent;
    }

    public Object getValueAt(int column) {
        TreeTableColumn headerValue = TreeTableColumn.fromIndex(column);

        if (headerValue == TreeTableColumn.SIZE) {
            return "";
        }
        return super.getValueAt(column);
    }

    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParent() {
        return parent;
    }


    public String getKey() {
        if (parent.endsWith(File.separator)) {
            return parent + key;
        }

        return parent + File.separator + key;
    }
}
