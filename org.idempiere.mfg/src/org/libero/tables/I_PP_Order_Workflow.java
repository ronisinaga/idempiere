// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Order;
import java.sql.Timestamp;
import org.compiere.model.I_AD_WorkflowProcessor;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.I_AD_WF_Responsible;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.I_AD_Table;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order_Workflow
{
    public static final String Table_Name = "PP_Order_Workflow";
    public static final int Table_ID = 53029;
    public static final KeyNamePair Model = new KeyNamePair(53029, "PP_Order_Workflow");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AccessLevel = "AccessLevel";
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";
    public static final String COLUMNNAME_AD_WF_Node_ID = "AD_WF_Node_ID";
    public static final String COLUMNNAME_AD_WF_Responsible_ID = "AD_WF_Responsible_ID";
    public static final String COLUMNNAME_AD_Workflow_ID = "AD_Workflow_ID";
    public static final String COLUMNNAME_AD_WorkflowProcessor_ID = "AD_WorkflowProcessor_ID";
    public static final String COLUMNNAME_Author = "Author";
    public static final String COLUMNNAME_Cost = "Cost";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";
    public static final String COLUMNNAME_Duration = "Duration";
    public static final String COLUMNNAME_DurationUnit = "DurationUnit";
    public static final String COLUMNNAME_EntityType = "EntityType";
    public static final String COLUMNNAME_Help = "Help";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsDefault = "IsDefault";
    public static final String COLUMNNAME_Limit = "Limit";
    public static final String COLUMNNAME_MovingTime = "MovingTime";
    public static final String COLUMNNAME_Name = "Name";
    public static final String COLUMNNAME_OverlapUnits = "OverlapUnits";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_PP_Order_Node_ID = "PP_Order_Node_ID";
    public static final String COLUMNNAME_PP_Order_Workflow_ID = "PP_Order_Workflow_ID";
    public static final String COLUMNNAME_PP_Order_Workflow_UU = "PP_Order_Workflow_UU";
    public static final String COLUMNNAME_Priority = "Priority";
    public static final String COLUMNNAME_ProcessType = "ProcessType";
    public static final String COLUMNNAME_PublishStatus = "PublishStatus";
    public static final String COLUMNNAME_QtyBatchSize = "QtyBatchSize";
    public static final String COLUMNNAME_QueuingTime = "QueuingTime";
    public static final String COLUMNNAME_SetupTime = "SetupTime";
    public static final String COLUMNNAME_S_Resource_ID = "S_Resource_ID";
    public static final String COLUMNNAME_UnitsCycles = "UnitsCycles";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_ValidateWorkflow = "ValidateWorkflow";
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";
    public static final String COLUMNNAME_ValidTo = "ValidTo";
    public static final String COLUMNNAME_Value = "Value";
    public static final String COLUMNNAME_Version = "Version";
    public static final String COLUMNNAME_WaitingTime = "WaitingTime";
    public static final String COLUMNNAME_WorkflowType = "WorkflowType";
    public static final String COLUMNNAME_WorkingTime = "WorkingTime";
    public static final String COLUMNNAME_Yield = "Yield";
    
    void setAccessLevel(final String p0);
    
    String getAccessLevel();
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_Table_ID(final int p0);
    
    int getAD_Table_ID();
    
    I_AD_Table getAD_Table() throws RuntimeException;
    
    void setAD_WF_Node_ID(final int p0);
    
    int getAD_WF_Node_ID();
    
    I_AD_WF_Node getAD_WF_Node() throws RuntimeException;
    
    void setAD_WF_Responsible_ID(final int p0);
    
    int getAD_WF_Responsible_ID();
    
    I_AD_WF_Responsible getAD_WF_Responsible() throws RuntimeException;
    
    void setAD_Workflow_ID(final int p0);
    
    int getAD_Workflow_ID();
    
    I_AD_Workflow getAD_Workflow() throws RuntimeException;
    
    void setAD_WorkflowProcessor_ID(final int p0);
    
    int getAD_WorkflowProcessor_ID();
    
    I_AD_WorkflowProcessor getAD_WorkflowProcessor() throws RuntimeException;
    
    void setAuthor(final String p0);
    
    String getAuthor();
    
    void setCost(final BigDecimal p0);
    
    BigDecimal getCost();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setDocumentNo(final String p0);
    
    String getDocumentNo();
    
    void setDuration(final int p0);
    
    int getDuration();
    
    void setDurationUnit(final String p0);
    
    String getDurationUnit();
    
    void setEntityType(final String p0);
    
    String getEntityType();
    
    void setHelp(final String p0);
    
    String getHelp();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsDefault(final boolean p0);
    
    boolean isDefault();
    
    void setLimit(final int p0);
    
    int getLimit();
    
    void setMovingTime(final int p0);
    
    int getMovingTime();
    
    void setName(final String p0);
    
    String getName();
    
    void setOverlapUnits(final BigDecimal p0);
    
    BigDecimal getOverlapUnits();
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setPP_Order_Node_ID(final int p0);
    
    int getPP_Order_Node_ID();
    
    I_PP_Order_Node getPP_Order_Node() throws RuntimeException;
    
    void setPP_Order_Workflow_ID(final int p0);
    
    int getPP_Order_Workflow_ID();
    
    void setPP_Order_Workflow_UU(final String p0);
    
    String getPP_Order_Workflow_UU();
    
    void setPriority(final int p0);
    
    int getPriority();
    
    void setProcessType(final String p0);
    
    String getProcessType();
    
    void setPublishStatus(final String p0);
    
    String getPublishStatus();
    
    void setQtyBatchSize(final BigDecimal p0);
    
    BigDecimal getQtyBatchSize();
    
    void setQueuingTime(final int p0);
    
    int getQueuingTime();
    
    void setSetupTime(final int p0);
    
    int getSetupTime();
    
    void setS_Resource_ID(final int p0);
    
    int getS_Resource_ID();
    
    I_S_Resource getS_Resource() throws RuntimeException;
    
    void setUnitsCycles(final BigDecimal p0);
    
    BigDecimal getUnitsCycles();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValidateWorkflow(final String p0);
    
    String getValidateWorkflow();
    
    void setValidFrom(final Timestamp p0);
    
    Timestamp getValidFrom();
    
    void setValidTo(final Timestamp p0);
    
    Timestamp getValidTo();
    
    void setValue(final String p0);
    
    String getValue();
    
    void setVersion(final int p0);
    
    int getVersion();
    
    void setWaitingTime(final int p0);
    
    int getWaitingTime();
    
    void setWorkflowType(final String p0);
    
    String getWorkflowType();
    
    void setWorkingTime(final int p0);
    
    int getWorkingTime();
    
    void setYield(final int p0);
    
    int getYield();
}
