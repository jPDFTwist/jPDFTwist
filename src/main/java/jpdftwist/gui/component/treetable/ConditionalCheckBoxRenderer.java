package jpdftwist.gui.component.treetable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Vasilis Naskos
 */
public class ConditionalCheckBoxRenderer implements TableCellRenderer {

    public ConditionalCheckBoxRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setForeground(table.getSelectionForeground());
        panel.setBackground(table.getSelectionBackground());

        if (value instanceof Boolean) {
            JCheckBox jcb = new JCheckBox();

            jcb.setOpaque(false);
            jcb.setContentAreaFilled(false);
            jcb.setMargin(new Insets(0, 0, 0, 0));

            panel.setLayout(new GridBagLayout());
            panel.add(jcb);

            jcb.setSelected((Boolean) value);
        }

        return panel;
    }
}