// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.util.Iterator;
import java.util.List;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MStorageReservation;
import java.sql.Timestamp;
import org.compiere.util.DB;
import java.util.Properties;
import java.math.BigDecimal;

public class MRequisition extends org.compiere.model.MRequisition
{
    private static final long serialVersionUID = 8844916612112952581L;
    private int C_BPartner_ID;
    private int M_Product_ID;
    private BigDecimal QtyPlanned;
    private int M_RequisitionLine_ID;
    
    public MRequisition(final Properties ctx, final int M_Requisition_ID, final String trxName) {
        super(ctx, M_Requisition_ID, trxName);
    }
    
    private void setPriceList(final int C_BPartner_ID) {
        this.C_BPartner_ID = C_BPartner_ID;
        final int M_PriceList_ID = DB.getSQLValueEx(this.get_TrxName(), "SELECT COALESCE(bp.PO_PriceList_ID,bpg.PO_PriceList_ID) FROM C_BPartner bp INNER JOIN C_BP_Group bpg ON (bpg.C_BP_Group_ID=bp.C_BP_Group_ID) WHERE bp.C_BPartner_ID=?", new Object[] { C_BPartner_ID });
        if (M_PriceList_ID > 0) {
            this.setM_PriceList_ID(M_PriceList_ID);
        }
    }
    
    public void create(final int PP_MRP_ID, BigDecimal QtyPlanned, final int M_Product_ID, final int C_BPartner, final int AD_Org_ID, final int AD_User_ID, final Timestamp DateRequired, final String description, final int M_Warehouse_ID, final int C_DocType_ID) {
        this.QtyPlanned = QtyPlanned;
        this.M_Product_ID = M_Product_ID;
        final BigDecimal available = MStorageReservation.getQtyAvailable(M_Warehouse_ID, M_Product_ID, 0, this.get_TrxName());
        if (QtyPlanned.compareTo(available) < 1) {
            return;
        }
        QtyPlanned = QtyPlanned.subtract(available);
        this.setPriceList(C_BPartner);
        this.setAD_Org_ID(AD_Org_ID);
        this.setAD_User_ID(AD_User_ID);
        this.setDateRequired(DateRequired);
        this.setDescription(description);
        this.setM_Warehouse_ID(M_Warehouse_ID);
        this.setC_DocType_ID(C_DocType_ID);
        this.saveEx(this.get_TrxName());
        this.createLine();
    }
    
    private void createLine() {
        final MRequisitionLine reqline = new MRequisitionLine((org.compiere.model.MRequisition)this);
        reqline.setLine(10);
        reqline.setAD_Org_ID(this.getAD_Org_ID());
        reqline.setC_BPartner_ID(this.C_BPartner_ID);
        reqline.setM_Product_ID(this.M_Product_ID);
        reqline.setPrice();
        reqline.setPriceActual(Env.ZERO);
        reqline.setQty(this.QtyPlanned);
        reqline.saveEx(this.get_TrxName());
        this.M_RequisitionLine_ID = reqline.get_ID();
    }
    
    private void setCorrectDates() {
        final List<MPPMRP> mrpList = new Query(this.getCtx(), "PP_MRP", "M_Requisition_ID=?", this.get_TrxName()).setParameters(new Object[] { this.getM_Requisition_ID() }).list();
        for (final MPPMRP mrp : mrpList) {
            mrp.setDatePromised(this.getDateRequired());
            mrp.setM_RequisitionLine_ID(this.M_RequisitionLine_ID);
            mrp.saveEx(this.get_TrxName());
        }
    }
}
