package jpdftwist.core.input;

import jpdftwist.gui.component.treetable.Node;

/**
 * @author Vasilis Naskos
 */
public enum InputElementType {

    FOLDER,
    REAL_FILE,
    VIRTUAL_FILE,
    PAGE;

    //TODO: Check has to be moved to the Node
    public static boolean isFile(final Node n) {
        return isFile(n.getUserObject());
    }

    public static boolean isFile(final InputElement row) {
        return row.getType() == REAL_FILE ||
            row.getType() == VIRTUAL_FILE;
    }
}
