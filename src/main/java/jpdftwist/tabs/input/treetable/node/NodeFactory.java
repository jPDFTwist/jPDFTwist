package jpdftwist.tabs.input.treetable.node;

import jpdftwist.core.input.FileInputElement;
import jpdftwist.core.input.InputElementType;

/**
 * @author Vasilis Naskos
 */
public class NodeFactory {

    public static FileNodeFactory getFileNodeFactory(InputElementType type, FileInputElement.SubType subType) {
        switch (type) {
            case REAL_FILE:
                return getRealFileNodeFactory(subType);
            case VIRTUAL_FILE:
                return getVirtualFileNodeFactory(subType);
            default:
                return null;
        }
    }

    private static FileNodeFactory getRealFileNodeFactory(FileInputElement.SubType subType) {
        switch (subType) {
            case PDF:
                return new RealPdfNodeFactory();
            case IMAGE:
                return new RealImageNodeFactory();
            default:
                return null;
        }
    }

    private static FileNodeFactory getVirtualFileNodeFactory(FileInputElement.SubType subType) {
        switch (subType) {
            case PDF:
                return new VirtualPdfNodeFactory();
            case IMAGE:
                return new VirtualImageNodeFactory();
            case BLANK:
                return new VirtualBlankNodeFactory();
            default:
                return null;
        }
    }

    public static PageNodeFactory getPageNodeFactory() {
        return new PageNodeFactory();
    }

}
