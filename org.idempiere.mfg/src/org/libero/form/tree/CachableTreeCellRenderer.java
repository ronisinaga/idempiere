// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form.tree;

import java.awt.Component;
import javax.swing.JTree;
import java.util.HashMap;
import javax.swing.tree.DefaultTreeCellRenderer;

public abstract class CachableTreeCellRenderer extends DefaultTreeCellRenderer
{
    private boolean virtual;
    private HashMap cache;
    private CachableTreeCellRenderer complement;
    
    protected abstract void init(final Object p0);
    
    public CachableTreeCellRenderer() {
        this(false);
    }
    
    public CachableTreeCellRenderer(final boolean virtual) {
        this.virtual = virtual;
        this.cache = new HashMap();
    }
    
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        String name = (String)this.getFromCache(value);
        if (name == null) {
            this.init(value);
            name = (String)this.getFromCache(value);
        }
        this.setName(name);
        return this;
    }
    
    public boolean isInitialized() {
        return !this.cache.isEmpty();
    }
    
    public void addToCache(final Object key, final Object value) {
        this.cache.put(key, value);
    }
    
    public Object getFromCache(final Object key) {
        return this.cache.get(key);
    }
    
    public boolean isVirtual() {
        return this.virtual;
    }
    
    public void setVirtual(final boolean on) {
        this.virtual = on;
    }
}
