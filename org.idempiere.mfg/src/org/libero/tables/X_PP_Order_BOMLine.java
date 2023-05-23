// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Warehouse;
import org.compiere.util.KeyNamePair;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_ChangeNotice;
import org.compiere.model.I_M_AttributeSetInstance;
import java.sql.Timestamp;
import org.compiere.model.I_C_UOM;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_User;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_BOMLine extends PO implements I_PP_Order_BOMLine, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int COMPONENTTYPE_AD_Reference_ID = 53225;
    public static final String COMPONENTTYPE_By_Product = "BY";
    public static final String COMPONENTTYPE_Component = "CO";
    public static final String COMPONENTTYPE_Phantom = "PH";
    public static final String COMPONENTTYPE_Packing = "PK";
    public static final String COMPONENTTYPE_Planning = "PL";
    public static final String COMPONENTTYPE_Tools = "TL";
    public static final String COMPONENTTYPE_Option = "OP";
    public static final String COMPONENTTYPE_Variant = "VA";
    public static final String COMPONENTTYPE_Co_Product = "CP";
    public static final int ISSUEMETHOD_AD_Reference_ID = 53226;
    public static final String ISSUEMETHOD_Issue = "0";
    public static final String ISSUEMETHOD_Backflush = "1";
    public static final String ISSUEMETHOD_FloorStock = "2";
    
    public X_PP_Order_BOMLine(final Properties ctx, final int PP_Order_BOMLine_ID, final String trxName) {
        super(ctx, PP_Order_BOMLine_ID, trxName);
    }
    
    public X_PP_Order_BOMLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_BOMLine.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53025, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_BOMLine[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public I_AD_User getAD_User() throws RuntimeException {
        return (I_AD_User)MTable.get(this.getCtx(), "AD_User").getPO(this.getAD_User_ID(), this.get_TrxName());
    }
    
    public void setAD_User_ID(final int AD_User_ID) {
        if (AD_User_ID < 1) {
            this.set_Value("AD_User_ID", (Object)null);
        }
        else {
            this.set_Value("AD_User_ID", (Object)AD_User_ID);
        }
    }
    
    public int getAD_User_ID() {
        final Integer ii = (Integer)this.get_Value("AD_User_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setAssay(final BigDecimal Assay) {
        this.set_ValueNoCheck("Assay", (Object)Assay);
    }
    
    public BigDecimal getAssay() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Assay");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setBackflushGroup(final String BackflushGroup) {
        this.set_ValueNoCheck("BackflushGroup", (Object)BackflushGroup);
    }
    
    public String getBackflushGroup() {
        return (String)this.get_Value("BackflushGroup");
    }
    
    public void setComponentType(final String ComponentType) {
        this.set_Value("ComponentType", (Object)ComponentType);
    }
    
    public String getComponentType() {
        return (String)this.get_Value("ComponentType");
    }
    
    public void setCostAllocationPerc(final BigDecimal CostAllocationPerc) {
        this.set_Value("CostAllocationPerc", (Object)CostAllocationPerc);
    }
    
    public BigDecimal getCostAllocationPerc() {
        final BigDecimal bd = (BigDecimal)this.get_Value("CostAllocationPerc");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public I_C_UOM getC_UOM() throws RuntimeException {
        return (I_C_UOM)MTable.get(this.getCtx(), "C_UOM").getPO(this.getC_UOM_ID(), this.get_TrxName());
    }
    
    public void setC_UOM_ID(final int C_UOM_ID) {
        if (C_UOM_ID < 1) {
            this.set_ValueNoCheck("C_UOM_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("C_UOM_ID", (Object)C_UOM_ID);
        }
    }
    
    public int getC_UOM_ID() {
        final Integer ii = (Integer)this.get_Value("C_UOM_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDateDelivered(final Timestamp DateDelivered) {
        this.set_Value("DateDelivered", (Object)DateDelivered);
    }
    
    public Timestamp getDateDelivered() {
        return (Timestamp)this.get_Value("DateDelivered");
    }
    
    public void setDescription(final String Description) {
        this.set_Value("Description", (Object)Description);
    }
    
    public String getDescription() {
        return (String)this.get_Value("Description");
    }
    
    public void setFeature(final String Feature) {
        this.set_Value("Feature", (Object)Feature);
    }
    
    public String getFeature() {
        return (String)this.get_Value("Feature");
    }
    
    public void setForecast(final BigDecimal Forecast) {
        this.set_ValueNoCheck("Forecast", (Object)Forecast);
    }
    
    public BigDecimal getForecast() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Forecast");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setHelp(final String Help) {
        this.set_Value("Help", (Object)Help);
    }
    
    public String getHelp() {
        return (String)this.get_Value("Help");
    }
    
    public void setIsCritical(final boolean IsCritical) {
        this.set_Value("IsCritical", (Object)IsCritical);
    }
    
    public boolean isCritical() {
        final Object oo = this.get_Value("IsCritical");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsQtyPercentage(final boolean IsQtyPercentage) {
        this.set_ValueNoCheck("IsQtyPercentage", (Object)IsQtyPercentage);
    }
    
    public boolean isQtyPercentage() {
        final Object oo = this.get_Value("IsQtyPercentage");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIssueMethod(final String IssueMethod) {
        this.set_Value("IssueMethod", (Object)IssueMethod);
    }
    
    public String getIssueMethod() {
        return (String)this.get_Value("IssueMethod");
    }
    
    public void setLeadTimeOffset(final int LeadTimeOffset) {
        this.set_Value("LeadTimeOffset", (Object)LeadTimeOffset);
    }
    
    public int getLeadTimeOffset() {
        final Integer ii = (Integer)this.get_Value("LeadTimeOffset");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setLine(final int Line) {
        this.set_Value("Line", (Object)Line);
    }
    
    public int getLine() {
        final Integer ii = (Integer)this.get_Value("Line");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException {
        return (I_M_AttributeSetInstance)MTable.get(this.getCtx(), "M_AttributeSetInstance").getPO(this.getM_AttributeSetInstance_ID(), this.get_TrxName());
    }
    
    public void setM_AttributeSetInstance_ID(final int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0) {
            this.set_ValueNoCheck("M_AttributeSetInstance_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("M_AttributeSetInstance_ID", (Object)M_AttributeSetInstance_ID);
        }
    }
    
    public int getM_AttributeSetInstance_ID() {
        final Integer ii = (Integer)this.get_Value("M_AttributeSetInstance_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_ChangeNotice getM_ChangeNotice() throws RuntimeException {
        return (I_M_ChangeNotice)MTable.get(this.getCtx(), "M_ChangeNotice").getPO(this.getM_ChangeNotice_ID(), this.get_TrxName());
    }
    
    public void setM_ChangeNotice_ID(final int M_ChangeNotice_ID) {
        if (M_ChangeNotice_ID < 1) {
            this.set_Value("M_ChangeNotice_ID", (Object)null);
        }
        else {
            this.set_Value("M_ChangeNotice_ID", (Object)M_ChangeNotice_ID);
        }
    }
    
    public int getM_ChangeNotice_ID() {
        final Integer ii = (Integer)this.get_Value("M_ChangeNotice_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_Locator getM_Locator() throws RuntimeException {
        return (I_M_Locator)MTable.get(this.getCtx(), "M_Locator").getPO(this.getM_Locator_ID(), this.get_TrxName());
    }
    
    public void setM_Locator_ID(final int M_Locator_ID) {
        if (M_Locator_ID < 1) {
            this.set_Value("M_Locator_ID", (Object)null);
        }
        else {
            this.set_Value("M_Locator_ID", (Object)M_Locator_ID);
        }
    }
    
    public int getM_Locator_ID() {
        final Integer ii = (Integer)this.get_Value("M_Locator_ID");
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
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), String.valueOf(this.getM_Product_ID()));
    }
    
    public I_M_Warehouse getM_Warehouse() throws RuntimeException {
        return (I_M_Warehouse)MTable.get(this.getCtx(), "M_Warehouse").getPO(this.getM_Warehouse_ID(), this.get_TrxName());
    }
    
    public void setM_Warehouse_ID(final int M_Warehouse_ID) {
        if (M_Warehouse_ID < 1) {
            this.set_Value("M_Warehouse_ID", (Object)null);
        }
        else {
            this.set_Value("M_Warehouse_ID", (Object)M_Warehouse_ID);
        }
    }
    
    public int getM_Warehouse_ID() {
        final Integer ii = (Integer)this.get_Value("M_Warehouse_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_PP_Order_BOM getPP_Order_BOM() throws RuntimeException {
        return (I_PP_Order_BOM)MTable.get(this.getCtx(), "PP_Order_BOM").getPO(this.getPP_Order_BOM_ID(), this.get_TrxName());
    }
    
    public void setPP_Order_BOM_ID(final int PP_Order_BOM_ID) {
        if (PP_Order_BOM_ID < 1) {
            this.set_ValueNoCheck("PP_Order_BOM_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_BOM_ID", (Object)PP_Order_BOM_ID);
        }
    }
    
    public int getPP_Order_BOM_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_BOM_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_BOMLine_ID(final int PP_Order_BOMLine_ID) {
        if (PP_Order_BOMLine_ID < 1) {
            this.set_ValueNoCheck("PP_Order_BOMLine_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("PP_Order_BOMLine_ID", (Object)PP_Order_BOMLine_ID);
        }
    }
    
    public int getPP_Order_BOMLine_ID() {
        final Integer ii = (Integer)this.get_Value("PP_Order_BOMLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPP_Order_BOMLine_UU(final String PP_Order_BOMLine_UU) {
        this.set_Value("PP_Order_BOMLine_UU", (Object)PP_Order_BOMLine_UU);
    }
    
    public String getPP_Order_BOMLine_UU() {
        return (String)this.get_Value("PP_Order_BOMLine_UU");
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
    
    public void setQtyBatch(final BigDecimal QtyBatch) {
        this.set_ValueNoCheck("QtyBatch", (Object)QtyBatch);
    }
    
    public BigDecimal getQtyBatch() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyBatch");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyBOM(final BigDecimal QtyBOM) {
        this.set_ValueNoCheck("QtyBOM", (Object)QtyBOM);
    }
    
    public BigDecimal getQtyBOM() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyBOM");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyDelivered(final BigDecimal QtyDelivered) {
        this.set_ValueNoCheck("QtyDelivered", (Object)QtyDelivered);
    }
    
    public BigDecimal getQtyDelivered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyDelivered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyEntered(final BigDecimal QtyEntered) {
        this.set_ValueNoCheck("QtyEntered", (Object)QtyEntered);
    }
    
    public BigDecimal getQtyEntered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyEntered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyPost(final BigDecimal QtyPost) {
        this.set_ValueNoCheck("QtyPost", (Object)QtyPost);
    }
    
    public BigDecimal getQtyPost() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyPost");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyReject(final BigDecimal QtyReject) {
        this.set_ValueNoCheck("QtyReject", (Object)QtyReject);
    }
    
    public BigDecimal getQtyReject() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyReject");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyRequired(final BigDecimal QtyRequired) {
        this.set_Value("QtyRequired", (Object)QtyRequired);
    }
    
    public BigDecimal getQtyRequired() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyRequired");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyReserved(final BigDecimal QtyReserved) {
        this.set_ValueNoCheck("QtyReserved", (Object)QtyReserved);
    }
    
    public BigDecimal getQtyReserved() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyReserved");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyScrap(final BigDecimal QtyScrap) {
        this.set_ValueNoCheck("QtyScrap", (Object)QtyScrap);
    }
    
    public BigDecimal getQtyScrap() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyScrap");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setScrap(final BigDecimal Scrap) {
        this.set_ValueNoCheck("Scrap", (Object)Scrap);
    }
    
    public BigDecimal getScrap() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Scrap");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setValidFrom(final Timestamp ValidFrom) {
        this.set_Value("ValidFrom", (Object)ValidFrom);
    }
    
    public Timestamp getValidFrom() {
        return (Timestamp)this.get_Value("ValidFrom");
    }
    
    public void setValidTo(final Timestamp ValidTo) {
        this.set_Value("ValidTo", (Object)ValidTo);
    }
    
    public Timestamp getValidTo() {
        return (Timestamp)this.get_Value("ValidTo");
    }
}
