// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Order;
import java.sql.Timestamp;
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
import org.compiere.model.I_AD_Column;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order_Node
{
    public static final String Table_Name = "PP_Order_Node";
    public static final int Table_ID = 53022;
    public static final KeyNamePair Model = new KeyNamePair(53022, "PP_Order_Node");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_Action = "Action";
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Column_ID = "AD_Column_ID";
    public static final String COLUMNNAME_AD_Form_ID = "AD_Form_ID";
    public static final String COLUMNNAME_AD_Image_ID = "AD_Image_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_Process_ID = "AD_Process_ID";
    public static final String COLUMNNAME_AD_Task_ID = "AD_Task_ID";
    public static final String COLUMNNAME_AD_WF_Block_ID = "AD_WF_Block_ID";
    public static final String COLUMNNAME_AD_WF_Node_ID = "AD_WF_Node_ID";
    public static final String COLUMNNAME_AD_WF_Responsible_ID = "AD_WF_Responsible_ID";
    public static final String COLUMNNAME_AD_Window_ID = "AD_Window_ID";
    public static final String COLUMNNAME_AD_Workflow_ID = "AD_Workflow_ID";
    public static final String COLUMNNAME_AttributeName = "AttributeName";
    public static final String COLUMNNAME_AttributeValue = "AttributeValue";
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";
    public static final String COLUMNNAME_Cost = "Cost";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_DateFinish = "DateFinish";
    public static final String COLUMNNAME_DateFinishSchedule = "DateFinishSchedule";
    public static final String COLUMNNAME_DateStart = "DateStart";
    public static final String COLUMNNAME_DateStartSchedule = "DateStartSchedule";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocAction = "DocAction";
    public static final String COLUMNNAME_DocStatus = "DocStatus";
    public static final String COLUMNNAME_Duration = "Duration";
    public static final String COLUMNNAME_DurationReal = "DurationReal";
    public static final String COLUMNNAME_DurationRequired = "DurationRequired";
    public static final String COLUMNNAME_EntityType = "EntityType";
    public static final String COLUMNNAME_FinishMode = "FinishMode";
    public static final String COLUMNNAME_Help = "Help";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsCentrallyMaintained = "IsCentrallyMaintained";
    public static final String COLUMNNAME_IsMilestone = "IsMilestone";
    public static final String COLUMNNAME_IsSubcontracting = "IsSubcontracting";
    public static final String COLUMNNAME_JoinElement = "JoinElement";
    public static final String COLUMNNAME_Limit = "Limit";
    public static final String COLUMNNAME_MovingTime = "MovingTime";
    public static final String COLUMNNAME_Name = "Name";
    public static final String COLUMNNAME_OverlapUnits = "OverlapUnits";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_PP_Order_Node_ID = "PP_Order_Node_ID";
    public static final String COLUMNNAME_PP_Order_Node_UU = "PP_Order_Node_UU";
    public static final String COLUMNNAME_PP_Order_Workflow_ID = "PP_Order_Workflow_ID";
    public static final String COLUMNNAME_Priority = "Priority";
    public static final String COLUMNNAME_QtyDelivered = "QtyDelivered";
    public static final String COLUMNNAME_QtyReject = "QtyReject";
    public static final String COLUMNNAME_QtyRequired = "QtyRequired";
    public static final String COLUMNNAME_QtyScrap = "QtyScrap";
    public static final String COLUMNNAME_QueuingTime = "QueuingTime";
    public static final String COLUMNNAME_SetupTime = "SetupTime";
    public static final String COLUMNNAME_SetupTimeReal = "SetupTimeReal";
    public static final String COLUMNNAME_SetupTimeRequired = "SetupTimeRequired";
    public static final String COLUMNNAME_SplitElement = "SplitElement";
    public static final String COLUMNNAME_S_Resource_ID = "S_Resource_ID";
    public static final String COLUMNNAME_StartMode = "StartMode";
    public static final String COLUMNNAME_SubflowExecution = "SubflowExecution";
    public static final String COLUMNNAME_UnitsCycles = "UnitsCycles";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";
    public static final String COLUMNNAME_ValidTo = "ValidTo";
    public static final String COLUMNNAME_Value = "Value";
    public static final String COLUMNNAME_WaitingTime = "WaitingTime";
    public static final String COLUMNNAME_Workflow_ID = "Workflow_ID";
    public static final String COLUMNNAME_WorkingTime = "WorkingTime";
    public static final String COLUMNNAME_XPosition = "XPosition";
    public static final String COLUMNNAME_Yield = "Yield";
    public static final String COLUMNNAME_YPosition = "YPosition";
    
    void setAction(final String p0);
    
    String getAction();
    
    int getAD_Client_ID();
    
    void setAD_Column_ID(final int p0);
    
    int getAD_Column_ID();
    
    I_AD_Column getAD_Column() throws RuntimeException;
    
    void setAD_Form_ID(final int p0);
    
    int getAD_Form_ID();
    
    I_AD_Form getAD_Form() throws RuntimeException;
    
    void setAD_Image_ID(final int p0);
    
    int getAD_Image_ID();
    
    I_AD_Image getAD_Image() throws RuntimeException;
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_Process_ID(final int p0);
    
    int getAD_Process_ID();
    
    I_AD_Process getAD_Process() throws RuntimeException;
    
    void setAD_Task_ID(final int p0);
    
    int getAD_Task_ID();
    
    I_AD_Task getAD_Task() throws RuntimeException;
    
    void setAD_WF_Block_ID(final int p0);
    
    int getAD_WF_Block_ID();
    
    I_AD_WF_Block getAD_WF_Block() throws RuntimeException;
    
    void setAD_WF_Node_ID(final int p0);
    
    int getAD_WF_Node_ID();
    
    I_AD_WF_Node getAD_WF_Node() throws RuntimeException;
    
    void setAD_WF_Responsible_ID(final int p0);
    
    int getAD_WF_Responsible_ID();
    
    I_AD_WF_Responsible getAD_WF_Responsible() throws RuntimeException;
    
    void setAD_Window_ID(final int p0);
    
    int getAD_Window_ID();
    
    I_AD_Window getAD_Window() throws RuntimeException;
    
    void setAD_Workflow_ID(final int p0);
    
    int getAD_Workflow_ID();
    
    I_AD_Workflow getAD_Workflow() throws RuntimeException;
    
    void setAttributeName(final String p0);
    
    String getAttributeName();
    
    void setAttributeValue(final String p0);
    
    String getAttributeValue();
    
    void setC_BPartner_ID(final int p0);
    
    int getC_BPartner_ID();
    
    I_C_BPartner getC_BPartner() throws RuntimeException;
    
    void setCost(final BigDecimal p0);
    
    BigDecimal getCost();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDateFinish(final Timestamp p0);
    
    Timestamp getDateFinish();
    
    void setDateFinishSchedule(final Timestamp p0);
    
    Timestamp getDateFinishSchedule();
    
    void setDateStart(final Timestamp p0);
    
    Timestamp getDateStart();
    
    void setDateStartSchedule(final Timestamp p0);
    
    Timestamp getDateStartSchedule();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setDocAction(final String p0);
    
    String getDocAction();
    
    void setDocStatus(final String p0);
    
    String getDocStatus();
    
    void setDuration(final int p0);
    
    int getDuration();
    
    void setDurationReal(final int p0);
    
    int getDurationReal();
    
    void setDurationRequired(final int p0);
    
    int getDurationRequired();
    
    void setEntityType(final String p0);
    
    String getEntityType();
    
    void setFinishMode(final String p0);
    
    String getFinishMode();
    
    void setHelp(final String p0);
    
    String getHelp();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsCentrallyMaintained(final boolean p0);
    
    boolean isCentrallyMaintained();
    
    void setIsMilestone(final boolean p0);
    
    boolean isMilestone();
    
    void setIsSubcontracting(final boolean p0);
    
    boolean isSubcontracting();
    
    void setJoinElement(final String p0);
    
    String getJoinElement();
    
    void setLimit(final int p0);
    
    int getLimit();
    
    void setMovingTime(final int p0);
    
    int getMovingTime();
    
    void setName(final String p0);
    
    String getName();
    
    void setOverlapUnits(final int p0);
    
    int getOverlapUnits();
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setPP_Order_Node_ID(final int p0);
    
    int getPP_Order_Node_ID();
    
    void setPP_Order_Node_UU(final String p0);
    
    String getPP_Order_Node_UU();
    
    void setPP_Order_Workflow_ID(final int p0);
    
    int getPP_Order_Workflow_ID();
    
    I_PP_Order_Workflow getPP_Order_Workflow() throws RuntimeException;
    
    void setPriority(final int p0);
    
    int getPriority();
    
    void setQtyDelivered(final BigDecimal p0);
    
    BigDecimal getQtyDelivered();
    
    void setQtyReject(final BigDecimal p0);
    
    BigDecimal getQtyReject();
    
    void setQtyRequired(final BigDecimal p0);
    
    BigDecimal getQtyRequired();
    
    void setQtyScrap(final BigDecimal p0);
    
    BigDecimal getQtyScrap();
    
    void setQueuingTime(final int p0);
    
    int getQueuingTime();
    
    void setSetupTime(final int p0);
    
    int getSetupTime();
    
    void setSetupTimeReal(final int p0);
    
    int getSetupTimeReal();
    
    void setSetupTimeRequired(final int p0);
    
    int getSetupTimeRequired();
    
    void setSplitElement(final String p0);
    
    String getSplitElement();
    
    void setS_Resource_ID(final int p0);
    
    int getS_Resource_ID();
    
    I_S_Resource getS_Resource() throws RuntimeException;
    
    void setStartMode(final String p0);
    
    String getStartMode();
    
    void setSubflowExecution(final String p0);
    
    String getSubflowExecution();
    
    void setUnitsCycles(final int p0);
    
    int getUnitsCycles();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValidFrom(final Timestamp p0);
    
    Timestamp getValidFrom();
    
    void setValidTo(final Timestamp p0);
    
    Timestamp getValidTo();
    
    void setValue(final String p0);
    
    String getValue();
    
    void setWaitingTime(final int p0);
    
    int getWaitingTime();
    
    void setWorkflow_ID(final int p0);
    
    int getWorkflow_ID();
    
    I_AD_Workflow getWorkflow() throws RuntimeException;
    
    void setWorkingTime(final int p0);
    
    int getWorkingTime();
    
    void setXPosition(final int p0);
    
    int getXPosition();
    
    void setYield(final int p0);
    
    int getYield();
    
    void setYPosition(final int p0);
    
    int getYPosition();
}
