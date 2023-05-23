// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Product_BOM;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_AttributeSet;
import java.sql.Timestamp;
import org.compiere.model.I_AD_Workflow;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_QM_Specification
{
    public static final String Table_Name = "QM_Specification";
    public static final int Table_ID = 53040;
    public static final KeyNamePair Model = new KeyNamePair(53040, "QM_Specification");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_Workflow_ID = "AD_Workflow_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_AttributeSet_ID = "M_AttributeSet_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_Name = "Name";
    public static final String COLUMNNAME_PP_Product_BOM_ID = "PP_Product_BOM_ID";
    public static final String COLUMNNAME_QM_Specification_ID = "QM_Specification_ID";
    public static final String COLUMNNAME_QM_Specification_UU = "QM_Specification_UU";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";
    public static final String COLUMNNAME_ValidTo = "ValidTo";
    public static final String COLUMNNAME_Value = "Value";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_Workflow_ID(final int p0);
    
    int getAD_Workflow_ID();
    
    I_AD_Workflow getAD_Workflow() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_AttributeSet_ID(final int p0);
    
    int getM_AttributeSet_ID();
    
    I_M_AttributeSet getM_AttributeSet() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setName(final String p0);
    
    String getName();
    
    void setPP_Product_BOM_ID(final int p0);
    
    int getPP_Product_BOM_ID();
    
    I_PP_Product_BOM getPP_Product_BOM() throws RuntimeException;
    
    void setQM_Specification_ID(final int p0);
    
    int getQM_Specification_ID();
    
    void setQM_Specification_UU(final String p0);
    
    String getQM_Specification_UU();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValidFrom(final Timestamp p0);
    
    Timestamp getValidFrom();
    
    void setValidTo(final Timestamp p0);
    
    Timestamp getValidTo();
    
    void setValue(final String p0);
    
    String getValue();
}
