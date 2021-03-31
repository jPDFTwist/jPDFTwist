package jpdftweak.tabs.watermark;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 *
 * @author Vasilis Naskos
 */
public class TextRenderer {

	public enum TextAlignment {
		TOP_LEFT, TOP, TOP_RIGHT, MIDDLE_LEFT, MIDDLE, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT, TOP_JUSTIFY,
		JUSTIFY, BOTTOM_JUSTIFY;

		public boolean isMiddle() {
			return this == MIDDLE || this == MIDDLE_LEFT || this == MIDDLE_RIGHT || this == JUSTIFY;
		}

		public boolean isBottom() {
			return this == BOTTOM || this == BOTTOM_LEFT || this == BOTTOM_RIGHT || this == BOTTOM_JUSTIFY;
		}

		public boolean isRight() {
			return this == TOP_RIGHT || this == MIDDLE_RIGHT || this == BOTTOM_RIGHT;
		}

		public boolean isCenter() {
			return this == TOP || this == MIDDLE || this == BOTTOM;
		}
	};

	public static Rectangle drawString(Graphics g, String text, Font font, Color color, Rectangle bounds,
			TextAlignment align, boolean underline, boolean strikethrough, boolean draw) {
		if (g == null) {
			throw new NullPointerException("The graphics handle cannot be null.");
		}
		if (text == null) {
			throw new NullPointerException("The text cannot be null.");
		}
		if (font == null) {
			throw new NullPointerException("The font cannot be null.");
		}
		if (color == null) {
			throw new NullPointerException("The text color cannot be null.");
		}
		if (bounds == null) {
			throw new NullPointerException("The text bounds cannot be null.");
		}

		if (text.length() == 0) {
			return new Rectangle(bounds.x, bounds.y, 0, 0);
		}

		Graphics2D g2D = (Graphics2D) g;

		AttributedString attributedString = new AttributedString(text);
		attributedString.addAttribute(TextAttribute.FOREGROUND, color);
		attributedString.addAttribute(TextAttribute.FONT, font);
		if (underline) {
			attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		if (strikethrough) {
			attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		}
		AttributedCharacterIterator attributedCharIterator = attributedString.getIterator();

		FontRenderContext fontContext = new FontRenderContext(null, true, false);
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(attributedCharIterator, fontContext);

		Point targetLocation = new Point(bounds.x, bounds.y);
		int nextOffset;

		if (align.isMiddle() || align.isBottom()) {
			if (align.isMiddle()) {
				targetLocation.y = bounds.y + (bounds.height / 2);
			}
			if (align.isBottom()) {
				targetLocation.y = bounds.y + bounds.height;
			}

			while (lineMeasurer.getPosition() < text.length()) {
				nextOffset = lineMeasurer.nextOffset(bounds.width);
				nextOffset = nextTextIndex(nextOffset, lineMeasurer.getPosition(), text);

				TextLayout textLayout = lineMeasurer.nextLayout(bounds.width, nextOffset, false);

				if (align.isMiddle()) {
					targetLocation.y -= (textLayout.getAscent() + textLayout.getLeading() + textLayout.getDescent())
							/ 2;
				}
				if (align.isBottom()) {
					targetLocation.y -= (textLayout.getAscent() + textLayout.getLeading() + textLayout.getDescent());
				}
			}

			lineMeasurer.setPosition(0);
		}

		if (align.isRight() || align.isCenter()) {
			targetLocation.x = bounds.x + bounds.width;
		}
		Rectangle consumedBounds = new Rectangle(targetLocation.x, targetLocation.y, 0, 0);

		while (lineMeasurer.getPosition() < text.length()) {
			nextOffset = lineMeasurer.nextOffset(bounds.width);
			nextOffset = nextTextIndex(nextOffset, lineMeasurer.getPosition(), text);

			TextLayout textLayout = lineMeasurer.nextLayout(bounds.width, nextOffset, false);
			Rectangle2D textBounds = textLayout.getBounds();

			targetLocation.y += textLayout.getAscent();
			consumedBounds.width = Math.max(consumedBounds.width, (int) textBounds.getWidth());

			switch (align) {
			case TOP_LEFT:
			case MIDDLE_LEFT:
			case BOTTOM_LEFT:
				if (draw)
					textLayout.draw(g2D, targetLocation.x, targetLocation.y);
				break;

			case TOP:
			case MIDDLE:
			case BOTTOM:
				targetLocation.x = bounds.x + (bounds.width / 2) - (int) (textBounds.getWidth() / 2);
				consumedBounds.x = Math.min(consumedBounds.x, targetLocation.x);
				if (draw)
					textLayout.draw(g2D, targetLocation.x, targetLocation.y);
				break;

			case TOP_RIGHT:
			case MIDDLE_RIGHT:
			case BOTTOM_RIGHT:
				targetLocation.x = bounds.x + bounds.width - (int) textBounds.getWidth();
				if (draw)
					textLayout.draw(g2D, targetLocation.x, targetLocation.y);
				consumedBounds.x = Math.min(consumedBounds.x, targetLocation.x);
				break;
			case TOP_JUSTIFY:
			case JUSTIFY:
			case BOTTOM_JUSTIFY:
				TextLayout justifyLayout = textLayout.getJustifiedLayout(bounds.width);
				if (draw)
					justifyLayout.draw(g2D, targetLocation.x, targetLocation.y);
				break;
			}
			targetLocation.y += textLayout.getLeading() + textLayout.getDescent();
		}

		consumedBounds.height = targetLocation.y - consumedBounds.y;

		return consumedBounds;
	}

	private static int nextTextIndex(int nextOffset, int measurerPosition, String text) {
		for (int i = measurerPosition + 1; i < nextOffset; ++i) {
			if (text.charAt(i) == '\n') {
				return i;
			}
		}

		return nextOffset;
	}

}
