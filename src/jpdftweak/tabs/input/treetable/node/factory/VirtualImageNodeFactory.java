package jpdftweak.tabs.input.treetable.node.factory;

import com.itextpdf.text.Rectangle;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpdftweak.core.IntegerList;
import jpdftweak.core.UnitTranslator;
import jpdftweak.tabs.input.treetable.UserObjectValue;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.VirtualFileUserObject;
import jpdftweak.utils.ImageParser;

/**
 *
 * @author Vasilis Naskos
 */
public class VirtualImageNodeFactory extends FileNodeFactory {
    
    private String filepath;
    private String srcFile;
    private int repeat;

    @Override
    public Node getFileNode(String filepath) {
        this.filepath = filepath;

        VirtualFileUserObject pdfUO = createVirtualPdfUserObject();

        try {
            Node file = new Node(pdfUO);
            insertPages(file);
            return file;
        } catch (IOException ex) {
            Logger.getLogger(VirtualImageNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
            //TODO throw excpetion
        }

        return null;
    }

    private VirtualFileUserObject createVirtualPdfUserObject() {
        VirtualFileUserObject imgUO = new VirtualFileUserObject(
                filepath,
                FileUserObject.SubType.IMAGE,
                "");

        imgUO.setValueAt(repeat, UserObjectValue.PAGES);
        imgUO.setValueAt(1, UserObjectValue.FROM);
        imgUO.setValueAt(repeat, UserObjectValue.TO);
        imgUO.setValueAt(true, UserObjectValue.EVEN);
        imgUO.setValueAt(true, UserObjectValue.ODD);
        imgUO.setValueAt(new IntegerList("0"), UserObjectValue.EMPTY_BEFORE);
        imgUO.setValueAt(0, UserObjectValue.BOOKMARK_LEVEL);

        return imgUO;
    }

    private void insertPages(Node file) throws IOException {
        updateListenersPageCount(repeat);

        PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();
        ImageParser.ImageObject imageObj = ImageParser.tryToReadImage(srcFile);
        
        if (imageObj == null || imageObj.getImage() == null) {
            throw new IOException(
                    String.format("Image %s\n not supported or corrupted!",
                            srcFile));
        }
        
        Rectangle size = new Rectangle(
                (float) UnitTranslator.pixelsToPoints(imageObj.getWidth(), 72),
                (float) UnitTranslator.pixelsToPoints(imageObj.getHeight(), 72));
        
        for (int i = 1; i <= repeat; i++) {
            updateListenersNextPage(i);

            pageNodeFactory.setSize(size);
            pageNodeFactory.setColorDepth(imageObj.getDepth());
            Node page = pageNodeFactory.getPageNode(i);

            file.insert(page, i - 1);
        }
    }

    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
    
}
