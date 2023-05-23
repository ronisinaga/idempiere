// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.compiere.model.MTable;
import org.libero.model.MPPMRP;
import java.util.Properties;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.util.Msg;
import org.compiere.minigrid.IDColumn;
import org.compiere.util.Language;
import org.compiere.util.Env;
import org.compiere.minigrid.ColumnInfo;
import org.compiere.model.MQuery;
import org.compiere.util.CLogger;

public abstract class MRPDetailed
{
    public static CLogger log;
    public int m_WindowNo;
    public int AD_Client_ID;
    public int p_WindowNo;
    public String p_keyColumn;
    public boolean p_multiSelection;
    public String p_whereClause;
    public int m_keyColumnIndex;
    public boolean m_cancel;
    public String m_sqlMain;
    public String m_sqlAdd;
    public final int INFO_WIDTH = 800;
    public int AD_Window_ID;
    public MQuery query;
    private boolean isBaseLanguage;
    public final ColumnInfo[] m_layout;
    
    static {
        MRPDetailed.log = CLogger.getCLogger((Class)MRPDetailed.class);
    }
    
    public MRPDetailed() {
        this.m_WindowNo = 0;
        this.AD_Client_ID = Env.getAD_Client_ID(this.getCtx());
        this.p_multiSelection = true;
        this.p_whereClause = "";
        this.m_keyColumnIndex = -1;
        this.m_cancel = false;
        Env.getLanguage(Env.getCtx());
        this.isBaseLanguage = (Language.getBaseAD_Language().compareTo(Env.getLoginLanguage(Env.getCtx()).getAD_Language()) == 0);
        this.m_layout = new ColumnInfo[] { new ColumnInfo(" ", String.valueOf(this.getTableName()) + ".PP_MRP_ID", (Class)IDColumn.class), new ColumnInfo(Msg.translate(Env.getCtx(), "Value"), "(SELECT Value FROM M_Product p WHERE p.M_Product_ID=" + this.getTableName() + ".M_Product_ID) AS ProductValue", (Class)String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "Name"), "(SELECT Name FROM M_Product p WHERE p.M_Product_ID=" + this.getTableName() + ".M_Product_ID)", (Class)String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "S_Resource_ID"), "(SELECT Name FROM S_Resource sr WHERE sr.S_Resource_ID=" + this.getTableName() + ".S_Resource_ID)", (Class)String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "M_Warehouse_ID"), "(SELECT Name FROM M_Warehouse wh WHERE wh.M_Warehouse_ID=" + this.getTableName() + ".M_Warehouse_ID)", (Class)String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "DatePromised"), this.getTableName() + ".DatePromised", (Class)Timestamp.class), new ColumnInfo(Msg.translate(Env.getCtx(), "QtyGrossReq"), "(CASE WHEN " + this.getTableName() + ".TypeMRP='D' THEN " + this.getTableName() + ".Qty ELSE NULL END)", (Class)BigDecimal.class), new ColumnInfo(Msg.translate(Env.getCtx(), "QtyScheduledReceipts"), "(CASE WHEN " + this.getTableName() + ".TypeMRP='S' AND " + this.getTableName() + ".DocStatus  IN ('IP','CO') THEN " + this.getTableName() + ".Qty ELSE NULL END)", (Class)BigDecimal.class), new ColumnInfo(Msg.translate(Env.getCtx(), "PlannedQty"), "(CASE WHEN " + this.getTableName() + ".TypeMRP='S' AND " + this.getTableName() + ".DocStatus ='DR' THEN " + this.getTableName() + ".Qty ELSE NULL END)", (Class)BigDecimal.class), new ColumnInfo(Msg.translate(Env.getCtx(), "QtyOnHandProjected"), "bomQtyOnHand(" + this.getTableName() + ".M_Product_ID , " + this.getTableName() + ".M_Warehouse_ID, 0)", (Class)BigDecimal.class), this.isBaseLanguage ? new ColumnInfo(Msg.translate(Env.getCtx(), "TypeMRP"), "(SELECT Name FROM  AD_Ref_List WHERE AD_Reference_ID=53230 AND Value = " + this.getTableName() + ".TypeMRP)", (Class)String.class) : new ColumnInfo(Msg.translate(Env.getCtx(), "TypeMRP"), "(SELECT rlt.Name FROM  AD_Ref_List rl INNER JOIN AD_Ref_List_Trl  rlt ON (rl.AD_Ref_List_ID=rlt.AD_Ref_List_ID)  WHERE rl.AD_Reference_ID=53230 AND rlt.AD_Language = '" + Env.getLoginLanguage(Env.getCtx()).getAD_Language() + "' AND Value = " + this.getTableName() + ".TypeMRP)", (Class)String.class), this.isBaseLanguage ? new ColumnInfo(Msg.translate(Env.getCtx(), "OrderType"), "(SELECT Name FROM  AD_Ref_List WHERE AD_Reference_ID=53229 AND Value = " + this.getTableName() + ".OrderType)", (Class)String.class) : new ColumnInfo(Msg.translate(Env.getCtx(), "OrderType"), "(SELECT rlt.Name FROM  AD_Ref_List rl INNER JOIN AD_Ref_List_Trl  rlt ON (rl.AD_Ref_List_ID=rlt.AD_Ref_List_ID)  WHERE rl.AD_Reference_ID=53229 AND rlt.AD_Language = '" + Env.getLoginLanguage(Env.getCtx()).getAD_Language() + "' AND Value = " + this.getTableName() + ".OrderType)", (Class)String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "DocumentNo"), "documentNo(" + this.getTableName() + ".PP_MRP_ID)", (Class)String.class), this.isBaseLanguage ? new ColumnInfo(Msg.translate(Env.getCtx(), "DocStatus"), "(SELECT Name FROM  AD_Ref_List WHERE AD_Reference_ID=131 AND Value = " + this.getTableName() + ".DocStatus)", (Class)String.class) : new ColumnInfo(Msg.translate(Env.getCtx(), "DocStatus"), "(SELECT rlt.Name FROM  AD_Ref_List rl INNER JOIN AD_Ref_List_Trl  rlt ON (rl.AD_Ref_List_ID=rlt.AD_Ref_List_ID)  WHERE rl.AD_Reference_ID=131 AND rlt.AD_Language = '" + Env.getLoginLanguage(Env.getCtx()).getAD_Language() + "' AND Value = " + this.getTableName() + ".DocStatus)", (Class)String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "DateStartSchedule"), this.getTableName() + ".DateStartSchedule", (Class)Timestamp.class), new ColumnInfo(Msg.translate(Env.getCtx(), "C_BPartner_ID"), "(SELECT cb.Name FROM C_BPartner cb WHERE cb.C_BPartner_ID=" + this.getTableName() + ".C_BPartner_ID)", (Class)String.class) };
    }
    
    public String getTableName() {
        return "RV_PP_MRP";
    }
    
    public void customize() {
    }
    
    public void doReset() {
    }
    
    public int getAD_Client_ID() {
        return Env.getAD_Client_ID(this.getCtx());
    }
    
    public Properties getCtx() {
        return Env.getCtx();
    }
    
    abstract Integer getSelectedRowKey();
    
    public String getWhereClause(final String staticWhere) {
        final StringBuffer where = new StringBuffer(this.getTableName() + ".DocStatus IN ('DR','IP','CO')  AND " + this.getTableName() + ".IsActive='Y' and " + this.getTableName() + ".Qty!=0 ");
        if (!staticWhere.equals("")) {
            where.append(staticWhere);
        }
        return where.toString();
    }
    
    public boolean hasCustomize() {
        return false;
    }
    
    public boolean hasHistory() {
        return false;
    }
    
    public boolean hasReset() {
        return false;
    }
    
    public boolean hasZoom() {
        return true;
    }
    
    public void showHistory() {
    }
    
    public void zoom() {
        MRPDetailed.log.info("InfoMRPDetailed.zoom");
        final Integer PP_MPR_ID = this.getSelectedRowKey();
        this.AD_Window_ID = 0;
        if (PP_MPR_ID == null) {
            return;
        }
        this.query = null;
        final MPPMRP mrp = new MPPMRP(this.getCtx(), PP_MPR_ID, null);
        final String ordertype = mrp.getOrderType();
        if ("POO".equals(ordertype)) {
            this.AD_Window_ID = MTable.get(this.getCtx(), 259).getPO_Window_ID();
            (this.query = new MQuery("C_Order")).addRestriction("C_Order_ID", "=", mrp.getC_Order_ID());
        }
        else if ("SOO".equals(ordertype)) {
            this.AD_Window_ID = MTable.get(this.getCtx(), 259).getAD_Window_ID();
            (this.query = new MQuery("C_Order")).addRestriction("C_Order_ID", "=", mrp.getC_Order_ID());
        }
        else if ("MOP".equals(ordertype)) {
            this.AD_Window_ID = MTable.get(this.getCtx(), 53027).getAD_Window_ID();
            (this.query = new MQuery("PP_Order")).addRestriction("PP_Order_ID", "=", mrp.getPP_Order_ID());
        }
        else if ("POR".equals(ordertype)) {
            this.AD_Window_ID = MTable.get(this.getCtx(), 702).getAD_Window_ID();
            (this.query = new MQuery("M_Requisition")).addRestriction("M_Requisition_ID", "=", mrp.getM_Requisition_ID());
        }
        else if ("FCT".equals(ordertype)) {
            this.AD_Window_ID = MTable.get(this.getCtx(), 720).getAD_Window_ID();
            (this.query = new MQuery("M_Forecast")).addRestriction("M_Forecast_ID", "=", mrp.getM_Forecast_ID());
        }
        if ("DOO".equals(ordertype)) {
            this.AD_Window_ID = MTable.get(this.getCtx(), 53037).getAD_Window_ID();
            (this.query = new MQuery("DD_Order")).addRestriction("DD_Order_ID", "=", mrp.getDD_Order_ID());
        }
        if (this.AD_Window_ID == 0) {
            return;
        }
        MRPDetailed.log.info("AD_WindowNo " + this.AD_Window_ID);
        this.zoom(this.AD_Window_ID, this.query);
    }
    
    abstract void zoom(final int p0, final MQuery p1);
}
