// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import java.sql.Timestamp;
import org.compiere.model.MTable;
import org.compiere.model.I_M_Attribute;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_QM_SpecificationLine extends PO implements I_QM_SpecificationLine, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    public static final int ANDOR_AD_Reference_ID = 204;
    public static final String ANDOR_And = "A";
    public static final String ANDOR_Or = "O";
    public static final int OPERATION_AD_Reference_ID = 205;
    public static final String OPERATION_Eq = "==";
    public static final String OPERATION_GtEq = ">=";
    public static final String OPERATION_Gt = ">>";
    public static final String OPERATION_Le = "<<";
    public static final String OPERATION_Like = "~~";
    public static final String OPERATION_LeEq = "<=";
    public static final String OPERATION_X = "AB";
    public static final String OPERATION_Sql = "SQ";
    public static final String OPERATION_NotEq = "!=";
    
    public X_QM_SpecificationLine(final Properties ctx, final int QM_SpecificationLine_ID, final String trxName) {
        super(ctx, QM_SpecificationLine_ID, trxName);
    }
    
    public X_QM_SpecificationLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_QM_SpecificationLine.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53041, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_QM_SpecificationLine[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setAndOr(final String AndOr) {
        this.set_Value("AndOr", (Object)AndOr);
    }
    
    public String getAndOr() {
        return (String)this.get_Value("AndOr");
    }
    
    public I_M_Attribute getM_Attribute() throws RuntimeException {
        return (I_M_Attribute)MTable.get(this.getCtx(), "M_Attribute").getPO(this.getM_Attribute_ID(), this.get_TrxName());
    }
    
    public void setM_Attribute_ID(final int M_Attribute_ID) {
        if (M_Attribute_ID < 1) {
            this.set_Value("M_Attribute_ID", (Object)null);
        }
        else {
            this.set_Value("M_Attribute_ID", (Object)M_Attribute_ID);
        }
    }
    
    public int getM_Attribute_ID() {
        final Integer ii = (Integer)this.get_Value("M_Attribute_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setOperation(final String Operation) {
        this.set_Value("Operation", (Object)Operation);
    }
    
    public String getOperation() {
        return (String)this.get_Value("Operation");
    }
    
    public I_QM_Specification getQM_Specification() throws RuntimeException {
        return (I_QM_Specification)MTable.get(this.getCtx(), "QM_Specification").getPO(this.getQM_Specification_ID(), this.get_TrxName());
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
    
    public void setQM_SpecificationLine_ID(final int QM_SpecificationLine_ID) {
        if (QM_SpecificationLine_ID < 1) {
            this.set_ValueNoCheck("QM_SpecificationLine_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("QM_SpecificationLine_ID", (Object)QM_SpecificationLine_ID);
        }
    }
    
    public int getQM_SpecificationLine_ID() {
        final Integer ii = (Integer)this.get_Value("QM_SpecificationLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setQM_SpecificationLine_UU(final String QM_SpecificationLine_UU) {
        this.set_Value("QM_SpecificationLine_UU", (Object)QM_SpecificationLine_UU);
    }
    
    public String getQM_SpecificationLine_UU() {
        return (String)this.get_Value("QM_SpecificationLine_UU");
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
    
    public void setValidFrom(final String ValidFrom) {
        this.set_Value("ValidFrom", (Object)ValidFrom);
    }
    
    public String getValidFrom() {
        return (String)this.get_Value("ValidFrom");
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
