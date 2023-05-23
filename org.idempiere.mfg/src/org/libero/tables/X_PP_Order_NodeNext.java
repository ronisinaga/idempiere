// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_NodeNext extends PO implements I_PP_Order_NodeNext, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int ENTITYTYPE_AD_Reference_ID = 389;
    
    public X_PP_Order_NodeNext(final Properties ctx, final int PP_Order_NodeNext_ID, final String trxName) {
        super(ctx, PP_Order_NodeNext_ID, trxName);
    }
    
    public X_PP_Order_NodeNext(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_NodeNext.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53023, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_NodeNext[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public I_AD_WF_Node getAD_WF_Next() throws RuntimeException {
        return (I_AD_WF_Node)MTable.get(this.getCtx(), "AD_WF_Node").getPO(this.getAD_WF_Next_ID(), this.get_TrxName());
    }
    
    public void setAD_WF_Next_ID(final int AD_WF_Next_ID) {
        if (AD_WF_Next_ID < 1) {
            this.set_Value("AD_WF_Next_ID", (Object)null);
        }
        else {
            this.set_Value("AD_WF_Next_ID", (Object)AD_WF_Next_ID);
        }
    }
    
    public int getAD_WF_Next_ID() {
        final Integer ii = (Integer)this.get_Value("AD_WF_Next_ID");
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
            this.set_Value("AD_WF_Node_ID", (Object)null);
        }
        else {
            this.set_Value("AD_WF_Node_ID", (Object)AD_WF_Node_ID);
        }
    }
    
    public int getAD_WF_Node_ID() {
        final Integer ii = (Integer)this.get_Value("AD_WF_Node_ID");
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
    
    public void setEntityType(final String EntityType) {
        this.set_Value("EntityType", (Object)EntityType);
    }
    
    public String getEntityType() {
        return (String)this.get_Value("EntityType");
    }
    
    public void setIsStdUserWorkflow(final boolean IsStdUserWorkflow) {
        this.set_Value("IsStdUserWorkflow", (Object)IsStdUserWorkflow);
    }
    
    public boolean isStdUserWorkflow() {
        final Object oo = this.get_Value("IsStdUserWorkflow");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
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
    
    public I_PP_Order_Node getPP_Order_Next() throws RuntimeException {
        return (I_PP_Order_Node)MTable.get(this.getCtx(), "PP_Order_Node").getPO(this.getPP_Order_Next_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_Next_ID(final int PP_Order_Next_ID) {
        if (PP_Order_Next_ID < 1) {
            this.set_Value("PP_Order_Next_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Order_Next_ID", (Object)PP_Order_Next_ID);
        }
    }
    
    public int getPP_Order_Next_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Next_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setPP_Order_NodeNext_ID(final int PP_Order_NodeNext_ID) {
        if (PP_Order_NodeNext_ID < 1) {
            this.set_ValueNoCheck("PP_Order_NodeNext_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_NodeNext_ID", (Object)PP_Order_NodeNext_ID);
        }
    }
    
    public int getPP_Order_NodeNext_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_NodeNext_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_NodeNext_UU(final String PP_Order_NodeNext_UU) {
        this.set_Value("PP_Order_NodeNext_UU", (Object)PP_Order_NodeNext_UU);
    }
    
    public String getPP_Order_NodeNext_UU() {
        return (String)this.get_Value("PP_Order_NodeNext_UU");
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
    
    public void setTransitionCode(final String TransitionCode) {
        this.set_Value("TransitionCode", (Object)TransitionCode);
    }
    
    public String getTransitionCode() {
        return (String)this.get_Value("TransitionCode");
    }
}
