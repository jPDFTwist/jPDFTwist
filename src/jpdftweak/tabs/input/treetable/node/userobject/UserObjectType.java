package jpdftweak.tabs.input.treetable.node.userobject;

import jpdftweak.tabs.input.treetable.node.Node;

/**
 *
 * @author Vasilis Naskos
 */
public enum UserObjectType {
    FOLDER, REAL_FILE, VIRTUAL_FILE, PAGE;
    
    public static boolean isFile(Node n) {
        return isFile(n.getUserObject());
    }
    
    public static boolean isFile(UserObject uo) {
        return uo.getType() == REAL_FILE || uo.getType() == VIRTUAL_FILE;
    }
}
