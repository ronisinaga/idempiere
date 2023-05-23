// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.bom.drop;

import org.zkoss.zul.AbstractTreeModel;

public class SupportRadioTreeModel extends AbstractTreeModel<ISupportRadioNode>
{
    private static final long serialVersionUID = -4260907076488563930L;
    
    public SupportRadioTreeModel(final ISupportRadioNode root) {
        super(root);
    }
    
    public boolean isLeaf(final ISupportRadioNode node) {
        return node.isLeaf();
    }
    
    public ISupportRadioNode getChild(final ISupportRadioNode parent, final int index) {
        return parent.getChild(index);
    }
    
    public int getChildCount(final ISupportRadioNode parent) {
        return parent.getChildCount();
    }
}
