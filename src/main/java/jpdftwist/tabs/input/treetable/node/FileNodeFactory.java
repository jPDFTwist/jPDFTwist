package jpdftwist.tabs.input.treetable.node;

import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.event.PageEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vasilis Naskos
 */
public abstract class FileNodeFactory {

    protected boolean optimize;

    private final List<PageEventListener> pageEvents;

    public FileNodeFactory() {
        pageEvents = new ArrayList<>();
    }

    public void addPageEventListener(PageEventListener pageEventListener) {
        pageEvents.add(pageEventListener);
    }

    protected void updateListenersPageCount(int pages) {
        for (PageEventListener listener : pageEvents) {
            listener.pageCountChanged(pages);
        }
    }

    protected void updateListenersNextPage(int page) {
        for (PageEventListener listener : pageEvents) {
            listener.nextPage(page);
        }
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public abstract Node getFileNode(String filepath);

}
