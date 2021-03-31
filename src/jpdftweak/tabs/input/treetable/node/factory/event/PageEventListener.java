package jpdftweak.tabs.input.treetable.node.factory.event;

/**
 *
 * @author Vasilis Naskos
 */
public interface PageEventListener {

	void pageCountChanged(int pages);

	void nextPage(int page);
}
