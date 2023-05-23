// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form.crp;

import org.libero.model.MPPOrderNode;
import org.libero.model.MPPOrder;
import org.compiere.util.Env;
import java.util.Date;
import org.compiere.model.MResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.JTree;
import java.util.HashMap;
import org.libero.form.tree.MapTreeCellRenderer;

public class DiagramTreeCellRenderer extends MapTreeCellRenderer
{
    private static final long serialVersionUID = 1L;
    
    public DiagramTreeCellRenderer(final HashMap<?, ?> map) {
        super(map);
    }
    
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        final Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        final String name = (String)this.getMapping(value);
        final ImageIcon icon = this.getIcon(value);
        if (this.isNotAvailable(name)) {
            final int x1 = this.getFontMetrics(this.getFont()).stringWidth(name) + icon.getIconWidth();
            final JLabel l = new JLabel(name.substring(1, name.length() - 1), icon, 2) {
                private static final long serialVersionUID = 1L;
                
                @Override
                public void paint(final Graphics g) {
                    super.paint(g);
                    final int y = this.getFont().getSize() / 2;
                    g.drawLine(0, y, x1, y);
                }
            };
            l.setFont(this.getFont());
            return l;
        }
        return c;
    }
    
    private boolean isNotAvailable(final String value) {
        return value.startsWith("{") && value.endsWith("}");
    }
    
    @Override
    protected ImageIcon getIcon(final Object value) {
        ImageIcon icon = null;
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if (!(node.getUserObject() instanceof MResource)) {
            if (node.getUserObject() instanceof Date) {
                icon = Env.getImageIcon("Calendar10.gif");
            }
            else if (!(node.getUserObject() instanceof MPPOrder)) {
                final boolean b = node.getUserObject() instanceof MPPOrderNode;
            }
        }
        return icon;
    }
}
