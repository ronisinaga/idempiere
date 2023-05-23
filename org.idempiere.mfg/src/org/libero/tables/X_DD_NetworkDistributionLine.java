// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import java.sql.Timestamp;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_M_Shipper;
import org.compiere.model.MTable;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_DD_NetworkDistributionLine extends PO implements I_DD_NetworkDistributionLine, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_DD_NetworkDistributionLine(final Properties ctx, final int DD_NetworkDistributionLine_ID, final String trxName) {
        super(ctx, DD_NetworkDistributionLine_ID, trxName);
    }
    
    public X_DD_NetworkDistributionLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_DD_NetworkDistributionLine.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53061, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_DD_NetworkDistributionLine[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public I_DD_NetworkDistribution getDD_NetworkDistribution() throws RuntimeException {
        return (I_DD_NetworkDistribution)MTable.get(this.getCtx(), "DD_NetworkDistribution").getPO(this.getDD_NetworkDistribution_ID(), this.get_TrxName());
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
    
    public void setDD_NetworkDistributionLine_ID(final int DD_NetworkDistributionLine_ID) {
        if (DD_NetworkDistributionLine_ID < 1) {
            this.set_ValueNoCheck("DD_NetworkDistributionLine_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("DD_NetworkDistributionLine_ID", (Object)DD_NetworkDistributionLine_ID);
        }
    }
    
    public int getDD_NetworkDistributionLine_ID() {
        final Integer ii = (Integer)this.get_Value("DD_NetworkDistributionLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDD_NetworkDistributionLine_UU(final String DD_NetworkDistributionLine_UU) {
        this.set_Value("DD_NetworkDistributionLine_UU", (Object)DD_NetworkDistributionLine_UU);
    }
    
    public String getDD_NetworkDistributionLine_UU() {
        return (String)this.get_Value("DD_NetworkDistributionLine_UU");
    }
    
    public I_M_Shipper getM_Shipper() throws RuntimeException {
        return (I_M_Shipper)MTable.get(this.getCtx(), "M_Shipper").getPO(this.getM_Shipper_ID(), this.get_TrxName());
    }
    
    public void setM_Shipper_ID(final int M_Shipper_ID) {
        if (M_Shipper_ID < 1) {
            this.set_Value("M_Shipper_ID", (Object)null);
        }
        else {
            this.set_Value("M_Shipper_ID", (Object)M_Shipper_ID);
        }
    }
    
    public int getM_Shipper_ID() {
        final Integer ii = (Integer)this.get_Value("M_Shipper_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
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
    
    public I_M_Warehouse getM_WarehouseSource() throws RuntimeException {
        return (I_M_Warehouse)MTable.get(this.getCtx(), "M_Warehouse").getPO(this.getM_WarehouseSource_ID(), this.get_TrxName());
    }
    
    public void setM_WarehouseSource_ID(final int M_WarehouseSource_ID) {
        if (M_WarehouseSource_ID < 1) {
            this.set_Value("M_WarehouseSource_ID", (Object)null);
        }
        else {
            this.set_Value("M_WarehouseSource_ID", (Object)M_WarehouseSource_ID);
        }
    }
    
    public int getM_WarehouseSource_ID() {
        final Integer ii = (Integer)this.get_Value("M_WarehouseSource_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setPercent(final BigDecimal Percent) {
        this.set_Value("Percent", (Object)Percent);
    }
    
    public BigDecimal getPercent() {
        final BigDecimal bd = (BigDecimal)this.get_Value("Percent");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setPriorityNo(final int PriorityNo) {
        this.set_Value("PriorityNo", (Object)PriorityNo);
    }
    
    public int getPriorityNo() {
        final Integer ii = (Integer)this.get_Value("PriorityNo");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setTransfertTime(final BigDecimal TransfertTime) {
        this.set_Value("TransfertTime", (Object)TransfertTime);
    }
    
    public BigDecimal getTransfertTime() {
        final BigDecimal bd = (BigDecimal)this.get_Value("TransfertTime");
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
