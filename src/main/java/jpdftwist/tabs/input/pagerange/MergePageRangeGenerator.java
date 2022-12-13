package jpdftwist.tabs.input.pagerange;

import jpdftwist.core.PageRange;
import jpdftwist.gui.components.treetable.Node;
import jpdftwist.gui.components.treetable.TreeTableRowType;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class MergePageRangeGenerator extends PageRangeGenerator {

	private final List<Node> folders;

	public MergePageRangeGenerator(Node root) {
		folders = getFolders(root);
	}

	
	public List<PageRange> generate(int taskIndex) {
		List<PageRange> ranges = new ArrayList<PageRange>();

		Node parent = folders.get(taskIndex);

		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			Node node = (Node) e.nextElement();
			if (TreeTableRowType.isFile(node)) {
				PageRange pageRange = getPageRange(node);
				if (pageRange != null) {
					ranges.add(pageRange);
				}
			}
		}

		return ranges;
	}

	private List<Node> getFolders(Node parent) {
		List<Node> folderNodes = new ArrayList<Node>();

		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			Node child = (Node) e.nextElement();
			if (child.getUserObject().getType() == TreeTableRowType.FOLDER) {
				folderNodes.add(child);
				folderNodes.addAll(getFolders(child));
			}
		}

		return folderNodes;
	}

}
