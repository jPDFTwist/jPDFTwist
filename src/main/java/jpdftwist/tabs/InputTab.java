package jpdftwist.tabs;

import jpdftwist.core.PDFTwist;
import jpdftwist.core.PageRange;
import jpdftwist.core.PdfBookmark;
import jpdftwist.gui.components.treetable.Node;
import jpdftwist.gui.components.treetable.TreeTableRowType;
import jpdftwist.gui.components.treetable.row.FolderTreeTableRow;
import jpdftwist.tabs.input.InputTabPanel;
import jpdftwist.tabs.input.InputValidator;
import jpdftwist.tabs.input.ModelReader;
import jpdftwist.tabs.input.pagerange.PageRangeGenerator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vasilis Naskos
 */
public class InputTab extends ActionTab {

	private static final String TAB_NAME = "Input";
	private InputTabPanel inputTabPanel;
	private boolean isModelEmpty, mergeByDir, batch, interleave, useTempFiles;
	private int interleaveSize;
	private int batchTaskSelection;
	private PageRangeGenerator generator;

	
	public String getTabName() {
		return TAB_NAME;
	}

	
	/**
	 * @wbp.parser.entryPoint
	 */
	public JPanel getUserInterface() {
		inputTabPanel = InputTabPanel.getInputPanel();

		return inputTabPanel;
	}

	
	/**
	 * @wbp.parser.entryPoint
	 */
	
	public void checkRun() {
		collectInputTabInfo();
		validateCollectedInfo();
	}

	private void collectInputTabInfo() {
		isModelEmpty = inputTabPanel.isModelEmpty();
		mergeByDir = inputTabPanel.isMergeByDirSelected();
		batch = inputTabPanel.isBatchSelected();
		interleave = inputTabPanel.isInterleaveSelected();

		if (interleave)
			interleaveSize = inputTabPanel.getInterleaveSize();
		else
			interleaveSize = 0;

		ModelReader reader = inputTabPanel.getModelReader();
	}

	private void validateCollectedInfo() {
		InputValidator validator = new InputValidator();
		List<PageRange> ranges = generatePageRangesForCheck();

		validator.setIsModelEmpty(isModelEmpty);
		validator.setInterleave(interleave);
		validator.setInterleaveSize(interleaveSize);
		validator.setPageRanges(ranges);

		validator.checkValidity();
	}

	private List<PageRange> generatePageRangesForCheck() {
		List<PageRange> ranges = generatePageRanges(0, false, false);
		generator = null;
		return ranges;
	}

	private List<String> getFilePaths(Node node) {
		List<String> filepaths = new ArrayList<String>();

		Enumeration e = node.children();
		while (e.hasMoreElements()) {
			Node child = (Node) e.nextElement();
			if (TreeTableRowType.isFile(child)) {
				filepaths.add(child.getUserObject().getKey());
			} else if (child.getUserObject() instanceof FolderTreeTableRow) {
				filepaths.addAll(getFilePaths(child));
			}
		}

		return filepaths;
	}

	
	/**
	 * @wbp.parser.entryPoint
	 */
	public PDFTwist run(PDFTwist input) {
		List<PageRange> ranges = generatePageRanges(batchTaskSelection, batch, mergeByDir);

		int n = 0;
		if (batch) {
			n = batchTaskSelection;
		}

		try {
			return new PDFTwist(ranges, useTempFiles, mergeByDir, interleaveSize);
		} catch (Exception ex) {
			Logger.getLogger(InputTab.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	private List<PageRange> generatePageRanges(int taskIndex, boolean batch, boolean merge) {
		if (generator == null) {
			generator = PageRangeGenerator.initGenerator(inputTabPanel.getRootNode(), batch, merge);
		}

		return generator.generate(taskIndex);
	}

	// TODO
	public void selectBatchTask(int batchTaskSelection) {
		this.batchTaskSelection = batchTaskSelection;
	}

	public int getBatchLength() {
		return inputTabPanel.getBatchLength();
	}

	public List<PdfBookmark> loadBookmarks() {
		//        if (model.isEmpty()) {
		//            return Collections.EMPTY_LIST;
		//        }

		List<PageRange> ranges = generatePageRangesForCheck();
		return PdfBookmark.buildBookmarks(ranges);
	}

	public void setUseTempFiles(boolean useTempFiles) {
		this.useTempFiles = useTempFiles;
		inputTabPanel.setUseTempFiles(useTempFiles);
	}
}
