// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.eevolution.model.MDDOrderLine;
import org.compiere.util.Msg;
import org.compiere.model.MMovementLine;
import java.math.BigDecimal;
import org.compiere.model.MMovement;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrg;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.compiere.model.MLocator;
import org.compiere.model.MDocType;
import org.eevolution.model.MDDOrder;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class InterWHCreateOutbound extends SvrProcess
{
    private int p_Locator;
    private int p_C_DocType_ID;
    private int p_DD_Order_ID;
    private Timestamp p_MovementDate;
    
    public InterWHCreateOutbound() {
        this.p_Locator = 0;
        this.p_C_DocType_ID = 0;
        this.p_DD_Order_ID = 0;
        this.p_MovementDate = null;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("M_Locator_ID")) {
                    this.p_Locator = para[i].getParameterAsInt();
                }
                else if (name.equals("C_DocType_ID")) {
                    this.p_C_DocType_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("DD_Order_ID")) {
                    this.p_DD_Order_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("MovementDate")) {
                    this.p_MovementDate = para[i].getParameterAsTimestamp();
                }
                else {
                    this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        if (this.p_DD_Order_ID <= 0) {
            return "Error: No Selected Inter-warehouse Document";
        }
        if (this.p_MovementDate == null) {
            return "Error: No Movement Date";
        }
        final MDDOrder interWH = new MDDOrder(this.getCtx(), this.p_DD_Order_ID, this.get_TrxName());
        if (!interWH.getDocStatus().equals("CO")) {
            return "Error: Only Completed Inter-warehouse Document Can be Processed";
        }
        if (this.p_C_DocType_ID <= 0) {
            return "Error: No Document Type Selected for Inbound Movement";
        }
        final MDocType docType = new MDocType(this.getCtx(), this.p_C_DocType_ID, this.get_TrxName());
        if (!docType.getDocBaseType().equals("MMM")) {
            return "Error: Selected Document Type Is Not Material Movement";
        }
        if (this.p_Locator <= 0) {
            return "Error: No Source Locator Selected";
        }
        final MLocator locator = new MLocator(this.getCtx(), this.p_Locator, this.get_TrxName());
        if (locator.getM_Warehouse_ID() != interWH.getM_Warehouse_ID()) {
            return "Error: Selected Locator Is Not in The Source Warehouse";
        }
        if (DB.getSQLValue(this.get_TrxName(), "select count(*) from m_movement where dd_order_id = " + this.p_DD_Order_ID + " and docstatus in ('DR','IN') and IsOutbound = 'Y'") > 0) {
            return "Error: Movement Created Draft ";
        }
        new MWarehouse(this.getCtx(), interWH.getM_Warehouse_ID(), this.get_TrxName());
        final MWarehouse whTo = new MWarehouse(this.getCtx(), interWH.get_ValueAsInt("M_WarehouseTo_ID"), this.get_TrxName());
        final String sqlWHTransit = "SELECT M_Warehouse_ID FROM M_Warehouse WHERE IsIntransit='Y' AND IsActive='Y' AND AD_Org_ID=" + whTo.getAD_Org_ID();
        final int M_WareHouse_InTransit_ID = DB.getSQLValue(this.get_TrxName(), sqlWHTransit);
        if (M_WareHouse_InTransit_ID <= 0) {
            final MOrg org = new MOrg(this.getCtx(), whTo.getAD_Org_ID(), this.get_TrxName());
            throw new AdempiereException("Warehouse.InTransit='Y' of organization " + org.getName() + " not exist");
        }
        final MWarehouse whTransit = new MWarehouse(this.getCtx(), M_WareHouse_InTransit_ID, this.get_TrxName());
        final String sqlLocatorTransit = "SELECT M_Locator_ID FROM M_Locator WHERE  IsActive='Y' AND M_Warehouse_ID=" + whTransit.getM_Warehouse_ID();
        final int locator_InTransit_ID = DB.getSQLValue(this.get_TrxName(), sqlLocatorTransit);
        if (locator_InTransit_ID <= 0) {
            throw new AdempiereException("Locator.IsIntransit='Y' of warehouse " + whTransit.getName() + " not exist");
        }
        final int M_WarehouseTo_ID = whTransit.getM_Warehouse_ID();
        final MMovement outbound = new MMovement(this.getCtx(), 0, this.get_TrxName());
        outbound.setAD_Org_ID(interWH.getAD_Org_ID());
        if (interWH.getAD_OrgTrx_ID() > 0) {
            outbound.setAD_OrgTrx_ID(interWH.getAD_OrgTrx_ID());
        }
        outbound.setMovementDate(this.p_MovementDate);
        outbound.setC_Project_ID(interWH.getC_Project_ID());
        outbound.setC_BPartner_ID(interWH.getC_BPartner_ID());
        outbound.setC_BPartner_Location_ID(interWH.getC_BPartner_Location_ID());
        outbound.setM_Shipper_ID(interWH.getM_Shipper_ID());
        outbound.setDocAction("CO");
        outbound.setDocStatus("DR");
        outbound.setC_DocType_ID(this.p_C_DocType_ID);
        outbound.set_ValueOfColumn("M_Warehouse_ID", (Object)interWH.getM_Warehouse_ID());
        outbound.set_ValueOfColumn("M_WarehouseTo_ID", (Object)M_WarehouseTo_ID);
        outbound.setDD_Order_ID(interWH.getDD_Order_ID());
        outbound.set_ValueOfColumn("IsOutbound", (Object)"Y");
        outbound.saveEx();
        final MDDOrderLine[] lines = interWH.getLines();
        new MWarehouse(this.getCtx(), outbound.get_ValueAsInt("M_Warehouse_ID"), this.get_TrxName());
        MDDOrderLine[] array;
        for (int length = (array = lines).length, i = 0; i < length; ++i) {
            final MDDOrderLine line = array[i];
            new StringBuilder("DD_OrderLine_ID=").append(line.getDD_OrderLine_ID()).append(" AND IsOutbound='Y' AND DocStatus IN ('CO','CL')").toString();
            final String jml = line.get_ValueAsString("QtyOutbound");
            final BigDecimal qtyEntOutbound = new BigDecimal(jml);
            if (line.getQtyEntered().subtract(qtyEntOutbound).compareTo(BigDecimal.ZERO) > 0) {
                final MMovementLine moveLine = new MMovementLine(outbound);
                moveLine.setLine(line.getLine());
                moveLine.setM_Product_ID(line.getM_Product_ID());
                moveLine.setMovementQty(line.getQtyEntered().subtract(qtyEntOutbound));
                if (line.getM_AttributeSetInstance_ID() > 0) {
                    moveLine.setM_AttributeSetInstance_ID(line.getM_AttributeSetInstance_ID());
                }
                moveLine.setM_Locator_ID(this.p_Locator);
                moveLine.setM_LocatorTo_ID(locator_InTransit_ID);
                moveLine.set_ValueOfColumn("C_UOM_ID", (Object)line.getC_UOM_ID());
                moveLine.setDD_OrderLine_ID(line.getDD_OrderLine_ID());
                moveLine.saveEx();
            }
        }
        outbound.saveEx();
        interWH.saveEx();
        final String message = Msg.parseTranslation(this.getCtx(), "@GeneratedOutbound@" + outbound.getDocumentNo());
        this.addBufferLog(0, (Timestamp)null, (BigDecimal)null, message, outbound.get_Table_ID(), outbound.getM_Movement_ID());
        return "Successfully Created Outbound Movement #" + outbound.getDocumentNo();
    }
    
    public boolean isDisallowNegativeInv(final MWarehouse warehouse) {
        return warehouse.isDisallowNegativeInv();
    }
}
