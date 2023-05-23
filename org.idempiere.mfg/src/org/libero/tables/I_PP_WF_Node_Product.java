// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_M_Product;
import java.sql.Timestamp;
import org.compiere.model.I_AD_WF_Node;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_WF_Node_Product
{
    public static final String Table_Name = "PP_WF_Node_Product";
    public static final int Table_ID = 53016;
    public static final KeyNamePair Model = new KeyNamePair(53016, "PP_WF_Node_Product");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_WF_Node_ID = "AD_WF_Node_ID";
    public static final String COLUMNNAME_ConfigurationLevel = "ConfigurationLevel";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_EntityType = "EntityType";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsSubcontracting = "IsSubcontracting";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_PP_WF_Node_Product_ID = "PP_WF_Node_Product_ID";
    public static final String COLUMNNAME_PP_WF_Node_Product_UU = "PP_WF_Node_Product_UU";
    public static final String COLUMNNAME_Qty = "Qty";
    public static final String COLUMNNAME_SeqNo = "SeqNo";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_WF_Node_ID(final int p0);
    
    int getAD_WF_Node_ID();
    
    I_AD_WF_Node getAD_WF_Node() throws RuntimeException;
    
    void setConfigurationLevel(final String p0);
    
    String getConfigurationLevel();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setEntityType(final String p0);
    
    String getEntityType();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsSubcontracting(final boolean p0);
    
    boolean isSubcontracting();
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setPP_WF_Node_Product_ID(final int p0);
    
    int getPP_WF_Node_Product_ID();
    
    void setPP_WF_Node_Product_UU(final String p0);
    
    String getPP_WF_Node_Product_UU();
    
    void setQty(final BigDecimal p0);
    
    BigDecimal getQty();
    
    void setSeqNo(final int p0);
    
    int getSeqNo();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
