package jpdftweak.tabs.input.pagerange;

import java.util.List;
import jpdftweak.core.PageRange;
import jpdftweak.tabs.input.treetable.node.Node;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class PageRangeGenerator {
    
    public static PageRangeGenerator initGenerator(Node root, boolean batch, boolean mergeByDir) {
        if(batch)
            return new BatchPageRangeGenerator(root);
        else if(mergeByDir)
            return new MergePageRangeGenerator(root);
        else
            return new MultiPageRangeGenerator(root);
    }
    
    public abstract List<PageRange> generate(int taskIndex);
    
    public PageRange getPageRange(Node node) {
        if (node.getUserObject() == null) {
            return null;
        }

        return new PageRange(node);
        
    }
}
