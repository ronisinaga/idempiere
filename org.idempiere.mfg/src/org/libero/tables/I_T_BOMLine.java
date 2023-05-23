// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Product_BOMLine;
import org.eevolution.model.I_PP_Product_BOM;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_CostType;
import org.compiere.model.I_M_CostElement;
import java.sql.Timestamp;
import org.compiere.model.I_C_AcctSchema;
import org.compiere.model.I_AD_PInstance;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_T_BOMLine
{
    public static final String Table_Name = "T_BOMLine";
    public static final int Table_ID = 53045;
    public static final KeyNamePair Model = new KeyNamePair(53045, "T_BOMLine");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_PInstance_ID = "AD_PInstance_ID";
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";
    public static final String COLUMNNAME_Cost = "Cost";
    public static final String COLUMNNAME_CostingMethod = "CostingMethod";
    public static final String COLUMNNAME_CostStandard = "CostStandard";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_CurrentCostPrice = "CurrentCostPrice";
    public static final String COLUMNNAME_CurrentCostPriceLL = "CurrentCostPriceLL";
    public static final String COLUMNNAME_FutureCostPrice = "FutureCostPrice";
    public static final String COLUMNNAME_FutureCostPriceLL = "FutureCostPriceLL";
    public static final String COLUMNNAME_Implosion = "Implosion";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsCostFrozen = "IsCostFrozen";
    public static final String COLUMNNAME_LevelNo = "LevelNo";
    public static final String COLUMNNAME_Levels = "Levels";
    public static final String COLUMNNAME_M_CostElement_ID = "M_CostElement_ID";
    public static final String COLUMNNAME_M_CostType_ID = "M_CostType_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_PP_Product_BOM_ID = "PP_Product_BOM_ID";
    public static final String COLUMNNAME_PP_Product_BOMLine_ID = "PP_Product_BOMLine_ID";
    public static final String COLUMNNAME_QtyBOM = "QtyBOM";
    public static final String COLUMNNAME_Sel_Product_ID = "Sel_Product_ID";
    public static final String COLUMNNAME_SeqNo = "SeqNo";
    public static final String COLUMNNAME_T_BOMLine_ID = "T_BOMLine_ID";
    public static final String COLUMNNAME_T_BOMLine_UU = "T_BOMLine_UU";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_PInstance_ID(final int p0);
    
    int getAD_PInstance_ID();
    
    I_AD_PInstance getAD_PInstance() throws RuntimeException;
    
    void setC_AcctSchema_ID(final int p0);
    
    int getC_AcctSchema_ID();
    
    I_C_AcctSchema getC_AcctSchema() throws RuntimeException;
    
    void setCost(final BigDecimal p0);
    
    BigDecimal getCost();
    
    void setCostingMethod(final String p0);
    
    String getCostingMethod();
    
    void setCostStandard(final BigDecimal p0);
    
    BigDecimal getCostStandard();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setCurrentCostPrice(final BigDecimal p0);
    
    BigDecimal getCurrentCostPrice();
    
    void setCurrentCostPriceLL(final BigDecimal p0);
    
    BigDecimal getCurrentCostPriceLL();
    
    void setFutureCostPrice(final BigDecimal p0);
    
    BigDecimal getFutureCostPrice();
    
    void setFutureCostPriceLL(final BigDecimal p0);
    
    BigDecimal getFutureCostPriceLL();
    
    void setImplosion(final boolean p0);
    
    boolean isImplosion();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsCostFrozen(final boolean p0);
    
    boolean isCostFrozen();
    
    void setLevelNo(final int p0);
    
    int getLevelNo();
    
    void setLevels(final String p0);
    
    String getLevels();
    
    void setM_CostElement_ID(final int p0);
    
    int getM_CostElement_ID();
    
    I_M_CostElement getM_CostElement() throws RuntimeException;
    
    void setM_CostType_ID(final int p0);
    
    int getM_CostType_ID();
    
    I_M_CostType getM_CostType() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setPP_Product_BOM_ID(final int p0);
    
    int getPP_Product_BOM_ID();
    
    I_PP_Product_BOM getPP_Product_BOM() throws RuntimeException;
    
    void setPP_Product_BOMLine_ID(final int p0);
    
    int getPP_Product_BOMLine_ID();
    
    I_PP_Product_BOMLine getPP_Product_BOMLine() throws RuntimeException;
    
    void setQtyBOM(final BigDecimal p0);
    
    BigDecimal getQtyBOM();
    
    void setSel_Product_ID(final int p0);
    
    int getSel_Product_ID();
    
    void setSeqNo(final int p0);
    
    int getSeqNo();
    
    void setT_BOMLine_ID(final int p0);
    
    int getT_BOMLine_ID();
    
    void setT_BOMLine_UU(final String p0);
    
    String getT_BOMLine_UU();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
