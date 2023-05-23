// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.MDocType;
import java.util.Collection;
import org.compiere.model.MNote;
import org.compiere.util.Util;
import org.compiere.model.MMessage;
import org.compiere.model.POResultSet;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MRequisition;
import org.compiere.model.MLocator;
import org.libero.model.MDDNetworkDistributionLine;
import org.eevolution.model.MDDOrderLine;
import org.eevolution.model.MDDOrder;
import org.compiere.model.MOrg;
import org.compiere.model.MWarehouse;
import org.libero.model.MDDNetworkDistribution;
import org.adempiere.exceptions.AdempiereException;
import java.math.RoundingMode;
import org.compiere.model.MProductPO;
import org.compiere.wf.MWorkflow;
import org.compiere.model.PO;
import org.eevolution.model.I_PP_Product_Planning;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.util.TimeUtil;
import org.compiere.util.DB;
import java.util.Iterator;
import org.compiere.util.Msg;
import javax.swing.tree.MutableTreeNode;
import org.eevolution.model.MPPProductBOM;
import javax.swing.tree.DefaultMutableTreeNode;
import org.compiere.model.MProduct;
import java.sql.SQLException;
import org.libero.model.MPPMRP;
import org.compiere.model.Query;
import org.libero.model.MPPOrder;
import java.util.ArrayList;
import org.compiere.util.Env;
import org.libero.model.TampungDataBom;
import org.libero.model.TampungDataDeleteMRP;
import java.util.List;
import org.compiere.model.MBPartner;
import org.compiere.util.CCache;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.eevolution.model.MPPProductPlanning;
import org.libero.form.TreeBOM;
import org.compiere.process.SvrProcess;

public class GenerateBOMProduct extends SvrProcess
{
    TreeBOM treebom;
    private int p_AD_Org_ID;
    private int p_S_Resource_ID;
    private int p_M_Warehouse_ID;
    private boolean p_IsRequiredDRP;
    private int p_Planner_ID;
    private String p_Version;
    protected int p_M_Product_ID;
    private int p_C_Order_ID;
    private int p_M_Order_ID;
    private int generatebom;
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
//    private int count_MO;
//    private int count_MR;
//    private int count_DO;
//    private int count_Msg;
    public int count_MO;
    public int count_MR;
    public int count_DO;
    public int count_Msg;
    private boolean p_DeleteMRP;
    private String msg_debug;
    private static CCache<String, Integer> dd_order_id_cache;
    private static CCache<Integer, MBPartner> partner_cache;
    private List<TampungDataDeleteMRP> listtaTampungDataDeleteMRPs;
    private List<TampungDataBom> list;
    
    static {
        GenerateBOMProduct.dd_order_id_cache = (CCache<String, Integer>)new CCache("DD_Order_ID", 50);
        GenerateBOMProduct.partner_cache = (CCache<Integer, MBPartner>)new CCache("C_BPartner", 50);
    }
    
    public GenerateBOMProduct() {
        this.treebom = new TreeBOM();
        this.p_AD_Org_ID = 0;
        this.p_S_Resource_ID = 0;
        this.p_M_Warehouse_ID = 0;
        this.p_IsRequiredDRP = false;
        this.p_Planner_ID = 0;
        this.p_Version = "1";
        this.p_M_Product_ID = 0;
        this.p_C_Order_ID = 0;
        this.p_M_Order_ID = 0;
        this.generatebom = 0;
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
        this.listtaTampungDataDeleteMRPs = null;
        this.list = new ArrayList<TampungDataBom>();
    }
    
    protected void prepare() {
    }
    
    public boolean isRequiredDRP() {
        return this.p_IsRequiredDRP;
    }
    
    public int getPlanner_ID() {
        if (this.p_Planner_ID <= 0) {
            this.p_Planner_ID = Env.getAD_User_ID(this.getCtx());
        }
        return this.p_Planner_ID;
    }
    
    protected String doIt() throws Exception {
        final StringBuffer resultMsg = new StringBuffer();
        final MPPOrder mppOrder = new MPPOrder(this.getCtx(), this.getRecord_ID(), this.get_TrxName());
        final List<MPPMRP> listmrp = new Query(this.getCtx(), "PP_MRP", "pp_orderref_id = " + mppOrder.getPP_Order_ID() + " and coalesce(pp_order_id,0) != " + mppOrder.getPP_Order_ID() + " and typemrp = 'S'", this.msg_debug).list();
        this.listtaTampungDataDeleteMRPs = new ArrayList<TampungDataDeleteMRP>();
        for (final MPPMRP mppmrp : listmrp) {
            final TampungDataDeleteMRP dataDeleteMRP = new TampungDataDeleteMRP();
            dataDeleteMRP.setRequisitionid(mppmrp.getM_Requisition_ID());
            dataDeleteMRP.setOrderid(mppmrp.getPP_Order_ID());
            this.listtaTampungDataDeleteMRPs.add(dataDeleteMRP);
        }
        try {
            this.deleteMRP(mppOrder.getAD_Client_ID(), mppOrder.getAD_Org_ID(), mppOrder.getS_Resource_ID(), mppOrder.getM_Warehouse_ID(), mppOrder.getPP_Order_ID());
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        final MProduct product = MProduct.get(this.getCtx(), mppOrder.getM_Product_ID());
        final DefaultMutableTreeNode parent = new DefaultMutableTreeNode(this.treebom.productSummary(product, false));
        for (final MPPProductBOM bom : MPPProductBOM.getProductBOMs(product)) {
            parent.add(this.parent(bom, mppOrder.getM_Product_ID(), mppOrder));
        }
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "Created"));
        resultMsg.append("<br> ");
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "PP_Order_ID") + ":" + this.count_MO);
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "DD_Order_ID") + ":" + this.count_DO);
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "M_Requisition_ID") + ":" + this.count_MR);
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "AD_Note_ID") + ":" + this.count_Msg);
        return String.valueOf(this.msg_debug) + "|" + resultMsg.toString();
    }
    
    protected String doIt1(int Record_ID) throws Exception {
        final StringBuffer resultMsg = new StringBuffer();
        final MPPOrder mppOrder = new MPPOrder(this.getCtx(), Record_ID, this.get_TrxName());
        final List<MPPMRP> listmrp = new Query(this.getCtx(), "PP_MRP", "pp_orderref_id = " + mppOrder.getPP_Order_ID() + " and coalesce(pp_order_id,0) != " + mppOrder.getPP_Order_ID() + " and typemrp = 'S'", this.msg_debug).list();
        this.listtaTampungDataDeleteMRPs = new ArrayList<TampungDataDeleteMRP>();
        for (final MPPMRP mppmrp : listmrp) {
            final TampungDataDeleteMRP dataDeleteMRP = new TampungDataDeleteMRP();
            dataDeleteMRP.setRequisitionid(mppmrp.getM_Requisition_ID());
            dataDeleteMRP.setOrderid(mppmrp.getPP_Order_ID());
            this.listtaTampungDataDeleteMRPs.add(dataDeleteMRP);
        }
        try {
            this.deleteMRP(mppOrder.getAD_Client_ID(), mppOrder.getAD_Org_ID(), mppOrder.getS_Resource_ID(), mppOrder.getM_Warehouse_ID(), mppOrder.getPP_Order_ID());
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        final MProduct product = MProduct.get(this.getCtx(), mppOrder.getM_Product_ID());
        final DefaultMutableTreeNode parent = new DefaultMutableTreeNode(this.treebom.productSummary(product, false));
        for (final MPPProductBOM bom : MPPProductBOM.getProductBOMs(product)) {
            parent.add(this.parent(bom, mppOrder.getM_Product_ID(), mppOrder));
        }
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "Created"));
        resultMsg.append("<br> ");
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "PP_Order_ID") + ":" + this.count_MO);
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "DD_Order_ID") + ":" + this.count_DO);
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "M_Requisition_ID") + ":" + this.count_MR);
        resultMsg.append("<br> " + Msg.translate(this.getCtx(), "AD_Note_ID") + ":" + this.count_Msg);
        return String.valueOf(this.msg_debug) + "|" + resultMsg.toString();
    }
    
    protected String runMRP(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID, final int C_Order_ID, final int PP_Order_ID, final int productid, final int ProductMPO, final BigDecimal qtybom, final int productmasterbom) throws SQLException {
        MProduct product = null;
        int BeforePP_MRP_ID = 0;
        Timestamp BeforeDateStartSchedule = null;
        Timestamp POQDateStartSchedule = null;
        BigDecimal Qty = BigDecimal.ZERO;
        this.docTypeReq_ID = this.getDocType("POR", AD_Org_ID);
        this.docTypeMO_ID = this.getDocType("MOP", AD_Org_ID);
        this.docTypeMF_ID = this.getDocType("MOF", AD_Org_ID);
        this.docTypeDO_ID = this.getDocType("DOO", AD_Org_ID);
        if (this.generatebom == 0) {
            final TampungDataBom tampungDataBom = new TampungDataBom();
            tampungDataBom.setProductparent(DB.getSQLValue(this.get_TrxName(), "select m_product_id from pp_order where pp_order_id = " + PP_Order_ID));
            tampungDataBom.setQtyrequired(DB.getSQLValueBD(this.get_TrxName(), "select qtyentered from pp_order where pp_order_id = " + PP_Order_ID, new Object[0]));
            this.list.add(tampungDataBom);
            this.generatebom = 1;
        }
        final int PP_MRP_ID = DB.getSQLValue((String)null, "select pp_mrp_id from pp_mrp where PP_Order_ID =  " + PP_Order_ID);
        final Timestamp DatePromised = DB.getSQLValueTS((String)null, "select datepromised from PP_Order where PP_Order_ID =  " + PP_Order_ID, new Object[0]);
        for (int i = 0; i < this.list.size(); ++i) {
            if (productmasterbom == this.list.get(i).getProductparent()) {
                Qty = this.list.get(i).getQtyrequired().multiply(qtybom);
            }
        }
        final int M_Product_ID = productid;
        product = MProduct.get(this.getCtx(), M_Product_ID);
        this.log.info("Calculte Plan to this Product:" + product);
        this.setProduct(AD_Client_ID, AD_Org_ID, S_Resource_ID, M_Warehouse_ID, product);
        if ("POQ".equals(this.m_product_planning.getOrder_Policy())) {
            POQDateStartSchedule = null;
        }
        final int daysPOQ = this.m_product_planning.getOrder_Period().intValueExact() - 1;
        if ("POQ".equals(this.m_product_planning.getOrder_Policy()) && this.DatePromisedTo != null && DatePromised.compareTo(this.DatePromisedTo) > 0) {
            this.calculatePlan(AD_Client_ID, AD_Org_ID, M_Warehouse_ID, PP_MRP_ID, product, this.DatePromisedFrom, C_Order_ID, ProductMPO, productmasterbom);
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
            if (this.DatePromisedTo != null && DatePromised.compareTo(this.DatePromisedTo) <= 0) {
                this.QtyGrossReqs = this.QtyGrossReqs.add(Qty);
                this.log.info("Accumulation   QtyGrossReqs:" + this.QtyGrossReqs);
                this.log.info("DatePromised:" + DatePromised);
                this.log.info("DatePromisedTo:" + this.DatePromisedTo);
            }
        }
        else if ("LFL".equals(this.m_product_planning.getOrder_Policy())) {
            this.QtyGrossReqs = this.QtyGrossReqs.add(Qty);
            BeforeDateStartSchedule = DatePromised;
            this.calculatePlan(AD_Client_ID, AD_Org_ID, M_Warehouse_ID, PP_MRP_ID, product, BeforeDateStartSchedule, C_Order_ID, ProductMPO, productmasterbom);
        }
        if (this.QtyGrossReqs.signum() != 0 && product != null) {
            if ("POQ".equals(this.m_product_planning.getOrder_Policy()) && POQDateStartSchedule.compareTo(this.Planning_Horizon) < 0) {
                BeforeDateStartSchedule = POQDateStartSchedule;
                this.calculatePlan(AD_Client_ID, AD_Org_ID, M_Warehouse_ID, BeforePP_MRP_ID, product, BeforeDateStartSchedule, C_Order_ID, ProductMPO, productmasterbom);
            }
            else if ("LFL".equals(this.m_product_planning.getOrder_Policy()) && BeforeDateStartSchedule.compareTo(this.Planning_Horizon) <= 0) {
                this.calculatePlan(AD_Client_ID, AD_Org_ID, M_Warehouse_ID, BeforePP_MRP_ID, product, BeforeDateStartSchedule, C_Order_ID, ProductMPO, productmasterbom);
            }
        }
        else if (product != null) {
            this.getNetRequirements(AD_Client_ID, AD_Org_ID, M_Warehouse_ID, product, null);
        }
        return "ok";
    }
    
    protected void deleteMRP(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID, final int pporderid) throws SQLException {
        String sql = "DELETE FROM PP_MRP WHERE OrderType = 'POR' AND DocStatus NOT IN ('CL', 'CO') AND AD_Client_ID = " + AD_Client_ID + " AND AD_Org_ID=" + AD_Org_ID + " AND M_Warehouse_ID=" + M_Warehouse_ID + " AND pp_orderref_id = " + pporderid;
        DB.executeUpdateEx(sql, this.get_TrxName());
        this.commitEx();
        String whereClause = "DocStatus IN ('DR') AND AD_Client_ID=? AND AD_Org_ID=? AND M_Warehouse_ID=?";
        sql = "DELETE FROM AD_Note WHERE AD_Table_ID=? AND AD_Client_ID=? AND AD_Org_ID=?";
        DB.executeUpdateEx(sql, new Object[] { 53043, AD_Client_ID, AD_Org_ID }, this.get_TrxName());
        this.commitEx();
        if (this.isRequiredDRP()) {
            whereClause = "DocStatus='DR' AND AD_Client_ID=? AND AD_Org_ID=? AND EXISTS (SELECT 1 FROM PP_MRP mrp WHERE  mrp.DD_Order_ID=DD_Order.DD_Order_ID AND mrp.S_Resource_ID=? ) AND EXISTS (SELECT 1 FROM DD_OrderLine ol INNER JOIN  M_Locator l ON (l.M_Locator_ID=ol.M_LocatorTo_ID)  WHERE ol.DD_Order_ID=DD_Order.DD_Order_ID AND l.M_Warehouse_ID=?)";
            this.deletePO("DD_Order", whereClause, new Object[] { AD_Client_ID, AD_Org_ID, S_Resource_ID, M_Warehouse_ID });
        }
        sql = "DELETE FROM PP_MRP WHERE OrderType = 'MOP' AND DocStatus NOT IN ('CL', 'CO')AND AD_Client_ID=" + AD_Client_ID + " AND AD_Org_ID=" + AD_Org_ID + " AND M_Warehouse_ID=" + M_Warehouse_ID + " AND S_Resource_ID=" + S_Resource_ID + " AND PP_Order_ID != " + pporderid;
        DB.executeUpdateEx(sql, this.get_TrxName());
        this.commitEx();
        whereClause = "DocStatus='DR' AND AD_Client_ID=? AND AD_Org_ID=? AND M_Warehouse_ID=? AND S_Resource_ID=? AND PP_Order_ID!=?";
        for (int i = 0; i < this.listtaTampungDataDeleteMRPs.size(); ++i) {
            if (this.listtaTampungDataDeleteMRPs.get(i).getRequisitionid() > 0) {
                this.deletePO("M_Requisition", "M_Requisition_ID = ?", new Object[] { this.listtaTampungDataDeleteMRPs.get(i).getRequisitionid() });
            }
            else if (this.listtaTampungDataDeleteMRPs.get(i).getOrderid() > 0) {
                this.deletePO("PP_Order", "PP_Order_ID = ?", new Object[] { this.listtaTampungDataDeleteMRPs.get(i).getOrderid() });
            }
        }
        DB.executeUpdateEx("UPDATE PP_MRP SET IsAvailable ='Y' WHERE TypeMRP = 'S' AND AD_Client_ID = ? AND AD_Org_ID=? AND M_Warehouse_ID=?", new Object[] { AD_Client_ID, AD_Org_ID, M_Warehouse_ID }, this.get_TrxName());
        this.commitEx();
    }
    
    public DefaultMutableTreeNode parent(final MPPProductBOM bom, final int product, final MPPOrder mppOrder) {
        final DefaultMutableTreeNode parent = new DefaultMutableTreeNode(this.treebom.productSummary(bom));
        MPPProductBOMLine[] amppproductbomline;
        for (int j = (amppproductbomline = bom.getLines()).length, i = 0; i < j; ++i) {
            final MPPProductBOMLine bomline = amppproductbomline[i];
            final MProduct component = MProduct.get(this.getCtx(), bomline.getM_Product_ID());
            try {
                this.runMRP(mppOrder.getAD_Client_ID(), mppOrder.getAD_Org_ID(), mppOrder.getS_Resource_ID(), mppOrder.getM_Warehouse_ID(), 0, mppOrder.getPP_Order_ID(), bomline.getM_Product_ID(), mppOrder.getPP_Order_ID(), bomline.getQtyBOM(), bomline.getPP_Product_BOM().getM_Product_ID());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            DB.getSQLValue(this.get_TrxName(), "select count(*) from PP_Product_BOM where m_product_id = " + bomline.getM_Product().getM_Product_ID());
            parent.add(this.component(component, product, mppOrder));
        }
        return parent;
    }
    
    public DefaultMutableTreeNode component(final MProduct product, final int productfp, final MPPOrder mppOrder) {
        final Iterator<MPPProductBOM> iterator = MPPProductBOM.getProductBOMs(product).iterator();
        if (iterator.hasNext()) {
            final MPPProductBOM bom = iterator.next();
            return this.parent(bom, productfp, mppOrder);
        }
        return new DefaultMutableTreeNode(this.treebom.productSummary(product, true));
    }
    
    private void setProduct(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID, final MProduct product) throws SQLException {
        this.DatePromisedTo = null;
        this.DatePromisedFrom = null;
        this.m_product_planning = this.getProductPlanning(AD_Client_ID, AD_Org_ID, S_Resource_ID, M_Warehouse_ID, product);
        this.log.info("PP:" + AD_Client_ID + "|" + AD_Org_ID + "|" + S_Resource_ID + "|" + M_Warehouse_ID + "|" + product);
        if (this.m_product_planning == null) {
            this.createMRPNote("MRP-120", AD_Org_ID, 0, product, null, null, (String)null);
            return;
        }
        if (this.m_product_planning.getTimeFence().signum() > 0) {
            this.TimeFence = TimeUtil.addDays(this.getToday(), this.m_product_planning.getTimeFence().intValueExact());
        }
        this.QtyProjectOnHand = this.getQtyOnHand((I_PP_Product_Planning)this.m_product_planning);
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
    
    protected MPPProductPlanning getProductPlanning(final int AD_Client_ID, final int AD_Org_ID, final int S_Resource_ID, final int M_Warehouse_ID, final MProduct product) throws SQLException {
        final MPPProductPlanning pp = MPPProductPlanning.find(this.getCtx(), AD_Org_ID, M_Warehouse_ID, S_Resource_ID, product.getM_Product_ID(), this.get_TrxName());
        if (pp == null) {
            return null;
        }
        final MPPProductPlanning pp2 = new MPPProductPlanning(this.getCtx(), 0, (String)null);
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
    
    protected BigDecimal getQtyOnHand(final I_PP_Product_Planning pp) {
        return MPPMRP.getQtyOnHand(this.getCtx(), pp.getM_Warehouse_ID(), pp.getM_Product_ID(), this.get_TrxName());
    }
    
    protected Timestamp getToday() {
        return this.Today;
    }
    
    private void calculatePlan(final int AD_Client_ID, final int AD_Org_ID, final int M_Warehouse_ID, final int PP_MRP_ID, final MProduct product, final Timestamp DemandDateStartSchedule, final int order_id, final int ProductMO, final int productmasterbom) throws SQLException {
        this.log.info("Create Plan ...");
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
                    this.createSupply(AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule, order_id, ProductMO, productmasterbom);
                }
                catch (Exception e) {
                    this.createMRPNote("MRP-160", AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule, e);
                }
            }
        }
        else {
            this.log.info("No Create Plan");
        }
    }
    
    protected void createSupply(final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, final BigDecimal QtyPlanned, final Timestamp DemandDateStartSchedule, final int orderid, final int productMO, final int productmasterbom) throws AdempiereException, SQLException {
        if (this.isRequiredDRP() && this.m_product_planning.getDD_NetworkDistribution_ID() > 0) {
            this.createDDOrder(AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule);
        }
        else if (product.isPurchased() && !product.isBOM()) {
            this.createRequisition(AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule, orderid, productMO, productmasterbom);
        }
        else {
            if (!product.isBOM()) {
                throw new IllegalStateException("MRP Internal Error: Don't know what document to create for " + product + "(" + this.m_product_planning + ")");
            }
            this.createPPOrder(AD_Org_ID, PP_MRP_ID, product, QtyPlanned, DemandDateStartSchedule, productMO);
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
                            GenerateBOMProduct.dd_order_id_cache.put(key, DD_Order_ID);
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
        this.commitEx();
    }
    
    protected void createRequisition(final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, final BigDecimal QtyPlanned, final Timestamp DemandDateStartSchedule, final int order_id, final int ProductMO, final int productmasterbom) throws AdempiereException, SQLException {
        this.log.info("Create Requisition");
        final int duration = MPPMRP.getDurationDays(null, QtyPlanned, (I_PP_Product_Planning)this.m_product_planning);
        int M_PriceList_ID = -1;
        if (this.m_product_planning.getC_BPartner_ID() > 0) {
            M_PriceList_ID = DB.getSQLValueEx(this.get_TrxName(), "SELECT COALESCE(bp.PO_PriceList_ID,bpg.PO_PriceList_ID) FROM C_BPartner bp INNER JOIN C_BP_Group bpg ON (bpg.C_BP_Group_ID=bp.C_BP_Group_ID) WHERE bp.C_BPartner_ID=?", new Object[] { this.m_product_planning.getC_BPartner_ID() });
        }
        final MRequisition req = new MRequisition(this.getCtx(), 0, this.get_TrxName());
        req.setAD_Org_ID(AD_Org_ID);
        req.setAD_User_ID(this.m_product_planning.getPlanner_ID());
        req.setDateRequired(TimeUtil.addDays(DemandDateStartSchedule, 0 - duration));
        req.setDescription("Requisition generated from MRP");
        req.setM_Warehouse_ID(this.m_product_planning.getM_Warehouse_ID());
        req.setC_DocType_ID(this.docTypeReq_ID);
        if (M_PriceList_ID > 0) {
            req.setM_PriceList_ID(M_PriceList_ID);
        }
        req.saveEx();
        final MRequisitionLine reqline = new MRequisitionLine(req);
        reqline.setLine(10);
        reqline.setAD_Org_ID(AD_Org_ID);
        reqline.setC_BPartner_ID(this.m_product_planning.getC_BPartner_ID());
        reqline.setM_Product_ID(this.m_product_planning.getM_Product_ID());
        reqline.setPrice();
        reqline.setPriceActual(Env.ZERO);
        reqline.setQty(QtyPlanned);
        reqline.saveEx();
        final List<MPPMRP> mrpList = new Query(this.getCtx(), "PP_MRP", "M_Requisition_ID=?", this.get_TrxName()).setParameters(new Object[] { req.getM_Requisition_ID() }).list();
        for (final MPPMRP mrp : mrpList) {
            mrp.setDateOrdered(this.getToday());
            mrp.setS_Resource_ID(this.m_product_planning.getS_Resource_ID());
            mrp.setDatePromised(req.getDateRequired());
            mrp.setDateStartSchedule(req.getDateRequired());
            mrp.setDateFinishSchedule(DemandDateStartSchedule);
            mrp.set_CustomColumn("PP_OrderRef_ID", (Object)ProductMO);
            if (order_id > 0) {
                mrp.setC_Order_ID(order_id);
            }
            mrp.saveEx();
        }
        this.commitEx();
        ++this.count_MR;
    }
    
    protected void createPPOrder(final int AD_Org_ID, final int PP_MRP_ID, final MProduct product, final BigDecimal QtyPlanned, final Timestamp DemandDateStartSchedule, final int ProductMO) throws AdempiereException, SQLException {
        this.log.info("PP_Product_BOM_ID:" + this.m_product_planning.getPP_Product_BOM_ID() + ", AD_Workflow_ID:" + this.m_product_planning.getAD_Workflow_ID() + ", product_planning:" + this.m_product_planning);
        if (this.m_product_planning.getPP_Product_BOM_ID() == 0 || this.m_product_planning.getAD_Workflow_ID() == 0) {
            throw new AdempiereException("@FillMandatory@ @PP_Product_BOM_ID@, @AD_Workflow_ID@ ( @M_Product_ID@=" + product.getValue() + ")");
        }
        final MPPOrder order = new MPPOrder(this.getCtx(), 0, this.get_TrxName());
        order.addDescription("MO generated from MRP");
        order.setAD_Org_ID(AD_Org_ID);
        order.setLine(10);
        if ("M".equals(this.getBOMType())) {
            this.log.info("Maintenance Order Created");
            order.setC_DocTypeTarget_ID(this.docTypeMF_ID);
            order.setC_DocType_ID(this.docTypeMF_ID);
        }
        else {
            this.log.info("Manufacturing Order Created");
            order.setC_DocTypeTarget_ID(this.docTypeMO_ID);
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
        final int duration = 0;
        order.setDateStartSchedule(TimeUtil.addDays(DemandDateStartSchedule, 0 - duration));
        order.setDateFinishSchedule(DemandDateStartSchedule);
        order.setQty(QtyPlanned);
        order.setC_UOM_ID(product.getC_UOM_ID());
        order.setYield(Env.ZERO);
        order.setScheduleType("D");
        order.setPriorityRule("5");
        order.setDocAction("CO");
        order.saveEx();
        final List<MPPMRP> mrpList = new Query(this.getCtx(), "PP_MRP", "PP_Order_ID=?", this.get_TrxName()).setParameters(new Object[] { order.getPP_Order_ID() }).list();
        for (final MPPMRP mrp : mrpList) {
            mrp.set_CustomColumn("PP_OrderRef_ID", (Object)ProductMO);
            mrp.saveEx();
        }
        this.commitEx();
        if (this.generatebom > 0) {
            final TampungDataBom tampungDataBom = new TampungDataBom();
            tampungDataBom.setProductparent(this.m_product_planning.getM_Product_ID());
            tampungDataBom.setQtyrequired(QtyPlanned);
            this.list.add(tampungDataBom);
            this.generatebom = 1;
        }
        ++this.count_MO;
    }
    
    private void deletePO(final String tableName, final String whereClause, final Object[] params) throws SQLException {
        final POResultSet<PO> rs = (POResultSet<PO>)new Query(this.getCtx(), tableName, whereClause, this.get_TrxName()).setParameters(params).scroll();
        try {
            while (rs.hasNext()) {
                rs.next().deleteEx(true);
            }
        }
        finally {
            rs.close();
        }
        rs.close();
        this.commitEx();
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
        note.saveEx();
        this.commitEx();
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
    
    private int getDDOrder_ID(final int AD_Org_ID, final int M_Warehouse_ID, final int M_Shipper_ID, final int C_BPartner_ID, final Timestamp DatePromised) {
        final String key = String.valueOf(AD_Org_ID) + "#" + M_Warehouse_ID + "#" + M_Shipper_ID + "#" + C_BPartner_ID + "#" + DatePromised + "DR";
        Integer order_id = (Integer)GenerateBOMProduct.dd_order_id_cache.get((Object)key.toString());
        if (order_id == null) {
            final String sql = "SELECT DD_Order_ID FROM DD_Order WHERE AD_Org_ID=? AND M_Warehouse_ID=? AND M_Shipper_ID = ? AND C_BPartner_ID=? AND DatePromised=? AND DocStatus=?";
            order_id = DB.getSQLValueEx(this.get_TrxName(), sql, new Object[] { AD_Org_ID, M_Warehouse_ID, M_Shipper_ID, C_BPartner_ID, DatePromised, "DR" });
            if (order_id > 0) {
                GenerateBOMProduct.dd_order_id_cache.put(key, order_id);
            }
        }
        return order_id;
    }
    
    private MBPartner getBPartner(final int C_BPartner_ID) {
        MBPartner partner = (MBPartner)GenerateBOMProduct.partner_cache.get((Object)C_BPartner_ID);
        if (partner == null) {
            partner = MBPartner.get(this.getCtx(), C_BPartner_ID);
            GenerateBOMProduct.partner_cache.put(C_BPartner_ID, partner);
        }
        return partner;
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
}
