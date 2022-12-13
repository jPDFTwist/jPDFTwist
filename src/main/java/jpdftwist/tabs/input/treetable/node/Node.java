package jpdftwist.tabs.input.treetable.node;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoCopyable;

import jpdftwist.tabs.input.treetable.SwapObserver;
import jpdftwist.tabs.input.treetable.UserObjectValue;
import jpdftwist.tabs.input.treetable.node.userobject.PageUserObject;
import jpdftwist.tabs.input.treetable.node.userobject.UserObject;


/**
 *
 * @author Vasilis Naskos
 */
public class Node extends AbstractMutableTreeTableNode implements KryoCopyable<Node> {

	private static SwapObserver observer;

	public Node() {
	}

	public Node(UserObject data) {
		this(data, true);
	}

	public Node(UserObject data, boolean allowChildren) {
		super(data);
		this.allowsChildren = allowChildren;
	}

	public final static void setObserver(SwapObserver observer) {
		Node.observer = observer;
	}

	public final static SwapObserver getObserver() {
		return observer;
	}

	
	public UserObject getUserObject() {
		return (UserObject) userObject;
	}

	
	public Object getValueAt(int i) {
		return ((UserObject) userObject).getValueAt(i);
	}

	
	public void setValueAt(Object aValue, int column) {
		((UserObject) userObject).setValueAt(aValue, column);
	}

	
	public int getColumnCount() {
		return UserObjectValue.values().length;
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
		TreeMap<Object, Node> nodeData = new TreeMap();

		for (int i = 0; i < childCount; i++) {
			Node child = (Node) this.getChildAt(i);
			if (child.getUserObject() instanceof PageUserObject) {
				return;
			}

			if (child.getChildCount() > 0 && recursive) {
				child.sortNode(sortColumn, sortAscending, recursive);
			}
			UserObject u = (UserObject) child.getUserObject();
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

	public int getFirstLevelChildrenCount() {
		if (!allowsChildren) {
			return 0;
		}

		return children.size();
	}

	
	public Node copy(Kryo kryo) {
		UserObject uo = (UserObject) kryo.copy(userObject);

		Node n = new Node(uo);

		return n;
	}
}
