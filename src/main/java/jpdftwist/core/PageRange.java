package jpdftwist.core;

import jpdftwist.core.input.FileInputElement;
import jpdftwist.core.input.InputElementType;
import jpdftwist.core.input.PageInputElement;
import jpdftwist.core.input.TreeTableColumn;
import jpdftwist.core.input.VirtualFileInputElement;
import jpdftwist.gui.component.treetable.Node;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class PageRange {
	private final Node node;
	private final FileInputElement fileInputElement;
	private final List<Integer> pageOrder;

	public PageRange(Node fileNode) {
		this.node = fileNode;
		fileInputElement = (FileInputElement) fileNode.getUserObject();
		pageOrder = new ArrayList<>();

		Enumeration<? extends MutableTreeTableNode> e = fileNode.children();
		while (e.hasMoreElements()) {
			Node n = (Node) e.nextElement();
			PageInputElement puo = (PageInputElement) n.getUserObject();

			int position = Integer.parseInt(puo.getKey()) - 1;
			pageOrder.add(position);
		}
	}

	public int[] getPages(int pagesBefore) {
		IntegerList emptyBefore = fileInputElement.getValueAt(TreeTableColumn.EMPTY_BEFORE, IntegerList.class);
		Integer from = fileInputElement.getValueAt(TreeTableColumn.FROM, Integer.class);
		Integer to = fileInputElement.getValueAt(TreeTableColumn.TO, Integer.class);
		Boolean includeEven = fileInputElement.getValueAt(TreeTableColumn.EVEN, Boolean.class);
		Boolean includeOdd = fileInputElement.getValueAt(TreeTableColumn.ODD, Boolean.class);

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

	public String getFilename() {
		return node.getUserObject().getFileName();
	}

	public String getName() {
		return node.getUserObject().getKey();
	}

	public String getParentName() {
		return ((Node) node.getParent()).getUserObject().getKey();
	}

	public InputElementType getType() {
		return fileInputElement.getType();
	}

	public FileInputElement.SubType getSubType() {
		return fileInputElement.getSubType();
	}

	public boolean isPDF() {
		return fileInputElement.getSubType() == FileInputElement.SubType.PDF;
	}

	public boolean isImage() {
		return fileInputElement.getSubType() == FileInputElement.SubType.IMAGE;
	}

	public boolean isRealFile() {
		return fileInputElement.getType() == InputElementType.REAL_FILE;
	}

	public boolean isVirtualFile() {
		return fileInputElement.getType() == InputElementType.VIRTUAL_FILE;
	}

	public List<VirtualPage> getVirtualBlankPages() {
		if (fileInputElement.getType() != InputElementType.VIRTUAL_FILE && fileInputElement.getSubType() != FileInputElement.SubType.BLANK) {
			return Collections.emptyList(); // FIXME: Throw instead of returning
		}

		final List<VirtualPage> virtualBlankPages = new ArrayList<>();

		Enumeration<? extends MutableTreeTableNode> e = node.children();
		while (e.hasMoreElements()) {
			Node pageNode = (Node) e.nextElement();
			PageInputElement page = (PageInputElement) pageNode.getUserObject();
			VirtualPage virtualPage = new VirtualPage(page.getWidth(), page.getHeight(), page.getBackgroundColor());
			virtualBlankPages.add(virtualPage);
		}

		return virtualBlankPages;
	}

	public Page getFirstPage() {
		if (!InputElementType.isFile(node)) {
			return null; // FIXME: Throw instead of returning
		}

		PageInputElement page = (PageInputElement) node.children().nextElement();
		return new Page(page.getWidth(), page.getHeight());
	}
	
	public Integer getVirtualFilePageCount() {
		if (fileInputElement.getType() != InputElementType.VIRTUAL_FILE) {
			return null; // FIXME: Throw instead of returning
		}

		return fileInputElement.getValueAt(TreeTableColumn.PAGES, Integer.class);
	}

	public String getVirtualFileSrcFilePath() {
		if (fileInputElement.getType() != InputElementType.VIRTUAL_FILE) {
			return null; // FIXME: Throw instead of returning
		}

		return ((VirtualFileInputElement) fileInputElement).getSrcFilePath();
	}

	public String getFileSize() {
		if (fileInputElement.getType() != InputElementType.REAL_FILE) {
			return "NaN"; // FIXME: Throw instead of returning
		}

		return fileInputElement.getValueAt(TreeTableColumn.SIZE, String.class);
	}

	public String getImageColorDepth() {
		if (fileInputElement.getSubType() != FileInputElement.SubType.IMAGE) {
			return "NaN"; // FIXME: Throw instead of returning
		}

		return fileInputElement.getValueAt(TreeTableColumn.COLOR_DEPTH, Integer.class).toString();
	}

	public static class Page {
		private final double width;
		private final double height;

		public Page(double width, double height) {
			this.width = width;
			this.height = height;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}
	}

	public static class VirtualPage extends Page {
		private final Color backgroundColor;

		public VirtualPage(double width, double height, Color backgroundColor) {
			super(width, height);
			this.backgroundColor = backgroundColor;
		}

		public Color getBackgroundColor() {
			return backgroundColor;
		}
	}
}
