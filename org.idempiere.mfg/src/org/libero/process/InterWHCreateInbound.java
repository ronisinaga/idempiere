// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.math.BigDecimal;
import org.compiere.util.Msg;
import org.compiere.model.MMovementLine;
import org.compiere.model.MMovement;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.model.MLocator;
import org.compiere.model.MDocType;
import org.eevolution.model.MDDOrder;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class InterWHCreateInbound extends SvrProcess
{
    private int p_M_Warehouse_ID;
    private int p_M_WarehouseZone_ID;
    private int p_Locator;
    private int p_C_DocType_ID;
    private int p_DD_Order_ID;
    private int p_M_MovementOutBound_ID;
    private Timestamp p_MovementDate;
    
    public InterWHCreateInbound() {
        this.p_M_Warehouse_ID = 0;
        this.p_M_WarehouseZone_ID = 0;
        this.p_Locator = 0;
        this.p_C_DocType_ID = 0;
        this.p_DD_Order_ID = 0;
        this.p_M_MovementOutBound_ID = 0;
        this.p_MovementDate = null;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("DD_Order_ID")) {
                    this.p_DD_Order_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_Warehouse_ID")) {
                    this.p_M_Warehouse_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_WarehouseZone_ID")) {
                    this.p_M_WarehouseZone_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_Locator_ID")) {
                    this.p_Locator = para[i].getParameterAsInt();
                }
                else if (name.equals("C_DocType_ID")) {
                    this.p_C_DocType_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_Movement_ID")) {
                    this.p_M_MovementOutBound_ID = para[i].getParameterAsInt();
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
        if (this.p_M_MovementOutBound_ID <= 0) {
            return "Error: No Outbound Document";
        }
        if (this.p_MovementDate == null) {
            return "Error: No Movement Date";
        }
        final MDDOrder interWH = new MDDOrder(this.getCtx(), this.p_DD_Order_ID, this.get_TrxName());
        if (!interWH.getDocStatus().equals("CO")) {
            return "Error: Only Completed Inter-warehouse Document Can be Processed";
        }
        if (interWH.get_ValueAsInt("M_MovementIn_ID") > 0) {
            return "Error: Inbound Movement Has Been Created";
        }
        if (this.p_C_DocType_ID <= 0) {
            return "Error: No Document Type Selected for Inbound Movement";
        }
        final MDocType docType = new MDocType(this.getCtx(), this.p_C_DocType_ID, this.get_TrxName());
        if (!docType.getDocBaseType().equals("MMM")) {
            return "Error: Selected Document Type Is Not Material Movement";
        }
        if (this.p_Locator <= 0) {
            return "Error: No Destination Locator Selected";
        }
        final MLocator locator = new MLocator(this.getCtx(), this.p_Locator, this.get_TrxName());
        if (locator.getM_Warehouse_ID() != this.p_M_Warehouse_ID) {
            return "Error: Selected Locator Is Not in The Destination Warehouse";
        }
        final boolean alreadyInbounded = new Query(this.getCtx(), "M_Movement", "M_OutBoundFrom_ID=" + this.p_M_MovementOutBound_ID + " AND DocStatus NOT IN ('VO','RE')", this.get_TrxName()).match();
        if (alreadyInbounded) {
            return "Error: This OutBound is already inbounded";
        }
        new MWarehouse(this.getCtx(), interWH.getM_Warehouse_ID(), this.get_TrxName());
        new MWarehouse(this.getCtx(), interWH.get_ValueAsInt("M_WarehouseTo_ID"), this.get_TrxName());
        final MMovement outbound = new MMovement(this.getCtx(), this.p_M_MovementOutBound_ID, this.get_TrxName());
        final MMovementLine[] lines = outbound.getLines(true);
        final MMovement inbound = new MMovement(this.getCtx(), 0, this.get_TrxName());
        inbound.setAD_Org_ID(interWH.getAD_Org_ID());
        inbound.setMovementDate(this.p_MovementDate);
        inbound.setC_DocType_ID(this.p_C_DocType_ID);
        inbound.setDocStatus("DR");
        inbound.setDocAction("CO");
        inbound.setDD_Order_ID(interWH.getDD_Order_ID());
        inbound.setC_Project_ID(interWH.getC_Project_ID());
        inbound.setC_BPartner_ID(outbound.getC_BPartner_ID());
        inbound.setC_BPartner_Location_ID(outbound.getC_BPartner_Location_ID());
        inbound.setM_Shipper_ID(outbound.getM_Shipper_ID());
        inbound.setAD_User_ID(outbound.getAD_User_ID());
        inbound.set_ValueOfColumn("IsInbound", (Object)"Y");
        inbound.set_ValueOfColumn("M_OutBoundFrom_ID", (Object)this.p_M_MovementOutBound_ID);
        inbound.set_ValueOfColumn("kendaraan", outbound.get_Value("kendaraan"));
        inbound.set_ValueOfColumn("Pengirim", outbound.get_Value("Pengirim"));
        inbound.set_ValueOfColumn("driver", outbound.get_Value("driver"));
        inbound.set_ValueOfColumn("datesendmovement", outbound.get_Value("datesendmovement"));
        inbound.set_ValueOfColumn("datereceivedmovement", outbound.get_Value("datereceivedmovement"));
        inbound.set_ValueOfColumn("salesrep_id", outbound.get_Value("salesrep_id"));
        inbound.set_ValueOfColumn("c_order_id", outbound.get_Value("c_order_id"));
        inbound.set_ValueOfColumn("salesrep_id", outbound.get_Value("salesrep_id"));
        inbound.set_ValueOfColumn("salesrep_id", outbound.get_Value("salesrep_id"));
        inbound.saveEx();
        MMovementLine[] array;
        for (int length = (array = lines).length, i = 0; i < length; ++i) {
            final MMovementLine line = array[i];
            final MMovementLine moveLine = new MMovementLine(inbound);
            moveLine.setLine(line.getLine());
            moveLine.setAD_Org_ID(interWH.getAD_Org_ID());
            moveLine.setM_Product_ID(line.getM_Product_ID());
            moveLine.set_ValueOfColumn("QtyEntered", line.get_Value("QtyEntered"));
            moveLine.setMovementQty(line.getMovementQty());
            moveLine.setM_Locator_ID(line.getM_LocatorTo_ID());
            moveLine.setM_LocatorTo_ID(this.p_Locator);
            moveLine.set_ValueOfColumn("C_UOM_ID", line.get_Value("C_UOM_ID"));
            moveLine.setDD_OrderLine_ID(line.getDD_OrderLine_ID());
            moveLine.set_ValueOfColumn("M_OutBoundLineFrom_ID", (Object)line.getM_MovementLine_ID());
            moveLine.setM_AttributeSetInstance_ID(line.getM_AttributeSetInstance_ID());
            moveLine.saveEx();
        }
        interWH.saveEx();
        final String message = Msg.parseTranslation(this.getCtx(), "@GeneratedInbound@" + inbound.getDocumentNo());
        this.addBufferLog(0, (Timestamp)null, (BigDecimal)null, message, inbound.get_Table_ID(), inbound.getM_Movement_ID());
        return "";
    }
}
