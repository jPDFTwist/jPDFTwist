package jpdftweak.tabs.input.treetable.node.userobject;

import java.text.DecimalFormat;
import java.awt.Color;
import jpdftweak.utils.ConstantClass1;
import jpdftweak.tabs.input.treetable.UserObjectValue;

public class PageUserObject extends UserObject {
	private double width;
	private double height;
	private String orientation;
	private String colorDepth;
	private int backgroundColor;
	private String id;

	public PageUserObject() {
		super((String) null, UserObjectType.PAGE);
	}

	public PageUserObject(final String key) {
		super(key, UserObjectType.PAGE);
	}

	public Object getValueAt(final int column) {
		final UserObjectValue headerValue = UserObjectValue.fromInt(column);
		switch (headerValue) {
			case ID : {
				
				return this.id;
			}
			case FILE : {
				return "Page " + this.getFileName();
			}
			case PAPER_SIZE : {
				return getPaperSize(this.width, this.height);
			}
			case ORIENTATION : {
				return this.orientation;
			}
			case COLOR_DEPTH : {
				return this.colorDepth;
			}
			default : {
				return "";
			}
		}
	}

	public void setValueAt(final Object value, final int column) {
		final UserObjectValue headerValue = UserObjectValue.fromInt(column);
		this.setValueAt(value, headerValue);
	}

//	*** Controls "ID" Numbering ***
	
	public void setValueAt(final Object value, final UserObjectValue column) {
		final ConstantClass1 C = new ConstantClass1();
		switch (column) {
			case ORIENTATION : {
				this.orientation = String.valueOf(value);
				this.id = C.getStringAsId();
//				this.id = "";
				break;
			}
			case COLOR_DEPTH : {
				this.colorDepth = String.valueOf(value);
				break;
			}
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
		final StringBuilder paperSizeBuilder = new StringBuilder();
		paperSizeBuilder.append(getPageInchDimensionFormated(width));
		paperSizeBuilder.append(" x ");
		paperSizeBuilder.append(getPageInchDimensionFormated(height));
		paperSizeBuilder.append(" inch");
		return paperSizeBuilder.toString();
	}

	private static String getPageInchDimensionFormated(final double value) {
		final DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		final double inches = value / 72.0;
		return df.format(inches);
	}
}
