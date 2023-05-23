// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.util.List;
import org.compiere.model.Query;
import java.util.ArrayList;
import org.adempiere.model.engines.CostDimension;
import org.compiere.util.DB;
import org.compiere.model.MAcctSchema;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.MCost;
import java.util.Iterator;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductPlanning;
import org.compiere.model.MProduct;
import org.libero.model.MPPMRP;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.model.MCostElement;
import java.util.Collection;
import org.compiere.process.SvrProcess;

public class RollupBillOfMaterial extends SvrProcess
{
    private int p_AD_Org_ID;
    private int p_C_AcctSchema_ID;
    private int p_M_CostType_ID;
    private String p_ConstingMethod;
    private int p_M_Product_ID;
    private int p_M_Product_Category_ID;
    private String p_ProductType;
    private int p_S_Resource_ID;
    private int p_M_Warehouse_ID;
    private Collection<MCostElement> m_costElements;
    
    public RollupBillOfMaterial() {
        this.p_AD_Org_ID = 0;
        this.p_C_AcctSchema_ID = 0;
        this.p_M_CostType_ID = 0;
        this.p_ConstingMethod = "";
        this.p_M_Product_ID = 0;
        this.p_M_Product_Category_ID = 0;
        this.p_ProductType = null;
        this.m_costElements = null;
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
                else if (name.equals("ProductType")) {
                    this.p_ProductType = ((para.getParameter() == null) ? null : para.getParameter().toString());
                }
                else if (name.equals("S_Resource_ID")) {
                    this.p_S_Resource_ID = ((para.getParameter() == null) ? null : Integer.valueOf(para.getParameterAsInt()));
                }
                else if (name.equals("M_Warehouse_ID")) {
                    this.p_M_Warehouse_ID = ((para.getParameter() == null) ? null : Integer.valueOf(para.getParameterAsInt()));
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        this.resetCostsLLForLLC0();
        int lowLevel;
        for (int maxLowLevel = lowLevel = MPPMRP.getMaxLowLevel(this.getCtx(), this.get_TrxName()); lowLevel >= 0; --lowLevel) {
            for (final MProduct product : this.getProducts(lowLevel)) {
                final MPPProductPlanning pp = MPPProductPlanning.find(this.getCtx(), this.p_AD_Org_ID, this.p_M_Warehouse_ID, this.p_S_Resource_ID, product.getM_Product_ID(), this.get_TrxName());
                int PP_Product_BOM_ID = 0;
                if (pp != null) {
                    PP_Product_BOM_ID = pp.getPP_Product_BOM_ID();
                }
                else {
                    this.createNotice(product, "@NotFound@ @PP_Product_Planning_ID@");
                }
                if (PP_Product_BOM_ID <= 0) {
                    PP_Product_BOM_ID = MPPProductBOM.getBOMSearchKey(product);
                }
                final MPPProductBOM bom = MPPProductBOM.get(this.getCtx(), PP_Product_BOM_ID);
                if (bom == null) {
                    this.createNotice(product, "@NotFound@ @PP_Product_BOM_ID@");
                }
                this.rollup(product, bom);
            }
        }
        return "@OK@";
    }
    
    protected void rollup(final MProduct product, final MPPProductBOM bom) {
        for (final MCostElement element : this.getCostElements()) {
            for (final MCost cost : this.getCosts(product, element.get_ID())) {
                this.log.info("Calculate Lower Cost for: " + bom);
                final BigDecimal price = this.getCurrentCostPriceLL(bom, element);
                this.log.info(String.valueOf(element.getName()) + " Cost Low Level:" + price);
                cost.setCurrentCostPriceLL(price);
                this.updateCoProductCosts(bom, cost);
                cost.saveEx();
            }
        }
    }
    
    private void updateCoProductCosts(final MPPProductBOM bom, final MCost baseCost) {
        if (bom == null) {
            return;
        }
        BigDecimal costPriceTotal = Env.ZERO;
        MPPProductBOMLine[] lines;
        for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine bomline = lines[i];
            if (bomline.isCoProduct()) {
                final BigDecimal costPrice = baseCost.getCurrentCostPriceLL().multiply(bomline.getCostAllocationPerc(true));
                MCost cost = MCost.get(baseCost.getCtx(), baseCost.getAD_Client_ID(), baseCost.getAD_Org_ID(), bomline.getM_Product_ID(), baseCost.getM_CostType_ID(), baseCost.getC_AcctSchema_ID(), baseCost.getM_CostElement_ID(), 0, baseCost.get_TrxName());
                if (cost == null) {
                    cost = new MCost(baseCost.getCtx(), 0, baseCost.get_TrxName());
                    cost.setAD_Org_ID(baseCost.getAD_Org_ID());
                    cost.setM_Product_ID(bomline.getM_Product_ID());
                    cost.setM_CostType_ID(baseCost.getM_CostType_ID());
                    cost.setC_AcctSchema_ID(baseCost.getC_AcctSchema_ID());
                    cost.setM_CostElement_ID(baseCost.getM_CostElement_ID());
                    cost.setM_AttributeSetInstance_ID(0);
                }
                cost.setCurrentCostPriceLL(costPrice);
                cost.saveEx();
                costPriceTotal = costPriceTotal.add(costPrice);
            }
        }
        if (costPriceTotal.signum() != 0) {
            baseCost.setCurrentCostPriceLL(costPriceTotal);
        }
    }
    
    private BigDecimal getCurrentCostPriceLL(final MPPProductBOM bom, final MCostElement element) {
        this.log.info("Element: " + element);
        BigDecimal costPriceLL = Env.ZERO;
        if (bom == null) {
            return costPriceLL;
        }
        MPPProductBOMLine[] lines;
        for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine bomline = lines[i];
            if (!bomline.isCoProduct()) {
                final MProduct component = MProduct.get(this.getCtx(), bomline.getM_Product_ID());
                for (final MCost cost : this.getCosts(component, element.get_ID())) {
                    final BigDecimal qty = bomline.getQty(true);
                    if (bomline.isByProduct()) {
                        cost.setCurrentCostPriceLL(Env.ZERO);
                    }
                    final BigDecimal costPrice = cost.getCurrentCostPrice().add(cost.getCurrentCostPriceLL());
                    final BigDecimal componentCost = costPrice.multiply(qty);
                    costPriceLL = costPriceLL.add(componentCost);
                    this.log.info("CostElement: " + element.getName() + ", Component: " + component.getValue() + ", CostPrice: " + costPrice + ", Qty: " + qty + ", Cost: " + componentCost + " => Total Cost Element: " + costPriceLL);
                }
            }
        }
        return costPriceLL;
    }
    
    private Collection<MCost> getCosts(final MProduct product, final int M_CostElement_ID) {
        final MAcctSchema as = MAcctSchema.get(this.getCtx(), this.p_C_AcctSchema_ID);
        final CostDimension d = new CostDimension(product, as, this.p_M_CostType_ID, this.p_AD_Org_ID, DB.getSQLValue((String)null, "select m_attributesetinstance_id from m_cost where m_product_id = ?", product.getM_Product_ID()), M_CostElement_ID);
        return d.toQuery(MCost.class, this.get_TrxName()).list();
    }
    
    private Collection<MProduct> getProducts(final int lowLevel) {
        final List<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer("AD_Client_ID=?").append(" AND ").append("LowLevel").append("=?");
        params.add(this.getAD_Client_ID());
        params.add(lowLevel);
        whereClause.append(" AND ").append("IsBOM").append("=?");
        params.add(true);
        if (this.p_M_Product_ID > 0) {
            whereClause.append(" AND ").append("M_Product_ID").append("=?");
            params.add(this.p_M_Product_ID);
        }
        else if (this.p_M_Product_Category_ID > 0) {
            whereClause.append(" AND ").append("M_Product_Category_ID").append("=?");
            params.add(this.p_M_Product_Category_ID);
        }
        if (this.p_M_Product_ID <= 0 && this.p_ProductType != null) {
            whereClause.append(" AND ").append("ProductType").append("=?");
            params.add(this.p_ProductType);
        }
        return new Query(this.getCtx(), "M_Product", whereClause.toString(), this.get_TrxName()).setParameters((List)params).list();
    }
    
    private void resetCostsLLForLLC0() {
        final List<Object> params = new ArrayList<Object>();
        final StringBuffer productWhereClause = new StringBuffer();
        productWhereClause.append("AD_Client_ID=? AND LowLevel=?");
        params.add(this.getAD_Client_ID());
        params.add(0);
        if (this.p_M_Product_ID > 0) {
            productWhereClause.append(" AND ").append("M_Product_ID").append("=?");
            params.add(this.p_M_Product_ID);
        }
        else if (this.p_M_Product_Category_ID > 0) {
            productWhereClause.append(" AND ").append("M_Product_Category_ID").append("=?");
            params.add(this.p_M_Product_Category_ID);
        }
        final String sql = "UPDATE M_Cost c SET CurrentCostPriceLL=0 WHERE EXISTS (SELECT 1 FROM M_Product p WHERE p.M_Product_ID=c.M_Product_ID AND " + (Object)productWhereClause + ")";
        final int no = DB.executeUpdateEx(sql, params.toArray(), this.get_TrxName());
        this.log.info("Updated #" + no);
    }
    
    private Collection<MCostElement> getCostElements() {
        if (this.m_costElements == null) {
            this.m_costElements = (Collection<MCostElement>)MCostElement.getByCostingMethod(this.getCtx(), this.p_ConstingMethod);
        }
        return this.m_costElements;
    }
    
    private void createNotice(final MProduct product, final String msg) {
        final String productValue = (product != null) ? product.getValue() : "-";
        this.addLog("WARNING: Product " + productValue + ": " + msg);
    }
}
