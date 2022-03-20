package jpdftweak.gui;

import com.sun.pdfview.PDFPage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Preview extends JPanel {

	private final DefaultListModel<JLabel> jLabelListModel = new DefaultListModel<>();
	private final JSpinner zoomValueSpinner;
	private final JScrollPane scrollPane;

	private PDFPage previewPage;
	private int originalElementWidth;
	private int originalElementHeight;
	private int initialPreviewHeight;
	private int initialPreviewWidth;
	private int currentPreviewHeight;
	private int currentPreviewWidth;

	public static int INCH = 72;
	public static final int SIZE = 45;

	private int increment;
	private int units;
	private double previousZoom;
	private double zoom;

	public Preview() {
		super(new BorderLayout());

		previousZoom = 0.0;
		zoom = 1.0;

		JButton zoomOut = new JButton("-");
		zoomOut.setToolTipText("Zoom out");

		JButton zoomIn = new JButton("+");
		zoomIn.setToolTipText("Zoom in");

		JButton reset = new JButton("Fit to Window");
		reset.setToolTipText("Default preview");

		zoomValueSpinner = new JSpinner();
		zoomValueSpinner.setModel(new SpinnerNumberModel(1, 1, 999, 1));
		zoomValueSpinner.setToolTipText("Zoom");

		JList<JLabel> list = new JList<>(jLabelListModel);

		MyRenderer myRenderer = new MyRenderer();
		list.setCellRenderer(myRenderer);
		list.setBackground(Color.gray);
		scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBackground(Color.gray);
		scrollPane.getVerticalScrollBar().setUnitIncrement(8);
		scrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
		add(scrollPane, BorderLayout.CENTER);

		final JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(zoomIn);
		buttonsPanel.add(zoomOut);
		buttonsPanel.add(zoomValueSpinner);
		buttonsPanel.add(reset);
		add(buttonsPanel, BorderLayout.PAGE_END);

		zoomIn.addActionListener(e -> {
			if (jLabelListModel.isEmpty()) {
				return;
			}

			final Icon icon = jLabelListModel.get(0).getIcon();
			if (!(icon instanceof ImageIcon)) {
				return;
			}

			zoom(1.5, originalElementWidth, originalElementHeight, previewPage, false);
			setZoom(1.5);
		});

		zoomOut.addActionListener(e -> {
			if (jLabelListModel.isEmpty()) {
				return;
			}

			final Icon icon = jLabelListModel.get(0).getIcon();
			if (!(icon instanceof ImageIcon)) {
				return;
			}

			zoom(0.5, originalElementWidth, originalElementHeight, previewPage, false);
			setZoom(0.5);
		});

		this.zoomValueSpinner.addChangeListener(e -> {
			if (jLabelListModel.isEmpty()) {
				return;
			}

			final Icon icon = jLabelListModel.get(0).getIcon();
			if (!(icon instanceof ImageIcon)) {
				return;
			}

			int zoomValue = (int) zoomValueSpinner.getValue();
			zoom(zoomValue, originalElementWidth, originalElementHeight, previewPage, true);
		});

		reset.addActionListener(e -> {
			if (initialPreviewWidth * 2 == originalElementWidth) {
				zoomValueSpinner.setValue(50);
			}
			if (initialPreviewWidth * 4 == originalElementWidth) {
				zoomValueSpinner.setValue(25);
			}
			if (originalElementWidth > 1440) {
				zoomValueSpinner.setValue(10);
			}
			if (originalElementWidth > 3600) {
				zoomValueSpinner.setValue(5);
			}
			if (originalElementWidth > 6400) {
				zoomValueSpinner.setValue(4);
			}
			if (originalElementWidth > 14400) {
				zoomValueSpinner.setValue(2);
			}
			if (originalElementWidth > 36000) {
				zoomValueSpinner.setValue(1);
			}

			previousZoom = 0.0;
			zoom = 1.0;
		});
	}

	public void preview(Image previewImage, int originalElementWidth, int originalElementHeight, PDFPage previewPage) {
		clearPreview();
		this.previewPage = previewPage;
		ImageIcon previewImage1 = new ImageIcon(previewImage);
		this.originalElementWidth = originalElementWidth;
		this.originalElementHeight = originalElementHeight;
		this.initialPreviewWidth = previewImage1.getIconWidth();
		this.initialPreviewHeight = previewImage1.getIconHeight();
		this.currentPreviewWidth = initialPreviewWidth;
		this.currentPreviewHeight = initialPreviewHeight;

		if (initialPreviewWidth == originalElementWidth) {
			zoomValueSpinner.setValue(100);
		} else if (initialPreviewWidth * 2 == originalElementWidth) {
			zoomValueSpinner.setValue(50);
		} else if (initialPreviewWidth * 4 == originalElementWidth) {
			zoomValueSpinner.setValue(25);
		} else if (originalElementWidth > 36000) {
			zoomValueSpinner.setValue(1);
		} else if (originalElementWidth > 14400) {
			zoomValueSpinner.setValue(2);
		} else if (originalElementWidth > 6400) {
			zoomValueSpinner.setValue(4);
		} else if (originalElementWidth > 3600) {
			zoomValueSpinner.setValue(10);
		} else if (originalElementWidth > 1440) {
			zoomValueSpinner.setValue(5);
		}

		JLabel previewImageLabel = new JLabel(previewImage1);
		jLabelListModel.addElement(previewImageLabel);

		createRuler(scrollPane, previewImageLabel);
		scrollPane.setPreferredSize(new Dimension((int) previewImageLabel.getPreferredSize().getWidth() / 10,
			(int) previewImageLabel.getPreferredSize().getHeight() / 10));
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
						if (currentPreviewHeight == originalElementHeight) {
							text = Integer.toString(i / units * 1);
						}

						if (currentPreviewHeight * 2 == originalElementHeight) {
							text = Integer.toString(i / units * 2);
						} else if (currentPreviewHeight * 4 == originalElementHeight) {
							text = Integer.toString(i / units * 4);
						} else if (originalElementWidth == 72000 && originalElementHeight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (originalElementHeight > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (originalElementHeight > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (originalElementHeight > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (originalElementHeight > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (originalElementHeight > 1440) {
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

						if (currentPreviewWidth == originalElementWidth) {
							text = Integer.toString(i / units * 1);
						}

						if (currentPreviewWidth * 2 == originalElementWidth) {
							text = Integer.toString(i / units * 2);
						} else if (currentPreviewWidth * 4 == originalElementWidth) {
							text = Integer.toString(i / units * 4);
						} else if (originalElementWidth == 72000 && originalElementHeight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (originalElementWidth > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (originalElementWidth > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (originalElementWidth > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (originalElementWidth > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (originalElementWidth > 1440) {
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
		if (jLabelListModel == null) {
			return;
		}
		jLabelListModel.clear();
	}

	private static class MyRenderer extends DefaultListCellRenderer {
		
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
			currentPreviewWidth = (int) ((rwidth * zoomFactor) / 100);
			currentPreviewHeight = (int) ((rheight * zoomFactor) / 100);
		} else {
			currentPreviewWidth = (int) (currentPreviewWidth * zoomFactor);
			currentPreviewHeight = (int) (currentPreviewHeight * zoomFactor);
			zoomValueSpinner.setValue((currentPreviewWidth * 100) / rwidth);
		}

		Rectangle rect = new Rectangle(0, 0, rwidth, rheight);
		Image zoomImage = page.getImage(currentPreviewWidth, currentPreviewHeight, rect, null, true, true);

		Image scaled = zoomImage.getScaledInstance(currentPreviewWidth, currentPreviewHeight, Image.SCALE_SMOOTH);
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

		JLabel zoomedLabel = new JLabel(new ImageIcon(zoomImage));
		clearPreview();
		jLabelListModel.addElement(zoomedLabel);
		updateRuler(scrollPane, zoomedLabel, zoomFactor, isPercent);
	}

	private void updateRuler(JScrollPane scrollPane, JLabel labelnew, double zoomfactor, boolean ispercent) {
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

				if (ispercent) {
					if (originalElementHeight < 360) {
						units = (int) (INCH * zoomfactor) / 100;
					}
					if (initialPreviewHeight * 2 == originalElementHeight) {
						units = (int) (INCH * zoomfactor) / 50;
					}
					if (initialPreviewHeight * 4 == originalElementHeight) {
						units = (int) (INCH * zoomfactor) / 25;
					}
					if (originalElementHeight > 1440) {
						units = (int) (INCH * zoomfactor) / 10;
					}
					if (originalElementHeight > 3600) {
						units = (int) (INCH * zoomfactor) / 5;
					}
					if (originalElementWidth > 6400) {
						units = (int) (INCH * zoomfactor) / 4;
					}
					if (originalElementWidth > 14400) {
						units = (int) (INCH * zoomfactor) / 2;
					}
					if (originalElementWidth > 36000) {
						units = (int) (INCH * zoomfactor);
					}
				} else {
					units = (int) (INCH * getZoom());
				}

				increment = units / 2;
				if (increment == 0) {
					return;
				}
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

						if (initialPreviewHeight == originalElementHeight) {
							text = Integer.toString(i / units * 1);
						}

						if (initialPreviewHeight * 2 == originalElementHeight) {
							text = Integer.toString(i / units * 2);
						} else if (initialPreviewHeight * 4 == originalElementHeight) {
							text = Integer.toString(i / units * 4);
						} else if (originalElementWidth == 72000 && originalElementHeight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (originalElementHeight > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (originalElementHeight > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (originalElementHeight > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (originalElementHeight > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (originalElementHeight > 1440) {
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

				if (ispercent) {
					if (originalElementWidth < 360) {
						units = (int) (INCH * zoomfactor) / 100;
					}
					if (initialPreviewWidth * 2 == originalElementWidth) {
						units = (int) (INCH * zoomfactor) / 50;
					}
					if (initialPreviewWidth * 4 == originalElementWidth) {
						units = (int) (INCH * zoomfactor) / 25;
					}
					if (originalElementWidth > 1440) {
						units = (int) (INCH * zoomfactor) / 10;
					}
					if (originalElementWidth > 3600) {
						units = (int) (INCH * zoomfactor) / 5;
					}
					if (originalElementWidth > 6400) {
						units = (int) (INCH * zoomfactor) / 4;
					}
					if (originalElementWidth > 14400) {
						units = (int) (INCH * zoomfactor) / 2;
					}
					if (originalElementWidth > 14400) {
						units = (int) (INCH * zoomfactor) / 2;
					}
					if (originalElementWidth > 36000) {
						units = (int) (INCH * zoomfactor);
					}

				} else {
					units = (int) (INCH * getZoom());
				}
				increment = units / 2;
				if (increment == 0) {
					return;
				}
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

						if (initialPreviewWidth == originalElementWidth) {
							text = Integer.toString(i / units);
						}

						if (initialPreviewWidth * 2 == originalElementWidth) {
							text = Integer.toString(i / units * 2);
						} else if (initialPreviewWidth * 4 == originalElementWidth) {
							text = Integer.toString(i / units * 4);
						} else if (originalElementWidth == 72000 && originalElementHeight == 72000) {
							text = Integer.toString(i / units * 200);
						} else if (originalElementWidth > 36000) {
							text = Integer.toString(i / units * 100);
						} else if (originalElementWidth > 14400) {
							text = Integer.toString(i / units * 50);
						} else if (originalElementWidth > 6400) {
							text = Integer.toString(i / units * 25);
						} else if (originalElementWidth > 3600) {
							text = Integer.toString(i / units * 20);
						} else if (originalElementWidth > 1440) {
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
		previousZoom = zoom;
		zoom = previousZoom * d;
	}
}
