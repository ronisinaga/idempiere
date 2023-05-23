// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.util.ArrayList;
import java.util.List;
import org.compiere.model.Query;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.util.CCache;
import org.libero.tables.X_DD_NetworkDistribution;

public class MDDNetworkDistribution extends X_DD_NetworkDistribution
{
    private static final long serialVersionUID = 1L;
    private static CCache<Integer, MDDNetworkDistribution> s_cache;
    private MDDNetworkDistributionLine[] m_lines;
    
    static {
        MDDNetworkDistribution.s_cache = (CCache<Integer, MDDNetworkDistribution>)new CCache("DD_NetworkDistribution", 50);
    }
    
    public MDDNetworkDistribution(final Properties ctx, final int DD_NetworkDistribution_ID, final String trxName) {
        super(ctx, DD_NetworkDistribution_ID, trxName);
        this.m_lines = null;
    }
    
    public MDDNetworkDistribution(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
        this.m_lines = null;
    }
    
    public static MDDNetworkDistribution get(final Properties ctx, final int DD_NetworkDistribution_ID) {
        MDDNetworkDistribution retValue = (MDDNetworkDistribution)MDDNetworkDistribution.s_cache.get((Object)DD_NetworkDistribution_ID);
        if (retValue != null) {
            return retValue;
        }
        retValue = new MDDNetworkDistribution(ctx, DD_NetworkDistribution_ID, null);
        MDDNetworkDistribution.s_cache.put(DD_NetworkDistribution_ID, retValue);
        return retValue;
    }
    
    public MDDNetworkDistributionLine[] getLines() {
        if (this.m_lines != null) {
            return this.m_lines;
        }
        final List<MDDNetworkDistributionLine> list = new Query(this.getCtx(), "DD_NetworkDistributionLine", "DD_NetworkDistribution_ID=?", this.get_TrxName()).setParameters(new Object[] { this.get_ID() }).setOrderBy("PriorityNo, M_Shipper_ID").list();
        return this.m_lines = list.toArray(new MDDNetworkDistributionLine[list.size()]);
    }
    
    public MDDNetworkDistributionLine[] getLines(final int M_Warehouse_ID) {
        final List<MDDNetworkDistributionLine> list = new ArrayList<MDDNetworkDistributionLine>();
        MDDNetworkDistributionLine[] lines;
        for (int length = (lines = this.getLines()).length, i = 0; i < length; ++i) {
            final MDDNetworkDistributionLine line = lines[i];
            if (line.getM_Warehouse_ID() == M_Warehouse_ID) {
                list.add(line);
            }
        }
        return list.toArray(new MDDNetworkDistributionLine[list.size()]);
    }
}
