// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_S_Resource;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_AD_User;
import org.compiere.util.KeyNamePair;
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
import org.compiere.model.MTable;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_MRP extends PO implements I_PP_MRP, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
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
    public static final int ORDERTYPE_AD_Reference_ID = 53229;
    public static final String ORDERTYPE_Forecast = "FCT";
    public static final String ORDERTYPE_ManufacturingOrder = "MOP";
    public static final String ORDERTYPE_PurchaseOrder = "POO";
    public static final String ORDERTYPE_MaterialRequisition = "POR";
    public static final String ORDERTYPE_SalesOrder = "SOO";
    public static final String ORDERTYPE_DistributionOrder = "DOO";
    public static final String ORDERTYPE_SafetyStock = "STK";
    public static final int TYPEMRP_AD_Reference_ID = 53230;
    public static final String TYPEMRP_Demand = "D";
    public static final String TYPEMRP_Supply = "S";
    
    public X_PP_MRP(final Properties ctx, final int PP_MRP_ID, final String trxName) {
        super(ctx, PP_MRP_ID, trxName);
    }
    
    public X_PP_MRP(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_MRP.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53043, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_MRP[").append(this.get_ID()).append("]");
        return sb.toString();
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
    
    public I_C_Order getC_Order() throws RuntimeException {
        return (I_C_Order)MTable.get(this.getCtx(), "C_Order").getPO(this.getC_Order_ID(), this.get_TrxName());
    }
    
    public void setC_Order_ID(final int C_Order_ID) {
        if (C_Order_ID < 1) {
            this.set_Value("C_Order_ID", (Object)null);
        }
        else {
            this.set_Value("C_Order_ID", (Object)C_Order_ID);
        }
    }
    
    public int getC_Order_ID() {
        final Integer ii = (Integer)this.get_Value("C_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setDateConfirm(final Timestamp DateConfirm) {
        this.set_Value("DateConfirm", (Object)DateConfirm);
    }
    
    public Timestamp getDateConfirm() {
        return (Timestamp)this.get_Value("DateConfirm");
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
    
    public void setDateSimulation(final Timestamp DateSimulation) {
        this.set_Value("DateSimulation", (Object)DateSimulation);
    }
    
    public Timestamp getDateSimulation() {
        return (Timestamp)this.get_Value("DateSimulation");
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
    
    public I_DD_Order getDD_Order() throws RuntimeException {
        return (I_DD_Order)MTable.get(this.getCtx(), "DD_Order").getPO(this.getDD_Order_ID(), this.get_TrxName());
    }
    
    public void setDD_Order_ID(final int DD_Order_ID) {
        if (DD_Order_ID < 1) {
            this.set_Value("DD_Order_ID", (Object)null);
        }
        else {
            this.set_Value("DD_Order_ID", (Object)DD_Order_ID);
        }
    }
    
    public int getDD_Order_ID() {
        final Integer ii = (Integer)this.get_Value("DD_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_DD_OrderLine getDD_OrderLine() throws RuntimeException {
        return (I_DD_OrderLine)MTable.get(this.getCtx(), "DD_OrderLine").getPO(this.getDD_OrderLine_ID(), this.get_TrxName());
    }
    
    public void setDD_OrderLine_ID(final int DD_OrderLine_ID) {
        if (DD_OrderLine_ID < 1) {
            this.set_Value("DD_OrderLine_ID", (Object)null);
        }
        else {
            this.set_Value("DD_OrderLine_ID", (Object)DD_OrderLine_ID);
        }
    }
    
    public int getDD_OrderLine_ID() {
        final Integer ii = (Integer)this.get_Value("DD_OrderLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDescription(final String Description) {
        this.set_Value("Description", (Object)Description);
    }
    
    public String getDescription() {
        return (String)this.get_Value("Description");
    }
    
    public void setDocStatus(final String DocStatus) {
        this.set_Value("DocStatus", (Object)DocStatus);
    }
    
    public String getDocStatus() {
        return (String)this.get_Value("DocStatus");
    }
    
    public void setIsAvailable(final boolean IsAvailable) {
        this.set_Value("IsAvailable", (Object)IsAvailable);
    }
    
    public boolean isAvailable() {
        final Object oo = this.get_Value("IsAvailable");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public I_M_Forecast getM_Forecast() throws RuntimeException {
        return (I_M_Forecast)MTable.get(this.getCtx(), "M_Forecast").getPO(this.getM_Forecast_ID(), this.get_TrxName());
    }
    
    public void setM_Forecast_ID(final int M_Forecast_ID) {
        if (M_Forecast_ID < 1) {
            this.set_Value("M_Forecast_ID", (Object)null);
        }
        else {
            this.set_Value("M_Forecast_ID", (Object)M_Forecast_ID);
        }
    }
    
    public int getM_Forecast_ID() {
        final Integer ii = (Integer)this.get_Value("M_Forecast_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_ForecastLine getM_ForecastLine() throws RuntimeException {
        return (I_M_ForecastLine)MTable.get(this.getCtx(), "M_ForecastLine").getPO(this.getM_ForecastLine_ID(), this.get_TrxName());
    }
    
    public void setM_ForecastLine_ID(final int M_ForecastLine_ID) {
        if (M_ForecastLine_ID < 1) {
            this.set_Value("M_ForecastLine_ID", (Object)null);
        }
        else {
            this.set_Value("M_ForecastLine_ID", (Object)M_ForecastLine_ID);
        }
    }
    
    public int getM_ForecastLine_ID() {
        final Integer ii = (Integer)this.get_Value("M_ForecastLine_ID");
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
    
    public I_M_Requisition getM_Requisition() throws RuntimeException {
        return (I_M_Requisition)MTable.get(this.getCtx(), "M_Requisition").getPO(this.getM_Requisition_ID(), this.get_TrxName());
    }
    
    public void setM_Requisition_ID(final int M_Requisition_ID) {
        if (M_Requisition_ID < 1) {
            this.set_Value("M_Requisition_ID", (Object)null);
        }
        else {
            this.set_Value("M_Requisition_ID", (Object)M_Requisition_ID);
        }
    }
    
    public int getM_Requisition_ID() {
        final Integer ii = (Integer)this.get_Value("M_Requisition_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_RequisitionLine getM_RequisitionLine() throws RuntimeException {
        return (I_M_RequisitionLine)MTable.get(this.getCtx(), "M_RequisitionLine").getPO(this.getM_RequisitionLine_ID(), this.get_TrxName());
    }
    
    public void setM_RequisitionLine_ID(final int M_RequisitionLine_ID) {
        if (M_RequisitionLine_ID < 1) {
            this.set_Value("M_RequisitionLine_ID", (Object)null);
        }
        else {
            this.set_Value("M_RequisitionLine_ID", (Object)M_RequisitionLine_ID);
        }
    }
    
    public int getM_RequisitionLine_ID() {
        final Integer ii = (Integer)this.get_Value("M_RequisitionLine_ID");
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
    
    public void setName(final String Name) {
        this.set_Value("Name", (Object)Name);
    }
    
    public String getName() {
        return (String)this.get_Value("Name");
    }
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), this.getName());
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
    
    public void setPP_MRP_ID(final int PP_MRP_ID) {
        if (PP_MRP_ID < 1) {
            this.set_ValueNoCheck("PP_MRP_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_MRP_ID", (Object)PP_MRP_ID);
        }
    }
    
    public int getPP_MRP_ID() {
        final Integer ii = (Integer)this.get_Value("PP_MRP_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_MRP_UU(final String PP_MRP_UU) {
        this.set_Value("PP_MRP_UU", (Object)PP_MRP_UU);
    }
    
    public String getPP_MRP_UU() {
        return (String)this.get_Value("PP_MRP_UU");
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
    
    public void setPriority(final String Priority) {
        this.set_Value("Priority", (Object)Priority);
    }
    
    public String getPriority() {
        return (String)this.get_Value("Priority");
    }
    
    public void setQty(final BigDecimal Qty) {
        this.set_Value("Qty", (Object)Qty);
    }
    
    public BigDecimal getQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Qty");
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
    
    public void setTypeMRP(final String TypeMRP) {
        this.set_Value("TypeMRP", (Object)TypeMRP);
    }
    
    public String getTypeMRP() {
        return (String)this.get_Value("TypeMRP");
    }
    
    public void setValue(final String Value) {
        this.set_Value("Value", (Object)Value);
    }
    
    public String getValue() {
        return (String)this.get_Value("Value");
    }
    
    public void setVersion(final BigDecimal Version) {
        this.set_Value("Version", (Object)Version);
    }
    
    public BigDecimal getVersion() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Version");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
}
