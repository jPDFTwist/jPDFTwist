package jpdftwist.tabs.input.treetable.node;

import com.itextpdf.text.Rectangle;
import jpdftwist.core.input.PageInputElement;
import jpdftwist.core.input.TreeTableColumn;
import jpdftwist.gui.component.treetable.Node;

/**
 * @author Vasilis Naskos
 */
public class PageNodeFactory {

	protected int color;
	protected Rectangle size;
	protected int colorDepth;

	public PageNodeFactory() {
		color = -1;
		colorDepth = -1;
	}

	public Node getPageNode(int index) {
		PageInputElement pageRow = new PageInputElement(Integer.toString(index));

		if (size != null) {
			pageRow.setValueAt(getPageOrientation(), TreeTableColumn.ORIENTATION);
			pageRow.setWidth(size.getWidth());
			pageRow.setHeight(size.getHeight());
		}

		if (color != -1) {
			pageRow.setBackgroundColor(color);
		}

		if (colorDepth != -1) {
			pageRow.setValueAt(getColorDepth(), TreeTableColumn.COLOR_DEPTH);
		}

		return new Node(pageRow, false);
	}

	public void setSize(Rectangle size) {
		this.size = size;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setColorDepth(int colorDepth) {
		this.colorDepth = colorDepth;
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
