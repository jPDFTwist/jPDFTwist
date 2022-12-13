package jpdftwist.tabs.input;

import jpdftwist.gui.component.treetable.Node;

import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public interface ModelReader {

	public List<Node> getFolderNodes();

	public List<Node> getFileNodes();

}
