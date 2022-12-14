package jpdftwist.gui.tab.input;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Vasilis Naskos
 */
public class InputOptionsPanel extends JPanel {

    private final CellConstraints CC = new CellConstraints();
    private JCheckBox batchProcessing, interleave, mergeByDir;
    private JCheckBox optimizePDF, autoRemoveRestrictionsOverwrite, autoRemoveRestrictionsNew;
    private JTextField interleaveSize;
    private JCheckBox ReadPageSizes;

    public InputOptionsPanel() {
        super(new FormLayout(new ColumnSpec[]{
            FormSpecs.PREF_COLSPEC,
            ColumnSpec.decode("60px"),
            ColumnSpec.decode("12px"),
            ColumnSpec.decode("142px"),
            ColumnSpec.decode("12px"),
            ColumnSpec.decode("240px"),
            ColumnSpec.decode("12px"),
            ColumnSpec.decode("pref:grow"),
            ColumnSpec.decode("12px"),},
            new RowSpec[]{
                RowSpec.decode("fill:pref"),
                RowSpec.decode("fill:pref"),}));

        initializeComponents();
        positionComponents();
    }

    private void initializeComponents() {
        interleave = new JCheckBox("Interleave documents in blocks of");

        interleaveSize = new JTextField("1", 10);
        interleaveSize.setEnabled(false);
        interleave.addItemListener(interleaveListener());
    }

    private ItemListener interleaveListener() {
        return e -> interleaveItemStateChanged();
    }

    private void interleaveItemStateChanged() {
        interleaveSize.setEnabled(interleave.isSelected());
    }

    private ItemListener autoRemoveRestrictionsItemStateChanged() {
        return e -> {
            JCheckBox check = (JCheckBox) e.getSource();
            if (check == autoRemoveRestrictionsOverwrite && e.getStateChange() == ItemEvent.SELECTED) {
                autoRemoveRestrictionsNew.setSelected(false);
            } else if (check == autoRemoveRestrictionsNew && e.getStateChange() == ItemEvent.SELECTED) {
                autoRemoveRestrictionsOverwrite.setSelected(false);
            }
        };
    }

    private void mergeByDirItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            batchProcessing.setSelected(false);
            interleave.setEnabled(true);
        }
    }

    private void batchProcessingItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            mergeByDir.setSelected(false);
            interleave.setEnabled(false);
            interleave.setSelected(false);
        } else {
            interleave.setEnabled(true);
        }
    }

    private void positionComponents() {
        autoRemoveRestrictionsOverwrite = new JCheckBox("Auto remove restrictions (overwrite)");
        autoRemoveRestrictionsOverwrite.addItemListener(autoRemoveRestrictionsItemStateChanged());

        batchProcessing = new JCheckBox("Batch Process");
        batchProcessing.addItemListener(this::batchProcessingItemStateChanged);
        this.add(batchProcessing, "1, 1");
        this.add(autoRemoveRestrictionsOverwrite, "6, 1");
        add(getReadPageSizes(), "8, 1");
        this.add(interleave, CC.xy(1, 2));
        this.add(interleaveSize, CC.xy(2, 2));
        autoRemoveRestrictionsNew = new JCheckBox("Auto remove restrictions (new)");
        autoRemoveRestrictionsNew.addItemListener(autoRemoveRestrictionsItemStateChanged());

        mergeByDir = new JCheckBox("Merge by directory");
        mergeByDir.addItemListener(this::mergeByDirItemStateChanged);
        this.add(mergeByDir, "4, 2");
        this.add(autoRemoveRestrictionsNew, "6, 2");

        optimizePDF = new JCheckBox("Optimize PDF");
        this.add(optimizePDF, "8, 2");
    }

    public boolean isMergeByDirSelected() {
        return mergeByDir.isSelected();
    }

    public boolean isBatchSelected() {
        return batchProcessing.isSelected();
    }

    public boolean isInterleaveSelected() {
        return interleave.isSelected();
    }

    public String getInterleaveSize() {
        return interleaveSize.getText();
    }

    public boolean isOptimizePDFSelected() {
        return optimizePDF.isSelected();
    }

    public boolean isAutoRestrictionsOverwriteSelected() {
        return autoRemoveRestrictionsOverwrite.isSelected();
    }

    public boolean isAutoRestrictionsNewSelected() {
        return autoRemoveRestrictionsNew.isSelected();
    }

    private JCheckBox getReadPageSizes() {
        if (ReadPageSizes == null) {
            ReadPageSizes = new JCheckBox("Read Page Sizes");
        }
        return ReadPageSizes;
    }
}
