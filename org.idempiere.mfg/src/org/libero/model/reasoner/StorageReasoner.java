// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model.reasoner;

import org.libero.model.wrapper.BOMLineWrapper;
import org.compiere.model.MStorageReservation;
import org.compiere.model.MStorageOnHand;
import java.math.BigDecimal;
import org.compiere.model.MProduct;
import org.compiere.model.MAttributeSet;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MAttribute;
import org.compiere.model.MAttributeInstance;
import org.compiere.util.Env;
import org.compiere.model.PO;
import org.libero.model.MPPOrderWorkflow;
import org.libero.model.MPPOrder;

public class StorageReasoner
{
    public MPPOrderWorkflow getPPOrderWorkflow(final MPPOrder order) {
        final int[] ids = PO.getAllIDs("PP_Order_Workflow", "PP_Order_ID = " + order.get_ID(), (String)null);
        if (ids.length != 1) {
            return null;
        }
        return new MPPOrderWorkflow(Env.getCtx(), ids[0], null);
    }
    
    public boolean equalAttributeInstanceValue(final MAttributeInstance ai1, final MAttributeInstance ai2) {
        if (ai1.getM_Attribute_ID() != ai2.getM_Attribute_ID()) {
            return false;
        }
        boolean equal = true;
        final MAttribute a = new MAttribute(Env.getCtx(), ai1.getM_Attribute_ID(), (String)null);
        if ("N".equals(a.getAttributeValueType())) {
            if (ai1.getValue() == null) {
                equal = (ai2.getValueNumber() == null);
            }
            else {
                equal = (ai1.getValueNumber().compareTo(ai2.getValueNumber()) == 0);
            }
        }
        else if ("S".equals(a.getAttributeValueType())) {
            if (ai1.getValue() == null) {
                equal = (ai2.getValue() == null);
            }
            else {
                equal = ai1.getValue().equals(ai2.getValue());
            }
        }
        else if ("L".equals(a.getAttributeValueType())) {
            equal = (ai1.getM_AttributeValue_ID() == ai2.getM_AttributeValue_ID());
        }
        return equal;
    }
    
    public int[] getAttributeIDs(final MAttributeSetInstance asi) {
        final MAttributeSet as = new MAttributeSet(Env.getCtx(), asi.getM_AttributeSet_ID(), (String)null);
        return this.getPOIDs("M_Attribute", "M_Attribute_ID IN (SELECT M_Attribute_ID FROM M_AttributeUse WHERE M_AttributeSet_ID = " + as.get_ID() + ")", null);
    }
    
    public BigDecimal getSumQtyAvailable(final MProduct p, final MAttributeSetInstance asi) {
        final int[] ids = this.getPOIDs("M_Locator", null, null);
        MStorageOnHand storage = null;
        BigDecimal sumQtyAvailable = BigDecimal.ZERO;
        for (int i = 0; i < ids.length; ++i) {
            storage = MStorageOnHand.get(Env.getCtx(), ids[i], p.get_ID(), asi.get_ID(), (String)null);
            if (storage != null) {
                final BigDecimal available = MStorageReservation.getQtyAvailable(p.get_ID(), storage.getM_Warehouse_ID(), asi.get_ID(), (String)null);
                sumQtyAvailable = sumQtyAvailable.add(available);
            }
        }
        return sumQtyAvailable;
    }
    
    public BigDecimal getSumQtyRequired(final BOMLineWrapper line) {
        final MProduct p = new MProduct(Env.getCtx(), line.getM_Product_ID(), (String)null);
        final MAttributeSetInstance asi = new MAttributeSetInstance(Env.getCtx(), line.getM_AttributeSetInstance_ID(), (String)null);
        return this.getSumQtyAvailable(p, asi).subtract(line.getQtyBOM()).negate();
    }
    
    public BigDecimal getAvailableQtyLocator(final int prodID, final String trxName) {
        final BigDecimal qtyOnHand = this.getQtyOnHand(prodID, trxName);
        final BigDecimal qtyReserved = this.getQtyReserved(prodID, trxName);
        return qtyOnHand.subtract(qtyReserved);
    }
    
    public BigDecimal getQtyOnHand(final int prodID, final String trxName) {
        BigDecimal qtyOnHand = BigDecimal.ZERO;
        final MStorageOnHand[] storages = MStorageOnHand.getOfProduct(Env.getCtx(), prodID, trxName);
        MStorageOnHand[] array;
        for (int length = (array = storages).length, i = 0; i < length; ++i) {
            final MStorageOnHand storage = array[i];
            if (storage != null) {
                qtyOnHand = qtyOnHand.add(storage.getQtyOnHand());
            }
        }
        return qtyOnHand;
    }
    
    public BigDecimal getQtyReserved(final int prodID, final String trxName) {
        BigDecimal qtyReserved = BigDecimal.ZERO;
        final MStorageReservation[] reserves = MStorageReservation.getOfProduct(Env.getCtx(), prodID, trxName);
        MStorageReservation[] array;
        for (int length = (array = reserves).length, i = 0; i < length; ++i) {
            final MStorageReservation reserve = array[i];
            qtyReserved = qtyReserved.add(reserve.getQty());
        }
        return qtyReserved;
    }
    
    public boolean isQtyAvailable(final BOMLineWrapper line) {
        final MProduct p = new MProduct(Env.getCtx(), line.getM_Product_ID(), (String)null);
        final MAttributeSetInstance asi = new MAttributeSetInstance(Env.getCtx(), line.getM_AttributeSetInstance_ID(), (String)null);
        return this.isQtyAvailable(p, asi);
    }
    
    public boolean isQtyAvailable(final MProduct p, final MAttributeSetInstance asi) {
        final int[] ids = this.getPOIDs("M_Locator", null, null);
        MStorageOnHand storage = null;
        BigDecimal sumQtyOnHand = BigDecimal.ZERO;
        BigDecimal sumQtyReserved = BigDecimal.ZERO;
        final int count = 0;
        for (int i = 0; i < ids.length; ++i) {
            storage = MStorageOnHand.get(Env.getCtx(), ids[i], p.get_ID(), asi.get_ID(), (String)null);
            if (storage != null) {
                final BigDecimal available = MStorageReservation.getQtyAvailable(p.get_ID(), storage.getM_Warehouse_ID(), asi.get_ID(), (String)null);
                final BigDecimal reserved = storage.getQtyOnHand().subtract(available);
                sumQtyOnHand = sumQtyOnHand.add(storage.getQtyOnHand());
                sumQtyReserved = sumQtyReserved.add(reserved);
            }
        }
        final double available2 = sumQtyOnHand.subtract(sumQtyReserved).setScale(2, 4).doubleValue();
        return count != 0 && available2 > 0.0;
    }
    
    public int[] getPOIDs(final String locator, final String where, final String trx) {
        final String client = "AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx());
        String w = null;
        if (where == null || where.length() == 0) {
            w = client;
        }
        else {
            w = String.valueOf(where) + " AND " + client;
        }
        return PO.getAllIDs(locator, w, trx);
    }
}
