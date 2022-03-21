/**
 * Original Functions		@author Michael Schierl					Affero GNU Public License
 * Additional Functions		@author & @sponsor: E.Victor			Proprietary for in-house use only / Not released to the Public
 */
package jpdftweak.tabs.watermark;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @ 
 */
public class WatermarkPreviewBox extends JComponent implements MouseListener, MouseMotionListener, ComponentListener {

	private Image doubleBufferedImage;
	private Graphics dbg;

	private Rectangle windowBounds;

	private Image background;
	private static final Color DEFAULT_COLOR = Color.RED;
	private Color fontColor = DEFAULT_COLOR;
	private Color backgroundColor;

	private double radAngle = 0;
	private double angle = 0;
	private float alpha = 1F;

	private int width, height;

	private static final String STANDARD_TEXT = "<html><body>Watermark</body></html>";
	private String previewText = STANDARD_TEXT;

	private Rectangle rect;

	private int preX, preY;

	private boolean mouseReleased = false;

	private final JLabel renderer = new JLabel(STANDARD_TEXT);

	private final CellRendererPane crp = new CellRendererPane();
	private Dimension dim;

	private int rotationCenterX, rotationCenterY;

	private final Rectangle[] positions = new Rectangle[9];

	private int startX, startY;

	private int oldWidth, oldHeight, newWidth = 360, newHeight = 215, oldX, oldY;

	private boolean enabled = true;

	private PreviewModel defaultModel;

	private boolean underline = false;
	private boolean strikethrough = false;

	private WatermarkPreviewBox() {
		initialize();
	}

	private void initialize() {
		addMouseMotionListener(this);
		addMouseListener(this);
		addComponentListener(this);

		//        try {
		//            background = ImageIO.read(getClass().getResource("/background.png"));
		//        } catch (IOException ex) {
		//            Logger.getLogger(WatermarkPreviewBox.class.getName()).log(Level.SEVERE, null, ex);
		//        }

		//        int w = background.getWidth(this);
		//        int h = background.getHeight(this);

		//        this.setSize(w, h);
		//        this.setMinimumSize(new Dimension(w, h));

		dim = renderer.getPreferredSize();
		width = dim.width;
		height = dim.height;
		
		rect = new Rectangle(startX, startY, width, height);
		
		startX = (int) (this.getSize().width / 2.1 - width / 2);
		startY = (int) (this.getSize().height / 2.4 - height / 2);

		rect = new Rectangle(startX, startY, width, height);

		windowBounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());
		//        boundedRect = rect;
		renderer.setForeground(fontColor);

		rotationCenterX = rect.x + (dim.width / 2);
		rotationCenterY = rect.y + (dim.height / 2);

		renderer.setVerticalTextPosition(JLabel.CENTER);
		renderer.setHorizontalTextPosition(JLabel.CENTER);

	}

	public static WatermarkPreviewBox createPreviewBox() {
		final WatermarkPreviewBox preview = new WatermarkPreviewBox();

		PreviewModel model = new PreviewModel() {

			
			public void setColor(Color c) {
				preview.setColor(c);
			}

			
			public void setBackgroundColor(Color c) {
				preview.setBackgroundColor(c);
			}

			
			public void setAngle(int angle) {
				preview.setAngle(angle);
			}

			
			public void setOpacity(int opacity) {
				preview.setAlpha(opacity);
			}

			
			public void setFont(Font font) {
				preview.setFonts(font);
			}

			
			public void setUnderline(boolean underline) {
				preview.setUnderline(underline);
			}

			
			public void setStrikethrough(boolean strikethrough) {
				preview.setStrikethrough(strikethrough);
			}
		};

		preview.setDefaultModel(model);

		return preview;
	}

	
	public void paintComponent(Graphics g) {
		rotationCenterX = rect.x + (dim.width / 2);
		rotationCenterY = rect.y + (dim.height / 2);

		doubleBufferedImage = createImage(this.getSize().width, this.getSize().height);
		dbg = doubleBufferedImage.getGraphics();

		width = dim.width;
		height = dim.height;

		Graphics2D g2 = (Graphics2D) dbg;

		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		windowBounds.setSize(this.getWidth(), this.getHeight());
		g2.setColor(Color.WHITE);
		g2.fill(windowBounds);
		//        g2.drawImage(background, this.getWidth() / 2 - background.getWidth(this) / 2, this.getHeight() / 2 - background.getHeight(this) / 2, this);

		//        g2.setColor(Color.WHITE);
		int k = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				positions[k] = new Rectangle((this.getWidth() / 3) * x, (this.getHeight() / 3) * y, this.getWidth() / 3,
						this.getHeight() / 3);
				//                g2.draw(positions[k]);
				k++;
			}
		}

		g2.rotate(radAngle, rotationCenterX, rotationCenterY);
		g2.setComposite(makeComposite(alpha));

		if (backgroundColor != null) {
			g2.setColor(backgroundColor);
			width = dim.width;
			height = dim.height;
			rect.setSize(width, height);
			g2.fill(rect);
		}

		g2.setColor(fontColor);
		crp.paintComponent(g2, renderer, this, rect.x, rect.y, dim.width, dim.height);
		if (underline) {
			g2.drawLine(rect.x, rect.y + dim.height, rect.x + dim.width, rect.y + dim.height);
		}
		if (strikethrough) {
			g2.drawLine(rect.x, rect.y + (dim.height / 2), rect.x + dim.width, rect.y + (dim.height / 2));
		}

		width = dim.width;
		height = dim.height;
		rect.setSize(width, height);

		g2.rotate(-radAngle, rotationCenterX, rotationCenterY);

		g2.dispose();

		g.drawImage(doubleBufferedImage, 0, 0, this);

	}

	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

	private void checkRectangleCollision() {
		if (windowBounds.contains(rect)) {
			return;
		}

		if (rect.x + rect.width / 2 >= windowBounds.getWidth() + windowBounds.getX()) {
			setLimitLocations((int) ((windowBounds.getWidth() - rect.width / 2) + windowBounds.getX()), rect.y);
		}
		if (rect.x < windowBounds.getX() - rect.width / 2) {
			setLimitLocations((int) windowBounds.getX() - rect.width / 2, rect.y);
		}
		if (rect.y + rect.height / 2 > windowBounds.getHeight() + windowBounds.getY()) {
			setLimitLocations(rect.x, (int) ((windowBounds.getHeight() - rect.height / 2) + windowBounds.getY()));
		}
		if (rect.y < windowBounds.getY() - rect.height / 2) {
			setLimitLocations(rect.x, (int) windowBounds.getY() - rect.height / 2);
		}
	}

	private void setLimitLocations(int newX, int newY) {
		int centerX = (rect.width / 2) - (rect.width / 2);
		int centerY = (rect.height / 2) - (rect.height / 2);

		rect.setLocation(newX, newY);
		rect.setLocation(newX - centerX, newY - centerY);
	}

	
	public void mouseClicked(MouseEvent me) {
		if (!enabled) {
			return;
		}
		for (Rectangle position : positions) {
			if (position.contains(me.getPoint())) {
				rect.setLocation(((position.width / 2) + position.x) - rect.width / 2,
						((position.height / 2) + position.y) - rect.height / 2);
				checkRectangleCollision();
				repaint();
				return;
			}
		}
	}

	
	public void mousePressed(MouseEvent me) {
		preX = rect.x - me.getX();
		preY = rect.y - me.getY();

		if (rect.contains(me.getX(), me.getY())) {
			updateLocation(me);
		} else {
			mouseReleased = true;
		}
	}

	
	public void mouseReleased(MouseEvent me) {
		if (rect.contains(me.getX(), me.getY())) {
			updateLocation(me);
		} else {
			mouseReleased = false;
		}
	}

	
	public void mouseEntered(MouseEvent me) {
		if (!enabled) {
			return;
		}
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	
	public void mouseExited(MouseEvent me) {
		if (!enabled) {
			return;
		}
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	
	public void mouseDragged(MouseEvent me) {
		if (!mouseReleased) {
			updateLocation(me);
		}
	}

	
	public void mouseMoved(MouseEvent me) {
		if (!enabled) {
			return;
		}
		if (rect.contains(me.getPoint())) {// bounded
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	public void updateLocation(MouseEvent e) {
		if (!enabled) {
			return;
		}
		rect.setLocation(preX + e.getX(), preY + e.getY());
		checkRectangleCollision();

		repaint();
	}

	public double getTextX() {
		return rect.getX();
	}

	public double getTextY() {
		return rect.getY();
	}

	public void setText(String text) {
		previewText = text;
		renderer.setText(previewText);
		updateLimits();
		repaint();
	}

	public void setAlpha(int value) {
		alpha = (float) ((100 - value) * Math.pow(10, -2));
		repaint();
	}

	public float getAlpha() {
		return alpha;
	}

	public void setFonts(Font font) {
		renderer.setFont(font);
		updateLimits();
	}

	public void setColor(Color color) {
		fontColor = color;
		renderer.setForeground(color);
		repaint();
	}

	public void setBackgroundColor(Color color) {
		backgroundColor = color;
		renderer.setBackground(color);
		repaint();
	}

	public Color getColor() {
		return renderer.getForeground();
	}

	public void setAngle(int angle) {
		this.angle = angle;
		radAngle = (angle * Math.PI) / 180;
		updateLimits();
	}

	public double getAngle() {
		return angle;
	}

	private void updateLimits() {
		dim = renderer.getPreferredSize();
		checkRectangleCollision();
		repaint();
	}

	public void setImage(ImageIcon image) {
		renderer.setIcon(image);
		updateLimits();
	}

	public void setImageAlign(int value) {
		switch (value) {
		case 0:
			renderer.setVerticalTextPosition(JLabel.TOP);
			renderer.setHorizontalTextPosition(JLabel.LEFT);
			break;
		case 1:
			renderer.setVerticalTextPosition(JLabel.TOP);
			renderer.setHorizontalTextPosition(JLabel.RIGHT);
			break;
		case 2:
			renderer.setVerticalTextPosition(JLabel.CENTER);
			renderer.setHorizontalTextPosition(JLabel.LEFT);
			break;
		case 3:
			renderer.setVerticalTextPosition(JLabel.CENTER);
			renderer.setHorizontalTextPosition(JLabel.RIGHT);
			break;
		case 4:
			renderer.setVerticalTextPosition(JLabel.TOP);
			renderer.setHorizontalTextPosition(JLabel.CENTER);
			break;
		case 5:
			renderer.setVerticalTextPosition(JLabel.BOTTOM);
			renderer.setHorizontalTextPosition(JLabel.CENTER);
			break;
		case 6:
			renderer.setVerticalTextPosition(JLabel.BOTTOM);
			renderer.setHorizontalTextPosition(JLabel.LEFT);
			break;
		case 7:
			renderer.setVerticalTextPosition(JLabel.BOTTOM);
			renderer.setHorizontalTextPosition(JLabel.RIGHT);
			break;
		}
		updateLimits();
	}

	public int getLabelCenterX() {
		return rect.x + rect.width / 2;
	}

	public int getLabelCenterY() {
		return rect.y + rect.height / 2;
	}

	public int getLabelWidth() {
		return dim.width;
	}

	public int getLabelHeight() {
		return dim.height;
	}

	public String getText() {
		return renderer.getText();
	}

	
	public void componentResized(ComponentEvent ce) {
		oldWidth = newWidth;
		oldHeight = newHeight;
		newWidth = this.getWidth();
		newHeight = this.getHeight();
		oldX = rect.x + rect.width / 2;
		oldY = rect.y + rect.height / 2;
		rect.setLocation((int) (((double) newWidth / oldWidth) * oldX - rect.width / 2),
				(int) (((double) newHeight / oldHeight) * oldY - rect.height / 2));
		dim = renderer.getPreferredSize();
		repaint();
	}

	
	public void componentMoved(ComponentEvent ce) {
	}

	
	public void componentShown(ComponentEvent ce) {
	}

	
	public void componentHidden(ComponentEvent ce) {
	}

	
	public void setEnabled(boolean value) {
		enabled = value;
		super.setEnabled(value);
	}

	
	public Dimension getSize() {
		return new Dimension(360, 250);
	}

	
	public Dimension getPreferredSize() {
		return new Dimension(360, 250);
	}

	
	public Dimension getMinimumSize() {
		return new Dimension(360, 250);
	}

	public void setDefaultModel(PreviewModel defaultModel) {
		this.defaultModel = defaultModel;
	}

	public PreviewModel getDefaultModel() {
		return defaultModel;
	}

	public static interface PreviewModel {
		public void setColor(Color c);

		public void setBackgroundColor(Color c);

		public void setAngle(int angle);

		public void setOpacity(int opacity);

		public void setFont(Font font);

		public void setUnderline(boolean underline);

		public void setStrikethrough(boolean strikethrough);
	}

	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
		repaint();
	}

	public boolean isStrikethrough() {
		return strikethrough;
	}

	public void setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
		repaint();
	}

}
