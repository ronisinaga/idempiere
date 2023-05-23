// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.eevolution.model.MPPProductBOM;
import org.compiere.model.MUOM;
import org.compiere.model.MProduct;
import org.compiere.util.Env;
import java.util.Properties;
import org.compiere.apps.form.TreeMaintenance;
import org.compiere.util.CLogger;

public class TreeBOM
{
    public static CLogger log;
    
    static {
        TreeBOM.log = CLogger.getCLogger((Class)TreeMaintenance.class);
    }
    
    public Properties getCtx() {
        return Env.getCtx();
    }
    
    public String productSummary(final MProduct product, final boolean isLeaf) {
        final MUOM uom = MUOM.get(this.getCtx(), product.getC_UOM_ID());
        final String value = product.getValue();
        final String name = product.get_Translation("Name");
        final StringBuffer sb = new StringBuffer(value);
        if (name != null && !value.equals(name)) {
            sb.append("_").append(product.getName());
        }
        sb.append(" [").append(uom.get_Translation("UOMSymbol")).append("]");
        return sb.toString();
    }
    
    public String productSummary(final MPPProductBOM bom) {
        final String value = bom.getValue();
        final String name = bom.get_Translation("Name");
        final StringBuffer sb = new StringBuffer(value);
        if (name != null && !name.equals(value)) {
            sb.append("_").append(name);
        }
        return sb.toString();
    }
}
