package jpdftwist.gui.component.treetable;

import jpdftwist.core.input.InputElement;
import jpdftwist.core.input.InputElementType;
import jpdftwist.core.input.PageInputElement;
import jpdftwist.core.input.TreeTableColumn;
import jpdftwist.utils.PreferencesUtil;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.StringValue;

import javax.swing.*;
import java.awt.*;

public class TreeTableRenderer extends DefaultTreeRenderer {

    public static final String ROOT_NODE_KEY = "/";

    public TreeTableRenderer() {
        super((StringValue) o -> {
            if (o == null) {
                return "";
            }
            if (o instanceof Node) {
                final InputElement uo = ((Node) o).getUserObject();

                return uo.getKey().equals(ROOT_NODE_KEY)
                    ? ROOT_NODE_KEY
                    : uo.getValueAt(TreeTableColumn.FILE, String.class);
            }
            if (o instanceof InputElement) {
                final InputElement uo = (InputElement) o;

                return uo.getKey().equals(ROOT_NODE_KEY)
                    ? ROOT_NODE_KEY
                    : uo.getValueAt(TreeTableColumn.FILE, String.class);
            }
            return "";
        });
    }

    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
                                                  final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        final Component C = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        C.setForeground(Color.BLACK);
        final InputElement uo = ((Node) value).getUserObject();
        if (InputElementType.isFile(uo)) {
            if (uo.getType() == InputElementType.VIRTUAL_FILE) {
                final Color blank = new Color(PreferencesUtil.PREFS.getInt("blankColor", -16711681));
                C.setBackground(blank);
            } else {
                final Color file = new Color(PreferencesUtil.PREFS.getInt("fileColor", -256));
                C.setBackground(file);
            }
        } else if (uo instanceof PageInputElement) {
            final Color page = new Color(PreferencesUtil.PREFS.getInt("pageColor", -855568));
            C.setBackground(page);
        } else {
            final Color folder = new Color(PreferencesUtil.PREFS.getInt("folderColor", -855568));
            C.setBackground(folder);
        }
        return C;
    }
}
