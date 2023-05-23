// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.infowindow;

import org.compiere.model.MNote;
import org.compiere.util.Util;
import org.compiere.model.MMessage;
import org.compiere.util.DB;
import org.compiere.model.MProductPO;
import org.compiere.wf.MWorkflow;
import org.eevolution.model.MPPProductBOM;
import org.compiere.model.PO;
import org.compiere.model.MDocType;
import org.libero.model.MRequisition;
import org.eevolution.model.I_PP_Product_Planning;
import org.compiere.model.MLocator;
import org.libero.model.MDDNetworkDistributionLine;
import org.eevolution.model.MDDOrderLine;
import org.eevolution.model.MDDOrder;
import org.compiere.model.MOrg;
import org.compiere.model.MWarehouse;
import org.libero.model.MDDNetworkDistribution;
import org.adempiere.exceptions.AdempiereException;
import java.util.Collection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import org.libero.model.MPPOrder;
import org.compiere.util.Msg;
import org.compiere.model.MProduct;
import org.compiere.util.TimeUtil;
import org.compiere.model.MResource;
import org.compiere.model.Query;
import org.libero.model.MPPMRP;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Env;
import org.compiere.model.MBPartner;
import org.compiere.util.CCache;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.eevolution.model.MPPProductPlanning;
import org.compiere.process.SvrProcess;

public class CalculateMaterialPlan extends SvrProcess
{
    private MPPProductPlanning m_product_planning;
    private BigDecimal QtyProjectOnHand;
    private BigDecimal QtyGrossReqs;
    private BigDecimal QtyScheduledReceipts;
    private Timestamp DatePromisedFrom;
    private Timestamp DatePromisedTo;
    private Timestamp Today;
    private Timestamp TimeFence;
    private Timestamp Planning_Horizon;
    private int docTypeReq_ID;
    private int docTypeMO_ID;
    private int docTypeMF_ID;
    private int docTypeDO_ID;
    private int count_MO;
    private int count_MR;
    private int count_DO;
    private int count_Msg;
    private static CCache<String, Integer> dd_order_id_cache;
    private static CCache<Integer, MBPartner> partner_cache;
    private boolean p_DeleteMRP;
    private boolean p_IsRequiredDRP;
    private int duration;
    private int p_Planner_ID;
    private StringBuilder endResult;
    
    static {
        CalculateMaterialPlan.dd_order_id_cache = (CCache<String, Integer>)new CCache("DD_Order_ID", 50);
        CalculateMaterialPlan.partner_cache = (CCache<Integer, MBPartner>)new CCache("C_BPartner", 50);
    }
    
    public CalculateMaterialPlan() {
        this.m_product_planning = null;
        this.QtyProjectOnHand = Env.ZERO;
        this.QtyGrossReqs = Env.ZERO;
        this.QtyScheduledReceipts = Env.ZERO;
        this.DatePromisedFrom = null;
        this.DatePromisedTo = null;
        this.Today = new Timestamp(System.currentTimeMillis());
        this.TimeFence = null;
        this.Planning_Horizon = null;
        this.docTypeReq_ID = 0;
        this.docTypeMO_ID = 0;
        this.docTypeMF_ID = 0;
        this.docTypeDO_ID = 0;
        this.count_MO = 0;
        this.count_MR = 0;
        this.count_DO = 0;
        this.count_Msg = 0;
        this.duration = 0;
        this.endResult = new StringBuilder();
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("DeleteMRP")) {
                    this.p_DeleteMRP = para[i].getParameterAsBoolean();
                }
                else if (name.equals("IsRequiredDRP")) {
                    this.p_IsRequiredDRP = para[i].getParameterAsBoolean();
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        MProduct product = null;
        int BeforePP_MRP_ID = 0;
        Timestamp BeforeDateStartSchedule = null;
        Timestamp POQDateStartSchedule = null;
        final int AD_Client_ID = this.getAD_Client_ID();
        int AD_Org_ID = -1;
        int S_Resource_ID = -1;
        int M_Warehouse_ID = -1;
        final int lowlevel = MPPMRP.getMaxLowLevel(this.getCtx(), this.get_TrxName());
        this.log.info("Low Level Is :" + lowlevel);
        final String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? AND T_Selection.T_Selection_ID=PP_MRP.PP_MRP_ID)";
        final List<MPPMRP> mrpset = new Query(Env.getCtx(), "PP_MRP", whereClause, this.get_TrxName()).setParameters(new Object[] { this.getAD_PInstance_ID() }).list();
        for (final MPPMRP mrp : mrpset) {
            if (this.p_DeleteMRP) {
                this.deleteMRP(mrp);
            }
            if ("D".equals(mrp.getTypeMRP()) && "FCT".equals(mrp.getOrderType()) && mrp.getDatePromised().compareTo(this.getToday()) <= 0) {
                continue;
            }
            MPPMRP.C_Order_ID = mrp.getC_Order_ID();
            MPPMRP.C_OrderLine_ID = mrp.getC_OrderLine_ID();
            this.duration = 0;
            final int PP_MRP_ID = mrp.getPP_MRP_ID();
            mrp.getTypeMRP();
            mrp.getOrderType();
            final Timestamp DatePromised = mrp.getDatePromised();
            mrp.getDateStartSchedule();
            final BigDecimal Qty = mrp.getQty();
            final int M_Product_ID = mrp.getM_Product_ID();
            AD_Org_ID = mrp.getAD_Org_ID();
            M_Warehouse_ID = mrp.getM_Warehouse_ID();
            S_Resource_ID = mrp.getS_Resource_ID();
            if (S_Resource_ID == 0) {
                this.log.severe("NO PLANT / RESOURCE FOR " + mrp.getM_Product());
            }
            else {
                final MResource plant = (MResource)new Query(Env.getCtx(), "S_Resource", "S_Resource_ID=?", this.get_TrxName()).setParameters(new Object[] { S_Resource_ID }).first();
                this.log.info("Run MRP to Plant: " + plant.getName());
                this.Planning_Horizon = TimeUtil.addDays(this.getToday(), plant.getPlanningHorizon());
                if (product == null || product.get_ID() != mrp.getM_Product_ID()) {
                    if (this.QtyGrossReqs.signum() != 0) {
                        if (product == null) {
                            throw new IllegalStateException("MRP Internal Error: QtyGrossReqs=" + this.QtyGrossReqs + " and we do not have previous demand defined");
                        }
                        if ("POQ".equals(this.m_product_planning.getOrder_Policy()) && POQDateStartSchedule.compareTo(this.Planning_Horizon) < 0) {
                            BeforeDateStartSchedule = POQDateStartSchedule;
                            this.calculatePlan(mrp, AD_Client_ID, AD_Org_ID, M_Warehouse_ID, BeforePP_MRP_ID, product, BeforeDateStartSchedule);
                        }
                        else if ("LFL".equals(this.m_product_planning.getOrder_Policy()) && BeforeDateStartSchedule.compareTo(this.Planning_Horizon) <= 0) {
                            this.calculatePlan(mrp, AD_Client_ID, AD_Org_ID, M_Warehouse_ID, BeforePP_MRP_ID, product, BeforeDateStartSchedule);
                        }
                        this.QtyGrossReqs = Env.ZERO;
                    }
                    product = MProduct.get(this.getCtx(), M_Product_ID);
                    this.log.info("Calculate Plan for this Product:" + product);
                    this.setProduct(AD_Client_ID, AD_Org_ID, S_Resource_ID, M_Warehouse_ID, product);
                    if (this.m_product_planning == null) {
                        continue;
                    }
                    if ("POQ".equals(this.m_product_planning.getOrder_Policy())) {
                        POQDateStartSchedule = null;
                    }
                }
                if (this.m_product_planning == null) {
                    continue;
                }
                final int daysPOQ = this.m_product_planning.getOrder_Period().intValueExact() - 1;
                if ("POQ".equals(this.m_product_planning.getOrder_Policy()) && this.DatePromisedTo != null && DatePromised.compareTo(this.DatePromisedTo) > 0) {
                    this.calculatePlan(mrp, AD_Client_ID, AD_Org_ID, M_Warehouse_ID, PP_MRP_ID, product, this.DatePromisedFrom);
                    this.DatePromisedFrom = DatePromised;
                    this.DatePromisedTo = TimeUtil.addDays(DatePromised, (daysPOQ < 0) ? 0 : daysPOQ);
                    POQDateStartSchedule = DatePromised;
                }
                else if (POQDateStartSchedule == null) {
                    this.DatePromisedFrom = DatePromised;
                    this.DatePromisedTo = TimeUtil.addDays(DatePromised, (daysPOQ < 0) ? 0 : daysPOQ);
                    POQDateStartSchedule = DatePromised;
                }
                if (DatePromised.compareTo(this.getToday()) < 0) {
                    final String comment = String.valueOf(Msg.translate(this.getCtx(), "DatePromised")) + ": " + DatePromised;
                    this.createMRPNote("MRP-150", AD_Org_ID, PP_MRP_ID, product, MPPMRP.getDocumentNo(PP_MRP_ID), Qty, comment);
                }
                BeforePP_MRP_ID = PP_MRP_ID;
                if ("POQ".equals(this.m_product_planning.getOrder_Policy())) {
                    if (this.DatePromisedTo == null || DatePromised.compareTo(this.DatePromisedTo) > 0) {
                        continue;
                    }
                    this.QtyGrossReqs = this.QtyGrossReqs.add(Qty);
                    this.log.info("Accumulation   QtyGrossReqs:" + this.QtyGrossReqs);
                    this.log.info("DatePromised:" + DatePromised);
                    this.log.info("DatePromisedTo:" + this.DatePromisedTo);
                }
                else {
                    if (!"LFL".equals(this.m_product_planning.getOrder_Policy())) {
                        continue;
                    }
                    this.QtyGrossReqs = this.QtyGrossReqs.add(Qty);
                    BeforeDateStartSchedule = DatePromised;
                    this.calculatePlan(mrp, AD_Client_ID, AD_Org_ID, M_Warehouse_ID, PP_MRP_ID, product, BeforeDateStartSchedule);
                }
            }
        }
        if (this.QtyGrossReqs.signum() != 0 && product != null) {
            if ("POQ".equals(this.m_product_planning.getOrder_Policy()) && POQDateStartSchedule.compareTo(this.Planning_Horizon) < 0) {
                BeforeDateStartSchedule = POQDateStartSchedule;
                this.calculatePlan(null, AD_Client_ID, AD_Org_ID, M_Warehouse_ID, BeforePP_MRP_ID, product, BeforeDateStartSchedule);
            }
            else if ("LFL".equals(this.m_product_planning.getOrder_Policy()) && BeforeDateStartSchedule.compareTo(this.Planning_Horizon) <= 0) {
                this.calculatePlan(null, AD_Client_ID, AD_Org_ID, M_Warehouse_ID, BeforePP_MRP_ID, product, BeforeDateStartSchedule);
            }
        }
        else if (product != null) {
            this.getNetRequirements(AD_Client_ID, AD_Org_ID, M_Warehouse_ID, product, null);
        }
        if (this.endResult.length() < 1) {
            this.endResult.append("Total MO:  " + this.count_MO);
            this.endResult.append(" Total Req:  " + this.count_MR + MPPOrder.count_MR);
            this.endResult.append(" Total DO:  " + this.count_DO);
            this.endResult.append(" Total Msg: " + this.count_Msg);
        }
        return this.endResult.toString();
    }
    
    private void calculatePlan(MPPMRP mrp, final int AD_Client_ID, final int AD_Org_ID, final int M_Warehouse_ID, final int PP_MRP_ID, final MProduct product, final Timestamp DemandDateStartSchedule) throws SQLException {
        if (mrp == null && PP_MRP_ID > 0) {
            mrp = new MPPMRP(Env.getCtx(), PP_MRP_ID, this.get_TrxName());
        }
        this.log.info("Creating Plan ...");
        if (this.m_product_planning.getM_Product_ID() != product.get_ID()) {
            throw new IllegalStateException("MRP Internal Error: DataPlanningProduct(" + this.m_product_planning.getM_Product_ID() + ")" + " <> Product(" + product + ")");
        }
        final BigDecimal yield = BigDecimal.valueOf(this.m_product_planning.getYield());
        if (yield.signum() != 0) {
            this.QtyGrossReqs = this.QtyGrossReqs.multiply(Env.ONEHUNDRED).divide(yield, 4, RoundingMode.HALF_UP);
        }
        BigDecimal QtyNetReqs = this.getNetRequirements(AD_Client_ID, AD_Org_ID, M_Warehouse_ID, product, DemandDateStartSchedule);
        BigDecimal QtyPlanned = Env.ZERO;
        ((PO)this.m_product_planning).dump();
        this.log.info("                    Product:" + product);
        this.log.info(" Demand Date Start Schedule:" + DemandDateStartSchedule);
        this.log.info("           DatePromisedFrom:" + this.DatePromisedFrom + " DatePromisedTo:" + this.DatePromisedTo);
        this.log.info("                Qty Planned:" + QtyPlanned);
        this.log.info("     Qty Scheduled Receipts:" + this.QtyScheduledReceipts);
        this.log.info("           QtyProjectOnHand:" + this.QtyProjectOnHand);
        this.log.info("               QtyGrossReqs:" + this.QtyGrossReqs);
        this.log.info("                     Supply:" + this.QtyScheduledReceipts.add(this.QtyProjectOnHand));
        this.log.info("                 QtyNetReqs:" + QtyNetReqs);
        if (QtyNetReqs.signum() > 0) {
            this.QtyProjectOnHand = QtyNetReqs;
            QtyNetReqs = Env.ZERO;
            this.QtyScheduledReceipts = Env.ZERO;
            QtyPlanned = Env.ZERO;
            this.QtyGrossReqs = Env.ZERO;
            return;
        }
        QtyPlanned = QtyNetReqs.negate();
        this.QtyGrossReqs = Env.ZERO;
        this.QtyScheduledReceipts = Env.ZERO;
        if (QtyPlanned.signum() > 0 && this.m_product_planning.getOrder_Min().signum() > 0) {
            if (this.m_product_planning.getOrder_Min().compareTo(QtyPlanned) > 0) {
                final String comment = String.valueOf(Msg.translate(this.getCtx(), "Order_Min")) + ":" + this.m_product_planning.getOrder_Min();
                this.createMRPNote("MRP-080", AD_Org_ID, PP_MRP_ID, product, null, QtyPlanned, comment);
            }
            QtyPlanned = QtyPlanned.max(this.m_product_planning.getOrder_Min());
        }
        if (this.m_product_planning.getOrder_Pack().signum() > 0 && QtyPlanned.signum() > 0) {
            QtyPlanned = this.m_product_planning.getOrder_Pack().multiply(QtyPlanned.divide(this.m_product_planning.getOrder_Pack(), 0, 0));
        }
        if (QtyPlanned.compareTo(this.m_product_planning.getOrder_Max()) > 0 && this.m_product_planning.getOrder_Max().signum() > 0) {
            final String comment = String.valueOf(Msg.translate(this.getCtx(), "Order_Max")) + ":" + this.m_product_planning.getOrder_Max();
            this.createMRPNote("MRP-090", AD_Org_ID, PP_MRP_ID, product, null, QtyPlanned, comment);
        }
        this.QtyProjectOnHand = QtyPlanned.add(QtyNetReqs);
        this.log.info("QtyNetReqs:" + QtyNetReqs);
        this.log.info("QtyPlanned:" + QtyPlanned);
        this.log.info("QtyProjectOnHand:" + this.QtyProjectOnHand);
        if (this.TimeFence != null && DemandDateStartSchedule.compareTo(this.TimeFence) < 0) {
            final String comment = String.valueOf(Msg.translate(this.getCtx(), "TimeFence")) + ":" + this.m_product_planning.getTimeFence() + "-" + Msg.getMsg(this.getCtx(), "Date") + ":" + this.TimeFence + " " + Msg.translate(this.getCtx(), "DatePromised") + ":" + DemandDateStartSchedule;
            this.createMRPNote("MRP-100", AD_Org_ID, PP_MRP_ID, product, null, QtyPlanned, comment);
        }
        if (!this.m_product_planning.isCreatePlan() && QtyPlanned.signum() > 0) {
            this.createMRPNote("MRP-020", AD_Org_ID, PP_MRP_ID, product, null, QtyPlanned, null);
            return;
        }
        if (QtyPlanned.signum() > 0) {
            int loops = 1;
            if (this.m_product_planning.getOrder_Policy().equals("FOQ")) {
                if (this.m_product_planning.getOrder_Qty().signum() != 0) {
                    loops = QtyPlanned.divide(this.m_product_planning.getOrder_Qty(), 0, 0).intValueExact();
                }
                QtyPlanned = this.m_product_planning.getOrder_Qty();
            }
            for (int ofq = 1; ofq <= loops; ++ofq) {
                this.log.info("Is Purchased: " + product.isPurchased() + " Is BOM: " + product.isBOM());
                try {
                    this.createSupply(mrp, AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule);
                }
                catch (Exception e) {
                    this.createMRPNote("MRP-160", AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule, e);
                    this.endResult.append(e.getMessage());
                }
            }
        }
        else {
            this.log.info("No Create Plan");
        }
    }
    
    private BigDecimal getNetRequirements(final int AD_Client_ID, final int AD_Org_ID, final int M_Warehouse_ID, final MProduct product, final Timestamp DemandDateStartSchedule) throws SQLException {
        BigDecimal QtyNetReqs = this.QtyProjectOnHand.subtract(this.QtyGrossReqs);
        final ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(AD_Client_ID);
        parameters.add(AD_Org_ID);
        parameters.add(product.get_ID());
        parameters.add(M_Warehouse_ID);
        parameters.add("S");
        parameters.add("CO");
        parameters.add("IP");
        parameters.add("DR");
        parameters.add(true);
        final Collection<MPPMRP> mrps = new Query(this.getCtx(), "PP_MRP", "AD_Client_ID=? AND AD_Org_ID=? AND M_Product_ID=? AND M_Warehouse_ID=? AND TypeMRP=? AND DocStatus IN (?,?,?) AND Qty<>0 AND IsAvailable=?", this.get_TrxName()).setParameters((List)parameters).setOrderBy("DateStartSchedule").list();
        for (final MPPMRP mrp : mrps) {
            if (mrp.isReleased()) {
                this.QtyScheduledReceipts = this.QtyScheduledReceipts.add(mrp.getQty());
            }
            if (DemandDateStartSchedule != null) {
                if (mrp.isReleased() && QtyNetReqs.negate().signum() > 0 && mrp.getDateStartSchedule() != null && mrp.getDateStartSchedule().compareTo(DemandDateStartSchedule) < 0) {
                    final String comment = String.valueOf(Msg.translate(this.getCtx(), "DateStartSchedule")) + ":" + mrp.getDateStartSchedule() + " " + Msg.translate(this.getCtx(), "DatePromised") + ":" + DemandDateStartSchedule;
                    this.createMRPNote("MRP-030", mrp, product, comment);
                }
                if (mrp.isReleased() && QtyNetReqs.negate().signum() > 0 && mrp.getDateStartSchedule() != null && mrp.getDateStartSchedule().compareTo(DemandDateStartSchedule) > 0) {
                    final String comment = String.valueOf(Msg.translate(this.getCtx(), "DateStartSchedule")) + ":" + mrp.getDateStartSchedule() + " " + Msg.translate(this.getCtx(), "DatePromised") + ":" + DemandDateStartSchedule;
                    this.createMRPNote("MRP-040", mrp, product, comment);
                }
                if (!mrp.isReleased() && QtyNetReqs.negate().signum() > 0 && mrp.getDateStartSchedule() != null && mrp.getDatePromised().compareTo(this.getToday()) >= 0) {
                    final String comment = String.valueOf(Msg.translate(this.getCtx(), "DatePromised")) + ":" + mrp.getDatePromised();
                    this.createMRPNote("MRP-060", mrp, product, comment);
                }
                if (!mrp.isReleased() && QtyNetReqs.negate().signum() > 0 && mrp.getDateStartSchedule() != null && mrp.getDatePromised().compareTo(this.getToday()) < 0) {
                    final String comment = String.valueOf(Msg.translate(this.getCtx(), "DatePromised")) + ":" + mrp.getDatePromised();
                    this.createMRPNote("MRP-070", mrp, product, comment);
                }
                if (mrp.isReleased() && mrp.getDateStartSchedule() != null && mrp.getDatePromised().compareTo(this.getToday()) < 0) {
                    final String comment = String.valueOf(Msg.translate(this.getCtx(), "DatePromised")) + ":" + mrp.getDatePromised();
                    this.createMRPNote("MRP-110", mrp, product, comment);
                }
                mrp.setIsAvailable(false);
                mrp.saveEx();
                QtyNetReqs = QtyNetReqs.add(mrp.getQty());
                if (QtyNetReqs.signum() >= 0) {
                    return QtyNetReqs;
                }
                continue;
            }
            else {
                if (mrp.isReleased() && this.QtyScheduledReceipts.signum() > 0) {
                    final String comment = String.valueOf(Msg.translate(this.getCtx(), "DatePromised")) + ":" + mrp.getDatePromised();
                    this.createMRPNote("MRP-050", mrp, product, comment);
                }
                mrp.setIsAvailable(false);
                mrp.saveEx();
                QtyNetReqs = QtyNetReqs.add(mrp.getQty());
            }
        }
        return QtyNetReqs;
    }
    
    private void setProduct(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID, final MProduct product) throws SQLException {
        this.DatePromisedTo = null;
        this.DatePromisedFrom = null;
        this.m_product_planning = this.getProductPlanning(AD_Client_ID, AD_Org_ID, S_Resource_ID, M_Warehouse_ID, product);
        if (this.m_product_planning == null) {
            this.createMRPNote("MRP-120", AD_Org_ID, 0, product, null, null, (String)null);
            return;
        }
        if (this.m_product_planning.getTimeFence().signum() > 0) {
            this.TimeFence = TimeUtil.addDays(this.getToday(), this.m_product_planning.getTimeFence().intValueExact());
        }
        this.QtyProjectOnHand = this.getQtyOnHand(this.m_product_planning);
        if (this.QtyProjectOnHand.signum() < 0) {
            final String comment = String.valueOf(Msg.translate(this.getCtx(), "QtyOnHand")) + ": " + this.QtyProjectOnHand;
            this.createMRPNote("MRP-140", AD_Org_ID, 0, product, null, this.QtyProjectOnHand, comment);
        }
        if (this.m_product_planning.getSafetyStock().signum() > 0 && this.m_product_planning.getSafetyStock().compareTo(this.QtyProjectOnHand) > 0) {
            final String comment = String.valueOf(Msg.translate(this.getCtx(), "QtyOnHand")) + ": " + this.QtyProjectOnHand + " " + Msg.translate(this.getCtx(), "SafetyStock") + ": " + this.m_product_planning.getSafetyStock();
            this.createMRPNote("MRP-001", AD_Org_ID, 0, product, null, this.QtyProjectOnHand, comment);
        }
        this.log.info("QtyOnHand :" + this.QtyProjectOnHand);
    }
    
    protected void createSupply(final MPPMRP mrp, final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, final BigDecimal QtyPlanned, final Timestamp DemandDateStartSchedule) throws AdempiereException, SQLException {
        if (this.isRequiredDRP() && this.m_product_planning.getDD_NetworkDistribution_ID() > 0) {
            this.createDDOrder(AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule);
        }
        else if (product.isPurchased()) {
            this.createRequisition(PP_MRP_ID, AD_Org_ID, product, QtyPlanned, DemandDateStartSchedule);
        }
        else {
            if (!product.isBOM()) {
                throw new IllegalStateException("MRP Internal Error: Don't know what document to create for " + product + "(" + this.m_product_planning + ")");
            }
            this.createPPOrder(mrp, AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule);
        }
    }
    
    protected void createDDOrder(final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, final BigDecimal QtyPlanned, final Timestamp DemandDateStartSchedule) throws AdempiereException, SQLException {
        if (this.m_product_planning.getDD_NetworkDistribution_ID() == 0) {
            this.createMRPNote("DRP-060", AD_Org_ID, PP_MRP_ID, product, null, null, (String)null);
        }
        final MDDNetworkDistribution network = MDDNetworkDistribution.get(this.getCtx(), this.m_product_planning.getDD_NetworkDistribution_ID());
        final MDDNetworkDistributionLine[] network_lines = network.getLines(this.m_product_planning.getM_Warehouse_ID());
        int M_Shipper_ID = 0;
        MDDOrder order = null;
        Integer DD_Order_ID = 0;
        MDDNetworkDistributionLine[] array;
        for (int length = (array = network_lines).length, i = 0; i < length; ++i) {
            final MDDNetworkDistributionLine network_line = array[i];
            final MWarehouse source = MWarehouse.get(this.getCtx(), network_line.getM_WarehouseSource_ID());
            final MLocator locator = source.getDefaultLocator();
            final MWarehouse target = MWarehouse.get(this.getCtx(), network_line.getM_Warehouse_ID());
            final MLocator locator_to = target.getDefaultLocator();
            BigDecimal transfertTime = network_line.getTransfertTime();
            if (transfertTime.compareTo(Env.ZERO) <= 0) {
                transfertTime = this.m_product_planning.getTransfertTime();
            }
            if (locator == null || locator_to == null) {
                final String comment = String.valueOf(Msg.translate(this.getCtx(), "M_WarehouseSource_ID")) + ":" + source.getName();
                this.createMRPNote("DRP-001", AD_Org_ID, PP_MRP_ID, product, null, null, comment);
            }
            else {
                final MWarehouse[] wsts = MWarehouse.getInTransitForOrg(this.getCtx(), source.getAD_Org_ID());
                if (wsts == null || wsts.length == 0) {
                    final String comment2 = String.valueOf(Msg.translate(this.getCtx(), "Name")) + ":" + MOrg.get(this.getCtx(), AD_Org_ID).getName();
                    this.createMRPNote("DRP-010", AD_Org_ID, PP_MRP_ID, product, null, null, comment2);
                }
                else if (network_line.getM_Shipper_ID() == 0) {
                    final String comment2 = String.valueOf(Msg.translate(this.getCtx(), "Name")) + ":" + network.getName();
                    this.createMRPNote("DRP-030", AD_Org_ID, PP_MRP_ID, product, null, null, comment2);
                }
                else {
                    if (M_Shipper_ID != network_line.getM_Shipper_ID()) {
                        final MOrg org = MOrg.get(this.getCtx(), locator_to.getAD_Org_ID());
                        final int C_BPartner_ID = org.getLinkedC_BPartner_ID(this.get_TrxName());
                        if (C_BPartner_ID == 0) {
                            final String comment3 = String.valueOf(Msg.translate(this.getCtx(), "Name")) + ":" + MOrg.get(this.getCtx(), AD_Org_ID).getName();
                            this.createMRPNote("DRP-020", AD_Org_ID, PP_MRP_ID, product, null, null, comment3);
                            continue;
                        }
                        final MBPartner bp = this.getBPartner(C_BPartner_ID);
                        DD_Order_ID = this.getDDOrder_ID(AD_Org_ID, wsts[0].get_ID(), network_line.getM_Shipper_ID(), bp.getC_BPartner_ID(), DemandDateStartSchedule);
                        if (DD_Order_ID <= 0) {
                            order = new MDDOrder(this.getCtx(), 0, this.get_TrxName());
                            order.setAD_Org_ID(target.getAD_Org_ID());
                            order.setC_BPartner_ID(C_BPartner_ID);
                            order.setAD_User_ID(bp.getPrimaryAD_User_ID());
                            order.setC_DocType_ID(this.docTypeDO_ID);
                            order.setM_Warehouse_ID(wsts[0].get_ID());
                            order.setDocAction("CO");
                            order.setDateOrdered(this.getToday());
                            order.setDatePromised(DemandDateStartSchedule);
                            order.setM_Shipper_ID(network_line.getM_Shipper_ID());
                            order.setIsInDispute(false);
                            order.setIsInTransit(false);
                            order.setSalesRep_ID(this.m_product_planning.getPlanner_ID());
                            order.saveEx();
                            DD_Order_ID = order.get_ID();
                            final String key = String.valueOf(order.getAD_Org_ID()) + "#" + order.getM_Warehouse_ID() + "#" + network_line.getM_Shipper_ID() + "#" + C_BPartner_ID + "#" + DemandDateStartSchedule + "DR";
                            CalculateMaterialPlan.dd_order_id_cache.put(key, DD_Order_ID);
                        }
                        else {
                            order = new MDDOrder(this.getCtx(), (int)DD_Order_ID, this.get_TrxName());
                        }
                        M_Shipper_ID = network_line.getM_Shipper_ID();
                    }
                    final BigDecimal QtyOrdered = QtyPlanned.multiply(network_line.getPercent()).divide(Env.ONEHUNDRED);
                    final MDDOrderLine oline = new MDDOrderLine(this.getCtx(), 0, this.get_TrxName());
                    oline.setDD_Order_ID(order.getDD_Order_ID());
                    oline.setAD_Org_ID(target.getAD_Org_ID());
                    oline.setM_Locator_ID(locator.getM_Locator_ID());
                    oline.setM_LocatorTo_ID(locator_to.getM_Locator_ID());
                    oline.setM_Product_ID(this.m_product_planning.getM_Product_ID());
                    oline.setDateOrdered(this.getToday());
                    oline.setDatePromised(DemandDateStartSchedule);
                    oline.setQtyEntered(QtyOrdered);
                    oline.setQtyOrdered(QtyOrdered);
                    oline.setTargetQty(MPPMRP.getQtyReserved(this.getCtx(), target.getM_Warehouse_ID(), this.m_product_planning.getM_Product_ID(), DemandDateStartSchedule, this.get_TrxName()));
                    oline.setIsInvoiced(false);
                    oline.saveEx();
                    final List<MPPMRP> mrpList = new Query(this.getCtx(), "PP_MRP", "DD_OrderLine_ID=?", this.get_TrxName()).setParameters(new Object[] { oline.getDD_OrderLine_ID() }).list();
                    for (final MPPMRP mrp : mrpList) {
                        mrp.setDateOrdered(this.getToday());
                        mrp.setS_Resource_ID(this.m_product_planning.getS_Resource_ID());
                        mrp.setDatePromised(TimeUtil.addDays(DemandDateStartSchedule, this.m_product_planning.getDeliveryTime_Promised().add(transfertTime).negate().intValueExact()));
                        mrp.setDateFinishSchedule(DemandDateStartSchedule);
                        mrp.saveEx();
                    }
                    ++this.count_DO;
                }
            }
        }
    }
    
    protected void createRequisition(final int PP_MRP_ID, final int AD_Org_ID, final MProduct product, final BigDecimal QtyPlanned, final Timestamp DemandDateStartSchedule) throws AdempiereException, SQLException {
        if (this.duration == 0) {
            this.duration = MPPMRP.getDurationDays(null, QtyPlanned, (I_PP_Product_Planning)this.m_product_planning);
        }
        final Timestamp DateRequired = TimeUtil.addDays(DemandDateStartSchedule, 0 - this.duration);
        this.log.info("Create Requisition");
        final MRequisition req = new MRequisition(this.getCtx(), 0, this.get_TrxName());
        req.create(PP_MRP_ID, QtyPlanned, this.m_product_planning.getM_Product_ID(), this.m_product_planning.getC_BPartner_ID(), AD_Org_ID, this.m_product_planning.getPlanner_ID(), DateRequired, "Generate from MRP", this.m_product_planning.getM_Warehouse_ID(), this.docTypeReq_ID);
        ++this.count_MR;
    }
    
    protected void createPPOrder(final MPPMRP mrp, final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, final BigDecimal QtyPlanned, final Timestamp DemandDateStartSchedule) throws AdempiereException, SQLException {
        this.log.info("PP_Product_BOM_ID:" + this.m_product_planning.getPP_Product_BOM_ID() + ", AD_Workflow_ID:" + this.m_product_planning.getAD_Workflow_ID());
        if (this.m_product_planning.getPP_Product_BOM_ID() == 0 || this.m_product_planning.getAD_Workflow_ID() == 0) {
            throw new AdempiereException("@FillMandatory@ @PP_Product_BOM_ID@, @AD_Workflow_ID@ ( @M_Product_ID@=" + product.getValue() + ")");
        }
        final MPPOrder check = (MPPOrder)new Query(Env.getCtx(), "PP_Order", "PP_Order_ID=?", mrp.get_TrxName()).setParameters(new Object[] { mrp.getPP_Order_ID() }).first();
        MPPOrder order;
        if (check == null) {
            order = new MPPOrder(this.getCtx(), 0, this.get_TrxName());
            order.addDescription("Generated by MRP");
            order.setAD_Org_ID(AD_Org_ID);
            order.setLine(10);
            this.log.info("PP Order Created");
        }
        else {
            order = check;
        }
        if ("M".equals(this.getBOMType())) {
            this.log.info("Maintenance Order BOMTypeMaintenance");
            final MDocType dt = (MDocType)new Query(Env.getCtx(), "C_DocType", "DocBaseType=?", this.get_TrxName()).setParameters(new Object[] { "MOF" }).first();
            order.setC_DocTypeTarget_ID(this.docTypeMF_ID = dt.getC_DocType_ID());
            order.setC_DocType_ID(this.docTypeMF_ID);
        }
        else {
            this.log.info("Manufacturing Order BOMType");
            final MDocType dt = (MDocType)new Query(Env.getCtx(), "C_DocType", "DocBaseType=?", this.get_TrxName()).setParameters(new Object[] { "MOP" }).first();
            order.setC_DocTypeTarget_ID(this.docTypeMO_ID = dt.getC_DocType_ID());
            order.setC_DocType_ID(this.docTypeMO_ID);
        }
        order.setS_Resource_ID(this.m_product_planning.getS_Resource_ID());
        order.setM_Warehouse_ID(this.m_product_planning.getM_Warehouse_ID());
        order.setM_Product_ID(this.m_product_planning.getM_Product_ID());
        order.setM_AttributeSetInstance_ID(0);
        order.setPP_Product_BOM_ID(this.m_product_planning.getPP_Product_BOM_ID());
        order.setAD_Workflow_ID(this.m_product_planning.getAD_Workflow_ID());
        order.setPlanner_ID(this.m_product_planning.getPlanner_ID());
        order.setDateOrdered(this.getToday());
        order.setDatePromised(DemandDateStartSchedule);
        if (this.duration == 0) {
            this.duration = MPPMRP.getDurationDays(mrp, QtyPlanned, (I_PP_Product_Planning)this.m_product_planning);
        }
        order.setDateStartSchedule(TimeUtil.addMinutess(DemandDateStartSchedule, 0 - this.duration));
        order.setDateFinishSchedule(DemandDateStartSchedule);
        order.setQty(QtyPlanned);
        order.setC_UOM_ID(product.getC_UOM_ID());
        order.setYield(Env.ZERO);
        order.setScheduleType("D");
        order.setPriorityRule("5");
        order.setDocAction("CO");
        order.saveEx(this.get_TrxName());
        mrp.setPP_Order_ID(order.get_ID());
        mrp.setDocStatus("IP");
        mrp.saveEx(mrp.get_TrxName());
        ++this.count_MO;
    }
    
    protected MPPProductPlanning getProductPlanning(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID, final MProduct product) throws SQLException {
        final MPPProductPlanning pp = MPPProductPlanning.find(this.getCtx(), AD_Org_ID, M_Warehouse_ID, S_Resource_ID, product.getM_Product_ID(), this.get_TrxName());
        if (pp == null) {
            return null;
        }
        final MPPProductPlanning pp2 = new MPPProductPlanning(this.getCtx(), 0, this.get_TrxName());
        MPPProductPlanning.copyValues((PO)pp, (PO)pp2);
        pp2.setIsRequiredDRP(this.isRequiredDRP());
        if (pp2.getPP_Product_BOM_ID() <= 0 && product.isBOM()) {
            pp2.setPP_Product_BOM_ID(MPPProductBOM.getBOMSearchKey(product));
        }
        if (pp2.getAD_Workflow_ID() <= 0 && product.isBOM()) {
            pp2.setAD_Workflow_ID(MWorkflow.getWorkflowSearchKey(product));
        }
        if (pp2.getPlanner_ID() <= 0) {
            pp2.setPlanner_ID(this.getPlanner_ID());
        }
        if (pp2.getM_Warehouse_ID() <= 0) {
            pp2.setM_Warehouse_ID(M_Warehouse_ID);
        }
        if (pp2.getS_Resource_ID() <= 0) {
            pp2.setS_Resource_ID(S_Resource_ID);
        }
        if (pp2.getOrder_Policy() == null) {
            pp2.setOrder_Policy("LFL");
        }
        if (!this.isRequiredDRP()) {
            if (product.isPurchased()) {
                int C_BPartner_ID = 0;
                final MProductPO[] ppos = MProductPO.getOfProduct(this.getCtx(), product.getM_Product_ID(), this.get_TrxName());
                for (int i = 0; i < ppos.length; ++i) {
                    if (ppos[i].isCurrentVendor() && ppos[i].getC_BPartner_ID() != 0) {
                        C_BPartner_ID = ppos[i].getC_BPartner_ID();
                        pp2.setDeliveryTime_Promised(BigDecimal.valueOf(ppos[i].getDeliveryTime_Promised()));
                        pp2.setOrder_Min(ppos[i].getOrder_Min());
                        pp2.setOrder_Max(Env.ZERO);
                        pp2.setOrder_Pack(ppos[i].getOrder_Pack());
                        pp2.setC_BPartner_ID(C_BPartner_ID);
                        break;
                    }
                }
                if (C_BPartner_ID <= 0) {
                    this.createMRPNote("MRP-130", AD_Org_ID, 0, product, null, null, (String)null);
                    pp2.setIsCreatePlan(false);
                }
            }
            if (product.isBOM() && pp2.getAD_Workflow_ID() <= 0) {
                this.log.info("Error: Do not exist workflow (" + product.getValue() + ")");
            }
        }
        return pp2;
    }
    
    private int getDDOrder_ID(final int AD_Org_ID, final int M_Warehouse_ID, final int M_Shipper_ID, final int C_BPartner_ID, final Timestamp DatePromised) {
        final String key = String.valueOf(AD_Org_ID) + "#" + M_Warehouse_ID + "#" + M_Shipper_ID + "#" + C_BPartner_ID + "#" + DatePromised + "DR";
        Integer order_id = (Integer)CalculateMaterialPlan.dd_order_id_cache.get((Object)key.toString());
        if (order_id == null) {
            final String sql = "SELECT DD_Order_ID FROM DD_Order WHERE AD_Org_ID=? AND M_Warehouse_ID=? AND M_Shipper_ID = ? AND C_BPartner_ID=? AND DatePromised=? AND DocStatus=?";
            order_id = DB.getSQLValueEx(this.get_TrxName(), sql, new Object[] { AD_Org_ID, M_Warehouse_ID, M_Shipper_ID, C_BPartner_ID, DatePromised, "DR" });
            if (order_id > 0) {
                CalculateMaterialPlan.dd_order_id_cache.put(key, order_id);
            }
        }
        return order_id;
    }
    
    private MBPartner getBPartner(final int C_BPartner_ID) {
        MBPartner partner = (MBPartner)CalculateMaterialPlan.partner_cache.get(C_BPartner_ID);
        if (partner == null) {
            partner = MBPartner.get(this.getCtx(), C_BPartner_ID);
            CalculateMaterialPlan.partner_cache.put(C_BPartner_ID, partner);
        }
        return partner;
    }
    
    protected void createMRPNote(final String code, final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, String documentNo, BigDecimal qty, String comment) throws SQLException {
        documentNo = ((documentNo != null) ? documentNo : "");
        comment = ((comment != null) ? comment : "");
        qty = ((qty != null) ? qty : Env.ZERO);
        MMessage msg = MMessage.get(this.getCtx(), code);
        if (msg == null) {
            msg = MMessage.get(this.getCtx(), "MRP-999");
        }
        String message = Msg.getMsg(this.getCtx(), msg.getValue());
        int user_id = 0;
        if (this.m_product_planning != null) {
            user_id = this.m_product_planning.getPlanner_ID();
        }
        String reference = "";
        if (product != null) {
            reference = String.valueOf(product.getValue()) + " " + product.getName();
        }
        if (!Util.isEmpty(documentNo, true)) {
            message = String.valueOf(message) + " " + Msg.translate(this.getCtx(), "DocumentNo") + ":" + documentNo;
        }
        if (qty != null) {
            message = String.valueOf(message) + " " + Msg.translate(this.getCtx(), "QtyPlan") + ":" + qty;
        }
        if (!Util.isEmpty(comment, true)) {
            message = String.valueOf(message) + " " + comment;
        }
        final MNote note = new MNote(this.getCtx(), msg.getAD_Message_ID(), user_id, 53043, PP_MRP_ID, reference, message, this.get_TrxName());
        note.setAD_Org_ID(AD_Org_ID);
        note.saveEx(this.get_TrxName());
        this.log.info(String.valueOf(code) + ": " + note.getTextMsg());
        ++this.count_Msg;
    }
    
    private void createMRPNote(final String code, final MPPMRP mrp, final MProduct product, final String comment) throws SQLException {
        this.createMRPNote(code, mrp.getAD_Org_ID(), mrp.get_ID(), product, MPPMRP.getDocumentNo(mrp.get_ID()), mrp.getQty(), comment);
    }
    
    protected void createMRPNote(final String code, final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, final BigDecimal qty, final Timestamp DemandDateStartSchedule, final Exception e) throws SQLException {
        final String documentNo = null;
        final String comment = e.getLocalizedMessage();
        this.createMRPNote(code, AD_Org_ID, PP_MRP_ID, product, documentNo, qty, comment);
    }
    
    protected void deleteMRP(final MPPMRP mrp) throws SQLException {
        final String sql = "DELETE FROM PP_MRP WHERE OrderType = 'MOP' AND DocStatus ='CL' AND AD_Client_ID=" + mrp.getAD_Client_ID() + " AND AD_Org_ID=" + mrp.getAD_Org_ID() + " AND M_Warehouse_ID=" + mrp.getM_Warehouse_ID() + " AND S_Resource_ID=" + mrp.getS_Resource_ID();
        DB.executeUpdateEx(sql, this.get_TrxName());
    }
    
    protected BigDecimal getQtyOnHand(final MPPProductPlanning pp) {
        return MPPMRP.getQtyOnHand(this.getCtx(), pp.getM_Warehouse_ID(), pp.getM_Product_ID(), this.get_TrxName());
    }
    
    protected Timestamp getToday() {
        return this.Today;
    }
    
    public boolean isRequiredDRP() {
        return this.p_IsRequiredDRP;
    }
    
    protected int getDocType(final String docBaseType, final int AD_Org_ID) {
        final MDocType[] docs = MDocType.getOfDocBaseType(this.getCtx(), docBaseType);
        if (docs == null || docs.length == 0) {
            final String reference = Msg.getMsg(this.getCtx(), "SequenceDocNotFound");
            final String textMsg = "Not found default document type for docbasetype " + docBaseType;
            final MNote note = new MNote(this.getCtx(), MMessage.getAD_Message_ID(this.getCtx(), "SequenceDocNotFound"), this.getPlanner_ID(), 53043, 0, reference, textMsg, this.get_TrxName());
            note.saveEx();
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
    
    private String getBOMType() {
        if (this.m_product_planning == null || this.m_product_planning.getPP_Product_BOM_ID() == 0) {
            return null;
        }
        final String BOMType = DB.getSQLValueString(this.get_TrxName(), "SELECT BOMType FROM PP_Product_BOM WHERE PP_Product_BOM_ID = ?", this.m_product_planning.getPP_Product_BOM_ID());
        return BOMType;
    }
    
    public int getPlanner_ID() {
        if (this.p_Planner_ID <= 0) {
            this.p_Planner_ID = Env.getAD_User_ID(this.getCtx());
        }
        return this.p_Planner_ID;
    }
}
