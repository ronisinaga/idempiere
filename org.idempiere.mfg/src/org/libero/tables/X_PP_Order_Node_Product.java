// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.util.Env;
import java.math.BigDecimal;
import org.eevolution.model.I_PP_Order;
import org.compiere.model.MTable;
import org.compiere.model.I_M_Product;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_Node_Product extends PO implements I_PP_Order_Node_Product, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_PP_Order_Node_Product(final Properties ctx, final int PP_Order_Node_Product_ID, final String trxName) {
        super(ctx, PP_Order_Node_Product_ID, trxName);
    }
    
    public X_PP_Order_Node_Product(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_Node_Product.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53030, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_Node_Product[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setIsSubcontracting(final boolean IsSubcontracting) {
        this.set_Value("IsSubcontracting", (Object)IsSubcontracting);
    }
    
    public boolean isSubcontracting() {
        final Object oo = this.get_Value("IsSubcontracting");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public I_M_Product getM_Product() throws RuntimeException {
        return (I_M_Product)MTable.get(this.getCtx(), "M_Product").getPO(this.getM_Product_ID(), this.get_TrxName());
    }
    
    public void setM_Product_ID(final int M_Product_ID) {
        if (M_Product_ID < 1) {
            this.set_Value("M_Product_ID", (Object)null);
        }
        else {
            this.set_Value("M_Product_ID", (Object)M_Product_ID);
        }
    }
    
    public int getM_Product_ID() {
        final Integer ii = (Integer)this.get_Value("M_Product_ID");
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
    
    public void setPP_Order_Node_Product_ID(final int PP_Order_Node_Product_ID) {
        if (PP_Order_Node_Product_ID < 1) {
            this.set_ValueNoCheck("PP_Order_Node_Product_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_Node_Product_ID", (Object)PP_Order_Node_Product_ID);
        }
    }
    
    public int getPP_Order_Node_Product_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Node_Product_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_Node_Product_UU(final String PP_Order_Node_Product_UU) {
        this.set_Value("PP_Order_Node_Product_UU", (Object)PP_Order_Node_Product_UU);
    }
    
    public String getPP_Order_Node_Product_UU() {
        return (String)this.get_Value("PP_Order_Node_Product_UU");
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
    
    public void setQty(final BigDecimal Qty) {
        this.set_Value("Qty", (Object)Qty);
    }
    
    public BigDecimal getQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Qty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
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
}
