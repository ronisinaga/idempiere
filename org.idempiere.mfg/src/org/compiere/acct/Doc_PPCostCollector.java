// 
// Decompiled by Procyon v0.5.36
// 

package org.compiere.acct;

import org.compiere.model.Query;
import java.util.Properties;
import org.compiere.model.MDocType;
import java.util.Iterator;
import java.math.RoundingMode;
import org.compiere.model.MAccount;
import org.compiere.model.MProduct;
import org.compiere.model.MCostElement;
import java.util.Collection;
import java.util.ArrayList;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.libero.model.RoutingServiceFactory;
import org.compiere.model.PO;
import java.sql.ResultSet;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCostDetail;
import java.util.List;
import org.libero.model.RoutingService;
import org.libero.model.MPPCostCollector;

public class Doc_PPCostCollector extends Doc
{
    protected DocLine_CostCollector m_line;
    protected MPPCostCollector m_cc;
    protected RoutingService m_routingService;
    private List<MCostDetail> m_costDetails;
    
    public Doc_PPCostCollector(final MAcctSchema ass, final ResultSet rs, final String trxName) {
        super(ass, (Class)MPPCostCollector.class, rs, "MCC", trxName);
        this.m_line = null;
        this.m_cc = null;
        this.m_routingService = null;
        this.m_costDetails = null;
    }
    
    protected String loadDocumentDetails() {
        this.setC_Currency_ID(-2);
        this.m_cc = (MPPCostCollector)this.getPO();
        this.setDateDoc(this.m_cc.getMovementDate());
        this.setDateAcct(this.m_cc.getMovementDate());
        (this.m_line = new DocLine_CostCollector(this.m_cc, this)).setQty(this.m_cc.getMovementQty(), false);
        if (this.m_line.getM_Product_ID() == 0) {
            this.log.warning(String.valueOf(this.m_line.toString()) + " - No Product");
        }
        this.log.fine(this.m_line.toString());
        this.m_routingService = RoutingServiceFactory.get().getRoutingService(this.m_cc.getAD_Client_ID());
        return null;
    }
    
    public BigDecimal getBalance() {
        final BigDecimal retValue = Env.ZERO;
        return retValue;
    }
    
    public ArrayList<Fact> createFacts(final MAcctSchema as) {
        this.setC_Currency_ID(as.getC_Currency_ID());
        final ArrayList<Fact> facts = new ArrayList<Fact>();
        if ("100".equals(this.m_cc.getCostCollectorType())) {
            facts.add(this.createMaterialReceipt(as));
        }
        else if ("110".equals(this.m_cc.getCostCollectorType())) {
            facts.add(this.createComponentIssue(as));
        }
        else if ("130".equals(this.m_cc.getCostCollectorType())) {
            facts.add(this.createVariance(as, 12));
        }
        else if ("120".equals(this.m_cc.getCostCollectorType())) {
            facts.add(this.createVariance(as, 13));
        }
        else if ("120".equals(this.m_cc.getCostCollectorType())) {
            facts.add(this.createVariance(as, 13));
        }
        else if ("140".equals(this.m_cc.getCostCollectorType())) {
            facts.add(this.createVariance(as, 14));
        }
        else if ("150".equals(this.m_cc.getCostCollectorType())) {
            facts.add(this.createVariance(as, 15));
        }
        else if ("160".equals(this.m_cc.getCostCollectorType())) {
            facts.addAll(this.createActivityControl(as));
        }
        return facts;
    }
    
    protected void createLines(final MCostElement element, final MAcctSchema as, final Fact fact, final MProduct product, final MAccount debit, final MAccount credit, final BigDecimal cost, final BigDecimal qty) {
        if (cost == null || debit == null || credit == null) {
            return;
        }
        this.log.info("CostElement: " + element + "Product: " + product.getName() + " Debit: " + debit.getDescription() + " Credit: " + credit.getDescription() + " Cost: " + cost + " Qty: " + qty);
        FactLine dr = null;
        FactLine cr = null;
        if (cost.signum() != 0) {
            dr = fact.createLine((DocLine)this.m_line, debit, as.getC_Currency_ID(), cost, (BigDecimal)null);
            dr.setQty(qty);
            final String desc = element.getName();
            dr.addDescription(desc);
            dr.setC_Project_ID(this.m_cc.getC_Project_ID());
            dr.setC_Activity_ID(this.m_cc.getC_Activity_ID());
            dr.setC_Campaign_ID(this.m_cc.getC_Campaign_ID());
            dr.setM_Locator_ID(this.m_cc.getM_Locator_ID());
            cr = fact.createLine((DocLine)this.m_line, credit, as.getC_Currency_ID(), (BigDecimal)null, cost);
            cr.setQty(qty);
            cr.addDescription(desc);
            cr.setC_Project_ID(this.m_cc.getC_Project_ID());
            cr.setC_Activity_ID(this.m_cc.getC_Activity_ID());
            cr.setC_Campaign_ID(this.m_cc.getC_Campaign_ID());
            cr.setM_Locator_ID(this.m_cc.getM_Locator_ID());
        }
    }
    
    protected Fact createMaterialReceipt(final MAcctSchema as) {
        final Fact fact = new Fact((Doc)this, as, "A");
        final MProduct product = this.m_cc.getM_Product();
        final MAccount credit = this.m_line.getAccount(11, as);
        for (final MCostDetail cd : this.getCostDetails()) {
            final MCostElement element = MCostElement.get(this.getCtx(), cd.getM_CostElement_ID());
            if (this.m_cc.getMovementQty().signum() != 0) {
                final MAccount debit = this.m_line.getAccount(3, as);
                BigDecimal cost = cd.getAmt();
                if (cost.scale() > as.getStdPrecision()) {
                    cost = cost.setScale(as.getStdPrecision(), RoundingMode.HALF_UP);
                }
                this.createLines(element, as, fact, product, debit, credit, cost, this.m_cc.getMovementQty());
            }
            if (this.m_cc.getScrappedQty().signum() != 0) {
                final MAccount debit = this.m_line.getAccount(22, as);
                BigDecimal cost = cd.getPrice().multiply(this.m_cc.getScrappedQty());
                if (cost.scale() > as.getStdPrecision()) {
                    cost = cost.setScale(as.getStdPrecision(), RoundingMode.HALF_UP);
                }
                this.createLines(element, as, fact, product, debit, credit, cost, this.m_cc.getScrappedQty());
            }
        }
        return fact;
    }
    
    protected Fact createComponentIssue(final MAcctSchema as) {
        final Fact fact = new Fact((Doc)this, as, "A");
        final MProduct product = this.m_cc.getM_Product();
        MAccount debit = null;
        final String docBaseType = MDocType.get(this.getCtx(), this.m_cc.getPP_Order().getC_DocType_ID()).getDocBaseType();
        MAccount credit = this.m_line.getAccount(3, as);
        if (this.m_cc.isFloorStock()) {
            credit = this.m_line.getAccount(16, as);
        }
        if (this.m_cc.getScrappedQty().signum() != 0) {
            final MAccount credit2 = this.m_line.getAccount(11, as);
            debit = this.m_line.getAccount(22, as);
            for (final MCostDetail cd : this.getCostDetails()) {
                final MCostElement element = MCostElement.get(this.getCtx(), cd.getM_CostElement_ID());
                BigDecimal cost = cd.getPrice().multiply(this.m_cc.getScrappedQty());
                if (cost.scale() > as.getStdPrecision()) {
                    cost = cost.setScale(as.getStdPrecision(), RoundingMode.HALF_UP);
                }
                this.createLines(element, as, fact, product, debit, credit2, cost, this.m_cc.getScrappedQty());
            }
        }
        for (final MCostDetail cd2 : this.getCostDetails()) {
            if ("MOF".equals(docBaseType)) {
                debit = this.m_line.getAccount(12, as);
            }
            else {
                debit = this.m_line.getAccount(11, as);
            }
            final MCostElement element2 = MCostElement.get(this.getCtx(), cd2.getM_CostElement_ID());
            BigDecimal cost2 = cd2.getAmt().negate();
            if (cost2.scale() > as.getStdPrecision()) {
                cost2 = cost2.setScale(as.getStdPrecision(), RoundingMode.HALF_UP);
            }
            this.createLines(element2, as, fact, product, debit, credit, cost2, this.m_cc.getMovementQty());
        }
        return fact;
    }
    
    protected List<Fact> createActivityControl(final MAcctSchema as) {
        final ArrayList<Fact> facts = new ArrayList<Fact>();
        final Fact fact = new Fact((Doc)this, as, "A");
        facts.add(fact);
        final MProduct product = this.m_cc.getM_Product();
        final MAccount debit = this.m_line.getAccount(11, as);
        for (final MCostDetail cd : this.getCostDetails()) {
            final BigDecimal costs = cd.getAmt().negate();
            if (costs.signum() == 0) {
                continue;
            }
            final MCostElement element = MCostElement.get(this.getCtx(), cd.getM_CostElement_ID());
            final MAccount credit = this.m_line.getAccount(as, element);
            this.createLines(element, as, fact, product, debit, credit, costs, this.m_cc.getMovementQty());
        }
        return facts;
    }
    
    protected Fact createVariance(final MAcctSchema as, final int VarianceAcctType) {
        final Fact fact = new Fact((Doc)this, as, "A");
        final MProduct product = this.m_cc.getM_Product();
        final MAccount debit = this.m_line.getAccount(VarianceAcctType, as);
        final MAccount credit = this.m_line.getAccount(11, as);
        for (final MCostDetail cd : this.getCostDetails()) {
            final MCostElement element = MCostElement.get(this.getCtx(), cd.getM_CostElement_ID());
            BigDecimal costs = cd.getAmt().negate();
            if (costs.scale() > as.getStdPrecision()) {
                costs = costs.setScale(as.getStdPrecision(), RoundingMode.HALF_UP);
            }
            final BigDecimal qty = cd.getQty();
            this.createLines(element, as, fact, product, debit, credit, costs, qty);
        }
        return fact;
    }
    
    public Collection<MCostElement> getCostElements() {
        final Collection<MCostElement> elements = (Collection<MCostElement>)MCostElement.getByCostingMethod(this.getCtx(), "S");
        return elements;
    }
    
    protected static final MProduct getProductForResource(final Properties ctx, final int S_Resource_ID, final String trxName) {
        final int M_Product_ID = new Query(ctx, "M_Product", "S_Resource_ID=?", trxName).setParameters(new Object[] { S_Resource_ID }).firstIdOnly();
        return MProduct.get(ctx, M_Product_ID);
    }
    
    private List<MCostDetail> getCostDetails() {
        if (this.m_costDetails == null) {
            final String whereClause = "PP_Cost_Collector_ID=?";
            this.m_costDetails = new Query(this.getCtx(), "M_CostDetail", whereClause, this.getTrxName()).setParameters(new Object[] { this.m_cc.getPP_Cost_Collector_ID() }).setOrderBy("M_CostDetail_ID").list();
        }
        return this.m_costDetails;
    }
}
