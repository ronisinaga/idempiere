// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import java.sql.Timestamp;
import org.eevolution.model.I_PP_Product_BOM;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_AttributeSet;
import org.compiere.model.MTable;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_QM_Specification extends PO implements I_QM_Specification, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_QM_Specification(final Properties ctx, final int QM_Specification_ID, final String trxName) {
        super(ctx, QM_Specification_ID, trxName);
    }
    
    public X_QM_Specification(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_QM_Specification.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53040, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_QM_Specification[").append(this.get_ID()).append("]");
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
    
    public void setDescription(final String Description) {
        this.set_Value("Description", (Object)Description);
    }
    
    public String getDescription() {
        return (String)this.get_Value("Description");
    }
    
    public I_M_AttributeSet getM_AttributeSet() throws RuntimeException {
        return (I_M_AttributeSet)MTable.get(this.getCtx(), "M_AttributeSet").getPO(this.getM_AttributeSet_ID(), this.get_TrxName());
    }
    
    public void setM_AttributeSet_ID(final int M_AttributeSet_ID) {
        if (M_AttributeSet_ID < 0) {
            this.set_Value("M_AttributeSet_ID", (Object)null);
        }
        else {
            this.set_Value("M_AttributeSet_ID", (Object)M_AttributeSet_ID);
        }
    }
    
    public int getM_AttributeSet_ID() {
        final Integer ii = (Integer)this.get_Value("M_AttributeSet_ID");
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
    
    public void setQM_Specification_ID(final int QM_Specification_ID) {
        if (QM_Specification_ID < 1) {
            this.set_ValueNoCheck("QM_Specification_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("QM_Specification_ID", (Object)QM_Specification_ID);
        }
    }
    
    public int getQM_Specification_ID() {
        final Integer ii = (Integer)this.get_Value("QM_Specification_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setQM_Specification_UU(final String QM_Specification_UU) {
        this.set_Value("QM_Specification_UU", (Object)QM_Specification_UU);
    }
    
    public String getQM_Specification_UU() {
        return (String)this.get_Value("QM_Specification_UU");
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
