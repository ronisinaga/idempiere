// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_S_Resource;
import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_RequisitionLine;
import org.compiere.model.I_M_Requisition;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_ForecastLine;
import org.compiere.model.I_M_Forecast;
import org.eevolution.model.I_DD_OrderLine;
import org.eevolution.model.I_DD_Order;
import java.sql.Timestamp;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_BPartner;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_MRP
{
    public static final String Table_Name = "PP_MRP";
    public static final int Table_ID = 53043;
    public static final KeyNamePair Model = new KeyNamePair(53043, "PP_MRP");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";
    public static final String COLUMNNAME_C_Order_ID = "C_Order_ID";
    public static final String COLUMNNAME_C_OrderLine_ID = "C_OrderLine_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_DateConfirm = "DateConfirm";
    public static final String COLUMNNAME_DateFinishSchedule = "DateFinishSchedule";
    public static final String COLUMNNAME_DateOrdered = "DateOrdered";
    public static final String COLUMNNAME_DatePromised = "DatePromised";
    public static final String COLUMNNAME_DateSimulation = "DateSimulation";
    public static final String COLUMNNAME_DateStart = "DateStart";
    public static final String COLUMNNAME_DateStartSchedule = "DateStartSchedule";
    public static final String COLUMNNAME_DD_Order_ID = "DD_Order_ID";
    public static final String COLUMNNAME_DD_OrderLine_ID = "DD_OrderLine_ID";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocStatus = "DocStatus";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsAvailable = "IsAvailable";
    public static final String COLUMNNAME_M_Forecast_ID = "M_Forecast_ID";
    public static final String COLUMNNAME_M_ForecastLine_ID = "M_ForecastLine_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_M_Requisition_ID = "M_Requisition_ID";
    public static final String COLUMNNAME_M_RequisitionLine_ID = "M_RequisitionLine_ID";
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";
    public static final String COLUMNNAME_Name = "Name";
    public static final String COLUMNNAME_OrderType = "OrderType";
    public static final String COLUMNNAME_Planner_ID = "Planner_ID";
    public static final String COLUMNNAME_PP_MRP_ID = "PP_MRP_ID";
    public static final String COLUMNNAME_PP_MRP_UU = "PP_MRP_UU";
    public static final String COLUMNNAME_PP_Order_BOMLine_ID = "PP_Order_BOMLine_ID";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_Priority = "Priority";
    public static final String COLUMNNAME_Qty = "Qty";
    public static final String COLUMNNAME_S_Resource_ID = "S_Resource_ID";
    public static final String COLUMNNAME_TypeMRP = "TypeMRP";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_Value = "Value";
    public static final String COLUMNNAME_Version = "Version";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setC_BPartner_ID(final int p0);
    
    int getC_BPartner_ID();
    
    I_C_BPartner getC_BPartner() throws RuntimeException;
    
    void setC_Order_ID(final int p0);
    
    int getC_Order_ID();
    
    I_C_Order getC_Order() throws RuntimeException;
    
    void setC_OrderLine_ID(final int p0);
    
    int getC_OrderLine_ID();
    
    I_C_OrderLine getC_OrderLine() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDateConfirm(final Timestamp p0);
    
    Timestamp getDateConfirm();
    
    void setDateFinishSchedule(final Timestamp p0);
    
    Timestamp getDateFinishSchedule();
    
    void setDateOrdered(final Timestamp p0);
    
    Timestamp getDateOrdered();
    
    void setDatePromised(final Timestamp p0);
    
    Timestamp getDatePromised();
    
    void setDateSimulation(final Timestamp p0);
    
    Timestamp getDateSimulation();
    
    void setDateStart(final Timestamp p0);
    
    Timestamp getDateStart();
    
    void setDateStartSchedule(final Timestamp p0);
    
    Timestamp getDateStartSchedule();
    
    void setDD_Order_ID(final int p0);
    
    int getDD_Order_ID();
    
    I_DD_Order getDD_Order() throws RuntimeException;
    
    void setDD_OrderLine_ID(final int p0);
    
    int getDD_OrderLine_ID();
    
    I_DD_OrderLine getDD_OrderLine() throws RuntimeException;
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setDocStatus(final String p0);
    
    String getDocStatus();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsAvailable(final boolean p0);
    
    boolean isAvailable();
    
    void setM_Forecast_ID(final int p0);
    
    int getM_Forecast_ID();
    
    I_M_Forecast getM_Forecast() throws RuntimeException;
    
    void setM_ForecastLine_ID(final int p0);
    
    int getM_ForecastLine_ID();
    
    I_M_ForecastLine getM_ForecastLine() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setM_Requisition_ID(final int p0);
    
    int getM_Requisition_ID();
    
    I_M_Requisition getM_Requisition() throws RuntimeException;
    
    void setM_RequisitionLine_ID(final int p0);
    
    int getM_RequisitionLine_ID();
    
    I_M_RequisitionLine getM_RequisitionLine() throws RuntimeException;
    
    void setM_Warehouse_ID(final int p0);
    
    int getM_Warehouse_ID();
    
    I_M_Warehouse getM_Warehouse() throws RuntimeException;
    
    void setName(final String p0);
    
    String getName();
    
    void setOrderType(final String p0);
    
    String getOrderType();
    
    void setPlanner_ID(final int p0);
    
    int getPlanner_ID();
    
    I_AD_User getPlanner() throws RuntimeException;
    
    void setPP_MRP_ID(final int p0);
    
    int getPP_MRP_ID();
    
    void setPP_MRP_UU(final String p0);
    
    String getPP_MRP_UU();
    
    void setPP_Order_BOMLine_ID(final int p0);
    
    int getPP_Order_BOMLine_ID();
    
    I_PP_Order_BOMLine getPP_Order_BOMLine() throws RuntimeException;
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setPriority(final String p0);
    
    String getPriority();
    
    void setQty(final BigDecimal p0);
    
    BigDecimal getQty();
    
    void setS_Resource_ID(final int p0);
    
    int getS_Resource_ID();
    
    I_S_Resource getS_Resource() throws RuntimeException;
    
    void setTypeMRP(final String p0);
    
    String getTypeMRP();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValue(final String p0);
    
    String getValue();
    
    void setVersion(final BigDecimal p0);
    
    BigDecimal getVersion();
}
