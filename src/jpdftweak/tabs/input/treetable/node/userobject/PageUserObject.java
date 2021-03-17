package jpdftweak.tabs.input.treetable.node.userobject;

import java.awt.Color;
import java.text.DecimalFormat;
import jpdftweak.core.UnitTranslator;
import jpdftweak.tabs.input.treetable.UserObjectValue;

/**
 *
 * @author Vasilis Naskos
 */
public class PageUserObject extends UserObject {
    
    private double width, height; //Postscript points
    private String orientation;
    private String colorDepth;
    private int backgroundColor;

    public PageUserObject() {
        super(null, UserObjectType.PAGE);
    }
    
    public PageUserObject(String key) {
        super(key, UserObjectType.PAGE);
    }
    
    @Override
    public Object getValueAt(int column) {
        UserObjectValue headerValue = UserObjectValue.fromInt(column);

        switch (headerValue) {
            case FILE:
                return "Page " + getFileName();
            case PAPER_SIZE:
                return getPaperSize(width, height);
            case ORIENTATION:
                return orientation;
            case COLOR_DEPTH:
                return colorDepth;
            default:
                return "";
        }
    }
    
    @Override
    public void setValueAt(Object value, int column) {
        UserObjectValue headerValue = UserObjectValue.fromInt(column);
        
        setValueAt(value, headerValue);
    }
    
    @Override
    public void setValueAt(Object value, UserObjectValue column) {
        switch(column) {
            case ORIENTATION:
                orientation = String.valueOf(value);
                break;
            case COLOR_DEPTH:
                colorDepth = String.valueOf(value);
                break;
        }
    }

    public void setWidth(double width) {
        this.width = width;
    }
    
    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public Color getBackgroundColor() {
        return new Color(backgroundColor, false);
    }
    
    private static String getPaperSize(double width, double height) {
        StringBuilder paperSizeBuilder = new StringBuilder();

        paperSizeBuilder.append(getPageInchDimensionFormated(width));
        paperSizeBuilder.append(" x ");
        paperSizeBuilder.append(getPageInchDimensionFormated(height));
        paperSizeBuilder.append(" inch");

        return paperSizeBuilder.toString();
    }

    private static String getPageInchDimensionFormated(double value) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        double inches = value / UnitTranslator.POINT_POSTSCRIPT;

        return df.format(inches);
    }
}
