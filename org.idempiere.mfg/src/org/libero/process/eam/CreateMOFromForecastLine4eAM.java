// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process.eam;

import java.sql.SQLException;
import org.compiere.model.PO;
import org.compiere.model.POResultSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.libero.model.MPPOrder;
import org.eevolution.model.MPPProductPlanning;
import org.compiere.model.MForecastLine;
import org.compiere.model.Query;
import java.util.ArrayList;
import org.compiere.model.MAsset;
import org.compiere.util.Trx;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.util.Env;
import org.compiere.process.SvrProcess;

public class CreateMOFromForecastLine4eAM extends SvrProcess
{
    private int p_AD_Client_ID;
    private int p_AD_User_ID;
    private int p_A_Asset_ID;
    private int p_M_Forecast_ID;
    private int p_PlanningHorizon;
    private Boolean p_DeleteOld;
    
    public CreateMOFromForecastLine4eAM() {
        this.p_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
        this.p_AD_User_ID = Env.getAD_User_ID(Env.getCtx());
        this.p_A_Asset_ID = 0;
        this.p_M_Forecast_ID = 0;
        this.p_PlanningHorizon = 0;
        this.p_DeleteOld = false;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("PlanningHorizon")) {
                    this.p_PlanningHorizon = para.getParameterAsInt();
                }
                else if (name.equals("A_Asset_ID")) {
                    this.p_A_Asset_ID = para.getParameterAsInt();
                }
                else if (name.equals("M_Forecast_ID")) {
                    this.p_M_Forecast_ID = para.getParameterAsInt();
                }
                else if (name.equals("DeleteOld")) {
                    this.p_DeleteOld = para.getParameterAsBoolean();
                }
                else {
                    this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        final String _result = "[v1.07] ";
        int count_ok = 0;
        int count_error = 0;
        final Trx trx_fcline = Trx.get(Trx.createTrxName("fcLine"), true);
        final MAsset asset = new MAsset(this.getCtx(), this.p_A_Asset_ID, this.get_TrxName());
        if (this.p_DeleteOld) {
            final List<Object> params = new ArrayList<Object>();
            params.add(asset.getAD_Client_ID());
            params.add(asset.getAD_Org_ID());
            params.add(asset.getM_Product_ID());
            this.deletePO("R_Request", " Record_ID IN (SELECT PP_Order_ID FROM PP_Order  WHERE DocStatus IN ('DR','IP') AND AD_Client_ID=? AND AD_Org_ID=? AND M_Product_ID=?)", params);
            this.deletePO("PP_Order", " DocStatus IN ('DR','IP') AND AD_Client_ID=? AND AD_Org_ID=? AND M_Product_ID=?", params);
        }
        try {
            final String whereClause = " M_Product_ID=" + asset.getM_Product_ID();
            final Collection<MForecastLine> fclines = new Query(this.getCtx(), "M_ForecastLine", whereClause, trx_fcline.getTrxName()).setOrderBy("M_ForecastLine_ID").setClient_ID().list();
            for (final MForecastLine fcline : fclines) {
                final int PP_ID = 50102;
                if (PP_ID > 0) {
                    final MPPProductPlanning pp = new MPPProductPlanning(Env.getCtx(), PP_ID, trx_fcline.getTrxName());
                    final MPPOrder morder = new MPPOrder(Env.getCtx(), 0, trx_fcline.getTrxName());
                    morder.addDescription("[created from Forecast])");
                    morder.setAD_Org_ID(fcline.getAD_Org_ID());
                    morder.setLine(10);
                    morder.setC_DocTypeTarget_ID(50010);
                    morder.setC_DocType_ID(50010);
                    morder.setS_Resource_ID(pp.getS_Resource_ID());
                    morder.setM_Warehouse_ID(fcline.getM_Warehouse_ID());
                    morder.setM_Product_ID(fcline.getM_Product_ID());
                    morder.setM_AttributeSetInstance_ID(0);
                    morder.setPP_Product_BOM_ID(pp.getPP_Product_BOM_ID());
                    morder.setAD_Workflow_ID(pp.getAD_Workflow_ID());
                    morder.setPlanner_ID(pp.getPlanner_ID());
                    morder.setDateOrdered(fcline.getDatePromised());
                    morder.setDatePromised(fcline.getDatePromised());
                    morder.setDateStartSchedule(fcline.getDatePromised());
                    morder.setDateFinishSchedule(fcline.getDatePromised());
                    morder.setQty(fcline.getQty());
                    morder.setYield(Env.ZERO);
                    morder.setScheduleType("D");
                    morder.setPriorityRule("5");
                    morder.saveEx();
                    if (morder.processIt("IP")) {
                        morder.saveEx();
                        ++count_ok;
                    }
                    else {
                        ++count_error;
                    }
                    trx_fcline.commit();
                    trx_fcline.commit();
                }
            }
            fclines.clear();
        }
        finally {
            if (trx_fcline != null) {
                trx_fcline.close();
            }
        }
        if (trx_fcline != null) {
            trx_fcline.close();
        }
        return String.valueOf(_result) + " Created:" + count_ok + " [Errors:" + count_error + "]";
    }
    
    private void deletePO(final String tableName, final String whereClause, final List<Object> params) throws SQLException {
        final POResultSet<PO> rsd = (POResultSet<PO>)new Query(this.getCtx(), tableName, whereClause, this.get_TrxName()).setParameters((List)params).scroll();
        try {
            while (rsd.hasNext()) {
                rsd.next().deleteEx(true);
                this.commitEx();
            }
        }
        finally {
            rsd.close();
        }
        rsd.close();
    }
}
