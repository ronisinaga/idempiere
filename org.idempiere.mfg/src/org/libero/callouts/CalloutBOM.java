// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.callouts;

import java.math.BigDecimal;
import org.compiere.util.Env;
import org.compiere.model.MUOMConversion;
import org.libero.tables.I_PP_Order_BOMLine;
import org.compiere.model.I_M_Product;
import org.eevolution.model.I_PP_Product_BOM;
import org.compiere.model.MProduct;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.GridTabWrapper;
import org.eevolution.model.I_PP_Product_BOMLine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import java.util.Properties;
import org.compiere.model.CalloutEngine;

public class CalloutBOM extends CalloutEngine
{
    public String parent(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        if (this.isCalloutActive() || value == null) {
            return "";
        }
        final int M_Product_ID = (int)value;
        if (M_Product_ID <= 0) {
            return "";
        }
        final I_PP_Product_BOMLine bomLine = (I_PP_Product_BOMLine)GridTabWrapper.create(mTab, (Class)I_PP_Product_BOMLine.class);
        final I_PP_Product_BOM bom = bomLine.getPP_Product_BOM();
        if (bom.getM_Product_ID() == bomLine.getM_Product_ID()) {
            throw new AdempiereException("@ValidComponent@ - Error Parent not be Component");
        }
        final I_M_Product product = (I_M_Product)MProduct.get(ctx, M_Product_ID);
        bomLine.setDescription(product.getDescription());
        bomLine.setHelp(product.getHelp());
        bomLine.setC_UOM_ID(product.getC_UOM_ID());
        return "";
    }
    
    public String qtyLine(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        if (this.isCalloutActive() || value == null) {
            return "";
        }
        final I_PP_Order_BOMLine bomLine = (I_PP_Order_BOMLine)GridTabWrapper.create(mTab, (Class)I_PP_Order_BOMLine.class);
        final int M_Product_ID = bomLine.getM_Product_ID();
        final String columnName = mField.getColumnName();
        if (M_Product_ID <= 0) {
            final BigDecimal QtyEntered = bomLine.getQtyEntered();
            bomLine.setQtyRequired(QtyEntered);
        }
        else if ("C_UOM_ID".equals(columnName) || "QtyEntered".equals(columnName)) {
            final BigDecimal QtyEntered = bomLine.getQtyEntered();
            BigDecimal QtyRequired = MUOMConversion.convertProductFrom(ctx, M_Product_ID, bomLine.getC_UOM_ID(), QtyEntered);
            if (QtyRequired == null) {
                QtyRequired = QtyEntered;
            }
            final boolean conversion = QtyEntered.compareTo(QtyRequired) != 0;
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion);
            bomLine.setQtyRequired(QtyRequired);
        }
        else if ("QtyRequired".equals(columnName)) {
            final BigDecimal QtyRequired2 = bomLine.getQtyRequired();
            BigDecimal QtyEntered2 = MUOMConversion.convertProductTo(ctx, M_Product_ID, bomLine.getC_UOM_ID(), QtyRequired2);
            if (QtyEntered2 == null) {
                QtyEntered2 = QtyRequired2;
            }
            final boolean conversion = QtyRequired2.compareTo(QtyEntered2) != 0;
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion);
            bomLine.setQtyEntered(QtyEntered2);
        }
        return "";
    }
    
    public String getdefaults(final Properties ctx, final int WindowNo, final GridTab mTab, final GridField mField, final Object value) {
        if (this.isCalloutActive() || value == null) {
            return "";
        }
        final int M_Product_ID = (int)value;
        if (M_Product_ID <= 0) {
            return "";
        }
        final I_M_Product product = (I_M_Product)MProduct.get(ctx, M_Product_ID);
        final I_PP_Product_BOM bom = (I_PP_Product_BOM)GridTabWrapper.create(mTab, (Class)I_PP_Product_BOM.class);
        bom.setValue(product.getValue());
        bom.setName(product.getName());
        bom.setDescription(product.getDescription());
        bom.setHelp(product.getHelp());
        bom.setC_UOM_ID(product.getC_UOM_ID());
        return "";
    }
}
