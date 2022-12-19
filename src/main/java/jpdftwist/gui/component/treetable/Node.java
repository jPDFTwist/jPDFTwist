package jpdftwist.gui.component.treetable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoCopyable;
import jpdftwist.gui.component.treetable.row.TreeTableColumn;
import jpdftwist.gui.component.treetable.row.TreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableRowType;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Vasilis Naskos
 */
public class Node extends AbstractMutableTreeTableNode implements KryoCopyable<Node> {

    private static SwapObserver observer;

    public Node() {
    }

    public Node(TreeTableRow data) {
        this(data, true);
    }

    public Node(TreeTableRow data, boolean allowChildren) {
        super(data);
        this.allowsChildren = allowChildren;
    }

    public static void setObserver(SwapObserver observer) {
        Node.observer = observer;
    }

    @Override
    public TreeTableRow getUserObject() {
        return (TreeTableRow) userObject;
    }

    @Override
    public Object getValueAt(int i) {
        return ((TreeTableRow) userObject).getValueAt(i);
    }

    @Override
    public void setValueAt(Object aValue, int column) {
        ((TreeTableRow) userObject).setValueAt(aValue, column);
    }

    @Override
    public int getColumnCount() {
        return TreeTableColumn.values().length;
    }

    /**
     * This method recursively (or not) sorts the nodes, ascending, or descending by
     * the specified column.
     *
     * @param sortColumn    Column to do the sorting by.
     * @param sortAscending Boolean value of weather the sorting to be done
     *                      ascending or not (descending).
     * @param recursive     Boolean value of weather or not the sorting should be
     *                      recursively applied to children nodes.
     */
    public void sortNode(int sortColumn, boolean sortAscending, boolean recursive) {
        int childCount = this.getChildCount();
        TreeMap<Object, Node> nodeData = new TreeMap<>();

        for (int i = 0; i < childCount; i++) {
            Node child = (Node) this.getChildAt(i);
            if (!child.isLeaf()) {
                return;
            }

            if (child.getChildCount() > 0 && recursive) {
                child.sortNode(sortColumn, sortAscending, recursive);
            }
            TreeTableRow u = child.getUserObject();
            nodeData.put(u.getKey(), child);
        }

        Iterator<Map.Entry<Object, Node>> nodesIterator;
        if (sortAscending) {
            nodesIterator = nodeData.entrySet().iterator();
        } else {
            nodesIterator = nodeData.descendingMap().entrySet().iterator();
        }

        int index = 0;
        while (nodesIterator.hasNext()) {
            Map.Entry<Object, Node> nodeEntry = nodesIterator.next();
            swap(nodeEntry.getValue(), index);
            index++;
        }
    }

    private void swap(Node node, int index) {
        Node.observer.notify(node, index);
    }

    public Node findChild(String key) {
        for (MutableTreeTableNode child : children) {
            Node node = (Node) child;
            if (node.getUserObject().getKey().equals(key)) {
                return node;
            }
        }

        return null;
    }

    public boolean isFile() {
        TreeTableRow treeTableRow = getUserObject();

        return treeTableRow.getType() == TreeTableRowType.REAL_FILE ||
            treeTableRow.getType() == TreeTableRowType.VIRTUAL_FILE;
    }

    public Node copy(Kryo kryo) {
        TreeTableRow uo = (TreeTableRow) kryo.copy(userObject);

        return new Node(uo);
    }
}
