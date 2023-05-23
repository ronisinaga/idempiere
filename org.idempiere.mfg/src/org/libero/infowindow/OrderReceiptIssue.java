// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.infowindow;

import org.compiere.model.MStorageOnHand;
import org.libero.model.MPPOrder;
import java.util.Iterator;
import java.util.List;
import org.libero.model.MPPOrderBOMLine;
import org.libero.model.MPPMRP;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.process.ProcessInfoParameter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class OrderReceiptIssue extends SvrProcess
{
    private String p_DeliveryRule;
    private boolean p_BackFlushGroup;
    private Timestamp p_MovementDate;
    private boolean firsttime;
    private int PP_Order_ID;
    private Timestamp minGuaranteeDate;
    private Timestamp movementDate;
    private BigDecimal qtyToDeliver;
    private BigDecimal qtyScrapComponent;
    
    public OrderReceiptIssue() {
        this.firsttime = true;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("DeliveryRule")) {
                    this.p_DeliveryRule = para[i].getParameterAsString();
                }
                else if (name.equals("BackFlushGroup")) {
                    this.p_BackFlushGroup = para[i].getParameterAsBoolean();
                }
                else if (name.equals("MovementDate")) {
                    this.p_MovementDate = para[i].getParameterAsTimestamp();
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        final String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? AND T_Selection.T_Selection_ID=PP_MRP.PP_MRP_ID)";
        final List<MPPMRP> mrpset = new Query(Env.getCtx(), "PP_MRP", whereClause, this.get_TrxName()).setParameters(new Object[] { this.getAD_PInstance_ID() }).list();
        for (final MPPMRP mrp : mrpset) {
            if (this.firsttime) {
                this.PP_Order_ID = mrp.getPP_Order_ID();
                this.firsttime = false;
            }
            final MPPOrderBOMLine bomline = (MPPOrderBOMLine)new Query(Env.getCtx(), "PP_Order_BOMLine", "PP_Order_ID=?", this.get_TrxName()).setParameters(new Object[] { mrp.getPP_Order_ID() }).first();
            if (this.p_DeliveryRule.equals("BackFlush") || this.p_DeliveryRule.equals("OnlyIssue")) {
                this.createIssue(bomline);
            }
            if (this.p_DeliveryRule.equals("BackFlush") || this.p_DeliveryRule.equals("OnlyReceipt")) {
                this.createReceipt(bomline);
            }
        }
        return null;
    }
    
    private void createReceipt(final MPPOrderBOMLine bomline) {
        new MPPOrder(Env.getCtx(), bomline.getPP_Order_ID(), bomline.get_TrxName());
    }
    
    private void createIssue(final MPPOrderBOMLine bomline) {
        final MPPOrder mo = new MPPOrder(Env.getCtx(), bomline.getPP_Order_ID(), bomline.get_TrxName());
        final int M_Product_ID = bomline.getM_Product_ID();
        final int PP_Order_BOMLine_ID = bomline.getPP_Order_BOMLine_ID();
        final int M_AttributeSetInstance_ID = bomline.getM_AttributeSetInstance_ID();
        final MStorageOnHand[] storages = MPPOrder.getStorages(Env.getCtx(), M_Product_ID, mo.getM_Warehouse_ID(), M_AttributeSetInstance_ID, this.minGuaranteeDate, bomline.get_TrxName());
        MPPOrder.createIssue(mo, PP_Order_BOMLine_ID, this.movementDate, this.qtyToDeliver, this.qtyScrapComponent, Env.ZERO, storages, false);
    }
}
