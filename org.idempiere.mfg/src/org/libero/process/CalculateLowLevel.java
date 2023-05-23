// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.POResultSet;
import java.util.logging.Level;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.model.MProduct;
import org.compiere.util.Env;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;

public class CalculateLowLevel extends SvrProcess
{
    protected void prepare() {
    }
    
    protected String doIt() throws Exception {
        int count_ok = 0;
        int count_err = 0;
        final POResultSet<MProduct> rs = new Query(this.getCtx(), "M_Product", "AD_Client_ID=?", this.get_TrxName()).setParameters(new Object[] { Env.getAD_Client_ID(this.getCtx()) }).setOrderBy("M_Product_ID").scroll();
        rs.setCloseOnError(true);
        while (rs.hasNext()) {
            final MProduct product = (MProduct)rs.next();
            try {
                final int lowlevel = MPPProductBOMLine.getLowLevel(this.getCtx(), product.get_ID(), this.get_TrxName());
                product.setLowLevel(lowlevel);
                product.saveEx();
                ++count_ok;
            }
            catch (Exception e) {
                this.log.log(Level.SEVERE, e.getLocalizedMessage(), (Throwable)e);
                ++count_err;
            }
        }
        rs.close();
        return "@Ok@ #" + count_ok + " @Error@ #" + count_err;
    }
}
