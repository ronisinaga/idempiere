// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import java.sql.Timestamp;
import org.compiere.model.I_AD_PInstance;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_T_MRP_CRP
{
    public static final String Table_Name = "T_MRP_CRP";
    public static final int Table_ID = 53044;
    public static final KeyNamePair Model = new KeyNamePair(53044, "T_MRP_CRP");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_PInstance_ID = "AD_PInstance_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_SeqNo = "SeqNo";
    public static final String COLUMNNAME_T_MRP_CRP_ID = "T_MRP_CRP_ID";
    public static final String COLUMNNAME_T_MRP_CRP_UU = "T_MRP_CRP_UU";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_PInstance_ID(final int p0);
    
    int getAD_PInstance_ID();
    
    I_AD_PInstance getAD_PInstance() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setSeqNo(final int p0);
    
    int getSeqNo();
    
    void setT_MRP_CRP_ID(final int p0);
    
    int getT_MRP_CRP_ID();
    
    void setT_MRP_CRP_UU(final String p0);
    
    String getT_MRP_CRP_UU();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
