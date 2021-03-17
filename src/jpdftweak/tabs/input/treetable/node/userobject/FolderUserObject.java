package jpdftweak.tabs.input.treetable.node.userobject;

import jpdftweak.tabs.input.treetable.UserObjectValue;

/**
 *
 * @author Vasilis Naskos
 */
public class FolderUserObject extends UserObject {

    public FolderUserObject() {
        super(null, UserObjectType.FOLDER);
    }
    
    public FolderUserObject(String key) {
        super(key, UserObjectType.FOLDER);
    }

    @Override
    public Object getValueAt(int column) {
        UserObjectValue headerValue = UserObjectValue.fromInt(column);
        
        switch(headerValue) {
            case FILE:
                String filename = getFileName();
                return getKey().equals("/") ? getKey() : filename;
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object value, UserObjectValue column) {
    }
    
}
