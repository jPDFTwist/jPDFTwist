package jpdftwist.gui.components.treetable;

import java.io.File;

/**
 * @author Vasilis Naskos
 */
public abstract class TreeTableRow {

    protected final String key;
    private final TreeTableRowType type;

    public TreeTableRow(String key, TreeTableRowType type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public TreeTableRowType getType() {
        return type;
    }

    public String getFileName() {
        int beginIndex = key.lastIndexOf(File.separatorChar);

        return key.substring(beginIndex + 1);
    }

    public <T> T getValueAt(TreeTableColumn v, Class<T> type) {
        return type.cast(getValueAt(v.getIndex()));
    }

    public abstract Object getValueAt(int column);

    public void setValueAt(Object value, int column) {
        TreeTableColumn headerValue = TreeTableColumn.fromIndex(column);

        setValueAt(value, headerValue);
    }

    public abstract void setValueAt(Object value, TreeTableColumn column);
}
