// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.util.Iterator;
import java.util.Collection;
import org.compiere.model.MProductPrice;
import org.compiere.model.MCost;
import org.adempiere.model.engines.CostDimension;
import org.compiere.model.MProduct;
import org.compiere.model.MConversionRate;
import org.compiere.model.MPriceListVersion;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MCostElement;
import org.compiere.model.MAcctSchema;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.math.BigDecimal;
import org.compiere.process.SvrProcess;

public class CopyPriceToStandard extends SvrProcess
{
    private int p_AD_Org_ID;
    private int p_C_AcctSchema_ID;
    private int p_M_CostType_ID;
    private int p_M_CostElement_ID;
    private int p_M_PriceList_Version_ID;
    
    public CopyPriceToStandard() {
        this.p_AD_Org_ID = 0;
        this.p_C_AcctSchema_ID = 0;
        this.p_M_CostType_ID = 0;
        this.p_M_CostElement_ID = 0;
        this.p_M_PriceList_Version_ID = 0;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("M_CostType_ID")) {
                    this.p_M_CostType_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("AD_Org_ID")) {
                    this.p_AD_Org_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("C_AcctSchema_ID")) {
                    this.p_C_AcctSchema_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("M_CostElement_ID")) {
                    this.p_M_CostElement_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("M_PriceList_Version_ID")) {
                    this.p_M_PriceList_Version_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        final MAcctSchema as = MAcctSchema.get(this.getCtx(), this.p_C_AcctSchema_ID);
        final MCostElement element = MCostElement.get(this.getCtx(), this.p_M_CostElement_ID);
        if (!"M".equals(element.getCostElementType())) {
            throw new AdempiereException("Only Material Cost Elements are allowed");
        }
        int count_updated = 0;
        final MPriceListVersion plv = new MPriceListVersion(this.getCtx(), this.p_M_PriceList_Version_ID, this.get_TrxName());
        MProductPrice[] productPrice;
        for (int length = (productPrice = plv.getProductPrice(" AND PriceStd<>0")).length, i = 0; i < length; ++i) {
            final MProductPrice pprice = productPrice[i];
            BigDecimal price = pprice.getPriceStd();
            final int C_Currency_ID = plv.getPriceList().getC_Currency_ID();
            if (C_Currency_ID != as.getC_Currency_ID()) {
                price = MConversionRate.convert(this.getCtx(), pprice.getPriceStd(), C_Currency_ID, as.getC_Currency_ID(), this.getAD_Client_ID(), this.p_AD_Org_ID);
            }
            final MProduct product = MProduct.get(this.getCtx(), pprice.getM_Product_ID());
            final CostDimension d = new CostDimension(product, as, this.p_M_CostType_ID, this.p_AD_Org_ID, 0, this.p_M_CostElement_ID);
            final Collection<MCost> costs = d.toQuery(MCost.class, this.get_TrxName()).list();
            for (final MCost cost : costs) {
                if (cost.getM_CostElement_ID() == element.get_ID()) {
                    cost.setFutureCostPrice(price);
                    cost.saveEx();
                    ++count_updated;
                    break;
                }
            }
        }
        return "@Updated@ #" + count_updated;
    }
}
