package jpdftweak.tabs;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import jpdftweak.core.PageRange;
import jpdftweak.core.PdfBookmark;
import jpdftweak.core.PdfTweak;
import jpdftweak.tabs.input.InputTabPanel;
import jpdftweak.tabs.input.InputValidator;
import jpdftweak.tabs.input.ModelReader;
import jpdftweak.tabs.input.pagerange.PageRangeGenerator;
import jpdftweak.tabs.input.preview.PreviewHandler;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FolderUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;

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
    
    @Override
    public String getTabName() {
        return TAB_NAME;
    }

    @Override
    public JPanel getUserInterface() {
        inputTabPanel = InputTabPanel.getInputPanel();
        
        return inputTabPanel;
    }
    
    @Override
    public void checkRun() {
        collectInputTabInfo();
        validateCollectedInfo();
    }
    
    private void collectInputTabInfo() {
        isModelEmpty = inputTabPanel.isModelEmpty();
        mergeByDir = inputTabPanel.isMergeByDirSelected();
        batch = inputTabPanel.isBatchSelected();
        interleave = inputTabPanel.isInterleaveSelected();
        
        if(interleave)
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
        while(e.hasMoreElements()) {
            Node child = (Node) e.nextElement();
            if(UserObjectType.isFile(child)) {
                filepaths.add(child.getUserObject().getKey());
            } else if(child.getUserObject() instanceof FolderUserObject) {
                filepaths.addAll(getFilePaths(child));
            }
        }
        
        return filepaths;
    }

    @Override
    public PdfTweak run(PdfTweak input) {
        List<PageRange> ranges = generatePageRanges(batchTaskSelection, batch, mergeByDir);
        
        int n = 0;
        if (batch) {
            n = batchTaskSelection;
        }

        try {
            return new PdfTweak(ranges, useTempFiles, mergeByDir, interleaveSize);
        } catch (Exception ex) {
            Logger.getLogger(InputTab.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private List<PageRange> generatePageRanges(int taskIndex, boolean batch, boolean merge) {
        if(generator == null) {
            generator = PageRangeGenerator.initGenerator(inputTabPanel.getRootNode(), batch, merge);
        }
        
        return generator.generate(taskIndex);
    }
    
    //TODO
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
    
    public void setPreviewHandler(PreviewHandler previewHandler) {
        inputTabPanel.setPreviewHandler(previewHandler);
    }
}
