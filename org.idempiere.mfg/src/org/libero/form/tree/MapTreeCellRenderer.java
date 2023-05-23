// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form.tree;

import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JTree;
import java.util.Map;
import javax.swing.ImageIcon;
import java.util.HashMap;
import javax.swing.tree.DefaultTreeCellRenderer;

public abstract class MapTreeCellRenderer extends DefaultTreeCellRenderer
{
    private HashMap<Object, Object> map;
    
    protected abstract ImageIcon getIcon(final Object p0);
    
    public MapTreeCellRenderer(final HashMap<?, ?> map) {
        (this.map = new HashMap<Object, Object>()).putAll(map);
    }
    
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        final String name = (String)this.getMapping(value);
        this.setText(name);
        final ImageIcon icon = this.getIcon(value);
        this.setIcon(icon);
        return this;
    }
    
    protected Object getMapping(final Object value) {
        return this.map.get(value);
    }
}
