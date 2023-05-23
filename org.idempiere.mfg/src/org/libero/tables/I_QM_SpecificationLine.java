// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_M_Attribute;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_QM_SpecificationLine
{
    public static final String Table_Name = "QM_SpecificationLine";
    public static final int Table_ID = 53041;
    public static final KeyNamePair Model = new KeyNamePair(53041, "QM_SpecificationLine");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AndOr = "AndOr";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_Attribute_ID = "M_Attribute_ID";
    public static final String COLUMNNAME_Operation = "Operation";
    public static final String COLUMNNAME_QM_Specification_ID = "QM_Specification_ID";
    public static final String COLUMNNAME_QM_SpecificationLine_ID = "QM_SpecificationLine_ID";
    public static final String COLUMNNAME_QM_SpecificationLine_UU = "QM_SpecificationLine_UU";
    public static final String COLUMNNAME_SeqNo = "SeqNo";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";
    public static final String COLUMNNAME_ValidTo = "ValidTo";
    public static final String COLUMNNAME_Value = "Value";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAndOr(final String p0);
    
    String getAndOr();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_Attribute_ID(final int p0);
    
    int getM_Attribute_ID();
    
    I_M_Attribute getM_Attribute() throws RuntimeException;
    
    void setOperation(final String p0);
    
    String getOperation();
    
    void setQM_Specification_ID(final int p0);
    
    int getQM_Specification_ID();
    
    I_QM_Specification getQM_Specification() throws RuntimeException;
    
    void setQM_SpecificationLine_ID(final int p0);
    
    int getQM_SpecificationLine_ID();
    
    void setQM_SpecificationLine_UU(final String p0);
    
    String getQM_SpecificationLine_UU();
    
    void setSeqNo(final int p0);
    
    int getSeqNo();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValidFrom(final String p0);
    
    String getValidFrom();
    
    void setValidTo(final Timestamp p0);
    
    Timestamp getValidTo();
    
    void setValue(final String p0);
    
    String getValue();
}
