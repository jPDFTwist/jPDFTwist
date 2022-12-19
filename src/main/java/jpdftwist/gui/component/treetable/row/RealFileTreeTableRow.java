package jpdftwist.gui.component.treetable.row;

import jpdftwist.core.Utils;

/**
 * @author Vasilis Naskos
 */
public class RealFileTreeTableRow extends FileTreeTableRow {

    private long filesize;

    public RealFileTreeTableRow() {
        super(null, TreeTableRowType.REAL_FILE, null);
    }

    public RealFileTreeTableRow(String key, SubType subType) {
        super(key, TreeTableRowType.REAL_FILE, subType);
    }

    public Object getValueAt(int columnIndex) {
        TreeTableColumn column = TreeTableColumn.fromIndex(columnIndex);

        if (column == TreeTableColumn.SIZE) {
            return Utils.readableFileSize(filesize);
        }

        return super.getValueAt(columnIndex);
    }

    public void setValueAt(Object value, TreeTableColumn column) {
        if (column == TreeTableColumn.SIZE) {
            filesize = (Long) value;
        } else {
            super.setValueAt(value, column);
        }
    }
}
