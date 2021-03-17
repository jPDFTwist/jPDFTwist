package jpdftweak.tabs.input.treetable.node.factory;

import com.itextpdf.text.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpdftweak.core.IntegerList;
import jpdftweak.core.UnitTranslator;
import jpdftweak.tabs.input.treetable.UserObjectValue;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.RealFileUserObject;
import jpdftweak.utils.ImageParser;

/**
 *
 * @author Vasilis Naskos
 */
public class RealImageNodeFactory extends FileNodeFactory {

    private String filepath;

    @Override
    public Node getFileNode(String filepath) {
        this.filepath = filepath;

        RealFileUserObject pdfUO = createRealPdfUserObject();
        
        try {
            Node file = new Node(pdfUO);
            insertPages(file);
            return file;
        } catch (IOException ex) {
            Logger.getLogger(RealImageNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
            //TODO throw exception
        }
        
        return null;
    }

    private RealFileUserObject createRealPdfUserObject() {
        long fileSize = new File(filepath).length();

        RealFileUserObject imgUO = new RealFileUserObject(
                filepath,
                FileUserObject.SubType.IMAGE);

        imgUO.setValueAt(fileSize, UserObjectValue.SIZE);
        imgUO.setValueAt(1, UserObjectValue.PAGES);
        imgUO.setValueAt(1, UserObjectValue.FROM);
        imgUO.setValueAt(1, UserObjectValue.TO);
        imgUO.setValueAt(true, UserObjectValue.EVEN);
        imgUO.setValueAt(true, UserObjectValue.ODD);
        imgUO.setValueAt(new IntegerList("0"), UserObjectValue.EMPTY_BEFORE);
        imgUO.setValueAt(0, UserObjectValue.BOOKMARK_LEVEL);

        return imgUO;
    }

    private void insertPages(Node file) throws IOException {
        updateListenersPageCount(1);

        PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();
        ImageParser.ImageObject imageObj = ImageParser.tryToReadImage(filepath);

        if (imageObj == null || imageObj.getImage() == null) {
            throw new IOException(
                    String.format("Image %s\n not supported or corrupted!",
                            filepath));
        }

        Rectangle size = new Rectangle(
                (float) UnitTranslator.pixelsToPoints(imageObj.getWidth(), 72),
                (float) UnitTranslator.pixelsToPoints(imageObj.getHeight(), 72));
        
        updateListenersNextPage(1);

        pageNodeFactory.setSize(size);
        Node page = pageNodeFactory.getPageNode(1);

        file.insert(page, 0);
    }
}
