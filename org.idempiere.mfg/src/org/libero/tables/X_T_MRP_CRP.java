// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.MTable;
import org.compiere.model.I_AD_PInstance;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_T_MRP_CRP extends PO implements I_T_MRP_CRP, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_T_MRP_CRP(final Properties ctx, final int T_MRP_CRP_ID, final String trxName) {
        super(ctx, T_MRP_CRP_ID, trxName);
    }
    
    public X_T_MRP_CRP(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_T_MRP_CRP.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53044, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_T_MRP_CRP[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public I_AD_PInstance getAD_PInstance() throws RuntimeException {
        return (I_AD_PInstance)MTable.get(this.getCtx(), "AD_PInstance").getPO(this.getAD_PInstance_ID(), this.get_TrxName());
    }
    
    public void setAD_PInstance_ID(final int AD_PInstance_ID) {
        if (AD_PInstance_ID < 1) {
            this.set_Value("AD_PInstance_ID", (Object)null);
        }
        else {
            this.set_Value("AD_PInstance_ID", (Object)AD_PInstance_ID);
        }
    }
    
    public int getAD_PInstance_ID() {
        final Integer ii = (Integer)this.get_Value("AD_PInstance_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDescription(final String Description) {
        this.set_Value("Description", (Object)Description);
    }
    
    public String getDescription() {
        return (String)this.get_Value("Description");
    }
    
    public void setSeqNo(final int SeqNo) {
        this.set_Value("SeqNo", (Object)SeqNo);
    }
    
    public int getSeqNo() {
        final Integer ii = (Integer)this.get_Value("SeqNo");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setT_MRP_CRP_ID(final int T_MRP_CRP_ID) {
        if (T_MRP_CRP_ID < 1) {
            this.set_ValueNoCheck("T_MRP_CRP_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("T_MRP_CRP_ID", (Object)T_MRP_CRP_ID);
        }
    }
    
    public int getT_MRP_CRP_ID() {
        final Integer ii = (Integer)this.get_Value("T_MRP_CRP_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setT_MRP_CRP_UU(final String T_MRP_CRP_UU) {
        this.set_Value("T_MRP_CRP_UU", (Object)T_MRP_CRP_UU);
    }
    
    public String getT_MRP_CRP_UU() {
        return (String)this.get_Value("T_MRP_CRP_UU");
    }
}
