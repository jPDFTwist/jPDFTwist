package jpdftweak.tabs.input.treetable.node.factory;

import com.itextpdf.text.Rectangle;

import jpdftweak.core.IntegerList;
import jpdftweak.tabs.input.treetable.UserObjectValue;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.VirtualFileUserObject;

/**
 *
 * @author Vasilis Naskos
 */
public class VirtualBlankNodeFactory extends FileNodeFactory {

	private String filepath;
	private int pageCount;
	private int color;
	private Rectangle size;

	
	public Node getFileNode(String filepath) {
		this.filepath = filepath;

		VirtualFileUserObject pdfUO = createVirtualBlankUserObject();

		Node file = new Node(pdfUO);
		insertPages(file);

		return file;
	}

	private VirtualFileUserObject createVirtualBlankUserObject() {
		VirtualFileUserObject pdfUO = new VirtualFileUserObject(filepath, FileUserObject.SubType.BLANK, "");

		pdfUO.setValueAt(pageCount, UserObjectValue.PAGES);
		pdfUO.setValueAt(1, UserObjectValue.FROM);
		pdfUO.setValueAt(pageCount, UserObjectValue.TO);
		pdfUO.setValueAt(true, UserObjectValue.EVEN);
		pdfUO.setValueAt(true, UserObjectValue.ODD);
		pdfUO.setValueAt(new IntegerList("0"), UserObjectValue.EMPTY_BEFORE);
		pdfUO.setValueAt(0, UserObjectValue.BOOKMARK_LEVEL);

		return pdfUO;
	}

	private void insertPages(Node file) {
		updateListenersPageCount(pageCount);

		PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();

		for (int i = 1; i <= pageCount; i++) {
			pageNodeFactory.setColor(color);
			pageNodeFactory.setSize(size);

			Node page = pageNodeFactory.getPageNode(i);

			file.insert(page, i - 1);
		}
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setSize(Rectangle size) {
		this.size = size;
	}

}
