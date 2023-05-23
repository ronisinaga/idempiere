// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Warehouse;
import org.compiere.util.KeyNamePair;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.util.Env;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.I_C_UOM;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.I_C_Activity;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_User;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Cost_Collector extends PO implements I_PP_Cost_Collector, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int COSTCOLLECTORTYPE_AD_Reference_ID = 53287;
    public static final String COSTCOLLECTORTYPE_MaterialReceipt = "100";
    public static final String COSTCOLLECTORTYPE_ComponentIssue = "110";
    public static final String COSTCOLLECTORTYPE_UsegeVariance = "120";
    public static final String COSTCOLLECTORTYPE_MethodChangeVariance = "130";
    public static final String COSTCOLLECTORTYPE_RateVariance = "140";
    public static final String COSTCOLLECTORTYPE_MixVariance = "150";
    public static final String COSTCOLLECTORTYPE_ActivityControl = "160";
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
    
    public X_PP_Cost_Collector(final Properties ctx, final int PP_Cost_Collector_ID, final String trxName) {
        super(ctx, PP_Cost_Collector_ID, trxName);
    }
    
    public X_PP_Cost_Collector(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Cost_Collector.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53035, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Cost_Collector[").append(this.get_ID()).append("]");
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
    
    public void setCostCollectorType(final String CostCollectorType) {
        this.set_Value("CostCollectorType", (Object)CostCollectorType);
    }
    
    public String getCostCollectorType() {
        return (String)this.get_Value("CostCollectorType");
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
            this.set_Value("C_UOM_ID", (Object)null);
        }
        else {
            this.set_Value("C_UOM_ID", (Object)C_UOM_ID);
        }
    }
    
    public int getC_UOM_ID() {
        final Integer ii = (Integer)this.get_Value("C_UOM_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDateAcct(final Timestamp DateAcct) {
        this.set_Value("DateAcct", (Object)DateAcct);
    }
    
    public Timestamp getDateAcct() {
        return (Timestamp)this.get_Value("DateAcct");
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
    
    public void setDurationReal(final BigDecimal DurationReal) {
        this.set_Value("DurationReal", (Object)DurationReal);
    }
    
    public BigDecimal getDurationReal() {
        final BigDecimal bd = (BigDecimal)this.get_Value("DurationReal");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setIsBatchTime(final boolean IsBatchTime) {
        this.set_Value("IsBatchTime", (Object)IsBatchTime);
    }
    
    public boolean isBatchTime() {
        final Object oo = this.get_Value("IsBatchTime");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsSubcontracting(final boolean IsSubcontracting) {
        this.set_ValueNoCheck("IsSubcontracting", (Object)IsSubcontracting);
    }
    
    public boolean isSubcontracting() {
        final Object oo = this.get_Value("IsSubcontracting");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
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
    
    public I_M_Locator getM_Locator() throws RuntimeException {
        return (I_M_Locator)MTable.get(this.getCtx(), "M_Locator").getPO(this.getM_Locator_ID(), this.get_TrxName());
    }
    
    public void setM_Locator_ID(final int M_Locator_ID) {
        if (M_Locator_ID < 1) {
            this.set_Value("M_Locator_ID", (Object)null);
        }
        else {
            this.set_Value("M_Locator_ID", (Object)M_Locator_ID);
        }
    }
    
    public int getM_Locator_ID() {
        final Integer ii = (Integer)this.get_Value("M_Locator_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setMovementDate(final Timestamp MovementDate) {
        this.set_Value("MovementDate", (Object)MovementDate);
    }
    
    public Timestamp getMovementDate() {
        return (Timestamp)this.get_Value("MovementDate");
    }
    
    public void setMovementQty(final BigDecimal MovementQty) {
        this.set_Value("MovementQty", (Object)MovementQty);
    }
    
    public BigDecimal getMovementQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("MovementQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public I_M_Product getM_Product() throws RuntimeException {
        return (I_M_Product)MTable.get(this.getCtx(), "M_Product").getPO(this.getM_Product_ID(), this.get_TrxName());
    }
    
    public void setM_Product_ID(final int M_Product_ID) {
        if (M_Product_ID < 1) {
            this.set_Value("M_Product_ID", (Object)null);
        }
        else {
            this.set_Value("M_Product_ID", (Object)M_Product_ID);
        }
    }
    
    public int getM_Product_ID() {
        final Integer ii = (Integer)this.get_Value("M_Product_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), String.valueOf(this.getM_Product_ID()));
    }
    
    public I_M_Warehouse getM_Warehouse() throws RuntimeException {
        return (I_M_Warehouse)MTable.get(this.getCtx(), "M_Warehouse").getPO(this.getM_Warehouse_ID(), this.get_TrxName());
    }
    
    public void setM_Warehouse_ID(final int M_Warehouse_ID) {
        if (M_Warehouse_ID < 1) {
            this.set_Value("M_Warehouse_ID", (Object)null);
        }
        else {
            this.set_Value("M_Warehouse_ID", (Object)M_Warehouse_ID);
        }
    }
    
    public int getM_Warehouse_ID() {
        final Integer ii = (Integer)this.get_Value("M_Warehouse_ID");
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
    
    public void setPP_Cost_Collector_ID(final int PP_Cost_Collector_ID) {
        if (PP_Cost_Collector_ID < 1) {
            this.set_ValueNoCheck("PP_Cost_Collector_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Cost_Collector_ID", (Object)PP_Cost_Collector_ID);
        }
    }
    
    public int getPP_Cost_Collector_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Cost_Collector_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Cost_Collector_UU(final String PP_Cost_Collector_UU) {
        this.set_Value("PP_Cost_Collector_UU", (Object)PP_Cost_Collector_UU);
    }
    
    public String getPP_Cost_Collector_UU() {
        return (String)this.get_Value("PP_Cost_Collector_UU");
    }
    
    public I_PP_Order_BOMLine getPP_Order_BOMLine() throws RuntimeException {
        return (I_PP_Order_BOMLine)MTable.get(this.getCtx(), "PP_Order_BOMLine").getPO(this.getPP_Order_BOMLine_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_BOMLine_ID(final int PP_Order_BOMLine_ID) {
        if (PP_Order_BOMLine_ID < 1) {
            this.set_Value("PP_Order_BOMLine_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Order_BOMLine_ID", (Object)PP_Order_BOMLine_ID);
        }
    }
    
    public int getPP_Order_BOMLine_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_BOMLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_PP_Order getPP_Order() throws RuntimeException {
        return (I_PP_Order)MTable.get(this.getCtx(), "PP_Order").getPO(this.getPP_Order_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_ID(final int PP_Order_ID) {
        if (PP_Order_ID < 1) {
            this.set_Value("PP_Order_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Order_ID", (Object)PP_Order_ID);
        }
    }
    
    public int getPP_Order_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_PP_Order_Node getPP_Order_Node() throws RuntimeException {
        return (I_PP_Order_Node)MTable.get(this.getCtx(), "PP_Order_Node").getPO(this.getPP_Order_Node_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_Node_ID(final int PP_Order_Node_ID) {
        if (PP_Order_Node_ID < 1) {
            this.set_Value("PP_Order_Node_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Order_Node_ID", (Object)PP_Order_Node_ID);
        }
    }
    
    public int getPP_Order_Node_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Node_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_PP_Order_Workflow getPP_Order_Workflow() throws RuntimeException {
        return (I_PP_Order_Workflow)MTable.get(this.getCtx(), "PP_Order_Workflow").getPO(this.getPP_Order_Workflow_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_Workflow_ID(final int PP_Order_Workflow_ID) {
        if (PP_Order_Workflow_ID < 1) {
            this.set_Value("PP_Order_Workflow_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Order_Workflow_ID", (Object)PP_Order_Workflow_ID);
        }
    }
    
    public int getPP_Order_Workflow_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Workflow_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public I_PP_Cost_Collector getReversal() throws RuntimeException {
        return (I_PP_Cost_Collector)MTable.get(this.getCtx(), "PP_Cost_Collector").getPO(this.getReversal_ID(), this.get_TrxName());
    }
    
    public void setReversal_ID(final int Reversal_ID) {
        if (Reversal_ID < 1) {
            this.set_Value("Reversal_ID", (Object)null);
        }
        else {
            this.set_Value("Reversal_ID", (Object)Reversal_ID);
        }
    }
    
    public int getReversal_ID() {
        final Integer ii = (Integer)this.get_Value("Reversal_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setScrappedQty(final BigDecimal ScrappedQty) {
        this.set_Value("ScrappedQty", (Object)ScrappedQty);
    }
    
    public BigDecimal getScrappedQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("ScrappedQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setSetupTimeReal(final BigDecimal SetupTimeReal) {
        this.set_Value("SetupTimeReal", (Object)SetupTimeReal);
    }
    
    public BigDecimal getSetupTimeReal() {
        final BigDecimal bd = (BigDecimal)this.get_Value("SetupTimeReal");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public I_S_Resource getS_Resource() throws RuntimeException {
        return (I_S_Resource)MTable.get(this.getCtx(), "S_Resource").getPO(this.getS_Resource_ID(), this.get_TrxName());
    }
    
    public void setS_Resource_ID(final int S_Resource_ID) {
        if (S_Resource_ID < 1) {
            this.set_Value("S_Resource_ID", (Object)null);
        }
        else {
            this.set_Value("S_Resource_ID", (Object)S_Resource_ID);
        }
    }
    
    public int getS_Resource_ID() {
        final Integer ii = (Integer)this.get_Value("S_Resource_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_User getUser1() throws RuntimeException {
        return (I_AD_User)MTable.get(this.getCtx(), "AD_User").getPO(this.getUser1_ID(), this.get_TrxName());
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
    
    public I_AD_User getUser2() throws RuntimeException {
        return (I_AD_User)MTable.get(this.getCtx(), "AD_User").getPO(this.getUser2_ID(), this.get_TrxName());
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
}
