package jpdftweak.gui;

import com.sun.pdfview.PDFPage;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Preview extends JPanel {

	public static int INCH = 72;
	public static final int SIZE = 45;
	private int height;
	private static DefaultListModel listModel = new DefaultListModel();
	private static DefaultListModel model;
	private JList<JLabel> list;
	private Dimension dim;
	private JScrollPane jsp;
	private int increment;
	private static int units;
	private JButton zoomOut;
	private JButton zoomIn;
	private JButton reset;
	private JComboBox<String> c;
	private static int hght;
	private static int wdth;
	private static int originalHeight;
	private static int originalWidth;
	private static PDFPage newPage;
	private ImageIcon image;
	private static double prevzoom;
	private static double zoom;
	private static boolean isPercent;
	private static int orgrwidth;
	private static int orgheight;
	private static Rectangle rect;
	private static Image zoomImage;

	public Preview() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	}

	public Preview(Dimension d, Image img, int rwidth, int rheight, PDFPage page) {
		super(new BorderLayout());
		orgrwidth = rwidth;
		orgheight = rheight;
		newPage = page;

		prevzoom = 0.0;
		zoom = 1.0;
		isPercent = false;
		clearPreview();

		image = new ImageIcon(img);
		zoomOut = new JButton("-");
		zoomOut.setToolTipText("Zoom out");

		zoomIn = new JButton("+");
		zoomIn.setToolTipText("Zoom in");

		reset = new JButton("Fit to Window");
		reset.setToolTipText("Default preview");

		c = new JComboBox<>();
		c.addItem("1%");
		c.addItem("2%");
		c.addItem("3%");
		c.addItem("4%");
		c.addItem("5%");
		c.addItem("10%");
		c.addItem("20%");
		c.addItem("25%");
		c.addItem("50%");
		c.addItem("75%");
		c.addItem("100%");

		c.setEditable(true);
		c.setToolTipText("Zoom");

		wdth = image.getIconWidth();
		hght = image.getIconHeight();

		originalWidth = wdth;
		originalHeight = hght;

		if (originalWidth == orgrwidth) {
			String value = "100%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		if (originalWidth * 2 == orgrwidth) {
			String value = "50%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		if (originalWidth * 4 == orgrwidth) {
			String value = "25%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		if (orgrwidth > 1440) {
			String value = "10%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		if (orgrwidth > 3600) {
			String value = "5%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		if (orgrwidth > 6400) {
			String value = "4%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		if (orgrwidth > 14400) {
			String value = "2%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		if (orgrwidth > 36000) {
			String value = "1%";
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (c.getItemAt(i).equals(value)) {
					c.setSelectedIndex(i);
					break;
				}
			}
		}

		dim = d;
		height = d.height;

		JLabel label = new JLabel(image);

		listModel.addElement(label);
		list = new JList<>(listModel);

		MyRenderer myRenderer = new MyRenderer();
		list.setCellRenderer(myRenderer);
		list.setBackground(Color.gray);
		jsp = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		createRuler(jsp, label);

		jsp.setBackground(Color.gray);
		jsp.getVerticalScrollBar().setUnitIncrement(8);
		jsp.setPreferredSize(new Dimension((int) label.getPreferredSize().getWidth() / 10,
				(int) label.getPreferredSize().getHeight() / 10));
		jsp.setViewportBorder(BorderFactory.createLineBorder(Color.black));
		add(jsp, BorderLayout.CENTER);
		JPanel btnpanel = new JPanel();
		btnpanel.setLayout(new FlowLayout());
		btnpanel.add(zoomIn);
		btnpanel.add(zoomOut);
		btnpanel.add(c);
		btnpanel.add(reset);
		add(btnpanel, BorderLayout.PAGE_END);

		this.zoomIn.addActionListener(e -> {
			DefaultListModel model = new DefaultListModel();
			model.addElement(listModel.get(0));
			clearPreview();

			if (model.getSize() > 0) {
				JLabel newLabel = (JLabel) model.get(0);
				Icon icon = newLabel.getIcon();

				if (icon instanceof ImageIcon) {
					setIsPercent(false);
					zoom(1.5, rwidth, rheight, newPage, isIsPercent());
					setZoom(1.5);
				}
			}
		});

		this.zoomOut.addActionListener(e -> {
			DefaultListModel model = new DefaultListModel();
			model.addElement(listModel.get(0));
			clearPreview();

			if (model.getSize() > 0) {
				JLabel newlabel = (JLabel) model.get(0);

				Icon icon = newlabel.getIcon();

				if (icon instanceof ImageIcon) {
					setIsPercent(false);
					zoom(0.5, rwidth, rheight, newPage, isIsPercent());
					setZoom(0.5);

				}
			}
		});

		this.c.addActionListener(e -> {
			model = new DefaultListModel();
			model.addElement(listModel.get(0));
			clearPreview();

			if (model.getSize() > 0) {
				JLabel newlabel = (JLabel) model.get(0);
				Icon icon = newlabel.getIcon();

				if (icon instanceof ImageIcon) {
					String zoomvalue = (String) c.getSelectedItem();
					String trimmedString;
					int l = zoomvalue.indexOf("%");
					trimmedString = zoomvalue.substring(0, l);
					double value = 0;
					value = Double.parseDouble(trimmedString);
					setIsPercent(true);
					zoom(value, orgrwidth, orgheight, newPage, isIsPercent());
				}
			}
		});

		this.reset.addActionListener(e -> {
			if (originalWidth * 2 == orgrwidth) {
				c.setSelectedItem("50%");
			}
			if (originalWidth * 4 == orgrwidth) {
				c.setSelectedItem("25%");
			}
			if (orgrwidth > 1440) {
				c.setSelectedItem("10%");
			}
			if (orgrwidth > 3600) {
				c.setSelectedItem("5%");
			}
			if (orgrwidth > 6400) {
				c.setSelectedItem("4%");
			}
			if (orgrwidth > 14400) {
				c.setSelectedItem("2%");
			}
			if (orgrwidth > 36000) {
				c.setSelectedItem("1%");
			}

			prevzoom = 0.0;
			zoom = 1.0;
		});
	}

	private void createRuler(JScrollPane scrollPane, JLabel label) {
		JLabel[] corners = new JLabel[4];
		for (int i = 0; i < 4; i++) {
			corners[i] = new JLabel();
			corners[i].setBackground(Color.lightGray);
			corners[i].setOpaque(true);
		}
		JLabel rowheader = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Rectangle rect = g.getClipBounds();
				g.setFont(new Font("SansSerif", Font.PLAIN, 10));

				g.setColor(Color.black);

				units = INCH;

				increment = units / 2;
				int tickLength = 0;
				String text = null;
				int start = 0;
				int end = 0;
				scrollRectToVisible(rect);
				start = (rect.y / increment) * increment;
				end = (((rect.y + rect.height) / increment) + 1) * increment;
				if (start == 0) {
					text = Integer.toString(0);

					tickLength = 10;
					g.drawLine(SIZE - 1, 0, SIZE - tickLength - 1, 0);

					g.drawString(text, 9, 10);

					text = null;

					start = increment;
				}

				for (int i = start; i < end; i += increment) {
					if (i % units == 0) {

						tickLength = 10;
						if (hght == orgheight) {
							text = Integer.toString(i / units * 1);
						}

						if (hght * 2 == orgheight) {
							text = Integer.toString(i / units * 2);
						} else if (hght * 4 == orgheight) {
							text = Integer.toString(i / units * 4);
						} else if (orgrwidth == 72000 && orgheight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (orgheight > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (orgheight > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (orgheight > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (orgheight > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (orgheight > 1440) {
							text = Integer.toString(i / units * 10);
						}
					} else {
						tickLength = 7;
						text = null;
					}

					if (tickLength != 0) {
						g.drawLine(SIZE - 1, i, SIZE - tickLength - 1, i);
						if (text != null) {
							g.drawString(text, 9, i + 3);
						}
					}
				}
			}

			public Dimension getPreferredSize() {
				return new Dimension(SIZE, (int) label.getPreferredSize().getHeight());
			}
		};

		rowheader.setBackground(Color.lightGray);
		rowheader.setOpaque(true);

		JLabel columnHeader = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				units = INCH;
				increment = units / 2;
				Rectangle r = g.getClipBounds();

				int tickLength = 0;

				String text = null;
				int start = 0;
				int end = 0;

				start = (r.x / increment) * increment;

				end = (((r.x + r.width) / increment) + 1) * increment;

				if (start == 0) {
					text = Integer.toString(0);

					tickLength = 10;
					g.drawLine(0, SIZE - 1, 0, SIZE - tickLength - 1);

					g.drawString(text, 2, 21);
					text = null;

					start = increment;
				}

				for (int i = start; i < end; i += increment) {
					if (i % units == 0) {

						tickLength = 10;

						if (wdth == orgrwidth) {
							text = Integer.toString(i / units * 1);
						}

						if (wdth * 2 == orgrwidth) {
							text = Integer.toString(i / units * 2);
						} else if (wdth * 4 == orgrwidth) {
							text = Integer.toString(i / units * 4);
						} else if (orgrwidth == 72000 && orgheight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (orgrwidth > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (orgrwidth > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (orgrwidth > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (orgrwidth > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (orgrwidth > 1440) {
							text = Integer.toString(i / units * 10);
						}
					} else {
						tickLength = 7;

						text = null;
					}
					g.drawLine(i, SIZE - 1, i, SIZE - tickLength - 1);
					if (text != null) {
						g.drawString(text + "", i - 3, 21);
					}
				}
			}

			public Dimension getPreferredSize() {
				return new Dimension((int) label.getPreferredSize().getWidth(), SIZE);
			}
		};
		columnHeader.setBackground(Color.lightGray);
		columnHeader.setOpaque(true);
		columnHeader.setAutoscrolls(true);

		scrollPane.setRowHeaderView(rowheader);
		scrollPane.setColumnHeaderView(columnHeader);
		scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, corners[0]);
		scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corners[1]);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corners[2]);
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corners[3]);
	}

	public void clearPreview() {

		if (listModel != null) {
			if (!listModel.isEmpty()) {
				listModel.clear();

			}
		}
	}

	public class MyRenderer extends DefaultListCellRenderer {
		
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setIcon(((JLabel) value).getIcon());
			label.setText(null);
			return label;
		}
	}

	private void zoom(double zoomFactor, int rwidth, int rheight, PDFPage page, boolean isPercent) {
		if (isPercent) {
			wdth = (int) ((rwidth * zoomFactor) / 100);
			hght = (int) ((rheight * zoomFactor) / 100);
		} else {
			wdth = (int) (wdth * zoomFactor);
			hght = (int) (hght * zoomFactor);
		}

		rect = new Rectangle(0, 0, rwidth, rheight);
		zoomImage = page.getImage(wdth, hght, rect, null, true, true);

		Image scaled = zoomImage.getScaledInstance(wdth, hght, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null),
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g.drawImage(zoomImage, 0, 0, zoomImage.getWidth(null), zoomImage.getHeight(null), null);

		repaint();
		g.dispose();

		JLabel label1 = new JLabel(new ImageIcon(zoomImage));
		listModel.addElement(label1);
		updateRuler(jsp, label1, rwidth, rheight, zoomFactor, isPercent);
	}

	private void updateRuler(JScrollPane scrollPane, JLabel labelnew, int rwidth, int rheight, double zoomfactor,
			boolean ispercent) {
		JLabel[] corners = new JLabel[4];
		for (int i = 0; i < 4; i++) {
			corners[i] = new JLabel();
			corners[i].setBackground(Color.lightGray);
			corners[i].setOpaque(true);
		}
		JLabel rowheader = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Rectangle rect = g.getClipBounds();

				g.setFont(new Font("SansSerif", Font.PLAIN, 10));

				g.setColor(Color.black);

				if (isIsPercent()) {
					if (orgheight < 360) {
						units = (int) ((int) (INCH * zoomfactor) / 100);
					}
					if (originalHeight * 2 == orgheight) {
						units = (int) (INCH * zoomfactor) / 50;
					}
					if (originalHeight * 4 == orgheight) {
						units = (int) (INCH * zoomfactor) / 25;
					}
					if (orgheight > 1440) {
						units = (int) (INCH * zoomfactor) / 10;
					}
					if (orgheight > 3600) {
						units = (int) (INCH * zoomfactor) / 5;
					}
					if (orgrwidth > 6400) {
						units = (int) (INCH * zoomfactor) / 4;
					}
					if (orgrwidth > 14400) {
						units = (int) (INCH * zoomfactor) / 2;
					}
					if (orgrwidth > 36000) {
						units = (int) (INCH * zoomfactor) / 1;
					}
				} else {
					units = (int) ((int) INCH * getZoom());
				}

				increment = units / 2;
				int tickLength = 0;
				String text = null;
				int start = 0;
				int end = 0;
				int temp = 0;
				scrollRectToVisible(rect);
				start = (rect.y / increment) * increment;
				end = (((rect.y + rect.height) / increment) + 1) * increment;
				if (start == 0) {
					text = Integer.toString(0);

					tickLength = 10;
					g.drawLine(SIZE - 1, 0, SIZE - tickLength - 1, 0);

					g.drawString(text, 9, 10);

					text = null;

					start = increment;
				}

				for (int i = start; i < end; i += increment) {
					if (units % 2 != 0) {
						units = units - 1;
					}
					if (i % units == 0) {
						tickLength = 10;

						if (ispercent == true) {
							temp = (int) ((int) (labelnew.getPreferredSize().getWidth() * 2) / zoomfactor);
						} else {
							temp = (int) ((int) (labelnew.getPreferredSize().getWidth() * 2) / getZoom());
						}

						if (originalHeight == orgheight) {
							text = Integer.toString(i / units * 1);
						}

						if (originalHeight * 2 == orgheight) {
							text = Integer.toString(i / units * 2);
						} else if (originalHeight * 4 == orgheight) {
							text = Integer.toString(i / units * 4);
						} else if (orgrwidth == 72000 && orgheight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (orgheight > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (orgheight > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (orgheight > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (orgheight > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (orgheight > 1440) {
							text = Integer.toString(i / units * 10);
						}
					} else {
						tickLength = 7;
						text = null;
					}

					g.drawLine(SIZE - 1, i, SIZE - tickLength - 1, i);
					if (text != null) {
						g.drawString(text, 9, i + 3);
					}
				}
			}

			public Dimension getPreferredSize() {
				return new Dimension(SIZE, (int) labelnew.getPreferredSize().getHeight());
			}
		};
		rowheader.setBackground(Color.lightGray);
		rowheader.setOpaque(true);

		JLabel columnHeader = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (isIsPercent()) {
					if (orgrwidth < 360) {
						units = (int) (INCH * zoomfactor) / 100;
					}
					if (originalWidth * 2 == orgrwidth) {
						units = (int) (INCH * zoomfactor) / 50;
					}
					if (originalWidth * 4 == orgrwidth) {
						units = (int) (INCH * zoomfactor) / 25;
					}
					if (orgrwidth > 1440) {
						units = (int) (INCH * zoomfactor) / 10;
					}
					if (orgrwidth > 3600) {
						units = (int) (INCH * zoomfactor) / 5;
					}
					if (orgrwidth > 6400) {
						units = (int) (INCH * zoomfactor) / 4;
					}
					if (orgrwidth > 14400) {
						units = (int) (INCH * zoomfactor) / 2;
					}
					if (orgrwidth > 14400) {
						units = (int) (INCH * zoomfactor) / 2;
					}
					if (orgrwidth > 36000) {
						units = (int) (INCH * zoomfactor);
					}

				} else {
					units = (int) (INCH * getZoom());
				}
				increment = units / 2;
				Rectangle r = g.getClipBounds();

				int tickLength = 0;

				String text = null;
				int start = 0;
				int end = 0;

				start = (r.x / increment) * increment;

				end = (((r.x + r.width) / increment) + 1) * increment;

				if (start == 0) {
					text = Integer.toString(0);

					tickLength = 10;
					g.drawLine(0, SIZE - 1, 0, SIZE - tickLength - 1);

					g.drawString(text, 2, 21);
					text = null;

					start = increment;
				}

				for (int i = start; i < end; i += increment) {

					if (units % 2 != 0) {
						units = units - 1;
					}
					if (i % units == 0) {
						tickLength = 10;

						if (originalWidth == orgrwidth) {
							text = Integer.toString(i / units);
						}

						if (originalWidth * 2 == orgrwidth) {
							text = Integer.toString(i / units * 2);
						} else if (originalWidth * 4 == orgrwidth) {
							text = Integer.toString(i / units * 4);
						} else if (orgrwidth == 72000 && orgheight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (orgrwidth > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (orgrwidth > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (orgrwidth > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (orgrwidth > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (orgrwidth > 1440) {
							text = Integer.toString(i / units * 10);
						}
					} else {
						tickLength = 7;
						text = null;
					}
					g.drawLine((int) (i), SIZE - 1, i, SIZE - tickLength - 1);

					if (text != null) {
						g.drawString(text + "", i - 3, 21);
					}
				}
			}

			public Dimension getPreferredSize() {
				return new Dimension((int) labelnew.getPreferredSize().getWidth(), SIZE);
			}
		};
		columnHeader.setBackground(Color.lightGray);
		columnHeader.setOpaque(true);
		columnHeader.setAutoscrolls(true);

		scrollPane.setRowHeaderView(rowheader);
		scrollPane.setColumnHeaderView(columnHeader);
		scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, corners[0]);
		scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corners[1]);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corners[2]);
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corners[3]);

	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double d) {
		prevzoom = zoom;
		zoom = prevzoom * d;
	}

	public static boolean isIsPercent() {
		return isPercent;
	}

	public static void setIsPercent(boolean isPercent) {
		Preview.isPercent = isPercent;
	}
}
