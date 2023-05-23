// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_C_ElementValue;
import org.compiere.model.I_M_Shipper;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.I_C_UOM;
import java.sql.Timestamp;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_C_Charge;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.I_C_Activity;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_DD_OrderLine
{
    public static final String Table_Name = "DD_OrderLine";
    public static final int Table_ID = 53038;
    public static final KeyNamePair Model = new KeyNamePair(53038, "DD_OrderLine");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(1L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";
    public static final String COLUMNNAME_C_Charge_ID = "C_Charge_ID";
    public static final String COLUMNNAME_ConfirmedQty = "ConfirmedQty";
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";
    public static final String COLUMNNAME_DateDelivered = "DateDelivered";
    public static final String COLUMNNAME_DateOrdered = "DateOrdered";
    public static final String COLUMNNAME_DatePromised = "DatePromised";
    public static final String COLUMNNAME_DD_Order_ID = "DD_Order_ID";
    public static final String COLUMNNAME_DD_OrderLine_ID = "DD_OrderLine_ID";
    public static final String COLUMNNAME_DD_OrderLine_UU = "DD_OrderLine_UU";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_FreightAmt = "FreightAmt";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsDescription = "IsDescription";
    public static final String COLUMNNAME_IsInvoiced = "IsInvoiced";
    public static final String COLUMNNAME_Line = "Line";
    public static final String COLUMNNAME_LineNetAmt = "LineNetAmt";
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
    public static final String COLUMNNAME_M_AttributeSetInstanceTo_ID = "M_AttributeSetInstanceTo_ID";
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";
    public static final String COLUMNNAME_M_LocatorTo_ID = "M_LocatorTo_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_M_Shipper_ID = "M_Shipper_ID";
    public static final String COLUMNNAME_PickedQty = "PickedQty";
    public static final String COLUMNNAME_Processed = "Processed";
    public static final String COLUMNNAME_QtyDelivered = "QtyDelivered";
    public static final String COLUMNNAME_QtyEntered = "QtyEntered";
    public static final String COLUMNNAME_QtyInTransit = "QtyInTransit";
    public static final String COLUMNNAME_QtyOrdered = "QtyOrdered";
    public static final String COLUMNNAME_QtyReserved = "QtyReserved";
    public static final String COLUMNNAME_ScrappedQty = "ScrappedQty";
    public static final String COLUMNNAME_TargetQty = "TargetQty";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_User1_ID = "User1_ID";
    public static final String COLUMNNAME_User2_ID = "User2_ID";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_OrgTrx_ID(final int p0);
    
    int getAD_OrgTrx_ID();
    
    void setC_Activity_ID(final int p0);
    
    int getC_Activity_ID();
    
    I_C_Activity getC_Activity() throws RuntimeException;
    
    void setC_Campaign_ID(final int p0);
    
    int getC_Campaign_ID();
    
    I_C_Campaign getC_Campaign() throws RuntimeException;
    
    void setC_Charge_ID(final int p0);
    
    int getC_Charge_ID();
    
    I_C_Charge getC_Charge() throws RuntimeException;
    
    void setConfirmedQty(final BigDecimal p0);
    
    BigDecimal getConfirmedQty();
    
    void setC_Project_ID(final int p0);
    
    int getC_Project_ID();
    
    I_C_Project getC_Project() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setC_UOM_ID(final int p0);
    
    int getC_UOM_ID();
    
    I_C_UOM getC_UOM() throws RuntimeException;
    
    void setDateDelivered(final Timestamp p0);
    
    Timestamp getDateDelivered();
    
    void setDateOrdered(final Timestamp p0);
    
    Timestamp getDateOrdered();
    
    void setDatePromised(final Timestamp p0);
    
    Timestamp getDatePromised();
    
    void setDD_Order_ID(final int p0);
    
    int getDD_Order_ID();
    
    I_DD_Order getDD_Order() throws RuntimeException;
    
    void setDD_OrderLine_ID(final int p0);
    
    int getDD_OrderLine_ID();
    
    void setDD_OrderLine_UU(final String p0);
    
    String getDD_OrderLine_UU();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setFreightAmt(final BigDecimal p0);
    
    BigDecimal getFreightAmt();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsDescription(final boolean p0);
    
    boolean isDescription();
    
    void setIsInvoiced(final boolean p0);
    
    boolean isInvoiced();
    
    void setLine(final int p0);
    
    int getLine();
    
    void setLineNetAmt(final BigDecimal p0);
    
    BigDecimal getLineNetAmt();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;
    
    void setM_AttributeSetInstanceTo_ID(final int p0);
    
    int getM_AttributeSetInstanceTo_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstanceTo() throws RuntimeException;
    
    void setM_Locator_ID(final int p0);
    
    int getM_Locator_ID();
    
    I_M_Locator getM_Locator() throws RuntimeException;
    
    void setM_LocatorTo_ID(final int p0);
    
    int getM_LocatorTo_ID();
    
    I_M_Locator getM_LocatorTo() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setM_Shipper_ID(final int p0);
    
    int getM_Shipper_ID();
    
    I_M_Shipper getM_Shipper() throws RuntimeException;
    
    void setPickedQty(final BigDecimal p0);
    
    BigDecimal getPickedQty();
    
    void setProcessed(final boolean p0);
    
    boolean isProcessed();
    
    void setQtyDelivered(final BigDecimal p0);
    
    BigDecimal getQtyDelivered();
    
    void setQtyEntered(final BigDecimal p0);
    
    BigDecimal getQtyEntered();
    
    void setQtyInTransit(final BigDecimal p0);
    
    BigDecimal getQtyInTransit();
    
    void setQtyOrdered(final BigDecimal p0);
    
    BigDecimal getQtyOrdered();
    
    void setQtyReserved(final BigDecimal p0);
    
    BigDecimal getQtyReserved();
    
    void setScrappedQty(final BigDecimal p0);
    
    BigDecimal getScrappedQty();
    
    void setTargetQty(final BigDecimal p0);
    
    BigDecimal getTargetQty();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setUser1_ID(final int p0);
    
    int getUser1_ID();
    
    I_C_ElementValue getUser1() throws RuntimeException;
    
    void setUser2_ID(final int p0);
    
    int getUser2_ID();
    
    I_C_ElementValue getUser2() throws RuntimeException;
}
