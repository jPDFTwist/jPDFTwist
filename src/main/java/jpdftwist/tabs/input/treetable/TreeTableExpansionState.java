package jpdftwist.tabs.input.treetable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;

/**
 * Store/Restore the expanded paths in a JXTreeTable.
 *
 * @author jorgen rapp
 *
 */
public class TreeTableExpansionState extends AbstractExpansionState {

	protected List<TreePath> expandedPaths = new ArrayList<TreePath>();

	public TreeTableExpansionState(JXTreeTable aTreeTable) {
		super(aTreeTable);
	}

	
	public void store() {
		expandedPaths.clear();
		Enumeration expandedDescendants = ((JXTreeTable) getAssociatedComponent()).getExpandedDescendants(
				new TreePath(((JXTreeTable) getAssociatedComponent()).getTreeTableModel().getRoot()));
		if (expandedDescendants != null) {
			while (expandedDescendants.hasMoreElements()) {
				Object nex = expandedDescendants.nextElement();
				TreePath np = (TreePath) nex;
				if (!(np.getLastPathComponent() == ((JXTreeTable) getAssociatedComponent()).getTreeTableModel()
						.getRoot())) {
					expandedPaths.add(np);
				}
			}
		}
	}

	
	public void restore() {
		JXTreeTable treeTable = (JXTreeTable) getAssociatedComponent();
		treeTable.collapseAll();
		for (TreePath path : expandedPaths) {

			treeTable.expandPath(path);
		}
	}

	/**
	 * public void savePaths() {
	 *
	 * JXTreeTable treeTable = (JXTreeTable) getAssociatedComponent(); // for
	 * (TreePath path : expandedPaths) for(int i=0;i<expandedPaths.size();i++) {
	 * //System.out.println("in savepaths.."); //
	 * System.out.println("paths:"+path.getLastPathComponent()); final Node node =
	 * (Node) expandedPaths.get(i).getLastPathComponent(); if (node.getUserObject()
	 * instanceof FileUserObject) { final FileUserObject userObject =
	 * (FileUserObject) node.getUserObject();
	 * System.out.println("path:"+userObject.getKey());
	 *
	 * } } }
	 **/
}
