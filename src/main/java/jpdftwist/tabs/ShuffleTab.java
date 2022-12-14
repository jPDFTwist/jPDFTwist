package jpdftwist.tabs;

import com.itextpdf.text.DocumentException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PageDimension;
import jpdftwist.core.ShuffleRule;
import jpdftwist.core.ShuffleRule.PageBase;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.component.ShufflePreviewPanel;
import jpdftwist.gui.component.table.TableComponent;
import jpdftwist.gui.dialog.OutputProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ShuffleTab extends Tab {

    private ShufflePreviewPanel pp;

    private JCheckBox shufflePages, blockShuffle;
    private JButton updatePreview, use;
    private JComboBox previewFormat, preset;
    private JTextField pagesPerPass, configString, blockSize;
    private TableComponent shuffleRulesTable;

    private int shufflePagesPerPass;
    private ShuffleRule[] shuffleRules;

    private final MainWindow mf;

    public ShuffleTab(MainWindow mf) {
        super(new BorderLayout());
        this.mf = mf;
        JPanel panel1 = new JPanel(new FormLayout("f:p, f:p:g, f:p, f:p", "f:p, f:p, f:p, 10dlu, f:p, f:p, f:p:g"));
        JPanel panel2 = new JPanel(new BorderLayout());
        JSplitPane jsp;
        add(jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2), BorderLayout.CENTER);
        jsp.setResizeWeight(0.5);
        CellConstraints CC = new CellConstraints();
        panel2.add(previewFormat = new JComboBox(), BorderLayout.NORTH);
        panel2.add(pp = new ShufflePreviewPanel(), BorderLayout.CENTER);
        for (PageDimension d : PageDimension.getCommonSizes()) {
            previewFormat.addItem(d);
        }
        previewFormat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pp.setPageFormat((PageDimension) previewFormat.getSelectedItem());
            }
        });
        previewFormat.setSelectedIndex(0);
        pp.setPreferredSize(new Dimension(50, 100));
        previewFormat.setMinimumSize(new Dimension(50, previewFormat.getMinimumSize().height));
        panel1.add(shufflePages = new JCheckBox("Shuffle pages"), CC.xyw(1, 1, 4));
        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEnabledState();
            }
        };
        shufflePages.addActionListener(l);
        panel1.add(new JLabel("Preset: "), CC.xy(1, 2));
        panel1.add(preset = new JComboBox(), CC.xyw(2, 2, 3));
        panel1.add(new JLabel("Config string: "), CC.xy(1, 3));
        panel1.add(configString = new JTextField(), CC.xyw(2, 3, 2));
        for (String p : ShuffleRule.predefinedRuleSets) {
            preset.addItem(p.substring(p.indexOf('=') + 1));
        }
        preset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (String p : ShuffleRule.predefinedRuleSets) {
                    if (p.endsWith("=" + (String) preset.getSelectedItem())) {
                        configString.setText(p.substring(0, p.indexOf('=')));
                        parseConfigString();
                    }
                }
            }
        });
        panel1.add(use = new JButton("Use"), CC.xy(4, 3));
        use.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parseConfigString();
            }
        });
        panel1.add(new JSeparator(), CC.xyw(1, 4, 4));
        panel1.add(new JLabel("Each pass covers  "), CC.xy(1, 5));
        panel1.add(pagesPerPass = new JTextField("1"), CC.xy(2, 5));
        panel1.add(new JLabel("  page(s) "), CC.xy(3, 5));
        panel1.add(updatePreview = new JButton("Update"), CC.xy(4, 5));
        updatePreview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parseGUI();
            }
        });
        panel1.add(blockShuffle = new JCheckBox("Shuffle blocks of  "), CC.xy(1, 6));
        panel1.add(blockSize = new JTextField("20"), CC.xy(2, 6));
        panel1.add(new JLabel(" pages individually"), CC.xyw(3, 6, 2));
        blockShuffle.addActionListener(l);
        panel1.add(shuffleRulesTable = new TableComponent(
            new String[]{"Page", "OffsetX", "OffsetY", "ScaleFactor", "Rotate", "NewPageBefore", "FrameWidth"},
            new Class[]{String.class, String.class, String.class, Double.class, String.class, Boolean.class,
                Double.class},
            new Object[]{"+1", "0%", "0%", 1.0, "None", true, 0.0}), CC.xyw(1, 7, 4));
        JComboBox rotateValues = new JComboBox(new String[]{"None", "Left", "Upside-Down", "Right"});
        shuffleRulesTable.getTable().getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(rotateValues));
        shuffleRulesTable.getScrollPane().setPreferredSize(new Dimension(400, 100));
        preset.setSelectedIndex(0);
        updateEnabledState();
    }

    protected void parseGUI() {
        StringBuffer sb = new StringBuffer();
        try {
            int pages = Integer.parseInt(pagesPerPass.getText());
            sb.append(pages);
            if (blockShuffle.isSelected()) {
                sb.append(',').append(Integer.parseInt(blockSize.getText()));
            }
            sb.append(':');
            for (int i = 0; i < shuffleRulesTable.getRowCount(); i++) {
                Object[] row = shuffleRulesTable.getRow(i);
                String tmp = (String) row[0];
                PageBase pb = PageBase.ABSOLUTE;
                if (tmp.startsWith("+")) {
                    pb = PageBase.BEGINNING;
                    tmp = tmp.substring(1);
                } else if (tmp.startsWith("-")) {
                    pb = PageBase.END;
                    tmp = tmp.substring(1);
                }
                int page = Integer.parseInt(tmp);
                if (page <= 0)
                    throw new NumberFormatException((String) row[0]);
                tmp = (String) row[1];
                boolean oxp = false, oyp = false;
                if (tmp.endsWith("%")) {
                    oxp = true;
                    tmp = tmp.substring(0, tmp.length() - 1);
                }
                double ox = Double.parseDouble(tmp);
                tmp = (String) row[2];
                if (tmp.endsWith("%")) {
                    oyp = true;
                    tmp = tmp.substring(0, tmp.length() - 1);
                }
                double oy = Double.parseDouble(tmp);
                double scale = (Double) row[3];
                char rotate = ((String) row[4]).charAt(0);
                boolean npb = (Boolean) row[5];
                double fw = (Double) row[6];
                ShuffleRule rule = new ShuffleRule(npb, pb, page, rotate, scale, ox, oxp, oy, oyp, fw);
                if (i > 0)
                    sb.append(",");
                sb.append(rule.toString());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mf, "Unparsable option: " + ex.getMessage());
            return;
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(mf, "Please fill in all the fields.");
            return;
        }
        configString.setText(sb.toString());
        parseConfigString();
    }

    private void parseConfigString() {
        try {
            parseConfigStringInternal();
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mf, "Unparsable config string: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mf, "Unparsable config string: " + configString.getText());
        }
    }

    private void parseConfigStringInternal() {
        String cstr = configString.getText();
        int[] ppp = new int[2];
        ShuffleRule[] rules = ShuffleRule.parseRuleSet(cstr, ppp);
        int pages = ppp[0], size = ppp[1];
        pp.setConfig(rules);
        pagesPerPass.setText("" + pages);
        if (size == 0) {
            blockShuffle.setSelected(false);
        } else {
            blockShuffle.setSelected(true);
            blockSize.setText("" + size);
        }
        shuffleRulesTable.clear();
        for (int i = 0; i < rules.length; i++) {
            shuffleRulesTable.addRow(rules[i].getPageString(), rules[i].getOffsetXString(), rules[i].getOffsetYString(),
                rules[i].getScale(), rotateName(rules[i].getRotate()), rules[i].isNewPageBefore(),
                rules[i].getFrameWidth());
        }
        shufflePagesPerPass = pages;
        shuffleRules = rules;
        updateEnabledState();
    }

    private String rotateName(char rotate) {
        switch (rotate) {
            case 'N':
                return "None";
            case 'L':
                return "Left";
            case 'R':
                return "Right";
            case 'U':
                return "Upside-Down";
            default:
                throw new IllegalArgumentException();
        }
    }

    private void updateEnabledState() {
        boolean b = shufflePages.isSelected();
        updatePreview.setEnabled(b);
        use.setEnabled(b);
        previewFormat.setEnabled(b);
        preset.setEnabled(b);
        pagesPerPass.setEnabled(b);
        blockShuffle.setEnabled(b);
        blockSize.setEnabled(b && blockShuffle.isSelected());
        configString.setEnabled(b);
        shuffleRulesTable.setEnabled(b);
    }


    public String getTabName() {
        return ("Shuffle / N-up");
    }


    public void checkRun() throws IOException {
        shuffleRulesTable.checkRun("shuffle rule");
        if (blockShuffle.isSelected()) {
            try {
                int shuffleSize = Integer.parseInt(blockSize.getText());
                if (shuffleSize < 2)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                throw new IOException("Invalid shuffle block size");
            }
        }
    }


    public PDFTwist run(PDFTwist pdfTwist, OutputProgressDialog outDialog) throws IOException, DocumentException {
        outDialog.updateJPDFTwistProgress(getTabName());
        if (shufflePages.isSelected()) {
            int shuffleSize = blockShuffle.isSelected() ? Integer.parseInt(blockSize.getText()) : 0;
            try {
                pdfTwist.shufflePages(shufflePagesPerPass, shuffleSize, shuffleRules, outDialog);
            } catch (DocumentException e) {
                outDialog.dispose();
                throw e;
            } catch (IOException e) {
                outDialog.dispose();
                throw e;
            }
        }
        return pdfTwist;
    }

}
