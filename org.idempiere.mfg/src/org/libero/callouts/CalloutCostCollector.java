// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.callouts;

import java.math.BigDecimal;
import org.libero.model.RoutingService;
import org.libero.model.RoutingServiceFactory;
import org.libero.model.MPPCostCollector;
import org.libero.model.MPPOrder;
import org.adempiere.model.GridTabWrapper;
import org.libero.tables.I_PP_Cost_Collector;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import java.util.Properties;
import org.libero.model.MPPOrderNode;
import org.compiere.model.CalloutEngine;

public class CalloutCostCollector extends CalloutEngine
{
    private MPPOrderNode m_node;
    
    public CalloutCostCollector() {
        this.m_node = null;
    }
    
    public String order(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final Integer PP_Order_ID = (Integer)value;
        if (PP_Order_ID == null || PP_Order_ID <= 0) {
            return "";
        }
        final I_PP_Cost_Collector cc = (I_PP_Cost_Collector)GridTabWrapper.create(mTab, (Class)I_PP_Cost_Collector.class);
        final MPPOrder pp_order = new MPPOrder(ctx, PP_Order_ID, null);
        MPPCostCollector.setPP_Order(cc, pp_order);
        return "";
    }
    
    public String node(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final Integer PP_Order_Node_ID = (Integer)value;
        if (PP_Order_Node_ID == null || PP_Order_Node_ID <= 0) {
            return "";
        }
        final I_PP_Cost_Collector cc = (I_PP_Cost_Collector)GridTabWrapper.create(mTab, (Class)I_PP_Cost_Collector.class);
        final MPPOrderNode node = this.getPP_Order_Node(ctx, PP_Order_Node_ID);
        cc.setS_Resource_ID(node.getS_Resource_ID());
        cc.setIsSubcontracting(node.isSubcontracting());
        cc.setMovementQty(node.getQtyToDeliver());
        this.duration(ctx, WindowNo, mTab, mField, value);
        return "";
    }
    
    public String duration(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final I_PP_Cost_Collector cc = (I_PP_Cost_Collector)GridTabWrapper.create(mTab, (Class)I_PP_Cost_Collector.class);
        if (cc.getPP_Order_Node_ID() <= 0) {
            return "";
        }
        final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(ctx);
        final BigDecimal durationReal = routingService.estimateWorkingTime(cc);
        cc.setDurationReal(durationReal);
        return "";
    }
    
    private MPPOrderNode getPP_Order_Node(final Properties ctx, final int PP_Order_Node_ID) {
        if (this.m_node != null && this.m_node.get_ID() == PP_Order_Node_ID) {
            return this.m_node;
        }
        return this.m_node = new MPPOrderNode(ctx, PP_Order_Node_ID, null);
    }
}
