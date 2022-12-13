package jpdftwist.gui.components.treetable.row;

import jpdftwist.gui.components.treetable.TreeTableColumn;
import jpdftwist.gui.components.treetable.TreeTableRow;
import jpdftwist.gui.components.treetable.TreeTableRowType;

/**
 * @author Vasilis Naskos
 */
public class FolderTreeTableRow extends TreeTableRow {

    public FolderTreeTableRow() {
        super(null, TreeTableRowType.FOLDER);
    }

    public FolderTreeTableRow(final String key) {
        super(key, TreeTableRowType.FOLDER);
    }

    public Object getValueAt(final int columnIndex) {
        final TreeTableColumn column = TreeTableColumn.fromIndex(columnIndex);

        if (column == TreeTableColumn.FILE) {
            return this.getFileName().length() == 0 ? this.getKey() : this.getFileName();
        }

        return "";
    }

    public void setValueAt(final Object value, final TreeTableColumn column) {
        //TODO: Check if this is empty on purpose
    }
}
