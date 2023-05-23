// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.util.Iterator;
import java.util.List;
import org.libero.model.MPPOrder;
import org.libero.model.MPPCostCollector;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.math.BigDecimal;
import org.compiere.process.SvrProcess;

public class ProsesVoidManufacturing extends SvrProcess
{
    private int p_PP_Cost_Collector_ID;
    
    public ProsesVoidManufacturing() {
        this.p_PP_Cost_Collector_ID = 0;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("PP_Cost_Collector_ID")) {
                    this.p_PP_Cost_Collector_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        if (this.p_PP_Cost_Collector_ID > 0) {
            final List<MPPCostCollector> list = new Query(this.getCtx(), "PP_Cost_Collector", " docstatus = 'CO' and pp_order_id = " + this.getRecord_ID() + " and description = '" + DB.getSQLValueString((String)null, "select description from pp_cost_collector where pp_cost_collector_id = ?", this.p_PP_Cost_Collector_ID) + "'", this.get_TrxName()).list();
            for (final MPPCostCollector mppCostCollector : list) {
                mppCostCollector.voidIt();
                mppCostCollector.setDocStatus("VO");
                mppCostCollector.setDocAction("VO");
                mppCostCollector.saveEx(this.get_TrxName());
            }
        }
        else {
            final MPPOrder mppOrder = new MPPOrder(this.getCtx(), this.getRecord_ID(), this.get_TrxName());
            final List<MPPCostCollector> list2 = new Query(this.getCtx(), "PP_Cost_Collector", " PP_Order_ID = " + mppOrder.getPP_Order_ID(), this.get_TrxName()).list();
            for (final MPPCostCollector mppCostCollector2 : list2) {
                mppCostCollector2.voidIt();
                mppCostCollector2.setDocStatus("VO");
                mppCostCollector2.setDocAction("VO");
                mppCostCollector2.saveEx(this.get_TrxName());
            }
        }
        return null;
    }
}
