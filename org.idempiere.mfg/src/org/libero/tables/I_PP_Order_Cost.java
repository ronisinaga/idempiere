// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_CostType;
import org.compiere.model.I_M_CostElement;
import org.compiere.model.I_M_AttributeSetInstance;
import java.sql.Timestamp;
import org.compiere.model.I_C_AcctSchema;
import org.compiere.model.I_AD_Workflow;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order_Cost
{
    public static final String Table_Name = "PP_Order_Cost";
    public static final int Table_ID = 53024;
    public static final KeyNamePair Model = new KeyNamePair(53024, "PP_Order_Cost");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_Workflow_ID = "AD_Workflow_ID";
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";
    public static final String COLUMNNAME_CostingMethod = "CostingMethod";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_CumulatedAmt = "CumulatedAmt";
    public static final String COLUMNNAME_CumulatedAmtPost = "CumulatedAmtPost";
    public static final String COLUMNNAME_CumulatedQty = "CumulatedQty";
    public static final String COLUMNNAME_CumulatedQtyPost = "CumulatedQtyPost";
    public static final String COLUMNNAME_CurrentCostPrice = "CurrentCostPrice";
    public static final String COLUMNNAME_CurrentCostPriceLL = "CurrentCostPriceLL";
    public static final String COLUMNNAME_CurrentQty = "CurrentQty";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
    public static final String COLUMNNAME_M_CostElement_ID = "M_CostElement_ID";
    public static final String COLUMNNAME_M_CostType_ID = "M_CostType_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_PP_Order_Cost_ID = "PP_Order_Cost_ID";
    public static final String COLUMNNAME_PP_Order_Cost_UU = "PP_Order_Cost_UU";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_Workflow_ID(final int p0);
    
    int getAD_Workflow_ID();
    
    I_AD_Workflow getAD_Workflow() throws RuntimeException;
    
    void setC_AcctSchema_ID(final int p0);
    
    int getC_AcctSchema_ID();
    
    I_C_AcctSchema getC_AcctSchema() throws RuntimeException;
    
    void setCostingMethod(final String p0);
    
    String getCostingMethod();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setCumulatedAmt(final BigDecimal p0);
    
    BigDecimal getCumulatedAmt();
    
    void setCumulatedAmtPost(final BigDecimal p0);
    
    BigDecimal getCumulatedAmtPost();
    
    void setCumulatedQty(final BigDecimal p0);
    
    BigDecimal getCumulatedQty();
    
    void setCumulatedQtyPost(final BigDecimal p0);
    
    BigDecimal getCumulatedQtyPost();
    
    void setCurrentCostPrice(final BigDecimal p0);
    
    BigDecimal getCurrentCostPrice();
    
    void setCurrentCostPriceLL(final BigDecimal p0);
    
    BigDecimal getCurrentCostPriceLL();
    
    void setCurrentQty(final BigDecimal p0);
    
    BigDecimal getCurrentQty();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;
    
    void setM_CostElement_ID(final int p0);
    
    int getM_CostElement_ID();
    
    I_M_CostElement getM_CostElement() throws RuntimeException;
    
    void setM_CostType_ID(final int p0);
    
    int getM_CostType_ID();
    
    I_M_CostType getM_CostType() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setPP_Order_Cost_ID(final int p0);
    
    int getPP_Order_Cost_ID();
    
    void setPP_Order_Cost_UU(final String p0);
    
    String getPP_Order_Cost_UU();
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
