package jpdftwist.gui.component.treetable;

import jpdftwist.core.FilenameUtils;
import jpdftwist.gui.component.treetable.row.FolderTreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableColumn;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.swing.tree.TreePath;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

/**
 * @author Vasilis Naskos
 */
public class TreeTableModel extends DefaultTreeTableModel implements SwapObserver {

    protected final Class<?>[] columnClasses;

    public TreeTableModel(String[] columnNames, Class<?>[] columnClasses) {
        super();

        initRoot();

        setColumnIdentifiers(Arrays.asList(columnNames));
        this.columnClasses = columnClasses;
    }

    private void initRoot() {
        setRoot(new Node(new FolderTreeTableRow("Root")));
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

        return n.isFile() && column > 5;
    }


    public void notify(Node node, int index) {
        Node parent = (Node) node.getParent();
        removeNodeFromParent(node);
        insertNodeInto(node, parent, index);
    }


    public Object getValueAt(Object row, int column) {
        Node node = (Node) row;

        TreeTableColumn headerValue = TreeTableColumn.fromIndex(column);

        if (headerValue == TreeTableColumn.BOOKMARK_LEVEL) {
            return new TreePath(getPathToRoot(node)).getPathCount() - 1;
        }
        return node.getValueAt(column);
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

    public void removeNodeFromParent(Node node) {
        super.removeNodeFromParent(node);
    }

    /**
     * Remove all children from a Parent Node recursively
     *
     * @param parent Starting Point/Node
     */
    private void remove(Node parent) {
        Stack<Node> nodes = new Stack<>();
        Enumeration<? extends MutableTreeTableNode> e = parent.children();
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

        removeNodeFromParent(node);
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

        Enumeration<? extends MutableTreeTableNode> e = node.children();
        while (e.hasMoreElements()) {
            Node child = (Node) e.nextElement();

            if (child.getUserObject() instanceof FolderTreeTableRow) {
                childCount += getFileCount(child);
            } else if (child.isFile()) {
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

        Enumeration<? extends MutableTreeTableNode> e = node.children();
        while (e.hasMoreElements()) {
            Node child = (Node) e.nextElement();

            if (child.getUserObject() instanceof FolderTreeTableRow) {
                if (getFileCount(node) > 0)
                    folderCount++;

                folderCount += getFolderCount(child);
            }
        }

        return folderCount;
    }

    public Node createParents(String filepath) {
        if (filepath == null) {
            return getRoot();
        }

        List<String> strParents = new ArrayList<>();
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
                Node newNode = new Node(new FolderTreeTableRow(parent), true);

                insertNodeInto(newNode, parentNode, parentNode.getChildCount());
                parentNode = newNode;
            }
        }

        return parentNode;
    }

}
