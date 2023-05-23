// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MDocType;
import java.util.ArrayList;
import org.compiere.model.MAttributeSet;
import org.compiere.model.MProduct;
import java.math.BigDecimal;
import org.eevolution.model.MDDOrderLine;
import java.sql.ResultSet;
import org.compiere.model.MProductCategory;
import org.libero.model.LiberoMovementLine;
import org.compiere.model.MLocator;
import org.eevolution.model.MDDOrder;
import org.compiere.model.MClient;
import java.sql.PreparedStatement;
import org.compiere.util.DB;
import org.compiere.util.AdempiereUserError;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Env;
import java.util.logging.Level;
import org.compiere.model.MStorageOnHand;
import java.util.HashMap;
import org.compiere.model.MMovement;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class MovementGenerate extends SvrProcess
{
    private boolean p_Selection;
    private int p_M_Warehouse_ID;
    private int p_C_BPartner_ID;
    private Timestamp p_DatePromised;
    private boolean p_IsUnconfirmedInOut;
    private String p_docAction;
    private boolean p_ConsolidateDocument;
    private Timestamp p_DateShipped;
    private MMovement m_movement;
    private int m_created;
    private int m_line;
    private Timestamp m_movementDate;
    private int m_lastC_BPartner_Location_ID;
    private String m_sql;
    private HashMap<SParameter, MStorageOnHand[]> m_map;
    private SParameter m_lastPP;
    private MStorageOnHand[] m_lastStorages;
    
    public MovementGenerate() {
        this.p_Selection = false;
        this.p_M_Warehouse_ID = 0;
        this.p_C_BPartner_ID = 0;
        this.p_DatePromised = null;
        this.p_IsUnconfirmedInOut = false;
        this.p_docAction = "CO";
        this.p_ConsolidateDocument = true;
        this.p_DateShipped = null;
        this.m_movement = null;
        this.m_created = 0;
        this.m_line = 0;
        this.m_movementDate = null;
        this.m_lastC_BPartner_Location_ID = -1;
        this.m_sql = null;
        this.m_map = new HashMap<SParameter, MStorageOnHand[]>();
        this.m_lastPP = null;
        this.m_lastStorages = null;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("M_Warehouse_ID")) {
                    this.p_M_Warehouse_ID = para.getParameterAsInt();
                }
                else if (name.equals("C_BPartner_ID")) {
                    this.p_C_BPartner_ID = para.getParameterAsInt();
                }
                else if (name.equals("DatePromised")) {
                    this.p_DatePromised = (Timestamp)para.getParameter();
                }
                else if (name.equals("Selection")) {
                    this.p_Selection = "Y".equals(para.getParameter());
                }
                else if (name.equals("IsUnconfirmedInOut")) {
                    this.p_IsUnconfirmedInOut = "Y".equals(para.getParameter());
                }
                else if (name.equals("ConsolidateDocument")) {
                    this.p_ConsolidateDocument = "Y".equals(para.getParameter());
                }
                else if (name.equals("DocAction")) {
                    this.p_docAction = (String)para.getParameter();
                }
                else if (name.equals("MovementDate")) {
                    this.p_DateShipped = (Timestamp)para.getParameter();
                }
                else {
                    this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
                }
            }
            if (this.p_DateShipped == null) {
                this.m_movementDate = Env.getContextAsDate(this.getCtx(), "#Date");
                if (this.m_movementDate == null) {
                    this.m_movementDate = new Timestamp(System.currentTimeMillis());
                }
            }
            else {
                this.m_movementDate = this.p_DateShipped;
            }
            if (!"CO".equals(this.p_docAction)) {
                this.p_docAction = "PR";
            }
        }
    }
    
    protected String doIt() throws Exception {
        this.log.info("Selection=" + this.p_Selection + ", M_Warehouse_ID=" + this.p_M_Warehouse_ID + ", C_BPartner_ID=" + this.p_C_BPartner_ID + ", Consolidate=" + this.p_ConsolidateDocument + ", IsUnconfirmed=" + this.p_IsUnconfirmedInOut + ", Movement=" + this.m_movementDate);
        if (this.p_M_Warehouse_ID == 0) {
            throw new AdempiereUserError("@NotFound@ @M_Warehouse_ID@");
        }
        if (this.p_Selection) {
            this.m_sql = "SELECT DD_Order.* FROM DD_Order, T_Selection WHERE DD_Order.DocStatus='CO' AND DD_Order.AD_Client_ID=? AND DD_Order.DD_Order_ID = T_Selection.T_Selection_ID AND T_Selection.AD_PInstance_ID=? ";
        }
        else {
            this.m_sql = "SELECT * FROM DD_Order o WHERE DocStatus='CO'  AND o.C_DocType_ID IN (SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='DOO')\tAND o.IsDropShip='N' AND o.DeliveryRule<>'M' AND EXISTS (SELECT 1 FROM DD_OrderLine ol  WHERE ? IN (SELECT l.M_Warehouse_ID FROM M_Locator l WHERE l.M_Locator_ID=ol.M_Locator_ID) ";
            if (this.p_DatePromised != null) {
                this.m_sql = String.valueOf(this.m_sql) + " AND TRUNC(ol.DatePromised)<=?";
            }
            this.m_sql = String.valueOf(this.m_sql) + " AND o.DD_Order_ID=ol.DD_Order_ID AND ol.QtyOrdered<>ol.QtyIntransit)";
            if (this.p_C_BPartner_ID != 0) {
                this.m_sql = String.valueOf(this.m_sql) + " AND o.C_BPartner_ID=?";
            }
            this.m_sql = String.valueOf(this.m_sql) + " ORDER BY M_Warehouse_ID, PriorityRule, M_Shipper_ID, C_BPartner_ID, C_BPartner_Location_ID, DD_Order_ID";
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(this.m_sql, this.get_TrxName());
            int index = 1;
            if (this.p_Selection) {
                pstmt.setInt(index++, Env.getAD_Client_ID(this.getCtx()));
                pstmt.setInt(index++, this.getAD_PInstance_ID());
            }
            else {
                pstmt.setInt(index++, this.p_M_Warehouse_ID);
                if (this.p_DatePromised != null) {
                    pstmt.setTimestamp(index++, this.p_DatePromised);
                }
                if (this.p_C_BPartner_ID != 0) {
                    pstmt.setInt(index++, this.p_C_BPartner_ID);
                }
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, this.m_sql, (Throwable)e);
        }
        return this.generate(pstmt);
    }
    
    private String generate(PreparedStatement pstmt) {
        final MClient client = MClient.get(this.getCtx());
        try {
            final ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                final MDDOrder order = new MDDOrder(this.getCtx(), rs, this.get_TrxName());
                if (!this.p_ConsolidateDocument || (this.m_movement != null && (this.m_movement.getC_BPartner_Location_ID() != order.getC_BPartner_Location_ID() || this.m_movement.getM_Shipper_ID() != order.getM_Shipper_ID()))) {
                    this.completeMovement();
                }
                this.log.fine("check: " + order + " - DeliveryRule=" + order.getDeliveryRule());
                final Timestamp minGuaranteeDate = this.m_movementDate;
                boolean completeOrder = "O".equals(order.getDeliveryRule());
                String where = " " + this.p_M_Warehouse_ID + " IN (SELECT l.M_Warehouse_ID FROM M_Locator l WHERE l.M_Locator_ID=M_Locator_ID) ";
                if (this.p_DatePromised != null) {
                    where = String.valueOf(where) + " AND (TRUNC(DatePromised)<=" + DB.TO_DATE(this.p_DatePromised, true) + " OR DatePromised IS NULL)";
                }
                if (!"F".equals(order.getDeliveryRule())) {
                    where = String.valueOf(where) + " AND (DD_OrderLine.M_Product_ID IS NULL OR EXISTS (SELECT * FROM M_Product p WHERE DD_OrderLine.M_Product_ID=p.M_Product_ID AND IsExcludeAutoDelivery='N'))";
                }
                if (!this.p_IsUnconfirmedInOut) {
                    where = String.valueOf(where) + " AND NOT EXISTS (SELECT * FROM M_MovementLine iol INNER JOIN M_Movement io ON (iol.M_Movement_ID=io.M_Movement_ID) WHERE iol.DD_OrderLine_ID=DD_OrderLine.DD_OrderLine_ID AND io.DocStatus IN ('IP','WC'))";
                }
                final MDDOrderLine[] lines = order.getLines(where, "M_Product_ID");
                for (int i = 0; i < lines.length; ++i) {
                    final MDDOrderLine line = lines[i];
                    final MLocator l = new MLocator(this.getCtx(), line.getM_Locator_ID(), this.get_TrxName());
                    if (l.getM_Warehouse_ID() == this.p_M_Warehouse_ID) {
                        this.log.fine("check: " + line);
                        BigDecimal onHand = Env.ZERO;
                        BigDecimal toDeliver = line.getConfirmedQty();
                        final MProduct product = line.getProduct();
                        if (product == null || toDeliver.signum() != 0) {
                            if (line.getC_Charge_ID() == 0 || toDeliver.signum() != 0) {
                                BigDecimal unconfirmedShippedQty = Env.ZERO;
                                if (this.p_IsUnconfirmedInOut && product != null && toDeliver.signum() != 0) {
                                    final String where2 = "EXISTS (SELECT * FROM M_Movement io WHERE io.M_Movement_ID=M_MovementLine.M_Movement_ID AND io.DocStatus IN ('IP','WC'))";
                                    final LiberoMovementLine[] iols = LiberoMovementLine.getOfOrderLine(this.getCtx(), line.getDD_OrderLine_ID(), where2, null);
                                    for (int j = 0; j < iols.length; ++j) {
                                        unconfirmedShippedQty = unconfirmedShippedQty.add(iols[j].getMovementQty());
                                    }
                                    String logInfo = "Unconfirmed Qty=" + unconfirmedShippedQty + " - ToDeliver=" + toDeliver + "->";
                                    toDeliver = toDeliver.subtract(unconfirmedShippedQty);
                                    logInfo = String.valueOf(logInfo) + toDeliver;
                                    if (toDeliver.signum() < 0) {
                                        toDeliver = Env.ZERO;
                                        logInfo = String.valueOf(logInfo) + " (set to 0)";
                                    }
                                    onHand = onHand.subtract(unconfirmedShippedQty);
                                    this.log.fine(logInfo);
                                }
                                if ((product == null || !product.isStocked()) && (line.getQtyOrdered().signum() == 0 || toDeliver.signum() != 0)) {
                                    if (!"O".equals(order.getDeliveryRule())) {
                                        this.createLine(order, line, toDeliver, null, false);
                                    }
                                }
                                else {
                                    final MProductCategory pc = MProductCategory.get(order.getCtx(), product.getM_Product_Category_ID());
                                    String MMPolicy = pc.getMMPolicy();
                                    if (MMPolicy == null || MMPolicy.length() == 0) {
                                        MMPolicy = client.getMMPolicy();
                                    }
                                    final MStorageOnHand[] storages = this.getStorages(l.getM_Warehouse_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), line.getM_AttributeSetInstance_ID() == 0, minGuaranteeDate, "F".equals(MMPolicy));
                                    for (int k = 0; k < storages.length; ++k) {
                                        final MStorageOnHand storage = storages[k];
                                        onHand = onHand.add(storage.getQtyOnHand());
                                    }
                                    final boolean fullLine = onHand.compareTo(toDeliver) >= 0 || toDeliver.signum() < 0;
                                    if (completeOrder && !fullLine) {
                                        this.log.fine("Failed CompleteOrder - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + " - " + line);
                                        completeOrder = false;
                                        break;
                                    }
                                    if (fullLine && "L".equals(order.getDeliveryRule())) {
                                        this.log.fine("CompleteLine - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + ", ToDeliver=" + toDeliver + " - " + line);
                                        this.createLine(order, line, toDeliver, storages, false);
                                    }
                                    else if ("A".equals(order.getDeliveryRule()) && (onHand.signum() > 0 || toDeliver.signum() < 0)) {
                                        BigDecimal deliver = toDeliver;
                                        if (deliver.compareTo(onHand) > 0) {
                                            deliver = onHand;
                                        }
                                        this.log.fine("Available - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + ", Delivering=" + deliver + " - " + line);
                                        this.createLine(order, line, deliver, storages, false);
                                    }
                                    else if ("F".equals(order.getDeliveryRule())) {
                                        final BigDecimal deliver = toDeliver;
                                        this.log.fine("Force - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + ", Delivering=" + deliver + " - " + line);
                                        this.createLine(order, line, deliver, storages, true);
                                    }
                                    else if ("M".equals(order.getDeliveryRule())) {
                                        this.log.fine("Manual - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + ") - " + line);
                                    }
                                    else {
                                        this.log.fine("Failed: " + order.getDeliveryRule() + " - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + " - " + line);
                                    }
                                }
                            }
                        }
                    }
                }
                if (completeOrder && "O".equals(order.getDeliveryRule())) {
                    for (int i = 0; i < lines.length; ++i) {
                        final MDDOrderLine line = lines[i];
                        final MLocator l = new MLocator(this.getCtx(), line.getM_Locator_ID(), this.get_TrxName());
                        if (l.getM_Warehouse_ID() == this.p_M_Warehouse_ID) {
                            final MProduct product2 = line.getProduct();
                            final BigDecimal toDeliver = line.getQtyOrdered().subtract(line.getQtyDelivered());
                            MStorageOnHand[] storages2 = null;
                            if (product2 != null && product2.isStocked()) {
                                final MProductCategory pc2 = MProductCategory.get(order.getCtx(), product2.getM_Product_Category_ID());
                                String MMPolicy2 = pc2.getMMPolicy();
                                if (MMPolicy2 == null || MMPolicy2.length() == 0) {
                                    MMPolicy2 = client.getMMPolicy();
                                }
                                storages2 = this.getStorages(l.getM_Warehouse_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), product2.getM_AttributeSet_ID(), line.getM_AttributeSetInstance_ID() == 0, minGuaranteeDate, "F".equals(MMPolicy2));
                            }
                            this.createLine(order, line, toDeliver, storages2, false);
                        }
                    }
                }
                this.m_line += 1000;
            }
            rs.close();
            pstmt.close();
            pstmt = null;
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, this.m_sql, (Throwable)e);
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            pstmt = null;
        }
        catch (Exception ex) {
            pstmt = null;
        }
        this.completeMovement();
        return "@Created@ = " + this.m_created;
    }
    
    private void createLine(final MDDOrder order, final MDDOrderLine orderLine, final BigDecimal qty, final MStorageOnHand[] storages, final boolean force) {
        if (this.m_lastC_BPartner_Location_ID != order.getC_BPartner_Location_ID()) {
            this.completeMovement();
        }
        this.m_lastC_BPartner_Location_ID = order.getC_BPartner_Location_ID();
        if (this.m_movement == null) {
            final MLocator locator = MLocator.get(this.getCtx(), orderLine.getM_Locator_ID());
            (this.m_movement = createMovement(order, this.m_movementDate)).setAD_Org_ID(locator.getAD_Org_ID());
            this.m_movement.setIsInTransit(true);
            this.m_movement.setDD_Order_ID(order.getDD_Order_ID());
            if (order.getC_BPartner_ID() != order.getC_BPartner_ID()) {
                this.m_movement.setC_BPartner_ID(order.getC_BPartner_ID());
            }
            if (order.getC_BPartner_Location_ID() != order.getC_BPartner_Location_ID()) {
                this.m_movement.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
            }
            final int docTypeDO_ID = this.getDocType("MMM", this.m_movement.getAD_Org_ID());
            if (docTypeDO_ID > 0) {
                this.m_movement.setC_DocType_ID(docTypeDO_ID);
            }
            if (!this.m_movement.save()) {
                throw new IllegalStateException("Could not create Movement");
            }
        }
        if (storages == null) {
            final LiberoMovementLine line = new LiberoMovementLine(this.m_movement);
            line.setOrderLine(orderLine, Env.ZERO, false);
            line.setMovementQty(qty);
            if (orderLine.getQtyEntered().compareTo(orderLine.getQtyOrdered()) != 0) {
                line.setMovementQty(qty.multiply(orderLine.getQtyEntered()).divide(orderLine.getQtyOrdered(), 12, 4));
            }
            line.setLine(this.m_line + orderLine.getLine());
            if (!line.save()) {
                throw new IllegalStateException("Could not create Shipment Line");
            }
            this.log.fine(line.toString());
        }
        else {
            final MProduct product = orderLine.getProduct();
            boolean linePerASI = false;
            if (product.getM_AttributeSet_ID() != 0) {
                final MAttributeSet mas = MAttributeSet.get(this.getCtx(), product.getM_AttributeSet_ID());
                linePerASI = mas.isInstanceAttribute();
            }
            final ArrayList<LiberoMovementLine> list = new ArrayList<LiberoMovementLine>();
            BigDecimal toDeliver = qty;
            for (int i = 0; i < storages.length; ++i) {
                final MStorageOnHand storage = storages[i];
                BigDecimal deliver = toDeliver;
                if (deliver.compareTo(storage.getQtyOnHand()) > 0 && storage.getQtyOnHand().signum() >= 0 && (!force || (force && i + 1 != storages.length))) {
                    deliver = storage.getQtyOnHand();
                }
                if (deliver.signum() != 0) {
                    final int M_Locator_ID = storage.getM_Locator_ID();
                    LiberoMovementLine line2 = null;
                    if (!linePerASI) {
                        for (int ll = 0; ll < list.size(); ++ll) {
                            final LiberoMovementLine test = list.get(ll);
                            if (test.getM_Locator_ID() == M_Locator_ID) {
                                line2 = test;
                                break;
                            }
                        }
                    }
                    if (line2 == null) {
                        line2 = new LiberoMovementLine(this.m_movement);
                        line2.setOrderLine(orderLine, deliver, false);
                        line2.setMovementQty(deliver);
                        list.add(line2);
                    }
                    else {
                        line2.setMovementQty(line2.getMovementQty().add(deliver));
                    }
                    if (orderLine.getQtyEntered().compareTo(orderLine.getQtyOrdered()) != 0) {
                        line2.setMovementQty(line2.getMovementQty().multiply(orderLine.getQtyEntered()).divide(orderLine.getQtyOrdered(), 12, 4));
                    }
                    line2.setLine(this.m_line + orderLine.getLine());
                    if (linePerASI) {
                        line2.setM_AttributeSetInstance_ID(storage.getM_AttributeSetInstance_ID());
                    }
                    if (!line2.save()) {
                        throw new IllegalStateException("Could not create Shipment Line");
                    }
                    this.log.fine("ToDeliver=" + qty + "/" + deliver + " - " + line2);
                    toDeliver = toDeliver.subtract(deliver);
                    storage.setQtyOnHand(storage.getQtyOnHand().subtract(deliver));
                    if (toDeliver.signum() == 0) {
                        break;
                    }
                }
            }
            if (toDeliver.signum() != 0) {
                throw new IllegalStateException("Not All Delivered - Remainder=" + toDeliver);
            }
        }
    }
    
    private static MMovement createMovement(final MDDOrder order, final Timestamp movementDate) {
        final MMovement move = new MMovement(order.getCtx(), 0, order.get_TrxName());
        move.setC_BPartner_ID(order.getC_BPartner_ID());
        move.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
        move.setAD_User_ID(order.getAD_User_ID());
        if (movementDate != null) {
            move.setMovementDate(movementDate);
        }
        move.setDD_Order_ID(order.getC_Order_ID());
        move.setDeliveryRule(order.getDeliveryRule());
        move.setDeliveryViaRule(order.getDeliveryViaRule());
        move.setM_Shipper_ID(order.getM_Shipper_ID());
        move.setFreightCostRule(order.getFreightCostRule());
        move.setFreightAmt(order.getFreightAmt());
        move.setSalesRep_ID(order.getSalesRep_ID());
        move.setC_Activity_ID(order.getC_Activity_ID());
        move.setC_Campaign_ID(order.getC_Campaign_ID());
        move.setC_Charge_ID(order.getC_Charge_ID());
        move.setChargeAmt(order.getChargeAmt());
        move.setC_Project_ID(order.getC_Project_ID());
        move.setDescription(order.getDescription());
        move.setSalesRep_ID(order.getSalesRep_ID());
        move.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
        move.setUser1_ID(order.getUser1_ID());
        move.setUser2_ID(order.getUser2_ID());
        move.setPriorityRule(order.getPriorityRule());
        return move;
    }
    
    private int getDocType(final String docBaseType, final int AD_Org_ID) {
        final MDocType[] docs = MDocType.getOfDocBaseType(this.getCtx(), docBaseType);
        if (docs == null || docs.length == 0) {
            final String textMsg = "Not found default document type for docbasetype " + docBaseType;
            throw new AdempiereException(textMsg);
        }
        MDocType[] array;
        for (int length = (array = docs).length, i = 0; i < length; ++i) {
            final MDocType doc = array[i];
            if (doc.getAD_Org_ID() == AD_Org_ID) {
                return doc.getC_DocType_ID();
            }
        }
        this.log.info("Doc Type for " + docBaseType + ": " + docs[0].getC_DocType_ID());
        return docs[0].getC_DocType_ID();
    }
    
    private MStorageOnHand[] getStorages(final int M_Warehouse_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int M_AttributeSet_ID, final boolean allAttributeInstances, final Timestamp minGuaranteeDate, final boolean FiFo) {
        this.m_lastPP = new SParameter(M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, M_AttributeSet_ID, allAttributeInstances, minGuaranteeDate, FiFo);
        this.m_lastStorages = this.m_map.get(this.m_lastPP);
        if (this.m_lastStorages == null) {
            this.m_lastStorages = MStorageOnHand.getWarehouse(this.getCtx(), M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, M_AttributeSet_ID, allAttributeInstances, minGuaranteeDate, FiFo, this.get_TrxName());
            this.m_map.put(this.m_lastPP, this.m_lastStorages);
        }
        return this.m_lastStorages;
    }
    
    private void completeMovement() {
        if (this.m_movement != null) {
            if (!this.m_movement.processIt(this.p_docAction)) {
                this.log.warning("Failed: " + this.m_movement);
            }
            this.m_movement.saveEx();
            this.addLog(this.m_movement.getM_Movement_ID(), this.m_movement.getMovementDate(), (BigDecimal)null, this.m_movement.getDocumentNo());
            ++this.m_created;
            this.m_map = new HashMap<SParameter, MStorageOnHand[]>();
            if (this.m_lastPP != null && this.m_lastStorages != null) {
                this.m_map.put(this.m_lastPP, this.m_lastStorages);
            }
        }
        this.m_movement = null;
        this.m_line = 0;
    }
    
    class SParameter
    {
        public int M_Warehouse_ID;
        public int M_Product_ID;
        public int M_AttributeSetInstance_ID;
        public int M_AttributeSet_ID;
        public boolean allAttributeInstances;
        public Timestamp minGuaranteeDate;
        public boolean FiFo;
        
        protected SParameter(final int p_Warehouse_ID, final int p_Product_ID, final int p_AttributeSetInstance_ID, final int p_AttributeSet_ID, final boolean p_allAttributeInstances, final Timestamp p_minGuaranteeDate, final boolean p_FiFo) {
            this.M_Warehouse_ID = p_Warehouse_ID;
            this.M_Product_ID = p_Product_ID;
            this.M_AttributeSetInstance_ID = p_AttributeSetInstance_ID;
            this.M_AttributeSet_ID = p_AttributeSet_ID;
            this.allAttributeInstances = p_allAttributeInstances;
            this.minGuaranteeDate = p_minGuaranteeDate;
            this.FiFo = p_FiFo;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj != null && obj instanceof SParameter) {
                final SParameter cmp = (SParameter)obj;
                boolean eq = cmp.M_Warehouse_ID == this.M_Warehouse_ID && cmp.M_Product_ID == this.M_Product_ID && cmp.M_AttributeSetInstance_ID == this.M_AttributeSetInstance_ID && cmp.M_AttributeSet_ID == this.M_AttributeSet_ID && cmp.allAttributeInstances == this.allAttributeInstances && cmp.FiFo == this.FiFo;
                if (eq) {
                    if (cmp.minGuaranteeDate != null || this.minGuaranteeDate != null) {
                        if (cmp.minGuaranteeDate == null || this.minGuaranteeDate == null || !cmp.minGuaranteeDate.equals(this.minGuaranteeDate)) {
                            eq = false;
                        }
                    }
                }
                return eq;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            long hash = this.M_Warehouse_ID + this.M_Product_ID * 2 + this.M_AttributeSetInstance_ID * 3 + this.M_AttributeSet_ID * 4;
            if (this.allAttributeInstances) {
                hash *= -1L;
            }
            if (this.FiFo) {}
            hash *= -2L;
            if (hash < 0L) {
                hash = -hash + 7L;
            }
            while (hash > 2147483647L) {
                hash -= 2147483647L;
            }
            if (this.minGuaranteeDate != null) {
                for (hash += this.minGuaranteeDate.hashCode(); hash > 2147483647L; hash -= 2147483647L) {}
            }
            return (int)hash;
        }
    }
}
