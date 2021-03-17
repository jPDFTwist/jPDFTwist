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
public class BatchPageRangeGenerator extends PageRangeGenerator {

    private final List<PageRange> pageRanges;
    
    public BatchPageRangeGenerator(Node root) {
        pageRanges = getPageRanges(root);
    }

    @Override
    public List<PageRange> generate(int taskIndex) {
        List<PageRange> ranges = new ArrayList<PageRange>();
        
        PageRange range = pageRanges.get(taskIndex);
        ranges.add(range);
        
        return ranges;
    }
    
    private List<PageRange> getPageRanges(Node parent) {
        List<PageRange> pageRanges = new ArrayList<PageRange>();

        Enumeration e = parent.children();
        while (e.hasMoreElements()) {
            Node child = (Node) e.nextElement();
            if (UserObjectType.isFile(child)) {
                PageRange pageRange = getPageRange(child);
                if (pageRange != null) {
                    pageRanges.add(pageRange);
                }
            } else if (child.getUserObject().getType() == UserObjectType.FOLDER) {
                pageRanges.addAll(getPageRanges(child));
            }
        }

        return pageRanges;
    }
    
}
