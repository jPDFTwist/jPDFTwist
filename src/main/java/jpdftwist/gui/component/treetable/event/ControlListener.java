package jpdftwist.gui.component.treetable.event;

import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.TreeTableComponent;

public interface ControlListener {

    void doubleClick(Node selectedNode);

    void onSelectNode(Node selectedNode);

    void onClear();

    void beforeDelete();

    void onExport(TreeTableComponent treeTable);

    void onSave(TreeTableComponent treeTable);

    void onLoad(TreeTableComponent treeTable);
}
