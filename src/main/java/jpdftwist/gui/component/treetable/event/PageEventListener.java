package jpdftwist.gui.component.treetable.event;

/**
 * @author Vasilis Naskos
 */
public interface PageEventListener {

    void pageCountChanged(int pages);

    void nextPage(int page);
}
