package jpdftwist.tabs;

import jpdftwist.core.PDFTwist;
import jpdftwist.core.PageRange;
import jpdftwist.core.PdfBookmark;
import jpdftwist.tabs.input.InputValidator;
import jpdftwist.tabs.input.pagerange.PageRangeGenerator;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class InputTabActions extends ActionTab {

    private static final String TAB_NAME = "Input";
    private jpdftwist.gui.tab.input.InputTab inputTab;
    private boolean isModelEmpty, mergeByDir, batch, interleave, useTempFiles;
    private int interleaveSize;
    private int batchTaskSelection;
    private PageRangeGenerator generator;

    public String getTabName() {
        return TAB_NAME;
    }

    public JPanel getUserInterface() {
        inputTab = jpdftwist.gui.tab.input.InputTab.getInputPanel();

        return inputTab;
    }

    public void checkRun() {
        collectInputTabInfo();
        validateCollectedInfo();
    }

    private void collectInputTabInfo() {
        isModelEmpty = inputTab.isModelEmpty();
        mergeByDir = inputTab.isMergeByDirSelected();
        batch = inputTab.isBatchSelected();
        interleave = inputTab.isInterleaveSelected();

        if (interleave)
            interleaveSize = inputTab.getInterleaveSize();
        else
            interleaveSize = 0;
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

    public PDFTwist run(PDFTwist input) {
        List<PageRange> ranges = generatePageRanges(batchTaskSelection, batch, mergeByDir);

        int n = 0;
        if (batch) {
            n = batchTaskSelection;
        }

        try {
            return new PDFTwist(ranges, useTempFiles, mergeByDir, interleaveSize);
        } catch (Exception ex) {
            Logger.getLogger(InputTabActions.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private List<PageRange> generatePageRanges(int taskIndex, boolean batch, boolean merge) {
        if (generator == null) {
            generator = PageRangeGenerator.initGenerator(inputTab.getRootNode(), batch, merge);
        }

        return generator.generate(taskIndex);
    }

    // TODO
    public void selectBatchTask(int batchTaskSelection) {
        this.batchTaskSelection = batchTaskSelection;
    }

    public int getBatchLength() {
        return inputTab.getBatchLength();
    }

    public List<PdfBookmark> loadBookmarks() {
        List<PageRange> ranges = generatePageRangesForCheck();
        return PdfBookmark.buildBookmarks(ranges);
    }

    public void setUseTempFiles(boolean useTempFiles) {
        this.useTempFiles = useTempFiles;
        inputTab.setUseTempFiles(useTempFiles);
    }
}
