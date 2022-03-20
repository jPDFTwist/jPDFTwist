package jpdftweak.tabs.input.treetable.node.factory;

import jpdftweak.tabs.input.treetable.UserObjectValue;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.PageUserObject;

/**
 *
 * @author Vasilis Naskos
 */
public class DefaultPageNodeFactory extends PageNodeFactory {

	
	public Node getPageNode(int index) {
		PageUserObject puo = new PageUserObject(Integer.toString(index));

		if (size != null) {
			puo.setValueAt(getPageOrientation(), UserObjectValue.ORIENTATION);
			puo.setWidth(size.getWidth());
			puo.setHeight(size.getHeight());
		}

		if (color != -1) {
			puo.setBackgroundColor(color);
		}

		if (colorDepth != -1) {
			puo.setValueAt(getColorDepth(), UserObjectValue.COLOR_DEPTH);
		}

		return new Node(puo, false);
	}

	private String getPageOrientation() {
		String orientation = "Portait";

		if (size.getWidth() > size.getHeight()) {
			orientation = "Landscape";
		}

		return orientation;
	}

	private String getColorDepth() {
		return colorDepth == -1 ? "" : Integer.toString(colorDepth);
	}

}
