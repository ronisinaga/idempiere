// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.util.List;
import org.compiere.model.Query;
import org.compiere.model.MProduct;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MLocator;
import org.compiere.model.MWarehouse;
import java.math.BigDecimal;
import org.eevolution.model.MDDOrderLine;
import java.util.Properties;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;

public class LiberoMovementLine extends MMovementLine
{
    private static final long serialVersionUID = 1L;
    
    public LiberoMovementLine(final MMovement parent) {
        super(parent);
    }
    
    public LiberoMovementLine(final Properties ctx, final int record_ID, final String trxName) {
        super(ctx, record_ID, trxName);
    }
    
    public void setOrderLine(final MDDOrderLine oLine, final BigDecimal Qty, final boolean isReceipt) {
        this.setDD_OrderLine_ID(oLine.getDD_OrderLine_ID());
        this.setLine(oLine.getLine());
        final MProduct product = oLine.getProduct();
        if (product == null) {
            this.set_ValueNoCheck("M_Product_ID", (Object)null);
            this.set_ValueNoCheck("M_AttributeSetInstance_ID", (Object)null);
            this.set_ValueNoCheck("M_AttributeSetInstanceTo_ID", (Object)null);
            this.set_ValueNoCheck("M_Locator_ID", (Object)null);
            this.set_ValueNoCheck("M_LocatorTo_ID", (Object)null);
        }
        else {
            this.setM_Product_ID(oLine.getM_Product_ID());
            this.setM_AttributeSetInstance_ID(oLine.getM_AttributeSetInstance_ID());
            this.setM_AttributeSetInstanceTo_ID(oLine.getM_AttributeSetInstanceTo_ID());
            if (product.isItem()) {
                final MWarehouse w = MWarehouse.get(this.getCtx(), oLine.getParent().getM_Warehouse_ID());
                final MLocator locator_inTransit = MLocator.getDefault(w);
                if (locator_inTransit == null) {
                    throw new AdempiereException("Do not exist Locator for the  Warehouse in transit");
                }
                if (isReceipt) {
                    this.setM_Locator_ID(locator_inTransit.getM_Locator_ID());
                    this.setM_LocatorTo_ID(oLine.getM_LocatorTo_ID());
                }
                else {
                    this.setM_Locator_ID(oLine.getM_Locator_ID());
                    this.setM_LocatorTo_ID(locator_inTransit.getM_Locator_ID());
                }
            }
            else {
                this.set_ValueNoCheck("M_Locator_ID", (Object)null);
                this.set_ValueNoCheck("M_LocatorTo_ID", (Object)null);
            }
        }
        this.setDescription(oLine.getDescription());
        this.setMovementQty(Qty);
    }
    
    public static LiberoMovementLine[] getOfOrderLine(final Properties ctx, final int DD_OrderLine_ID, final String where, final String trxName) {
        String whereClause = "DD_OrderLine_ID=?";
        if (where != null && where.length() > 0) {
            whereClause = String.valueOf(whereClause) + " AND (" + where + ")";
        }
        final List<MMovementLine> list = new Query(ctx, "M_MovementLine", whereClause, trxName).setParameters(new Object[] { DD_OrderLine_ID }).list();
        return list.toArray(new LiberoMovementLine[list.size()]);
    }
}
