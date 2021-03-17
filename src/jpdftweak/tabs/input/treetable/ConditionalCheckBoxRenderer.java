package jpdftweak.tabs.input.treetable;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Vasilis Naskos
 */
public class ConditionalCheckBoxRenderer implements TableCellRenderer {

    public ConditionalCheckBoxRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setForeground(table.getSelectionForeground());
        panel.setBackground(table.getSelectionBackground());
        
        
        if (value instanceof Boolean) {
            if (value != "") {
                JCheckBox jcb = new JCheckBox();
                
                jcb.setOpaque(false);
                jcb.setContentAreaFilled(false);
                jcb.setMargin(new Insets(0, 0, 0, 0));
                
                panel.setLayout(new GridBagLayout());
                panel.add(jcb);
                
                jcb.setSelected((Boolean) value);
            }
        }

        return panel;
    }
}