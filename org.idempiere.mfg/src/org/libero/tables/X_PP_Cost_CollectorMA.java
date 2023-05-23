// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.MTable;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Cost_CollectorMA extends PO implements I_PP_Cost_CollectorMA, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_PP_Cost_CollectorMA(final Properties ctx, final int PP_Cost_CollectorMA_ID, final String trxName) {
        super(ctx, PP_Cost_CollectorMA_ID, trxName);
    }
    
    public X_PP_Cost_CollectorMA(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Cost_CollectorMA.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53062, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Cost_CollectorMA[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException {
        return (I_M_AttributeSetInstance)MTable.get(this.getCtx(), "M_AttributeSetInstance").getPO(this.getM_AttributeSetInstance_ID(), this.get_TrxName());
    }
    
    public void setM_AttributeSetInstance_ID(final int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0) {
            this.set_Value("M_AttributeSetInstance_ID", (Object)null);
        }
        else {
            this.set_Value("M_AttributeSetInstance_ID", (Object)M_AttributeSetInstance_ID);
        }
    }
    
    public int getM_AttributeSetInstance_ID() {
        final Integer ii = (Integer)this.get_Value("M_AttributeSetInstance_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setMovementQty(final BigDecimal MovementQty) {
        this.set_Value("MovementQty", (Object)MovementQty);
    }
    
    public BigDecimal getMovementQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("MovementQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public I_PP_Cost_Collector getPP_Cost_Collector() throws RuntimeException {
        return (I_PP_Cost_Collector)MTable.get(this.getCtx(), "PP_Cost_Collector").getPO(this.getPP_Cost_Collector_ID(), this.get_TrxName());
    }
    
    public void setPP_Cost_Collector_ID(final int PP_Cost_Collector_ID) {
        if (PP_Cost_Collector_ID < 1) {
            this.set_Value("PP_Cost_Collector_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Cost_Collector_ID", (Object)PP_Cost_Collector_ID);
        }
    }
    
    public int getPP_Cost_Collector_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Cost_Collector_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Cost_CollectorMA_ID(final int PP_Cost_CollectorMA_ID) {
        if (PP_Cost_CollectorMA_ID < 1) {
            this.set_ValueNoCheck("PP_Cost_CollectorMA_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Cost_CollectorMA_ID", (Object)PP_Cost_CollectorMA_ID);
        }
    }
    
    public int getPP_Cost_CollectorMA_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Cost_CollectorMA_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Cost_CollectorMA_UU(final String PP_Cost_CollectorMA_UU) {
        this.set_Value("PP_Cost_CollectorMA_UU", (Object)PP_Cost_CollectorMA_UU);
    }
    
    public String getPP_Cost_CollectorMA_UU() {
        return (String)this.get_Value("PP_Cost_CollectorMA_UU");
    }
}
