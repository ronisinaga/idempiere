// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.callouts;

import org.eevolution.model.MPPProductBOM;
import org.compiere.wf.MWorkflow;
import org.eevolution.model.MPPProductPlanning;
import org.eevolution.model.I_PP_Product_BOM;
import org.eevolution.model.I_PP_Product_Planning;
import org.compiere.model.MProduct;
import org.libero.model.MPPOrder;
import org.adempiere.model.GridTabWrapper;
import org.eevolution.model.I_PP_Order;
import org.compiere.model.MUOMConversion;
import java.math.BigDecimal;
import org.compiere.util.Env;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import java.util.Properties;
import org.compiere.model.CalloutEngine;

public class CalloutOrder extends CalloutEngine
{
    private boolean steps;
    
    public CalloutOrder() {
        this.steps = false;
    }
    
    public String qty(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        if (value == null) {
            return "";
        }
        final int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");
        if (this.steps) {
            this.log.warning("qty - init - M_Product_ID=" + M_Product_ID + " - ");
        }
        BigDecimal QtyOrdered = Env.ZERO;
        BigDecimal QtyEntered = Env.ZERO;
        if (M_Product_ID == 0) {
            QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
            mTab.setValue("QtyOrdered", (Object)QtyEntered);
        }
        else if (mField.getColumnName().equals("C_UOM_ID")) {
            final int C_UOM_To_ID = (int)value;
            QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
            QtyOrdered = MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);
            if (QtyOrdered == null) {
                QtyOrdered = QtyEntered;
            }
            final boolean conversion = QtyEntered.compareTo(QtyOrdered) != 0;
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
            mTab.setValue("QtyOrdered", (Object)QtyOrdered);
        }
        else if (mField.getColumnName().equals("QtyEntered")) {
            final int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");
            QtyEntered = (BigDecimal)value;
            QtyOrdered = MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);
            if (QtyOrdered == null) {
                QtyOrdered = QtyEntered;
            }
            final boolean conversion = QtyEntered.compareTo(QtyOrdered) != 0;
            this.log.fine("qty - UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " QtyOrdered=" + QtyOrdered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
            mTab.setValue("QtyOrdered", (Object)QtyOrdered);
        }
        else if (mField.getColumnName().equals("QtyOrdered")) {
            final int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");
            QtyOrdered = (BigDecimal)value;
            QtyEntered = MUOMConversion.convertProductTo(ctx, M_Product_ID, C_UOM_To_ID, QtyOrdered);
            if (QtyEntered == null) {
                QtyEntered = QtyOrdered;
            }
            final boolean conversion = QtyOrdered.compareTo(QtyEntered) != 0;
            this.log.fine("qty - UOM=" + C_UOM_To_ID + ", QtyOrdered=" + QtyOrdered + " -> " + conversion + " QtyEntered=" + QtyEntered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
            mTab.setValue("QtyEntered", (Object)QtyEntered);
        }
        return this.qtyBatch(ctx, WindowNo, mTab, mField, value);
    }
    
    public String qtyBatch(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final I_PP_Order order = (I_PP_Order)GridTabWrapper.create(mTab, (Class)I_PP_Order.class);
        MPPOrder.updateQtyBatchs(ctx, order, true);
        return "";
    }
    
    public String product(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        if (this.isCalloutActive()) {
            return "";
        }
        final I_PP_Order order = (I_PP_Order)GridTabWrapper.create(mTab, (Class)I_PP_Order.class);
        final MProduct product = MProduct.get(ctx, order.getM_Product_ID());
        if (product == null) {
            return "";
        }
        order.setC_UOM_ID(product.getC_UOM_ID());
        final I_PP_Product_Planning pp = getPP_Product_Planning(ctx, order);
        order.setAD_Workflow_ID(pp.getAD_Workflow_ID());
        order.setPP_Product_BOM_ID(pp.getPP_Product_BOM_ID());
        if (pp.getPP_Product_BOM_ID() > 0) {
            final I_PP_Product_BOM bom = pp.getPP_Product_BOM();
            order.setC_UOM_ID(bom.getC_UOM_ID());
        }
        MPPOrder.updateQtyBatchs(ctx, order, true);
        return "";
    }
    
    protected static I_PP_Product_Planning getPP_Product_Planning(final Properties ctx, final I_PP_Order order) {
        I_PP_Product_Planning pp = (I_PP_Product_Planning)MPPProductPlanning.find(ctx, order.getAD_Org_ID(), order.getM_Warehouse_ID(), order.getS_Resource_ID(), order.getM_Product_ID(), (String)null);
        if (pp == null) {
            pp = (I_PP_Product_Planning)new MPPProductPlanning(ctx, 0, (String)null);
            pp.setAD_Org_ID(order.getAD_Org_ID());
            pp.setM_Warehouse_ID(order.getM_Warehouse_ID());
            pp.setS_Resource_ID(order.getS_Resource_ID());
            pp.setM_Product_ID(order.getM_Product_ID());
        }
        final MProduct product = MProduct.get(ctx, pp.getM_Product_ID());
        if (pp.getAD_Workflow_ID() <= 0) {
            pp.setAD_Workflow_ID(MWorkflow.getWorkflowSearchKey(product));
        }
        if (pp.getPP_Product_BOM_ID() <= 0) {
            final I_PP_Product_BOM bom = (I_PP_Product_BOM)MPPProductBOM.getDefault(product, (String)null);
            if (bom != null) {
                pp.setPP_Product_BOM_ID(bom.getPP_Product_BOM_ID());
            }
        }
        return pp;
    }
}
