// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.bom.drop;

import org.zkoss.zul.Treerow;
import org.zkoss.zul.Treeitem;

public interface IRendererListener
{
    void render(final Treeitem p0, final Treerow p1, final ISupportRadioNode p2, final int p3);
    
    void onchecked(final Treeitem p0, final ISupportRadioNode p1, final boolean p2);
}
