// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.util.List;
import org.compiere.model.Query;
import java.util.ArrayList;
import java.util.Iterator;
import org.compiere.model.MProduct;
import org.compiere.model.MCost;
import org.adempiere.model.engines.CostDimension;
import org.compiere.model.MAcctSchema;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.model.MCostElement;
import java.util.Collection;
import org.compiere.process.SvrProcess;

public class CreateCostElement extends SvrProcess
{
    private Integer p_AD_Org_ID;
    private int p_C_AcctSchema_ID;
    private int p_M_CostType_ID;
    private int p_M_CostElement_ID;
    private int p_M_Product_Category_ID;
    private int p_M_Product_ID;
    private int p_M_AttributeSetInstance_ID;
    private Collection<MCostElement> m_costElements;
    private int[] m_productIDs;
    
    public CreateCostElement() {
        this.p_AD_Org_ID = null;
        this.p_C_AcctSchema_ID = 0;
        this.p_M_CostType_ID = 0;
        this.p_M_CostElement_ID = 0;
        this.p_M_Product_Category_ID = 0;
        this.p_M_Product_ID = 0;
        this.p_M_AttributeSetInstance_ID = 0;
        this.m_costElements = null;
        this.m_productIDs = null;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("AD_Org_ID")) {
                    this.p_AD_Org_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("C_AcctSchema_ID")) {
                    this.p_C_AcctSchema_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_CostType_ID")) {
                    this.p_M_CostType_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_CostElement_ID")) {
                    this.p_M_CostElement_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_Product_Category_ID")) {
                    this.p_M_Product_Category_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_Product_ID")) {
                    this.p_M_Product_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_AttributeSetInstance_ID")) {
                    this.p_M_AttributeSetInstance_ID = para[i].getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        final MAcctSchema as = MAcctSchema.get(this.getCtx(), this.p_C_AcctSchema_ID);
        int count_costs = 0;
        int count_all = 0;
        int[] orgs;
        for (int length = (orgs = this.getOrgs(as)).length, i = 0; i < length; ++i) {
            final int org_id = orgs[i];
            int[] product_IDs;
            for (int length2 = (product_IDs = this.getProduct_IDs()).length, j = 0; j < length2; ++j) {
                final int product_id = product_IDs[j];
                for (final MCostElement element : this.getElements()) {
                    final CostDimension d = new CostDimension(this.getAD_Client_ID(), org_id, product_id, 0, this.p_M_CostType_ID, as.get_ID(), element.get_ID());
                    MCost cost = (MCost)d.toQuery(MCost.class, this.get_TrxName()).firstOnly();
                    if (cost == null) {
                        final MProduct product = MProduct.get(this.getCtx(), product_id);
                        cost = new MCost(product, d.getM_AttributeSetInstance_ID(), as, d.getAD_Org_ID(), d.getM_CostElement_ID());
                        cost.setM_CostType_ID(d.getM_CostType_ID());
                        cost.saveEx(this.get_TrxName());
                        ++count_costs;
                    }
                    ++count_all;
                }
            }
        }
        return "@Created@ #" + count_costs + " / " + count_all;
    }
    
    private int[] getOrgs(final MAcctSchema as) {
        String whereClause = "";
        final ArrayList<Object> params = new ArrayList<Object>();
        final String CostingLevel = as.getCostingLevel();
        if ("C".equals(CostingLevel)) {
            this.p_AD_Org_ID = 0;
            this.p_M_AttributeSetInstance_ID = 0;
            return new int[1];
        }
        if (this.p_AD_Org_ID != null) {
            whereClause = "AD_Org_ID=?";
            params.add(this.p_AD_Org_ID);
        }
        return new Query(this.getCtx(), "AD_Org", whereClause, this.get_TrxName()).setParameters((List)params).setClient_ID().getIDs();
    }
    
    private Collection<MCostElement> getElements() {
        if (this.m_costElements != null) {
            return this.m_costElements;
        }
        String whereClauseElements = "";
        final ArrayList<Object> paramsElements = new ArrayList<Object>();
        if (this.p_M_CostElement_ID > 0) {
            whereClauseElements = "M_CostElement_ID=?";
            paramsElements.add(this.p_M_CostElement_ID);
        }
        return this.m_costElements = new Query(this.getCtx(), "M_CostElement", whereClauseElements, this.get_TrxName()).setParameters((List)paramsElements).setOnlyActiveRecords(true).setClient_ID().list();
    }
    
    private int[] getProduct_IDs() {
        if (this.m_productIDs != null) {
            return this.m_productIDs;
        }
        String whereClauseProducts = "";
        final ArrayList<Object> paramsProducts = new ArrayList<Object>();
        if (this.p_M_Product_Category_ID > 0) {
            whereClauseProducts = "M_Product_Category_ID=?";
            paramsProducts.add(this.p_M_Product_Category_ID);
            this.p_M_Product_ID = 0;
        }
        if (this.p_M_Product_ID > 0) {
            if (whereClauseProducts.length() > 0) {
                whereClauseProducts = String.valueOf(whereClauseProducts) + " AND ";
            }
            whereClauseProducts = String.valueOf(whereClauseProducts) + "M_Product_ID=?";
            paramsProducts.add(this.p_M_Product_ID);
        }
        else {
            this.p_M_AttributeSetInstance_ID = 0;
        }
        return this.m_productIDs = new Query(this.getCtx(), "M_Product", whereClauseProducts, this.get_TrxName()).setClient_ID().setParameters((List)paramsProducts).getIDs();
    }
}
