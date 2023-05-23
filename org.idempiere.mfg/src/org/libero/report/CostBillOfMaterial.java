// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.report;

import org.adempiere.model.engines.CostEngine;
import java.math.BigDecimal;
import org.compiere.model.MCost;
import org.adempiere.model.engines.CostEngineFactory;
import org.libero.tables.X_T_BOMLine;
import org.compiere.util.Env;
import org.compiere.model.Query;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eevolution.model.MPPProductBOMLine;
import org.eevolution.model.MPPProductBOM;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MProduct;
import org.adempiere.exceptions.FillMandatoryException;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.model.MCostElement;
import java.util.Collection;
import org.compiere.model.MAcctSchema;
import org.compiere.process.SvrProcess;

public class CostBillOfMaterial extends SvrProcess
{
    private static final String LEVELS = "....................";
    private int p_AD_Org_ID;
    private int p_C_AcctSchema_ID;
    private int p_M_Product_ID;
    private int p_M_CostType_ID;
    private String p_ConstingMethod;
    private boolean p_implosion;
    private int m_LevelNo;
    private int m_SeqNo;
    private MAcctSchema m_as;
    private Collection<MCostElement> m_costElements;
    
    public CostBillOfMaterial() {
        this.p_AD_Org_ID = 0;
        this.p_C_AcctSchema_ID = 0;
        this.p_M_Product_ID = 0;
        this.p_M_CostType_ID = 0;
        this.p_ConstingMethod = "S";
        this.p_implosion = false;
        this.m_LevelNo = 0;
        this.m_SeqNo = 0;
        this.m_as = null;
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
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        if (this.p_M_Product_ID == 0) {
            throw new FillMandatoryException(new String[] { "M_Product_ID" });
        }
        this.explodeProduct(this.p_M_Product_ID, false);
        return "";
    }
    
    private void explodeProduct(final int M_Product_ID, final boolean isComponent) {
        final MProduct product = MProduct.get(this.getCtx(), M_Product_ID);
        final List<MPPProductBOM> list = this.getBOMs(product, isComponent);
        if (!isComponent && list.size() == 0) {
            throw new AdempiereException("@Error@ Product is not a BOM");
        }
        for (final MPPProductBOM bom : list) {
            if (!isComponent) {
                this.createLines(bom, null);
            }
            ++this.m_LevelNo;
            MPPProductBOMLine[] lines;
            for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
                final MPPProductBOMLine bomLine = lines[i];
                if (bomLine.isActive()) {
                    this.createLines(bom, bomLine);
                    this.explodeProduct(bomLine.getM_Product_ID(), true);
                }
            }
            --this.m_LevelNo;
        }
    }
    
    private List<MPPProductBOM> getBOMs(final MProduct product, final boolean includeAlternativeBOMs) {
        final ArrayList<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer();
        whereClause.append("M_Product_ID").append("=?");
        params.add(product.get_ID());
        if (includeAlternativeBOMs) {
            whereClause.append(" AND ").append("Value").append("=?");
            params.add(product.getValue());
        }
        final List<MPPProductBOM> list = new Query(this.getCtx(), "PP_Product_BOM", whereClause.toString(), (String)null).setParameters((List)params).setOnlyActiveRecords(true).setOrderBy("Value").list();
        return list;
    }
    
    private void createLines(final MPPProductBOM bom, final MPPProductBOMLine bomLine) {
        MProduct product;
        BigDecimal qty;
        if (bomLine != null) {
            product = MProduct.get(this.getCtx(), bomLine.getM_Product_ID());
            qty = bomLine.getQty();
        }
        else {
            if (bom == null) {
                throw new AdempiereException("@NotFound@ @PP_Product_BOM_ID@");
            }
            product = MProduct.get(this.getCtx(), bom.getM_Product_ID());
            qty = Env.ONE;
        }
        for (final MCostElement costElement : this.getCostElements()) {
            final X_T_BOMLine tboml = new X_T_BOMLine(this.getCtx(), 0, this.get_TrxName());
            tboml.setAD_Org_ID(this.p_AD_Org_ID);
            tboml.setSel_Product_ID(this.p_M_Product_ID);
            tboml.setImplosion(this.p_implosion);
            tboml.setC_AcctSchema_ID(this.p_C_AcctSchema_ID);
            tboml.setM_CostType_ID(this.p_M_CostType_ID);
            tboml.setCostingMethod(this.p_ConstingMethod);
            tboml.setAD_PInstance_ID(this.getAD_PInstance_ID());
            tboml.setM_CostElement_ID(costElement.get_ID());
            tboml.setM_Product_ID(product.get_ID());
            tboml.setQtyBOM(qty);
            tboml.setSeqNo(this.m_SeqNo);
            tboml.setLevelNo(this.m_LevelNo);
            tboml.setLevels(String.valueOf("....................".substring(0, this.m_LevelNo)) + this.m_LevelNo);
            final CostEngine engine = CostEngineFactory.getCostEngine(this.getAD_Client_ID());
            final Collection<MCost> costs = engine.getByElement(product, this.m_as, this.p_M_CostType_ID, this.p_AD_Org_ID, 0, costElement.getM_CostElement_ID());
            BigDecimal currentCostPrice = Env.ZERO;
            BigDecimal currentCostPriceLL = Env.ZERO;
            BigDecimal futureCostPrice = Env.ZERO;
            BigDecimal futureCostPriceLL = Env.ZERO;
            boolean isCostFrozen = false;
            for (final MCost cost : costs) {
                currentCostPrice = currentCostPrice.add(cost.getCurrentCostPrice());
                currentCostPriceLL = currentCostPriceLL.add(cost.getCurrentCostPriceLL());
                futureCostPrice = futureCostPrice.add(cost.getFutureCostPrice());
                futureCostPriceLL = futureCostPriceLL.add(cost.getFutureCostPriceLL());
                isCostFrozen = cost.isCostFrozen();
            }
            tboml.setCurrentCostPrice(currentCostPrice);
            tboml.setCurrentCostPriceLL(currentCostPriceLL);
            tboml.setFutureCostPrice(currentCostPrice);
            tboml.setFutureCostPriceLL(currentCostPriceLL);
            tboml.setIsCostFrozen(isCostFrozen);
            if (bomLine != null) {
                tboml.setPP_Product_BOM_ID(bomLine.getPP_Product_BOM_ID());
                tboml.setPP_Product_BOMLine_ID(bomLine.getPP_Product_BOMLine_ID());
            }
            else if (bom != null) {
                tboml.setPP_Product_BOM_ID(bom.getPP_Product_BOM_ID());
            }
            tboml.saveEx();
            ++this.m_SeqNo;
        }
    }
    
    public Collection<MCostElement> getCostElements() {
        if (this.m_costElements == null) {
            this.m_costElements = (Collection<MCostElement>)MCostElement.getByCostingMethod(this.getCtx(), this.p_ConstingMethod);
        }
        return this.m_costElements;
    }
}
