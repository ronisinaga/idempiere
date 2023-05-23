// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_Shipper;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_DD_NetworkDistributionLine
{
    public static final String Table_Name = "DD_NetworkDistributionLine";
    public static final int Table_ID = 53061;
    public static final KeyNamePair Model = new KeyNamePair(53061, "DD_NetworkDistributionLine");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_DD_NetworkDistribution_ID = "DD_NetworkDistribution_ID";
    public static final String COLUMNNAME_DD_NetworkDistributionLine_ID = "DD_NetworkDistributionLine_ID";
    public static final String COLUMNNAME_DD_NetworkDistributionLine_UU = "DD_NetworkDistributionLine_UU";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_Shipper_ID = "M_Shipper_ID";
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";
    public static final String COLUMNNAME_M_WarehouseSource_ID = "M_WarehouseSource_ID";
    public static final String COLUMNNAME_Percent = "Percent";
    public static final String COLUMNNAME_PriorityNo = "PriorityNo";
    public static final String COLUMNNAME_TransfertTime = "TransfertTime";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";
    public static final String COLUMNNAME_ValidTo = "ValidTo";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDD_NetworkDistribution_ID(final int p0);
    
    int getDD_NetworkDistribution_ID();
    
    I_DD_NetworkDistribution getDD_NetworkDistribution() throws RuntimeException;
    
    void setDD_NetworkDistributionLine_ID(final int p0);
    
    int getDD_NetworkDistributionLine_ID();
    
    void setDD_NetworkDistributionLine_UU(final String p0);
    
    String getDD_NetworkDistributionLine_UU();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_Shipper_ID(final int p0);
    
    int getM_Shipper_ID();
    
    I_M_Shipper getM_Shipper() throws RuntimeException;
    
    void setM_Warehouse_ID(final int p0);
    
    int getM_Warehouse_ID();
    
    I_M_Warehouse getM_Warehouse() throws RuntimeException;
    
    void setM_WarehouseSource_ID(final int p0);
    
    int getM_WarehouseSource_ID();
    
    I_M_Warehouse getM_WarehouseSource() throws RuntimeException;
    
    void setPercent(final BigDecimal p0);
    
    BigDecimal getPercent();
    
    void setPriorityNo(final int p0);
    
    int getPriorityNo();
    
    void setTransfertTime(final BigDecimal p0);
    
    BigDecimal getTransfertTime();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValidFrom(final Timestamp p0);
    
    Timestamp getValidFrom();
    
    void setValidTo(final Timestamp p0);
    
    Timestamp getValidTo();
}
