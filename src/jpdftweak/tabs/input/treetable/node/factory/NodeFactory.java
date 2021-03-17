package jpdftweak.tabs.input.treetable.node.factory;

import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;

/**
 *
 * @author Vasilis Naskos
 */
public class NodeFactory {
    
    public static FileNodeFactory getFileNodeFactory(
            UserObjectType type, FileUserObject.SubType subType) {
        switch(type) {
            case REAL_FILE:
                return getRealFileNodeFactory(subType);
            case VIRTUAL_FILE:
                return getVirtualFileNodeFactory(subType);
            default:
                return null; 
        }
    }
    
    private static FileNodeFactory getRealFileNodeFactory(
            FileUserObject.SubType subType) {
        switch(subType) {
            case PDF:
                return new RealPdfNodeFactory();
            case IMAGE:
                return new RealImageNodeFactory();
            default:
                return null;
        }
    }
    
    private static FileNodeFactory getVirtualFileNodeFactory(
            FileUserObject.SubType subType) {
        switch(subType) {
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
        return new DefaultPageNodeFactory();
    }
    
}
