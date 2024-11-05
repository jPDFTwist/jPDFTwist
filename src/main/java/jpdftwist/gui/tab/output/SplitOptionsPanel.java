package jpdftwist.gui.tab.output;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SplitOptionsPanel extends JPanel {

    private final JRadioButton splitByPageTextRadioButton;
    private final JRadioButton splitByOddPagesRadioButton;
    private final JRadioButton splitByEvenPagesRadioButton;
    private final JRadioButton splitBySpecificPagesRadioButton;
    private final JRadioButton splitByChunkRadioButton;
    private final JRadioButton splitBySizeRadioButton;
    private final JRadioButton splitByBookmarkLevelRadioButton;
    private final JRadioButton splitByBookmarkTextRadioButton;
    private final JTextField splitBySpecificPagesTextField;
    private final JTextField splitByChunkTextField;
    private final JTextField splitByPageTextTextField;
    private final JTextField splitByBookmarkLevelTextField;
    private final JTextField splitByBookmarkTextTextField;
    private final JComboBox<String> splitBySizeComboBox;

    public SplitOptionsPanel() {
        super(new FormLayout(
            new ColumnSpec[]{
                ColumnSpec.decode("max(160px;pref)"),
                ColumnSpec.decode("max(200px;pref):grow"),
                ColumnSpec.decode("15px"),
                ColumnSpec.decode("max(160px;pref)"),
                ColumnSpec.decode("max(200px;pref):grow"),
                ColumnSpec.decode("15px")
            },
            new RowSpec[]{
                RowSpec.decode("23px"),
                RowSpec.decode("23px"),
                RowSpec.decode("23px"),
                RowSpec.decode("23px"),
                RowSpec.decode("23px")
            }));
        this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Split Options",
            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

        splitByOddPagesRadioButton = new JRadioButton("Split by odd pages");
        splitByOddPagesRadioButton.setSelected(false);
        splitByOddPagesRadioButton.setEnabled(false);

        splitByOddPagesRadioButton.setToolTipText("Split after each odd page");

        splitByEvenPagesRadioButton = new JRadioButton("Split by even pages");
        splitByEvenPagesRadioButton.setSelected(false);
        splitByEvenPagesRadioButton.setEnabled(false);

        splitByEvenPagesRadioButton.setToolTipText("Split after each even page");

        splitBySpecificPagesRadioButton = new JRadioButton("Split by specific pages");
        splitBySpecificPagesRadioButton.setSelected(false);
        splitBySpecificPagesRadioButton.setEnabled(false);

        splitBySpecificPagesRadioButton.setToolTipText("Split after specific pages (Ex: 4-6, 9, 14)");
        splitBySpecificPagesTextField = new JTextField("");

        splitByChunkRadioButton = new JRadioButton("Split by chunk of  'n' pages");
        splitByChunkRadioButton.setSelected(false);
        splitByChunkRadioButton.setEnabled(false);

        splitByChunkRadioButton.setSelected(true);
        splitByChunkRadioButton.setToolTipText("Split after a chunk of pages (Ex: 100)");
        splitByChunkTextField = new JTextField("1");

        splitBySizeRadioButton = new JRadioButton("Split by size");
        splitBySizeRadioButton.setSelected(false);
        splitBySizeRadioButton.setEnabled(false);

        splitBySizeRadioButton.setToolTipText("Split 'after' a specific size");
        splitBySizeComboBox = new JComboBox<>();
        splitBySizeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"(MB) > MegaBytes", "(KB)  > KiloBytes", "(B)    > Bytes"}));
        splitBySizeComboBox.setSelectedIndex(1);

        splitByBookmarkLevelRadioButton = new JRadioButton("Split by bookmark level");
        splitByBookmarkLevelRadioButton.setSelected(false);
        splitByBookmarkLevelRadioButton.setEnabled(false);

        splitByBookmarkLevelRadioButton.setToolTipText("Split 'before' the page linked to a bookmark level");
        splitByBookmarkLevelTextField = new JTextField("");

        splitByBookmarkTextRadioButton = new JRadioButton("Split by bookmark text");
        splitByBookmarkTextRadioButton.setSelected(false);
        splitByBookmarkTextRadioButton.setEnabled(false);

        splitByBookmarkTextRadioButton.setToolTipText("Split 'before' the bookmark containing a specific text");
        splitByBookmarkTextTextField = new JTextField("");

        splitByPageTextRadioButton = new JRadioButton("Split by page text");
        splitByPageTextRadioButton.setSelected(false);
        splitByPageTextRadioButton.setEnabled(false);

        splitByPageTextRadioButton.setToolTipText("Split 'after' the page containing a specific text");
        splitByPageTextTextField = new JTextField("");

        add(splitByOddPagesRadioButton, "1, 1, left, center");
        add(splitByBookmarkLevelRadioButton, "4, 1, left, center");
        add(splitByBookmarkLevelTextField, "5, 1, fill, default");
        add(splitByBookmarkTextRadioButton, "4, 2, left, center");
        add(splitByBookmarkTextTextField, "5, 2, fill, default");
        add(splitBySpecificPagesTextField, "2, 3, fill, default");
        add(splitBySizeRadioButton, "4, 3, left, center");
        add(splitByEvenPagesRadioButton, "1, 2, left, center");
        add(splitBySpecificPagesRadioButton, "1, 3, left, center");
        add(splitBySizeComboBox, "5, 3, fill, default");
        add(splitByChunkRadioButton, "1, 4, left, center");
        add(splitByChunkTextField, "2, 4, fill, default");
        add(splitByPageTextRadioButton, "1, 5, left, center");
        add(splitByPageTextTextField, "2, 5, fill, default");

        setEnabled(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        splitByOddPagesRadioButton.setEnabled(false);
        splitByEvenPagesRadioButton.setEnabled(false);
        splitBySpecificPagesRadioButton.setEnabled(false);
        splitBySpecificPagesTextField.setEnabled(false);
        splitByChunkRadioButton.setEnabled(false);
        splitByChunkTextField.setEnabled(false);
        splitByPageTextRadioButton.setEnabled(false);
        splitByPageTextTextField.setEnabled(false);
        splitByBookmarkLevelRadioButton.setEnabled(false);
        splitByBookmarkLevelTextField.setEnabled(false);
        splitByBookmarkTextTextField.setEnabled(false);
        splitByBookmarkTextRadioButton.setEnabled(false);
        splitBySizeRadioButton.setEnabled(false);
        splitBySizeComboBox.setEnabled(false);
    }
}
