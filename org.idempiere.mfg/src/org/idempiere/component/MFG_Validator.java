// 
// Decompiled by Procyon v0.5.36
// 

package org.idempiere.component;

import org.compiere.model.MMovementLine;
import java.math.BigDecimal;
import org.compiere.util.Msg;
import org.compiere.model.MRMALine;
import org.compiere.util.Env;
import org.eevolution.model.MDDOrderLine;
import java.util.Iterator;
import java.util.Collection;
import org.compiere.model.MInOutLine;
import org.eevolution.model.MDDOrder;
import org.compiere.model.MMovement;
import org.libero.model.MPPCostCollector;
import org.compiere.model.Query;
import org.compiere.model.MInOut;
import org.libero.model.MPPOrderBOMLine;
import org.libero.model.MPPOrder;
import org.compiere.model.MForecastLine;
import org.compiere.model.X_M_Forecast;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MRequisition;
import org.compiere.model.MOrder;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MProduct;
import org.compiere.model.MOrderLine;
import org.compiere.process.DocAction;
import org.libero.model.MPPMRP;
import org.adempiere.base.event.LoginEventData;
import org.osgi.service.event.Event;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.adempiere.base.event.AbstractEventHandler;

public class MFG_Validator extends AbstractEventHandler
{
    private static CLogger log;
    private String trxName;
    private PO po;
    private static final int X_RV_PP_Product_BOMLine_Table_ID = 1000000;
    private static final String X_RV_PP_Product_BOMLine_Table_Name = "RV_PP_SalesOrder";
    
    static {
        MFG_Validator.log = CLogger.getCLogger((Class)MFG_Validator.class);
    }
    
    public MFG_Validator() {
        this.trxName = "";
        this.po = null;
    }
    
    protected void initialize() {
        this.registerEvent("adempiere/afterLogin");
        this.registerTableEvent("adempiere/po/beforeNew", "M_Movement");
        this.registerTableEvent("adempiere/po/afterNew", "C_Order");
        this.registerTableEvent("adempiere/po/afterNew", "C_OrderLine");
        this.registerTableEvent("adempiere/po/afterNew", "M_Requisition");
        this.registerTableEvent("adempiere/po/afterNew", "M_RequisitionLine");
        this.registerTableEvent("adempiere/po/afterNew", "M_Forecast");
        this.registerTableEvent("adempiere/po/afterNew", "M_ForecastLine");
        this.registerTableEvent("adempiere/po/afterNew", "DD_Order");
        this.registerTableEvent("adempiere/po/afterNew", "DD_OrderLine");
        this.registerTableEvent("adempiere/po/afterNew", "PP_Order");
        this.registerTableEvent("adempiere/po/afterNew", "PP_Order_BOMLine");
        this.registerTableEvent("adempiere/po/afterNew", "C_Order");
        this.registerTableEvent("adempiere/po/afterNew", "C_Order");
        this.registerTableEvent("adempiere/po/beforeChange", "M_Product");
        this.registerTableEvent("adempiere/po/afterChange", "C_Order");
        this.registerTableEvent("adempiere/po/afterChange", "C_OrderLine");
        this.registerTableEvent("adempiere/po/afterChange", "M_Requisition");
        this.registerTableEvent("adempiere/po/afterChange", "M_RequisitionLine");
        this.registerTableEvent("adempiere/po/afterChange", "M_Forecast");
        this.registerTableEvent("adempiere/po/afterChange", "M_ForecastLine");
        this.registerTableEvent("adempiere/po/afterChange", "DD_Order");
        this.registerTableEvent("adempiere/po/afterChange", "DD_OrderLine");
        this.registerTableEvent("adempiere/po/afterChange", "PP_Order");
        this.registerTableEvent("adempiere/po/afterChange", "PP_Order_BOMLine");
        this.registerTableEvent("adempiere/po/afterChange", "M_Forecast");
        this.registerTableEvent("adempiere/po/afterChange", "M_ForecastLine");
        this.registerTableEvent("adempiere/po/beforeDelete", "C_Order");
        this.registerTableEvent("adempiere/po/beforeDelete", "C_OrderLine");
        this.registerTableEvent("adempiere/po/beforeDelete", "M_Requisition");
        this.registerTableEvent("adempiere/po/beforeDelete", "M_RequisitionLine");
        this.registerTableEvent("adempiere/po/beforeDelete", "M_Forecast");
        this.registerTableEvent("adempiere/po/beforeDelete", "M_ForecastLine");
        this.registerTableEvent("adempiere/po/beforeDelete", "DD_Order");
        this.registerTableEvent("adempiere/po/beforeDelete", "DD_OrderLine");
        this.registerTableEvent("adempiere/po/beforeDelete", "PP_Order");
        this.registerTableEvent("adempiere/po/beforeDelete", "PP_Order_BOMLine");
        this.registerTableEvent("adempiere/po/beforeDelete", "M_Forecast");
        this.registerTableEvent("adempiere/po/beforeDelete", "M_ForecastLine");
        this.registerTableEvent("adempiere/doc/beforePrepare", "M_Forecast");
        this.registerTableEvent("adempiere/doc/beforeComplete", "M_ForecastLine");
        this.registerTableEvent("adempiere/doc/afterComplete", "M_Movement");
        this.registerTableEvent("adempiere/doc/afterComplete", "M_InOut");
        this.registerTableEvent("adempiere/doc/afterComplete", "C_Order");
        this.registerTableEvent("adempiere/doc/beforeReactivate", "DD_Order");
        this.registerTableEvent("adempiere/doc/afterComplete", "PP_Order");
        MFG_Validator.log.info("MFG MODEL VALIDATOR IS NOW INITIALIZED");
    }
    
    protected void doHandleEvent(final Event event) {
        final String type = event.getTopic();
        DocAction doc = null;
        boolean isDelete = false;
        boolean isReleased = false;
        boolean isVoided = false;
        boolean isChange = false;
        if (type.equals("adempiere/afterLogin")) {
            final LoginEventData eventData = (LoginEventData)this.getEventData(event);
            MFG_Validator.log.fine(" topic=" + event.getTopic() + " AD_Client_ID=" + eventData.getAD_Client_ID() + " AD_Org_ID=" + eventData.getAD_Org_ID() + " AD_Role_ID=" + eventData.getAD_Role_ID() + " AD_User_ID=" + eventData.getAD_User_ID());
        }
        else {
            this.setPo(this.getPO(event));
            this.setTrxName(this.po.get_TrxName());
            MFG_Validator.log.info(" topic=" + event.getTopic() + " po=" + this.po);
            isChange = ("adempiere/po/afterNew" == type || ("adempiere/po/afterChange" == type && MPPMRP.isChanged(this.po)));
            isDelete = ("adempiere/po/beforeDelete" == type);
            isReleased = false;
            isVoided = false;
            if (this.po instanceof DocAction) {
                doc = (DocAction)this.po;
            }
            else if (this.po instanceof MOrderLine) {
                doc = (DocAction)((MOrderLine)this.po).getParent();
            }
            if (doc != null) {
                final String docStatus = doc.getDocStatus();
                isReleased = ("IP".equals(docStatus) || "CO".equals(docStatus));
                isVoided = "VO".equals(docStatus);
            }
            if (this.po instanceof MProduct && "adempiere/po/beforeChange" == type && this.po.is_ValueChanged("C_UOM_ID") && MPPMRP.hasProductRecords((MProduct)this.po)) {
                throw new AdempiereException("@SaveUomError@");
            }
            if (isDelete || isVoided || !this.po.isActive()) {
                this.logEvent(event, this.po, type);
                MPPMRP.deleteMRP(this.po);
            }
            else if (this.po instanceof MOrder) {
                final MOrder order = (MOrder)this.po;
                if (isChange && !order.isSOTrx()) {
                    this.logEvent(event, this.po, type);
                    MPPMRP.C_Order(order);
                }
                else if (type == "adempiere/po/afterChange" && order.isSOTrx() && (isReleased || MPPMRP.isChanged((PO)order))) {
                    this.logEvent(event, this.po, type);
                    MPPMRP.C_Order(order);
                }
            }
            else if (this.po instanceof MOrderLine && isChange) {
                final MOrderLine ol = (MOrderLine)this.po;
                final MOrder order2 = ol.getParent();
                if (!order2.isSOTrx()) {
                    this.logEvent(event, this.po, type);
                    MPPMRP.C_OrderLine(ol);
                }
                else if (order2.isSOTrx() && isReleased) {
                    this.logEvent(event, this.po, type);
                }
            }
            else if (this.po instanceof MRequisition && isChange) {
                final MRequisition r = (MRequisition)this.po;
                this.logEvent(event, this.po, type);
                MFG_Validator.log.warning(event.getTopic());
                MPPMRP.M_Requisition(r);
            }
            else if (this.po instanceof MRequisitionLine && isChange) {
                final MRequisitionLine rl = (MRequisitionLine)this.po;
                this.logEvent(event, this.po, type);
                MPPMRP.M_RequisitionLine(rl);
            }
            else if (this.po instanceof X_M_Forecast && isChange) {
                final X_M_Forecast fl = (X_M_Forecast)this.po;
                this.logEvent(event, this.po, type);
                MPPMRP.M_Forecast(fl);
            }
            else if (this.po instanceof MForecastLine && isChange) {
                final MForecastLine mForecastLine = (MForecastLine)this.po;
                this.logEvent(event, this.po, type);
            }
            else if (this.po instanceof MPPOrder && isChange) {
                final MPPOrder order3 = (MPPOrder)this.po;
                this.logEvent(event, this.po, type);
                MPPMRP.PP_Order(order3);
            }
            else if (this.po instanceof MPPOrderBOMLine && isChange) {
                final MPPOrderBOMLine obl = (MPPOrderBOMLine)this.po;
                this.logEvent(event, this.po, type);
                MPPMRP.PP_Order_BOMLine(obl);
            }
            if (event.getTopic().equals("adempiere/po/afterNew")) {
                this.po = this.getPO(event);
                MFG_Validator.log.info(" topic=" + event.getTopic() + " po=" + this.po);
            }
            else if (event.getTopic().equals("adempiere/po/beforeChange")) {
                this.po = this.getPO(event);
                MFG_Validator.log.info(" topic=" + event.getTopic() + " po=" + this.po);
                if (this.po.get_TableName().equals("M_Product")) {
                    this.logEvent(event, this.po, type);
                }
            }
            if (this.po instanceof MInOut && type == "adempiere/doc/afterComplete") {
                this.logEvent(event, this.po, type);
                final MInOut inout = (MInOut)this.po;
                if (inout.isSOTrx()) {
                    MInOutLine[] lines;
                    for (int length = (lines = inout.getLines()).length, i = 0; i < length; ++i) {
                        final MInOutLine outline = lines[i];
                        this.updateMPPOrder(outline);
                    }
                }
                else {
                    MInOutLine[] lines2;
                    for (int length2 = (lines2 = inout.getLines()).length, j = 0; j < length2; ++j) {
                        final MInOutLine line = lines2[j];
                        final Collection<MOrderLine> olines = new Query(this.po.getCtx(), "C_OrderLine", "C_OrderLine_ID=? AND PP_Cost_Collector_ID IS NOT NULL", this.trxName).setParameters(new Object[] { line.getC_OrderLine_ID() }).list();
                        for (final MOrderLine oline : olines) {
                            if (oline.getQtyOrdered().compareTo(oline.getQtyDelivered()) >= 0) {
                                final MPPCostCollector cc = new MPPCostCollector(this.po.getCtx(), oline.getPP_Cost_Collector_ID(), this.trxName);
                                final String docStatus2 = cc.completeIt();
                                cc.setDocStatus(docStatus2);
                                cc.setDocAction("CL");
                                cc.saveEx(this.trxName);
                                return;
                            }
                        }
                    }
                }
            }
            else if (this.po instanceof MMovement && type == "adempiere/doc/afterReverseCorrect") {
                this.logEvent(event, this.po, type);
                final MMovement move = (MMovement)this.po;
                updateDDOrderQtyInOutBoundReverse(move);
            }
            else if (this.po instanceof MMovement && type == "adempiere/doc/afterComplete") {
                this.logEvent(event, this.po, type);
                final MMovement move = (MMovement)this.po;
                updateDDOrderQtyInOutBound(move);
            }
            else if (this.po instanceof MDDOrder && type == "adempiere/doc/beforeReactivate") {
                this.logEvent(event, this.po, type);
                final MDDOrder ddorder = (MDDOrder)this.po;
                checkActiveInOutBound(ddorder);
            }
            else if (this.po instanceof MPPOrder && type == "adempiere/doc/afterComplete") {
                this.logEvent(event, this.po, type);
                final MPPOrder mppOrder = (MPPOrder)this.po;
            }
        }
    }
    
    private boolean isInTransit(final MDDOrder order) {
        MDDOrderLine[] lines = null;
        for (int length = (lines = order.getLines((boolean)(1 != 0), (String)null)).length, i = 0; i < length; ++i) {
            final MDDOrderLine line = lines[i];
            if (line.getQtyInTransit().signum() != 0) {
                return true;
            }
        }
        return false;
    }
    
    private void updateMPPOrder(final MInOutLine outline) {
        MPPOrder order = null;
        BigDecimal qtyShipment = Env.ZERO;
        final MInOut inout = outline.getParent();
        final String movementType = inout.getMovementType();
        int C_OrderLine_ID = 0;
        if ("C-".equals(movementType)) {
            C_OrderLine_ID = outline.getC_OrderLine_ID();
            qtyShipment = outline.getMovementQty();
        }
        else if ("C+".equals(movementType)) {
            final MRMALine rmaline = new MRMALine(outline.getCtx(), outline.getM_RMALine_ID(), (String)null);
            final MInOutLine line = (MInOutLine)rmaline.getM_InOutLine();
            C_OrderLine_ID = line.getC_OrderLine_ID();
            qtyShipment = outline.getMovementQty().negate();
        }
        order = (MPPOrder)new Query(outline.getCtx(), "PP_Order", " C_OrderLine_ID = ?  AND DocStatus IN  (?,?) AND EXISTS (SELECT 1 FROM  PP_Order_BOM  WHERE PP_Order_BOM.PP_Order_ID=PP_Order.PP_Order_ID AND PP_Order_BOM.BOMType =? )", outline.get_TrxName()).setParameters(new Object[] { C_OrderLine_ID, "IP", "CO", "K" }).firstOnly();
        if (order == null) {
            return;
        }
        if ("IP".equals(order.getDocStatus())) {
            order.completeIt();
            order.setDocStatus("CO");
            order.setDocAction("CL");
            order.saveEx(this.trxName);
        }
        if ("CO".equals(order.getDocStatus())) {
            final String description = (order.getDescription() != null) ? order.getDescription() : (Msg.translate(inout.getCtx(), "M_InOut_ID") + " : " + Msg.translate(inout.getCtx(), "DocumentNo"));
            order.setDescription(description);
            order.updateMakeToKit(qtyShipment);
            order.saveEx(this.trxName);
        }
        if (order.getQtyToDeliver().compareTo(Env.ZERO) == 0) {
            order.closeIt();
            order.setDocStatus("CL");
            order.setDocAction("--");
            order.saveEx(this.trxName);
        }
    }
    
    private void logEvent(final Event event, final PO po, final String msg) {
        MFG_Validator.log.info("LiberoMFG >> ModelValidator // " + event.getTopic() + " po=" + po + " MESSAGE =" + msg);
    }
    
    private void setPo(final PO eventPO) {
        this.po = eventPO;
    }
    
    private void setTrxName(final String get_TrxName) {
        this.trxName = get_TrxName;
    }
    
    private static String updateDDOrderQtyInOutBound(final MMovement move) {
        boolean isOutbound = false;
        boolean isInbound = false;
        if (move.get_ValueAsBoolean("IsOutbound")) {
            isOutbound = true;
        }
        else if (move.get_ValueAsBoolean("IsInbound")) {
            isInbound = true;
        }
        if (isOutbound || isInbound) {
            final MMovementLine[] moveLines = move.getLines(false);
            MMovementLine[] array;
            for (int length = (array = moveLines).length, i = 0; i < length; ++i) {
                final MMovementLine moveLine = array[i];
                final int DD_OrderLine_ID = moveLine.get_ValueAsInt("DD_OrderLine_ID");
                if (DD_OrderLine_ID <= 0) {
                    return "Bound move line : " + moveLine.get_Value("Line") + " have no referenced DD_OrderLine_ID";
                }
                final MDDOrderLine ddLine = new MDDOrderLine(move.getCtx(), DD_OrderLine_ID, move.get_TrxName());
                if (isOutbound && moveLine.getMovementQty().compareTo(BigDecimal.ZERO) > 0) {
                    final String jml = ddLine.get_ValueAsString("qtyoutbound");
                    BigDecimal qtyDDOutBound = new BigDecimal(jml);
                    qtyDDOutBound = qtyDDOutBound.add(moveLine.getMovementQty());
                    ddLine.set_ValueOfColumn("qtyoutbound", (Object)qtyDDOutBound);
                }
                else if (isInbound && moveLine.getMovementQty().compareTo(BigDecimal.ZERO) > 0) {
                    final String jml = ddLine.get_ValueAsString("qtyinbound");
                    BigDecimal qtyDDInBound = new BigDecimal(jml);
                    qtyDDInBound = qtyDDInBound.add(moveLine.getMovementQty());
                    ddLine.set_ValueOfColumn("qtyinbound", (Object)qtyDDInBound);
                }
                ddLine.saveEx(move.get_TrxName());
            }
        }
        return "";
    }
    
    private static String updateDDOrderQtyInOutBoundReverse(final MMovement move) {
        boolean isOutbound = false;
        boolean isInbound = false;
        if (move.get_ValueAsBoolean("IsOutbound")) {
            isOutbound = true;
        }
        else if (move.get_ValueAsBoolean("IsInbound")) {
            isInbound = true;
        }
        if (isOutbound || isInbound) {
            final MMovementLine[] moveLines = move.getLines(false);
            MMovementLine[] array;
            for (int length = (array = moveLines).length, i = 0; i < length; ++i) {
                final MMovementLine moveLine = array[i];
                final int DD_OrderLine_ID = moveLine.get_ValueAsInt("DD_OrderLine_ID");
                if (DD_OrderLine_ID <= 0) {
                    return "Bound move line : " + moveLine.get_Value("Line") + " have no referenced DD_OrderLine_ID";
                }
                final MDDOrderLine ddLine = new MDDOrderLine(move.getCtx(), DD_OrderLine_ID, move.get_TrxName());
                if (isOutbound && moveLine.getMovementQty().compareTo(BigDecimal.ZERO) > 0) {
                    final String jml = ddLine.get_ValueAsString("QtyOutbound");
                    BigDecimal qtyDDOutBound = new BigDecimal(jml);
                    qtyDDOutBound = qtyDDOutBound.add(moveLine.getMovementQty().negate());
                    ddLine.set_ValueOfColumn("QtyOutbound", (Object)qtyDDOutBound);
                }
                else if (isInbound && moveLine.getMovementQty().compareTo(BigDecimal.ZERO) > 0) {
                    final String jml = ddLine.get_ValueAsString("qtyinbound");
                    BigDecimal qtyDDInBound = new BigDecimal(jml);
                    qtyDDInBound = qtyDDInBound.add(moveLine.getMovementQty().negate());
                    ddLine.set_ValueOfColumn("QtyInbound", (Object)qtyDDInBound);
                }
                ddLine.saveEx(move.get_TrxName());
            }
        }
        return "";
    }
    
    private static String checkActiveInOutBound(final MDDOrder ddOrder) {
        final String sqlWhere = "DD_Order_ID=" + ddOrder.getDD_Order_ID() + " AND DocStatus IN ('CO','CL') AND AD_Client_ID=" + ddOrder.getAD_Client_ID();
        final boolean match = new Query(ddOrder.getCtx(), "M_Movement", sqlWhere, ddOrder.get_TrxName()).match();
        if (match) {
            throw new AdempiereException("Active OutBound Or InBound Exist");
        }
        return "";
    }
}
