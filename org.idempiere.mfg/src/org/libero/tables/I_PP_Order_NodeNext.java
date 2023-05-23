// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.eevolution.model.I_PP_Order;
import java.sql.Timestamp;
import org.compiere.model.I_AD_WF_Node;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;

public interface I_PP_Order_NodeNext
{
    public static final String Table_Name = "PP_Order_NodeNext";
    public static final int Table_ID = 53023;
    public static final KeyNamePair Model = new KeyNamePair(53023, "PP_Order_NodeNext");
    public static final BigDecimal accessLevel = BigDecimal.valueOf(3L);
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
    public static final String COLUMNNAME_AD_WF_Next_ID = "AD_WF_Next_ID";
    public static final String COLUMNNAME_AD_WF_Node_ID = "AD_WF_Node_ID";
    public static final String COLUMNNAME_Created = "Created";
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";
    public static final String COLUMNNAME_Description = "Description";
    public static final String COLUMNNAME_EntityType = "EntityType";
    public static final String COLUMNNAME_IsActive = "IsActive";
    public static final String COLUMNNAME_IsStdUserWorkflow = "IsStdUserWorkflow";
    public static final String COLUMNNAME_PP_Order_ID = "PP_Order_ID";
    public static final String COLUMNNAME_PP_Order_Next_ID = "PP_Order_Next_ID";
    public static final String COLUMNNAME_PP_Order_Node_ID = "PP_Order_Node_ID";
    public static final String COLUMNNAME_PP_Order_NodeNext_ID = "PP_Order_NodeNext_ID";
    public static final String COLUMNNAME_PP_Order_NodeNext_UU = "PP_Order_NodeNext_UU";
    public static final String COLUMNNAME_SeqNo = "SeqNo";
    public static final String COLUMNNAME_TransitionCode = "TransitionCode";
    public static final String COLUMNNAME_Updated = "Updated";
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
    
    int getAD_Client_ID();
    
    void setAD_Org_ID(final int p0);
    
    int getAD_Org_ID();
    
    void setAD_WF_Next_ID(final int p0);
    
    int getAD_WF_Next_ID();
    
    I_AD_WF_Node getAD_WF_Next() throws RuntimeException;
    
    void setAD_WF_Node_ID(final int p0);
    
    int getAD_WF_Node_ID();
    
    I_AD_WF_Node getAD_WF_Node() throws RuntimeException;
    
    Timestamp getCreated();
    
    int getCreatedBy();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setEntityType(final String p0);
    
    String getEntityType();
    
    void setIsActive(final boolean p0);
    
    boolean isActive();
    
    void setIsStdUserWorkflow(final boolean p0);
    
    boolean isStdUserWorkflow();
    
    void setPP_Order_ID(final int p0);
    
    int getPP_Order_ID();
    
    I_PP_Order getPP_Order() throws RuntimeException;
    
    void setPP_Order_Next_ID(final int p0);
    
    int getPP_Order_Next_ID();
    
    I_PP_Order_Node getPP_Order_Next() throws RuntimeException;
    
    void setPP_Order_Node_ID(final int p0);
    
    int getPP_Order_Node_ID();
    
    I_PP_Order_Node getPP_Order_Node() throws RuntimeException;
    
    void setPP_Order_NodeNext_ID(final int p0);
    
    int getPP_Order_NodeNext_ID();
    
    void setPP_Order_NodeNext_UU(final String p0);
    
    String getPP_Order_NodeNext_UU();
    
    void setSeqNo(final int p0);
    
    int getSeqNo();
    
    void setTransitionCode(final String p0);
    
    String getTransitionCode();
    
    Timestamp getUpdated();
    
    int getUpdatedBy();
}
