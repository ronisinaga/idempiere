// 
// Decompiled by Procyon v0.5.36
// 

package org.adempiere.model.engines;

import java.util.HashMap;

public class CostEngineFactory
{
    private static final HashMap<Integer, CostEngine> s_engines;
    
    static {
        s_engines = new HashMap<Integer, CostEngine>();
    }
    
    public static CostEngine getCostEngine(final int AD_Client_ID) {
        CostEngine engine = CostEngineFactory.s_engines.get(AD_Client_ID);
        if (engine == null && AD_Client_ID > 0) {
            engine = CostEngineFactory.s_engines.get(0);
        }
        if (engine == null) {
            engine = new CostEngine();
            CostEngineFactory.s_engines.put(AD_Client_ID, engine);
        }
        return engine;
    }
    
    public static void registerCostEngine(final int AD_Client_ID, final CostEngine engine) {
        CostEngineFactory.s_engines.put(AD_Client_ID, engine);
    }
}
