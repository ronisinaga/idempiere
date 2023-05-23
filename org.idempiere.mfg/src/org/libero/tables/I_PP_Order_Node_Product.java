// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Product;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order_Node_Product
{
    public static final String Table_Name = "PP_Order_Node_Product";
    public static final int Table_ID = 53030;
    public static final KeyNamePair Model = new KeyNamePair(53030, "PP_Order_Node_Product");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsSubcontracting = "IsSubcontracting";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_PP_Order_Node_ID = "PP_Order_Node_ID";
    public static final String COLUMNNAME_PP_Order_Node_Product_ID = "PP_Order_Node_Product_ID";
    public static final String COLUMNNAME_PP_Order_Node_Product_UU = "PP_Order_Node_Product_UU";
    public static final String COLUMNNAME_PP_Order_Workflow_ID = "PP_Order_Workflow_ID";
    public static final String COLUMNNAME_Qty = "Qty";
    public static final String COLUMNNAME_SeqNo = "SeqNo";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsSubcontracting(final boolean p0);
    
    boolean isSubcontracting();
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setPP_Order_Node_ID(final int p0);
    
    int getPP_Order_Node_ID();
    
    I_PP_Order_Node getPP_Order_Node() throws RuntimeException;
    
    void setPP_Order_Node_Product_ID(final int p0);
    
    int getPP_Order_Node_Product_ID();
    
    void setPP_Order_Node_Product_UU(final String p0);
    
    String getPP_Order_Node_Product_UU();
    
    void setPP_Order_Workflow_ID(final int p0);
    
    int getPP_Order_Workflow_ID();
    
    I_PP_Order_Workflow getPP_Order_Workflow() throws RuntimeException;
    
    void setQty(final BigDecimal p0);
    
    BigDecimal getQty();
    
    void setSeqNo(final int p0);
    
    int getSeqNo();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
