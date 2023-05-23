// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.MTable;
import org.compiere.model.I_A_Asset;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_WF_Node_Asset extends PO implements I_PP_WF_Node_Asset, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_PP_WF_Node_Asset(final Properties ctx, final int PP_WF_Node_Asset_ID, final String trxName) {
        super(ctx, PP_WF_Node_Asset_ID, trxName);
    }
    
    public X_PP_WF_Node_Asset(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_WF_Node_Asset.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53017, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_WF_Node_Asset[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public I_A_Asset getA_Asset() throws RuntimeException {
        return (I_A_Asset)MTable.get(this.getCtx(), "A_Asset").getPO(this.getA_Asset_ID(), this.get_TrxName());
    }
    
    public void setA_Asset_ID(final int A_Asset_ID) {
        if (A_Asset_ID < 1) {
            this.set_Value("A_Asset_ID", (Object)null);
        }
        else {
            this.set_Value("A_Asset_ID", (Object)A_Asset_ID);
        }
    }
    
    public int getA_Asset_ID() {
        final Integer ii = (Integer)this.get_Value("A_Asset_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_AD_WF_Node getAD_WF_Node() throws RuntimeException {
        return (I_AD_WF_Node)MTable.get(this.getCtx(), "AD_WF_Node").getPO(this.getAD_WF_Node_ID(), this.get_TrxName());
    }
    
    public void setAD_WF_Node_ID(final int AD_WF_Node_ID) {
        if (AD_WF_Node_ID < 1) {
            this.set_ValueNoCheck("AD_WF_Node_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("AD_WF_Node_ID", (Object)AD_WF_Node_ID);
        }
    }
    
    public int getAD_WF_Node_ID() {
        final Integer ii = (Integer)this.get_Value("AD_WF_Node_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_WF_Node_Asset_ID(final int PP_WF_Node_Asset_ID) {
        if (PP_WF_Node_Asset_ID < 1) {
            this.set_ValueNoCheck("PP_WF_Node_Asset_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_WF_Node_Asset_ID", (Object)PP_WF_Node_Asset_ID);
        }
    }
    
    public int getPP_WF_Node_Asset_ID() {
        final Integer ii = (Integer)this.get_Value("PP_WF_Node_Asset_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_WF_Node_Asset_UU(final String PP_WF_Node_Asset_UU) {
        this.set_Value("PP_WF_Node_Asset_UU", (Object)PP_WF_Node_Asset_UU);
    }
    
    public String getPP_WF_Node_Asset_UU() {
        return (String)this.get_Value("PP_WF_Node_Asset_UU");
    }
    
    public void setSeqNo(final int SeqNo) {
        this.set_ValueNoCheck("SeqNo", (Object)SeqNo);
    }
    
    public int getSeqNo() {
        final Integer ii = (Integer)this.get_Value("SeqNo");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
}
