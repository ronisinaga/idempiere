// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.I_C_UOM;
import java.sql.Timestamp;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.I_C_Activity;
import org.compiere.model.I_AD_User;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Cost_Collector
{
    public static final String Table_Name = "PP_Cost_Collector";
    public static final int Table_ID = 53035;
    public static final KeyNamePair Model = new KeyNamePair(53035, "PP_Cost_Collector");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(1L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";
    public static final String COLUMNNAME_C_DocTypeTarget_ID = "C_DocTypeTarget_ID";
    public static final String COLUMNNAME_CostCollectorType = "CostCollectorType";
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";
    public static final String COLUMNNAME_DateAcct = "DateAcct";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocAction = "DocAction";
    public static final String COLUMNNAME_DocStatus = "DocStatus";
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";
    public static final String COLUMNNAME_DurationReal = "DurationReal";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsBatchTime = "IsBatchTime";
    public static final String COLUMNNAME_IsSubcontracting = "IsSubcontracting";
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";
    public static final String COLUMNNAME_MovementDate = "MovementDate";
    public static final String COLUMNNAME_MovementQty = "MovementQty";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";
    public static final String COLUMNNAME_Posted = "Posted";
    public static final String COLUMNNAME_PP_Cost_Collector_ID = "PP_Cost_Collector_ID";
    public static final String COLUMNNAME_PP_Cost_Collector_UU = "PP_Cost_Collector_UU";
    public static final String COLUMNNAME_PP_Order_BOMLine_ID = "PP_Order_BOMLine_ID";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_PP_Order_Node_ID = "PP_Order_Node_ID";
    public static final String COLUMNNAME_PP_Order_Workflow_ID = "PP_Order_Workflow_ID";
    public static final String COLUMNNAME_Processed = "Processed";
    public static final String COLUMNNAME_ProcessedOn = "ProcessedOn";
    public static final String COLUMNNAME_Processing = "Processing";
    public static final String COLUMNNAME_QtyReject = "QtyReject";
    public static final String COLUMNNAME_Reversal_ID = "Reversal_ID";
    public static final String COLUMNNAME_ScrappedQty = "ScrappedQty";
    public static final String COLUMNNAME_SetupTimeReal = "SetupTimeReal";
    public static final String COLUMNNAME_S_Resource_ID = "S_Resource_ID";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_User1_ID = "User1_ID";
    public static final String COLUMNNAME_User2_ID = "User2_ID";
    
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
    
    void setC_Campaign_ID(final int p0);
    
    int getC_Campaign_ID();
    
    I_C_Campaign getC_Campaign() throws RuntimeException;
    
    void setC_DocType_ID(final int p0);
    
    int getC_DocType_ID();
    
    I_C_DocType getC_DocType() throws RuntimeException;
    
    void setC_DocTypeTarget_ID(final int p0);
    
    int getC_DocTypeTarget_ID();
    
    I_C_DocType getC_DocTypeTarget() throws RuntimeException;
    
    void setCostCollectorType(final String p0);
    
    String getCostCollectorType();
    
    void setC_Project_ID(final int p0);
    
    int getC_Project_ID();
    
    I_C_Project getC_Project() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setC_UOM_ID(final int p0);
    
    int getC_UOM_ID();
    
    I_C_UOM getC_UOM() throws RuntimeException;
    
    void setDateAcct(final Timestamp p0);
    
    Timestamp getDateAcct();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setDocAction(final String p0);
    
    String getDocAction();
    
    void setDocStatus(final String p0);
    
    String getDocStatus();
    
    void setDocumentNo(final String p0);
    
    String getDocumentNo();
    
    void setDurationReal(final BigDecimal p0);
    
    BigDecimal getDurationReal();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsBatchTime(final boolean p0);
    
    boolean isBatchTime();
    
    void setIsSubcontracting(final boolean p0);
    
    boolean isSubcontracting();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;
    
    void setM_Locator_ID(final int p0);
    
    int getM_Locator_ID();
    
    I_M_Locator getM_Locator() throws RuntimeException;
    
    void setMovementDate(final Timestamp p0);
    
    Timestamp getMovementDate();
    
    void setMovementQty(final BigDecimal p0);
    
    BigDecimal getMovementQty();
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setM_Warehouse_ID(final int p0);
    
    int getM_Warehouse_ID();
    
    I_M_Warehouse getM_Warehouse() throws RuntimeException;
    
    void setPosted(final boolean p0);
    
    boolean isPosted();
    
    void setPP_Cost_Collector_ID(final int p0);
    
    int getPP_Cost_Collector_ID();
    
    void setPP_Cost_Collector_UU(final String p0);
    
    String getPP_Cost_Collector_UU();
    
    void setPP_Order_BOMLine_ID(final int p0);
    
    int getPP_Order_BOMLine_ID();
    
    I_PP_Order_BOMLine getPP_Order_BOMLine() throws RuntimeException;
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setPP_Order_Node_ID(final int p0);
    
    int getPP_Order_Node_ID();
    
    I_PP_Order_Node getPP_Order_Node() throws RuntimeException;
    
    void setPP_Order_Workflow_ID(final int p0);
    
    int getPP_Order_Workflow_ID();
    
    I_PP_Order_Workflow getPP_Order_Workflow() throws RuntimeException;
    
    void setProcessed(final boolean p0);
    
    boolean isProcessed();
    
    void setProcessedOn(final BigDecimal p0);
    
    BigDecimal getProcessedOn();
    
    void setProcessing(final boolean p0);
    
    boolean isProcessing();
    
    void setQtyReject(final BigDecimal p0);
    
    BigDecimal getQtyReject();
    
    void setReversal_ID(final int p0);
    
    int getReversal_ID();
    
    I_PP_Cost_Collector getReversal() throws RuntimeException;
    
    void setScrappedQty(final BigDecimal p0);
    
    BigDecimal getScrappedQty();
    
    void setSetupTimeReal(final BigDecimal p0);
    
    BigDecimal getSetupTimeReal();
    
    void setS_Resource_ID(final int p0);
    
    int getS_Resource_ID();
    
    I_S_Resource getS_Resource() throws RuntimeException;
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setUser1_ID(final int p0);
    
    int getUser1_ID();
    
    I_AD_User getUser1() throws RuntimeException;
    
    void setUser2_ID(final int p0);
    
    int getUser2_ID();
    
    I_AD_User getUser2() throws RuntimeException;
}
