package jpdftweak.tabs.input.pagerange;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import jpdftweak.core.PageRange;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;

/**
 *
 * @author Vasilis Naskos
 */
public class MergePageRangeGenerator extends PageRangeGenerator {

    private final List<Node> folders;
    
    public MergePageRangeGenerator(Node root) {
        folders = getFolders(root);
    }

    @Override
    public List<PageRange> generate(int taskIndex) {
        List<PageRange> ranges = new ArrayList<PageRange>();
        
        Node parent = folders.get(taskIndex);
        
        Enumeration e = parent.children();
        while(e.hasMoreElements()) {
            Node node = (Node) e.nextElement();
            if(UserObjectType.isFile(node)) {
                PageRange pageRange = getPageRange(node);
                if (pageRange != null) {
                    ranges.add(pageRange);
                }
            }
        }
        
        return ranges;
    }
    
    private List<Node> getFolders(Node parent) {
        List<Node> folderNodes = new ArrayList<Node>();

        Enumeration e = parent.children();
        while (e.hasMoreElements()) {
            Node child = (Node) e.nextElement();
            if (child.getUserObject().getType() == UserObjectType.FOLDER) {
                folderNodes.add(child);
                folderNodes.addAll(getFolders(child));
            }
        }

        return folderNodes;
    }
    
}
