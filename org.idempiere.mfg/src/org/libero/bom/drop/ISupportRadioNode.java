// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.bom.drop;

public interface ISupportRadioNode
{
    boolean isLeaf();
    
    ISupportRadioNode getChild(final int p0);
    
    int getChildCount();
    
    boolean isRadio();
    
    String getGroupName();
    
    String getLabel();
    
    boolean isChecked();
    
    boolean isDisable();
    
    void setIsChecked(final boolean p0);
    
    void setIsDisable(final boolean p0);
}
