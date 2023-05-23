// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_C_ElementValue;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_Shipper;
import org.compiere.util.KeyNamePair;
import java.sql.Timestamp;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_Invoice;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Charge;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Activity;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_User;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_DD_Order extends PO implements I_DD_Order, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int DELIVERYRULE_AD_Reference_ID = 151;
    public static final String DELIVERYRULE_AfterReceipt = "R";
    public static final String DELIVERYRULE_Availability = "A";
    public static final String DELIVERYRULE_CompleteLine = "L";
    public static final String DELIVERYRULE_CompleteOrder = "O";
    public static final String DELIVERYRULE_Force = "F";
    public static final String DELIVERYRULE_Manual = "M";
    public static final int DELIVERYVIARULE_AD_Reference_ID = 152;
    public static final String DELIVERYVIARULE_Pickup = "P";
    public static final String DELIVERYVIARULE_Delivery = "D";
    public static final String DELIVERYVIARULE_Shipper = "S";
    public static final int DOCACTION_AD_Reference_ID = 135;
    public static final String DOCACTION_Complete = "CO";
    public static final String DOCACTION_Approve = "AP";
    public static final String DOCACTION_Reject = "RJ";
    public static final String DOCACTION_Post = "PO";
    public static final String DOCACTION_Void = "VO";
    public static final String DOCACTION_Close = "CL";
    public static final String DOCACTION_Reverse_Correct = "RC";
    public static final String DOCACTION_Reverse_Accrual = "RA";
    public static final String DOCACTION_Invalidate = "IN";
    public static final String DOCACTION_Re_Activate = "RE";
    public static final String DOCACTION_None = "--";
    public static final String DOCACTION_Prepare = "PR";
    public static final String DOCACTION_Unlock = "XL";
    public static final String DOCACTION_WaitComplete = "WC";
    public static final int DOCSTATUS_AD_Reference_ID = 131;
    public static final String DOCSTATUS_Drafted = "DR";
    public static final String DOCSTATUS_Completed = "CO";
    public static final String DOCSTATUS_Approved = "AP";
    public static final String DOCSTATUS_NotApproved = "NA";
    public static final String DOCSTATUS_Voided = "VO";
    public static final String DOCSTATUS_Invalid = "IN";
    public static final String DOCSTATUS_Reversed = "RE";
    public static final String DOCSTATUS_Closed = "CL";
    public static final String DOCSTATUS_Unknown = "??";
    public static final String DOCSTATUS_InProgress = "IP";
    public static final String DOCSTATUS_WaitingPayment = "WP";
    public static final String DOCSTATUS_WaitingConfirmation = "WC";
    public static final int FREIGHTCOSTRULE_AD_Reference_ID = 153;
    public static final String FREIGHTCOSTRULE_FreightIncluded = "I";
    public static final String FREIGHTCOSTRULE_FixPrice = "F";
    public static final String FREIGHTCOSTRULE_Calculated = "C";
    public static final String FREIGHTCOSTRULE_Line = "L";
    public static final int PRIORITYRULE_AD_Reference_ID = 154;
    public static final String PRIORITYRULE_High = "3";
    public static final String PRIORITYRULE_Medium = "5";
    public static final String PRIORITYRULE_Low = "7";
    public static final String PRIORITYRULE_Urgent = "1";
    public static final String PRIORITYRULE_Minor = "9";
    
    public X_DD_Order(final Properties ctx, final int DD_Order_ID, final String trxName) {
        super(ctx, DD_Order_ID, trxName);
    }
    
    public X_DD_Order(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_DD_Order.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53037, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_DD_Order[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setAD_OrgTrx_ID(final int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) {
            this.set_Value("AD_OrgTrx_ID", (Object)null);
        }
        else {
            this.set_Value("AD_OrgTrx_ID", (Object)AD_OrgTrx_ID);
        }
    }
    
    public int getAD_OrgTrx_ID() {
        final Integer ii = (Integer)this.get_Value("AD_OrgTrx_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_User getAD_User() throws RuntimeException {
        return (I_AD_User)MTable.get(this.getCtx(), "AD_User").getPO(this.getAD_User_ID(), this.get_TrxName());
    }
    
    public void setAD_User_ID(final int AD_User_ID) {
        if (AD_User_ID < 1) {
            this.set_Value("AD_User_ID", (Object)null);
        }
        else {
            this.set_Value("AD_User_ID", (Object)AD_User_ID);
        }
    }
    
    public int getAD_User_ID() {
        final Integer ii = (Integer)this.get_Value("AD_User_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Activity getC_Activity() throws RuntimeException {
        return (I_C_Activity)MTable.get(this.getCtx(), "C_Activity").getPO(this.getC_Activity_ID(), this.get_TrxName());
    }
    
    public void setC_Activity_ID(final int C_Activity_ID) {
        if (C_Activity_ID < 1) {
            this.set_Value("C_Activity_ID", (Object)null);
        }
        else {
            this.set_Value("C_Activity_ID", (Object)C_Activity_ID);
        }
    }
    
    public int getC_Activity_ID() {
        final Integer ii = (Integer)this.get_Value("C_Activity_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_BPartner getC_BPartner() throws RuntimeException {
        return (I_C_BPartner)MTable.get(this.getCtx(), "C_BPartner").getPO(this.getC_BPartner_ID(), this.get_TrxName());
    }
    
    public void setC_BPartner_ID(final int C_BPartner_ID) {
        if (C_BPartner_ID < 1) {
            this.set_Value("C_BPartner_ID", (Object)null);
        }
        else {
            this.set_Value("C_BPartner_ID", (Object)C_BPartner_ID);
        }
    }
    
    public int getC_BPartner_ID() {
        final Integer ii = (Integer)this.get_Value("C_BPartner_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException {
        return (I_C_BPartner_Location)MTable.get(this.getCtx(), "C_BPartner_Location").getPO(this.getC_BPartner_Location_ID(), this.get_TrxName());
    }
    
    public void setC_BPartner_Location_ID(final int C_BPartner_Location_ID) {
        if (C_BPartner_Location_ID < 1) {
            this.set_Value("C_BPartner_Location_ID", (Object)null);
        }
        else {
            this.set_Value("C_BPartner_Location_ID", (Object)C_BPartner_Location_ID);
        }
    }
    
    public int getC_BPartner_Location_ID() {
        final Integer ii = (Integer)this.get_Value("C_BPartner_Location_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Campaign getC_Campaign() throws RuntimeException {
        return (I_C_Campaign)MTable.get(this.getCtx(), "C_Campaign").getPO(this.getC_Campaign_ID(), this.get_TrxName());
    }
    
    public void setC_Campaign_ID(final int C_Campaign_ID) {
        if (C_Campaign_ID < 1) {
            this.set_Value("C_Campaign_ID", (Object)null);
        }
        else {
            this.set_Value("C_Campaign_ID", (Object)C_Campaign_ID);
        }
    }
    
    public int getC_Campaign_ID() {
        final Integer ii = (Integer)this.get_Value("C_Campaign_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Charge getC_Charge() throws RuntimeException {
        return (I_C_Charge)MTable.get(this.getCtx(), "C_Charge").getPO(this.getC_Charge_ID(), this.get_TrxName());
    }
    
    public void setC_Charge_ID(final int C_Charge_ID) {
        if (C_Charge_ID < 1) {
            this.set_Value("C_Charge_ID", (Object)null);
        }
        else {
            this.set_Value("C_Charge_ID", (Object)C_Charge_ID);
        }
    }
    
    public int getC_Charge_ID() {
        final Integer ii = (Integer)this.get_Value("C_Charge_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_DocType getC_DocType() throws RuntimeException {
        return (I_C_DocType)MTable.get(this.getCtx(), "C_DocType").getPO(this.getC_DocType_ID(), this.get_TrxName());
    }
    
    public void setC_DocType_ID(final int C_DocType_ID) {
        if (C_DocType_ID < 0) {
            this.set_ValueNoCheck("C_DocType_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("C_DocType_ID", (Object)C_DocType_ID);
        }
    }
    
    public int getC_DocType_ID() {
        final Integer ii = (Integer)this.get_Value("C_DocType_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setChargeAmt(final BigDecimal ChargeAmt) {
        this.set_Value("ChargeAmt", (Object)ChargeAmt);
    }
    
    public BigDecimal getChargeAmt() {
        final BigDecimal bd = (BigDecimal)this.get_Value("ChargeAmt");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public I_C_Invoice getC_Invoice() throws RuntimeException {
        return (I_C_Invoice)MTable.get(this.getCtx(), "C_Invoice").getPO(this.getC_Invoice_ID(), this.get_TrxName());
    }
    
    public void setC_Invoice_ID(final int C_Invoice_ID) {
        if (C_Invoice_ID < 1) {
            this.set_ValueNoCheck("C_Invoice_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("C_Invoice_ID", (Object)C_Invoice_ID);
        }
    }
    
    public int getC_Invoice_ID() {
        final Integer ii = (Integer)this.get_Value("C_Invoice_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Order getC_Order() throws RuntimeException {
        return (I_C_Order)MTable.get(this.getCtx(), "C_Order").getPO(this.getC_Order_ID(), this.get_TrxName());
    }
    
    public void setC_Order_ID(final int C_Order_ID) {
        if (C_Order_ID < 1) {
            this.set_ValueNoCheck("C_Order_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("C_Order_ID", (Object)C_Order_ID);
        }
    }
    
    public int getC_Order_ID() {
        final Integer ii = (Integer)this.get_Value("C_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Project getC_Project() throws RuntimeException {
        return (I_C_Project)MTable.get(this.getCtx(), "C_Project").getPO(this.getC_Project_ID(), this.get_TrxName());
    }
    
    public void setC_Project_ID(final int C_Project_ID) {
        if (C_Project_ID < 1) {
            this.set_Value("C_Project_ID", (Object)null);
        }
        else {
            this.set_Value("C_Project_ID", (Object)C_Project_ID);
        }
    }
    
    public int getC_Project_ID() {
        final Integer ii = (Integer)this.get_Value("C_Project_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setCreateConfirm(final String CreateConfirm) {
        this.set_Value("CreateConfirm", (Object)CreateConfirm);
    }
    
    public String getCreateConfirm() {
        return (String)this.get_Value("CreateConfirm");
    }
    
    public void setCreateFrom(final String CreateFrom) {
        this.set_Value("CreateFrom", (Object)CreateFrom);
    }
    
    public String getCreateFrom() {
        return (String)this.get_Value("CreateFrom");
    }
    
    public void setCreatePackage(final String CreatePackage) {
        this.set_Value("CreatePackage", (Object)CreatePackage);
    }
    
    public String getCreatePackage() {
        return (String)this.get_Value("CreatePackage");
    }
    
    public void setDateOrdered(final Timestamp DateOrdered) {
        this.set_ValueNoCheck("DateOrdered", (Object)DateOrdered);
    }
    
    public Timestamp getDateOrdered() {
        return (Timestamp)this.get_Value("DateOrdered");
    }
    
    public void setDatePrinted(final Timestamp DatePrinted) {
        this.set_Value("DatePrinted", (Object)DatePrinted);
    }
    
    public Timestamp getDatePrinted() {
        return (Timestamp)this.get_Value("DatePrinted");
    }
    
    public void setDatePromised(final Timestamp DatePromised) {
        this.set_Value("DatePromised", (Object)DatePromised);
    }
    
    public Timestamp getDatePromised() {
        return (Timestamp)this.get_Value("DatePromised");
    }
    
    public void setDateReceived(final Timestamp DateReceived) {
        this.set_Value("DateReceived", (Object)DateReceived);
    }
    
    public Timestamp getDateReceived() {
        return (Timestamp)this.get_Value("DateReceived");
    }
    
    public void setDD_Order_ID(final int DD_Order_ID) {
        if (DD_Order_ID < 1) {
            this.set_ValueNoCheck("DD_Order_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("DD_Order_ID", (Object)DD_Order_ID);
        }
    }
    
    public int getDD_Order_ID() {
        final Integer ii = (Integer)this.get_Value("DD_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDD_Order_UU(final String DD_Order_UU) {
        this.set_Value("DD_Order_UU", (Object)DD_Order_UU);
    }
    
    public String getDD_Order_UU() {
        return (String)this.get_Value("DD_Order_UU");
    }
    
    public void setDeliveryRule(final String DeliveryRule) {
        this.set_Value("DeliveryRule", (Object)DeliveryRule);
    }
    
    public String getDeliveryRule() {
        return (String)this.get_Value("DeliveryRule");
    }
    
    public void setDeliveryViaRule(final String DeliveryViaRule) {
        this.set_Value("DeliveryViaRule", (Object)DeliveryViaRule);
    }
    
    public String getDeliveryViaRule() {
        return (String)this.get_Value("DeliveryViaRule");
    }
    
    public void setDescription(final String Description) {
        this.set_Value("Description", (Object)Description);
    }
    
    public String getDescription() {
        return (String)this.get_Value("Description");
    }
    
    public void setDocAction(final String DocAction) {
        this.set_Value("DocAction", (Object)DocAction);
    }
    
    public String getDocAction() {
        return (String)this.get_Value("DocAction");
    }
    
    public void setDocStatus(final String DocStatus) {
        this.set_Value("DocStatus", (Object)DocStatus);
    }
    
    public String getDocStatus() {
        return (String)this.get_Value("DocStatus");
    }
    
    public void setDocumentNo(final String DocumentNo) {
        this.set_ValueNoCheck("DocumentNo", (Object)DocumentNo);
    }
    
    public String getDocumentNo() {
        return (String)this.get_Value("DocumentNo");
    }
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), this.getDocumentNo());
    }
    
    public void setFreightAmt(final BigDecimal FreightAmt) {
        this.set_Value("FreightAmt", (Object)FreightAmt);
    }
    
    public BigDecimal getFreightAmt() {
        final BigDecimal bd = (BigDecimal)this.get_Value("FreightAmt");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setFreightCostRule(final String FreightCostRule) {
        this.set_Value("FreightCostRule", (Object)FreightCostRule);
    }
    
    public String getFreightCostRule() {
        return (String)this.get_Value("FreightCostRule");
    }
    
    public void setGenerateTo(final String GenerateTo) {
        this.set_Value("GenerateTo", (Object)GenerateTo);
    }
    
    public String getGenerateTo() {
        return (String)this.get_Value("GenerateTo");
    }
    
    public void setIsApproved(final boolean IsApproved) {
        this.set_Value("IsApproved", (Object)IsApproved);
    }
    
    public boolean isApproved() {
        final Object oo = this.get_Value("IsApproved");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsDelivered(final boolean IsDelivered) {
        this.set_Value("IsDelivered", (Object)IsDelivered);
    }
    
    public boolean isDelivered() {
        final Object oo = this.get_Value("IsDelivered");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsDropShip(final boolean IsDropShip) {
        this.set_Value("IsDropShip", (Object)IsDropShip);
    }
    
    public boolean isDropShip() {
        final Object oo = this.get_Value("IsDropShip");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsInDispute(final boolean IsInDispute) {
        this.set_Value("IsInDispute", (Object)IsInDispute);
    }
    
    public boolean isInDispute() {
        final Object oo = this.get_Value("IsInDispute");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsInTransit(final boolean IsInTransit) {
        this.set_Value("IsInTransit", (Object)IsInTransit);
    }
    
    public boolean isInTransit() {
        final Object oo = this.get_Value("IsInTransit");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsPrinted(final boolean IsPrinted) {
        this.set_Value("IsPrinted", (Object)IsPrinted);
    }
    
    public boolean isPrinted() {
        final Object oo = this.get_Value("IsPrinted");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsSelected(final boolean IsSelected) {
        this.set_Value("IsSelected", (Object)IsSelected);
    }
    
    public boolean isSelected() {
        final Object oo = this.get_Value("IsSelected");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsSOTrx(final boolean IsSOTrx) {
        this.set_Value("IsSOTrx", (Object)IsSOTrx);
    }
    
    public boolean isSOTrx() {
        final Object oo = this.get_Value("IsSOTrx");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public I_M_Shipper getM_Shipper() throws RuntimeException {
        return (I_M_Shipper)MTable.get(this.getCtx(), "M_Shipper").getPO(this.getM_Shipper_ID(), this.get_TrxName());
    }
    
    public void setM_Shipper_ID(final int M_Shipper_ID) {
        if (M_Shipper_ID < 1) {
            this.set_Value("M_Shipper_ID", (Object)null);
        }
        else {
            this.set_Value("M_Shipper_ID", (Object)M_Shipper_ID);
        }
    }
    
    public int getM_Shipper_ID() {
        final Integer ii = (Integer)this.get_Value("M_Shipper_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_Warehouse getM_Warehouse() throws RuntimeException {
        return (I_M_Warehouse)MTable.get(this.getCtx(), "M_Warehouse").getPO(this.getM_Warehouse_ID(), this.get_TrxName());
    }
    
    public void setM_Warehouse_ID(final int M_Warehouse_ID) {
        if (M_Warehouse_ID < 1) {
            this.set_ValueNoCheck("M_Warehouse_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("M_Warehouse_ID", (Object)M_Warehouse_ID);
        }
    }
    
    public int getM_Warehouse_ID() {
        final Integer ii = (Integer)this.get_Value("M_Warehouse_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setNoPackages(final int NoPackages) {
        this.set_Value("NoPackages", (Object)NoPackages);
    }
    
    public int getNoPackages() {
        final Integer ii = (Integer)this.get_Value("NoPackages");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPickDate(final Timestamp PickDate) {
        this.set_Value("PickDate", (Object)PickDate);
    }
    
    public Timestamp getPickDate() {
        return (Timestamp)this.get_Value("PickDate");
    }
    
    public void setPOReference(final String POReference) {
        this.set_Value("POReference", (Object)POReference);
    }
    
    public String getPOReference() {
        return (String)this.get_Value("POReference");
    }
    
    public void setPosted(final boolean Posted) {
        this.set_Value("Posted", (Object)Posted);
    }
    
    public boolean isPosted() {
        final Object oo = this.get_Value("Posted");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setPriorityRule(final String PriorityRule) {
        this.set_Value("PriorityRule", (Object)PriorityRule);
    }
    
    public String getPriorityRule() {
        return (String)this.get_Value("PriorityRule");
    }
    
    public void setProcessed(final boolean Processed) {
        this.set_Value("Processed", (Object)Processed);
    }
    
    public boolean isProcessed() {
        final Object oo = this.get_Value("Processed");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setProcessedOn(final BigDecimal ProcessedOn) {
        this.set_Value("ProcessedOn", (Object)ProcessedOn);
    }
    
    public BigDecimal getProcessedOn() {
        final BigDecimal bd = (BigDecimal)this.get_Value("ProcessedOn");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setProcessing(final boolean Processing) {
        this.set_Value("Processing", (Object)Processing);
    }
    
    public boolean isProcessing() {
        final Object oo = this.get_Value("Processing");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public I_C_Order getRef_Order() throws RuntimeException {
        return (I_C_Order)MTable.get(this.getCtx(), "C_Order").getPO(this.getRef_Order_ID(), this.get_TrxName());
    }
    
    public void setRef_Order_ID(final int Ref_Order_ID) {
        if (Ref_Order_ID < 1) {
            this.set_Value("Ref_Order_ID", (Object)null);
        }
        else {
            this.set_Value("Ref_Order_ID", (Object)Ref_Order_ID);
        }
    }
    
    public int getRef_Order_ID() {
        final Integer ii = (Integer)this.get_Value("Ref_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_User getSalesRep() throws RuntimeException {
        return (I_AD_User)MTable.get(this.getCtx(), "AD_User").getPO(this.getSalesRep_ID(), this.get_TrxName());
    }
    
    public void setSalesRep_ID(final int SalesRep_ID) {
        if (SalesRep_ID < 1) {
            this.set_Value("SalesRep_ID", (Object)null);
        }
        else {
            this.set_Value("SalesRep_ID", (Object)SalesRep_ID);
        }
    }
    
    public int getSalesRep_ID() {
        final Integer ii = (Integer)this.get_Value("SalesRep_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setSendEMail(final boolean SendEMail) {
        this.set_Value("SendEMail", (Object)SendEMail);
    }
    
    public boolean isSendEMail() {
        final Object oo = this.get_Value("SendEMail");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setShipDate(final Timestamp ShipDate) {
        this.set_Value("ShipDate", (Object)ShipDate);
    }
    
    public Timestamp getShipDate() {
        return (Timestamp)this.get_Value("ShipDate");
    }
    
    public void setTrackingNo(final String TrackingNo) {
        this.set_Value("TrackingNo", (Object)TrackingNo);
    }
    
    public String getTrackingNo() {
        return (String)this.get_Value("TrackingNo");
    }
    
    public I_C_ElementValue getUser1() throws RuntimeException {
        return (I_C_ElementValue)MTable.get(this.getCtx(), "C_ElementValue").getPO(this.getUser1_ID(), this.get_TrxName());
    }
    
    public void setUser1_ID(final int User1_ID) {
        if (User1_ID < 1) {
            this.set_Value("User1_ID", (Object)null);
        }
        else {
            this.set_Value("User1_ID", (Object)User1_ID);
        }
    }
    
    public int getUser1_ID() {
        final Integer ii = (Integer)this.get_Value("User1_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_ElementValue getUser2() throws RuntimeException {
        return (I_C_ElementValue)MTable.get(this.getCtx(), "C_ElementValue").getPO(this.getUser2_ID(), this.get_TrxName());
    }
    
    public void setUser2_ID(final int User2_ID) {
        if (User2_ID < 1) {
            this.set_Value("User2_ID", (Object)null);
        }
        else {
            this.set_Value("User2_ID", (Object)User2_ID);
        }
    }
    
    public int getUser2_ID() {
        final Integer ii = (Integer)this.get_Value("User2_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setVolume(final BigDecimal Volume) {
        this.set_Value("Volume", (Object)Volume);
    }
    
    public BigDecimal getVolume() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Volume");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setWeight(final BigDecimal Weight) {
        this.set_Value("Weight", (Object)Weight);
    }
    
    public BigDecimal getWeight() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Weight");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
}
