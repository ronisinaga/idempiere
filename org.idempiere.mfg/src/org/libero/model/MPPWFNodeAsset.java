// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.sql.ResultSet;
import org.compiere.model.Query;
import java.util.Properties;
import java.util.Collection;
import org.compiere.util.CCache;
import org.libero.tables.X_PP_WF_Node_Asset;

public class MPPWFNodeAsset extends X_PP_WF_Node_Asset
{
    private static final long serialVersionUID = 1L;
    private static CCache<Integer, Collection<MPPWFNodeAsset>> s_cache;
    
    static {
        MPPWFNodeAsset.s_cache = (CCache<Integer, Collection<MPPWFNodeAsset>>)new CCache("PP_WF_Node_Asset", 20);
    }
    
    public static Collection<MPPWFNodeAsset> forAD_WF_Node_ID(final Properties ctx, final int AD_WF_Node_ID) {
        Collection<MPPWFNodeAsset> lines = (Collection<MPPWFNodeAsset>)MPPWFNodeAsset.s_cache.get((Object)AD_WF_Node_ID);
        if (lines != null) {
            return lines;
        }
        lines = new Query(ctx, "PP_WF_Node_Asset", "AD_WF_Node_ID=?", (String)null).setParameters(new Object[] { AD_WF_Node_ID }).setOnlyActiveRecords(true).setOrderBy("SeqNo").list();
        MPPWFNodeAsset.s_cache.put(AD_WF_Node_ID, lines);
        return lines;
    }
    
    public MPPWFNodeAsset(final Properties ctx, final int PP_WF_Node_Asset_ID, final String trxName) {
        super(ctx, PP_WF_Node_Asset_ID, trxName);
    }
    
    public MPPWFNodeAsset(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
}
