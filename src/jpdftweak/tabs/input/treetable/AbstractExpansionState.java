package jpdftweak.tabs.input.treetable;

import javax.swing.JComponent;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class AbstractExpansionState {
  /**
   * The component we want to manipulate expansion state of.
   */
  protected JComponent associatedComponent ;
  
  public AbstractExpansionState(JComponent aComponent){
    associatedComponent = aComponent;
  }
  /**
   * Stores the expansion state of the JComponent.
   */
  abstract public void store();
  /**
   * Restore expansion state of the JComponent.
   */
  abstract public void restore();

  public JComponent getAssociatedComponent() {
    return associatedComponent;
  }

  public void setAssociatedComponent(JComponent associatedObject) {
    this.associatedComponent = associatedObject;
  }
}