package jpdftweak.tabs.input.treetable.node.userobject;

import jpdftweak.core.IntegerList;
import jpdftweak.tabs.input.treetable.UserObjectValue;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class FileUserObject extends UserObject {

    public enum SubType {
        IMAGE, PDF, BLANK
    }
    
    private final SubType subType;
    private int pages;
    private int from, to;
    private boolean even, odd;
    private IntegerList emptyBefore;

    public FileUserObject() {
        super(null, null);
        this.subType = null;
    }
    
    public FileUserObject(String key, UserObjectType type, SubType subType) {
        super(key, type);
        this.subType = subType;
    }
    
    @Override
    public Object getValueAt(int column) {
        UserObjectValue headerValue = UserObjectValue.fromInt(column);
        
        switch(headerValue) {
            case FILE:
                return getFileName();
            case PAGES:
                return pages;
            case FROM:
                return from;
            case TO:
                return to;
            case EVEN:
                return even;
            case ODD:
                return odd;
            case EMPTY_BEFORE:
                return emptyBefore;
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object value, UserObjectValue column) {
        switch(column) {
            case PAGES:
                pages = (Integer) value;
                break;
            case FROM:
                from = (Integer) value;
                break;
            case TO:
                to = (Integer) value;
                break;
            case EVEN:
                even = (Boolean) value;
                break;
            case ODD:
                odd = (Boolean) value;
                break;
            case EMPTY_BEFORE:
                emptyBefore = (IntegerList) value;
                break;
        }
    }

    public SubType getSubType() {
        return subType;
    }
}
