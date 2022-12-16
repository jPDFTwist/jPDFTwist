package jpdftwist.core.input;

import jpdftwist.core.IntegerList;

public abstract class FileInputElement extends InputElement {

    private Object id;
    private final SubType subType;
    private int pages;
    private int from;
    private int to;
    private boolean even;
    private boolean odd;
    private IntegerList emptyBefore;

    public FileInputElement() {
        super(null, null);
        this.subType = null;
    }

    public FileInputElement(final String key, final InputElementType type, final SubType subType) {
        super(key, type);
        this.subType = subType;
    }

    public Object getValueAt(final int columnIndex) {
        final TreeTableColumn column = TreeTableColumn.fromIndex(columnIndex);
        switch (column) {
            case ID:
                return this.id;
            case FILE:
                return this.getFileName();
            case PAGES:
                return this.pages;
            case FROM:
                return this.from;
            case TO:
                return this.to;
            case EVEN:
                return this.even;
            case ODD:
                return this.odd;
            case EMPTY_BEFORE:
                return this.emptyBefore;
            default:
                return "";
        }
    }

    public void setValueAt(final Object value, final TreeTableColumn column) {
        switch (column) {
            case ID:
                this.id = value;
                break;
            case PAGES:
                this.pages = (int) value;
                break;
            case FROM:
                this.from = (int) value;
                break;
            case TO:
                this.to = (int) value;
                break;
            case EVEN:
                this.even = (boolean) value;
                break;
            case ODD:
                this.odd = (boolean) value;
                break;
            case EMPTY_BEFORE:
                this.emptyBefore = (IntegerList) value;
                break;
        }
    }

    public SubType getSubType() {
        return this.subType;
    }

    public enum SubType {
        IMAGE,
        PDF,
        BLANK
    }
}
