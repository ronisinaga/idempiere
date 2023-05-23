// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.PO;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.util.AdempiereSystemError;
import org.eevolution.model.MPPProductBOM;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.util.Env;
import java.util.Properties;
import org.compiere.process.SvrProcess;

public class CopyFromBOM extends SvrProcess
{
    private int p_Record_ID;
    private int p_PP_Product_BOM_ID;
    private int no;
    private Properties ctx;
    
    public CopyFromBOM() {
        this.p_Record_ID = 0;
        this.p_PP_Product_BOM_ID = 0;
        this.no = 0;
        this.ctx = Env.getCtx();
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("PP_Product_BOM_ID")) {
                    this.p_PP_Product_BOM_ID = para[i].getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
        this.p_Record_ID = this.getRecord_ID();
    }
    
    protected String doIt() throws Exception {
        this.log.info("From PP_Product_BOM_ID=" + this.p_PP_Product_BOM_ID + " to " + this.p_Record_ID);
        if (this.p_Record_ID == 0) {
            throw new IllegalArgumentException("Target PP_Product_BOM_ID == 0");
        }
        if (this.p_PP_Product_BOM_ID == 0) {
            throw new IllegalArgumentException("Source PP_Product_BOM_ID == 0");
        }
        if (this.p_Record_ID == this.p_PP_Product_BOM_ID) {
            return "";
        }
        final MPPProductBOM fromBom = new MPPProductBOM(this.ctx, this.p_PP_Product_BOM_ID, this.get_TrxName());
        final MPPProductBOM toBOM = new MPPProductBOM(this.ctx, this.p_Record_ID, this.get_TrxName());
        if (toBOM.getLines().length > 0) {
            throw new AdempiereSystemError("@Error@ Existing BOM Line(s)");
        }
        final MPPProductBOMLine[] frombomlines = fromBom.getLines();
        MPPProductBOMLine[] array;
        for (int length = (array = frombomlines).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine frombomline = array[i];
            final MPPProductBOMLine tobomline = new MPPProductBOMLine(this.ctx, 0, this.get_TrxName());
            MPPProductBOMLine.copyValues((PO)frombomline, (PO)tobomline);
            tobomline.setPP_Product_BOM_ID(toBOM.getPP_Product_BOM_ID());
            tobomline.save();
            ++this.no;
        }
        return "OK";
    }
    
    protected void postProcess(final boolean success) {
        this.addLog("@Copied@=" + this.no);
    }
}
