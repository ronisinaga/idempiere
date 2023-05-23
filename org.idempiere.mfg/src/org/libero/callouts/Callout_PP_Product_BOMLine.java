// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.callouts;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import java.util.Properties;
import org.adempiere.base.IColumnCallout;

public class Callout_PP_Product_BOMLine extends CalloutBOM implements IColumnCallout
{
    public String start(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value, final Object oldValue) {
        if (mField.getColumnName().equals("M_Product_ID")) {
            return this.parent(ctx, WindowNo, mTab, mField, value);
        }
        return null;
    }
}
