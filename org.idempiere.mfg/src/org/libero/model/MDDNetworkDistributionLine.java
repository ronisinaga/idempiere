// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.X_DD_NetworkDistributionLine;

public class MDDNetworkDistributionLine extends X_DD_NetworkDistributionLine
{
    private static final long serialVersionUID = 1L;
    
    public MDDNetworkDistributionLine(final Properties ctx, final int DD_NetworkDistributionLine_ID, final String trxName) {
        super(ctx, DD_NetworkDistributionLine_ID, trxName);
    }
    
    public MDDNetworkDistributionLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
}
