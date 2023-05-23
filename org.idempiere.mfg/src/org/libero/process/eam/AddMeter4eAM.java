// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process.eam;

import org.compiere.util.DB;
import org.compiere.model.MAsset;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.util.Env;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class AddMeter4eAM extends SvrProcess
{
    private int p_AD_Client_ID;
    private int p_AD_User_ID;
    private int p_A_Asset_ID;
    private Timestamp p_DateValue;
    private int p_UnitsCycles;
    
    public AddMeter4eAM() {
        this.p_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
        this.p_AD_User_ID = Env.getAD_User_ID(Env.getCtx());
        this.p_A_Asset_ID = 0;
        this.p_DateValue = null;
        this.p_UnitsCycles = 0;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("A_Asset_ID")) {
                    this.p_A_Asset_ID = para.getParameterAsInt();
                }
                else if (name.equals("DateValue")) {
                    this.p_DateValue = para.getParameterAsTimestamp();
                }
                else if (name.equals("UnitsCycles")) {
                    this.p_UnitsCycles = para.getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        if (!this.getPMRuleStr(this.p_A_Asset_ID, "Asset_Prev_Maintenance_Rule").equals("M")) {
            return "Asset don't have a Meter rule!";
        }
        final String _result = null;
        final MAsset asset = new MAsset(this.getCtx(), this.p_A_Asset_ID, this.get_TrxName());
        asset.setUseUnits(this.p_UnitsCycles);
        asset.saveEx();
        return _result;
    }
    
    public String getPMRuleStr(final int Asset_ID, final String Field) {
        return DB.getSQLValueString(this.get_TrxName(), "SELECT " + Field + " FROM A_Asset_Prev_Maintenance WHERE isActive='Y' AND A_Asset_ID=" + Asset_ID, new Object[0]);
    }
}
