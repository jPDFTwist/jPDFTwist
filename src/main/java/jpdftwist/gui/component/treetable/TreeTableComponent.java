package jpdftwist.gui.component.treetable;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import jpdftwist.gui.component.treetable.event.ControlListener;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreeTableComponent extends JPanel {
    private final JScrollPane scrollPane;
    private final TreeTableModel model;
    private JXTreeTable treeTable;
    private final JButton expandBtn;
    private final JButton upBtn;
    private final JButton downBtn;
    private final JButton deleteBtn;
    private final JButton orderBtn;
    private final JButton colorBtn;
    private final JButton exportList;
    private final JButton saveList;
    private final JButton openList;
    private boolean expandCollapse;
    private boolean ascendingOrder;
    TreeTableExpansionState expansionState;
    private final CellConstraints CC;
    private final ControlListener controlListener;

    public TreeTableComponent(final String[] headers, final Class<?>[] classes, final ControlListener controlListener) {
        this.CC = new CellConstraints();
        this.expandCollapse = false;
        this.ascendingOrder = true;
        this.controlListener = controlListener;
        if (headers.length != classes.length) {
            throw new IllegalArgumentException();
        }

        setLayout(new FormLayout(
            new ColumnSpec[]{
                ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
                ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
                ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
            },
            new RowSpec[]{RowSpec.decode("fill:pref:grow"), RowSpec.decode("fill:pref"),}));
        this.model = new TreeTableModel(headers, classes);
        this.initTreeTable();
        this.expansionState = new TreeTableExpansionState();
        this.scrollPane = new JScrollPane(this.treeTable);
        this.expandBtn = new JButton("Expand/Collapse");
        this.upBtn = new JButton("Up");
        this.downBtn = new JButton("Down");
        this.deleteBtn = new JButton("Delete");
        this.orderBtn = new JButton("Alphabetical order");
        this.colorBtn = new JButton("Colors");
        this.exportList = new JButton("ExportList");
        this.saveList = new JButton("Save");
        this.openList = new JButton("Load");
        this.createUI();
    }

    public JXTreeTable getTreeTable() {
        return this.treeTable;
    }


    private void initTreeTable() {
        this.treeTable = new JXTreeTable(this.model);

        this.treeTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        this.treeTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        this.treeTable.getColumnModel().getColumn(9).setPreferredWidth(30);
        this.treeTable.getColumnModel().getColumn(10).setPreferredWidth(30);
        this.treeTable.getColumnModel().getColumn(9)
            .setCellRenderer(new ConditionalCheckBoxRenderer());
        this.treeTable.getColumnModel().getColumn(10)
            .setCellRenderer(new ConditionalCheckBoxRenderer());
        this.treeTable.setRootVisible(false);
        this.treeTable.setShowGrid(true);
        this.treeTable.setColumnControlVisible(true);
        this.treeTable.setSortable(true);
        this.treeTable.setSortOrder(0, SortOrder.DESCENDING);
        final TreeTableRenderer treeCellRenderer = new TreeTableRenderer();
        this.treeTable.setTreeCellRenderer(treeCellRenderer);

        this.treeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent evt) {
                treeTableMouseListenerAction(evt);
            }
        });

        this.treeTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                int keyCode = evt.getKeyCode();

                if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_PAGE_DOWN || keyCode == KeyEvent.VK_PAGE_UP) {
                    treeTableKeyListenerAction(evt);
                }
            }
        });

        this.treeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controlListener.onSelectNode(getSelected());
            }
        });

        final BorderHighlighter topHighlighter = new BorderHighlighter((arg0, arg1) -> true, BorderFactory.createMatteBorder(0, 0, 1, 1, Color.DARK_GRAY));
        final ColorHighlighter colorHighlighter = new ColorHighlighter((arg0, arg1) -> !(arg0 instanceof JTree), new Color(243, 242, 241), Color.BLACK);
        this.treeTable.addHighlighter(topHighlighter);
        this.treeTable.addHighlighter(colorHighlighter);
    }

    private void createUI() {
        try {
            //Create Tree List
            this.scrollPane.setPreferredSize(new Dimension(750, 400));
            this.add(this.scrollPane, CC.xyw(1, 1, 9));
            this.add(this.expandBtn, CC.xy(1, 2));
            this.expandBtn.addActionListener(e -> expandButtonListenerAction());
            this.add(this.upBtn, CC.xy(2, 2));
            this.upBtn.addActionListener(e -> upButtonListenerAction());
            this.add(this.downBtn, CC.xy(3, 2));
            this.downBtn.addActionListener(e -> downButtonListenerAction());
            this.add(this.deleteBtn, CC.xy(4, 2));
            this.deleteBtn.addActionListener(e -> {
                try {
                    deleteButtonListenerAction();
                } catch (Exception ex) {
                    Logger.getLogger(TreeTableComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            this.add(this.orderBtn, CC.xy(5, 2));
            this.orderBtn.addActionListener(e -> sortButtonListenerAction());
            this.add(this.colorBtn, CC.xy(6, 2));
            this.colorBtn.addActionListener(e -> colorButtonListenerAction());

            this.add(this.exportList, CC.xy(7, 2));
            this.exportList.addActionListener(e -> controlListener.onExport(this));

            this.add(this.saveList, CC.xy(8, 2));
            this.saveList.addActionListener(e -> controlListener.onSave(this));

            this.add(this.openList, CC.xy(9, 2));
            this.openList.addActionListener(e -> controlListener.onLoad(this));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void treeTableMouseListenerAction(final MouseEvent evt) {
        if (evt.getClickCount() != 2) {
            return;
        }
        final int row = this.treeTable.getSelectedRow();
        final TreePath path = this.treeTable.getPathForRow(row);

        if (path == null) {
            return;
        }
        final Node node = (Node) path.getLastPathComponent();
        if ((evt.getModifiers() & InputEvent.BUTTON1_DOWN_MASK) != 0) {
            return;
        }

        controlListener.doubleClick(node);
    }

    private void treeTableKeyListenerAction(final KeyEvent evt) {
        int row = -1;

        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            row = this.treeTable.getSelectedRow() + 1;
        }

        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            row = this.treeTable.getSelectedRow() - 1;
        }
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            row = this.treeTable.getSelectedRow() + 31;
        }

        if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            row = this.treeTable.getSelectedRow() - 31;
        }
        final TreePath path = this.treeTable.getPathForRow(row);

        if (path == null) {
            return;
        }
        final Node node = (Node) path.getLastPathComponent();
        controlListener.onSelectNode(node);
    }

    private void expandButtonListenerAction() {
        if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
            return;
        }
        if (this.expandCollapse) {
            this.treeTable.collapseAll();
        } else {
            this.treeTable.expandAll();
        }
        this.expandCollapse = !this.expandCollapse;
    }

    private void upButtonListenerAction() {
        this.expansionState.store(this.treeTable);
        final ArrayList<TreePath> newPaths = new ArrayList<>();
        for (int selectedRowCount = this.treeTable.getSelectedRowCount(), i = 0; i < selectedRowCount; ++i) {
            final int row = this.treeTable.getSelectedRows()[0];
            if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
                return;
            }
            final TreePath path = this.treeTable.getPathForRow(row);
            final TreePath newPath = this.model.moveRow(path, -1);
            newPaths.add(newPath);
        }
        this.expansionState.restore(this.treeTable);
        final Runnable setSelectionRunnable = () -> {
            final TreeSelectionModel tsm = TreeTableComponent.this.treeTable.getTreeSelectionModel();
            tsm.setSelectionPaths(newPaths.toArray(new TreePath[0]));
        };
        SwingUtilities.invokeLater(setSelectionRunnable);
    }

    private void downButtonListenerAction() {
        this.expansionState.store(this.treeTable);
        final ArrayList<TreePath> newPaths = new ArrayList<>();
        for (int i = this.treeTable.getSelectedRowCount() - 1; i >= 0; --i) {
            final int row = this.treeTable.getSelectedRows()[i];
            if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
                return;
            }
            final TreePath path = this.treeTable.getPathForRow(row);
            final TreePath newPath = this.model.moveRow(path, 1);
            newPaths.add(newPath);
        }
        this.expansionState.restore(this.treeTable);
        final Runnable setSelectionRunnable = () -> {
            final TreeSelectionModel tsm = TreeTableComponent.this.treeTable.getTreeSelectionModel();
            tsm.setSelectionPaths(newPaths.toArray(new TreePath[0]));
        };
        SwingUtilities.invokeLater(setSelectionRunnable);
    }

    private void deleteButtonListenerAction() {
        controlListener.beforeDelete();
        for (int i = this.treeTable.getSelectedRowCount() - 1; i >= 0
            && this.treeTable.getSelectedRowCount() != 0; --i) {
            final int row = this.treeTable.getSelectedRows()[i];
            if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
                return;
            }
            final TreePath path = this.treeTable.getPathForRow(row);
            this.model.removeNodeFromParent((Node) path.getLastPathComponent());
        }
    }

    private void sortButtonListenerAction() {
        this.expansionState.store(this.treeTable);
        final Node parent = this.model.getRoot();
        parent.sortNode(0, this.ascendingOrder, true);
        this.ascendingOrder = !this.ascendingOrder;
        this.expansionState.restore(this.treeTable);
    }

    private void colorButtonListenerAction() {
        final TreeTableColorPickerDialog colors = new TreeTableColorPickerDialog();
        colors.setVisible(true);
    }

    public TreeTableModel getModel() {
        return this.model;
    }

    public void clear() {
        controlListener.onClear();
        this.model.clear();
    }

    public Node getSelected() {
        final int row = this.treeTable.getSelectedRow();
        final TreePath path = this.treeTable.getPathForRow(row);
        if (path == null) {
            return null;
        }

        return (Node) path.getLastPathComponent();
    }

    public void updateTreeTableUI() {
        SwingUtilities.invokeLater(() -> TreeTableComponent.this.treeTable.updateUI());
    }
}

