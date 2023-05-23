// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.eevolution.model.MPPProductPlanning;
import org.eevolution.model.MPPProductBOMLine;
import java.util.Iterator;
import org.compiere.model.MOrderLine;
import org.eevolution.model.MPPProductBOM;
import org.compiere.model.MProduct;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.model.MOrder;
import org.compiere.process.SvrProcess;

public class CalculateProductPlanningFromSalesOrder extends SvrProcess
{
    private int p_AD_Workflow_ID;
    private int count_created;
    private int count_updated;
    private int count_error;
    MOrder order;
    
    public CalculateProductPlanningFromSalesOrder() {
        this.p_AD_Workflow_ID = 0;
        this.count_created = 0;
        this.count_updated = 0;
        this.count_error = 0;
        this.order = null;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("AD_Workflow_ID")) {
                    this.p_AD_Workflow_ID = para.getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
        this.order = new MOrder(this.getCtx(), this.getRecord_ID(), this.get_TrxName());
    }
    
    protected String doIt() throws Exception {
        MOrderLine[] lines;
        for (int length = (lines = this.order.getLines()).length, i = 0; i < length; ++i) {
            final MOrderLine orderLine = lines[i];
            final MProduct M_Product = MProduct.get(this.getCtx(), orderLine.getM_Product_ID());
            for (final MPPProductBOM bom : MPPProductBOM.getProductBOMs(M_Product)) {
                this.createPlanning(M_Product.getM_Product_ID(), this.order, bom);
                this.parent(bom);
            }
        }
        return "@Created@ #" + this.count_created + " @Updated@ #" + this.count_updated + " @Error@ #" + this.count_error;
    }
    
    public void parent(final MPPProductBOM bom) {
        MPPProductBOMLine[] lines;
        for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine bomline = lines[i];
            final MProduct component = MProduct.get(this.getCtx(), bomline.getM_Product_ID());
            this.createPlanning(component.getM_Product_ID(), this.order, bom);
            this.component(component);
        }
    }
    
    public void component(final MProduct product) {
        for (final MPPProductBOM bom : MPPProductBOM.getProductBOMs(product)) {
            this.parent(bom);
        }
    }
    
    private void createPlanning(final int M_Product_ID, final MOrder order, final MPPProductBOM bom) {
        MPPProductPlanning pp = MPPProductPlanning.get(this.getCtx(), this.getAD_Client_ID(), order.getAD_Org_ID(), order.getM_Warehouse_ID(), 1000000, M_Product_ID, this.get_TrxName());
        final boolean isNew = pp == null;
        if (pp == null) {
            pp = new MPPProductPlanning(this.getCtx(), 0, this.get_TrxName());
            pp.setAD_Org_ID(order.getAD_Org_ID());
            pp.setM_Warehouse_ID(order.getM_Warehouse_ID());
            pp.setS_Resource_ID(1000000);
            pp.setM_Product_ID(M_Product_ID);
        }
        pp.setDD_NetworkDistribution_ID(0);
        pp.setAD_Workflow_ID(this.p_AD_Workflow_ID);
        pp.setPP_Product_BOM_ID(bom.getPP_Product_BOM_ID());
        pp.setIsCreatePlan(true);
        pp.setIsMPS(false);
        pp.setIsRequiredMRP(true);
        pp.setIsRequiredDRP(true);
        pp.setPlanner_ID(this.getAD_User_ID());
        pp.setOrder_Policy("LFL");
        pp.setIsPhantom(false);
        if (!pp.save()) {
            ++this.count_error;
        }
        if (isNew) {
            ++this.count_created;
        }
        else {
            ++this.count_updated;
        }
    }
}
