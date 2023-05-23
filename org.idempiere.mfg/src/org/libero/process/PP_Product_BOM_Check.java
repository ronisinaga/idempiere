// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.util.ValueNamePair;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.model.MProduct;
import org.compiere.util.Env;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.process.SvrProcess;

public class PP_Product_BOM_Check extends SvrProcess
{
    private int p_Record_ID;
    
    public PP_Product_BOM_Check() {
        this.p_Record_ID = 0;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
            }
        }
        this.p_Record_ID = this.getRecord_ID();
    }
    
    protected String doIt() throws Exception {
        this.log.info("Check BOM Structure");
        final MProduct xp = new MProduct(Env.getCtx(), this.p_Record_ID, this.get_TrxName());
        if (!xp.isBOM()) {
            this.log.info("Product is not a BOM");
            return "OK";
        }
        int lowlevel = MPPProductBOMLine.getLowLevel(this.getCtx(), this.p_Record_ID, this.get_TrxName());
        xp.setLowLevel(lowlevel);
        xp.setIsVerified(true);
        xp.saveEx();
        final MPPProductBOM tbom = MPPProductBOM.getDefault(xp, this.get_TrxName());
        if (tbom == null) {
            this.raiseError("No Default BOM found: ", "Check BOM Parent search key");
        }
        if (tbom.getM_Product_ID() != 0) {
            final MPPProductBOMLine[] tbomlines = tbom.getLines();
            MPPProductBOMLine[] array;
            for (int length = (array = tbomlines).length, i = 0; i < length; ++i) {
                final MPPProductBOMLine tbomline = array[i];
                lowlevel = tbomline.getLowLevel();
                final MProduct p = new MProduct(this.getCtx(), tbomline.getM_Product_ID(), this.get_TrxName());
                p.setLowLevel(lowlevel);
                p.setIsVerified(true);
                p.saveEx();
            }
        }
        return "OK";
    }
    
    private void raiseError(final String string, final String hint) throws Exception {
        DB.rollback(false, this.get_TrxName());
        final MProduct xp = new MProduct(this.getCtx(), this.p_Record_ID, (String)null);
        xp.setIsVerified(false);
        xp.saveEx();
        String msg = string;
        final ValueNamePair pp = CLogger.retrieveError();
        if (pp != null) {
            msg = String.valueOf(pp.getName()) + " - ";
        }
        msg = String.valueOf(msg) + hint;
        throw new AdempiereUserError(msg);
    }
}
