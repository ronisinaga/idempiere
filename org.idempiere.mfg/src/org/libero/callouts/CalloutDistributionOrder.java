// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.callouts;

import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MOrg;
import org.compiere.model.I_M_Movement;
import org.compiere.model.MWarehouse;
import org.adempiere.model.GridTabWrapper;
import org.libero.tables.I_DD_OrderLine;
import org.compiere.util.Msg;
import org.eevolution.model.MDDOrderLine;
import org.compiere.model.MStorageReservation;
import org.compiere.model.MLocator;
import org.compiere.model.MProduct;
import org.compiere.model.MUOMConversion;
import org.compiere.model.MUOM;
import java.math.BigDecimal;
import org.compiere.util.Env;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import java.util.Properties;
import org.compiere.model.CalloutEngine;

public class CalloutDistributionOrder extends CalloutEngine
{
    private boolean steps;
    
    public CalloutDistributionOrder() {
        this.steps = false;
    }
    
    public String qty(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        if (this.isCalloutActive() || value == null) {
            return "";
        }
        final int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");
        if (this.steps) {
            this.log.warning("init - M_Product_ID=" + M_Product_ID + " - ");
        }
        BigDecimal QtyOrdered = Env.ZERO;
        if (M_Product_ID == 0) {
            return "";
        }
        if (mField.getColumnName().equals("C_UOM_ID")) {
            final int C_UOM_To_ID = (int)value;
            BigDecimal QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
            final BigDecimal QtyEntered2 = QtyEntered.setScale(MUOM.getPrecision(ctx, C_UOM_To_ID), 4);
            if (QtyEntered.compareTo(QtyEntered2) != 0) {
                this.log.fine("Corrected QtyEntered Scale UOM=" + C_UOM_To_ID + "; QtyEntered=" + QtyEntered + "->" + QtyEntered2);
                QtyEntered = QtyEntered2;
                mTab.setValue("QtyEntered", (Object)QtyEntered);
            }
            QtyOrdered = MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);
            if (QtyOrdered == null) {
                QtyOrdered = QtyEntered;
            }
            final boolean conversion = QtyEntered.compareTo(QtyOrdered) != 0;
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
            mTab.setValue("QtyOrdered", (Object)QtyOrdered);
        }
        else if (mField.getColumnName().equals("QtyEntered")) {
            final int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");
            BigDecimal QtyEntered = (BigDecimal)value;
            final BigDecimal QtyEntered2 = QtyEntered.setScale(MUOM.getPrecision(ctx, C_UOM_To_ID), 4);
            if (QtyEntered.compareTo(QtyEntered2) != 0) {
                this.log.fine("Corrected QtyEntered Scale UOM=" + C_UOM_To_ID + "; QtyEntered=" + QtyEntered + "->" + QtyEntered2);
                QtyEntered = QtyEntered2;
                mTab.setValue("QtyEntered", (Object)QtyEntered);
            }
            QtyOrdered = MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);
            if (QtyOrdered == null) {
                QtyOrdered = QtyEntered;
            }
            final boolean conversion = QtyEntered.compareTo(QtyOrdered) != 0;
            this.log.fine("UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " QtyOrdered=" + QtyOrdered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
            mTab.setValue("QtyOrdered", (Object)QtyOrdered);
        }
        else if (mField.getColumnName().equals("QtyOrdered")) {
            final int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");
            QtyOrdered = (BigDecimal)value;
            final int precision = MProduct.get(ctx, M_Product_ID).getUOMPrecision();
            final BigDecimal QtyOrdered2 = QtyOrdered.setScale(precision, 4);
            if (QtyOrdered.compareTo(QtyOrdered2) != 0) {
                this.log.fine("Corrected QtyOrdered Scale " + QtyOrdered + "->" + QtyOrdered2);
                QtyOrdered = QtyOrdered2;
                mTab.setValue("QtyOrdered", (Object)QtyOrdered);
            }
            BigDecimal QtyEntered = MUOMConversion.convertProductTo(ctx, M_Product_ID, C_UOM_To_ID, QtyOrdered);
            if (QtyEntered == null) {
                QtyEntered = QtyOrdered;
            }
            final boolean conversion2 = QtyOrdered.compareTo(QtyEntered) != 0;
            this.log.fine("UOM=" + C_UOM_To_ID + ", QtyOrdered=" + QtyOrdered + " -> " + conversion2 + " QtyEntered=" + QtyEntered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion2 ? "Y" : "N");
            mTab.setValue("QtyEntered", (Object)QtyEntered);
        }
        else {
            QtyOrdered = (BigDecimal)mTab.getValue("QtyOrdered");
        }
        if (M_Product_ID != 0 && Env.isSOTrx(ctx, WindowNo) && QtyOrdered.signum() > 0) {
            final MProduct product = MProduct.get(ctx, M_Product_ID);
            if (product.isStocked()) {
                final int M_Locator_ID = Env.getContextAsInt(ctx, WindowNo, "M_Locator_ID");
                final int M_AttributeSetInstance_ID = Env.getContextAsInt(ctx, WindowNo, "M_AttributeSetInstance_ID");
                final int M_Warehouse_ID = MLocator.get(ctx, M_Locator_ID).getM_Warehouse_ID();
                BigDecimal available = MStorageReservation.getQtyAvailable(M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, (String)null);
                if (available == null) {
                    available = Env.ZERO;
                }
                if (available.signum() == 0) {
                    mTab.fireDataStatusEEvent("NoQtyAvailable", "0", false);
                }
                else if (available.compareTo(QtyOrdered) < 0) {
                    mTab.fireDataStatusEEvent("InsufficientQtyAvailable", available.toString(), false);
                }
                else {
                    Integer DD_OrderLine_ID = (Integer)mTab.getValue("DD_OrderLine_ID");
                    if (DD_OrderLine_ID == null) {
                        DD_OrderLine_ID = new Integer(0);
                    }
                    BigDecimal notReserved = MDDOrderLine.getNotReserved(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, (int)DD_OrderLine_ID);
                    if (notReserved == null) {
                        notReserved = Env.ZERO;
                    }
                    final BigDecimal total = available.subtract(notReserved);
                    if (total.compareTo(QtyOrdered) < 0) {
                        final String info = Msg.parseTranslation(ctx, "@QtyAvailable@=" + available + "  -  @QtyNotReserved@=" + notReserved + "  =  " + total);
                        mTab.fireDataStatusEEvent("InsufficientQtyAvailable", info, false);
                    }
                }
            }
        }
        return "";
    }
    
    public String qtyConfirmed(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final I_DD_OrderLine line = (I_DD_OrderLine)GridTabWrapper.create(mTab, (Class)I_DD_OrderLine.class);
        if (line.getConfirmedQty().compareTo(line.getQtyOrdered().subtract(line.getQtyInTransit()).subtract(line.getQtyDelivered())) > 0) {
            final String info = Msg.parseTranslation(ctx, "@ConfirmedQty@ : " + line.getConfirmedQty() + " > @QtyToDeliver@ : " + line.getQtyOrdered().subtract(line.getQtyInTransit()).subtract(line.getQtyDelivered()));
            mTab.fireDataStatusEEvent("", info, false);
            line.setConfirmedQty(line.getQtyOrdered().subtract(line.getQtyInTransit()).subtract(line.getQtyDelivered()));
        }
        return "";
    }
    
    public String setLocatorTo(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final I_DD_OrderLine line = (I_DD_OrderLine)GridTabWrapper.create(mTab, (Class)I_DD_OrderLine.class);
        if (value != null) {
            final MProduct product = MProduct.get(ctx, (int)value);
            if (line.getC_UOM_ID() <= 0) {
                line.setC_UOM_ID(product.getC_UOM_ID());
            }
        }
        final MWarehouse[] ws = MWarehouse.getForOrg(ctx, line.getAD_Org_ID());
        if (ws == null && ws.length < 0) {
            return "";
        }
        final MLocator locator_to = MLocator.getDefault(ws[0]);
        if (locator_to != null) {
            line.setM_LocatorTo_ID(locator_to.getM_Locator_ID());
        }
        return "";
    }
    
    public String UOM(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final I_DD_OrderLine line = (I_DD_OrderLine)GridTabWrapper.create(mTab, (Class)I_DD_OrderLine.class);
        final MProduct product = MProduct.get(ctx, line.getM_Product_ID());
        if (product != null) {
            line.setC_UOM_ID(product.getC_UOM_ID());
        }
        return "";
    }
    
    public String bPartner(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        final I_M_Movement m_movement = (I_M_Movement)GridTabWrapper.create(mTab, (Class)I_M_Movement.class);
        final MOrg org = MOrg.get(ctx, m_movement.getAD_Org_ID());
        final int C_BPartner_ID = org.getLinkedC_BPartner_ID((String)null);
        if (C_BPartner_ID > 0) {
            final MBPartnerLocation[] locations = MBPartnerLocation.getForBPartner(ctx, C_BPartner_ID, (String)null);
            m_movement.setC_BPartner_ID(C_BPartner_ID);
            if (locations.length > 0) {
                m_movement.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());
            }
        }
        return "";
    }
}
