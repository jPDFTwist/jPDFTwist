package jpdftwist.tabs;

import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.tabparams.OutputParameters;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.dialog.OutputProgressDialog;
import jpdftwist.gui.tab.output.OutputTab;
import jpdftwist.tabs.input.InputValidator;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputTabActions extends ActionTab {

	private static final String TAB_NAME = "Output";
	private final OutputTab outputTab;
	private final MainWindow mainWindow;
	private OutputParameters parameters;

	public OutputTabActions(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		outputTab = new OutputTab();
		outputTab.setTempFileListener(
				e -> mainWindow.getInputTab().setUseTempFiles(outputTab.isTempFilesComboBoxSelected()));
	}

	@Override
	public String getTabName() {
		return TAB_NAME;
	}

	@Override
	public JPanel getUserInterface() {
		return outputTab;
	}

	@Override
	public void checkRun() {
		try {
			parameters = outputTab.getParameters();
			if (parameters.getOutputFilepath().length() == 0)
			    showException(new IOException("No output file selected !"), "No output file selected !");
				return;
		} catch (Exception ex) {
		}

		String outputFileName = parameters.getOutputFilepath();
		if (mainWindow.getInputTab().getBatchLength() > 1) {
			if (!outputFileName.contains("<F>") && !outputFileName.contains("<FX>") && !outputFileName.contains("<P>")
					&& !outputFileName.contains("<#>")) {
				showException(new IOException("Variables in output file name required for batch mode"),
						"Variables in output file name required for batch mode");
			}
		}
	}

	@Override
	public PDFTwist run(PDFTwist input, OutputEventListener outputEventListener,
			OutputProgressDialog outputProgressDialog) {
		outputEventListener.updateJPDFTwistProgress(getTabName());
		outputEventListener.setAction("Producing output file(s)");
		if (parameters.isPageMarks()) {
			if (parameters.isUncompressed()) {
				input.addPageMarks();
			} else {
				input.removePageMarks();
			}
		}

		try {
			input.setPdfImages(parameters.getPdfToImage());
			input.writeOutput(parameters.getOutputFilepath(), parameters.isMultiPageTiff(), parameters.isBurst(),
					parameters.isUncompressed(), parameters.isOptimizeSize(), parameters.isFullyCompressed());
		} catch (Exception ex) {
			Logger.getLogger(OutputTabActions.class.getName()).log(Level.SEVERE, "Ex029", ex);
		}

		return input;
	}

	private void showException(Exception ex, String message) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
