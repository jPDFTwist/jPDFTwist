/**
 * Original Functions		@author Michael Schierl					Affero GNU Public License
 * Additional Functions		@author & @sponsor: E.Victor			Proprietary for in-house use only / Not released to the Public
 */
package jpdftwist.gui.tab.watermark;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import jpdftwist.core.watermark.WatermarkStyle;
import jpdftwist.gui.component.table.TableComponent;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class WatermarkPlusTabPanel extends JPanel {

    private TableComponent pageNumberRanges;
    private WatermarkPreviewComponent previewBox;
    private final CellConstraints CC;
    private WatermarkOptionsPanel optionsPanel;
    private WatermarkVariableTextPanel variableTextPanel;
    private WatermarkBatesPanel batesPanel;
    private WatermarkRepeatedTextPanel repeatedTextPanel;
    private WatermarkImagePanel imagePanel;
    private JPanel emptyPanel;

    private JPanel styleOptionsPanel;

    public static WatermarkPlusTabPanel getWatermarkTabPanel() {
        return new WatermarkPlusTabPanel();
    }

    public WatermarkPlusTabPanel() {
        super(new FormLayout(new ColumnSpec[]{
            ColumnSpec.decode("pref:grow"),
            FormSpecs.PREF_COLSPEC,
            ColumnSpec.decode("5dlu"),
            FormSpecs.DEFAULT_COLSPEC,
            FormSpecs.RELATED_GAP_COLSPEC,},
            new RowSpec[]{
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.PREF_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("fill:pref:grow"),}));

        CC = new CellConstraints();
        generateUserInterface();
    }

    private void generateUserInterface() {
        initializeComponents();
        positionComponents();
    }

    private void initializeComponents() {
        pageNumberRanges = new TableComponent(new String[]{"Style", "Start Page", "Prefix", "Logical Page"},
            new Class[]{WatermarkStyle.class, Integer.class, String.class, Integer.class},
            new Object[]{new WatermarkStyle(), 1, "", 1});
        pageNumberRanges.getScrollPane().setPreferredSize(new Dimension(400, 220));

        pageNumberRanges.getTable().getSelectionModel().addListSelectionListener(e -> setOptions());

        StyleChangeListener styleChangeListener = value -> {
            replacePanel(value);
            setOptions();
        };

        previewBox = WatermarkPreviewComponent.createPreviewBox();

        optionsPanel = new WatermarkOptionsPanel();
        optionsPanel.setPreviewModel(previewBox.getDefaultModel());
        optionsPanel.setStyleChangeListener(styleChangeListener);

        variableTextPanel = new WatermarkVariableTextPanel();
        batesPanel = new WatermarkBatesPanel();
        repeatedTextPanel = new WatermarkRepeatedTextPanel();
        imagePanel = new WatermarkImagePanel();

        emptyPanel = new JPanel();
        styleOptionsPanel = emptyPanel;
    }

    private void positionComponents() {
        this.add(pageNumberRanges, "1, 2, 2, 1");
        this.add(previewBox, CC.xy(4, 2));
        this.add(optionsPanel, "1, 4, 2, 1");
        this.add(styleOptionsPanel, "4, 3, 1, 2");
    }

    private void replacePanel(final int value) {
        SwingUtilities.invokeLater(() -> {
            remove(styleOptionsPanel);

            switch (value) {
                case 5:
                    styleOptionsPanel = batesPanel;
                    break;
                case 6:
                    styleOptionsPanel = repeatedTextPanel;
                    break;
                case 7:
                    styleOptionsPanel = imagePanel;
                    break;
                case 8:
                    styleOptionsPanel = variableTextPanel;
                    break;
                default:
                    styleOptionsPanel = emptyPanel;
            }

            add(styleOptionsPanel, CC.xy(4, 4));
            validate();
            repaint();
        });
    }

    private void setOptions() {
        try {
            int row = pageNumberRanges.getTable().getSelectedRow();
            TableModel model = pageNumberRanges.getTable().getModel();
            WatermarkStyle style = (WatermarkStyle) model.getValueAt(row, 0);

            optionsPanel.setStyle(style);

            if (style.getType() == WatermarkStyle.WatermarkType.REPEATED_TEXT)
                repeatedTextPanel.setStyle(style);
            else if (style.getType() == WatermarkStyle.WatermarkType.VARIABLE_TEXT)
                variableTextPanel.setStyle(style);
            else if (style.getType() == WatermarkStyle.WatermarkType.IMAGE)
                imagePanel.setStyle(style);
            else if (style.getType() == WatermarkStyle.WatermarkType.BATES_NUMBERING)
                batesPanel.setStyle(style);

            switch (style.getType()) {
                case NUMBERS:
                    previewBox.setText("1, 2, 3");
                    break;
                case LATIN_CAPITAL:
                    previewBox.setText("I, II, III");
                    break;
                case LATIN_LOWERCASE:
                    previewBox.setText("i, ii, iii");
                    break;
                case CAPITAL_LETTERS:
                    previewBox.setText("A, B, C");
                    break;
                case LOWERCASE_LETTERS:
                    previewBox.setText("a, b, c");
                    break;
                case BATES_NUMBERING:
                    previewBox.setText("Bates Numbering");
                    break;
                case IMAGE:
                    previewBox.setText("Image");
                    break;
                case REPEATED_TEXT:
                    previewBox.setText("Repeated Text");
                    break;
                case VARIABLE_TEXT:
                    previewBox.setText("Variable Text");
                    break;
                default:
                    previewBox.setText("Watermark");
                    break;
            }
        } catch (Exception ex) {

        }
    }

    public interface StyleChangeListener {
        public void styleChanged(int value);
    }

    public WatermarkStyle[] getStyles() {
        TableModel model = pageNumberRanges.getTable().getModel();
        WatermarkStyle[] styles = new WatermarkStyle[model.getRowCount()];

        for (int i = 0; i < model.getRowCount(); i++) {
            styles[i] = (WatermarkStyle) model.getValueAt(i, 0);
            styles[i].setStartPage(Integer.parseInt(model.getValueAt(i, 1).toString()));
            styles[i].setPrefix(model.getValueAt(i, 2).toString());
            styles[i].setLogicalPage(Integer.parseInt(model.getValueAt(i, 3).toString()));
        }

        return styles;
    }
}
