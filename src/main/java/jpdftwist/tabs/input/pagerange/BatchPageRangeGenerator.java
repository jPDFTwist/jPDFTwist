package jpdftwist.tabs.input.pagerange;

import jpdftwist.core.PageRange;
import jpdftwist.core.input.InputElementType;
import jpdftwist.gui.component.treetable.Node;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Vasilis Naskos
 */
public class BatchPageRangeGenerator extends PageRangeGenerator {

    private final List<PageRange> pageRanges;

    public BatchPageRangeGenerator(Node root) {
        pageRanges = getPageRanges(root);
    }

    public List<PageRange> generate(int taskIndex) {
        List<PageRange> ranges = new ArrayList<>();

        PageRange range = pageRanges.get(taskIndex);
        ranges.add(range);

        return ranges;
    }

    private List<PageRange> getPageRanges(Node parent) {
        List<PageRange> pageRanges = new ArrayList<>();

        Enumeration<? extends MutableTreeTableNode> e = parent.children();
        while (e.hasMoreElements()) {
            Node child = (Node) e.nextElement();
            if (InputElementType.isFile(child)) {
                PageRange pageRange = getPageRange(child);
                if (pageRange != null) {
                    pageRanges.add(pageRange);
                }
            } else if (child.getUserObject().getType() == InputElementType.FOLDER) {
                pageRanges.addAll(getPageRanges(child));
            }
        }

        return pageRanges;
    }
}
