// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.util.Env;
import org.compiere.util.DB;
import java.sql.ResultSet;
import org.compiere.model.Query;
import java.util.Properties;
import java.util.Collection;
import org.compiere.util.CCache;
import org.libero.tables.X_PP_WF_Node_Product;

public class MPPWFNodeProduct extends X_PP_WF_Node_Product
{
    private static final long serialVersionUID = 1L;
    private static CCache<Integer, Collection<MPPWFNodeProduct>> s_cache;
    
    static {
        MPPWFNodeProduct.s_cache = (CCache<Integer, Collection<MPPWFNodeProduct>>)new CCache("PP_WF_Node_Product", 20);
    }
    
    public static Collection<MPPWFNodeProduct> forAD_WF_Node_ID(final Properties ctx, final int AD_WF_Node_ID) {
        Collection<MPPWFNodeProduct> lines = (Collection<MPPWFNodeProduct>)MPPWFNodeProduct.s_cache.get((Object)AD_WF_Node_ID);
        if (lines != null) {
            return lines;
        }
        lines = new Query(ctx, "PP_WF_Node_Product", "AD_WF_Node_ID=?", (String)null).setParameters(new Object[] { AD_WF_Node_ID }).setOnlyActiveRecords(true).setOrderBy("SeqNo").list();
        MPPWFNodeProduct.s_cache.put(AD_WF_Node_ID, lines);
        return lines;
    }
    
    public MPPWFNodeProduct(final Properties ctx, final int PP_WF_Node_Product_ID, final String trxName) {
        super(ctx, PP_WF_Node_Product_ID, trxName);
    }
    
    public MPPWFNodeProduct(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected boolean beforeSave(final boolean newRecord) {
        if (this.getSeqNo() == 0) {
            final int seqNo = DB.getSQLValueEx(this.get_TrxName(), "SELECT COALESCE(MAX(SeqNo),0)+10 FROM PP_WF_Node_Product WHERE  AD_WF_Node_ID=? AND PP_WF_Node_Product_ID<>?", new Object[] { this.getAD_WF_Node_ID(), this.get_ID() });
            this.setSeqNo(seqNo);
        }
        if (this.getQty().compareTo(Env.ZERO) == 0 && this.isSubcontracting()) {
            this.setQty(Env.ONE);
        }
        return true;
    }
}
