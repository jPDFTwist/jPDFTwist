package jpdftwist.tabs.input.treetable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import jpdftwist.core.FilenameUtils;
import jpdftwist.tabs.input.treetable.node.Node;
import jpdftwist.tabs.input.treetable.node.userobject.FolderUserObject;
import jpdftwist.tabs.input.treetable.node.userobject.UserObjectType;

/**
 *
 * @author Vasilis Naskos
 */
public class FileTreeTableModel extends DefaultTreeTableModel implements SwapObserver {

	protected final Class[] columnClasses;

	public FileTreeTableModel(String[] columnNames, Class[] columnClasses) {
		super();

		initRoot();

		setColumnIdentifiers(Arrays.asList(columnNames));
		this.columnClasses = columnClasses;
	}

	private void initRoot() {
		setRoot(new Node(new FolderUserObject("Root")));
		Node.setObserver(this);
	}

	
	public Class<?> getColumnClass(int column) {
		return columnClasses[column];
	}

	
	public Node getRoot() {
		return (Node) super.getRoot();
	}

	
	public boolean isCellEditable(Object node, int column) {
		if (!(node instanceof Node)) {
			return false;
		}

		Node n = (Node) node;

		return UserObjectType.isFile(n) && column > 5;
	}

	
	public void notify(Node node, int index) {
		Node parent = (Node) node.getParent();
		removeNodeFromParent(node, false);
		insertNodeInto(node, parent, index);
	}

	
	public Object getValueAt(Object row, int column) {
		Node node = (Node) row;

		UserObjectValue headerValue = UserObjectValue.fromInt(column);

		switch (headerValue) {
		case BOOKMARK_LEVEL:
			return new TreePath(getPathToRoot(node)).getPathCount() - 1;
		default:
			return node.getValueAt(column);
		}
	}

	/**
	 * Remove all Nodes/Rows except root
	 */
	public void clear() {
		Node rootNode = (Node) root;

		// Nothing to remove
		if (rootNode.getChildCount() == 0) {
			return;
		}

		remove(rootNode);
	}

	public void removeNodeFromParent(Node node, boolean recursive) {
		// remove(node);
		super.removeNodeFromParent(node);
	}

	// TODO recursivly if there is memory leak
	/**
	 * Remove all children from a Parent Node recursively
	 *
	 * @param parent Starting Point/Node
	 */
	private void remove(Node parent) {
		Stack<Node> nodes = new Stack<Node>();
		Enumeration e = parent.children();
		while (e.hasMoreElements()) {
			Node nextNode = (Node) e.nextElement();
			nodes.push(nextNode);
		}

		while (!nodes.isEmpty()) {
			Node node = nodes.pop();
			super.removeNodeFromParent(node);
		}
	}

	public TreePath moveRow(TreePath path, int offset) {
		Node node = (Node) path.getLastPathComponent();
		Node parent = (Node) node.getParent();

		if (offset == -1 && parent.getIndex(node) == 0) {
			return path;
		}

		if (offset == 1 && parent.getIndex(node) == parent.getChildCount() - 1) {
			return path;
		}

		int index = parent.getIndex(node);

		removeNodeFromParent(node, false);
		insertNodeInto(node, parent, index + offset);

		return new TreePath(getPathToRoot(node));
	}

	/**
	 * Check if there are no files in the model
	 *
	 * @return true if model is empty
	 */
	public boolean isEmpty() {
		return getFileCount((Node) root) == 0;
	}

	/**
	 * Get the number of File children from a node In order to get all files send as
	 * parameter the root node
	 *
	 * @param node the first node
	 * @return count of file nodes
	 */
	public int getFileCount(Node node) {
		int childCount = 0;

		Enumeration e = node.children();
		while (e.hasMoreElements()) {
			Node child = (Node) e.nextElement();

			if (child.getUserObject() instanceof FolderUserObject) {
				childCount += getFileCount(child);
			} else if (UserObjectType.isFile(child)) {
				childCount++;
			}
		}

		return childCount;
	}

	/**
	 * Get the number of Folder nodes from a starting node In order to get all
	 * folders send as parameter the root node
	 *
	 * @param node the first node
	 * @return count of file nodes
	 */
	public int getFolderCount(Node node) {
		int folderCount = 0;

		Enumeration e = node.children();
		while (e.hasMoreElements()) {
			Node child = (Node) e.nextElement();

			if (child.getUserObject() instanceof FolderUserObject) {
				if (getFileCount(node) > 0)
					folderCount++;

				folderCount += getFileCount(child);
			}
		}

		return folderCount;
	}

	public Node createParents(String filepath) {
		if (filepath == null) {
			return getRoot();
		}

		List<String> strParents = new ArrayList<String>();
		filepath = FilenameUtils.normalize(filepath);

		File p = new File(filepath).getParentFile();
		while (p != null) {
			strParents.add(p.getAbsolutePath());
			p = p.getParentFile();
		}

		Collections.reverse(strParents);

		if (strParents.get(0).equals("")) {
			strParents.remove(0);
			strParents.add(0, File.separator);
		}

		Node parentNode = getRoot();

		for (String parent : strParents) {
			Node potentialParent = parentNode.findChild(parent);

			if (potentialParent != null && potentialParent.getAllowsChildren()) {
				parentNode = potentialParent;
			} else {
				Node newNode = new Node(new FolderUserObject(parent), true);

				insertNodeInto(newNode, parentNode, parentNode.getChildCount());
				parentNode = newNode;
			}
		}

		return parentNode;
	}

}
