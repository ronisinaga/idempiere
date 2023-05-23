// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_CostType;
import org.compiere.model.I_M_CostElement;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_C_AcctSchema;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_Cost extends PO implements I_PP_Order_Cost, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int COSTINGMETHOD_AD_Reference_ID = 122;
    public static final String COSTINGMETHOD_StandardCosting = "S";
    public static final String COSTINGMETHOD_AveragePO = "A";
    public static final String COSTINGMETHOD_Lifo = "L";
    public static final String COSTINGMETHOD_Fifo = "F";
    public static final String COSTINGMETHOD_LastPOPrice = "p";
    public static final String COSTINGMETHOD_AverageInvoice = "I";
    public static final String COSTINGMETHOD_LastInvoice = "i";
    public static final String COSTINGMETHOD_UserDefined = "U";
    public static final String COSTINGMETHOD__ = "x";
    
    public X_PP_Order_Cost(final Properties ctx, final int PP_Order_Cost_ID, final String trxName) {
        super(ctx, PP_Order_Cost_ID, trxName);
    }
    
    public X_PP_Order_Cost(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_Cost.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53024, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_Cost[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public I_AD_Workflow getAD_Workflow() throws RuntimeException {
        return (I_AD_Workflow)MTable.get(this.getCtx(), "AD_Workflow").getPO(this.getAD_Workflow_ID(), this.get_TrxName());
    }
    
    public void setAD_Workflow_ID(final int AD_Workflow_ID) {
        if (AD_Workflow_ID < 1) {
            this.set_Value("AD_Workflow_ID", (Object)null);
        }
        else {
            this.set_Value("AD_Workflow_ID", (Object)AD_Workflow_ID);
        }
    }
    
    public int getAD_Workflow_ID() {
        final Integer ii = (Integer)this.get_Value("AD_Workflow_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_AcctSchema getC_AcctSchema() throws RuntimeException {
        return (I_C_AcctSchema)MTable.get(this.getCtx(), "C_AcctSchema").getPO(this.getC_AcctSchema_ID(), this.get_TrxName());
    }
    
    public void setC_AcctSchema_ID(final int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) {
            this.set_Value("C_AcctSchema_ID", (Object)null);
        }
        else {
            this.set_Value("C_AcctSchema_ID", (Object)C_AcctSchema_ID);
        }
    }
    
    public int getC_AcctSchema_ID() {
        final Integer ii = (Integer)this.get_Value("C_AcctSchema_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setCostingMethod(final String CostingMethod) {
        this.set_ValueNoCheck("CostingMethod", (Object)CostingMethod);
    }
    
    public String getCostingMethod() {
        return (String)this.get_Value("CostingMethod");
    }
    
    public void setCumulatedAmt(final BigDecimal CumulatedAmt) {
        this.set_ValueNoCheck("CumulatedAmt", (Object)CumulatedAmt);
    }
    
    public BigDecimal getCumulatedAmt() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CumulatedAmt");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCumulatedAmtPost(final BigDecimal CumulatedAmtPost) {
        this.set_ValueNoCheck("CumulatedAmtPost", (Object)CumulatedAmtPost);
    }
    
    public BigDecimal getCumulatedAmtPost() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CumulatedAmtPost");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCumulatedQty(final BigDecimal CumulatedQty) {
        this.set_ValueNoCheck("CumulatedQty", (Object)CumulatedQty);
    }
    
    public BigDecimal getCumulatedQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CumulatedQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCumulatedQtyPost(final BigDecimal CumulatedQtyPost) {
        this.set_ValueNoCheck("CumulatedQtyPost", (Object)CumulatedQtyPost);
    }
    
    public BigDecimal getCumulatedQtyPost() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CumulatedQtyPost");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCurrentCostPrice(final BigDecimal CurrentCostPrice) {
        this.set_ValueNoCheck("CurrentCostPrice", (Object)CurrentCostPrice);
    }
    
    public BigDecimal getCurrentCostPrice() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CurrentCostPrice");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCurrentCostPriceLL(final BigDecimal CurrentCostPriceLL) {
        this.set_ValueNoCheck("CurrentCostPriceLL", (Object)CurrentCostPriceLL);
    }
    
    public BigDecimal getCurrentCostPriceLL() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CurrentCostPriceLL");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCurrentQty(final BigDecimal CurrentQty) {
        this.set_Value("CurrentQty", (Object)CurrentQty);
    }
    
    public BigDecimal getCurrentQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CurrentQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
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
    
    public I_M_CostElement getM_CostElement() throws RuntimeException {
        return (I_M_CostElement)MTable.get(this.getCtx(), "M_CostElement").getPO(this.getM_CostElement_ID(), this.get_TrxName());
    }
    
    public void setM_CostElement_ID(final int M_CostElement_ID) {
        if (M_CostElement_ID < 1) {
            this.set_ValueNoCheck("M_CostElement_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("M_CostElement_ID", (Object)M_CostElement_ID);
        }
    }
    
    public int getM_CostElement_ID() {
        final Integer ii = (Integer)this.get_Value("M_CostElement_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_CostType getM_CostType() throws RuntimeException {
        return (I_M_CostType)MTable.get(this.getCtx(), "M_CostType").getPO(this.getM_CostType_ID(), this.get_TrxName());
    }
    
    public void setM_CostType_ID(final int M_CostType_ID) {
        if (M_CostType_ID < 1) {
            this.set_Value("M_CostType_ID", (Object)null);
        }
        else {
            this.set_Value("M_CostType_ID", (Object)M_CostType_ID);
        }
    }
    
    public int getM_CostType_ID() {
        final Integer ii = (Integer)this.get_Value("M_CostType_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_Product getM_Product() throws RuntimeException {
        return (I_M_Product)MTable.get(this.getCtx(), "M_Product").getPO(this.getM_Product_ID(), this.get_TrxName());
    }
    
    public void setM_Product_ID(final int M_Product_ID) {
        if (M_Product_ID < 1) {
            this.set_ValueNoCheck("M_Product_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("M_Product_ID", (Object)M_Product_ID);
        }
    }
    
    public int getM_Product_ID() {
        final Integer ii = (Integer)this.get_Value("M_Product_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_Cost_ID(final int PP_Order_Cost_ID) {
        if (PP_Order_Cost_ID < 1) {
            this.set_ValueNoCheck("PP_Order_Cost_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_Cost_ID", (Object)PP_Order_Cost_ID);
        }
    }
    
    public int getPP_Order_Cost_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_Cost_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_Cost_UU(final String PP_Order_Cost_UU) {
        this.set_Value("PP_Order_Cost_UU", (Object)PP_Order_Cost_UU);
    }
    
    public String getPP_Order_Cost_UU() {
        return (String)this.get_Value("PP_Order_Cost_UU");
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
}
