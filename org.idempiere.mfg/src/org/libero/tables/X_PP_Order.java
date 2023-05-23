// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_C_ElementValue;
import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Product_BOM;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.util.KeyNamePair;
import java.sql.Timestamp;
import org.compiere.model.I_C_UOM;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.I_C_Activity;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order extends PO implements I_PP_Order, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
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
    public static final int PRIORITYRULE_AD_Reference_ID = 154;
    public static final String PRIORITYRULE_High = "3";
    public static final String PRIORITYRULE_Medium = "5";
    public static final String PRIORITYRULE_Low = "7";
    public static final String PRIORITYRULE_Urgent = "1";
    public static final String PRIORITYRULE_Minor = "9";
    
    public X_PP_Order(final Properties ctx, final int PP_Order_ID, final String trxName) {
        super(ctx, PP_Order_ID, trxName);
    }
    
    public X_PP_Order(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53027, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order[").append(this.get_ID()).append("]");
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
    
    public I_AD_Workflow getAD_Workflow() throws RuntimeException {
        return (I_AD_Workflow)MTable.get(this.getCtx(), "AD_Workflow").getPO(this.getAD_Workflow_ID(), this.get_TrxName());
    }
    
    public void setAD_Workflow_ID(final int AD_Workflow_ID) {
        if (AD_Workflow_ID < 1) {
            this.set_ValueNoCheck("AD_Workflow_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("AD_Workflow_ID", (Object)AD_Workflow_ID);
        }
    }
    
    public int getAD_Workflow_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Workflow_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setAssay(final BigDecimal Assay) {
        this.set_Value("Assay", (Object)Assay);
    }
    
    public BigDecimal getAssay() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Assay");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
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
    
    public I_C_DocType getC_DocType() throws RuntimeException {
        return (I_C_DocType)MTable.get(this.getCtx(), "C_DocType").getPO(this.getC_DocType_ID(), this.get_TrxName());
    }
    
    public void setC_DocType_ID(final int C_DocType_ID) {
        if (C_DocType_ID < 0) {
            this.set_Value("C_DocType_ID", (Object)null);
        }
        else {
            this.set_Value("C_DocType_ID", (Object)C_DocType_ID);
        }
    }
    
    public int getC_DocType_ID() {
        final Integer ii = (Integer)this.get_Value("C_DocType_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_DocType getC_DocTypeTarget() throws RuntimeException {
        return (I_C_DocType)MTable.get(this.getCtx(), "C_DocType").getPO(this.getC_DocTypeTarget_ID(), this.get_TrxName());
    }
    
    public void setC_DocTypeTarget_ID(final int C_DocTypeTarget_ID) {
        if (C_DocTypeTarget_ID < 1) {
            this.set_ValueNoCheck("C_DocTypeTarget_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("C_DocTypeTarget_ID", (Object)C_DocTypeTarget_ID);
        }
    }
    
    public int getC_DocTypeTarget_ID() {
        final Integer ii = (Integer)this.get_Value("C_DocTypeTarget_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setCopyFrom(final String CopyFrom) {
        this.set_Value("CopyFrom", (Object)CopyFrom);
    }
    
    public String getCopyFrom() {
        return (String)this.get_Value("CopyFrom");
    }
    
    public I_C_OrderLine getC_OrderLine() throws RuntimeException {
        return (I_C_OrderLine)MTable.get(this.getCtx(), "C_OrderLine").getPO(this.getC_OrderLine_ID(), this.get_TrxName());
    }
    
    public void setC_OrderLine_ID(final int C_OrderLine_ID) {
        if (C_OrderLine_ID < 1) {
            this.set_Value("C_OrderLine_ID", (Object)null);
        }
        else {
            this.set_Value("C_OrderLine_ID", (Object)C_OrderLine_ID);
        }
    }
    
    public int getC_OrderLine_ID() {
        final Integer ii = (Integer)this.get_Value("C_OrderLine_ID");
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
    
    public I_C_UOM getC_UOM() throws RuntimeException {
        return (I_C_UOM)MTable.get(this.getCtx(), "C_UOM").getPO(this.getC_UOM_ID(), this.get_TrxName());
    }
    
    public void setC_UOM_ID(final int C_UOM_ID) {
        if (C_UOM_ID < 1) {
            this.set_ValueNoCheck("C_UOM_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("C_UOM_ID", (Object)C_UOM_ID);
        }
    }
    
    public int getC_UOM_ID() {
        final Integer ii = (Integer)this.get_Value("C_UOM_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDateConfirm(final Timestamp DateConfirm) {
        this.set_ValueNoCheck("DateConfirm", (Object)DateConfirm);
    }
    
    public Timestamp getDateConfirm() {
        return (Timestamp)this.get_Value("DateConfirm");
    }
    
    public void setDateDelivered(final Timestamp DateDelivered) {
        this.set_ValueNoCheck("DateDelivered", (Object)DateDelivered);
    }
    
    public Timestamp getDateDelivered() {
        return (Timestamp)this.get_Value("DateDelivered");
    }
    
    public void setDateFinish(final Timestamp DateFinish) {
        this.set_ValueNoCheck("DateFinish", (Object)DateFinish);
    }
    
    public Timestamp getDateFinish() {
        return (Timestamp)this.get_Value("DateFinish");
    }
    
    public void setDateFinishSchedule(final Timestamp DateFinishSchedule) {
        this.set_Value("DateFinishSchedule", (Object)DateFinishSchedule);
    }
    
    public Timestamp getDateFinishSchedule() {
        return (Timestamp)this.get_Value("DateFinishSchedule");
    }
    
    public void setDateOrdered(final Timestamp DateOrdered) {
        this.set_Value("DateOrdered", (Object)DateOrdered);
    }
    
    public Timestamp getDateOrdered() {
        return (Timestamp)this.get_Value("DateOrdered");
    }
    
    public void setDatePromised(final Timestamp DatePromised) {
        this.set_Value("DatePromised", (Object)DatePromised);
    }
    
    public Timestamp getDatePromised() {
        return (Timestamp)this.get_Value("DatePromised");
    }
    
    public void setDateStart(final Timestamp DateStart) {
        this.set_ValueNoCheck("DateStart", (Object)DateStart);
    }
    
    public Timestamp getDateStart() {
        return (Timestamp)this.get_Value("DateStart");
    }
    
    public void setDateStartSchedule(final Timestamp DateStartSchedule) {
        this.set_Value("DateStartSchedule", (Object)DateStartSchedule);
    }
    
    public Timestamp getDateStartSchedule() {
        return (Timestamp)this.get_Value("DateStartSchedule");
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
        this.set_Value("DocumentNo", (Object)DocumentNo);
    }
    
    public String getDocumentNo() {
        return (String)this.get_Value("DocumentNo");
    }
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), this.getDocumentNo());
    }
    
    public void setFloatAfter(final BigDecimal FloatAfter) {
        this.set_Value("FloatAfter", (Object)FloatAfter);
    }
    
    public BigDecimal getFloatAfter() {
        final BigDecimal bd = (BigDecimal)this.get_Value("FloatAfter");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setFloatBefored(final BigDecimal FloatBefored) {
        this.set_Value("FloatBefored", (Object)FloatBefored);
    }
    
    public BigDecimal getFloatBefored() {
        final BigDecimal bd = (BigDecimal)this.get_Value("FloatBefored");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
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
    
    public void setIsQtyPercentage(final boolean IsQtyPercentage) {
        this.set_Value("IsQtyPercentage", (Object)IsQtyPercentage);
    }
    
    public boolean isQtyPercentage() {
        final Object oo = this.get_Value("IsQtyPercentage");
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
    
    public void setLine(final int Line) {
        this.set_Value("Line", (Object)Line);
    }
    
    public int getLine() {
        final Integer ii = (Integer)this.get_Value("Line");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setLot(final String Lot) {
        this.set_Value("Lot", (Object)Lot);
    }
    
    public String getLot() {
        return (String)this.get_Value("Lot");
    }
    
    public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException {
        return (I_M_AttributeSetInstance)MTable.get(this.getCtx(), "M_AttributeSetInstance").getPO(this.getM_AttributeSetInstance_ID(), this.get_TrxName());
    }
    
    public void setM_AttributeSetInstance_ID(final int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0) {
            this.set_Value("M_AttributeSetInstance_ID", (Object)null);
        }
        else {
            this.set_Value("M_AttributeSetInstance_ID", (Object)M_AttributeSetInstance_ID);
        }
    }
    
    public int getM_AttributeSetInstance_ID() {
        final Integer ii = (Integer)this.get_Value("M_AttributeSetInstance_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_Product getM_Product() throws RuntimeException {
        return (I_M_Product)MTable.get(this.getCtx(), "M_Product").getPO(this.getM_Product_ID(), this.get_TrxName());
    }
    
    public void setM_Product_ID(final int M_Product_ID) {
        if (M_Product_ID < 1) {
            this.set_ValueNoCheck("M_Product_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("M_Product_ID", (Object)M_Product_ID);
        }
    }
    
    public int getM_Product_ID() {
        final Integer ii = (Integer)this.get_Value("M_Product_ID");
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
    
    public void setOrderType(final String OrderType) {
        this.set_Value("OrderType", (Object)OrderType);
    }
    
    public String getOrderType() {
        return (String)this.get_Value("OrderType");
    }
    
    public I_AD_User getPlanner() throws RuntimeException {
        return (I_AD_User)MTable.get(this.getCtx(), "AD_User").getPO(this.getPlanner_ID(), this.get_TrxName());
    }
    
    public void setPlanner_ID(final int Planner_ID) {
        if (Planner_ID < 1) {
            this.set_Value("Planner_ID", (Object)null);
        }
        else {
            this.set_Value("Planner_ID", (Object)Planner_ID);
        }
    }
    
    public int getPlanner_ID() {
        final Integer ii = (Integer)this.get_Value("Planner_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setPP_Order_ID(final int PP_Order_ID) {
        if (PP_Order_ID < 1) {
            this.set_ValueNoCheck("PP_Order_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_ID", (Object)PP_Order_ID);
        }
    }
    
    public int getPP_Order_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_UU(final String PP_Order_UU) {
        this.set_Value("PP_Order_UU", (Object)PP_Order_UU);
    }
    
    public String getPP_Order_UU() {
        return (String)this.get_Value("PP_Order_UU");
    }
    
    public I_PP_Product_BOM getPP_Product_BOM() throws RuntimeException {
        return (I_PP_Product_BOM)MTable.get(this.getCtx(), "PP_Product_BOM").getPO(this.getPP_Product_BOM_ID(), this.get_TrxName());
    }
    
    public void setPP_Product_BOM_ID(final int PP_Product_BOM_ID) {
        if (PP_Product_BOM_ID < 1) {
            this.set_ValueNoCheck("PP_Product_BOM_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Product_BOM_ID", (Object)PP_Product_BOM_ID);
        }
    }
    
    public int getPP_Product_BOM_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Product_BOM_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setQtyBatchs(final BigDecimal QtyBatchs) {
        this.set_ValueNoCheck("QtyBatchs", (Object)QtyBatchs);
    }
    
    public BigDecimal getQtyBatchs() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyBatchs");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyBatchSize(final BigDecimal QtyBatchSize) {
        this.set_ValueNoCheck("QtyBatchSize", (Object)QtyBatchSize);
    }
    
    public BigDecimal getQtyBatchSize() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyBatchSize");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyDelivered(final BigDecimal QtyDelivered) {
        this.set_Value("QtyDelivered", (Object)QtyDelivered);
    }
    
    public BigDecimal getQtyDelivered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyDelivered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyEntered(final BigDecimal QtyEntered) {
        this.set_Value("QtyEntered", (Object)QtyEntered);
    }
    
    public BigDecimal getQtyEntered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyEntered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyOrdered(final BigDecimal QtyOrdered) {
        this.set_ValueNoCheck("QtyOrdered", (Object)QtyOrdered);
    }
    
    public BigDecimal getQtyOrdered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyOrdered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyReject(final BigDecimal QtyReject) {
        this.set_Value("QtyReject", (Object)QtyReject);
    }
    
    public BigDecimal getQtyReject() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyReject");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyReserved(final BigDecimal QtyReserved) {
        this.set_Value("QtyReserved", (Object)QtyReserved);
    }
    
    public BigDecimal getQtyReserved() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyReserved");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyScrap(final BigDecimal QtyScrap) {
        this.set_Value("QtyScrap", (Object)QtyScrap);
    }
    
    public BigDecimal getQtyScrap() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyScrap");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setScheduleType(final String ScheduleType) {
        this.set_Value("ScheduleType", (Object)ScheduleType);
    }
    
    public String getScheduleType() {
        return (String)this.get_Value("ScheduleType");
    }
    
    public void setSerNo(final String SerNo) {
        this.set_Value("SerNo", (Object)SerNo);
    }
    
    public String getSerNo() {
        return (String)this.get_Value("SerNo");
    }
    
    public I_S_Resource getS_Resource() throws RuntimeException {
        return (I_S_Resource)MTable.get(this.getCtx(), "S_Resource").getPO(this.getS_Resource_ID(), this.get_TrxName());
    }
    
    public void setS_Resource_ID(final int S_Resource_ID) {
        if (S_Resource_ID < 1) {
            this.set_ValueNoCheck("S_Resource_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("S_Resource_ID", (Object)S_Resource_ID);
        }
    }
    
    public int getS_Resource_ID() {
        final Integer ii = (Integer)this.get_Value("S_Resource_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setYield(final BigDecimal Yield) {
        this.set_Value("Yield", (Object)Yield);
    }
    
    public BigDecimal getYield() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Yield");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
}
