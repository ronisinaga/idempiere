// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.util.KeyNamePair;
import java.sql.Timestamp;
import org.compiere.model.MTable;
import org.compiere.model.I_M_ChangeNotice;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_DD_NetworkDistribution extends PO implements I_DD_NetworkDistribution, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_DD_NetworkDistribution(final Properties ctx, final int DD_NetworkDistribution_ID, final String trxName) {
        super(ctx, DD_NetworkDistribution_ID, trxName);
    }
    
    public X_DD_NetworkDistribution(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_DD_NetworkDistribution.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53060, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_DD_NetworkDistribution[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setCopyFrom(final String CopyFrom) {
        this.set_Value("CopyFrom", (Object)CopyFrom);
    }
    
    public String getCopyFrom() {
        return (String)this.get_Value("CopyFrom");
    }
    
    public void setDD_NetworkDistribution_ID(final int DD_NetworkDistribution_ID) {
        if (DD_NetworkDistribution_ID < 1) {
            this.set_ValueNoCheck("DD_NetworkDistribution_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("DD_NetworkDistribution_ID", (Object)DD_NetworkDistribution_ID);
        }
    }
    
    public int getDD_NetworkDistribution_ID() {
        final Integer ii = (Integer)this.get_Value("DD_NetworkDistribution_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDD_NetworkDistribution_UU(final String DD_NetworkDistribution_UU) {
        this.set_Value("DD_NetworkDistribution_UU", (Object)DD_NetworkDistribution_UU);
    }
    
    public String getDD_NetworkDistribution_UU() {
        return (String)this.get_Value("DD_NetworkDistribution_UU");
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
    
    public void setName(final String Name) {
        this.set_Value("Name", (Object)Name);
    }
    
    public String getName() {
        return (String)this.get_Value("Name");
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
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), this.getValue());
    }
}
