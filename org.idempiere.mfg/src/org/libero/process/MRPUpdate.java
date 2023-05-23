// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.PO;
import org.compiere.model.POResultSet;
import org.compiere.util.DB;
import java.sql.SQLException;
import java.util.Iterator;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.model.MWarehouse;
import org.compiere.model.MOrg;
import org.compiere.model.MResource;
import org.adempiere.exceptions.AdempiereException;
import java.util.List;
import org.compiere.model.Query;
import java.util.ArrayList;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.util.Env;
import org.compiere.process.SvrProcess;

public class MRPUpdate extends SvrProcess
{
    private int m_AD_Client_ID;
    private int p_AD_Org_ID;
    private int p_S_Resource_ID;
    private int p_M_Warehouse_ID;
    
    public MRPUpdate() {
        this.m_AD_Client_ID = 0;
        this.p_AD_Org_ID = 0;
        this.p_S_Resource_ID = 0;
        this.p_M_Warehouse_ID = 0;
    }
    
    protected void prepare() {
        this.m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("AD_Org_ID")) {
                    this.p_AD_Org_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("S_Resource_ID")) {
                    this.p_S_Resource_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_Warehouse_ID")) {
                    this.p_M_Warehouse_ID = para[i].getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        String result = null;
        ArrayList<Object> parameters = new ArrayList<Object>();
        StringBuffer whereClause = new StringBuffer("ManufacturingResourceType=? AND AD_Client_ID=?");
        parameters.add("PT");
        parameters.add(this.m_AD_Client_ID);
        if (this.p_S_Resource_ID > 0) {
            whereClause.append(" AND S_Resource_ID=?");
            parameters.add(this.p_S_Resource_ID);
        }
        final List<MResource> plants = new Query(this.getCtx(), "S_Resource", whereClause.toString(), this.get_TrxName()).setParameters((List)parameters).list();
        if (plants.size() == 0) {
            throw new AdempiereException("No plants found");
        }
        for (final MResource plant : plants) {
            this.log.info("Run MRP to Plant: " + plant.getName());
            parameters = new ArrayList<Object>();
            whereClause = new StringBuffer("AD_Client_ID=?");
            parameters.add(this.m_AD_Client_ID);
            if (this.p_AD_Org_ID > 0) {
                whereClause.append(" AND AD_Org_ID=?");
                parameters.add(this.p_AD_Org_ID);
            }
            final List<MOrg> organizations = new Query(this.getCtx(), "AD_Org", whereClause.toString(), this.get_TrxName()).setParameters((List)parameters).list();
            for (final MOrg organization : organizations) {
                this.log.info("Run MRP to Organization: " + organization.getName());
                if (this.p_M_Warehouse_ID == 0) {
                    final MWarehouse[] ws = MWarehouse.getForOrg(this.getCtx(), organization.getAD_Org_ID());
                    MWarehouse[] array;
                    for (int length = (array = ws).length, i = 0; i < length; ++i) {
                        final MWarehouse w = array[i];
                        this.log.info("Run MRP to Wharehouse: " + w.getName());
                        this.deleteRecords(this.m_AD_Client_ID, organization.getAD_Org_ID(), plant.getS_Resource_ID(), w.getM_Warehouse_ID());
                        this.createRecords(this.m_AD_Client_ID, organization.getAD_Org_ID(), plant.getS_Resource_ID(), w.getM_Warehouse_ID());
                        result = String.valueOf(result) + "<br>finish MRP to Warehouse " + w.getName();
                    }
                }
                else {
                    this.log.info("Run MRP to Wharehouse: " + this.p_M_Warehouse_ID);
                    this.deleteRecords(this.m_AD_Client_ID, organization.getAD_Org_ID(), plant.getS_Resource_ID(), this.p_M_Warehouse_ID);
                    this.createRecords(this.m_AD_Client_ID, organization.getAD_Org_ID(), plant.getS_Resource_ID(), this.p_M_Warehouse_ID);
                }
                result = String.valueOf(result) + "<br>finish MRP to Organization " + organization.getName();
            }
            result = String.valueOf(result) + "<br>finish MRP to Plant " + plant.getName();
        }
        if (Util.isEmpty(result, true)) {
            return "No records found";
        }
        return Msg.getMsg(this.getCtx(), "ProcessOK");
    }
    
    private void deleteRecords(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID) throws SQLException {
        List<Object> params = new ArrayList<Object>();
        params.add(AD_Client_ID);
        params.add(AD_Org_ID);
        params.add(M_Warehouse_ID);
        String whereClause = "OrderType IN ('FCT','POR', 'SOO', 'POO') AND AD_Client_ID=? AND AD_Org_ID=? AND M_Warehouse_ID=?";
        this.executeUpdate("DELETE FROM PP_MRP WHERE " + whereClause, params);
        whereClause = "DocStatus IN ('DR','CL') AND AD_Client_ID=? AND AD_Org_ID=? AND M_Warehouse_ID=?";
        this.deletePO("M_Requisition", whereClause, params);
        whereClause = "DocStatus IN ('DR') AND AD_Client_ID=? AND AD_Org_ID=? AND M_Warehouse_ID=?";
        this.deletePO("DD_Order", whereClause, params);
        params = new ArrayList<Object>();
        params.add(AD_Client_ID);
        params.add(AD_Org_ID);
        params.add(S_Resource_ID);
        params.add(M_Warehouse_ID);
        whereClause = "OrderType IN ('MOP','DOO') AND AD_Client_ID=? AND AD_Org_ID=? AND S_Resource_ID= ? AND M_Warehouse_ID=?";
        this.executeUpdate("DELETE FROM PP_MRP WHERE " + whereClause, params);
        whereClause = "DocStatus='DR' AND AD_Client_ID=? AND AD_Org_ID=? AND S_Resource_ID= ? AND M_Warehouse_ID=?";
        this.deletePO("PP_Order", whereClause, params);
        params = new ArrayList<Object>();
        params.add(AD_Client_ID);
        params.add(AD_Org_ID);
        whereClause = "AD_Table_ID=53043 AND AD_Client_ID=? AND AD_Org_ID=?";
        this.executeUpdate("DELETE FROM AD_Note WHERE " + whereClause, params);
    }
    
    private void createRecords(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID) throws SQLException {
        List<Object> params = new ArrayList<Object>();
        params.add(AD_Client_ID);
        params.add(AD_Org_ID);
        params.add(M_Warehouse_ID);
        String sql_insert = " SELECT t.ad_org_id,t.created, t.createdby , t.datepromised,t.datepromised, t.datepromised, t.datepromised, f.Name,'IP', t.isactive , t.m_forecastline_id, t.m_forecast_id, null, null,null, null,null, null,t.m_product_id, t.m_warehouse_id,nextidfunc(53040,'N'), null ,t.qty,  'D', 'FCT', t.updated, t.updatedby, f.Name,t.ad_client_id , null as S_Resource_ID, null as C_BPartner_ID  FROM M_ForecastLine t  INNER JOIN M_Forecast f ON (f.M_Forecast_ID=t.M_Forecast_ID)  WHERE t.Qty > 0 AND t.AD_Client_ID=? AND t.AD_Org_ID=? AND t.M_Warehouse_ID= ?";
        this.executeUpdate("INSERT INTO PP_MRP (ad_org_id, created, createdby , dateordered,datepromised, datestart, datestartschedule, description,docstatus, isactive , m_forecastline_id, m_forecast_id,pp_order_id, pp_order_bomline_id,c_order_id, c_orderline_id,m_requisition_id, m_requisitionline_id,m_product_id, m_warehouse_id, pp_mrp_id, planner_id, qty, typemrp, ordertype, updated, updatedby, value, ad_client_id, s_resource_id, c_bpartner_id )" + sql_insert, params);
        sql_insert = " SELECT t.ad_org_id,t.created, t.createdby , t.datepromised,t.datepromised, t.datepromised, t.datepromised, o.DocumentNo,o.DocStatus, o.isactive ,  null, null,  null, null,  t.c_order_id, t.c_orderline_id,  null, null, t.m_product_id, t.m_warehouse_id,nextidfunc(53040,'N'), null ,t.QtyOrdered-t.QtyDelivered,  (case when o.IsSOTrx='Y' then 'D' else 'S' end) , (case when o.IsSOTrx='Y' then 'SOO' else 'POO' end), t.updated, t.updatedby, o.DocumentNo,t.ad_client_id , null as S_Resource_ID, o.C_BPartner_ID FROM C_OrderLine t INNER JOIN C_Order o  ON (o.c_order_id=t.c_order_id) WHERE  (t.QtyOrdered - t.QtyDelivered) <> 0 AND o.DocStatus IN ('IP','CO') AND t.AD_Client_ID=? AND t.AD_Org_ID=? AND t.M_Warehouse_ID= ?";
        this.executeUpdate("INSERT INTO PP_MRP (ad_org_id, created, createdby , dateordered,datepromised, datestart, datestartschedule, description,docstatus, isactive , m_forecastline_id, m_forecast_id,pp_order_id, pp_order_bomline_id,c_order_id, c_orderline_id,m_requisition_id, m_requisitionline_id,m_product_id, m_warehouse_id, pp_mrp_id, planner_id, qty, typemrp, ordertype, updated, updatedby, value, ad_client_id, s_resource_id, c_bpartner_id )" + sql_insert, params);
        sql_insert = " SELECT rl.ad_org_id,rl.created, rl.createdby , t.daterequired, t.daterequired,  t.daterequired,  t.daterequired, t.DocumentNo,t.DocStatus, t.isactive ,  null, null,  null, null,  null, null, rl.m_requisition_id, rl.m_requisitionline_id, rl.m_product_id, t.m_warehouse_id,nextidfunc(53040,'N'), null ,rl.Qty, 'S', 'POR', rl.updated, rl.updatedby, t.DocumentNo,rl.ad_client_id , null as S_Resource_ID, null as C_BPartner_ID  FROM M_RequisitionLine rl INNER JOIN M_Requisition t ON (rl.m_requisition_id=t.m_requisition_id) WHERE rl.Qty > 0 AND t.DocStatus IN ('DR','IN') AND t.AD_Client_ID=? AND t.AD_Org_ID=? AND t.M_Warehouse_ID= ?";
        this.executeUpdate("INSERT INTO PP_MRP (ad_org_id, created, createdby , dateordered,datepromised, datestart, datestartschedule, description,docstatus, isactive , m_forecastline_id, m_forecast_id,pp_order_id, pp_order_bomline_id,c_order_id, c_orderline_id,m_requisition_id, m_requisitionline_id,m_product_id, m_warehouse_id, pp_mrp_id, planner_id, qty, typemrp, ordertype, updated, updatedby, value, ad_client_id, s_resource_id, c_bpartner_id )" + sql_insert, params);
        params = new ArrayList<Object>();
        params.add(AD_Client_ID);
        params.add(AD_Org_ID);
        params.add(S_Resource_ID);
        params.add(M_Warehouse_ID);
        sql_insert = " SELECT t.ad_org_id,t.created, t.createdby , t.datepromised,t.datepromised, t.datepromised, t.datepromised, t.DocumentNo,t.DocStatus, t.isactive ,  null, null, t.pp_order_id, null, null, null,  null, null, t.m_product_id, t.m_warehouse_id,nextidfunc(53040,'N'), null ,t.QtyOrdered-t.QtyDelivered,  'S', 'MOP', t.updated, t.updatedby, t.DocumentNo,t.ad_client_id, t.S_Resource_ID, null as C_BPartner_ID  FROM PP_Order t  WHERE (t.QtyOrdered - t.QtyDelivered) <> 0 AND t.DocStatus IN ('DR','IP','CO') AND t.AD_Client_ID=? AND t.AD_Org_ID=? AND t.S_Resource_ID=? AND t.M_Warehouse_ID= ?";
        this.executeUpdate("INSERT INTO PP_MRP (ad_org_id, created, createdby , dateordered,datepromised, datestart, datestartschedule, description,docstatus, isactive , m_forecastline_id, m_forecast_id,pp_order_id, pp_order_bomline_id,c_order_id, c_orderline_id,m_requisition_id, m_requisitionline_id,m_product_id, m_warehouse_id, pp_mrp_id, planner_id, qty, typemrp, ordertype, updated, updatedby, value, ad_client_id, s_resource_id, c_bpartner_id )" + sql_insert, params);
        sql_insert = " SELECT t.ad_org_id,t.created, t.createdby , o.datepromised,o.datepromised, o.datepromised, o.datepromised, o.DocumentNo,o.DocStatus, o.isactive ,  null, null, t.pp_order_id, t.pp_order_bomline_id, null, null,  null, null, t.m_product_id, t.m_warehouse_id,nextidfunc(53040,'N'), null ,t.QtyEntered-t.QtyDelivered,  'D', 'MOP', t.updated, t.updatedby, o.DocumentNo,t.ad_client_id, o.S_Resource_ID, null as C_BPartner_ID  FROM PP_Order_BOMLine t  INNER JOIN PP_Order o ON (o.pp_order_id=t.pp_order_id) WHERE  (t.QtyEntered-t.QtyDelivered) <> 0 AND o.DocStatus IN ('DR','IP','CO') AND t.AD_Client_ID=? AND t.AD_Org_ID=? AND o.S_Resource_ID=? AND t.M_Warehouse_ID= ?";
        this.executeUpdate("INSERT INTO PP_MRP (ad_org_id, created, createdby , dateordered,datepromised, datestart, datestartschedule, description,docstatus, isactive , m_forecastline_id, m_forecast_id,pp_order_id, pp_order_bomline_id,c_order_id, c_orderline_id,m_requisition_id, m_requisitionline_id,m_product_id, m_warehouse_id, pp_mrp_id, planner_id, qty, typemrp, ordertype, updated, updatedby, value, ad_client_id, s_resource_id, c_bpartner_id )" + sql_insert, params);
        this.commitEx();
    }
    
    private void executeUpdate(final String sql, final List<Object> params) throws SQLException {
        Object[] pa = null;
        if (params != null) {
            pa = params.toArray(new Object[params.size()]);
        }
        else {
            pa = new Object[0];
        }
        final int no = DB.executeUpdateEx(sql, pa, this.get_TrxName());
        this.commitEx();
        this.log.fine("#" + no + " -- " + sql);
    }
    
    private void deletePO(final String tableName, final String whereClause, final List<Object> params) throws SQLException {
        final POResultSet<PO> rs = (POResultSet<PO>)new Query(this.getCtx(), tableName, whereClause, this.get_TrxName()).setParameters((List)params).scroll();
        try {
            while (rs.hasNext()) {
                rs.next().deleteEx(true);
                this.commitEx();
            }
        }
        finally {
            rs.close();
        }
        rs.close();
    }
}
