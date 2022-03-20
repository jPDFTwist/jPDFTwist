package jpdftweak.tabs.watermark;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import jpdftweak.gui.TableComponent;
import jpdftweak.tabs.watermark.optionpanels.WatermarkBatesPanel;
import jpdftweak.tabs.watermark.optionpanels.WatermarkImagePanel;
import jpdftweak.tabs.watermark.optionpanels.WatermarkOptionsPanel;
import jpdftweak.tabs.watermark.optionpanels.WatermarkRepeatedTextPanel;
import jpdftweak.tabs.watermark.optionpanels.WatermarkVariableTextPanel;

/**
 *
 * @author Vasilis Naskos
 */
public class WatermarkTabPanel extends JPanel {

	private TableComponent pageNumberRanges;
	private WatermarkPreviewBox previewBox;
	private final CellConstraints CC;
	private WatermarkOptionsPanel optionsPanel;

	private WatermarkVariableTextPanel variableTextPanel;
	private WatermarkBatesPanel batesPanel;
	private WatermarkRepeatedTextPanel repeatedTextPanel;
	private WatermarkImagePanel imagePanel;
	private JPanel emptyPanel;

	private JPanel styleOptionsPanel;

	private StyleChangeListener styleChangeListener;

	public static WatermarkTabPanel getWatermarkTabPanel() {
		return new WatermarkTabPanel();
	}

	public WatermarkTabPanel() {
		super(new FormLayout("f:p:g, f:p, $lcgap, f:p", "p, $lcgap, f:p:g"));

		CC = new CellConstraints();
		generateUserInterface();
	}

	private void generateUserInterface() {
		initializeComponents();
		positionComponents();
	}

	private void initializeComponents() {
		pageNumberRanges = new TableComponent(new String[] { "Style", "Start Page", "Prefix", "Logical Page" },
				new Class[] { WatermarkStyle.class, Integer.class, String.class, Integer.class },
				new Object[] { new WatermarkStyle(), 1, "", 1 });
		pageNumberRanges.getScrollPane().setPreferredSize(new Dimension(400, 220));

		pageNumberRanges.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			
			public void valueChanged(ListSelectionEvent e) {
				setOptions();
			}
		});

		styleChangeListener = new StyleChangeListener() {

			
			public void styleChanged(int value) {
				replacePanel(value);
				setOptions();
			}
		};

		previewBox = WatermarkPreviewBox.createPreviewBox();

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
		this.add(pageNumberRanges, CC.xyw(1, 1, 2));
		this.add(previewBox, CC.xy(4, 1));
		this.add(optionsPanel, CC.xyw(1, 3, 2));
		this.add(styleOptionsPanel, CC.xy(4, 3));
	}

	private void replacePanel(final int value) {
		SwingUtilities.invokeLater(new Runnable() {

			
			public void run() {
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

				add(styleOptionsPanel, CC.xy(4, 3));
				validate();
				repaint();
			}
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

			// replacePanel(style.getType().getMask());

			switch (style.getType()) {
			case NUMBERS:
				previewBox.setText("1");
				break;
			case LATIN_CAPITAL:
				previewBox.setText("I");
				break;
			case LATIN_LOWERCASE:
				previewBox.setText("i");
				break;
			case CAPITAL_LETTERS:
				previewBox.setText("A");
				break;
			case LOWERCASE_LETTERS:
				previewBox.setText("a");
				break;
			case BATES_NUMBERING:
				previewBox.setText("1");
				break;
			case REPEATED_TEXT:
				previewBox.setText("watermark");
				break;
			case VARIABLE_TEXT:
				previewBox.setText("watermark");
				break;
			}
		} catch (Exception e) {

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
