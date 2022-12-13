package jpdftwist.gui.components.treetable.event;

/**
 * @author Vasilis Naskos
 */
public interface PageEventListener {

	void pageCountChanged(int pages);

	void nextPage(int page);
}
