package jpdftweak.tabs.input.treetable.node.factory;

import java.util.ArrayList;
import java.util.List;

import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.factory.event.PageEventListener;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class FileNodeFactory {

	protected boolean optimize;

	private final List<PageEventListener> pageEvents;

	public FileNodeFactory() {
		pageEvents = new ArrayList<PageEventListener>();
	}

	public void addPageEventListener(PageEventListener pageEventListener) {
		pageEvents.add(pageEventListener);
	}

	public void removePageEventListener(PageEventListener pageEventListener) {
		pageEvents.remove(pageEventListener);
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
