// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import java.sql.Timestamp;
import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Order;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_AD_WorkflowProcessor;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.I_AD_WF_Responsible;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_Table;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_Workflow extends PO implements I_PP_Order_Workflow, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int ACCESSLEVEL_AD_Reference_ID = 5;
    public static final String ACCESSLEVEL_Organization = "1";
    public static final String ACCESSLEVEL_ClientPlusOrganization = "3";
    public static final String ACCESSLEVEL_SystemOnly = "4";
    public static final String ACCESSLEVEL_All = "7";
    public static final String ACCESSLEVEL_SystemPlusClient = "6";
    public static final String ACCESSLEVEL_ClientOnly = "2";
    public static final int DURATIONUNIT_AD_Reference_ID = 299;
    public static final String DURATIONUNIT_Year = "Y";
    public static final String DURATIONUNIT_Month = "M";
    public static final String DURATIONUNIT_Day = "D";
    public static final String DURATIONUNIT_Hour = "h";
    public static final String DURATIONUNIT_Minute = "m";
    public static final String DURATIONUNIT_Second = "s";
    public static final int ENTITYTYPE_AD_Reference_ID = 389;
    public static final int PROCESSTYPE_AD_Reference_ID = 53224;
    public static final String PROCESSTYPE_BatchFlow = "BF";
    public static final String PROCESSTYPE_ContinuousFlow = "CF";
    public static final String PROCESSTYPE_DedicateRepetititiveFlow = "DR";
    public static final String PROCESSTYPE_JobShop = "JS";
    public static final String PROCESSTYPE_MixedRepetitiveFlow = "MR";
    public static final String PROCESSTYPE_Plant = "PL";
    public static final int PUBLISHSTATUS_AD_Reference_ID = 310;
    public static final String PUBLISHSTATUS_Released = "R";
    public static final String PUBLISHSTATUS_Test = "T";
    public static final String PUBLISHSTATUS_UnderRevision = "U";
    public static final String PUBLISHSTATUS_Void = "V";
    public static final int WORKFLOWTYPE_AD_Reference_ID = 108;
    public static final String WORKFLOWTYPE_SingleRecord = "S";
    public static final String WORKFLOWTYPE_Maintain = "M";
    public static final String WORKFLOWTYPE_Transaction = "T";
    public static final String WORKFLOWTYPE_QueryOnly = "Q";
    
    public X_PP_Order_Workflow(final Properties ctx, final int PP_Order_Workflow_ID, final String trxName) {
        super(ctx, PP_Order_Workflow_ID, trxName);
    }
    
    public X_PP_Order_Workflow(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_Workflow.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53029, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_Workflow[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setAccessLevel(final String AccessLevel) {
        this.set_Value("AccessLevel", (Object)AccessLevel);
    }
    
    public String getAccessLevel() {
        return (String)this.get_Value("AccessLevel");
    }
    
    public I_AD_Table getAD_Table() throws RuntimeException {
        return (I_AD_Table)MTable.get(this.getCtx(), "AD_Table").getPO(this.getAD_Table_ID(), this.get_TrxName());
    }
    
    public void setAD_Table_ID(final int AD_Table_ID) {
        if (AD_Table_ID < 1) {
            this.set_Value("AD_Table_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Table_ID", (Object)AD_Table_ID);
        }
    }
    
    public int getAD_Table_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Table_ID");
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
    
    public I_AD_WorkflowProcessor getAD_WorkflowProcessor() throws RuntimeException {
        return (I_AD_WorkflowProcessor)MTable.get(this.getCtx(), "AD_WorkflowProcessor").getPO(this.getAD_WorkflowProcessor_ID(), this.get_TrxName());
    }
    
    public void setAD_WorkflowProcessor_ID(final int AD_WorkflowProcessor_ID) {
        if (AD_WorkflowProcessor_ID < 1) {
            this.set_Value("AD_WorkflowProcessor_ID", (Object)null);
        }
        else {
            this.set_Value("AD_WorkflowProcessor_ID", (Object)AD_WorkflowProcessor_ID);
        }
    }
    
    public int getAD_WorkflowProcessor_ID() {
        final Integer ii = (Integer)this.get_Value("AD_WorkflowProcessor_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setAuthor(final String Author) {
        this.set_Value("Author", (Object)Author);
    }
    
    public String getAuthor() {
        return (String)this.get_Value("Author");
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
    
    public void setDescription(final String Description) {
        this.set_Value("Description", (Object)Description);
    }
    
    public String getDescription() {
        return (String)this.get_Value("Description");
    }
    
    public void setDocumentNo(final String DocumentNo) {
        this.set_Value("DocumentNo", (Object)DocumentNo);
    }
    
    public String getDocumentNo() {
        return (String)this.get_Value("DocumentNo");
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
    
    public void setDurationUnit(final String DurationUnit) {
        this.set_Value("DurationUnit", (Object)DurationUnit);
    }
    
    public String getDurationUnit() {
        return (String)this.get_Value("DurationUnit");
    }
    
    public void setEntityType(final String EntityType) {
        this.set_Value("EntityType", (Object)EntityType);
    }
    
    public String getEntityType() {
        return (String)this.get_Value("EntityType");
    }
    
    public void setHelp(final String Help) {
        this.set_Value("Help", (Object)Help);
    }
    
    public String getHelp() {
        return (String)this.get_Value("Help");
    }
    
    public void setIsDefault(final boolean IsDefault) {
        this.set_Value("IsDefault", (Object)IsDefault);
    }
    
    public boolean isDefault() {
        final Object oo = this.get_Value("IsDefault");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
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
    
    public void setOverlapUnits(final BigDecimal OverlapUnits) {
        this.set_Value("OverlapUnits", (Object)OverlapUnits);
    }
    
    public BigDecimal getOverlapUnits() {
        final BigDecimal bd = (BigDecimal)this.get_Value("OverlapUnits");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
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
    
    public void setPP_Order_Workflow_UU(final String PP_Order_Workflow_UU) {
        this.set_Value("PP_Order_Workflow_UU", (Object)PP_Order_Workflow_UU);
    }
    
    public String getPP_Order_Workflow_UU() {
        return (String)this.get_Value("PP_Order_Workflow_UU");
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
    
    public void setProcessType(final String ProcessType) {
        this.set_Value("ProcessType", (Object)ProcessType);
    }
    
    public String getProcessType() {
        return (String)this.get_Value("ProcessType");
    }
    
    public void setPublishStatus(final String PublishStatus) {
        this.set_Value("PublishStatus", (Object)PublishStatus);
    }
    
    public String getPublishStatus() {
        return (String)this.get_Value("PublishStatus");
    }
    
    public void setQtyBatchSize(final BigDecimal QtyBatchSize) {
        this.set_Value("QtyBatchSize", (Object)QtyBatchSize);
    }
    
    public BigDecimal getQtyBatchSize() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyBatchSize");
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
    
    public void setUnitsCycles(final BigDecimal UnitsCycles) {
        this.set_Value("UnitsCycles", (Object)UnitsCycles);
    }
    
    public BigDecimal getUnitsCycles() {
        final BigDecimal bd = (BigDecimal)this.get_Value("UnitsCycles");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setValidateWorkflow(final String ValidateWorkflow) {
        this.set_Value("ValidateWorkflow", (Object)ValidateWorkflow);
    }
    
    public String getValidateWorkflow() {
        return (String)this.get_Value("ValidateWorkflow");
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
    
    public void setVersion(final int Version) {
        this.set_Value("Version", (Object)Version);
    }
    
    public int getVersion() {
        final Integer ii = (Integer)this.get_Value("Version");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setWorkflowType(final String WorkflowType) {
        this.set_Value("WorkflowType", (Object)WorkflowType);
    }
    
    public String getWorkflowType() {
        return (String)this.get_Value("WorkflowType");
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
}
