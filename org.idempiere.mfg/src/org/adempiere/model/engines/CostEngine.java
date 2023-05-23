// 
// Decompiled by Procyon v0.5.36
// 

package org.adempiere.model.engines;

import org.libero.tables.I_PP_Order_BOMLine;
import org.compiere.model.I_AD_WF_Node;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.wf.MWorkflow;
import org.libero.model.RoutingService;
import org.libero.tables.I_PP_Cost_Collector;
import org.libero.model.RoutingServiceFactory;
import java.util.ArrayList;
import java.util.Properties;
import org.compiere.model.I_M_CostElement;
import java.util.Iterator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.MCostDetail;
import org.compiere.model.MTransaction;
import java.util.Collection;
import java.math.RoundingMode;
import org.libero.model.MPPOrderCost;
import org.compiere.util.DB;
import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MProduct;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.libero.model.MPPCostCollector;
import org.compiere.util.CLogger;

public class CostEngine
{
    protected transient CLogger log;
    private CostDimension d;
    
    public CostEngine() {
        this.log = CLogger.getCLogger((Class)this.getClass());
        this.d = null;
    }
    
    public String getCostingMethod() {
        return "S";
    }
    
    public String getCostingMethodFifo() {
        return "F";
    }
    
    public BigDecimal getResourceStandardCostRate(final MPPCostCollector cc, final int S_Resource_ID, final CostDimension d, final String trxName) {
        final MProduct resourceProduct = MProduct.forS_Resource_ID(Env.getCtx(), S_Resource_ID, (String)null);
        return this.getProductStandardCostPrice(cc, resourceProduct, MAcctSchema.get(Env.getCtx(), d.getC_AcctSchema_ID()), MCostElement.get(Env.getCtx(), d.getM_CostElement_ID()));
    }
    
    public BigDecimal getResourceActualCostRate(final MPPCostCollector cc, final int S_Resource_ID, final CostDimension d, final String trxName) {
        if (S_Resource_ID <= 0) {
            return Env.ZERO;
        }
        final MProduct resourceProduct = MProduct.forS_Resource_ID(Env.getCtx(), S_Resource_ID, (String)null);
        return this.getProductActualCostPrice(cc, resourceProduct, MAcctSchema.get(Env.getCtx(), d.getC_AcctSchema_ID()), MCostElement.get(Env.getCtx(), d.getM_CostElement_ID()), trxName);
    }
    
    public BigDecimal getProductActualCostPrice(final MPPCostCollector cc, final MProduct product, final MAcctSchema as, final MCostElement element, final String trxName) {
        int adorg = 0;
        if (cc == null) {
            adorg = product.getAD_Org_ID();
        }
        else {
            adorg = cc.getAD_Org_ID();
        }
        if (element.getCostingMethod().equals("F")) {
            this.d = new CostDimension(product, as, as.getM_CostType_ID(), adorg, cc.getM_AttributeSetInstance_ID(), element.getM_CostElement_ID());
        }
        else {
            this.d = new CostDimension(product, as, as.getM_CostType_ID(), adorg, product.getM_AttributeSetInstance_ID(), element.getM_CostElement_ID());
        }
        final MCost cost = (MCost)this.d.toQuery(MCost.class, trxName).firstOnly();
        if (cost == null) {
            return Env.ZERO;
        }
        final BigDecimal price = cost.getCurrentCostPrice().add(cost.getCurrentCostPriceLL());
        return this.roundCost(price, as.getC_AcctSchema_ID());
    }
    
    public BigDecimal getProductStandardCostPrice(final MPPCostCollector cc, final MProduct product, final MAcctSchema as, final MCostElement element) {
        System.out.println("yoyok" + element.getCostingMethod());
        if (element.getCostingMethod().equals("F")) {
            this.d = new CostDimension(product, as, as.getM_CostType_ID(), cc.getAD_Org_ID(), DB.getSQLValue((String)null, "select m_attributesetinstance_id from PP_Order_Cost where pp_order_id = " + cc.getPP_Order_ID() + " and m_attributesetinstance_id > 0 and m_product_id = " + product.getM_Product_ID()), 1000000);
        }
        else {
            this.d = new CostDimension(product, as, as.getM_CostType_ID(), cc.getAD_Org_ID(), product.getM_AttributeSetInstance_ID(), element.getM_CostElement_ID());
        }
        final MPPOrderCost oc = (MPPOrderCost)this.d.toQuery(MPPOrderCost.class, "PP_Order_ID=?", new Object[] { cc.getPP_Order_ID() }, cc.get_TrxName()).firstOnly();
        if (oc == null) {
            return Env.ZERO;
        }
        final BigDecimal costs = oc.getCurrentCostPrice().add(oc.getCurrentCostPriceLL());
        return this.roundCost(costs, as.getC_AcctSchema_ID());
    }
    
    protected BigDecimal roundCost(final BigDecimal price, final int C_AcctSchema_ID) {
        final int precision = MAcctSchema.get(Env.getCtx(), C_AcctSchema_ID).getCostingPrecision();
        BigDecimal priceRounded = price;
        if (priceRounded.scale() > precision) {
            priceRounded = priceRounded.setScale(precision, RoundingMode.HALF_UP);
        }
        return priceRounded;
    }
    
    public Collection<MCost> getByElement(final MProduct product, final MAcctSchema as, final int M_CostType_ID, final int AD_Org_ID, final int M_AttributeSetInstance_ID, final int M_CostElement_ID) {
        final CostDimension cd = new CostDimension(product, as, M_CostType_ID, AD_Org_ID, M_AttributeSetInstance_ID, M_CostElement_ID);
        return cd.toQuery(MCost.class, product.get_TrxName()).setOnlyActiveRecords(true).list();
    }
    
    private MCostDetail getCostDetail(final IDocumentLine model, final MTransaction mtrx, final MAcctSchema as, final int M_CostElement_ID) {
        final String whereClause = "AD_Client_ID=? AND AD_Org_ID=? AND " + model.get_TableName() + "_ID=?" + " AND " + "M_Product_ID" + "=?" + " AND " + "M_AttributeSetInstance_ID" + "=?" + " AND " + "C_AcctSchema_ID" + "=?" + " AND " + "M_CostElement_ID" + "=?";
        final Object[] params = { mtrx.getAD_Client_ID(), mtrx.getAD_Org_ID(), model.get_ID(), mtrx.getM_Product_ID(), mtrx.getM_AttributeSetInstance_ID(), as.getC_AcctSchema_ID(), M_CostElement_ID };
        return (MCostDetail)new Query(mtrx.getCtx(), "M_CostDetail", whereClause, mtrx.get_TrxName()).setParameters(params).firstOnly();
    }
    
    public void createCostDetail(final IDocumentLine model, final MTransaction mtrx) {
        final MPPCostCollector cc = (model instanceof MPPCostCollector) ? ((MPPCostCollector)model) : null;
        for (final MAcctSchema as : this.getAcctSchema((PO)mtrx)) {
            final MProduct product = MProduct.get(mtrx.getCtx(), mtrx.getM_Product_ID());
            final String costingMethod = product.getCostingMethod(as);
            for (final MCostElement element : this.getCostElements(mtrx.getCtx(), costingMethod)) {
                this.deleteCostDetail(model, as, element.get_ID(), mtrx.getM_AttributeSetInstance_ID());
                final BigDecimal qty = mtrx.getMovementQty();
                final BigDecimal price = this.getProductActualCostPrice(cc, product, as, element, mtrx.get_TrxName());
                final BigDecimal amt = this.roundCost(price.multiply(qty), as.getC_AcctSchema_ID());
                MCostDetail cd = this.getCostDetail(model, mtrx, as, element.get_ID());
                if (cd == null) {
                    cd = new MCostDetail(as, mtrx.getAD_Org_ID(), mtrx.getM_Product_ID(), mtrx.getM_AttributeSetInstance_ID(), element.get_ID(), amt, qty, model.getDescription(), mtrx.get_TrxName());
                    if (model instanceof MPPCostCollector) {
                        cd.setPP_Cost_Collector_ID(model.get_ID());
                    }
                }
                else {
                    cd.setDeltaAmt(amt.subtract(cd.getAmt()));
                    cd.setDeltaQty(mtrx.getMovementQty().subtract(cd.getQty()));
                    if (cd.isDelta()) {
                        cd.setProcessed(false);
                        cd.setAmt(amt);
                        cd.setQty(mtrx.getMovementQty());
                    }
                }
                cd.saveEx();
                this.processCostDetail(cd);
                this.log.config(new StringBuilder().append(cd).toString());
            }
        }
    }
    
    private int deleteCostDetail(final IDocumentLine model, final MAcctSchema as, final int M_CostElement_ID, final int M_AttributeSetInstance_ID) {
        final String sql = "DELETE FROM M_CostDetail WHERE Processed='N' AND COALESCE(DeltaAmt,0)=0 AND COALESCE(DeltaQty,0)=0 AND " + model.get_TableName() + "_ID=?" + " AND " + "C_AcctSchema_ID" + "=?" + " AND " + "M_AttributeSetInstance_ID" + "=?" + " AND " + "M_CostElement_ID" + "=?";
        final Object[] parameters = { model.get_ID(), as.getC_AcctSchema_ID(), M_AttributeSetInstance_ID, M_CostElement_ID };
        final int no = DB.executeUpdateEx(sql, parameters, model.get_TrxName());
        if (no != 0) {
            this.log.config("Deleted #" + no);
        }
        return no;
    }
    
    private void processCostDetail(final MCostDetail cd) {
        if (!cd.isProcessed()) {
            cd.process();
        }
    }
    
    public static boolean isActivityControlElement(final I_M_CostElement element) {
        final String costElementType = element.getCostElementType();
        return "R".equals(costElementType) || "O".equals(costElementType) || "B".equals(costElementType);
    }
    
    private Collection<MCostElement> getCostElements(final Properties ctx, final String costingelement) {
        if (costingelement.equals("F")) {
            return (Collection<MCostElement>)MCostElement.getByCostingMethod(ctx, this.getCostingMethodFifo());
        }
        return (Collection<MCostElement>)MCostElement.getByCostingMethod(ctx, this.getCostingMethod());
    }
    
    private Collection<MAcctSchema> getAcctSchema(final PO po) {
        final int AD_Org_ID = po.getAD_Org_ID();
        final MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(po.getCtx(), po.getAD_Client_ID());
        final ArrayList<MAcctSchema> list = new ArrayList<MAcctSchema>(ass.length);
        MAcctSchema[] array;
        for (int length = (array = ass).length, i = 0; i < length; ++i) {
            final MAcctSchema as = array[i];
            if (!as.isSkipOrg(AD_Org_ID)) {
                list.add(as);
            }
        }
        return list;
    }
    
    private MCostDetail getCostDetail(final MPPCostCollector cc, final int M_CostElement_ID) {
        final MCostDetail cd = (MCostDetail)new Query(cc.getCtx(), "M_CostDetail", "PP_Cost_Collector_ID=? AND M_CostElement_ID=?", cc.get_TrxName()).setParameters(new Object[] { cc.getPP_Cost_Collector_ID(), M_CostElement_ID }).firstOnly();
        return cd;
    }
    
    private MPPCostCollector createVarianceCostCollector(final MPPCostCollector cc, final String CostCollectorType) {
        final MPPCostCollector ccv = new MPPCostCollector(cc.getCtx(), 0, cc.get_TrxName());
        MPPCostCollector.copyValues((PO)cc, (PO)ccv);
        ccv.setProcessing(false);
        ccv.setProcessed(false);
        ccv.setDocStatus("DR");
        ccv.setDocAction("CO");
        ccv.setCostCollectorType(CostCollectorType);
        ccv.setDocumentNo(null);
        ccv.saveEx();
        return ccv;
    }
    
    private MCostDetail createVarianceCostDetail(final MPPCostCollector ccv, final BigDecimal amt, final BigDecimal qty, final MCostDetail cd, final MProduct product, final MAcctSchema as, final MCostElement element) {
        final MCostDetail cdv = new MCostDetail(ccv.getCtx(), 0, ccv.get_TrxName());
        if (cd != null) {
            MCostDetail.copyValues((PO)cd, (PO)cdv);
            cdv.setProcessed(false);
        }
        if (product != null) {
            cdv.setM_Product_ID(product.getM_Product_ID());
            cdv.setM_AttributeSetInstance_ID(0);
        }
        if (as != null) {
            cdv.setC_AcctSchema_ID(as.getC_AcctSchema_ID());
        }
        if (element != null) {
            cdv.setM_CostElement_ID(element.getM_CostElement_ID());
        }
        cdv.setPP_Cost_Collector_ID(ccv.getPP_Cost_Collector_ID());
        cdv.setAmt(amt);
        cdv.setQty(qty);
        cdv.saveEx();
        this.processCostDetail(cdv);
        return cdv;
    }
    
    public void createActivityControl(final MPPCostCollector cc) {
        if (!cc.isCostCollectorType("160")) {
            return;
        }
        final MProduct product = MProduct.forS_Resource_ID(cc.getCtx(), cc.getS_Resource_ID(), (String)null);
        final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(cc.getAD_Client_ID());
        final BigDecimal qty = routingService.getResourceBaseValue(cc.getS_Resource_ID(), cc);
        final String costingMethod = product.getCostingMethod(MAcctSchema.get(1000000));
        for (final MAcctSchema as : this.getAcctSchema(cc)) {
            for (final MCostElement element : this.getCostElements(cc.getCtx(), costingMethod)) {
                if (!isActivityControlElement((I_M_CostElement)element)) {
                    continue;
                }
                final CostDimension d = new CostDimension(product, as, as.getM_CostType_ID(), cc.getAD_Org_ID(), product.getM_AttributeSetInstance_ID(), element.getM_CostElement_ID());
                final BigDecimal price = this.getResourceActualCostRate(cc, cc.getS_Resource_ID(), d, cc.get_TrxName());
                BigDecimal costs = price.multiply(qty);
                if (costs.scale() > as.getCostingPrecision()) {
                    costs = costs.setScale(as.getCostingPrecision(), RoundingMode.HALF_UP);
                }
                final MCostDetail cd = new MCostDetail(as, cc.getAD_Org_ID(), d.getM_Product_ID(), product.getM_AttributeSetInstance_ID(), element.getM_CostElement_ID(), costs.negate().multiply(new BigDecimal(cc.getPP_Order_Node().get_Value("jmltenagakerja").toString())), qty.negate(), "", cc.get_TrxName());
                cd.setPP_Cost_Collector_ID(cc.getPP_Cost_Collector_ID());
                cd.saveEx();
                this.processCostDetail(cd);
            }
        }
    }
    
    public void createUsageVariances(final MPPCostCollector ccuv, final BigDecimal costvarian, final BigDecimal qtyjmltenagakerja) {
        if (!ccuv.isCostCollectorType("120")) {
            throw new IllegalArgumentException("Cost Collector is not Material Usage Variance");
        }
        MProduct product;
        BigDecimal qty;
        if (ccuv.getPP_Order_BOMLine_ID() > 0) {
            product = MProduct.get(ccuv.getCtx(), ccuv.getM_Product_ID());
            qty = ccuv.getMovementQty();
        }
        else {
            product = MProduct.forS_Resource_ID(ccuv.getCtx(), ccuv.getS_Resource_ID(), (String)null);
            RoutingServiceFactory.get().getRoutingService(ccuv.getAD_Client_ID());
            MWorkflow.get(ccuv.getCtx(), ccuv.getPP_Order_Node().getAD_WF_Node().getAD_Workflow_ID());
            qty = new BigDecimal(ccuv.getPP_Order_Node().getAD_WF_Node().getDuration() - ccuv.getPP_Order_Node().getDuration()).multiply(ccuv.getMovementQty());
        }
        final String costingMethod = product.getCostingMethod(MAcctSchema.get(1000000));
        for (final MAcctSchema as : this.getAcctSchema(ccuv)) {
            for (final MCostElement element : this.getCostElements(ccuv.getCtx(), costingMethod)) {
                final BigDecimal price = this.getProductActualCostPrice(ccuv, product, as, element, ccuv.get_TrxName());
                BigDecimal amt = BigDecimal.ZERO;
                if (costvarian.compareTo(BigDecimal.ZERO) > 0) {
                    amt = this.roundCost(price.multiply(qtyjmltenagakerja).multiply(qty), as.getC_AcctSchema_ID());
                }
                else {
                    amt = this.roundCost(price.multiply(qty), as.getC_AcctSchema_ID());
                }
                if (costvarian.compareTo(BigDecimal.ZERO) > 0) {
                    if (price.compareTo(BigDecimal.ZERO) <= 0 || ccuv.getPP_Order_BOMLine_ID() != 0 || element.getM_CostElement_ID() == 1000000) {
                        continue;
                    }
                    this.createVarianceCostDetail(ccuv, amt, qty, null, product, as, element);
                }
                else {
                    this.createVarianceCostDetail(ccuv, amt, qty, null, product, as, element);
                }
            }
        }
    }
    
    public void createRateVariances(final MPPCostCollector cc) {
        MProduct product;
        if (cc.isCostCollectorType("160")) {
            final I_AD_WF_Node node = cc.getPP_Order_Node().getAD_WF_Node();
            product = MProduct.forS_Resource_ID(cc.getCtx(), node.getS_Resource_ID(), (String)null);
        }
        else {
            if (!cc.isCostCollectorType("110")) {
                return;
            }
            final I_PP_Order_BOMLine bomLine = cc.getPP_Order_BOMLine();
            product = MProduct.get(cc.getCtx(), bomLine.getM_Product_ID());
        }
        MPPCostCollector ccrv = null;
        for (final MAcctSchema as : this.getAcctSchema(cc)) {
            final String costingMethod = product.getCostingMethod(as);
            for (final MCostElement element : this.getCostElements(cc.getCtx(), costingMethod)) {
                final MCostDetail cd = this.getCostDetail(cc, element.getM_CostElement_ID());
                if (cd == null) {
                    continue;
                }
                final BigDecimal qty = cd.getQty();
                final BigDecimal priceStd = this.getProductStandardCostPrice(cc, product, as, element);
                final BigDecimal priceActual = this.getProductActualCostPrice(cc, product, as, element, cc.get_TrxName());
                final BigDecimal amtStd = this.roundCost(priceStd.multiply(qty), as.getC_AcctSchema_ID());
                final BigDecimal amtActual = this.roundCost(priceActual.multiply(qty), as.getC_AcctSchema_ID());
                if (amtStd.compareTo(amtActual) == 0) {
                    continue;
                }
                if (ccrv == null) {
                    ccrv = this.createVarianceCostCollector(cc, "140");
                }
                this.createVarianceCostDetail(ccrv, amtStd.abs().subtract(amtActual.abs()), qty.negate(), cd, null, as, element);
            }
        }
        if (ccrv != null) {
            final boolean ok = ccrv.processIt("CO");
            ccrv.saveEx();
            if (!ok) {
                throw new AdempiereException(ccrv.getProcessMsg());
            }
        }
    }
    
    public void createMethodVariances(final MPPCostCollector cc) {
        if (cc.isCostCollectorType("130")) {
            for (final MAcctSchema as : this.getAcctSchema(cc)) {
                for (final MCostElement element : this.getCostElements(cc.getCtx(), this.getCostingMethod())) {
                    final MProduct product = cc.getM_Product();
                    final BigDecimal qty = cc.getMovementQty();
                    final BigDecimal priceStd = this.getProductActualCostPrice(cc, product, as, element, cc.get_TrxName());
                    final BigDecimal amtStd = priceStd.multiply(qty);
                    this.createVarianceCostDetail(cc, amtStd, qty, null, product, as, element);
                }
            }
            return;
        }
        if (!cc.isCostCollectorType("160")) {
            return;
        }
        final int std_resource_id = cc.getPP_Order_Node().getAD_WF_Node().getS_Resource_ID();
        final int actual_resource_id = cc.getS_Resource_ID();
        if (std_resource_id == actual_resource_id) {
            return;
        }
        MPPCostCollector ccmv = null;
        final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(cc.getAD_Client_ID());
        for (final MAcctSchema as2 : this.getAcctSchema(cc)) {
            for (final MCostElement element2 : this.getCostElements(cc.getCtx(), this.getCostingMethod())) {
                final MProduct resourcePStd = MProduct.forS_Resource_ID(cc.getCtx(), std_resource_id, (String)null);
                final MProduct resourcePActual = MProduct.forS_Resource_ID(cc.getCtx(), actual_resource_id, (String)null);
                final BigDecimal priceStd2 = this.getProductActualCostPrice(cc, resourcePStd, as2, element2, cc.get_TrxName());
                final BigDecimal priceActual = this.getProductActualCostPrice(cc, resourcePActual, as2, element2, cc.get_TrxName());
                if (priceStd2.compareTo(priceActual) == 0) {
                    continue;
                }
                if (ccmv == null) {
                    ccmv = this.createVarianceCostCollector(cc, "130");
                }
                final BigDecimal qty2 = routingService.getResourceBaseValue(cc.getS_Resource_ID(), cc);
                final BigDecimal amtStd2 = priceStd2.multiply(qty2);
                final BigDecimal amtActual = priceActual.multiply(qty2);
                this.createVarianceCostDetail(ccmv, amtActual, qty2, null, resourcePActual, as2, element2);
                this.createVarianceCostDetail(ccmv, amtStd2.negate(), qty2.negate(), null, resourcePStd, as2, element2);
            }
        }
        if (ccmv != null) {
            final boolean ok = ccmv.processIt("CO");
            ccmv.saveEx();
            if (!ok) {
                throw new AdempiereException(ccmv.getProcessMsg());
            }
        }
    }
}
