// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Product_BOMLine;
import org.eevolution.model.I_PP_Product_BOM;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_CostType;
import org.compiere.model.I_M_CostElement;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_C_AcctSchema;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_PInstance;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_T_BOMLine extends PO implements I_T_BOMLine, I_Persistent
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
    
    public X_T_BOMLine(final Properties ctx, final int T_BOMLine_ID, final String trxName) {
        super(ctx, T_BOMLine_ID, trxName);
    }
    
    public X_T_BOMLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_T_BOMLine.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53045, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_T_BOMLine[").append(this.get_ID()).append("]");
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
    
    public void setCost(final BigDecimal Cost) {
        this.set_Value("Cost", (Object)Cost);
    }
    
    public BigDecimal getCost() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Cost");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCostingMethod(final String CostingMethod) {
        this.set_Value("CostingMethod", (Object)CostingMethod);
    }
    
    public String getCostingMethod() {
        return (String)this.get_Value("CostingMethod");
    }
    
    public void setCostStandard(final BigDecimal CostStandard) {
        this.set_Value("CostStandard", (Object)CostStandard);
    }
    
    public BigDecimal getCostStandard() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CostStandard");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCurrentCostPrice(final BigDecimal CurrentCostPrice) {
        this.set_Value("CurrentCostPrice", (Object)CurrentCostPrice);
    }
    
    public BigDecimal getCurrentCostPrice() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CurrentCostPrice");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setCurrentCostPriceLL(final BigDecimal CurrentCostPriceLL) {
        this.set_Value("CurrentCostPriceLL", (Object)CurrentCostPriceLL);
    }
    
    public BigDecimal getCurrentCostPriceLL() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CurrentCostPriceLL");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setFutureCostPrice(final BigDecimal FutureCostPrice) {
        this.set_Value("FutureCostPrice", (Object)FutureCostPrice);
    }
    
    public BigDecimal getFutureCostPrice() {
        final BigDecimal bd = (BigDecimal)this.get_Value("FutureCostPrice");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setFutureCostPriceLL(final BigDecimal FutureCostPriceLL) {
        this.set_Value("FutureCostPriceLL", (Object)FutureCostPriceLL);
    }
    
    public BigDecimal getFutureCostPriceLL() {
        final BigDecimal bd = (BigDecimal)this.get_Value("FutureCostPriceLL");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setImplosion(final boolean Implosion) {
        this.set_Value("Implosion", (Object)Implosion);
    }
    
    public boolean isImplosion() {
        final Object oo = this.get_Value("Implosion");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsCostFrozen(final boolean IsCostFrozen) {
        this.set_Value("IsCostFrozen", (Object)IsCostFrozen);
    }
    
    public boolean isCostFrozen() {
        final Object oo = this.get_Value("IsCostFrozen");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setLevelNo(final int LevelNo) {
        this.set_Value("LevelNo", (Object)LevelNo);
    }
    
    public int getLevelNo() {
        final Integer ii = (Integer)this.get_Value("LevelNo");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setLevels(final String Levels) {
        this.set_Value("Levels", (Object)Levels);
    }
    
    public String getLevels() {
        return (String)this.get_Value("Levels");
    }
    
    public I_M_CostElement getM_CostElement() throws RuntimeException {
        return (I_M_CostElement)MTable.get(this.getCtx(), "M_CostElement").getPO(this.getM_CostElement_ID(), this.get_TrxName());
    }
    
    public void setM_CostElement_ID(final int M_CostElement_ID) {
        if (M_CostElement_ID < 1) {
            this.set_Value("M_CostElement_ID", (Object)null);
        }
        else {
            this.set_Value("M_CostElement_ID", (Object)M_CostElement_ID);
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
    
    public I_PP_Product_BOM getPP_Product_BOM() throws RuntimeException {
        return (I_PP_Product_BOM)MTable.get(this.getCtx(), "PP_Product_BOM").getPO(this.getPP_Product_BOM_ID(), this.get_TrxName());
    }
    
    public void setPP_Product_BOM_ID(final int PP_Product_BOM_ID) {
        if (PP_Product_BOM_ID < 1) {
            this.set_Value("PP_Product_BOM_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Product_BOM_ID", (Object)PP_Product_BOM_ID);
        }
    }
    
    public int getPP_Product_BOM_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Product_BOM_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_PP_Product_BOMLine getPP_Product_BOMLine() throws RuntimeException {
        return (I_PP_Product_BOMLine)MTable.get(this.getCtx(), "PP_Product_BOMLine").getPO(this.getPP_Product_BOMLine_ID(), this.get_TrxName());
    }
    
    public void setPP_Product_BOMLine_ID(final int PP_Product_BOMLine_ID) {
        if (PP_Product_BOMLine_ID < 1) {
            this.set_Value("PP_Product_BOMLine_ID", (Object)null);
        }
        else {
            this.set_Value("PP_Product_BOMLine_ID", (Object)PP_Product_BOMLine_ID);
        }
    }
    
    public int getPP_Product_BOMLine_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Product_BOMLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setQtyBOM(final BigDecimal QtyBOM) {
        this.set_Value("QtyBOM", (Object)QtyBOM);
    }
    
    public BigDecimal getQtyBOM() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyBOM");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setSel_Product_ID(final int Sel_Product_ID) {
        if (Sel_Product_ID < 1) {
            this.set_Value("Sel_Product_ID", (Object)null);
        }
        else {
            this.set_Value("Sel_Product_ID", (Object)Sel_Product_ID);
        }
    }
    
    public int getSel_Product_ID() {
        final Integer ii = (Integer)this.get_Value("Sel_Product_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public void setT_BOMLine_ID(final int T_BOMLine_ID) {
        if (T_BOMLine_ID < 1) {
            this.set_ValueNoCheck("T_BOMLine_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("T_BOMLine_ID", (Object)T_BOMLine_ID);
        }
    }
    
    public int getT_BOMLine_ID() {
        final Integer ii = (Integer)this.get_Value("T_BOMLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setT_BOMLine_UU(final String T_BOMLine_UU) {
        this.set_Value("T_BOMLine_UU", (Object)T_BOMLine_UU);
    }
    
    public String getT_BOMLine_UU() {
        return (String)this.get_Value("T_BOMLine_UU");
    }
}
