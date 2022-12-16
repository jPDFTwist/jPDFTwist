package jpdftwist.core.input;

/**
 * @author Vasilis Naskos
 */
public class FolderInputElement extends InputElement {

    public FolderInputElement() {
        super(null, InputElementType.FOLDER);
    }

    public FolderInputElement(final String key) {
        super(key, InputElementType.FOLDER);
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
