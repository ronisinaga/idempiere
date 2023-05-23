// 
// Decompiled by Procyon v0.5.36
// 

package org.compiere.acct;

import org.compiere.util.DB;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAccount;
import org.compiere.model.MCostElement;
import org.compiere.model.MAcctSchema;
import org.compiere.model.PO;
import java.util.HashMap;

public class DocLine_CostCollector extends DocLine
{
    private static final HashMap<Integer, String> s_acctName;
    
    static {
        (s_acctName = new HashMap<Integer, String>()).put(11, "P_WIP_Acct");
        DocLine_CostCollector.s_acctName.put(12, "P_MethodChangeVariance_Acct");
        DocLine_CostCollector.s_acctName.put(13, "P_UsageVariance_Acct");
        DocLine_CostCollector.s_acctName.put(14, "P_RateVariance_Acct");
        DocLine_CostCollector.s_acctName.put(15, "P_MixVariance_Acct");
        DocLine_CostCollector.s_acctName.put(16, "P_FloorStock_Acct");
        DocLine_CostCollector.s_acctName.put(17, "P_CostOfProduction_Acct");
        DocLine_CostCollector.s_acctName.put(18, "P_Labor_Acct");
        DocLine_CostCollector.s_acctName.put(19, "P_Burden_Acct");
        DocLine_CostCollector.s_acctName.put(20, "P_OutsideProcessing_Acct");
        DocLine_CostCollector.s_acctName.put(21, "P_Overhead_Acct");
        DocLine_CostCollector.s_acctName.put(22, "P_Scrap_Acct");
    }
    
    public DocLine_CostCollector(final PO po, final Doc doc) {
        super(po, doc);
    }
    
    public MAccount getAccount(final MAcctSchema as, final MCostElement element) {
        final String costElementType = element.getCostElementType();
        int acctType;
        if ("M".equals(costElementType)) {
            acctType = 3;
        }
        else if ("R".equals(costElementType)) {
            acctType = 18;
        }
        else if ("B".equals(costElementType)) {
            acctType = 19;
        }
        else if ("O".equals(costElementType)) {
            acctType = 21;
        }
        else {
            if (!"X".equals(costElementType)) {
                throw new AdempiereException("@NotSupported@ " + element);
            }
            acctType = 20;
        }
        return this.getAccount(acctType, as);
    }
    
    public MAccount getAccount(final int AcctType, final MAcctSchema as) {
        final String acctName = DocLine_CostCollector.s_acctName.get(AcctType);
        if (this.getM_Product_ID() == 0 || acctName == null) {
            return super.getAccount(AcctType, as);
        }
        return this.getAccount(acctName, as);
    }
    
    public MAccount getAccount(final String acctName, final MAcctSchema as) {
        final String sql = " SELECT  COALESCE(pa." + acctName + ",pca." + acctName + ",asd." + acctName + ")" + " FROM M_Product p" + " INNER JOIN M_Product_Acct pa ON (pa.M_Product_ID=p.M_Product_ID)" + " INNER JOIN M_Product_Category_Acct pca ON (pca.M_Product_Category_ID=p.M_Product_Category_ID AND pca.C_AcctSchema_ID=pa.C_AcctSchema_ID)" + " INNER JOIN C_AcctSchema_Default asd ON (asd.C_AcctSchema_ID=pa.C_AcctSchema_ID)" + " WHERE pa.M_Product_ID=? AND pa.C_AcctSchema_ID=?";
        final int validCombination_ID = DB.getSQLValueEx((String)null, sql, new Object[] { this.getM_Product_ID(), as.get_ID() });
        if (validCombination_ID <= 0) {
            return null;
        }
        return MAccount.get(as.getCtx(), validCombination_ID);
    }
}
