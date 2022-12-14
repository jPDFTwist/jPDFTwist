package jpdftwist.tabs.input.pagerange;

import jpdftwist.core.PageRange;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.TreeTableRowType;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class MultiPageRangeGenerator extends PageRangeGenerator {

	private final Node root;

	public MultiPageRangeGenerator(Node root) {
		this.root = root;
	}

	
	public List<PageRange> generate(int taskIndex) {
		return getPageRanges(root);
	}

	private List<PageRange> getPageRanges(Node parent) {
		List<PageRange> pageRanges = new ArrayList<PageRange>();

		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			Node child = (Node) e.nextElement();
			if (TreeTableRowType.isFile(child)) {
				PageRange pageRange = getPageRange(child);
				if (pageRange != null)
					pageRanges.add(pageRange);
			} else if (child.getUserObject().getType() == TreeTableRowType.FOLDER) {
				pageRanges.addAll(getPageRanges(child));
			}
		}

		return pageRanges;
	}

}