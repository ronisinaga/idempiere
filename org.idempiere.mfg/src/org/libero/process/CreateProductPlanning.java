// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.eevolution.model.MPPProductPlanning;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import org.adempiere.exceptions.DBException;
import java.util.List;
import org.compiere.util.DB;
import java.util.ArrayList;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.model.MWarehouse;
import java.util.logging.Level;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.process.SvrProcess;

public class CreateProductPlanning extends SvrProcess
{
    private int p_M_Product_Category_ID;
    private int p_M_Warehouse_ID;
    private int p_S_Resource_ID;
    private int p_Planner;
    private BigDecimal p_DeliveryTime_Promised;
    private int p_DD_NetworkDistribution_ID;
    private int p_AD_Workflow_ID;
    private BigDecimal p_TimeFence;
    private boolean p_CreatePlan;
    private boolean p_MPS;
    private String p_OrderPolicy;
    private BigDecimal p_OrderPeriod;
    private BigDecimal p_TransferTime;
    private BigDecimal p_SafetyStock;
    private BigDecimal p_Order_Min;
    private BigDecimal p_Order_Max;
    private BigDecimal p_Order_Pack;
    private BigDecimal p_Order_Qty;
    private BigDecimal p_WorkingTime;
    private int p_Yield;
    private int m_AD_Org_ID;
    private int m_AD_Client_ID;
    private int count_created;
    private int count_updated;
    private int count_error;
    
    public CreateProductPlanning() {
        this.p_M_Product_Category_ID = 0;
        this.p_M_Warehouse_ID = 0;
        this.p_S_Resource_ID = 0;
        this.p_Planner = 0;
        this.p_DeliveryTime_Promised = Env.ZERO;
        this.p_DD_NetworkDistribution_ID = 0;
        this.p_AD_Workflow_ID = 0;
        this.p_TimeFence = Env.ZERO;
        this.p_CreatePlan = false;
        this.p_MPS = false;
        this.p_OrderPolicy = "";
        this.p_OrderPeriod = Env.ZERO;
        this.p_TransferTime = Env.ZERO;
        this.p_SafetyStock = Env.ZERO;
        this.p_Order_Min = Env.ZERO;
        this.p_Order_Max = Env.ZERO;
        this.p_Order_Pack = Env.ZERO;
        this.p_Order_Qty = Env.ZERO;
        this.p_WorkingTime = Env.ZERO;
        this.p_Yield = 0;
        this.m_AD_Org_ID = 0;
        this.m_AD_Client_ID = 0;
        this.count_created = 0;
        this.count_updated = 0;
        this.count_error = 0;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("M_Product_Category_ID")) {
                    this.p_M_Product_Category_ID = para.getParameterAsInt();
                }
                else if (name.equals("M_Warehouse_ID")) {
                    this.p_M_Warehouse_ID = para.getParameterAsInt();
                }
                else if (name.equals("S_Resource_ID")) {
                    this.p_S_Resource_ID = para.getParameterAsInt();
                }
                else if (name.equals("IsCreatePlan")) {
                    this.p_CreatePlan = "Y".equals(para.getParameter());
                }
                else if (name.equals("IsMPS")) {
                    this.p_MPS = "Y".equals(para.getParameter());
                }
                else if (name.equals("DD_NetworkDistribution_ID")) {
                    this.p_DD_NetworkDistribution_ID = para.getParameterAsInt();
                }
                else if (name.equals("AD_Workflow_ID")) {
                    this.p_AD_Workflow_ID = para.getParameterAsInt();
                }
                else if (name.equals("TimeFence")) {
                    this.p_TimeFence = (BigDecimal)para.getParameter();
                }
                else if (name.equals("TransfertTime")) {
                    this.p_TransferTime = (BigDecimal)para.getParameter();
                }
                else if (name.equals("SafetyStock")) {
                    this.p_SafetyStock = (BigDecimal)para.getParameter();
                }
                else if (name.equals("Order_Min")) {
                    this.p_Order_Min = (BigDecimal)para.getParameter();
                }
                else if (name.equals("Order_Max")) {
                    this.p_Order_Max = (BigDecimal)para.getParameter();
                }
                else if (name.equals("Order_Pack")) {
                    this.p_Order_Pack = (BigDecimal)para.getParameter();
                }
                else if (name.equals("Order_Qty")) {
                    this.p_Order_Qty = (BigDecimal)para.getParameter();
                }
                else if (name.equals("WorkingTime")) {
                    this.p_WorkingTime = (BigDecimal)para.getParameter();
                }
                else if (name.equals("Yield")) {
                    this.p_Yield = ((BigDecimal)para.getParameter()).intValue();
                }
                else if (name.equals("DeliveryTime_Promised")) {
                    this.p_DeliveryTime_Promised = (BigDecimal)para.getParameter();
                }
                else if (name.equals("Order_Period")) {
                    this.p_OrderPeriod = (BigDecimal)para.getParameter();
                }
                else if (name.equals("Order_Policy")) {
                    this.p_OrderPolicy = (String)para.getParameter();
                }
                else if (name.equals("Planner_ID")) {
                    this.p_Planner = para.getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
        this.m_AD_Client_ID = Env.getAD_Client_ID(this.getCtx());
        if (this.p_M_Warehouse_ID > 0) {
            final MWarehouse w = MWarehouse.get(this.getCtx(), this.p_M_Warehouse_ID);
            this.m_AD_Org_ID = w.getAD_Org_ID();
        }
    }
    
    protected String doIt() throws Exception {
        final ArrayList<Object> params = new ArrayList<Object>();
        String sql = "SELECT p.M_Product_ID FROM M_Product p WHERE p.AD_Client_ID=?";
        params.add(this.m_AD_Client_ID);
        if (this.p_M_Product_Category_ID > 0) {
            sql = String.valueOf(sql) + " AND p.M_Product_Category_ID=?";
            params.add(this.p_M_Product_Category_ID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
            DB.setParameters(pstmt, (List)params);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final int M_Product_ID = rs.getInt(1);
                this.createPlanning(M_Product_ID);
            }
        }
        catch (SQLException e) {
            throw new DBException(e, sql);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return "@Created@ #" + this.count_created + " @Updated@ #" + this.count_updated + " @Error@ #" + this.count_error;
    }
    
    private void createPlanning(final int M_Product_ID) {
        MPPProductPlanning pp = MPPProductPlanning.get(this.getCtx(), this.m_AD_Client_ID, this.m_AD_Org_ID, this.p_M_Warehouse_ID, this.p_S_Resource_ID, M_Product_ID, this.get_TrxName());
        final boolean isNew = pp == null;
        if (pp == null) {
            pp = new MPPProductPlanning(this.getCtx(), 0, this.get_TrxName());
            pp.setAD_Org_ID(this.m_AD_Org_ID);
            pp.setM_Warehouse_ID(this.p_M_Warehouse_ID);
            pp.setS_Resource_ID(this.p_S_Resource_ID);
            pp.setM_Product_ID(M_Product_ID);
        }
        pp.setDD_NetworkDistribution_ID(this.p_DD_NetworkDistribution_ID);
        pp.setAD_Workflow_ID(this.p_AD_Workflow_ID);
        pp.setIsCreatePlan(this.p_CreatePlan);
        pp.setIsMPS(this.p_MPS);
        pp.setIsRequiredMRP(true);
        pp.setIsRequiredDRP(true);
        pp.setDeliveryTime_Promised(this.p_DeliveryTime_Promised);
        pp.setOrder_Period(this.p_OrderPeriod);
        pp.setPlanner_ID(this.p_Planner);
        pp.setOrder_Policy(this.p_OrderPolicy);
        pp.setSafetyStock(this.p_SafetyStock);
        pp.setOrder_Qty(this.p_Order_Qty);
        pp.setOrder_Min(this.p_Order_Min);
        pp.setOrder_Max(this.p_Order_Max);
        pp.setOrder_Pack(this.p_Order_Pack);
        pp.setTimeFence(this.p_TimeFence);
        pp.setTransfertTime(this.p_TransferTime);
        pp.setIsPhantom(false);
        pp.setWorkingTime(this.p_WorkingTime);
        pp.setYield(this.p_Yield);
        if (!pp.save()) {
            ++this.count_error;
        }
        if (isNew) {
            ++this.count_created;
        }
        else {
            ++this.count_updated;
        }
    }
}
