// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import java.sql.Timestamp;
import org.eevolution.model.I_PP_Order;
import org.compiere.util.KeyNamePair;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_ChangeNotice;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.MTable;
import org.compiere.model.I_C_UOM;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_PP_Order_BOM extends PO implements I_PP_Order_BOM, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int BOMTYPE_AD_Reference_ID = 347;
    public static final String BOMTYPE_CurrentActive = "A";
    public static final String BOMTYPE_Make_To_Order = "O";
    public static final String BOMTYPE_Previous = "P";
    public static final String BOMTYPE_PreviousSpare = "S";
    public static final String BOMTYPE_Future = "F";
    public static final String BOMTYPE_Maintenance = "M";
    public static final String BOMTYPE_Repair = "R";
    public static final String BOMTYPE_ProductConfigure = "C";
    public static final String BOMTYPE_Make_To_Kit = "K";
    public static final int BOMUSE_AD_Reference_ID = 348;
    public static final String BOMUSE_Master = "A";
    public static final String BOMUSE_Engineering = "E";
    public static final String BOMUSE_Manufacturing = "M";
    public static final String BOMUSE_Planning = "P";
    public static final String BOMUSE_Quality = "Q";
    
    public X_PP_Order_BOM(final Properties ctx, final int PP_Order_BOM_ID, final String trxName) {
        super(ctx, PP_Order_BOM_ID, trxName);
    }
    
    public X_PP_Order_BOM(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_PP_Order_BOM.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53026, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_PP_Order_BOM[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setBOMType(final String BOMType) {
        this.set_Value("BOMType", (Object)BOMType);
    }
    
    public String getBOMType() {
        return (String)this.get_Value("BOMType");
    }
    
    public void setBOMUse(final String BOMUse) {
        this.set_Value("BOMUse", (Object)BOMUse);
    }
    
    public String getBOMUse() {
        return (String)this.get_Value("BOMUse");
    }
    
    public void setCopyFrom(final String CopyFrom) {
        this.set_Value("CopyFrom", (Object)CopyFrom);
    }
    
    public String getCopyFrom() {
        return (String)this.get_Value("CopyFrom");
    }
    
    public I_C_UOM getC_UOM() throws RuntimeException {
        return (I_C_UOM)MTable.get(this.getCtx(), "C_UOM").getPO(this.getC_UOM_ID(), this.get_TrxName());
    }
    
    public void setC_UOM_ID(final int C_UOM_ID) {
        if (C_UOM_ID < 1) {
            this.set_Value("C_UOM_ID", (Object)null);
        }
        else {
            this.set_Value("C_UOM_ID", (Object)C_UOM_ID);
        }
    }
    
    public int getC_UOM_ID() {
        final Integer ii = (Integer)this.get_Value("C_UOM_ID");
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
    
    public void setDocumentNo(final String DocumentNo) {
        this.set_Value("DocumentNo", (Object)DocumentNo);
    }
    
    public String getDocumentNo() {
        return (String)this.get_Value("DocumentNo");
    }
    
    public void setHelp(final String Help) {
        this.set_Value("Help", (Object)Help);
    }
    
    public String getHelp() {
        return (String)this.get_Value("Help");
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
    
    public void setName(final String Name) {
        this.set_Value("Name", (Object)Name);
    }
    
    public String getName() {
        return (String)this.get_Value("Name");
    }
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), this.getName());
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
    
    public void setPP_Order_BOM_UU(final String PP_Order_BOM_UU) {
        this.set_Value("PP_Order_BOM_UU", (Object)PP_Order_BOM_UU);
    }
    
    public String getPP_Order_BOM_UU() {
        return (String)this.get_Value("PP_Order_BOM_UU");
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
    
    public void setProcessing(final boolean Processing) {
        this.set_Value("Processing", (Object)Processing);
    }
    
    public boolean isProcessing() {
        final Object oo = this.get_Value("Processing");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setRevision(final String Revision) {
        this.set_Value("Revision", (Object)Revision);
    }
    
    public String getRevision() {
        return (String)this.get_Value("Revision");
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
    
    public void setValue(final String Value) {
        this.set_Value("Value", (Object)Value);
    }
    
    public String getValue() {
        return (String)this.get_Value("Value");
    }
}
