// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_ChangeNotice;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.I_C_UOM;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order_BOM
{
    public static final String Table_Name = "PP_Order_BOM";
    public static final int Table_ID = 53026;
    public static final KeyNamePair Model = new KeyNamePair(53026, "PP_Order_BOM");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_BOMType = "BOMType";
    public static final String COLUMNNAME_BOMUse = "BOMUse";
    public static final String COLUMNNAME_CopyFrom = "CopyFrom";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";
    public static final String COLUMNNAME_Help = "Help";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";
    public static final String COLUMNNAME_M_ChangeNotice_ID = "M_ChangeNotice_ID";
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";
    public static final String COLUMNNAME_Name = "Name";
    public static final String COLUMNNAME_PP_Order_BOM_ID = "PP_Order_BOM_ID";
    public static final String COLUMNNAME_PP_Order_BOM_UU = "PP_Order_BOM_UU";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_Processing = "Processing";
    public static final String COLUMNNAME_Revision = "Revision";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";
    public static final String COLUMNNAME_ValidTo = "ValidTo";
    public static final String COLUMNNAME_Value = "Value";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setBOMType(final String p0);
    
    String getBOMType();
    
    void setBOMUse(final String p0);
    
    String getBOMUse();
    
    void setCopyFrom(final String p0);
    
    String getCopyFrom();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setC_UOM_ID(final int p0);
    
    int getC_UOM_ID();
    
    I_C_UOM getC_UOM() throws RuntimeException;
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setDocumentNo(final String p0);
    
    String getDocumentNo();
    
    void setHelp(final String p0);
    
    String getHelp();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_AttributeSetInstance_ID(final int p0);
    
    int getM_AttributeSetInstance_ID();
    
    I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;
    
    void setM_ChangeNotice_ID(final int p0);
    
    int getM_ChangeNotice_ID();
    
    I_M_ChangeNotice getM_ChangeNotice() throws RuntimeException;
    
    void setM_Product_ID(final int p0);
    
    int getM_Product_ID();
    
    I_M_Product getM_Product() throws RuntimeException;
    
    void setName(final String p0);
    
    String getName();
    
    void setPP_Order_BOM_ID(final int p0);
    
    int getPP_Order_BOM_ID();
    
    void setPP_Order_BOM_UU(final String p0);
    
    String getPP_Order_BOM_UU();
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setProcessing(final boolean p0);
    
    boolean isProcessing();
    
    void setRevision(final String p0);
    
    String getRevision();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
    
    void setValidFrom(final Timestamp p0);
    
    Timestamp getValidFrom();
    
    void setValidTo(final Timestamp p0);
    
    Timestamp getValidTo();
    
    void setValue(final String p0);
    
    String getValue();
}
