package jpdftwist.gui.component.table;

import javax.swing.table.AbstractTableModel;

import jpdftwist.core.OutputPdfProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableComponentModel extends AbstractTableModel {

    List<Object[]> rows = new ArrayList<>();
    private final String[] columnNames;
    private final Class[] columnClasses;
    private RowListener listener;

    public TableComponentModel(String[] columnNames, Class[] columnClasses) {
        this.columnNames = columnNames;

        if (columnClasses == null) {
            this.columnClasses = new Class[columnNames.length];
            Arrays.fill(this.columnClasses, Object.class);
        } else {
            this.columnClasses = columnClasses;
        }

        if (this.columnClasses.length != columnNames.length) {
            Logger.getLogger(TableComponentModel.class.getName()).log(Level.SEVERE, "Ex128");
            throw new IllegalArgumentException();
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex)[columnIndex];
    }


    public String getColumnName(int column) {
        return columnNames[column];
    }


    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public void clear() {
        if (getRowCount() > 0) {
            fireTableRowsDeleted(0, getRowCount() - 1);
        }
        rows.clear();
    }

    public void addRow(Object[] params) {
        Object[] r = new Object[params.length];
        System.arraycopy(params, 0, r, 0, params.length);
        rows.add(r);
        fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    public void deleteRow(int row) {
        fireTableRowsDeleted(row, row);
        rows.remove(row);
    }

    public void moveRow(int row, int offset) {
        fireTableRowsDeleted(row, row);
        Object[] r = rows.remove(row);
        rows.add(row + offset, r);
        fireTableRowsInserted(row + offset, row + offset);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        rows.get(rowIndex)[columnIndex] = aValue;
        if (listener != null)
            listener.rowChanged(rowIndex, columnIndex);
    }

    public Object[] getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    public void setRowListener(RowListener listener) {
        this.listener = listener;
    }

    public interface RowListener {
        /**
         * Invoked when a value in a row changed.
         */
        void rowChanged(int rowIndex, int columnIndex);
    }
}
