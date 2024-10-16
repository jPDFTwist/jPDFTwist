package jpdftwist.gui.dialog;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.NumberField;
import jpdftwist.core.OutputPdfProcessor;
import jpdftwist.core.UnitTranslator;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class ScaleCustomSizeDialog extends JPanel {

    private JComboBox unitsBox;
    private NumberField widthField, heightField;
    private JLabel widthLabel, heightLabel;

    public ScaleCustomSizeDialog() {
        initComponents();
        buildGui();
    }

    private void initComponents() {
        widthLabel = new JLabel("Width:");
        heightLabel = new JLabel("Height:");

        widthField = new NumberField();
        heightField = new NumberField();

        unitsBox = new JComboBox(new String[]{"mm", "inch", "%", "points"});
    }

    private void buildGui() {
        FormLayout layout = new FormLayout("right:p, 3dlu, f:p:g", "3dlu, f:p, 3dlu, f:p, 3dlu, f:p");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setBorder(null);

        // builder.setBorder(BorderFactory.createLoweredBevelBorder());
        // builder.setBorder(BorderFactory.createLineBorder(Color.black));

        CellConstraints CC = new CellConstraints();

        builder.addLabel("Units:", CC.xy(1, 2));
        builder.add(unitsBox, CC.xy(3, 2));
        builder.add(widthLabel, CC.xy(1, 4));
        builder.add(widthField, CC.xy(3, 4));
        builder.add(heightLabel, CC.xy(1, 6));
        builder.add(heightField, CC.xy(3, 6));

        this.add(builder.getPanel());

    }

    public float getPagePostscriptWidth() {
        double width = 0;
        try {
            width = Double.parseDouble(widthField.getText());
        } catch (NumberFormatException ex) {

        }
        switch (unitsBox.getSelectedIndex()) {
            case 0:
                width = UnitTranslator.millisToPoints(width);
                break;
            case 1:
                width = UnitTranslator.POINT_POSTSCRIPT * width;
                break;
        }
        width = round(width, 2);
        return (float) width;
    }

    public String getPageWidth() {
        String width = "0";
        if (!widthField.getText().equals("")) {
            width = widthField.getText();
        }
        return width;
    }

    public float getPagePostscriptHeight() {
        double height = 0;
        try {
            height = Double.parseDouble(heightField.getText());
        } catch (NumberFormatException ex) {

        }
        switch (unitsBox.getSelectedIndex()) {
            case 0:
                height = UnitTranslator.millisToPoints(height);
                break;
            case 1:
                height = UnitTranslator.POINT_POSTSCRIPT * height;
                break;
        }
        height = round(height, 2);
        return (float) height;
    }

    public String getPageHeight() {
        String height = "0";
        if (!heightField.getText().equals("")) {
            height = heightField.getText();
        }
        return height;
    }

    public boolean isPercentage() {
        boolean percentage = false;
        if (unitsBox.getSelectedIndex() == 2) {
            percentage = true;
        }
        return percentage;
    }

    public String getUnitsName() {
        return unitsBox.getSelectedItem().toString();
    }

    private double round(double value, int places) {
        if (places < 0) {
            Logger.getLogger(ScaleCustomSizeDialog.class.getName()).log(Level.SEVERE, "Ex130");
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
