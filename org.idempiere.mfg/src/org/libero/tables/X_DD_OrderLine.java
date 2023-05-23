// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tables;

import org.compiere.model.I_C_ElementValue;
import org.compiere.model.I_M_Shipper;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.util.KeyNamePair;
import java.sql.Timestamp;
import org.compiere.model.I_C_UOM;
import org.compiere.model.I_C_Project;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.compiere.model.I_C_Charge;
import org.compiere.model.I_C_Campaign;
import org.compiere.model.MTable;
import org.compiere.model.I_C_Activity;
import org.compiere.model.POInfo;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.I_Persistent;
import org.compiere.model.PO;

public class X_DD_OrderLine extends PO implements I_DD_OrderLine, I_Persistent
{
    private static final long serialVersionUID = 20130626L;
    
    public X_DD_OrderLine(final Properties ctx, final int DD_OrderLine_ID, final String trxName) {
        super(ctx, DD_OrderLine_ID, trxName);
    }
    
    public X_DD_OrderLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    protected int get_AccessLevel() {
        return X_DD_OrderLine.accessLevel.intValue();
    }
    
    protected POInfo initPO(final Properties ctx) {
        final POInfo poi = POInfo.getPOInfo(ctx, 53038, this.get_TrxName());
        return poi;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("X_DD_OrderLine[").append(this.get_ID()).append("]");
        return sb.toString();
    }
    
    public void setAD_OrgTrx_ID(final int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) {
            this.set_Value("AD_OrgTrx_ID", (Object)null);
        }
        else {
            this.set_Value("AD_OrgTrx_ID", (Object)AD_OrgTrx_ID);
        }
    }
    
    public int getAD_OrgTrx_ID() {
        final Integer ii = (Integer)this.get_Value("AD_OrgTrx_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Activity getC_Activity() throws RuntimeException {
        return (I_C_Activity)MTable.get(this.getCtx(), "C_Activity").getPO(this.getC_Activity_ID(), this.get_TrxName());
    }
    
    public void setC_Activity_ID(final int C_Activity_ID) {
        if (C_Activity_ID < 1) {
            this.set_Value("C_Activity_ID", (Object)null);
        }
        else {
            this.set_Value("C_Activity_ID", (Object)C_Activity_ID);
        }
    }
    
    public int getC_Activity_ID() {
        final Integer ii = (Integer)this.get_Value("C_Activity_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Campaign getC_Campaign() throws RuntimeException {
        return (I_C_Campaign)MTable.get(this.getCtx(), "C_Campaign").getPO(this.getC_Campaign_ID(), this.get_TrxName());
    }
    
    public void setC_Campaign_ID(final int C_Campaign_ID) {
        if (C_Campaign_ID < 1) {
            this.set_Value("C_Campaign_ID", (Object)null);
        }
        else {
            this.set_Value("C_Campaign_ID", (Object)C_Campaign_ID);
        }
    }
    
    public int getC_Campaign_ID() {
        final Integer ii = (Integer)this.get_Value("C_Campaign_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_Charge getC_Charge() throws RuntimeException {
        return (I_C_Charge)MTable.get(this.getCtx(), "C_Charge").getPO(this.getC_Charge_ID(), this.get_TrxName());
    }
    
    public void setC_Charge_ID(final int C_Charge_ID) {
        if (C_Charge_ID < 1) {
            this.set_Value("C_Charge_ID", (Object)null);
        }
        else {
            this.set_Value("C_Charge_ID", (Object)C_Charge_ID);
        }
    }
    
    public int getC_Charge_ID() {
        final Integer ii = (Integer)this.get_Value("C_Charge_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setConfirmedQty(final BigDecimal ConfirmedQty) {
        this.set_Value("ConfirmedQty", (Object)ConfirmedQty);
    }
    
    public BigDecimal getConfirmedQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("ConfirmedQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public I_C_Project getC_Project() throws RuntimeException {
        return (I_C_Project)MTable.get(this.getCtx(), "C_Project").getPO(this.getC_Project_ID(), this.get_TrxName());
    }
    
    public void setC_Project_ID(final int C_Project_ID) {
        if (C_Project_ID < 1) {
            this.set_Value("C_Project_ID", (Object)null);
        }
        else {
            this.set_Value("C_Project_ID", (Object)C_Project_ID);
        }
    }
    
    public int getC_Project_ID() {
        final Integer ii = (Integer)this.get_Value("C_Project_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_UOM getC_UOM() throws RuntimeException {
        return (I_C_UOM)MTable.get(this.getCtx(), "C_UOM").getPO(this.getC_UOM_ID(), this.get_TrxName());
    }
    
    public void setC_UOM_ID(final int C_UOM_ID) {
        if (C_UOM_ID < 1) {
            this.set_ValueNoCheck("C_UOM_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("C_UOM_ID", (Object)C_UOM_ID);
        }
    }
    
    public int getC_UOM_ID() {
        final Integer ii = (Integer)this.get_Value("C_UOM_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDateDelivered(final Timestamp DateDelivered) {
        this.set_Value("DateDelivered", (Object)DateDelivered);
    }
    
    public Timestamp getDateDelivered() {
        return (Timestamp)this.get_Value("DateDelivered");
    }
    
    public void setDateOrdered(final Timestamp DateOrdered) {
        this.set_Value("DateOrdered", (Object)DateOrdered);
    }
    
    public Timestamp getDateOrdered() {
        return (Timestamp)this.get_Value("DateOrdered");
    }
    
    public void setDatePromised(final Timestamp DatePromised) {
        this.set_Value("DatePromised", (Object)DatePromised);
    }
    
    public Timestamp getDatePromised() {
        return (Timestamp)this.get_Value("DatePromised");
    }
    
    public I_DD_Order getDD_Order() throws RuntimeException {
        return (I_DD_Order)MTable.get(this.getCtx(), "DD_Order").getPO(this.getDD_Order_ID(), this.get_TrxName());
    }
    
    public void setDD_Order_ID(final int DD_Order_ID) {
        if (DD_Order_ID < 1) {
            this.set_ValueNoCheck("DD_Order_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("DD_Order_ID", (Object)DD_Order_ID);
        }
    }
    
    public int getDD_Order_ID() {
        final Integer ii = (Integer)this.get_Value("DD_Order_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDD_OrderLine_ID(final int DD_OrderLine_ID) {
        if (DD_OrderLine_ID < 1) {
            this.set_ValueNoCheck("DD_OrderLine_ID", (Object)null);
        }
        else {
            this.set_ValueNoCheck("DD_OrderLine_ID", (Object)DD_OrderLine_ID);
        }
    }
    
    public int getDD_OrderLine_ID() {
        final Integer ii = (Integer)this.get_Value("DD_OrderLine_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public void setDD_OrderLine_UU(final String DD_OrderLine_UU) {
        this.set_Value("DD_OrderLine_UU", (Object)DD_OrderLine_UU);
    }
    
    public String getDD_OrderLine_UU() {
        return (String)this.get_Value("DD_OrderLine_UU");
    }
    
    public void setDescription(final String Description) {
        this.set_Value("Description", (Object)Description);
    }
    
    public String getDescription() {
        return (String)this.get_Value("Description");
    }
    
    public void setFreightAmt(final BigDecimal FreightAmt) {
        this.set_Value("FreightAmt", (Object)FreightAmt);
    }
    
    public BigDecimal getFreightAmt() {
        final BigDecimal bd = (BigDecimal)this.get_Value("FreightAmt");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setIsDescription(final boolean IsDescription) {
        this.set_Value("IsDescription", (Object)IsDescription);
    }
    
    public boolean isDescription() {
        final Object oo = this.get_Value("IsDescription");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setIsInvoiced(final boolean IsInvoiced) {
        this.set_Value("IsInvoiced", (Object)IsInvoiced);
    }
    
    public boolean isInvoiced() {
        final Object oo = this.get_Value("IsInvoiced");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setLine(final int Line) {
        this.set_Value("Line", (Object)Line);
    }
    
    public int getLine() {
        final Integer ii = (Integer)this.get_Value("Line");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(this.get_ID(), String.valueOf(this.getLine()));
    }
    
    public void setLineNetAmt(final BigDecimal LineNetAmt) {
        this.set_Value("LineNetAmt", (Object)LineNetAmt);
    }
    
    public BigDecimal getLineNetAmt() {
        final BigDecimal bd = (BigDecimal)this.get_Value("LineNetAmt");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
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
    
    public I_M_AttributeSetInstance getM_AttributeSetInstanceTo() throws RuntimeException {
        return (I_M_AttributeSetInstance)MTable.get(this.getCtx(), "M_AttributeSetInstance").getPO(this.getM_AttributeSetInstanceTo_ID(), this.get_TrxName());
    }
    
    public void setM_AttributeSetInstanceTo_ID(final int M_AttributeSetInstanceTo_ID) {
        if (M_AttributeSetInstanceTo_ID < 1) {
            this.set_Value("M_AttributeSetInstanceTo_ID", (Object)null);
        }
        else {
            this.set_Value("M_AttributeSetInstanceTo_ID", (Object)M_AttributeSetInstanceTo_ID);
        }
    }
    
    public int getM_AttributeSetInstanceTo_ID() {
        final Integer ii = (Integer)this.get_Value("M_AttributeSetInstanceTo_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_Locator getM_Locator() throws RuntimeException {
        return (I_M_Locator)MTable.get(this.getCtx(), "M_Locator").getPO(this.getM_Locator_ID(), this.get_TrxName());
    }
    
    public void setM_Locator_ID(final int M_Locator_ID) {
        if (M_Locator_ID < 1) {
            this.set_Value("M_Locator_ID", (Object)null);
        }
        else {
            this.set_Value("M_Locator_ID", (Object)M_Locator_ID);
        }
    }
    
    public int getM_Locator_ID() {
        final Integer ii = (Integer)this.get_Value("M_Locator_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_M_Locator getM_LocatorTo() throws RuntimeException {
        return (I_M_Locator)MTable.get(this.getCtx(), "M_Locator").getPO(this.getM_LocatorTo_ID(), this.get_TrxName());
    }
    
    public void setM_LocatorTo_ID(final int M_LocatorTo_ID) {
        if (M_LocatorTo_ID < 1) {
            this.set_Value("M_LocatorTo_ID", (Object)null);
        }
        else {
            this.set_Value("M_LocatorTo_ID", (Object)M_LocatorTo_ID);
        }
    }
    
    public int getM_LocatorTo_ID() {
        final Integer ii = (Integer)this.get_Value("M_LocatorTo_ID");
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
    
    public void setPickedQty(final BigDecimal PickedQty) {
        this.set_Value("PickedQty", (Object)PickedQty);
    }
    
    public BigDecimal getPickedQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("PickedQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setProcessed(final boolean Processed) {
        this.set_Value("Processed", (Object)Processed);
    }
    
    public boolean isProcessed() {
        final Object oo = this.get_Value("Processed");
        if (oo == null) {
            return false;
        }
        if (oo instanceof Boolean) {
            return (boolean)oo;
        }
        return "Y".equals(oo);
    }
    
    public void setQtyDelivered(final BigDecimal QtyDelivered) {
        this.set_Value("QtyDelivered", (Object)QtyDelivered);
    }
    
    public BigDecimal getQtyDelivered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyDelivered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyEntered(final BigDecimal QtyEntered) {
        this.set_Value("QtyEntered", (Object)QtyEntered);
    }
    
    public BigDecimal getQtyEntered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyEntered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyInTransit(final BigDecimal QtyInTransit) {
        this.set_Value("QtyInTransit", (Object)QtyInTransit);
    }
    
    public BigDecimal getQtyInTransit() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyInTransit");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyOrdered(final BigDecimal QtyOrdered) {
        this.set_Value("QtyOrdered", (Object)QtyOrdered);
    }
    
    public BigDecimal getQtyOrdered() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyOrdered");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setQtyReserved(final BigDecimal QtyReserved) {
        this.set_Value("QtyReserved", (Object)QtyReserved);
    }
    
    public BigDecimal getQtyReserved() {
        final BigDecimal bd = (BigDecimal)this.get_Value("QtyReserved");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setScrappedQty(final BigDecimal ScrappedQty) {
        this.set_Value("ScrappedQty", (Object)ScrappedQty);
    }
    
    public BigDecimal getScrappedQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("ScrappedQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public void setTargetQty(final BigDecimal TargetQty) {
        this.set_Value("TargetQty", (Object)TargetQty);
    }
    
    public BigDecimal getTargetQty() {
        final BigDecimal bd = (BigDecimal)this.get_Value("TargetQty");
        if (bd == null) {
            return Env.ZERO;
        }
        return bd;
    }
    
    public I_C_ElementValue getUser1() throws RuntimeException {
        return (I_C_ElementValue)MTable.get(this.getCtx(), "C_ElementValue").getPO(this.getUser1_ID(), this.get_TrxName());
    }
    
    public void setUser1_ID(final int User1_ID) {
        if (User1_ID < 1) {
            this.set_Value("User1_ID", (Object)null);
        }
        else {
            this.set_Value("User1_ID", (Object)User1_ID);
        }
    }
    
    public int getUser1_ID() {
        final Integer ii = (Integer)this.get_Value("User1_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
    
    public I_C_ElementValue getUser2() throws RuntimeException {
        return (I_C_ElementValue)MTable.get(this.getCtx(), "C_ElementValue").getPO(this.getUser2_ID(), this.get_TrxName());
    }
    
    public void setUser2_ID(final int User2_ID) {
        if (User2_ID < 1) {
            this.set_Value("User2_ID", (Object)null);
        }
        else {
            this.set_Value("User2_ID", (Object)User2_ID);
        }
    }
    
    public int getUser2_ID() {
        final Integer ii = (Integer)this.get_Value("User2_ID");
        if (ii == null) {
            return 0;
        }
        return ii;
    }
}
