// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_M_AttributeSetInstance;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Cost_CollectorMA
{
    public static final String Table_Name = "PP_Cost_CollectorMA";
    public static final int Table_ID = 53062;
    public static final KeyNamePair Model = new KeyNamePair(53062, "PP_Cost_CollectorMA");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
    public static final String COLUMNNAME_MovementQty = "MovementQty";
    public static final String COLUMNNAME_PP_Cost_Collector_ID = "PP_Cost_Collector_ID";
    public static final String COLUMNNAME_PP_Cost_CollectorMA_ID = "PP_Cost_CollectorMA_ID";
    public static final String COLUMNNAME_PP_Cost_CollectorMA_UU = "PP_Cost_CollectorMA_UU";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;
    
    void setMovementQty(final BigDecimal p0);
    
    BigDecimal getMovementQty();
    
    void setPP_Cost_Collector_ID(final int p0);
    
    int getPP_Cost_Collector_ID();
    
    I_PP_Cost_Collector getPP_Cost_Collector() throws RuntimeException;
    
    void setPP_Cost_CollectorMA_ID(final int p0);
    
    int getPP_Cost_CollectorMA_ID();
    
    void setPP_Cost_CollectorMA_UU(final String p0);
    
    String getPP_Cost_CollectorMA_UU();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
