// 
// Decompiled by Procyon v0.5.36
// 

package org.adempiere.model.engines;

import java.math.BigDecimal;
import java.util.Properties;

public interface IDocumentLine
{
    Properties getCtx();
    
    String get_TrxName();
    
    String get_TableName();
    
    int get_ID();
    
    int getAD_Client_ID();
    
    int getAD_Org_ID();
    
    int getM_Product_ID();
    
    String getDescription();
    
    int getM_Locator_ID();
    
    void setM_Locator_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    BigDecimal getMovementQty();
}
