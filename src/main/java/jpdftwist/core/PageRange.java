package jpdftwist.core;

import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.TreeTableColumn;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.PageTreeTableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class PageRange {

	private final Node node;
	private final FileTreeTableRow fileUO;
	private final List<Integer> pageOrder;

	public PageRange(Node fileNode) {
		this.node = fileNode;
		fileUO = (FileTreeTableRow) fileNode.getUserObject();
		pageOrder = new ArrayList<Integer>();

		Enumeration e = fileNode.children();
		while (e.hasMoreElements()) {
			Node n = (Node) e.nextElement();
			PageTreeTableRow puo = (PageTreeTableRow) n.getUserObject();

			int position = Integer.parseInt(puo.getKey()) - 1;
			pageOrder.add(position);
		}
	}

	public Node getNode() {
		return node;
	}

	public FileTreeTableRow getFileUO() {
		return fileUO;
	}

	public int[] getPages(int pagesBefore) {
		IntegerList emptyBefore = fileUO.getValueAt(TreeTableColumn.EMPTY_BEFORE, IntegerList.class);
		Integer from = fileUO.getValueAt(TreeTableColumn.FROM, Integer.class);
		Integer to = fileUO.getValueAt(TreeTableColumn.TO, Integer.class);
		Boolean includeEven = fileUO.getValueAt(TreeTableColumn.EVEN, Boolean.class);
		Boolean includeOdd = fileUO.getValueAt(TreeTableColumn.ODD, Boolean.class);

		int emptyPagesBefore = emptyBefore.getValue()[pagesBefore % emptyBefore.getValue().length];
		int[] pages = new int[emptyPagesBefore + Math.abs(to - from) + 1];
		Arrays.fill(pages, 0, emptyPagesBefore, -1);
		int length = emptyPagesBefore;
		for (int i = from;; i += from > to ? -1 : 1) {
			if ((i % 2 == 0 && includeEven) || (i % 2 == 1 && includeOdd)) {
				if (pageOrder.size() >= i)
					pages[length++] = pageOrder.get(i - 1) + 1;
			}
			if (i == to) {
				break;
			}
		}
		if (length != pages.length) {
			int[] newPages = new int[length];
			System.arraycopy(pages, 0, newPages, 0, length);
			return newPages;
		}
		return pages;
	}

	public int getPage(int i) {
		return pageOrder.get(i);
	}

}
