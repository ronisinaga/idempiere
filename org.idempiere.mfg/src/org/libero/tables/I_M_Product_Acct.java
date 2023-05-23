// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_M_Product;
import java.sql.Timestamp;
import org.compiere.model.I_C_AcctSchema;
import org.compiere.model.MTable;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_M_Product_Acct
{
    public static final String Table_Name = "M_Product_Acct";
    public static final int Table_ID = MTable.getTable_ID("M_Product_Acct");
    public static final KeyNamePair Model = new KeyNamePair(I_M_Product_Acct.Table_ID, "M_Product_Acct");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_P_Asset_Acct = "P_Asset_Acct";
    public static final String COLUMNNAME_P_AverageCostVariance_Acct = "P_AverageCostVariance_Acct";
    public static final String COLUMNNAME_P_Burden_Acct = "P_Burden_Acct";
    public static final String COLUMNNAME_P_COGS_Acct = "P_COGS_Acct";
    public static final String COLUMNNAME_P_CostAdjustment_Acct = "P_CostAdjustment_Acct";
    public static final String COLUMNNAME_P_CostOfProduction_Acct = "P_CostOfProduction_Acct";
    public static final String COLUMNNAME_P_Expense_Acct = "P_Expense_Acct";
    public static final String COLUMNNAME_P_FloorStock_Acct = "P_FloorStock_Acct";
    public static final String COLUMNNAME_P_InventoryClearing_Acct = "P_InventoryClearing_Acct";
    public static final String COLUMNNAME_P_InvoicePriceVariance_Acct = "P_InvoicePriceVariance_Acct";
    public static final String COLUMNNAME_P_Labor_Acct = "P_Labor_Acct";
    public static final String COLUMNNAME_P_MethodChangeVariance_Acct = "P_MethodChangeVariance_Acct";
    public static final String COLUMNNAME_P_MixVariance_Acct = "P_MixVariance_Acct";
    public static final String COLUMNNAME_P_OutsideProcessing_Acct = "P_OutsideProcessing_Acct";
    public static final String COLUMNNAME_P_Overhead_Acct = "P_Overhead_Acct";
    public static final String COLUMNNAME_P_PurchasePriceVariance_Acct = "P_PurchasePriceVariance_Acct";
    public static final String COLUMNNAME_P_RateVariance_Acct = "P_RateVariance_Acct";
    public static final String COLUMNNAME_P_Revenue_Acct = "P_Revenue_Acct";
    public static final String COLUMNNAME_P_Scrap_Acct = "P_Scrap_Acct";
    public static final String COLUMNNAME_P_TradeDiscountGrant_Acct = "P_TradeDiscountGrant_Acct";
    public static final String COLUMNNAME_P_TradeDiscountRec_Acct = "P_TradeDiscountRec_Acct";
    public static final String COLUMNNAME_P_UsageVariance_Acct = "P_UsageVariance_Acct";
    public static final String COLUMNNAME_P_WIP_Acct = "P_WIP_Acct";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setC_AcctSchema_ID(final int p0);
    
    int getC_AcctSchema_ID();
    
    I_C_AcctSchema getC_AcctSchema() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setP_Asset_Acct(final int p0);
    
    int getP_Asset_Acct();
    
    I_C_ValidCombination getP_Asset_A() throws RuntimeException;
    
    void setP_AverageCostVariance_Acct(final int p0);
    
    int getP_AverageCostVariance_Acct();
    
    I_C_ValidCombination getP_AverageCostVariance_A() throws RuntimeException;
    
    void setP_Burden_Acct(final int p0);
    
    int getP_Burden_Acct();
    
    I_C_ValidCombination getP_Burden_A() throws RuntimeException;
    
    void setP_COGS_Acct(final int p0);
    
    int getP_COGS_Acct();
    
    I_C_ValidCombination getP_COGS_A() throws RuntimeException;
    
    void setP_CostAdjustment_Acct(final int p0);
    
    int getP_CostAdjustment_Acct();
    
    I_C_ValidCombination getP_CostAdjustment_A() throws RuntimeException;
    
    void setP_CostOfProduction_Acct(final int p0);
    
    int getP_CostOfProduction_Acct();
    
    I_C_ValidCombination getP_CostOfProduction_A() throws RuntimeException;
    
    void setP_Expense_Acct(final int p0);
    
    int getP_Expense_Acct();
    
    I_C_ValidCombination getP_Expense_A() throws RuntimeException;
    
    void setP_FloorStock_Acct(final int p0);
    
    int getP_FloorStock_Acct();
    
    I_C_ValidCombination getP_FloorStock_A() throws RuntimeException;
    
    void setP_InventoryClearing_Acct(final int p0);
    
    int getP_InventoryClearing_Acct();
    
    I_C_ValidCombination getP_InventoryClearing_A() throws RuntimeException;
    
    void setP_InvoicePriceVariance_Acct(final int p0);
    
    int getP_InvoicePriceVariance_Acct();
    
    I_C_ValidCombination getP_InvoicePriceVariance_A() throws RuntimeException;
    
    void setP_Labor_Acct(final int p0);
    
    int getP_Labor_Acct();
    
    I_C_ValidCombination getP_Labor_A() throws RuntimeException;
    
    void setP_MethodChangeVariance_Acct(final int p0);
    
    int getP_MethodChangeVariance_Acct();
    
    I_C_ValidCombination getP_MethodChangeVariance_A() throws RuntimeException;
    
    void setP_MixVariance_Acct(final int p0);
    
    int getP_MixVariance_Acct();
    
    I_C_ValidCombination getP_MixVariance_A() throws RuntimeException;
    
    void setP_OutsideProcessing_Acct(final int p0);
    
    int getP_OutsideProcessing_Acct();
    
    I_C_ValidCombination getP_OutsideProcessing_A() throws RuntimeException;
    
    void setP_Overhead_Acct(final int p0);
    
    int getP_Overhead_Acct();
    
    I_C_ValidCombination getP_Overhead_A() throws RuntimeException;
    
    void setP_PurchasePriceVariance_Acct(final int p0);
    
    int getP_PurchasePriceVariance_Acct();
    
    I_C_ValidCombination getP_PurchasePriceVariance_A() throws RuntimeException;
    
    void setP_RateVariance_Acct(final int p0);
    
    int getP_RateVariance_Acct();
    
    I_C_ValidCombination getP_RateVariance_A() throws RuntimeException;
    
    void setP_Revenue_Acct(final int p0);
    
    int getP_Revenue_Acct();
    
    I_C_ValidCombination getP_Revenue_A() throws RuntimeException;
    
    void setP_Scrap_Acct(final int p0);
    
    int getP_Scrap_Acct();
    
    I_C_ValidCombination getP_Scrap_A() throws RuntimeException;
    
    void setP_TradeDiscountGrant_Acct(final int p0);
    
    int getP_TradeDiscountGrant_Acct();
    
    I_C_ValidCombination getP_TradeDiscountGrant_A() throws RuntimeException;
    
    void setP_TradeDiscountRec_Acct(final int p0);
    
    int getP_TradeDiscountRec_Acct();
    
    I_C_ValidCombination getP_TradeDiscountRec_A() throws RuntimeException;
    
    void setP_UsageVariance_Acct(final int p0);
    
    int getP_UsageVariance_Acct();
    
    I_C_ValidCombination getP_UsageVariance_A() throws RuntimeException;
    
    void setP_WIP_Acct(final int p0);
    
    int getP_WIP_Acct();
    
    I_C_ValidCombination getP_WIP_A() throws RuntimeException;
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
