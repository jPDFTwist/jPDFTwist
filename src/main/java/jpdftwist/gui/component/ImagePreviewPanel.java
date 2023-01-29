package jpdftwist.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePreviewPanel extends JPanel {

    private final DefaultListModel<JLabel> jLabelListModel = new DefaultListModel<>();
    private final JLabel zoomLabel;
    private final JScrollPane scrollPane;
    private final JList<JLabel> list;

    private Image originalImage;

    public static int INCH = 72;
    public static final int SIZE = 45;

    private double zoom;

    public ImagePreviewPanel() {
        super(new BorderLayout());

        JButton zoomOut = new JButton("-");
        zoomOut.setToolTipText("Zoom out");

        JButton zoomIn = new JButton("+");
        zoomIn.setToolTipText("Zoom in");

        JButton reset = new JButton("Fit to Window");
        reset.setToolTipText("Default preview");

        zoomLabel = new JLabel("100%");
        zoomLabel.setToolTipText("Zoom");

        list = new JList<>(jLabelListModel);

        MyRenderer myRenderer = new MyRenderer();
        list.setCellRenderer(myRenderer);
        list.setBackground(Color.gray);
        list.setSelectionModel(new NoSelectionModel());
        scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(Color.gray);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
        add(scrollPane, BorderLayout.CENTER);

        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(zoomOut);
        buttonsPanel.add(zoomLabel);
        buttonsPanel.add(zoomIn);
        buttonsPanel.add(reset);
        add(buttonsPanel, BorderLayout.PAGE_END);

        this.zoom = 1;

        zoomIn.addActionListener(e -> zoom(zoom + 0.05));
        zoomOut.addActionListener(e -> zoom(zoom - 0.05));
        scrollPane.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    zoom(zoom + 0.05);
                } else {
                    zoom(zoom - 0.05);
                }
            }
        });
        reset.addActionListener(e -> {
            if (originalImage == null) {
                return;
            }
            zoom(getVisibleWidth() / originalImage.getWidth(null));
        });
    }

    public double getVisibleWidth() {
        return scrollPane.getViewport().getWidth();
    }

    private static class NoSelectionModel extends DefaultListSelectionModel {

        @Override
        public void setAnchorSelectionIndex(final int anchorIndex) {
        }

        @Override
        public void setLeadAnchorNotificationEnabled(final boolean flag) {
        }

        @Override
        public void setLeadSelectionIndex(final int leadIndex) {
        }

        @Override
        public void setSelectionInterval(final int index0, final int index1) {
        }
    }

    public void preview(Image previewImage) {
        clearPreview();

        if (previewImage == null) {
            return;
        }

        this.originalImage = previewImage;
        jLabelListModel.addElement(new JLabel(new ImageIcon(previewImage)));

        zoom(getVisibleWidth() / originalImage.getWidth(null));
    }

    private void createRuler(JScrollPane scrollPane, JLabel label) {
        JLabel[] corners = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            corners[i] = new JLabel();
            corners[i].setBackground(Color.lightGray);
            corners[i].setOpaque(true);
        }
        JLabel verticalRuler = new JLabel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.setColor(Color.black);

                double units = INCH * zoom;
                double increment = units / 2;

                for (double i = 0; i < list.getHeight(); i += units) {
                    String text = i == 0 ? "" : Integer.toString((int) i / (int) units);
                    drawHorizontalTickWithNumber(g, (int) Math.round(i), text);
                    drawHorizontalTick(g, (int) Math.round(i + increment));
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension(SIZE, (int) label.getPreferredSize().getHeight());
            }
        };

        verticalRuler.setBackground(Color.lightGray);
        verticalRuler.setOpaque(true);

        JLabel columnHeader = new JLabel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.setColor(Color.black);

                double units = INCH * zoom;
                double increment = units / 2;

                for (double i = 0; i < list.getWidth(); i += units) {
                    String text = i == 0 ? "" : Integer.toString((int) i / (int) units);
                    drawVerticalTickWithNumber(g, (int) Math.round(i), text);
                    drawVerticalTick(g, (int) Math.round(i + increment));
                }
            }

            public Dimension getPreferredSize() {
                return new Dimension((int) label.getPreferredSize().getWidth(), SIZE);
            }
        };
        columnHeader.setBackground(Color.lightGray);
        columnHeader.setOpaque(true);
        columnHeader.setAutoscrolls(true);

        scrollPane.setRowHeaderView(verticalRuler);
        scrollPane.setColumnHeaderView(columnHeader);
        scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, corners[0]);
        scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corners[1]);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corners[2]);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corners[3]);
    }

    private void drawHorizontalTickWithNumber(Graphics g, int atY, String number) {
        int tickLength = 10;
        g.drawLine(SIZE - 1, atY, SIZE - tickLength - 1, atY);
        g.drawString(number, 9, atY + 3);
    }

    private void drawHorizontalTick(Graphics g, int atY) {
        int tickLength = 7;
        g.drawLine(SIZE - 1, atY, SIZE - tickLength - 1, atY);
    }

    private void drawVerticalTickWithNumber(Graphics g, int atX, String number) {
        int tickLength = 10;
        g.drawLine(atX, SIZE - 1, atX, SIZE - tickLength - 1);
        g.drawString(number, atX - 3, 21);
    }

    private void drawVerticalTick(Graphics g, int atX) {
        int tickLength = 7;
        g.drawLine(atX, SIZE - 1, atX, SIZE - tickLength - 1);
    }

    public void clearPreview() {
        jLabelListModel.clear();
        originalImage = null;
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

    private void zoom(double zoomFactor) {
        if (originalImage == null) {
            return;
        }
        if (INCH * zoomFactor < 2) {
            return;
        }

        zoom = zoomFactor;
        zoomLabel.setText((int) (zoomFactor * 100) + "%");

        int currentPreviewWidth = (int) (originalImage.getWidth(null) * zoomFactor);
        int currentPreviewHeight = (int) (originalImage.getHeight(null) * zoomFactor);

        Image scaled = originalImage.getScaledInstance(currentPreviewWidth, currentPreviewHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null),
            BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.drawImage(scaled, 0, 0, currentPreviewWidth, currentPreviewHeight, null);

        repaint();
        g.dispose();

        JLabel zoomedLabel = new JLabel(new ImageIcon(scaled));
        jLabelListModel.clear();
        jLabelListModel.addElement(zoomedLabel);
        createRuler(scrollPane, zoomedLabel);
        scrollPane.setPreferredSize(new Dimension((int) zoomedLabel.getPreferredSize().getWidth() / 10,
            (int) zoomedLabel.getPreferredSize().getHeight() / 10));
    }
}
