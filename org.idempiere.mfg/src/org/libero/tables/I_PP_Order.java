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
import org.compiere.model.I_C_UOM;
import java.sql.Timestamp;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.I_C_Activity;
import org.compiere.model.I_AD_Workflow;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order
{
    public static final String Table_Name = "PP_Order";
    public static final int Table_ID = 53027;
    public static final KeyNamePair Model = new KeyNamePair(53027, "PP_Order");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(1L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";
    public static final String COLUMNNAME_AD_Workflow_ID = "AD_Workflow_ID";
    public static final String COLUMNNAME_Assay = "Assay";
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";
    public static final String COLUMNNAME_C_DocTypeTarget_ID = "C_DocTypeTarget_ID";
    public static final String COLUMNNAME_CopyFrom = "CopyFrom";
    public static final String COLUMNNAME_C_OrderLine_ID = "C_OrderLine_ID";
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";
    public static final String COLUMNNAME_DateConfirm = "DateConfirm";
    public static final String COLUMNNAME_DateDelivered = "DateDelivered";
    public static final String COLUMNNAME_DateFinish = "DateFinish";
    public static final String COLUMNNAME_DateFinishSchedule = "DateFinishSchedule";
    public static final String COLUMNNAME_DateOrdered = "DateOrdered";
    public static final String COLUMNNAME_DatePromised = "DatePromised";
    public static final String COLUMNNAME_DateStart = "DateStart";
    public static final String COLUMNNAME_DateStartSchedule = "DateStartSchedule";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocAction = "DocAction";
    public static final String COLUMNNAME_DocStatus = "DocStatus";
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";
    public static final String COLUMNNAME_FloatAfter = "FloatAfter";
    public static final String COLUMNNAME_FloatBefored = "FloatBefored";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsApproved = "IsApproved";
    public static final String COLUMNNAME_IsPrinted = "IsPrinted";
    public static final String COLUMNNAME_IsQtyPercentage = "IsQtyPercentage";
    public static final String COLUMNNAME_IsSelected = "IsSelected";
    public static final String COLUMNNAME_IsSOTrx = "IsSOTrx";
    public static final String COLUMNNAME_Line = "Line";
    public static final String COLUMNNAME_Lot = "Lot";
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";
    public static final String COLUMNNAME_OrderType = "OrderType";
    public static final String COLUMNNAME_Planner_ID = "Planner_ID";
    public static final String COLUMNNAME_Posted = "Posted";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_PP_Order_UU = "PP_Order_UU";
    public static final String COLUMNNAME_PP_Product_BOM_ID = "PP_Product_BOM_ID";
    public static final String COLUMNNAME_PriorityRule = "PriorityRule";
    public static final String COLUMNNAME_Processed = "Processed";
    public static final String COLUMNNAME_ProcessedOn = "ProcessedOn";
    public static final String COLUMNNAME_Processing = "Processing";
    public static final String COLUMNNAME_QtyBatchs = "QtyBatchs";
    public static final String COLUMNNAME_QtyBatchSize = "QtyBatchSize";
    public static final String COLUMNNAME_QtyDelivered = "QtyDelivered";
    public static final String COLUMNNAME_QtyEntered = "QtyEntered";
    public static final String COLUMNNAME_QtyOrdered = "QtyOrdered";
    public static final String COLUMNNAME_QtyReject = "QtyReject";
    public static final String COLUMNNAME_QtyReserved = "QtyReserved";
    public static final String COLUMNNAME_QtyScrap = "QtyScrap";
    public static final String COLUMNNAME_ScheduleType = "ScheduleType";
    public static final String COLUMNNAME_SerNo = "SerNo";
    public static final String COLUMNNAME_S_Resource_ID = "S_Resource_ID";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_User1_ID = "User1_ID";
    public static final String COLUMNNAME_User2_ID = "User2_ID";
    public static final String COLUMNNAME_Yield = "Yield";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_OrgTrx_ID(final int p0);
    
    int getAD_OrgTrx_ID();
    
    void setAD_Workflow_ID(final int p0);
    
    int getAD_Workflow_ID();
    
    I_AD_Workflow getAD_Workflow() throws RuntimeException;
    
    void setAssay(final BigDecimal p0);
    
    BigDecimal getAssay();
    
    void setC_Activity_ID(final int p0);
    
    int getC_Activity_ID();
    
    I_C_Activity getC_Activity() throws RuntimeException;
    
    void setC_Campaign_ID(final int p0);
    
    int getC_Campaign_ID();
    
    I_C_Campaign getC_Campaign() throws RuntimeException;
    
    void setC_DocType_ID(final int p0);
    
    int getC_DocType_ID();
    
    I_C_DocType getC_DocType() throws RuntimeException;
    
    void setC_DocTypeTarget_ID(final int p0);
    
    int getC_DocTypeTarget_ID();
    
    I_C_DocType getC_DocTypeTarget() throws RuntimeException;
    
    void setCopyFrom(final String p0);
    
    String getCopyFrom();
    
    void setC_OrderLine_ID(final int p0);
    
    int getC_OrderLine_ID();
    
    I_C_OrderLine getC_OrderLine() throws RuntimeException;
    
    void setC_Project_ID(final int p0);
    
    int getC_Project_ID();
    
    I_C_Project getC_Project() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setC_UOM_ID(final int p0);
    
    int getC_UOM_ID();
    
    I_C_UOM getC_UOM() throws RuntimeException;
    
    void setDateConfirm(final Timestamp p0);
    
    Timestamp getDateConfirm();
    
    void setDateDelivered(final Timestamp p0);
    
    Timestamp getDateDelivered();
    
    void setDateFinish(final Timestamp p0);
    
    Timestamp getDateFinish();
    
    void setDateFinishSchedule(final Timestamp p0);
    
    Timestamp getDateFinishSchedule();
    
    void setDateOrdered(final Timestamp p0);
    
    Timestamp getDateOrdered();
    
    void setDatePromised(final Timestamp p0);
    
    Timestamp getDatePromised();
    
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
    
    void setDocumentNo(final String p0);
    
    String getDocumentNo();
    
    void setFloatAfter(final BigDecimal p0);
    
    BigDecimal getFloatAfter();
    
    void setFloatBefored(final BigDecimal p0);
    
    BigDecimal getFloatBefored();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsApproved(final boolean p0);
    
    boolean isApproved();
    
    void setIsPrinted(final boolean p0);
    
    boolean isPrinted();
    
    void setIsQtyPercentage(final boolean p0);
    
    boolean isQtyPercentage();
    
    void setIsSelected(final boolean p0);
    
    boolean isSelected();
    
    void setIsSOTrx(final boolean p0);
    
    boolean isSOTrx();
    
    void setLine(final int p0);
    
    int getLine();
    
    void setLot(final String p0);
    
    String getLot();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setM_Warehouse_ID(final int p0);
    
    int getM_Warehouse_ID();
    
    I_M_Warehouse getM_Warehouse() throws RuntimeException;
    
    void setOrderType(final String p0);
    
    String getOrderType();
    
    void setPlanner_ID(final int p0);
    
    int getPlanner_ID();
    
    I_AD_User getPlanner() throws RuntimeException;
    
    void setPosted(final boolean p0);
    
    boolean isPosted();
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    void setPP_Order_UU(final String p0);
    
    String getPP_Order_UU();
    
    void setPP_Product_BOM_ID(final int p0);
    
    int getPP_Product_BOM_ID();
    
    I_PP_Product_BOM getPP_Product_BOM() throws RuntimeException;
    
    void setPriorityRule(final String p0);
    
    String getPriorityRule();
    
    void setProcessed(final boolean p0);
    
    boolean isProcessed();
    
    void setProcessedOn(final BigDecimal p0);
    
    BigDecimal getProcessedOn();
    
    void setProcessing(final boolean p0);
    
    boolean isProcessing();
    
    void setQtyBatchs(final BigDecimal p0);
    
    BigDecimal getQtyBatchs();
    
    void setQtyBatchSize(final BigDecimal p0);
    
    BigDecimal getQtyBatchSize();
    
    void setQtyDelivered(final BigDecimal p0);
    
    BigDecimal getQtyDelivered();
    
    void setQtyEntered(final BigDecimal p0);
    
    BigDecimal getQtyEntered();
    
    void setQtyOrdered(final BigDecimal p0);
    
    BigDecimal getQtyOrdered();
    
    void setQtyReject(final BigDecimal p0);
    
    BigDecimal getQtyReject();
    
    void setQtyReserved(final BigDecimal p0);
    
    BigDecimal getQtyReserved();
    
    void setQtyScrap(final BigDecimal p0);
    
    BigDecimal getQtyScrap();
    
    void setScheduleType(final String p0);
    
    String getScheduleType();
    
    void setSerNo(final String p0);
    
    String getSerNo();
    
    void setS_Resource_ID(final int p0);
    
    int getS_Resource_ID();
    
    I_S_Resource getS_Resource() throws RuntimeException;
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setUser1_ID(final int p0);
    
    int getUser1_ID();
    
    I_C_ElementValue getUser1() throws RuntimeException;
    
    void setUser2_ID(final int p0);
    
    int getUser2_ID();
    
    I_C_ElementValue getUser2() throws RuntimeException;
    
    void setYield(final BigDecimal p0);
    
    BigDecimal getYield();
}
