// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.model.I_M_Product;
import org.eevolution.model.I_PP_Order;
import org.libero.tables.I_PP_Order_BOMLine;
import org.libero.tables.I_PP_Order_Node;
import org.compiere.model.MUOM;
import org.compiere.model.I_C_UOM;
import org.compiere.model.MBPartner;
import org.compiere.util.TimeUtil;
import org.adempiere.exceptions.NoVendorForProductException;
import org.compiere.model.MProductPO;
import org.compiere.model.MOrder;
import java.util.HashMap;
import org.compiere.model.MLocator;
import org.adempiere.exceptions.FillMandatoryException;
import org.compiere.model.MWarehouse;
import org.compiere.print.ReportEngine;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Collection;
import org.compiere.util.Env;
import org.adempiere.model.engines.CostEngineFactory;
import org.compiere.util.Msg;
import org.compiere.model.MOrderLine;
import org.compiere.model.Query;
import org.adempiere.model.engines.StorageEngine;
import org.compiere.model.MProduct;
import org.libero.exceptions.ActivityProcessedException;
import org.compiere.model.MPeriod;
import org.compiere.model.PO;
import org.compiere.model.ModelValidationEngine;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.adempiere.exceptions.DocTypeNotFoundException;
import org.compiere.model.MDocType;
import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.I_PP_Cost_Collector;
import org.adempiere.exceptions.AdempiereException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.adempiere.model.engines.IDocumentLine;
import org.compiere.process.DocAction;
import org.libero.tables.X_PP_Cost_Collector;

public class MPPCostCollector extends X_PP_Cost_Collector implements DocAction, IDocumentLine
{
    private static final long serialVersionUID = 1L;
    private static BigDecimal costva;
    private static BigDecimal jmltenagakerja;
    private static BigDecimal qtyreserved;
    private String m_processMsg;
    private boolean m_justPrepared;
    private MPPOrder m_order;
    private MPPOrderNode m_orderNode;
    private MPPOrderBOMLine m_bomLine;
    private int attribute;
    
    static {
        MPPCostCollector.costva = BigDecimal.ZERO;
        MPPCostCollector.jmltenagakerja = BigDecimal.ZERO;
        MPPCostCollector.qtyreserved = BigDecimal.ZERO;
    }
    
    public static MPPCostCollector createCollector(final MPPOrder order, final int M_Product_ID, final int M_Locator_ID, final int M_AttributeSetInstance_ID, final int S_Resource_ID, final int PP_Order_BOMLine_ID, final int PP_Order_Node_ID, final int C_DocType_ID, final String CostCollectorType, final Timestamp movementdate, final BigDecimal qty, final BigDecimal scrap, final BigDecimal reject, final int durationSetup, final BigDecimal duration, final BigDecimal costvarian, final BigDecimal qtyres, final BigDecimal qtyjmltenagakerja) {
        MPPCostCollector.qtyreserved = qtyres;
        MPPCostCollector.jmltenagakerja = qtyjmltenagakerja;
        MPPCostCollector.costva = costvarian;
        final MPPCostCollector cc = new MPPCostCollector(order);
        cc.setPP_Order_BOMLine_ID(PP_Order_BOMLine_ID);
        cc.setPP_Order_Node_ID(PP_Order_Node_ID);
        cc.setC_DocType_ID(C_DocType_ID);
        cc.setC_DocTypeTarget_ID(C_DocType_ID);
        cc.setCostCollectorType(CostCollectorType);
        cc.setDocAction("CO");
        cc.setDocStatus("DR");
        cc.setIsActive(true);
        cc.setM_Locator_ID(M_Locator_ID);
        cc.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
        cc.setS_Resource_ID(S_Resource_ID);
        cc.setMovementDate(movementdate);
        cc.setDateAcct(movementdate);
        cc.setMovementQty(qty);
        cc.setScrappedQty(scrap);
        cc.setQtyReject(reject);
        cc.setSetupTimeReal(new BigDecimal(durationSetup));
        cc.setDurationReal(duration);
        cc.setPosted(false);
        cc.setProcessed(false);
        cc.setProcessing(false);
        cc.setUser1_ID(order.getUser1_ID());
        cc.setUser2_ID(order.getUser2_ID());
        cc.setM_Product_ID(M_Product_ID);
        if (PP_Order_Node_ID > 0) {
            cc.setIsSubcontracting(PP_Order_Node_ID);
        }
        if (PP_Order_BOMLine_ID > 0) {
            cc.setC_UOM_ID(0);
        }
        cc.saveEx(order.get_TrxName());
        if (!cc.processIt("CO")) {
            throw new AdempiereException(cc.getProcessMsg());
        }
        cc.saveEx(order.get_TrxName());
        return cc;
    }
    
    public static void setPP_Order(final I_PP_Cost_Collector cc, final MPPOrder order) {
        cc.setPP_Order_ID(order.getPP_Order_ID());
        cc.setPP_Order_Workflow_ID(order.getMPPOrderWorkflow().get_ID());
        cc.setAD_Org_ID(order.getAD_Org_ID());
        cc.setM_Warehouse_ID(order.getM_Warehouse_ID());
        cc.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
        cc.setC_Activity_ID(order.getC_Activity_ID());
        cc.setC_Campaign_ID(order.getC_Campaign_ID());
        cc.setC_Project_ID(order.getC_Project_ID());
        cc.setDescription(order.getDescription());
        cc.setS_Resource_ID(order.getS_Resource_ID());
        cc.setM_Product_ID(order.getM_Product_ID());
        cc.setC_UOM_ID(order.getC_UOM_ID());
        cc.setM_AttributeSetInstance_ID(order.getM_AttributeSetInstance_ID());
        cc.setMovementQty(order.getQtyOrdered());
    }
    
    public MPPCostCollector(final Properties ctx, final int PP_Cost_Collector_ID, final String trxName) {
        super(ctx, PP_Cost_Collector_ID, trxName);
        this.m_processMsg = null;
        this.m_justPrepared = false;
        this.m_order = null;
        this.m_orderNode = null;
        this.m_bomLine = null;
        this.attribute = 0;
        if (PP_Cost_Collector_ID == 0) {
            this.setDocStatus("DR");
            this.setDocAction("CO");
            this.setMovementDate(new Timestamp(System.currentTimeMillis()));
            this.setIsActive(true);
            this.setPosted(false);
            this.setProcessing(false);
            this.setProcessed(false);
        }
    }
    
    public MPPCostCollector(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
        this.m_processMsg = null;
        this.m_justPrepared = false;
        this.m_order = null;
        this.m_orderNode = null;
        this.m_bomLine = null;
        this.attribute = 0;
    }
    
    public MPPCostCollector(final MPPOrder order) {
        this(order.getCtx(), 0, order.get_TrxName());
        setPP_Order(this, order);
        this.m_order = order;
    }
    
    public void addDescription(final String description) {
        final String desc = this.getDescription();
        if (desc == null) {
            this.setDescription(description);
        }
        else {
            this.setDescription(String.valueOf(desc) + " | " + description);
        }
    }
    
    public void setC_DocTypeTarget_ID(final String docBaseType) {
        final MDocType[] doc = MDocType.getOfDocBaseType(this.getCtx(), docBaseType);
        if (doc == null) {
            throw new DocTypeNotFoundException(docBaseType, "");
        }
        this.setC_DocTypeTarget_ID(doc[0].get_ID());
    }
    
    @Override
    public void setProcessed(final boolean processed) {
        super.setProcessed(processed);
        if (this.get_ID() == 0) {
            return;
        }
        final int noLine = DB.executeUpdateEx("UPDATE PP_Cost_Collector SET Processed=? WHERE PP_Cost_Collector_ID=?", new Object[] { processed, this.get_ID() }, this.get_TrxName());
        this.log.fine("setProcessed - " + processed + " - Lines=" + noLine);
    }
    
    public boolean processIt(final String processAction) {
        this.m_processMsg = null;
        final DocumentEngine engine = new DocumentEngine((DocAction)this, this.getDocStatus());
        return engine.processIt(processAction, this.getDocAction());
    }
    
    public boolean unlockIt() {
        this.log.info("unlockIt - " + this.toString());
        this.setProcessing(false);
        return true;
    }
    
    public boolean invalidateIt() {
        this.log.info("invalidateIt - " + this.toString());
        this.setDocAction("PR");
        return true;
    }
    
    public String prepareIt() {
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 1);
        if (this.m_processMsg != null) {
            return "IN";
        }
        MPeriod.testPeriodOpen(this.getCtx(), this.getDateAcct(), this.getC_DocTypeTarget_ID(), this.getAD_Org_ID());
        this.setC_DocType_ID(this.getC_DocTypeTarget_ID());
        if (this.isActivityControl()) {
            final MPPOrderNode activity = this.getPP_Order_Node();
            if ("CO".equals(activity.getDocStatus())) {
                throw new ActivityProcessedException(activity);
            }
            if (activity.isSubcontracting()) {
                if ("IP".equals(activity.getDocStatus()) && "IP".equals(this.getDocStatus())) {
                    return "IP";
                }
                if ("IP".equals(activity.getDocStatus()) && "DR".equals(this.getDocStatus())) {
                    throw new ActivityProcessedException(activity);
                }
                this.m_processMsg = this.createPO(activity);
                this.m_justPrepared = false;
                activity.setInProgress(this);
                activity.saveEx(this.get_TrxName());
                return "IP";
            }
            else {
                activity.set_CustomColumn("qtyreserved", (Object)MPPCostCollector.qtyreserved);
                activity.setInProgress(this);
                activity.setQtyDelivered(activity.getQtyDelivered().add(this.getMovementQty()));
                activity.setQtyScrap(activity.getQtyScrap().add(this.getScrappedQty()));
                activity.setQtyReject(activity.getQtyReject().add(this.getQtyReject()));
                activity.setDurationReal(activity.getDurationReal() + this.getDurationReal().intValueExact());
                activity.setSetupTimeReal(activity.getSetupTimeReal() + this.getSetupTimeReal().intValueExact());
                activity.saveEx(this.get_TrxName());
                if (activity.isMilestone()) {
                    final MPPOrderWorkflow order_workflow = activity.getMPPOrderWorkflow();
                    order_workflow.closeActivities(activity, this.getMovementDate(), true);
                }
            }
        }
        else if (this.isIssue()) {
            final MProduct product = this.getM_Product();
            if (this.getM_AttributeSetInstance_ID() == 0 && product.isASIMandatory(false)) {
                throw new AdempiereException("@M_AttributeSet_ID@ @IsMandatory@ @M_Product_ID@=" + product.getValue());
            }
        }
        else if (this.isReceipt()) {
            final MProduct product = this.getM_Product();
            if (this.getM_AttributeSetInstance_ID() == 0 && product.isASIMandatory(true)) {
                throw new AdempiereException("@M_AttributeSet_ID@ @IsMandatory@ @M_Product_ID@=" + product.getValue());
            }
        }
        this.m_justPrepared = true;
        this.setDocAction("CO");
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 8);
        if (this.m_processMsg != null) {
            return "IN";
        }
        return "IP";
    }
    
    public boolean approveIt() {
        this.log.info("approveIt - " + this.toString());
        return true;
    }
    
    public boolean rejectIt() {
        this.log.info("rejectIt - " + this.toString());
        return true;
    }
    
    public String completeIt() {
        if (!this.m_justPrepared) {
            final String status = this.prepareIt();
            if (!"IP".equals(status)) {
                return status;
            }
        }
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 7);
        if (this.m_processMsg != null) {
            return "IN";
        }
        if (this.isIssue() || this.isReceipt()) {
            final MProduct product = this.getM_Product();
            final String docBaseType = MDocType.get(this.getCtx(), this.getPP_Order().getC_DocType_ID()).getDocBaseType();
            if (docBaseType.equals("MOF")) {
                if (product != null && product.isStocked()) {
                    StorageEngine.createTransaction(this, this.getMovementType(), this.getMovementDate(), this.getMovementQty(), false, this.getM_Warehouse_ID(), this.getPP_Order().getM_AttributeSetInstance_ID(), this.getPP_Order().getM_Warehouse_ID(), false);
                }
            }
            else if (product != null && product.isStocked() && !this.isVariance()) {
                StorageEngine.createTransaction(this, this.getMovementType(), this.getMovementDate(), this.getMovementQty(), false, this.getM_Warehouse_ID(), this.getPP_Order().getM_AttributeSetInstance_ID(), this.getPP_Order().getM_Warehouse_ID(), false);
            }
            if (this.isIssue()) {
                final MPPOrderBOMLine obomline = this.getPP_Order_BOMLine();
                obomline.setQtyDelivered(obomline.getQtyDelivered().add(this.getMovementQty()));
                obomline.setQtyScrap(obomline.getQtyScrap().add(this.getScrappedQty()));
                obomline.setQtyReject(obomline.getQtyReject().add(this.getQtyReject()));
                obomline.setDateDelivered(this.getMovementDate());
                this.log.fine("OrderLine - Reserved=" + obomline.getQtyReserved() + ", Delivered=" + obomline.getQtyDelivered());
                obomline.saveEx(this.get_TrxName());
                this.log.fine("OrderLine -> Reserved=" + obomline.getQtyReserved() + ", Delivered=" + obomline.getQtyDelivered());
            }
            if (this.isReceipt()) {
                final MPPOrder order = this.getPP_Order();
                order.setQtyDelivered(order.getQtyDelivered().add(this.getMovementQty()));
                order.setQtyScrap(order.getQtyScrap().add(this.getScrappedQty()));
                order.setQtyReject(order.getQtyReject().add(this.getQtyReject()));
                order.setDateDelivered(this.getMovementDate());
                if (order.getDateStart() == null) {
                    order.setDateStart(this.getDateStart());
                }
                if (order.getQtyOpen().signum() <= 0) {
                    order.setDateFinish(this.getDateFinish());
                }
                order.saveEx(this.get_TrxName());
            }
        }
        else if (this.isActivityControl()) {
            final MPPOrderNode activity = this.getPP_Order_Node();
            if (activity.isProcessed()) {
                throw new ActivityProcessedException(activity);
            }
            if (this.isSubcontracting()) {
                final String whereClause = "PP_Cost_Collector_ID=?";
                final Collection<MOrderLine> olines = new Query(this.getCtx(), "C_OrderLine", whereClause, this.get_TrxName()).setParameters(new Object[] { this.get_ID() }).list();
                String DocStatus = "CO";
                final StringBuffer msg = new StringBuffer("The quantity do not is complete for next Purchase Order : ");
                for (final MOrderLine oline : olines) {
                    if (oline.getQtyDelivered().compareTo(oline.getQtyOrdered()) < 0) {
                        DocStatus = "IP";
                    }
                    msg.append(oline.getParent().getDocumentNo()).append(",");
                }
                if ("IP".equals(DocStatus)) {
                    this.m_processMsg = msg.toString();
                    return DocStatus;
                }
                this.setProcessed(true);
                this.setDocAction("CL");
                this.setDocStatus("CO");
                activity.completeIt();
                activity.saveEx(this.get_TrxName());
                this.m_processMsg = String.valueOf(Msg.translate(this.getCtx(), "PP_Order_ID")) + ": " + this.getPP_Order().getDocumentNo() + " " + Msg.translate(this.getCtx(), "PP_Order_Node_ID") + ": " + this.getPP_Order_Node().getValue();
                return DocStatus;
            }
            else {
                CostEngineFactory.getCostEngine(this.getAD_Client_ID()).createActivityControl(this);
                if (activity.getQtyDelivered().compareTo(activity.getQtyRequired()) >= 0) {
                    activity.closeIt();
                    activity.saveEx(this.get_TrxName());
                }
            }
        }
        else if (this.isCostCollectorType("120") && this.getPP_Order_BOMLine_ID() > 0) {
            final MPPOrderBOMLine obomline2 = this.getPP_Order_BOMLine();
            obomline2.setQtyDelivered(obomline2.getQtyDelivered().add(this.getMovementQty()));
            obomline2.setQtyScrap(obomline2.getQtyScrap().add(this.getScrappedQty()));
            obomline2.setQtyReject(obomline2.getQtyReject().add(this.getQtyReject()));
            this.log.fine("OrderLine - Reserved=" + obomline2.getQtyReserved() + ", Delivered=" + obomline2.getQtyDelivered());
            obomline2.saveEx(this.get_TrxName());
            this.log.fine("OrderLine -> Reserved=" + obomline2.getQtyReserved() + ", Delivered=" + obomline2.getQtyDelivered());
            CostEngineFactory.getCostEngine(this.getAD_Client_ID()).createUsageVariances(this, Env.ZERO, Env.ZERO);
        }
        else if (this.isCostCollectorType("120") && this.getPP_Order_Node_ID() > 0) {
            final MPPOrderNode activity = this.getPP_Order_Node();
            activity.setDurationReal(activity.getDurationReal() + this.getDurationReal().intValueExact());
            activity.setSetupTimeReal(activity.getSetupTimeReal() + this.getSetupTimeReal().intValueExact());
            activity.saveEx(this.get_TrxName());
            CostEngineFactory.getCostEngine(this.getAD_Client_ID()).createUsageVariances(this, MPPCostCollector.costva, MPPCostCollector.jmltenagakerja);
        }
        if (MPPCostCollector.qtyreserved.compareTo(BigDecimal.ZERO) == 0) {
            CostEngineFactory.getCostEngine(this.getAD_Client_ID()).createRateVariances(this);
            CostEngineFactory.getCostEngine(this.getAD_Client_ID()).createMethodVariances(this);
        }
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 9);
        if (this.m_processMsg != null) {
            return "IN";
        }
        this.setProcessed(true);
        this.setDocAction("CL");
        this.setDocStatus("CO");
        this.setDescription(String.valueOf(DB.getSQLValue((String)null, "select count(*) from pp_cost_collector where pp_order_id = " + this.getPP_Order_ID() + " and costcollectortype = '100' and docstatus = 'CO'")));
        return "CO";
    }
    
    public boolean voidIt() {
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 2);
        final MPPCostCollector collector = new MPPCostCollector(this.getCtx(), this.getPP_Cost_Collector_ID(), this.get_TrxName());
        if (collector.getCostCollectorType().equals("100")) {
            StorageEngine.createTransaction(this, "W-", new Timestamp(new Date().getTime()), collector.getMovementQty(), true, collector.getM_Warehouse_ID(), collector.getM_AttributeSetInstance_ID(), collector.getM_Warehouse_ID(), false);
        }
        else if (collector.getCostCollectorType().equals("110")) {
            StorageEngine.createTransaction(this, "W+", new Timestamp(new Date().getTime()), collector.getMovementQty(), true, collector.getM_Warehouse_ID(), collector.getM_AttributeSetInstance_ID(), collector.getM_Warehouse_ID(), false);
        }
        if (collector.getCostCollectorType().equals("100")) {
            final MPPOrder order = collector.getPP_Order();
            order.setQtyDelivered(order.getQtyDelivered().subtract(collector.getMovementQty()));
            order.setQtyScrap(order.getQtyScrap().subtract(collector.getScrappedQty()));
            order.setQtyReject(order.getQtyReject().subtract(collector.getQtyReject()));
            if (order.getDocStatus().equals("CL")) {
                order.setDocStatus("CO");
                order.setDocAction("CL");
            }
            order.saveEx(this.get_TrxName());
        }
        else if (collector.getCostCollectorType().equals("110")) {
            final MPPOrderBOMLine obomline = collector.getPP_Order_BOMLine();
            obomline.setQtyDelivered(obomline.getQtyDelivered().subtract(collector.getMovementQty()));
            obomline.setQtyScrap(obomline.getQtyScrap().subtract(collector.getScrappedQty()));
            obomline.setQtyReject(obomline.getQtyReject().subtract(collector.getQtyReject()));
            obomline.saveEx(this.get_TrxName());
        }
        else if (collector.getCostCollectorType().equals("160")) {
            final MPPOrderNode orderNode = collector.getPP_Order_Node();
            final BigDecimal qtydeliver = orderNode.getQtyRequired().subtract(collector.getMovementQty().add(new BigDecimal(orderNode.get_Value("qtyreserved").toString())));
            orderNode.setQtyDelivered(orderNode.getQtyDelivered().subtract(collector.getMovementQty()));
            orderNode.setDurationReal(orderNode.getDurationReal() - collector.getDurationReal().intValue());
            orderNode.set_CustomColumn("qtyreserved", (Object)orderNode.getQtyRequired().subtract(collector.getMovementQty().add(new BigDecimal(orderNode.get_Value("qtyreserved").toString()))));
            if (orderNode.getDocStatus().equals("CL") || orderNode.getDocStatus().equals("IP")) {
                if (qtydeliver.compareTo(BigDecimal.ZERO) == 0) {
                    orderNode.setDocStatus("DR");
                    orderNode.setDocAction("CO");
                }
                else {
                    orderNode.setDocStatus("IP");
                    orderNode.setDocAction("CO");
                }
            }
            orderNode.saveEx();
        }
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 10);
        DB.executeUpdate("delete from Fact_Acct where record_id = " + collector.getPP_Cost_Collector_ID(), this.get_TrxName());
        if (DB.getSQLValueString(this.get_TrxName(), "select coalesce(costingmethod,'') from M_Product_Category_Acct where m_product_category_id = " + collector.getM_Product().getM_Product_Category_ID(), new Object[0]).equals("F")) {
            DB.executeUpdate("update m_cost set currentqty = (select sum(qtyonhand) from m_storage where m_product_id = " + collector.getM_Product_ID() + " and M_AttributeSetInstance_ID = " + collector.getM_AttributeSetInstance_ID() + ") where m_product_id = " + collector.getM_Product_ID() + " and M_AttributeSetInstance_ID = " + collector.getM_AttributeSetInstance_ID(), this.get_TrxName());
        }
        else {
            DB.executeUpdate("update m_cost set currentqty = (select sum(qtyonhand) from m_storage where m_product_id = " + collector.getM_Product_ID() + ") where m_product_id = " + collector.getM_Product_ID() + " and M_CostElement_ID=1000000 ", this.get_TrxName());
        }
        return true;
    }
    
    public boolean closeIt() {
        this.log.info("closeIt - " + this.toString());
        this.setDocAction("--");
        return true;
    }
    
    public boolean reverseCorrectIt() {
        return false;
    }
    
    public boolean reverseAccrualIt() {
        return false;
    }
    
    public boolean reActivateIt() {
        return false;
    }
    
    public String getSummary() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getDescription());
        return sb.toString();
    }
    
    public String getProcessMsg() {
        return this.m_processMsg;
    }
    
    public int getDoc_User_ID() {
        return this.getCreatedBy();
    }
    
    public int getC_Currency_ID() {
        return 0;
    }
    
    public BigDecimal getApprovalAmt() {
        return Env.ZERO;
    }
    
    public File createPDF() {
        try {
            final File temp = File.createTempFile(String.valueOf(this.get_TableName()) + this.get_ID() + "_", ".pdf");
            return this.createPDF(temp);
        }
        catch (Exception e) {
            this.log.severe("Could not create PDF - " + e.getMessage());
            return null;
        }
    }
    
    public File createPDF(final File file) {
        final ReportEngine re = ReportEngine.get(this.getCtx(), 0, this.getPP_Order_ID());
        if (re == null) {
            return null;
        }
        return re.getPDF(file);
    }
    
    public String getDocumentInfo() {
        final MDocType dt = MDocType.get(this.getCtx(), this.getC_DocType_ID());
        return String.valueOf(dt.getName()) + " " + this.getDocumentNo();
    }
    
    protected boolean beforeSave(final boolean newRecord) {
        if (this.getM_Locator_ID() <= 0 && this.getM_Warehouse_ID() > 0) {
            final MWarehouse wh = MWarehouse.get(this.getCtx(), this.getM_Warehouse_ID());
            final MLocator loc = wh.getDefaultLocator();
            if (loc != null) {
                this.setM_Locator_ID(loc.get_ID());
            }
        }
        if (this.isIssue()) {
            if (this.getPP_Order_BOMLine_ID() <= 0) {
                throw new FillMandatoryException(new String[] { "PP_Order_BOMLine_ID" });
            }
            if (this.getC_UOM_ID() <= 0) {
                this.setC_UOM_ID(this.getPP_Order_BOMLine().getC_UOM_ID());
            }
            if (this.getC_UOM_ID() != this.getPP_Order_BOMLine().getC_UOM_ID()) {
                throw new AdempiereException("@PP_Cost_Collector_ID@ @C_UOM_ID@ <> @PP_Order_BOMLine_ID@ @C_UOM_ID@");
            }
        }
        if (this.isActivityControl() && this.getPP_Order_Node_ID() <= 0) {
            throw new FillMandatoryException(new String[] { "PP_Order_Node_ID" });
        }
        return true;
    }
    
    @Override
    public MPPOrderNode getPP_Order_Node() {
        final int node_id = this.getPP_Order_Node_ID();
        if (node_id <= 0) {
            return this.m_orderNode = null;
        }
        if (this.m_orderNode == null || this.m_orderNode.get_ID() != node_id) {
            this.m_orderNode = new MPPOrderNode(this.getCtx(), node_id, this.get_TrxName());
        }
        return this.m_orderNode;
    }
    
    @Override
    public MPPOrderBOMLine getPP_Order_BOMLine() {
        final int id = this.getPP_Order_BOMLine_ID();
        if (id <= 0) {
            return this.m_bomLine = null;
        }
        if (this.m_bomLine == null || this.m_bomLine.get_ID() != id) {
            this.m_bomLine = new MPPOrderBOMLine(this.getCtx(), id, this.get_TrxName());
        }
        this.m_bomLine.set_TrxName(this.get_TrxName());
        return this.m_bomLine;
    }
    
    public MPPOrder getPP_Order() {
        final int id = this.getPP_Order_ID();
        if (id <= 0) {
            return this.m_order = null;
        }
        if (this.m_order == null || this.m_order.get_ID() != id) {
            this.m_order = new MPPOrder(this.getCtx(), id, this.get_TrxName());
        }
        return this.m_order;
    }
    
    public long getDurationBaseSec() {
        return this.getPP_Order().getMPPOrderWorkflow().getDurationBaseSec();
    }
    
    public Timestamp getDateStart() {
        final double duration = this.getDurationReal().doubleValue();
        if (duration != 0.0) {
            final long durationMillis = (long)(this.getDurationReal().doubleValue() * this.getDurationBaseSec() * 1000.0);
            return new Timestamp(this.getMovementDate().getTime() - durationMillis);
        }
        return this.getMovementDate();
    }
    
    public Timestamp getDateFinish() {
        return this.getMovementDate();
    }
    
    private String createPO(final MPPOrderNode activity) {
        String msg = "";
        final HashMap<Integer, MOrder> orders = new HashMap<Integer, MOrder>();
        final String whereClause = "PP_Order_Node_ID=? AND IsSubcontracting=?";
        final Collection<MPPOrderNodeProduct> subcontracts = new Query(this.getCtx(), "PP_Order_Node_Product", whereClause, this.get_TrxName()).setParameters(new Object[] { activity.get_ID(), true }).setOnlyActiveRecords(true).list();
        for (final MPPOrderNodeProduct subcontract : subcontracts) {
            final MProduct product = MProduct.get(this.getCtx(), subcontract.getM_Product_ID());
            if (!product.isPurchased() || !"S".equals(product.getProductType())) {
                throw new AdempiereException("The Product: " + product.getName() + " Do not is Purchase or Service Type");
            }
            int C_BPartner_ID = activity.getC_BPartner_ID();
            MProductPO product_po = null;
            MProductPO[] ofProduct;
            for (int length = (ofProduct = MProductPO.getOfProduct(this.getCtx(), product.get_ID(), (String)null)).length, i = 0; i < length; ++i) {
                final MProductPO ppo = ofProduct[i];
                if (C_BPartner_ID == ppo.getC_BPartner_ID()) {
                    C_BPartner_ID = ppo.getC_BPartner_ID();
                    product_po = ppo;
                    break;
                }
                if (ppo.isCurrentVendor() && ppo.getC_BPartner_ID() != 0) {
                    C_BPartner_ID = ppo.getC_BPartner_ID();
                    product_po = ppo;
                    break;
                }
            }
            if (C_BPartner_ID <= 0 || product_po == null) {
                throw new NoVendorForProductException(product.getName());
            }
            final Timestamp today = new Timestamp(System.currentTimeMillis());
            final Timestamp datePromised = TimeUtil.addDays(today, product_po.getDeliveryTime_Promised());
            MOrder order = orders.get(C_BPartner_ID);
            if (order == null) {
                order = new MOrder(this.getCtx(), 0, this.get_TrxName());
                final MBPartner vendor = MBPartner.get(this.getCtx(), C_BPartner_ID);
                order.setAD_Org_ID(this.getAD_Org_ID());
                order.setBPartner(vendor);
                order.setIsSOTrx(false);
                order.setC_DocTypeTarget_ID();
                order.setDatePromised(datePromised);
                order.setDescription(String.valueOf(Msg.translate(this.getCtx(), "PP_Order_ID")) + ":" + this.getPP_Order().getDocumentNo());
                order.setDocStatus("DR");
                order.setDocAction("CO");
                order.setAD_User_ID(this.getAD_User_ID());
                order.setM_Warehouse_ID(this.getM_Warehouse_ID());
                order.saveEx(this.get_TrxName());
                this.addDescription(String.valueOf(Msg.translate(this.getCtx(), "C_Order_ID")) + ": " + order.getDocumentNo());
                orders.put(C_BPartner_ID, order);
                msg = String.valueOf(msg) + Msg.translate(this.getCtx(), "C_Order_ID") + " : " + order.getDocumentNo() + " - " + Msg.translate(this.getCtx(), "C_BPartner_ID") + " : " + vendor.getName() + " , ";
            }
            BigDecimal QtyOrdered = this.getMovementQty().multiply(subcontract.getQty());
            if (product_po.getOrder_Min().signum() > 0) {
                QtyOrdered = QtyOrdered.max(product_po.getOrder_Min());
            }
            if (product_po.getOrder_Pack().signum() > 0 && QtyOrdered.signum() > 0) {
                QtyOrdered = product_po.getOrder_Pack().multiply(QtyOrdered.divide(product_po.getOrder_Pack(), 0, 0));
            }
            final MOrderLine oline = new MOrderLine(order);
            oline.setM_Product_ID(product.getM_Product_ID());
            oline.setDescription(activity.getDescription());
            oline.setM_Warehouse_ID(this.getM_Warehouse_ID());
            oline.setQty(QtyOrdered);
            oline.setPP_Cost_Collector_ID(this.get_ID());
            oline.setDatePromised(datePromised);
            oline.saveEx(this.get_TrxName());
            this.setProcessed(true);
        }
        return msg;
    }
    
    public MProduct getM_Product() {
        return MProduct.get(this.getCtx(), this.getM_Product_ID());
    }
    
    @Override
    public I_C_UOM getC_UOM() {
        return (I_C_UOM)MUOM.get(this.getCtx(), this.getC_UOM_ID());
    }
    
    public boolean isIssue() {
        return this.isCostCollectorType("110") || (this.isCostCollectorType("130") && this.getPP_Order_BOMLine_ID() > 0) || (this.isCostCollectorType("150") && this.getPP_Order_BOMLine_ID() > 0);
    }
    
    public boolean isReceipt() {
        return this.isCostCollectorType("100");
    }
    
    public boolean isActivityControl() {
        return this.isCostCollectorType("160");
    }
    
    public boolean isVariance() {
        return this.isCostCollectorType("130", "120", "140", "150");
    }
    
    public String getMovementType() {
        if (this.isReceipt()) {
            return "W+";
        }
        if (this.isIssue()) {
            return "W-";
        }
        return null;
    }
    
    public boolean isCostCollectorType(final String... types) {
        final String type = this.getCostCollectorType();
        for (final String t : types) {
            if (type.equals(t)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isFloorStock() {
        final boolean isFloorStock = new Query(this.getCtx(), "PP_Order_BOMLine", "PP_Order_BOMLine_ID=? AND IssueMethod=?", this.get_TrxName()).setOnlyActiveRecords(true).setParameters(new Object[] { this.getPP_Order_BOMLine_ID(), "2" }).match();
        return isFloorStock;
    }
    
    public void setIsSubcontracting(final int PP_Order_Node_ID) {
        this.setIsSubcontracting(MPPOrderNode.get(this.getCtx(), PP_Order_Node_ID, this.get_TrxName()).isSubcontracting());
    }
}
