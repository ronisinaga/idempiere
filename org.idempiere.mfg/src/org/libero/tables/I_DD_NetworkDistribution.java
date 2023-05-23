// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_M_ChangeNotice;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_DD_NetworkDistribution
{
    public static final String Table_Name = "DD_NetworkDistribution";
    public static final int Table_ID = 53060;
    public static final KeyNamePair Model = new KeyNamePair(53060, "DD_NetworkDistribution");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_CopyFrom = "CopyFrom";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_DD_NetworkDistribution_ID = "DD_NetworkDistribution_ID";
    public static final String COLUMNNAME_DD_NetworkDistribution_UU = "DD_NetworkDistribution_UU";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";
    public static final String COLUMNNAME_Help = "Help";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_M_ChangeNotice_ID = "M_ChangeNotice_ID";
    public static final String COLUMNNAME_Name = "Name";
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
    
    void setCopyFrom(final String p0);
    
    String getCopyFrom();
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDD_NetworkDistribution_ID(final int p0);
    
    int getDD_NetworkDistribution_ID();
    
    void setDD_NetworkDistribution_UU(final String p0);
    
    String getDD_NetworkDistribution_UU();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setDocumentNo(final String p0);
    
    String getDocumentNo();
    
    void setHelp(final String p0);
    
    String getHelp();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setM_ChangeNotice_ID(final int p0);
    
    int getM_ChangeNotice_ID();
    
    I_M_ChangeNotice getM_ChangeNotice() throws RuntimeException;
    
    void setName(final String p0);
    
    String getName();
    
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
