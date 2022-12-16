package jpdftwist.core.input;

import jpdftwist.core.Utils;

/**
 * @author Vasilis Naskos
 */
public class RealFileInputElement extends FileInputElement {

    private long filesize;

    public RealFileInputElement() {
        super(null, InputElementType.REAL_FILE, null);
    }

    public RealFileInputElement(String key, SubType subType) {
        super(key, InputElementType.REAL_FILE, subType);
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
