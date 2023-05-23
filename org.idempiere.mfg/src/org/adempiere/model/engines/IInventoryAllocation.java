// 
// Decompiled by Procyon v0.5.36
// 

package org.adempiere.model.engines;

import java.math.BigDecimal;

public interface IInventoryAllocation
{
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    int getM_AttributeSetInstance_ID();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    BigDecimal getMovementQty();
    
    void setMovementQty(final BigDecimal p0);
}
