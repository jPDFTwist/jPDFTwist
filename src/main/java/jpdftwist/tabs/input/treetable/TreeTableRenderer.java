package jpdftwist.tabs.input.treetable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;

import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.StringValue;

import jpdftwist.tabs.input.treetable.node.Node;
import jpdftwist.tabs.input.treetable.node.userobject.PageUserObject;
import jpdftwist.tabs.input.treetable.node.userobject.UserObject;
import jpdftwist.tabs.input.treetable.node.userobject.UserObjectType;
import jpdftwist.utils.PreferencesUtil;

class TreeTableRenderer extends DefaultTreeRenderer {
	public TreeTableRenderer() {
		super((StringValue) new StringValue() {
			public String getString(final Object o) {
				if (o == null) {
					return "";
				}
				if (o instanceof Node) {
					final UserObject uo = ((Node) o).getUserObject();
					return (String) (uo.getKey().equals("/") ? "/"
							: uo.getValueAt(UserObjectValue.FILE, (Class) String.class));
				}
				if (o instanceof UserObject) {

					final UserObject uo = (UserObject) o;

					return (String) (uo.getKey().equals("/") ? "/"
							: uo.getValueAt(UserObjectValue.FILE, (Class) String.class));
				}
				return "";
			}
		});
	}

	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		final Component C = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		C.setForeground(Color.BLACK);
		final UserObject uo = ((Node) value).getUserObject();
		if (UserObjectType.isFile(uo)) {
			if (uo.getType() == UserObjectType.VIRTUAL_FILE) {
				final Color blank = new Color(PreferencesUtil.PREFS.getInt("blankColor", -16711681));
				C.setBackground(blank);
			} else {
				final Color file = new Color(PreferencesUtil.PREFS.getInt("fileColor", -256));
				C.setBackground(file);
			}
		} else if (uo instanceof PageUserObject) {
			final Color page = new Color(PreferencesUtil.PREFS.getInt("pageColor", -855568));
			C.setBackground(page);
		} else {
			final Color folder = new Color(PreferencesUtil.PREFS.getInt("folderColor", -855568));
			C.setBackground(folder);
		}
		return C;
	}
}
