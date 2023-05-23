// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_M_Product;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_WF_Node_Product extends PO implements I_PP_WF_Node_Product, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int CONFIGURATIONLEVEL_AD_Reference_ID = 53222;
    public static final String CONFIGURATIONLEVEL_System = "S";
    public static final String CONFIGURATIONLEVEL_Client = "C";
    public static final String CONFIGURATIONLEVEL_Organization = "O";
    public static final int ENTITYTYPE_AD_Reference_ID = 389;
    
    public X_PP_WF_Node_Product(final Properties ctx, final int PP_WF_Node_Product_ID, final String trxName) {
        super(ctx, PP_WF_Node_Product_ID, trxName);
    }
    
    public X_PP_WF_Node_Product(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_WF_Node_Product.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53016, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_WF_Node_Product[").append(this.get_ID()).append("]");
        return sb.toString();
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
    
    public void setConfigurationLevel(final String ConfigurationLevel) {
        this.set_Value("ConfigurationLevel", (Object)ConfigurationLevel);
    }
    
    public String getConfigurationLevel() {
        return (String)this.get_Value("ConfigurationLevel");
    }
    
    public void setEntityType(final String EntityType) {
        this.set_Value("EntityType", (Object)EntityType);
    }
    
    public String getEntityType() {
        return (String)this.get_Value("EntityType");
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
    
    public void setPP_WF_Node_Product_ID(final int PP_WF_Node_Product_ID) {
        if (PP_WF_Node_Product_ID < 1) {
            this.set_ValueNoCheck("PP_WF_Node_Product_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_WF_Node_Product_ID", (Object)PP_WF_Node_Product_ID);
        }
    }
    
    public int getPP_WF_Node_Product_ID() {
        final Integer ii = (Integer)this.get_Value("PP_WF_Node_Product_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_WF_Node_Product_UU(final String PP_WF_Node_Product_UU) {
        this.set_Value("PP_WF_Node_Product_UU", (Object)PP_WF_Node_Product_UU);
    }
    
    public String getPP_WF_Node_Product_UU() {
        return (String)this.get_Value("PP_WF_Node_Product_UU");
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
