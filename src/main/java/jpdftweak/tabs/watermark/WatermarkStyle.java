/**
 * Original Functions		@author Michael Schierl					Affero GNU Public License
 * Additional Functions		@author & @sponsor: E.Victor			Proprietary for in-house use only / Not released to the Public
 */
package jpdftweak.tabs.watermark;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @ 
 */
public class WatermarkStyle {

	public enum WatermarkType {
		NUMBERS(0), LATIN_CAPITAL(1), LATIN_LOWERCASE(2), CAPITAL_LETTERS(3), LOWERCASE_LETTERS(4), BATES_NUMBERING(5),
		REPEATED_TEXT(6), IMAGE(7), VARIABLE_TEXT(8), EMPTY(9);

		private final int mask;

		private WatermarkType(int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}

		public static String toString(WatermarkType type) {
			String value = "";

			switch (type) {
			case NUMBERS:
				value = "1, 2, 3";
				break;
			case LATIN_CAPITAL:
				value = "I, II, III";
				break;
			case LATIN_LOWERCASE:
				value = "i, ii, iii";
				break;
			case CAPITAL_LETTERS:
				value = "A, B, C";
				break;
			case LOWERCASE_LETTERS:
				value = "a, b, c";
				break;
			case BATES_NUMBERING:
				value = "Bates Numbering";
				break;
			case REPEATED_TEXT:
				value = "Repeated Text";
				break;
			case IMAGE:
				value = "Image";
				break;
			case VARIABLE_TEXT:
				value = "Variable Text";
				break;
			case EMPTY:
				value = "Empty";
				break;
			}

			return value;
		}
	}

	public enum HorizontalAlign {
		LEFT, CENTER, RIGHT, JUSTIFY
	}

	public enum VerticalAlign {
		TOP, MIDDLE, BOTTOM
	}

	public enum Units {
		POINTS, INCHES, MM
	}

	private WatermarkType type = WatermarkType.NUMBERS;

	private Font font = new Font("Arial", Font.PLAIN, 12);
	private int fontSize = 12;
	private int fontStyle = 0;

	private Color fontColor = Color.RED;

	private boolean underline = false;
	private boolean strikethrough = false;
	private boolean background = false;
	private Color backgroundColor = Color.GREEN;

	private int angle = 0;

	private float width = 0;
	private float height = 0;

	private int opacity = 0;

	private HorizontalAlign hAlign = HorizontalAlign.LEFT;
	private VerticalAlign vAlign = VerticalAlign.TOP;

	private float horizontalPosition = 0;
	private float verticalPosition = 0;
	private int horizontalReference = 0;
	private int verticalReference = 0;

	private String repeatedText = "";

	private String variableTextFile = "";

	private int batesStartWith = 1;
	private int batesStep = 1;
	private int batesRepeatFor = 1;
	private int batesZeroPadding = 0;
	private String batesPrefix = "";
	private String batesSuffix = "";
	private int batesApplyTo = 0;
	private String batesPages = "";

	private int startPage = 1;
	private int logicalPage = 1;
	private String prefix = "";

	private String imagePath = "";
	private int pdfPage = 0;

	private Units units = Units.POINTS;

	public WatermarkType getType() {
		return type;
	}

	public void setType(int type) {
		WatermarkType[] values = WatermarkType.values();

		this.type = values[type];
	}

	public void setType(WatermarkType type) {
		this.type = type;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	public boolean isStrikethrough() {
		return strikethrough;
	}

	public void setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
	}

	public boolean isBackground() {
		return background;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public int getOpacity() {
		return opacity;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public HorizontalAlign getHorizontalAlign() {
		return hAlign;
	}

	public void setHorizontalAlign(HorizontalAlign align) {
		this.hAlign = align;
	}

	public VerticalAlign getVerticalAlign() {
		return vAlign;
	}

	public void setVerticalAlign(VerticalAlign align) {
		this.vAlign = align;
	}

	public float getHorizontalPosition() {
		return horizontalPosition;
	}

	public void setHorizontalPosition(float horizontalPosition) {
		this.horizontalPosition = horizontalPosition;
	}

	public float getVerticalPosition() {
		return verticalPosition;
	}

	public void setVerticalPosition(float verticalPosition) {
		this.verticalPosition = verticalPosition;
	}

	public int getHorizontalReference() {
		return horizontalReference;
	}

	public void setHorizontalReference(int horizontalReference) {
		this.horizontalReference = horizontalReference;
	}

	public int getVerticalReference() {
		return verticalReference;
	}

	public void setVerticalReference(int verticalReference) {
		this.verticalReference = verticalReference;
	}

	public String getRepeatedText() {
		return repeatedText;
	}

	public void setRepeatedText(String repeatedText) {
		this.repeatedText = repeatedText;
	}

	public String getVariableTextFile() {
		return variableTextFile;
	}

	public void setVariableTextFile(String variableTextFile) {
		this.variableTextFile = variableTextFile;
	}

	public int getBatesStartWith() {
		return batesStartWith;
	}

	public void setBatesStartWith(int batesStartWith) {
		this.batesStartWith = batesStartWith;
	}

	public int getBatesStep() {
		return batesStep;
	}

	public void setBatesStep(int batesStep) {
		this.batesStep = batesStep;
	}

	public int getBatesRepeatFor() {
		return batesRepeatFor;
	}

	public void setBatesRepeatFor(int batesRepeatFor) {
		this.batesRepeatFor = batesRepeatFor;
	}

	public int getBatesZeroPadding() {
		return batesZeroPadding;
	}

	public void setBatesZeroPadding(int batesZeroPadding) {
		this.batesZeroPadding = batesZeroPadding;
	}

	public String getBatesPrefix() {
		return batesPrefix;
	}

	public void setBatesPrefix(String batesPrefix) {
		this.batesPrefix = batesPrefix;
	}

	public String getBatesSuffix() {
		return batesSuffix;
	}

	public void setBatesSuffix(String batesSuffix) {
		this.batesSuffix = batesSuffix;
	}

	public int getBatesApplyTo() {
		return batesApplyTo;
	}

	public void setBatesApplyTo(int batesApplyTo) {
		this.batesApplyTo = batesApplyTo;
	}

	public String getBatesPages() {
		return batesPages;
	}

	public List<Integer> getBatesPagesList() {
		if (batesApplyTo < 5) {
			return Collections.EMPTY_LIST;
		}
		List<Integer> batesPagesList = new ArrayList<Integer>();
		String pagesStr = batesPages;
		String[] pagesArray = pagesStr.split(",");
		for (String str : pagesArray) {
			try {
				int val = Integer.parseInt(str);
				batesPagesList.add(val);
			} catch (Exception e) {
			}
		}

		return batesPagesList;
	}

	public void setBatesPages(String batesPages) {
		this.batesPages = batesPages;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getLogicalPage() {
		return logicalPage;
	}

	public void setLogicalPage(int logicalPage) {
		this.logicalPage = logicalPage;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getPdfPage() {
		return pdfPage;
	}

	public void setPdfPage(int pdfPage) {
		this.pdfPage = pdfPage;
	}

	public Units getUnits() {
		return units;
	}

	public void setUnits(Units units) {
		this.units = units;
	}

	
	public String toString() {
		return WatermarkType.toString(type);
	}
}
