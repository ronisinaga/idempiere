// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process.eam;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.compiere.model.MForecastLine;
import org.compiere.util.TimeUtil;
import org.eevolution.model.MPPProductPlanning;
import org.compiere.util.DB;
import org.compiere.model.MAsset;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.util.Env;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class CreateForecastLine4eAM extends SvrProcess
{
    private int p_AD_Client_ID;
    private int p_AD_User_ID;
    private int p_A_Asset_ID;
    private int p_M_Forecast_ID;
    private Timestamp p_DateValue;
    private int p_PlanningHorizon;
    private Boolean p_DeleteOld;
    
    public CreateForecastLine4eAM() {
        this.p_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
        this.p_AD_User_ID = Env.getAD_User_ID(Env.getCtx());
        this.p_A_Asset_ID = 0;
        this.p_M_Forecast_ID = 0;
        this.p_DateValue = null;
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
        if (this.p_DateValue == null) {
            this.p_DateValue = new Timestamp(System.currentTimeMillis());
        }
        String _result = "";
        String pmrule = null;
        Timestamp MaintenanceDate = null;
        Timestamp FinishMaintenanceDate = null;
        Timestamp NextMaintenanceDate = null;
        Timestamp LastMaintenanceDate = null;
        int LastMaintenanceUnit = 0;
        int rate = 0;
        int rate_planning_end = 0;
        int rate_current = 0;
        int counter1 = 1;
        int counter2 = 1;
        int unitscycles = 0;
        int unit_current = 0;
        final MAsset asset = new MAsset(this.getCtx(), this.p_A_Asset_ID, this.get_TrxName());
        if (this.p_DeleteOld) {
            DB.executeUpdateEx("DELETE FROM pp_mrp WHERE  m_forecastline_ID IN  (SELECT m_forecastline_ID FROM m_forecastline WHERE m_product_ID=" + asset.getM_Product_ID() + " AND  AD_Org_ID=" + asset.getAD_Org_ID() + ");" + " DELETE FROM m_forecastline WHERE m_product_ID=" + asset.getM_Product_ID() + " AND AD_Org_ID=" + asset.getAD_Org_ID(), this.get_TrxName());
            this.commitEx();
        }
        final String sql_so = "SELECT a_asset_id, Asset_Prev_Maintenance_Rule,   nextmaintenencedate, pp_product_planning_id, rate, unitscycles,   description, c_uom_id, validfrom, validto  FROM A_Asset_Prev_Maintenance  WHERE isActive='Y' AND A_Asset_ID=" + this.p_A_Asset_ID;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql_so, (String)null);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final MPPProductPlanning pp = new MPPProductPlanning(this.getCtx(), rs.getInt(4), this.get_TrxName());
                pmrule = rs.getString("Asset_Prev_Maintenance_Rule");
                if (pmrule.equals("D")) {
                    for (FinishMaintenanceDate = TimeUtil.addDays(asset.getLastMaintenanceDate(), this.p_PlanningHorizon), MaintenanceDate = TimeUtil.addDays(asset.getLastMaintenanceDate(), rs.getInt("UnitsCycles")); MaintenanceDate.compareTo(FinishMaintenanceDate) <= 0; MaintenanceDate = TimeUtil.addDays(MaintenanceDate, rs.getInt("UnitsCycles"))) {
                        final MForecastLine fcl = new MForecastLine(this.getCtx(), 0, this.get_TrxName());
                        fcl.setAD_Org_ID(pp.getAD_Org_ID());
                        fcl.setM_Forecast_ID(this.p_M_Forecast_ID);
                        fcl.setM_Product_ID(asset.getM_Product_ID());
                        fcl.setM_Warehouse_ID(pp.getM_Warehouse_ID());
                        fcl.setQty(Env.ONE);
                        fcl.setC_Period_ID(this.getPeriod_ID(MaintenanceDate));
                        fcl.setDatePromised(MaintenanceDate);
                        fcl.saveEx();
                    }
                    _result = String.valueOf(_result) + " Created Forecat Lines for " + asset.getName() + " based Date Rules!";
                }
                if (pmrule.equals("M")) {
                    asset.getUseUnits();
                    rate = rs.getInt("Rate");
                    unitscycles = rs.getInt("UnitsCycles");
                    LastMaintenanceUnit = asset.getLastMaintenanceUnit();
                    LastMaintenanceDate = asset.getLastMaintenanceDate();
                    for (rate_planning_end = LastMaintenanceUnit + this.p_PlanningHorizon * rate, rate_current = LastMaintenanceUnit + rate; rate_current <= rate_planning_end; rate_current += rate, ++counter1, ++counter2) {
                        unit_current = rate * counter2;
                        if (unit_current >= unitscycles) {
                            final MForecastLine fcl = new MForecastLine(this.getCtx(), 0, this.get_TrxName());
                            fcl.setAD_Org_ID(pp.getAD_Org_ID());
                            fcl.setM_Forecast_ID(this.p_M_Forecast_ID);
                            fcl.setM_Product_ID(asset.getM_Product_ID());
                            fcl.setM_Warehouse_ID(pp.getM_Warehouse_ID());
                            fcl.setQty(Env.ONE);
                            fcl.setC_Period_ID(this.getPeriod_ID(TimeUtil.addDays(LastMaintenanceDate, counter1)));
                            fcl.setDatePromised(TimeUtil.addDays(LastMaintenanceDate, counter1));
                            fcl.saveEx();
                            unit_current = 0;
                            counter2 = 0;
                        }
                    }
                    _result = String.valueOf(_result) + " Created Forecat Lines for " + asset.getName() + " based Meter Rules!";
                    rate_current = 0;
                    counter1 = 0;
                    counter2 = 0;
                }
                if (pmrule.equals("L")) {
                    NextMaintenanceDate = rs.getTimestamp("NextMaintenenceDate");
                    if (NextMaintenanceDate == null) {
                        continue;
                    }
                    final MForecastLine fcl = new MForecastLine(this.getCtx(), 0, this.get_TrxName());
                    fcl.setAD_Org_ID(pp.getAD_Org_ID());
                    fcl.setM_Forecast_ID(this.p_M_Forecast_ID);
                    fcl.setM_Product_ID(asset.getM_Product_ID());
                    fcl.setM_Warehouse_ID(pp.getM_Warehouse_ID());
                    fcl.setQty(Env.ONE);
                    fcl.setC_Period_ID(this.getPeriod_ID(NextMaintenanceDate));
                    fcl.setDatePromised(NextMaintenanceDate);
                    fcl.saveEx();
                    _result = String.valueOf(_result) + " Created Forecat Lines for " + asset.getName() + " based List Dates!";
                }
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql_so, (Throwable)e);
            return _result;
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return _result;
    }
    
    public String getPMRuleStr(final int Asset_ID, final String Field) {
        return DB.getSQLValueString(this.get_TrxName(), "SELECT " + Field + " FROM A_Asset_Prev_Maintenance WHERE isActive='Y' AND A_Asset_ID=" + Asset_ID, new Object[0]);
    }
    
    public int getPMRuleInt(final int Asset_ID, final String Field) {
        return DB.getSQLValue(this.get_TrxName(), "SELECT " + Field + " FROM A_Asset_Prev_Maintenance WHERE isActive='Y' AND A_Asset_ID=" + Asset_ID);
    }
    
    public Timestamp getPMRuleTS(final int Asset_ID, final String Field) {
        return DB.getSQLValueTS(this.get_TrxName(), "SELECT " + Field + " FROM A_Asset_Prev_Maintenance WHERE isActive='Y' AND A_Asset_ID=" + Asset_ID, new Object[0]);
    }
    
    public int getPeriod_ID(final Timestamp date) {
        return DB.getSQLValue(this.get_TrxName(), "SELECT C_Period_ID FROM C_Period WHERE AD_Client_ID=" + this.p_AD_Client_ID + " AND CAST('" + date + "' AS date) BETWEEN CAST(StartDate AS date) and CAST(EndDate AS date);");
    }
}
