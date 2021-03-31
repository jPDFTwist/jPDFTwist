package jpdftweak.tabs.input;

import java.util.List;

import jpdftweak.tabs.input.treetable.node.Node;

/**
 *
 * @author Vasilis Naskos
 */
public interface ModelReader {

	public List<Node> getFolderNodes();

	public List<Node> getFileNodes();

}
