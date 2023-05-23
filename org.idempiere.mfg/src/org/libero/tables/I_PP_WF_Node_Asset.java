// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import java.sql.Timestamp;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.I_A_Asset;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_WF_Node_Asset
{
    public static final String Table_Name = "PP_WF_Node_Asset";
    public static final int Table_ID = 53017;
    public static final KeyNamePair Model = new KeyNamePair(53017, "PP_WF_Node_Asset");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_A_Asset_ID = "A_Asset_ID";
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_WF_Node_ID = "AD_WF_Node_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_PP_WF_Node_Asset_ID = "PP_WF_Node_Asset_ID";
    public static final String COLUMNNAME_PP_WF_Node_Asset_UU = "PP_WF_Node_Asset_UU";
    public static final String COLUMNNAME_SeqNo = "SeqNo";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    void setA_Asset_ID(final int p0);
    
    int getA_Asset_ID();
    
    I_A_Asset getA_Asset() throws RuntimeException;
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_WF_Node_ID(final int p0);
    
    int getAD_WF_Node_ID();
    
    I_AD_WF_Node getAD_WF_Node() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setPP_WF_Node_Asset_ID(final int p0);
    
    int getPP_WF_Node_Asset_ID();
    
    void setPP_WF_Node_Asset_UU(final String p0);
    
    String getPP_WF_Node_Asset_UU();
    
    void setSeqNo(final int p0);
    
    int getSeqNo();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
