// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.wf.MWFNode;
import java.math.RoundingMode;
import java.math.BigDecimal;
import org.compiere.model.I_AD_WF_Node;
import org.libero.model.MPPCostCollector;
import org.adempiere.model.engines.CostEngineFactory;
import org.compiere.model.MCost;
import org.adempiere.model.engines.CostDimension;
import org.compiere.model.I_M_CostElement;
import org.adempiere.model.engines.CostEngine;
import org.compiere.model.MCostElement;
import org.compiere.util.Env;
import java.util.List;
import org.compiere.model.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eevolution.model.MPPProductPlanning;
import org.compiere.wf.MWorkflow;
import org.compiere.model.MProduct;
import org.libero.model.RoutingServiceFactory;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.libero.model.RoutingService;
import org.compiere.model.MAcctSchema;
import org.compiere.process.SvrProcess;

public class RollupWorkflow extends SvrProcess
{
    private int p_AD_Org_ID;
    private int p_C_AcctSchema_ID;
    private int p_M_CostType_ID;
    private int p_M_Product_ID;
    private int p_M_Product_Category_ID;
    private String p_ConstingMethod;
    private MAcctSchema m_as;
    private RoutingService m_routingService;
    private int p_S_Resource_ID;
    private int p_M_Warehouse_ID;
    private int p_workflow_ID;
    
    public RollupWorkflow() {
        this.p_AD_Org_ID = 0;
        this.p_C_AcctSchema_ID = 0;
        this.p_M_CostType_ID = 0;
        this.p_M_Product_ID = 0;
        this.p_M_Product_Category_ID = 0;
        this.p_ConstingMethod = "S";
        this.m_as = null;
        this.m_routingService = null;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("AD_Org_ID")) {
                    this.p_AD_Org_ID = para.getParameterAsInt();
                }
                else if (name.equals("C_AcctSchema_ID")) {
                    this.p_C_AcctSchema_ID = para.getParameterAsInt();
                    this.m_as = MAcctSchema.get(this.getCtx(), this.p_C_AcctSchema_ID);
                }
                else if (name.equals("M_CostType_ID")) {
                    this.p_M_CostType_ID = para.getParameterAsInt();
                }
                else if (name.equals("CostingMethod")) {
                    this.p_ConstingMethod = (String)para.getParameter();
                }
                else if (name.equals("M_Product_ID")) {
                    this.p_M_Product_ID = para.getParameterAsInt();
                }
                else if (name.equals("M_Product_Category_ID")) {
                    this.p_M_Product_Category_ID = para.getParameterAsInt();
                }
                else if (name.equals("S_Resource_ID")) {
                    this.p_S_Resource_ID = ((para.getParameter() == null) ? null : Integer.valueOf(para.getParameterAsInt()));
                }
                else if (name.equals("M_Warehouse_ID")) {
                    this.p_M_Warehouse_ID = ((para.getParameter() == null) ? null : Integer.valueOf(para.getParameterAsInt()));
                }
                else if (name.equals("AD_Workflow_ID")) {
                    this.p_workflow_ID = para.getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        this.m_routingService = RoutingServiceFactory.get().getRoutingService(this.getAD_Client_ID());
        for (final MProduct product : this.getProducts()) {
            this.log.info("Product: " + product);
            int AD_Workflow_ID = 0;
            MPPProductPlanning pp = null;
            if (AD_Workflow_ID <= 0) {
                AD_Workflow_ID = MWorkflow.getWorkflowSearchKey(product);
            }
            if (AD_Workflow_ID <= 0) {
                pp = MPPProductPlanning.find(this.getCtx(), this.p_AD_Org_ID, this.p_M_Warehouse_ID, this.p_S_Resource_ID, product.get_ID(), this.get_TrxName());
                if (pp != null) {
                    AD_Workflow_ID = pp.getAD_Workflow_ID();
                }
                else {
                    this.createNotice(product, "@NotFound@ @PP_Product_Planning_ID@");
                }
            }
            if (AD_Workflow_ID <= 0) {
                this.createNotice(product, "@NotFound@ @AD_Workflow_ID@");
            }
            else {
                final MWorkflow workflow = new MWorkflow(this.getCtx(), AD_Workflow_ID, this.get_TrxName());
                this.rollup(product, workflow);
                if (pp == null) {
                    continue;
                }
                pp.setYield(workflow.getYield());
                pp.saveEx();
            }
        }
        return "@OK@";
    }
    
    private Collection<MProduct> getProducts() {
        final List<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer("AD_Client_ID=?");
        params.add(this.getAD_Client_ID());
        whereClause.append(" AND (").append("ProductType").append("=?");
        params.add("I");
        whereClause.append(" OR ").append("ProductType").append("=?");
        params.add("R");
        whereClause.append(") AND ").append("IsBOM").append("=?");
        params.add(true);
        if (this.p_M_Product_ID > 0) {
            whereClause.append(" AND ").append("M_Product_ID").append("=?");
            params.add(this.p_M_Product_ID);
        }
        else if (this.p_M_Product_Category_ID > 0) {
            whereClause.append(" AND ").append("M_Product_Category_ID").append("=?");
            params.add(this.p_M_Product_Category_ID);
        }
        final Collection<MProduct> products = new Query(this.getCtx(), "M_Product", whereClause.toString(), this.get_TrxName()).setOrderBy("LowLevel").setParameters((List)params).list();
        return products;
    }
    
    public void rollup(final MProduct product, final MWorkflow workflow) {
        this.log.info("Workflow: " + workflow);
        workflow.setCost(Env.ZERO);
        double Yield = 1.0;
        int QueuingTime = 0;
        int SetupTime = 0;
        int Duration = 0;
        int WaitingTime = 0;
        int jmltenagakerja = 0;
        int MovingTime = 0;
        int WorkingTime = 0;
        final MWFNode[] nodes = workflow.getNodes(false, this.getAD_Client_ID());
        MWFNode[] array;
        for (int length = (array = nodes).length, i = 0; i < length; ++i) {
            final MWFNode node = array[i];
            node.setCost(Env.ZERO);
            if (node.getYield() != 0) {
                Yield *= node.getYield() / 100.0;
            }
            final long nodeDuration = node.getDuration();
            QueuingTime += node.getQueuingTime();
            SetupTime += node.getSetupTime();
            Duration += (int)nodeDuration;
            WaitingTime += node.getWaitingTime();
            MovingTime += node.getMovingTime();
            WorkingTime += node.getWorkingTime();
            jmltenagakerja += node.get_ValueAsInt("jmltenagakerja");
        }
        workflow.setCost(Env.ZERO);
        workflow.setYield((int)(Yield * 100.0));
        workflow.setQueuingTime(QueuingTime);
        workflow.setSetupTime(SetupTime);
        workflow.setDuration(Duration);
        workflow.setWaitingTime(WaitingTime);
        workflow.setMovingTime(MovingTime);
        workflow.setWorkingTime(WorkingTime);
        workflow.set_CustomColumn("jmltenagakerja", (Object)jmltenagakerja);
        for (final MCostElement element : MCostElement.getByCostingMethod(this.getCtx(), this.p_ConstingMethod)) {
            if (!CostEngine.isActivityControlElement((I_M_CostElement)element)) {
                continue;
            }
            final CostDimension d = new CostDimension(product, this.m_as, this.p_M_CostType_ID, this.p_AD_Org_ID, 0, element.get_ID());
            final List<MCost> costs = d.toQuery(MCost.class, this.get_TrxName()).list();
            for (final MCost cost : costs) {
                final int precision = MAcctSchema.get(Env.getCtx(), cost.getC_AcctSchema_ID()).getCostingPrecision();
                BigDecimal segmentCost = Env.ZERO;
                MWFNode[] array2;
                for (int length2 = (array2 = nodes).length, j = 0; j < length2; ++j) {
                    final MWFNode node2 = array2[j];
                    final CostEngine costEngine = CostEngineFactory.getCostEngine(node2.getAD_Client_ID());
                    final BigDecimal rate = costEngine.getResourceActualCostRate(null, node2.getS_Resource_ID(), d, this.get_TrxName());
                    final BigDecimal baseValue = this.m_routingService.getResourceBaseValue(node2.getS_Resource_ID(), (I_AD_WF_Node)node2);
                    BigDecimal nodeCost = baseValue.multiply(rate).multiply(new BigDecimal(node2.get_ValueAsInt("jmltenagakerja")));
                    if (nodeCost.scale() > precision) {
                        nodeCost = nodeCost.setScale(precision, RoundingMode.HALF_UP);
                    }
                    segmentCost = segmentCost.add(nodeCost);
                    this.log.info("Element : " + element + ", Node=" + node2 + ", BaseValue=" + baseValue + ", rate=" + rate + ", nodeCost=" + nodeCost + " => Cost=" + segmentCost);
                    node2.setCost(node2.getCost().add(nodeCost));
                }
                cost.setCurrentCostPrice(segmentCost);
                cost.saveEx();
                workflow.setCost(workflow.getCost().add(segmentCost));
            }
        }
        MWFNode[] array3;
        for (int length3 = (array3 = nodes).length, k = 0; k < length3; ++k) {
            final MWFNode node = array3[k];
            node.saveEx();
        }
        workflow.saveEx();
        this.log.info("Product: " + product.getName() + " WFCost: " + workflow.getCost());
    }
    
    private void createNotice(final MProduct product, final String msg) {
        final String productValue = (product != null) ? product.getValue() : "-";
        this.addLog("WARNING: Product " + productValue + ": " + msg);
    }
}
