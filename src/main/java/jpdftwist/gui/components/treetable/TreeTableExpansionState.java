package jpdftwist.gui.components.treetable;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TreeTableExpansionState {

    private final List<TreePath> expandedPaths = new ArrayList<>();

    public void store(JXTreeTable treeTable) {
        expandedPaths.clear();

        Object rootNode = treeTable.getTreeTableModel().getRoot();
        Enumeration<?> expandedDescendants = treeTable.getExpandedDescendants(new TreePath(rootNode));
        if (expandedDescendants == null) {
            return;
        }

        while (expandedDescendants.hasMoreElements()) {
            TreePath np = (TreePath) expandedDescendants.nextElement();
            if (!(np.getLastPathComponent() == rootNode)) {
                expandedPaths.add(np);
            }
        }
    }

    public void restore(JXTreeTable treeTable) {
        treeTable.collapseAll();
        expandedPaths.forEach(treeTable::expandPath);
    }
}
