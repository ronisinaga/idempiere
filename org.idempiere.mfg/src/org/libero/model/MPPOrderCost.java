// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.model.PO;
import org.compiere.model.MCost;
import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.X_PP_Order_Cost;

public class MPPOrderCost extends X_PP_Order_Cost
{
    private static final long serialVersionUID = 1L;
    
    public MPPOrderCost(final Properties ctx, final int PP_Order_Cost_ID, final String trxName) {
        super(ctx, PP_Order_Cost_ID, trxName);
    }
    
    public MPPOrderCost(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    public MPPOrderCost(final MCost cost, final int PP_Order_ID, final String trxName) {
        this(cost.getCtx(), 0, trxName);
        this.setClientOrg((PO)cost);
        this.setPP_Order_ID(PP_Order_ID);
        this.setC_AcctSchema_ID(cost.getC_AcctSchema_ID());
        this.setM_CostType_ID(cost.getM_CostType_ID());
        this.setCumulatedAmt(cost.getCumulatedAmt());
        this.setCumulatedQty(cost.getCumulatedQty());
        this.setCurrentCostPrice(cost.getCurrentCostPrice());
        this.setCurrentCostPriceLL(cost.getCurrentCostPriceLL());
        this.setM_Product_ID(cost.getM_Product_ID());
        this.setM_AttributeSetInstance_ID(cost.getM_AttributeSetInstance_ID());
        this.setM_CostElement_ID(cost.getM_CostElement_ID());
    }
}
