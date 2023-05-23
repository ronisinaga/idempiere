// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.callouts;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import java.util.Properties;
import org.adempiere.base.IColumnCallout;

public class Callout_PP_Cost_Collector extends CalloutCostCollector implements IColumnCallout
{
    public String start(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value, final Object oldValue) {
        if (mField.getColumnName().equals("PP_Order_ID")) {
            return this.order(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("PP_Order_Node_ID")) {
            return this.node(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("MovementQty")) {
            return this.duration(ctx, WindowNo, mTab, mField, value);
        }
        return null;
    }
}
