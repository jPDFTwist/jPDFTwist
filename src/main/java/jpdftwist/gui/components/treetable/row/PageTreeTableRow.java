package jpdftwist.gui.components.treetable.row;

import jpdftwist.gui.components.treetable.TreeTableColumn;
import jpdftwist.gui.components.treetable.TreeTableRow;
import jpdftwist.gui.components.treetable.TreeTableRowType;

import java.awt.*;
import java.text.DecimalFormat;

public class PageTreeTableRow extends TreeTableRow {
    private double width;
    private double height;
    private String orientation;
    private String colorDepth;
    private int backgroundColor;
    private String id;

    public PageTreeTableRow() {
        super(null, TreeTableRowType.PAGE);
    }

    public PageTreeTableRow(final String key) {
        super(key, TreeTableRowType.PAGE);
    }

    public Object getValueAt(final int column) {
        final TreeTableColumn headerValue = TreeTableColumn.fromIndex(column);
        switch (headerValue) {
            case ID:
                return this.id;
            case FILE:
                return "Page " + this.getFileName();
            case PAPER_SIZE:
                return getPaperSize(this.width, this.height);
            case ORIENTATION:
                return this.orientation;
            case COLOR_DEPTH:
                return this.colorDepth;
            default:
                return "";
        }
    }

    public void setValueAt(final Object value, final int column) {
        final TreeTableColumn headerValue = TreeTableColumn.fromIndex(column);
        this.setValueAt(value, headerValue);
    }

    public void setValueAt(final Object value, final TreeTableColumn column) {
        final PageID C = new PageID();
        switch (column) {
            case ORIENTATION:
                this.orientation = String.valueOf(value);
                this.id = C.getStringAsId();
                break;
            case COLOR_DEPTH:
                this.colorDepth = String.valueOf(value);
                break;
        }
    }

    public void setWidth(final double width) {
        this.width = width;
    }

    public double getWidth() {
        return this.width;
    }

    public void setHeight(final double height) {
        this.height = height;
    }

    public double getHeight() {
        return this.height;
    }

    public void setBackgroundColor(final int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return new Color(this.backgroundColor, false);
    }

    private static String getPaperSize(final double width, final double height) {
        return getPageInchDimensionFormatted(width) + " x " + getPageInchDimensionFormatted(height) + " inch";
    }

    private static String getPageInchDimensionFormatted(final double value) {
        final DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        final double inches = value / 72.0;
        return df.format(inches);
    }
}
