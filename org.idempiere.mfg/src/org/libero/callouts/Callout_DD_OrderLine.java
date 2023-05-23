// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.callouts;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import java.util.Properties;
import org.adempiere.base.IColumnCallout;

public class Callout_DD_OrderLine extends CalloutDistributionOrder implements IColumnCallout
{
    public String start(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value, final Object oldValue) {
        if (mField.getColumnName().equals("QtyEntered")) {
            return this.qty(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("M_Product_ID")) {
            return this.setLocatorTo(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("ConfirmedQty")) {
            return this.qtyConfirmed(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("C_UOM_ID")) {
            return this.qty(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("M_AtttributeSetInstanceTo_ID")) {
            return this.qtyConfirmed(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("M_AtttributeSetInstance_ID")) {
            return this.qty(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("QtyOrdered")) {
            return this.qty(ctx, WindowNo, mTab, mField, value);
        }
        if (mField.getColumnName().equals("AD_Org_ID")) {
            return this.bPartner(ctx, WindowNo, mTab, mField, value);
        }
        return null;
    }
}
