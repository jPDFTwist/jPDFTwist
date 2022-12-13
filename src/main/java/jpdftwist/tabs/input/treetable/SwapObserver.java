package jpdftwist.tabs.input.treetable;

import jpdftwist.tabs.input.treetable.node.Node;

/**
 *
 * @author Vasilis Naskos
 */
public interface SwapObserver {

	public void notify(Node node, int index);

}
