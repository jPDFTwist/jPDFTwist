package jpdftwist.gui.component.treetable;

/**
 * @author Vasilis Naskos
 */
public enum TreeTableRowType {

    FOLDER,
    REAL_FILE,
    VIRTUAL_FILE,
    PAGE;

    //TODO: Check has to be moved to the Node
    public static boolean isFile(final Node n) {
        return isFile(n.getUserObject());
    }

    public static boolean isFile(final TreeTableRow row) {
        return row.getType() == REAL_FILE ||
               row.getType() == VIRTUAL_FILE;
    }
}
