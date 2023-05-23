// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_ChangeNotice;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.I_C_UOM;
import java.sql.Timestamp;
import org.compiere.model.I_AD_User;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order_BOMLine
{
    public static final String Table_Name = "PP_Order_BOMLine";
    public static final int Table_ID = 53025;
    public static final KeyNamePair Model = new KeyNamePair(53025, "PP_Order_BOMLine");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";
    public static final String COLUMNNAME_Assay = "Assay";
    public static final String COLUMNNAME_BackflushGroup = "BackflushGroup";
    public static final String COLUMNNAME_ComponentType = "ComponentType";
    public static final String COLUMNNAME_CostAllocationPerc = "CostAllocationPerc";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";
    public static final String COLUMNNAME_DateDelivered = "DateDelivered";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_Feature = "Feature";
    public static final String COLUMNNAME_Forecast = "Forecast";
    public static final String COLUMNNAME_Help = "Help";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsCritical = "IsCritical";
    public static final String COLUMNNAME_IsQtyPercentage = "IsQtyPercentage";
    public static final String COLUMNNAME_IssueMethod = "IssueMethod";
    public static final String COLUMNNAME_LeadTimeOffset = "LeadTimeOffset";
    public static final String COLUMNNAME_Line = "Line";
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
    public static final String COLUMNNAME_M_ChangeNotice_ID = "M_ChangeNotice_ID";
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";
    public static final String COLUMNNAME_PP_Order_BOM_ID = "PP_Order_BOM_ID";
    public static final String COLUMNNAME_PP_Order_BOMLine_ID = "PP_Order_BOMLine_ID";
    public static final String COLUMNNAME_PP_Order_BOMLine_UU = "PP_Order_BOMLine_UU";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_QtyBatch = "QtyBatch";
    public static final String COLUMNNAME_QtyBOM = "QtyBOM";
    public static final String COLUMNNAME_QtyDelivered = "QtyDelivered";
    public static final String COLUMNNAME_QtyEntered = "QtyEntered";
    public static final String COLUMNNAME_QtyPost = "QtyPost";
    public static final String COLUMNNAME_QtyReject = "QtyReject";
    public static final String COLUMNNAME_QtyRequired = "QtyRequired";
    public static final String COLUMNNAME_QtyReserved = "QtyReserved";
    public static final String COLUMNNAME_QtyScrap = "QtyScrap";
    public static final String COLUMNNAME_Scrap = "Scrap";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";
    public static final String COLUMNNAME_ValidTo = "ValidTo";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_User_ID(final int p0);
    
    int getAD_User_ID();
    
    I_AD_User getAD_User() throws RuntimeException;
    
    void setAssay(final BigDecimal p0);
    
    BigDecimal getAssay();
    
    void setBackflushGroup(final String p0);
    
    String getBackflushGroup();
    
    void setComponentType(final String p0);
    
    String getComponentType();
    
    void setCostAllocationPerc(final BigDecimal p0);
    
    BigDecimal getCostAllocationPerc();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setC_UOM_ID(final int p0);
    
    int getC_UOM_ID();
    
    I_C_UOM getC_UOM() throws RuntimeException;
    
    void setDateDelivered(final Timestamp p0);
    
    Timestamp getDateDelivered();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setFeature(final String p0);
    
    String getFeature();
    
    void setForecast(final BigDecimal p0);
    
    BigDecimal getForecast();
    
    void setHelp(final String p0);
    
    String getHelp();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsCritical(final boolean p0);
    
    boolean isCritical();
    
    void setIsQtyPercentage(final boolean p0);
    
    boolean isQtyPercentage();
    
    void setIssueMethod(final String p0);
    
    String getIssueMethod();
    
    void setLeadTimeOffset(final int p0);
    
    int getLeadTimeOffset();
    
    void setLine(final int p0);
    
    int getLine();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;
    
    void setM_ChangeNotice_ID(final int p0);
    
    int getM_ChangeNotice_ID();
    
    I_M_ChangeNotice getM_ChangeNotice() throws RuntimeException;
    
    void setM_Locator_ID(final int p0);
    
    int getM_Locator_ID();
    
    I_M_Locator getM_Locator() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setM_Warehouse_ID(final int p0);
    
    int getM_Warehouse_ID();
    
    I_M_Warehouse getM_Warehouse() throws RuntimeException;
    
    void setPP_Order_BOM_ID(final int p0);
    
    int getPP_Order_BOM_ID();
    
    I_PP_Order_BOM getPP_Order_BOM() throws RuntimeException;
    
    void setPP_Order_BOMLine_ID(final int p0);
    
    int getPP_Order_BOMLine_ID();
    
    void setPP_Order_BOMLine_UU(final String p0);
    
    String getPP_Order_BOMLine_UU();
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setQtyBatch(final BigDecimal p0);
    
    BigDecimal getQtyBatch();
    
    void setQtyBOM(final BigDecimal p0);
    
    BigDecimal getQtyBOM();
    
    void setQtyDelivered(final BigDecimal p0);
    
    BigDecimal getQtyDelivered();
    
    void setQtyEntered(final BigDecimal p0);
    
    BigDecimal getQtyEntered();
    
    void setQtyPost(final BigDecimal p0);
    
    BigDecimal getQtyPost();
    
    void setQtyReject(final BigDecimal p0);
    
    BigDecimal getQtyReject();
    
    void setQtyRequired(final BigDecimal p0);
    
    BigDecimal getQtyRequired();
    
    void setQtyReserved(final BigDecimal p0);
    
    BigDecimal getQtyReserved();
    
    void setQtyScrap(final BigDecimal p0);
    
    BigDecimal getQtyScrap();
    
    void setScrap(final BigDecimal p0);
    
    BigDecimal getScrap();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValidFrom(final Timestamp p0);
    
    Timestamp getValidFrom();
    
    void setValidTo(final Timestamp p0);
    
    Timestamp getValidTo();
}
