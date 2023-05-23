// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.util.Env;
import org.compiere.model.PO;
import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.X_PP_Order_Node_Product;

public class MPPOrderNodeProduct extends X_PP_Order_Node_Product
{
    private static final long serialVersionUID = 1L;
    
    public MPPOrderNodeProduct(final Properties ctx, final int PP_WF_Order_Product_ID, final String trxName) {
        super(ctx, PP_WF_Order_Product_ID, trxName);
    }
    
    public MPPOrderNodeProduct(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    public MPPOrderNodeProduct(final MPPWFNodeProduct np, final MPPOrderNode PP_Order_Node) {
        this(PP_Order_Node.getCtx(), 0, PP_Order_Node.get_TrxName());
        this.setClientOrg((PO)PP_Order_Node);
        this.setSeqNo(np.getSeqNo());
        this.setIsActive(np.isActive());
        this.setM_Product_ID(np.getM_Product_ID());
        this.setQty(np.getQty());
        this.setIsSubcontracting(np.isSubcontracting());
        this.setPP_Order_ID(PP_Order_Node.getPP_Order_ID());
        this.setPP_Order_Workflow_ID(PP_Order_Node.getPP_Order_Workflow_ID());
        this.setPP_Order_Node_ID(PP_Order_Node.get_ID());
    }
    
    protected boolean beforeSave(final boolean newRecord) {
        if (this.getQty().signum() == 0 && this.isSubcontracting()) {
            this.setQty(Env.ONE);
        }
        return true;
    }
}
