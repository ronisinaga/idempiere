// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_C_ElementValue;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_Shipper;
import java.sql.Timestamp;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Charge;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Activity;
import org.compiere.model.I_AD_User;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_DD_Order
{
    public static final String Table_Name = "DD_Order";
    public static final int Table_ID = 53037;
    public static final KeyNamePair Model = new KeyNamePair(53037, "DD_Order");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(1L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";
    public static final String COLUMNNAME_C_BPartner_Location_ID = "C_BPartner_Location_ID";
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";
    public static final String COLUMNNAME_C_Charge_ID = "C_Charge_ID";
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";
    public static final String COLUMNNAME_ChargeAmt = "ChargeAmt";
    public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";
    public static final String COLUMNNAME_C_Order_ID = "C_Order_ID";
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";
    public static final String COLUMNNAME_CreateConfirm = "CreateConfirm";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_CreateFrom = "CreateFrom";
    public static final String COLUMNNAME_CreatePackage = "CreatePackage";
    public static final String COLUMNNAME_DateOrdered = "DateOrdered";
    public static final String COLUMNNAME_DatePrinted = "DatePrinted";
    public static final String COLUMNNAME_DatePromised = "DatePromised";
    public static final String COLUMNNAME_DateReceived = "DateReceived";
    public static final String COLUMNNAME_DD_Order_ID = "DD_Order_ID";
    public static final String COLUMNNAME_DD_Order_UU = "DD_Order_UU";
    public static final String COLUMNNAME_DeliveryRule = "DeliveryRule";
    public static final String COLUMNNAME_DeliveryViaRule = "DeliveryViaRule";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocAction = "DocAction";
    public static final String COLUMNNAME_DocStatus = "DocStatus";
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";
    public static final String COLUMNNAME_FreightAmt = "FreightAmt";
    public static final String COLUMNNAME_FreightCostRule = "FreightCostRule";
    public static final String COLUMNNAME_GenerateTo = "GenerateTo";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsApproved = "IsApproved";
    public static final String COLUMNNAME_IsDelivered = "IsDelivered";
    public static final String COLUMNNAME_IsDropShip = "IsDropShip";
    public static final String COLUMNNAME_IsInDispute = "IsInDispute";
    public static final String COLUMNNAME_IsInTransit = "IsInTransit";
    public static final String COLUMNNAME_IsPrinted = "IsPrinted";
    public static final String COLUMNNAME_IsSelected = "IsSelected";
    public static final String COLUMNNAME_IsSOTrx = "IsSOTrx";
    public static final String COLUMNNAME_M_Shipper_ID = "M_Shipper_ID";
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";
    public static final String COLUMNNAME_NoPackages = "NoPackages";
    public static final String COLUMNNAME_PickDate = "PickDate";
    public static final String COLUMNNAME_POReference = "POReference";
    public static final String COLUMNNAME_Posted = "Posted";
    public static final String COLUMNNAME_PriorityRule = "PriorityRule";
    public static final String COLUMNNAME_Processed = "Processed";
    public static final String COLUMNNAME_ProcessedOn = "ProcessedOn";
    public static final String COLUMNNAME_Processing = "Processing";
    public static final String COLUMNNAME_Ref_Order_ID = "Ref_Order_ID";
    public static final String COLUMNNAME_SalesRep_ID = "SalesRep_ID";
    public static final String COLUMNNAME_SendEMail = "SendEMail";
    public static final String COLUMNNAME_ShipDate = "ShipDate";
    public static final String COLUMNNAME_TrackingNo = "TrackingNo";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_User1_ID = "User1_ID";
    public static final String COLUMNNAME_User2_ID = "User2_ID";
    public static final String COLUMNNAME_Volume = "Volume";
    public static final String COLUMNNAME_Weight = "Weight";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_OrgTrx_ID(final int p0);
    
    int getAD_OrgTrx_ID();
    
    void setAD_User_ID(final int p0);
    
    int getAD_User_ID();
    
    I_AD_User getAD_User() throws RuntimeException;
    
    void setC_Activity_ID(final int p0);
    
    int getC_Activity_ID();
    
    I_C_Activity getC_Activity() throws RuntimeException;
    
    void setC_BPartner_ID(final int p0);
    
    int getC_BPartner_ID();
    
    I_C_BPartner getC_BPartner() throws RuntimeException;
    
    void setC_BPartner_Location_ID(final int p0);
    
    int getC_BPartner_Location_ID();
    
    I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException;
    
    void setC_Campaign_ID(final int p0);
    
    int getC_Campaign_ID();
    
    I_C_Campaign getC_Campaign() throws RuntimeException;
    
    void setC_Charge_ID(final int p0);
    
    int getC_Charge_ID();
    
    I_C_Charge getC_Charge() throws RuntimeException;
    
    void setC_DocType_ID(final int p0);
    
    int getC_DocType_ID();
    
    I_C_DocType getC_DocType() throws RuntimeException;
    
    void setChargeAmt(final BigDecimal p0);
    
    BigDecimal getChargeAmt();
    
    void setC_Invoice_ID(final int p0);
    
    int getC_Invoice_ID();
    
    I_C_Invoice getC_Invoice() throws RuntimeException;
    
    void setC_Order_ID(final int p0);
    
    int getC_Order_ID();
    
    I_C_Order getC_Order() throws RuntimeException;
    
    void setC_Project_ID(final int p0);
    
    int getC_Project_ID();
    
    I_C_Project getC_Project() throws RuntimeException;
    
    void setCreateConfirm(final String p0);
    
    String getCreateConfirm();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setCreateFrom(final String p0);
    
    String getCreateFrom();
    
    void setCreatePackage(final String p0);
    
    String getCreatePackage();
    
    void setDateOrdered(final Timestamp p0);
    
    Timestamp getDateOrdered();
    
    void setDatePrinted(final Timestamp p0);
    
    Timestamp getDatePrinted();
    
    void setDatePromised(final Timestamp p0);
    
    Timestamp getDatePromised();
    
    void setDateReceived(final Timestamp p0);
    
    Timestamp getDateReceived();
    
    void setDD_Order_ID(final int p0);
    
    int getDD_Order_ID();
    
    void setDD_Order_UU(final String p0);
    
    String getDD_Order_UU();
    
    void setDeliveryRule(final String p0);
    
    String getDeliveryRule();
    
    void setDeliveryViaRule(final String p0);
    
    String getDeliveryViaRule();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setDocAction(final String p0);
    
    String getDocAction();
    
    void setDocStatus(final String p0);
    
    String getDocStatus();
    
    void setDocumentNo(final String p0);
    
    String getDocumentNo();
    
    void setFreightAmt(final BigDecimal p0);
    
    BigDecimal getFreightAmt();
    
    void setFreightCostRule(final String p0);
    
    String getFreightCostRule();
    
    void setGenerateTo(final String p0);
    
    String getGenerateTo();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsApproved(final boolean p0);
    
    boolean isApproved();
    
    void setIsDelivered(final boolean p0);
    
    boolean isDelivered();
    
    void setIsDropShip(final boolean p0);
    
    boolean isDropShip();
    
    void setIsInDispute(final boolean p0);
    
    boolean isInDispute();
    
    void setIsInTransit(final boolean p0);
    
    boolean isInTransit();
    
    void setIsPrinted(final boolean p0);
    
    boolean isPrinted();
    
    void setIsSelected(final boolean p0);
    
    boolean isSelected();
    
    void setIsSOTrx(final boolean p0);
    
    boolean isSOTrx();
    
    void setM_Shipper_ID(final int p0);
    
    int getM_Shipper_ID();
    
    I_M_Shipper getM_Shipper() throws RuntimeException;
    
    void setM_Warehouse_ID(final int p0);
    
    int getM_Warehouse_ID();
    
    I_M_Warehouse getM_Warehouse() throws RuntimeException;
    
    void setNoPackages(final int p0);
    
    int getNoPackages();
    
    void setPickDate(final Timestamp p0);
    
    Timestamp getPickDate();
    
    void setPOReference(final String p0);
    
    String getPOReference();
    
    void setPosted(final boolean p0);
    
    boolean isPosted();
    
    void setPriorityRule(final String p0);
    
    String getPriorityRule();
    
    void setProcessed(final boolean p0);
    
    boolean isProcessed();
    
    void setProcessedOn(final BigDecimal p0);
    
    BigDecimal getProcessedOn();
    
    void setProcessing(final boolean p0);
    
    boolean isProcessing();
    
    void setRef_Order_ID(final int p0);
    
    int getRef_Order_ID();
    
    I_C_Order getRef_Order() throws RuntimeException;
    
    void setSalesRep_ID(final int p0);
    
    int getSalesRep_ID();
    
    I_AD_User getSalesRep() throws RuntimeException;
    
    void setSendEMail(final boolean p0);
    
    boolean isSendEMail();
    
    void setShipDate(final Timestamp p0);
    
    Timestamp getShipDate();
    
    void setTrackingNo(final String p0);
    
    String getTrackingNo();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setUser1_ID(final int p0);
    
    int getUser1_ID();
    
    I_C_ElementValue getUser1() throws RuntimeException;
    
    void setUser2_ID(final int p0);
    
    int getUser2_ID();
    
    I_C_ElementValue getUser2() throws RuntimeException;
    
    void setVolume(final BigDecimal p0);
    
    BigDecimal getVolume();
    
    void setWeight(final BigDecimal p0);
    
    BigDecimal getWeight();
}
