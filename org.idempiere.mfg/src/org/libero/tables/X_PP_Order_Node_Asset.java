// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.MTable;
import org.compiere.model.I_A_Asset;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_Node_Asset extends PO implements I_PP_Order_Node_Asset, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_PP_Order_Node_Asset(final Properties ctx, final int PP_Order_Node_Asset_ID, final String trxName) {
        super(ctx, PP_Order_Node_Asset_ID, trxName);
    }
    
    public X_PP_Order_Node_Asset(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_Node_Asset.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53031, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_Node_Asset[").append(this.get_ID()).append("]");
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
    
    public I_PP_Order getPP_Order() throws RuntimeException {
        return (I_PP_Order)MTable.get(this.getCtx(), "PP_Order").getPO(this.getPP_Order_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_ID(final int PP_Order_ID) {
        if (PP_Order_ID < 1) {
            this.set_ValueNoCheck("PP_Order_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_ID", (Object)PP_Order_ID);
        }
    }
    
    public int getPP_Order_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_Node_Asset_ID(final int PP_Order_Node_Asset_ID) {
        if (PP_Order_Node_Asset_ID < 1) {
            this.set_ValueNoCheck("PP_Order_Node_Asset_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_Node_Asset_ID", (Object)PP_Order_Node_Asset_ID);
        }
    }
    
    public int getPP_Order_Node_Asset_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Node_Asset_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_Node_Asset_UU(final String PP_Order_Node_Asset_UU) {
        this.set_Value("PP_Order_Node_Asset_UU", (Object)PP_Order_Node_Asset_UU);
    }
    
    public String getPP_Order_Node_Asset_UU() {
        return (String)this.get_Value("PP_Order_Node_Asset_UU");
    }
    
    public I_PP_Order_Node getPP_Order_Node() throws RuntimeException {
        return (I_PP_Order_Node)MTable.get(this.getCtx(), "PP_Order_Node").getPO(this.getPP_Order_Node_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_Node_ID(final int PP_Order_Node_ID) {
        if (PP_Order_Node_ID < 1) {
            this.set_ValueNoCheck("PP_Order_Node_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_Node_ID", (Object)PP_Order_Node_ID);
        }
    }
    
    public int getPP_Order_Node_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Node_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_PP_Order_Workflow getPP_Order_Workflow() throws RuntimeException {
        return (I_PP_Order_Workflow)MTable.get(this.getCtx(), "PP_Order_Workflow").getPO(this.getPP_Order_Workflow_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_Workflow_ID(final int PP_Order_Workflow_ID) {
        if (PP_Order_Workflow_ID < 1) {
            this.set_ValueNoCheck("PP_Order_Workflow_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_Workflow_ID", (Object)PP_Order_Workflow_ID);
        }
    }
    
    public int getPP_Order_Workflow_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Workflow_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
}
