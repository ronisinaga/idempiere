// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Order;
import org.compiere.util.KeyNamePair;
import java.sql.Timestamp;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.I_AD_Window;
import org.compiere.model.I_AD_WF_Responsible;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.I_AD_WF_Block;
import org.compiere.model.I_AD_Task;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_Image;
import org.compiere.model.I_AD_Form;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_Column;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_Node extends PO implements I_PP_Order_Node, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int ACTION_AD_Reference_ID = 302;
    public static final String ACTION_WaitSleep = "Z";
    public static final String ACTION_UserChoice = "C";
    public static final String ACTION_SubWorkflow = "F";
    public static final String ACTION_SetVariable = "V";
    public static final String ACTION_UserWindow = "W";
    public static final String ACTION_UserForm = "X";
    public static final String ACTION_AppsTask = "T";
    public static final String ACTION_AppsReport = "R";
    public static final String ACTION_AppsProcess = "P";
    public static final String ACTION_DocumentAction = "D";
    public static final String ACTION_EMail = "M";
    public static final String ACTION_UserWorkbench = "B";
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
    public static final int ENTITYTYPE_AD_Reference_ID = 389;
    public static final int FINISHMODE_AD_Reference_ID = 303;
    public static final String FINISHMODE_Automatic = "A";
    public static final String FINISHMODE_Manual = "M";
    public static final int JOINELEMENT_AD_Reference_ID = 301;
    public static final String JOINELEMENT_AND = "A";
    public static final String JOINELEMENT_XOR = "X";
    public static final int SPLITELEMENT_AD_Reference_ID = 301;
    public static final String SPLITELEMENT_AND = "A";
    public static final String SPLITELEMENT_XOR = "X";
    public static final int STARTMODE_AD_Reference_ID = 303;
    public static final String STARTMODE_Automatic = "A";
    public static final String STARTMODE_Manual = "M";
    public static final int SUBFLOWEXECUTION_AD_Reference_ID = 307;
    public static final String SUBFLOWEXECUTION_Asynchronously = "A";
    public static final String SUBFLOWEXECUTION_Synchronously = "S";
    
    public X_PP_Order_Node(final Properties ctx, final int PP_Order_Node_ID, final String trxName) {
        super(ctx, PP_Order_Node_ID, trxName);
    }
    
    public X_PP_Order_Node(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_Node.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53022, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_Node[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setAction(final String Action) {
        this.set_Value("Action", (Object)Action);
    }
    
    public String getAction() {
        return (String)this.get_Value("Action");
    }
    
    public I_AD_Column getAD_Column() throws RuntimeException {
        return (I_AD_Column)MTable.get(this.getCtx(), "AD_Column").getPO(this.getAD_Column_ID(), this.get_TrxName());
    }
    
    public void setAD_Column_ID(final int AD_Column_ID) {
        if (AD_Column_ID < 1) {
            this.set_Value("AD_Column_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Column_ID", (Object)AD_Column_ID);
        }
    }
    
    public int getAD_Column_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Column_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_Form getAD_Form() throws RuntimeException {
        return (I_AD_Form)MTable.get(this.getCtx(), "AD_Form").getPO(this.getAD_Form_ID(), this.get_TrxName());
    }
    
    public void setAD_Form_ID(final int AD_Form_ID) {
        if (AD_Form_ID < 1) {
            this.set_Value("AD_Form_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Form_ID", (Object)AD_Form_ID);
        }
    }
    
    public int getAD_Form_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Form_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_Image getAD_Image() throws RuntimeException {
        return (I_AD_Image)MTable.get(this.getCtx(), "AD_Image").getPO(this.getAD_Image_ID(), this.get_TrxName());
    }
    
    public void setAD_Image_ID(final int AD_Image_ID) {
        if (AD_Image_ID < 1) {
            this.set_Value("AD_Image_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Image_ID", (Object)AD_Image_ID);
        }
    }
    
    public int getAD_Image_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Image_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_Process getAD_Process() throws RuntimeException {
        return (I_AD_Process)MTable.get(this.getCtx(), "AD_Process").getPO(this.getAD_Process_ID(), this.get_TrxName());
    }
    
    public void setAD_Process_ID(final int AD_Process_ID) {
        if (AD_Process_ID < 1) {
            this.set_Value("AD_Process_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Process_ID", (Object)AD_Process_ID);
        }
    }
    
    public int getAD_Process_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Process_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_Task getAD_Task() throws RuntimeException {
        return (I_AD_Task)MTable.get(this.getCtx(), "AD_Task").getPO(this.getAD_Task_ID(), this.get_TrxName());
    }
    
    public void setAD_Task_ID(final int AD_Task_ID) {
        if (AD_Task_ID < 1) {
            this.set_Value("AD_Task_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Task_ID", (Object)AD_Task_ID);
        }
    }
    
    public int getAD_Task_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Task_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_WF_Block getAD_WF_Block() throws RuntimeException {
        return (I_AD_WF_Block)MTable.get(this.getCtx(), "AD_WF_Block").getPO(this.getAD_WF_Block_ID(), this.get_TrxName());
    }
    
    public void setAD_WF_Block_ID(final int AD_WF_Block_ID) {
        if (AD_WF_Block_ID < 1) {
            this.set_Value("AD_WF_Block_ID", (Object)null);
        }
        else {
            this.set_Value("AD_WF_Block_ID", (Object)AD_WF_Block_ID);
        }
    }
    
    public int getAD_WF_Block_ID() {
        final Integer ii = (Integer)this.get_Value("AD_WF_Block_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_WF_Node getAD_WF_Node() throws RuntimeException {
        return (I_AD_WF_Node)MTable.get(this.getCtx(), "AD_WF_Node").getPO(this.getAD_WF_Node_ID(), this.get_TrxName());
    }
    
    public void setAD_WF_Node_ID(final int AD_WF_Node_ID) {
        if (AD_WF_Node_ID < 1) {
            this.set_Value("AD_WF_Node_ID", (Object)null);
        }
        else {
            this.set_Value("AD_WF_Node_ID", (Object)AD_WF_Node_ID);
        }
    }
    
    public int getAD_WF_Node_ID() {
        final Integer ii = (Integer)this.get_Value("AD_WF_Node_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_WF_Responsible getAD_WF_Responsible() throws RuntimeException {
        return (I_AD_WF_Responsible)MTable.get(this.getCtx(), "AD_WF_Responsible").getPO(this.getAD_WF_Responsible_ID(), this.get_TrxName());
    }
    
    public void setAD_WF_Responsible_ID(final int AD_WF_Responsible_ID) {
        if (AD_WF_Responsible_ID < 1) {
            this.set_Value("AD_WF_Responsible_ID", (Object)null);
        }
        else {
            this.set_Value("AD_WF_Responsible_ID", (Object)AD_WF_Responsible_ID);
        }
    }
    
    public int getAD_WF_Responsible_ID() {
        final Integer ii = (Integer)this.get_Value("AD_WF_Responsible_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_Window getAD_Window() throws RuntimeException {
        return (I_AD_Window)MTable.get(this.getCtx(), "AD_Window").getPO(this.getAD_Window_ID(), this.get_TrxName());
    }
    
    public void setAD_Window_ID(final int AD_Window_ID) {
        if (AD_Window_ID < 1) {
            this.set_Value("AD_Window_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Window_ID", (Object)AD_Window_ID);
        }
    }
    
    public int getAD_Window_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Window_ID");
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
            this.set_Value("AD_Workflow_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Workflow_ID", (Object)AD_Workflow_ID);
        }
    }
    
    public int getAD_Workflow_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Workflow_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setAttributeName(final String AttributeName) {
        this.set_Value("AttributeName", (Object)AttributeName);
    }
    
    public String getAttributeName() {
        return (String)this.get_Value("AttributeName");
    }
    
    public void setAttributeValue(final String AttributeValue) {
        this.set_Value("AttributeValue", (Object)AttributeValue);
    }
    
    public String getAttributeValue() {
        return (String)this.get_Value("AttributeValue");
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
    
    public void setCost(final BigDecimal Cost) {
        this.set_Value("Cost", (Object)Cost);
    }
    
    public BigDecimal getCost() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Cost");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setDateFinish(final Timestamp DateFinish) {
        this.set_Value("DateFinish", (Object)DateFinish);
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
    
    public void setDateStart(final Timestamp DateStart) {
        this.set_Value("DateStart", (Object)DateStart);
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
    
    public void setDuration(final int Duration) {
        this.set_Value("Duration", (Object)Duration);
    }
    
    public int getDuration() {
        final Integer ii = (Integer)this.get_Value("Duration");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDurationReal(final int DurationReal) {
        this.set_Value("DurationReal", (Object)DurationReal);
    }
    
    public int getDurationReal() {
        final Integer ii = (Integer)this.get_Value("DurationReal");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDurationRequired(final int DurationRequired) {
        this.set_Value("DurationRequired", (Object)DurationRequired);
    }
    
    public int getDurationRequired() {
        final Integer ii = (Integer)this.get_Value("DurationRequired");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setEntityType(final String EntityType) {
        this.set_Value("EntityType", (Object)EntityType);
    }
    
    public String getEntityType() {
        return (String)this.get_Value("EntityType");
    }
    
    public void setFinishMode(final String FinishMode) {
        this.set_Value("FinishMode", (Object)FinishMode);
    }
    
    public String getFinishMode() {
        return (String)this.get_Value("FinishMode");
    }
    
    public void setHelp(final String Help) {
        this.set_Value("Help", (Object)Help);
    }
    
    public String getHelp() {
        return (String)this.get_Value("Help");
    }
    
    public void setIsCentrallyMaintained(final boolean IsCentrallyMaintained) {
        this.set_Value("IsCentrallyMaintained", (Object)IsCentrallyMaintained);
    }
    
    public boolean isCentrallyMaintained() {
        final Object oo = this.get_Value("IsCentrallyMaintained");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsMilestone(final boolean IsMilestone) {
        this.set_Value("IsMilestone", (Object)IsMilestone);
    }
    
    public boolean isMilestone() {
        final Object oo = this.get_Value("IsMilestone");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsSubcontracting(final boolean IsSubcontracting) {
        this.set_Value("IsSubcontracting", (Object)IsSubcontracting);
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
    
    public void setJoinElement(final String JoinElement) {
        this.set_Value("JoinElement", (Object)JoinElement);
    }
    
    public String getJoinElement() {
        return (String)this.get_Value("JoinElement");
    }
    
    public void setLimit(final int Limit) {
        this.set_Value("Limit", (Object)Limit);
    }
    
    public int getLimit() {
        final Integer ii = (Integer)this.get_Value("Limit");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setMovingTime(final int MovingTime) {
        this.set_Value("MovingTime", (Object)MovingTime);
    }
    
    public int getMovingTime() {
        final Integer ii = (Integer)this.get_Value("MovingTime");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setName(final String Name) {
        this.set_Value("Name", (Object)Name);
    }
    
    public String getName() {
        return (String)this.get_Value("Name");
    }
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), this.getName());
    }
    
    public void setOverlapUnits(final int OverlapUnits) {
        this.set_Value("OverlapUnits", (Object)OverlapUnits);
    }
    
    public int getOverlapUnits() {
        final Integer ii = (Integer)this.get_Value("OverlapUnits");
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
    
    public void setPP_Order_Node_ID(final int PP_Order_Node_ID) {
        if (PP_Order_Node_ID < 1) {
            this.set_ValueNoCheck("PP_Order_Node_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_Node_ID", (Object)PP_Order_Node_ID);
        }
    }
    
    public int getPP_Order_Node_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Node_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_Node_UU(final String PP_Order_Node_UU) {
        this.set_Value("PP_Order_Node_UU", (Object)PP_Order_Node_UU);
    }
    
    public String getPP_Order_Node_UU() {
        return (String)this.get_Value("PP_Order_Node_UU");
    }
    
    public I_PP_Order_Workflow getPP_Order_Workflow() throws RuntimeException {
        return (I_PP_Order_Workflow)MTable.get(this.getCtx(), "PP_Order_Workflow").getPO(this.getPP_Order_Workflow_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_Workflow_ID(final int PP_Order_Workflow_ID) {
        if (PP_Order_Workflow_ID < 1) {
            this.set_ValueNoCheck("PP_Order_Workflow_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_Workflow_ID", (Object)PP_Order_Workflow_ID);
        }
    }
    
    public int getPP_Order_Workflow_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Workflow_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPriority(final int Priority) {
        this.set_Value("Priority", (Object)Priority);
    }
    
    public int getPriority() {
        final Integer ii = (Integer)this.get_Value("Priority");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setQtyRequired(final BigDecimal QtyRequired) {
        this.set_Value("QtyRequired", (Object)QtyRequired);
    }
    
    public BigDecimal getQtyRequired() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyRequired");
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
    
    public void setQueuingTime(final int QueuingTime) {
        this.set_Value("QueuingTime", (Object)QueuingTime);
    }
    
    public int getQueuingTime() {
        final Integer ii = (Integer)this.get_Value("QueuingTime");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setSetupTime(final int SetupTime) {
        this.set_Value("SetupTime", (Object)SetupTime);
    }
    
    public int getSetupTime() {
        final Integer ii = (Integer)this.get_Value("SetupTime");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setSetupTimeReal(final int SetupTimeReal) {
        this.set_Value("SetupTimeReal", (Object)SetupTimeReal);
    }
    
    public int getSetupTimeReal() {
        final Integer ii = (Integer)this.get_Value("SetupTimeReal");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setSetupTimeRequired(final int SetupTimeRequired) {
        this.set_Value("SetupTimeRequired", (Object)SetupTimeRequired);
    }
    
    public int getSetupTimeRequired() {
        final Integer ii = (Integer)this.get_Value("SetupTimeRequired");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setSplitElement(final String SplitElement) {
        this.set_Value("SplitElement", (Object)SplitElement);
    }
    
    public String getSplitElement() {
        return (String)this.get_Value("SplitElement");
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
    
    public void setStartMode(final String StartMode) {
        this.set_Value("StartMode", (Object)StartMode);
    }
    
    public String getStartMode() {
        return (String)this.get_Value("StartMode");
    }
    
    public void setSubflowExecution(final String SubflowExecution) {
        this.set_Value("SubflowExecution", (Object)SubflowExecution);
    }
    
    public String getSubflowExecution() {
        return (String)this.get_Value("SubflowExecution");
    }
    
    public void setUnitsCycles(final int UnitsCycles) {
        this.set_Value("UnitsCycles", (Object)UnitsCycles);
    }
    
    public int getUnitsCycles() {
        final Integer ii = (Integer)this.get_Value("UnitsCycles");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setValidFrom(final Timestamp ValidFrom) {
        this.set_Value("ValidFrom", (Object)ValidFrom);
    }
    
    public Timestamp getValidFrom() {
        return (Timestamp)this.get_Value("ValidFrom");
    }
    
    public void setValidTo(final Timestamp ValidTo) {
        this.set_Value("ValidTo", (Object)ValidTo);
    }
    
    public Timestamp getValidTo() {
        return (Timestamp)this.get_Value("ValidTo");
    }
    
    public void setValue(final String Value) {
        this.set_Value("Value", (Object)Value);
    }
    
    public String getValue() {
        return (String)this.get_Value("Value");
    }
    
    public void setWaitingTime(final int WaitingTime) {
        this.set_Value("WaitingTime", (Object)WaitingTime);
    }
    
    public int getWaitingTime() {
        final Integer ii = (Integer)this.get_Value("WaitingTime");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_Workflow getWorkflow() throws RuntimeException {
        return (I_AD_Workflow)MTable.get(this.getCtx(), "AD_Workflow").getPO(this.getWorkflow_ID(), this.get_TrxName());
    }
    
    public void setWorkflow_ID(final int Workflow_ID) {
        if (Workflow_ID < 1) {
            this.set_Value("Workflow_ID", (Object)null);
        }
        else {
            this.set_Value("Workflow_ID", (Object)Workflow_ID);
        }
    }
    
    public int getWorkflow_ID() {
        final Integer ii = (Integer)this.get_Value("Workflow_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setWorkingTime(final int WorkingTime) {
        this.set_Value("WorkingTime", (Object)WorkingTime);
    }
    
    public int getWorkingTime() {
        final Integer ii = (Integer)this.get_Value("WorkingTime");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setXPosition(final int XPosition) {
        this.set_Value("XPosition", (Object)XPosition);
    }
    
    public int getXPosition() {
        final Integer ii = (Integer)this.get_Value("XPosition");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setYield(final int Yield) {
        this.set_Value("Yield", (Object)Yield);
    }
    
    public int getYield() {
        final Integer ii = (Integer)this.get_Value("Yield");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setYPosition(final int YPosition) {
        this.set_Value("YPosition", (Object)YPosition);
    }
    
    public int getYPosition() {
        final Integer ii = (Integer)this.get_Value("YPosition");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
}
