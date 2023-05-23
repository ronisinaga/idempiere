// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.model.I_M_Product;
import org.compiere.model.MLocator;
import org.libero.tables.I_PP_Order_Node;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCost;
import org.adempiere.model.engines.CostDimension;
import java.util.TreeSet;
import org.compiere.model.MClient;
import org.compiere.model.MWarehouse;
import org.libero.tables.I_PP_Order_BOMLine;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWFNode;
import org.compiere.model.I_AD_Workflow;
import org.libero.exceptions.RoutingExpiredException;
import org.eevolution.model.I_PP_Product_BOM;
import org.libero.exceptions.BOMExpiredException;
import org.compiere.model.MUOM;
import org.compiere.model.POResultSet;
import org.compiere.print.ReportEngine;
import java.io.File;
import org.compiere.util.Msg;
import java.util.Iterator;
import org.compiere.model.ModelValidationEngine;
import org.compiere.process.DocumentEngine;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.DB;
import org.adempiere.exceptions.DocTypeNotFoundException;
import org.compiere.model.MDocType;
import java.util.List;
import org.compiere.model.PO;
import java.sql.ResultSet;
import org.compiere.model.MTable;
import org.compiere.model.MResource;
import org.eevolution.model.MPPProductBOM;
import org.compiere.model.MProject;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MProduct;
import org.compiere.util.KeyNamePair;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.compiere.util.Env;
import java.math.RoundingMode;
import org.compiere.wf.MWorkflow;
import org.eevolution.model.I_PP_Order;
import org.compiere.model.MForecastLine;
import org.compiere.model.Query;
import org.compiere.model.MOrderLine;
import java.util.Properties;
import org.compiere.model.MCostElement;
import java.util.Collection;
import java.math.BigDecimal;
import org.compiere.util.CLogger;
import org.compiere.process.DocOptions;
import org.compiere.process.DocAction;
import org.eevolution.model.X_PP_Order;

public class MPPOrder extends X_PP_Order implements DocAction, DocOptions
{
    private static final long serialVersionUID = 1L;
    private static CLogger log;
    private MPPOrderBOMLine[] m_lines;
    private String m_processMsg;
    private boolean m_justPrepared;
    private MPPOrderWorkflow m_PP_Order_Workflow;
    public static int count_MR;
    private BigDecimal costvarian;
    private Collection<MCostElement> m_costElements;
    private RoutingService m_routingService;
    
    static {
        MPPOrder.log = CLogger.getCLogger((Class)MPPOrder.class);
    }
    
    public static MPPOrder forC_OrderLine_ID(final Properties ctx, final int C_OrderLine_ID, final String trxName) {
        final MOrderLine line = new MOrderLine(ctx, C_OrderLine_ID, trxName);
        return (MPPOrder)new Query(ctx, "PP_Order", "C_OrderLine_ID=? AND M_Product_ID=?", trxName).setParameters(new Object[] { C_OrderLine_ID, line.getM_Product_ID() }).firstOnly();
    }
    
    public static MPPOrder forM_ForecastLine_ID(final Properties ctx, final int C_OrderLine_ID, final String trxName) {
        final MForecastLine line = new MForecastLine(ctx, C_OrderLine_ID, trxName);
        return (MPPOrder)new Query(ctx, "PP_Order", "M_ForecastLine_ID=? AND M_Product_ID=?", trxName).setParameters(new Object[] { C_OrderLine_ID, line.getM_Product_ID() }).firstOnly();
    }
    
    public static void updateQtyBatchs(final Properties ctx, final I_PP_Order order, final boolean override) {
        BigDecimal qtyBatchSize = order.getQtyBatchSize();
        if (qtyBatchSize.signum() == 0 || override) {
            final int AD_Workflow_ID = order.getAD_Workflow_ID();
            if (AD_Workflow_ID <= 0) {
                return;
            }
            final MWorkflow wf = MWorkflow.get(ctx, AD_Workflow_ID);
            qtyBatchSize = wf.getQtyBatchSize().setScale(0, RoundingMode.UP);
            order.setQtyBatchSize(qtyBatchSize);
        }
        BigDecimal QtyBatchs;
        if (qtyBatchSize.signum() == 0) {
            QtyBatchs = Env.ONE;
        }
        else {
            QtyBatchs = order.getQtyOrdered().divide(qtyBatchSize, 0, 0);
        }
        order.setQtyBatchs(QtyBatchs);
    }
    
    public static boolean isQtyAvailable(final MPPOrder order, final ArrayList[][] issue, final Timestamp minGuaranteeDate) {
        boolean isCompleteQtyDeliver = false;
        for (int i = 0; i < issue.length; ++i) {
            final KeyNamePair key = (KeyNamePair)issue[i][0].get(0);
            final boolean isSelected = key.getName().equals("Y");
            if (key != null) {
                if (isSelected) {
                    final String value = (String) issue[i][0].get(2);
                    final KeyNamePair productkey = (KeyNamePair) issue[i][0].get(3);
                    final int M_Product_ID = productkey.getKey();
                    final BigDecimal qtyToDeliver = (BigDecimal) issue[i][0].get(4);
                    final BigDecimal qtyScrapComponent = (BigDecimal) issue[i][0].get(5);
                    final MProduct product = MProduct.get(order.getCtx(), M_Product_ID);
                    if (product != null && product.isStocked()) {
                        int M_AttributeSetInstance_ID = 0;
                        if (value == null && isSelected) {
                            M_AttributeSetInstance_ID = key.getKey();
                        }
                        else if (value != null && isSelected) {
                            final int PP_Order_BOMLine_ID = key.getKey();
                            if (PP_Order_BOMLine_ID > 0) {
                                final MPPOrderBOMLine orderBOMLine = new MPPOrderBOMLine(order.getCtx(), PP_Order_BOMLine_ID, order.get_TrxName());
                                M_AttributeSetInstance_ID = orderBOMLine.getM_AttributeSetInstance_ID();
                            }
                        }
                        final MStorageOnHand[] storages = getStorages(order.getCtx(), M_Product_ID, order.getM_Warehouse_ID(), M_AttributeSetInstance_ID, minGuaranteeDate, order.get_TrxName());
                        if (M_AttributeSetInstance_ID == 0) {
                            BigDecimal toIssue = qtyToDeliver.add(qtyScrapComponent);
                            MStorageOnHand[] array;
                            for (int length = (array = storages).length, j = 0; j < length; ++j) {
                                final MStorageOnHand storage = array[j];
                                if (storage.getQtyOnHand().signum() != 0) {
                                    final BigDecimal issueActual = toIssue.min(storage.getQtyOnHand());
                                    toIssue = toIssue.subtract(issueActual);
                                    if (toIssue.signum() <= 0) {
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            BigDecimal qtydelivered = qtyToDeliver;
                            qtydelivered.setScale(4, 4);
                            qtydelivered = Env.ZERO;
                        }
                        BigDecimal onHand = Env.ZERO;
                        MStorageOnHand[] array2;
                        for (int length2 = (array2 = storages).length, k = 0; k < length2; ++k) {
                            final MStorageOnHand storage = array2[k];
                            onHand = onHand.add(storage.getQtyOnHand());
                        }
                        isCompleteQtyDeliver = (onHand.compareTo(qtyToDeliver.add(qtyScrapComponent)) >= 0);
                        if (!isCompleteQtyDeliver) {
                            break;
                        }
                    }
                }
            }
        }
        return isCompleteQtyDeliver;
    }
    
    public static MStorageOnHand[] getStorages(final Properties ctx, final int M_Product_ID, final int M_Warehouse_ID, final int M_ASI_ID, final Timestamp minGuaranteeDate, final String trxName) {
        final MProduct product = MProduct.get(ctx, M_Product_ID);
        if (product == null || !product.isStocked()) {
            return new MStorageOnHand[0];
        }
        if (product.getM_AttributeSetInstance_ID() == 0) {
            final String MMPolicy = product.getMMPolicy();
            return MStorageOnHand.getWarehouse(ctx, M_Warehouse_ID, M_Product_ID, M_ASI_ID, minGuaranteeDate, "F".equals(MMPolicy), true, 0, trxName);
        }
        final String MMPolicy = product.getMMPolicy();
        return MStorageOnHand.getWarehouse(ctx, M_Warehouse_ID, M_Product_ID, 0, minGuaranteeDate, "F".equals(MMPolicy), true, 0, trxName);
    }
    
    public MPPOrder(final Properties ctx, final int PP_Order_ID, final String trxName) {
        super(ctx, PP_Order_ID, trxName);
        this.m_lines = null;
        this.m_processMsg = null;
        this.m_justPrepared = false;
        this.m_PP_Order_Workflow = null;
        this.costvarian = BigDecimal.ZERO;
        this.m_costElements = null;
        this.m_routingService = null;
        if (PP_Order_ID == 0) {
            this.setDefault();
        }
    }
    
    public MPPOrder(final MProject project, final int PP_Product_BOM_ID, final int AD_Workflow_ID) {
        this(project.getCtx(), 0, project.get_TrxName());
        this.setAD_Client_ID(project.getAD_Client_ID());
        this.setAD_Org_ID(project.getAD_Org_ID());
        this.setC_Campaign_ID(project.getC_Campaign_ID());
        this.setC_Project_ID(project.getC_Project_ID());
        this.setDescription(project.getName());
        this.setLine(10);
        this.setPriorityRule("5");
        if (project.getDateContract() == null) {
            throw new IllegalStateException("Date Contract is mandatory for Manufacturing Order.");
        }
        if (project.getDateFinish() == null) {
            throw new IllegalStateException("Date Finish is mandatory for Manufacturing Order.");
        }
        Timestamp ts = project.getDateContract();
        final Timestamp df = project.getDateContract();
        if (ts != null) {
            this.setDateOrdered(ts);
        }
        if (ts != null) {
            this.setDateStartSchedule(ts);
        }
        ts = project.getDateFinish();
        if (df != null) {
            this.setDatePromised(df);
        }
        this.setM_Warehouse_ID(project.getM_Warehouse_ID());
        this.setPP_Product_BOM_ID(PP_Product_BOM_ID);
        this.setAD_Workflow_ID(AD_Workflow_ID);
        this.setQtyEntered(Env.ONE);
        this.setQtyOrdered(Env.ONE);
        final MPPProductBOM bom = new MPPProductBOM(project.getCtx(), PP_Product_BOM_ID, project.get_TrxName());
        final MProduct product = MProduct.get(project.getCtx(), bom.getM_Product_ID());
        this.setC_UOM_ID(product.getC_UOM_ID());
        this.setM_Product_ID(bom.getM_Product_ID());
        final String where = "IsManufacturingResource = 'Y' AND ManufacturingResourceType = 'PT' AND M_Warehouse_ID = " + project.getM_Warehouse_ID();
        final MResource resoruce = (MResource)MTable.get(project.getCtx(), 487).getPO(where, project.get_TrxName());
        if (resoruce == null) {
            throw new IllegalStateException("Resource is mandatory.");
        }
        this.setS_Resource_ID(resoruce.getS_Resource_ID());
    }
    
    public MPPOrder(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
        this.m_lines = null;
        this.m_processMsg = null;
        this.m_justPrepared = false;
        this.m_PP_Order_Workflow = null;
        this.costvarian = BigDecimal.ZERO;
        this.m_costElements = null;
        this.m_routingService = null;
    }
    
    public BigDecimal getQtyOpen() {
        return this.getQtyOrdered().subtract(this.getQtyDelivered()).subtract(this.getQtyScrap());
    }
    
    public MPPOrderBOMLine[] getLines(final boolean requery) {
        if (this.m_lines != null && !requery) {
            set_TrxName((PO[])this.m_lines, this.get_TrxName());
            return this.m_lines;
        }
        final String whereClause = "PP_Order_ID=?";
        final List<MPPOrderBOMLine> list = new Query(this.getCtx(), "PP_Order_BOMLine", whereClause, this.get_TrxName()).setParameters(new Object[] { this.getPP_Order_ID() }).setOrderBy("Line").list();
        return this.m_lines = list.toArray(new MPPOrderBOMLine[list.size()]);
    }
    
    public MPPOrderBOMLine[] getLines() {
        return this.getLines(true);
    }
    
    public void setC_DocTypeTarget_ID(final String docBaseType) {
        if (this.getC_DocTypeTarget_ID() > 0) {
            return;
        }
        final MDocType[] doc = MDocType.getOfDocBaseType(this.getCtx(), docBaseType);
        if (doc == null) {
            throw new DocTypeNotFoundException(docBaseType, "");
        }
        this.setC_DocTypeTarget_ID(doc[0].get_ID());
    }
    
    public void setProcessed(final boolean processed) {
        super.setProcessed(processed);
        if (this.get_ID() <= 0) {
            return;
        }
        DB.executeUpdateEx("UPDATE PP_Order SET Processed=? WHERE PP_Order_ID=?", new Object[] { processed, this.get_ID() }, this.get_TrxName());
    }
    
    protected boolean beforeSave(final boolean newRecord) {
        if (this.getAD_Client_ID() == 0) {
            this.m_processMsg = "AD_Client_ID = 0";
            return false;
        }
        if (this.getAD_Org_ID() == 0) {
            final int context_AD_Org_ID = Env.getAD_Org_ID(this.getCtx());
            if (context_AD_Org_ID == 0) {
                this.m_processMsg = "AD_Org_ID = 0";
                return false;
            }
            this.setAD_Org_ID(context_AD_Org_ID);
            MPPOrder.log.warning("beforeSave - Changed Org to Context=" + context_AD_Org_ID);
        }
        if (this.getM_Warehouse_ID() == 0) {
            final int ii = Env.getContextAsInt(this.getCtx(), "#M_Warehouse_ID");
            if (ii != 0) {
                this.setM_Warehouse_ID(ii);
            }
        }
        if (this.getC_UOM_ID() <= 0 && this.getM_Product_ID() > 0) {
            this.setC_UOM_ID(this.getM_Product().getC_UOM_ID());
        }
        if (this.getDateFinishSchedule() == null) {
            this.setDateFinishSchedule(this.getDatePromised());
        }
        updateQtyBatchs(this.getCtx(), (I_PP_Order)this, false);
        return true;
    }
    
    protected boolean afterSave(final boolean newRecord, final boolean success) {
        if (!success) {
            return false;
        }
        if ("CL".equals(this.getDocAction()) || "VO".equals(this.getDocAction())) {
            return true;
        }
        if (this.is_ValueChanged("QtyEntered") && !this.isDelivered()) {
            this.deleteWorkflowAndBOM();
            this.explosion();
        }
        if (this.is_ValueChanged("QtyEntered") && this.isDelivered()) {
            throw new AdempiereException("Cannot Change Quantity, Only for Draft or In-Progess Status");
        }
        if (!newRecord) {
            return success;
        }
        this.explosion();
        return true;
    }
    
    protected boolean beforeDelete() {
        if (this.getDocStatus().equals("DR") || this.getDocStatus().equals("IP")) {
            final String whereClause = "PP_Order_ID=? AND AD_Client_ID=?";
            final Object[] params = { this.get_ID(), this.getAD_Client_ID() };
            this.deletePO("PP_Order_Cost", whereClause, params);
            this.deleteWorkflowAndBOM();
        }
        this.setQtyOrdered(Env.ZERO);
        this.orderStock();
        return true;
    }
    
    private void deleteWorkflowAndBOM() {
        if (this.get_ID() <= 0) {
            return;
        }
        final String whereClause = "PP_Order_ID=? AND AD_Client_ID=?";
        final Object[] params = { this.get_ID(), this.getAD_Client_ID() };
        DB.executeUpdateEx("UPDATE PP_Order_Workflow SET PP_Order_Node_ID=NULL WHERE " + whereClause, params, this.get_TrxName());
        this.deletePO("PP_Order_Node_Asset", whereClause, params);
        this.deletePO("PP_Order_Node_Product", whereClause, params);
        this.deletePO("PP_Order_NodeNext", whereClause, params);
        this.deletePO("PP_Order_Node", whereClause, params);
        this.deletePO("PP_Order_Workflow", whereClause, params);
        this.deletePO("PP_Order_BOMLine", whereClause, params);
        this.deletePO("PP_Order_BOM", whereClause, params);
    }
    
    public boolean processIt(final String processAction) {
        this.m_processMsg = null;
        final DocumentEngine engine = new DocumentEngine((DocAction)this, this.getDocStatus());
        return engine.processIt(processAction, this.getDocAction());
    }
    
    public boolean unlockIt() {
        MPPOrder.log.info(this.toString());
        this.setProcessing(false);
        return true;
    }
    
    public boolean invalidateIt() {
        MPPOrder.log.info(this.toString());
        this.setDocAction("PR");
        return true;
    }
    
    public String prepareIt() {
        MPPOrder.log.info(this.toString());
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 1);
        if (this.m_processMsg != null) {
            return "IN";
        }
        final MPPOrderBOMLine[] lines = this.getLines(true);
        if (lines.length == 0) {
            this.m_processMsg = "@NoLines@";
            return "IN";
        }
        if (this.getC_DocType_ID() != 0) {
            for (int i = 0; i < lines.length; ++i) {
                if (lines[i].getM_Warehouse_ID() != this.getM_Warehouse_ID()) {
                    MPPOrder.log.warning("different Warehouse " + lines[i]);
                    this.m_processMsg = "@CannotChangeDocType@";
                    return "IN";
                }
            }
        }
        if ("DR".equals(this.getDocStatus()) || "IP".equals(this.getDocStatus()) || "IN".equals(this.getDocStatus()) || this.getC_DocType_ID() == 0) {
            this.setC_DocType_ID(this.getC_DocTypeTarget_ID());
        }
        final String docBaseType = MDocType.get(this.getCtx(), this.getC_DocType_ID()).getDocBaseType();
        if (!"MQO".equals(docBaseType)) {
            this.reserveStock(lines);
        }
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 8);
        if (this.m_processMsg != null) {
            return "IN";
        }
        this.resetCostsLLForLLC0();
        int lowLevel;
        for (int maxLowLevel = lowLevel = MPPMRP.getMaxLowLevel(this.getCtx(), this.get_TrxName()); lowLevel >= 0; --lowLevel) {
            for (final MProduct product : this.getProducts(lowLevel)) {
                final MPPProductBOM bom = MPPProductBOM.get(this.getCtx(), this.getPP_Product_BOM_ID());
                this.rollup(product, bom);
            }
        }
        for (final MProduct product2 : this.getProductsResource()) {
            final MWorkflow workflow = new MWorkflow(this.getCtx(), this.getAD_Workflow_ID(), this.get_TrxName());
            this.rollup(product2, workflow);
        }
        this.m_justPrepared = true;
        return "IP";
    }
    
    private void orderStock() {
        final MProduct product = this.getM_Product();
        if (!product.isStocked()) {
            return;
        }
        final BigDecimal target = this.getQtyOrdered();
        final BigDecimal difference = target.subtract(this.getQtyReserved()).subtract(this.getQtyDelivered());
        if (difference.signum() == 0) {
            return;
        }
        final BigDecimal ordered = difference;
        final int M_Locator_ID = this.getM_Locator_ID(ordered, product);
        if ("CL".equals(this.getDocAction())) {
            if (!MStorageOnHand.add(this.getCtx(), this.getM_Warehouse_ID(), M_Locator_ID, this.getM_Product_ID(), this.getM_AttributeSetInstance_ID(), ordered, this.get_TrxName())) {
                throw new AdempiereException();
            }
        }
        else if (!MStorageOnHand.add(this.getCtx(), this.getM_Warehouse_ID(), M_Locator_ID, this.getM_Product_ID(), this.getM_AttributeSetInstance_ID(), ordered, this.get_TrxName())) {
            throw new AdempiereException();
        }
        this.setQtyReserved(this.getQtyReserved().add(difference));
    }
    
    private void reserveStock(final MPPOrderBOMLine[] lines) {
        for (final MPPOrderBOMLine line : lines) {
            line.reserveStock();
            line.saveEx(this.get_TrxName());
        }
    }
    
    public boolean approveIt() {
        MPPOrder.log.info("approveIt - " + this.toString());
        final MDocType doc = MDocType.get(this.getCtx(), this.getC_DocType_ID());
        if (doc.getDocBaseType().equals("MQO")) {
            final String whereClause = "PP_Product_BOM_ID=? AND AD_Workflow_ID=?";
            final MQMSpecification qms = (MQMSpecification)new Query(this.getCtx(), "QM_Specification", whereClause, this.get_TrxName()).setParameters(new Object[] { this.getPP_Product_BOM_ID(), this.getAD_Workflow_ID() }).firstOnly();
            return qms == null || qms.isValid(this.getM_AttributeSetInstance_ID());
        }
        this.setIsApproved(true);
        return true;
    }
    
    public boolean rejectIt() {
        MPPOrder.log.info("rejectIt - " + this.toString());
        this.setIsApproved(false);
        return true;
    }
    
    public String completeIt() {
        if ("PR".equals(this.getDocAction())) {
            this.setProcessed(false);
            return "IP";
        }
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
        if (!this.isApproved()) {
            this.approveIt();
        }
        this.createStandardCosts();
        this.createStandardFifo();
        this.autoReportActivities();
        this.setDocAction("CL");
        final String valid = ModelValidationEngine.get().fireDocValidate((PO)this, 9);
        if (valid != null) {
            this.m_processMsg = valid;
            return "IN";
        }
        return "CO";
    }
    
    public boolean isAvailable() {
        final String whereClause = "QtyOnHand >= QtyRequired AND PP_Order_ID=?";
        final boolean available = new Query(this.getCtx(), "RV_PP_Order_Storage", whereClause, this.get_TrxName()).setParameters(new Object[] { this.get_ID() }).match();
        return available;
    }
    
    public boolean voidIt() {
        MPPOrder.log.info(this.toString());
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 2);
        if (this.m_processMsg != null) {
            return false;
        }
        if (this.isDelivered()) {
            throw new AdempiereException("Cannot void this document because exist transactions");
        }
        MPPOrderBOMLine[] lines;
        for (int length = (lines = this.getLines()).length, i = 0; i < length; ++i) {
            final MPPOrderBOMLine line = lines[i];
            final BigDecimal old = line.getQtyRequired();
            if (old.signum() != 0) {
                line.addDescription(Msg.parseTranslation(this.getCtx(), "@Voided@ @QtyRequired@ : (" + old + ")"));
                line.setQtyRequired(Env.ZERO);
                line.saveEx(this.get_TrxName());
            }
        }
        this.getMPPOrderWorkflow().voidActivities();
        final BigDecimal old2 = this.getQtyOrdered();
        if (old2.signum() != 0) {
            this.addDescription(Msg.parseTranslation(this.getCtx(), "@Voided@ @QtyOrdered@ : (" + old2 + ")"));
            this.setQtyOrdered(Env.ZERO);
            this.setQtyEntered(Env.ZERO);
            this.saveEx(this.get_TrxName());
        }
        this.orderStock();
        this.reserveStock(this.getLines());
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 10);
        if (this.m_processMsg != null) {
            return false;
        }
        this.setDocAction("--");
        return true;
    }
    
    public boolean closeIt() {
        MPPOrder.log.info(this.toString());
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 3);
        if (this.m_processMsg != null) {
            return false;
        }
        if ("CL".equals(this.getDocStatus())) {
            return true;
        }
        if (!"CO".equals(this.getDocStatus())) {
            final String DocStatus = this.completeIt();
            this.setDocStatus(DocStatus);
            this.setDocAction("--");
        }
        if (!this.isDelivered()) {
            throw new AdempiereException("Cannot close this document because do not exist transactions");
        }
        this.createVariances();
        MPPOrderBOMLine[] lines;
        for (int length = (lines = this.getLines()).length, i = 0; i < length; ++i) {
            final MPPOrderBOMLine line = lines[i];
            final BigDecimal old = line.getQtyRequired();
            if (old.compareTo(line.getQtyDelivered()) != 0) {
                line.setQtyRequired(line.getQtyDelivered());
                line.addDescription(Msg.parseTranslation(this.getCtx(), "@closed@ @QtyRequired@ (" + old + ")"));
                line.saveEx(this.get_TrxName());
            }
        }
        final MPPOrderWorkflow m_order_wf = this.getMPPOrderWorkflow();
        m_order_wf.closeActivities(m_order_wf.getLastNode(this.getAD_Client_ID()), this.getUpdated(), false);
        final BigDecimal old2 = this.getQtyOrdered();
        if (old2.signum() != 0) {
            this.addDescription(Msg.parseTranslation(this.getCtx(), "@closed@ @QtyOrdered@ : (" + old2 + ")"));
            this.setQtyOrdered(this.getQtyDelivered());
            this.saveEx(this.get_TrxName());
        }
        this.orderStock();
        this.reserveStock(this.getLines());
        this.setDocStatus("CL");
        this.setDocAction("--");
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 11);
        return this.m_processMsg == null;
    }
    
    public boolean reverseCorrectIt() {
        MPPOrder.log.info("reverseCorrectIt - " + this.toString());
        return this.voidIt();
    }
    
    public boolean reverseAccrualIt() {
        MPPOrder.log.info("reverseAccrualIt - " + this.toString());
        return false;
    }
    
    public boolean reActivateIt() {
        this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 12);
        if (this.m_processMsg != null) {
            return false;
        }
        if (this.isDelivered()) {
            throw new AdempiereException("Cannot re activate this document because exist transactions");
        }
        this.setDocAction("CO");
        this.setProcessed(false);
        return true;
    }
    
    public int getDoc_User_ID() {
        return this.getPlanner_ID();
    }
    
    public BigDecimal getApprovalAmt() {
        return Env.ZERO;
    }
    
    public int getC_Currency_ID() {
        return 0;
    }
    
    public String getProcessMsg() {
        return this.m_processMsg;
    }
    
    public String getSummary() {
        return this.getDocumentNo() + "/" + this.getDatePromised();
    }
    
    public File createPDF() {
        try {
            final File temp = File.createTempFile(String.valueOf(this.get_TableName()) + this.get_ID() + "_", ".pdf");
            return this.createPDF(temp);
        }
        catch (Exception e) {
            MPPOrder.log.severe("Could not create PDF - " + e.getMessage());
            return null;
        }
    }
    
    public File createPDF(final File file) {
        final ReportEngine re = ReportEngine.get(this.getCtx(), 8, this.getPP_Order_ID());
        if (re == null) {
            return null;
        }
        return re.getPDF(file);
    }
    
    public String getDocumentInfo() {
        final MDocType dt = MDocType.get(this.getCtx(), this.getC_DocType_ID());
        return String.valueOf(dt.getName()) + " " + this.getDocumentNo();
    }
    
    private void deletePO(final String tableName, final String whereClause, final Object[] params) {
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
    }
    
    public void setQty(final BigDecimal Qty) {
        super.setQtyEntered(Qty);
        super.setQtyOrdered(this.getQtyEntered());
    }
    
    public void setQtyEntered(BigDecimal QtyEntered) {
        if (QtyEntered != null && this.getC_UOM_ID() != 0) {
            final int precision = MUOM.getPrecision(this.getCtx(), this.getC_UOM_ID());
            QtyEntered = QtyEntered.setScale(precision, 4);
        }
        super.setQtyEntered(QtyEntered);
    }
    
    public void setQtyOrdered(BigDecimal QtyOrdered) {
        if (QtyOrdered != null) {
            final int precision = this.getM_Product().getUOMPrecision();
            QtyOrdered = QtyOrdered.setScale(precision, 4);
        }
        super.setQtyOrdered(QtyOrdered);
    }
    
    public MProduct getM_Product() {
        return MProduct.get(this.getCtx(), this.getM_Product_ID());
    }
    
    public MPPOrderBOM getMPPOrderBOM() {
        return (MPPOrderBOM)new Query(this.getCtx(), "PP_Order_BOM", "PP_Order_ID=?", this.get_TrxName()).setParameters(new Object[] { this.getPP_Order_ID() }).firstOnly();
    }
    
    public MPPOrderWorkflow getMPPOrderWorkflow() {
        if (this.m_PP_Order_Workflow != null) {
            return this.m_PP_Order_Workflow;
        }
        return this.m_PP_Order_Workflow = (MPPOrderWorkflow)new Query(this.getCtx(), "PP_Order_Workflow", "PP_Order_ID=?", this.get_TrxName()).setParameters(new Object[] { this.getPP_Order_ID() }).firstOnly();
    }
    
    private void explosion() {
        final MPPProductBOM PP_Product_BOM = MPPProductBOM.get(this.getCtx(), this.getPP_Product_BOM_ID());
        if (this.getM_Product_ID() != PP_Product_BOM.getM_Product_ID()) {
            throw new AdempiereException("@NotMatch@ @PP_Product_BOM_ID@ , @M_Product_ID@");
        }
        final MProduct product = MProduct.get(this.getCtx(), PP_Product_BOM.getM_Product_ID());
        if (!product.isVerified()) {
            throw new AdempiereException("Product BOM Configuration not verified. Please verify the product first - " + product.getValue());
        }
        if (!PP_Product_BOM.isValidFromTo(this.getDateStartSchedule())) {
            throw new BOMExpiredException((I_PP_Product_BOM)PP_Product_BOM, this.getDateStartSchedule());
        }
        final MPPOrderBOM PP_Order_BOM = new MPPOrderBOM(PP_Product_BOM, this.getPP_Order_ID(), this.get_TrxName());
        PP_Order_BOM.setAD_Org_ID(this.getAD_Org_ID());
        PP_Order_BOM.saveEx(this.get_TrxName());
        this.expandBOM(PP_Product_BOM, PP_Order_BOM, this.getQtyOrdered());
        final MWorkflow AD_Workflow = MWorkflow.get(this.getCtx(), this.getAD_Workflow_ID());
        if (!AD_Workflow.isValid()) {
            throw new AdempiereException("Routing is not valid. Please validate it first - " + AD_Workflow.getValue());
        }
        if (AD_Workflow.isValidFromTo(this.getDateStartSchedule())) {
            final MPPOrderWorkflow PP_Order_Workflow = new MPPOrderWorkflow(AD_Workflow, this.get_ID(), this.get_TrxName());
            PP_Order_Workflow.setAD_Org_ID(this.getAD_Org_ID());
            PP_Order_Workflow.saveEx(this.get_TrxName());
            MWFNode[] nodes = null;
            for (int length = (nodes = AD_Workflow.getNodes((boolean)(0 != 0), this.getAD_Client_ID())).length, i = 0; i < length; ++i) {
                final MWFNode AD_WF_Node = nodes[i];
                if (AD_WF_Node.isValidFromTo(this.getDateStartSchedule())) {
                    final MPPOrderNode PP_Order_Node = new MPPOrderNode(AD_WF_Node, PP_Order_Workflow, this.getQtyOrdered(), this.get_TrxName());
                    PP_Order_Node.setAD_Org_ID(this.getAD_Org_ID());
                    PP_Order_Node.set_CustomColumn("jmltenagakerja", (Object)Integer.parseInt(AD_WF_Node.get_Value("jmltenagakerja").toString()));
                    PP_Order_Node.saveEx(this.get_TrxName());
                    MWFNodeNext[] transitions;
                    for (int length2 = (transitions = AD_WF_Node.getTransitions(this.getAD_Client_ID())).length, j = 0; j < length2; ++j) {
                        final MWFNodeNext AD_WF_NodeNext = transitions[j];
                        final MPPOrderNodeNext nodenext = new MPPOrderNodeNext(AD_WF_NodeNext, PP_Order_Node);
                        nodenext.setAD_Org_ID(this.getAD_Org_ID());
                        nodenext.saveEx(this.get_TrxName());
                    }
                    for (final MPPWFNodeProduct wfnp : MPPWFNodeProduct.forAD_WF_Node_ID(this.getCtx(), AD_WF_Node.get_ID())) {
                        final MPPOrderNodeProduct nodeOrderProduct = new MPPOrderNodeProduct(wfnp, PP_Order_Node);
                        nodeOrderProduct.setAD_Org_ID(this.getAD_Org_ID());
                        nodeOrderProduct.saveEx(this.get_TrxName());
                    }
                    for (final MPPWFNodeAsset wfna : MPPWFNodeAsset.forAD_WF_Node_ID(this.getCtx(), AD_WF_Node.get_ID())) {
                        final MPPOrderNodeAsset nodeorderasset = new MPPOrderNodeAsset(wfna, PP_Order_Node);
                        nodeorderasset.setAD_Org_ID(this.getAD_Org_ID());
                        nodeorderasset.saveEx(this.get_TrxName());
                    }
                }
            }
            PP_Order_Workflow.getNodes(true);
            MPPOrderNode[] nodes2 = null;
            for (int length3 = (nodes2 = PP_Order_Workflow.getNodes((boolean)(0 != 0), this.getAD_Client_ID())).length, k = 0; k < length3; ++k) {
                final MPPOrderNode orderNode = nodes2[k];
                if (PP_Order_Workflow.getAD_WF_Node_ID() == orderNode.getAD_WF_Node_ID()) {
                    PP_Order_Workflow.setPP_Order_Node_ID(orderNode.getPP_Order_Node_ID());
                }
                MPPOrderNodeNext[] transitions2;
                for (int length4 = (transitions2 = orderNode.getTransitions(this.getAD_Client_ID())).length, l = 0; l < length4; ++l) {
                    final MPPOrderNodeNext next = transitions2[l];
                    next.setPP_Order_Next_ID();
                    next.saveEx(this.get_TrxName());
                }
            }
            PP_Order_Workflow.saveEx(this.get_TrxName());
            return;
        }
        throw new RoutingExpiredException((I_AD_Workflow)AD_Workflow, this.getDateStartSchedule());
    }
    
    private void expandBOM(final MPPProductBOM PP_Product_BOM, final MPPOrderBOM PP_Order_BOM, final BigDecimal parentQty) {
        MPPProductBOMLine[] lines = null;
        for (int length = (lines = PP_Product_BOM.getLines((boolean)(1 != 0))).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine PP_Product_BOMline = lines[i];
            final MProduct mProduct = (MProduct)PP_Product_BOMline.getM_Product();
            if (PP_Product_BOMline.isValidFromTo(this.getDateStartSchedule())) {
                final MPPOrderBOMLine obl = new MPPOrderBOMLine(PP_Product_BOMline, this.getPP_Order_ID(), PP_Order_BOM.get_ID(), this.getM_Warehouse_ID(), this.get_TrxName());
                obl.setAD_Org_ID(this.getAD_Org_ID());
                obl.setM_Warehouse_ID(this.getM_Warehouse_ID());
                obl.setM_Locator_ID(this.getM_Locator_ID());
                obl.setQtyPlusScrap(parentQty);
                obl.saveEx(this.get_TrxName());
            }
            else {
                MPPOrder.log.fine("BOM Line skiped - " + PP_Product_BOMline);
            }
        }
    }
    
    private void createRequisitionIFpurchased(final MProduct product, final BigDecimal qtyRequired) {
        if (product.isPurchased()) {
            MPPOrder.log.finer("CLASS PPOrder.explosion product is purchased, check stock/creating requisition");
            final MPPMRP mrp = (MPPMRP)new Query(Env.getCtx(), "PP_MRP", "PP_Order_ID=?", this.get_TrxName()).setParameters(new Object[] { this.getPP_Order_ID() }).first();
            final MRequisition req = new MRequisition(Env.getCtx(), 0, this.get_TrxName());
            req.create(mrp.get_ID(), qtyRequired, product.getM_Product_ID(), this.getC_OrderLine().getC_BPartner_ID(), this.getAD_Org_ID(), this.getPlanner_ID(), this.getDatePromised(), String.valueOf(this.getDescription()) + "!!!", this.getM_Warehouse_ID(), this.getC_DocType_ID());
            ++MPPOrder.count_MR;
        }
    }
    
    public static void createReceipt(final MPPOrder order, final Timestamp movementDate, final BigDecimal qtyDelivered, final BigDecimal qtyToDeliver, final BigDecimal qtyScrap, final BigDecimal qtyReject, final int M_Locator_ID, final int M_AttributeSetInstance_ID) {
        if (qtyToDeliver.signum() != 0 || qtyScrap.signum() != 0 || qtyReject.signum() != 0) {
            MPPCostCollector.createCollector(order, order.getM_Product_ID(), M_Locator_ID, M_AttributeSetInstance_ID, order.getS_Resource_ID(), 0, 0, MDocType.getDocType("MCC"), "100", movementDate, qtyToDeliver, qtyScrap, qtyReject, 0, Env.ZERO, Env.ZERO, Env.ZERO, Env.ZERO);
        }
        order.setDateDelivered(movementDate);
        if (order.getDateStart() == null) {
            order.setDateStart(movementDate);
        }
        final BigDecimal DQ = qtyDelivered;
        final BigDecimal SQ = qtyScrap;
        final BigDecimal OQ = qtyToDeliver;
        if (DQ.add(SQ).compareTo(OQ) >= 0) {
            order.setDateFinish(movementDate);
        }
        order.saveEx(order.get_TrxName());
    }
    
    public static void createIssue(final MPPOrder order, final int PP_OrderBOMLine_ID, final Timestamp movementdate, final BigDecimal qty, final BigDecimal qtyScrap, final BigDecimal qtyReject, final MStorageOnHand[] storages, final boolean forceIssue) {
        if (qty.signum() == 0) {
            return;
        }
        final MPPOrderBOMLine PP_orderbomLine = new MPPOrderBOMLine(order.getCtx(), PP_OrderBOMLine_ID, order.get_TrxName());
        BigDecimal toIssue = qty.add(qtyScrap);
        for (final MStorageOnHand storage : storages) {
            if (storage.getQtyOnHand().signum() != 0) {
                final BigDecimal qtyIssue = toIssue.min(storage.getQtyOnHand());
                if (qtyIssue.signum() != 0 || qtyScrap.signum() != 0 || qtyReject.signum() != 0) {
                    String CostCollectorType = "110";
                    if (PP_orderbomLine.getQtyBatch().signum() == 0 && PP_orderbomLine.getQtyBOM().signum() == 0) {
                        CostCollectorType = "130";
                    }
                    else if (PP_orderbomLine.isComponentType("CP")) {
                        CostCollectorType = "150";
                    }
                    MPPCostCollector.createCollector(order, PP_orderbomLine.getM_Product_ID(), storage.getM_Locator_ID(), storage.getM_AttributeSetInstance_ID(), order.getS_Resource_ID(), PP_OrderBOMLine_ID, 0, MDocType.getDocType("MCC"), CostCollectorType, movementdate, qtyIssue, qtyScrap, qtyReject, 0, Env.ZERO, Env.ZERO, Env.ZERO, Env.ZERO);
                }
                toIssue = toIssue.subtract(qtyIssue);
                if (toIssue.signum() == 0) {
                    break;
                }
            }
        }
        if (forceIssue && toIssue.signum() != 0) {
            MPPCostCollector.createCollector(order, PP_orderbomLine.getM_Product_ID(), PP_orderbomLine.getM_Locator_ID(), PP_orderbomLine.getM_AttributeSetInstance_ID(), order.getS_Resource_ID(), PP_OrderBOMLine_ID, 0, MDocType.getDocType("MCC"), "110", movementdate, toIssue, Env.ZERO, Env.ZERO, 0, Env.ZERO, Env.ZERO, Env.ZERO, Env.ZERO);
            return;
        }
        if (toIssue.signum() != 0) {
            throw new AdempiereException("Should not happen toIssue=" + toIssue);
        }
    }
    
    public static boolean isQtyAvailable(final MPPOrder order, final I_PP_Order_BOMLine line) {
        final MProduct product = MProduct.get(order.getCtx(), line.getM_Product_ID());
        if (product == null || !product.isStocked()) {
            return true;
        }
        final BigDecimal qtyToDeliver = line.getQtyRequired();
        final BigDecimal qtyScrap = line.getQtyScrap();
        final BigDecimal qtyRequired = qtyToDeliver.add(qtyScrap);
        final BigDecimal qtyAvailable = MStorageOnHand.getQtyOnHand(line.getM_Product_ID(), order.getM_Warehouse_ID(), line.getM_AttributeSetInstance_ID(), order.get_TrxName());
        return qtyAvailable.compareTo(qtyRequired) >= 0;
    }
    
    public int getM_Locator_ID() {
        final MWarehouse wh = MWarehouse.get(this.getCtx(), this.getM_Warehouse_ID());
        return wh.getDefaultLocator().getM_Locator_ID();
    }
    
    private int getM_Locator_ID(final BigDecimal qty, final MProduct product) {
        int M_Locator_ID = 0;
        final int M_ASI_ID = this.getM_AttributeSetInstance_ID();
        if (M_ASI_ID != 0) {
            M_Locator_ID = MStorageOnHand.getM_Locator_ID(this.getM_Warehouse_ID(), this.getM_Product_ID(), M_ASI_ID, qty, this.get_TrxName());
        }
        if (M_Locator_ID == 0) {
            if (product.getM_Locator_ID() > 0) {
                M_Locator_ID = product.getM_Locator_ID();
            }
            else {
                M_Locator_ID = this.getM_Locator_ID();
            }
        }
        return M_Locator_ID;
    }
    
    public boolean isDelivered() {
        if (this.getQtyDelivered().signum() > 0 || this.getQtyScrap().signum() > 0 || this.getQtyReject().signum() > 0) {
            return true;
        }
        MPPOrderBOMLine[] lines;
        for (int length = (lines = this.getLines()).length, i = 0; i < length; ++i) {
            final MPPOrderBOMLine line = lines[i];
            if (line.getQtyDelivered().signum() > 0) {
                return true;
            }
        }
        MPPOrderNode[] nodes = null;
        for (int length2 = (nodes = this.getMPPOrderWorkflow().getNodes((boolean)(1 != 0), this.getAD_Client_ID())).length, j = 0; j < length2; ++j) {
            final MPPOrderNode node = nodes[j];
            if (node.getQtyDelivered().signum() > 0) {
                return true;
            }
            if (node.getDurationReal() > 0) {
                return true;
            }
        }
        return false;
    }
    
    public void setDefault() {
        this.setLine(10);
        this.setPriorityRule("5");
        this.setDescription("");
        this.setQtyDelivered(Env.ZERO);
        this.setQtyReject(Env.ZERO);
        this.setQtyScrap(Env.ZERO);
        this.setIsSelected(false);
        this.setIsSOTrx(false);
        this.setIsApproved(false);
        this.setIsPrinted(false);
        this.setProcessed(false);
        this.setProcessing(false);
        this.setPosted(false);
        this.setC_DocTypeTarget_ID("MOP");
        this.setC_DocType_ID(this.getC_DocTypeTarget_ID());
        this.setDocStatus("DR");
        this.setDocAction("PR");
        this.setC_OrderLine_ID(MPPMRP.C_OrderLine_ID);
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
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("MPPOrder[").append(this.get_ID()).append("-").append(this.getDocumentNo()).append(",IsSOTrx=").append(this.isSOTrx()).append(",C_DocType_ID=").append(this.getC_DocType_ID()).append("]");
        return sb.toString();
    }
    
    public void autoReportActivities() {
        for (final MPPOrderNode activity : this.getMPPOrderWorkflow().getNodes()) {
            if (activity.isMilestone() && (activity.isSubcontracting() || activity.get_ID() == this.getMPPOrderWorkflow().getPP_Order_Node_ID())) {
                MPPCostCollector.createCollector(this, this.getM_Product_ID(), this.getM_Locator_ID(), this.getM_AttributeSetInstance_ID(), this.getS_Resource_ID(), 0, activity.getPP_Order_Node_ID(), MDocType.getDocType("MCC"), "160", this.getUpdated(), activity.getQtyToDeliver(), Env.ZERO, Env.ZERO, 0, Env.ZERO, Env.ZERO, Env.ZERO, Env.ZERO);
            }
        }
    }
    
    private final void createStandardCosts() {
        final MAcctSchema as = MClient.get(this.getCtx(), this.getAD_Client_ID()).getAcctSchema();
        MPPOrder.log.info("Cost_Group_ID" + as.getM_CostType_ID());
        final TreeSet<Integer> productsAdded = new TreeSet<Integer>();
        final MProduct product = this.getM_Product();
        productsAdded.add(product.getM_Product_ID());
        final CostDimension d = new CostDimension(product, as, as.getM_CostType_ID(), this.getAD_Org_ID(), this.getM_AttributeSetInstance_ID(), -10);
        final Collection<MCost> costs = d.toQuery(MCost.class, this.get_TrxName()).list();
        for (final MCost cost : costs) {
            final MPPOrderCost PP_Order_Cost = new MPPOrderCost(cost, this.get_ID(), this.get_TrxName());
            PP_Order_Cost.saveEx(this.get_TrxName());
        }
        MPPOrderBOMLine[] lines;
        for (int length = (lines = this.getLines()).length, i = 0; i < length; ++i) {
            final MPPOrderBOMLine line = lines[i];
            final MProduct product2 = line.getM_Product();
            if (!productsAdded.contains(product2.getM_Product_ID())) {
                productsAdded.add(product2.getM_Product_ID());
                final CostDimension d2 = new CostDimension(line.getM_Product(), as, as.getM_CostType_ID(), line.getAD_Org_ID(), line.getM_AttributeSetInstance_ID(), -10);
                final Collection<MCost> costs2 = d2.toQuery(MCost.class, this.get_TrxName()).list();
                for (final MCost cost2 : costs2) {
                    final MPPOrderCost PP_Order_Cost2 = new MPPOrderCost(cost2, this.get_ID(), this.get_TrxName());
                    PP_Order_Cost2.saveEx(this.get_TrxName());
                }
            }
        }
        for (final MPPOrderNode node : this.getMPPOrderWorkflow().getNodes(true)) {
            final int S_Resource_ID = node.getS_Resource_ID();
            if (S_Resource_ID <= 0) {
                continue;
            }
            final MProduct resourceProduct = MProduct.forS_Resource_ID(this.getCtx(), S_Resource_ID, (String)null);
            if (productsAdded.contains(resourceProduct.getM_Product_ID())) {
                continue;
            }
            productsAdded.add(resourceProduct.getM_Product_ID());
            final CostDimension d3 = new CostDimension(resourceProduct, as, as.getM_CostType_ID(), node.getAD_Org_ID(), 0, -10);
            final Collection<MCost> costs3 = d3.toQuery(MCost.class, this.get_TrxName()).list();
            for (final MCost cost3 : costs3) {
                final MPPOrderCost orderCost = new MPPOrderCost(cost3, this.getPP_Order_ID(), this.get_TrxName());
                orderCost.saveEx(this.get_TrxName());
            }
        }
    }
    
    private final void createStandardFifo() {
        final MAcctSchema as = MClient.get(this.getCtx(), this.getAD_Client_ID()).getAcctSchema();
        MPPOrder.log.info("Cost_Group_ID" + as.getM_CostType_ID());
        final TreeSet<Integer> productsAdded = new TreeSet<Integer>();
        MPPOrderBOMLine[] lines;
        for (int length = (lines = this.getLines()).length, i = 0; i < length; ++i) {
            final MPPOrderBOMLine line = lines[i];
            final MProduct product = line.getM_Product();
            if (!productsAdded.contains(product.getM_Product_ID())) {
                productsAdded.add(product.getM_Product_ID());
                if (DB.getSQLValue((String)null, "select m_attributesetinstance_id from m_cost where m_product_id = ? and coalesce(m_attributesetinstance_id,0)>0", product.getM_Product_ID()) > 0) {
                    final CostDimension d = new CostDimension(line.getM_Product(), as, as.getM_CostType_ID(), line.getAD_Org_ID(), DB.getSQLValue((String)null, "select m_attributesetinstance_id from m_cost where m_product_id = ? and coalesce(m_attributesetinstance_id,0)>0 ", product.getM_Product_ID()), -10);
                    final Collection<MCost> costs = d.toQuery(MCost.class, this.get_TrxName()).list();
                    for (final MCost cost : costs) {
                        final MPPOrderCost PP_Order_Cost = new MPPOrderCost(cost, this.get_ID(), this.get_TrxName());
                        PP_Order_Cost.saveEx(this.get_TrxName());
                    }
                }
            }
        }
    }
    
    public void createVariances() {
        MPPOrderBOMLine[] lines = null;
        for (int length = (lines = this.getLines((boolean)(1 != 0))).length, i = 0; i < length; ++i) {
            final MPPOrderBOMLine line = lines[i];
            this.createUsageVariance(line);
        }
        this.m_lines = null;
        final MPPOrderWorkflow orderWorkflow = this.getMPPOrderWorkflow();
        if (orderWorkflow != null) {
            for (final MPPOrderNode node : orderWorkflow.getNodes(true)) {
                this.createUsageVariance(node);
                this.createUsageVarianceCost(node);
            }
        }
    }
    
    private void createUsageVariance(final I_PP_Order_BOMLine bomLine) {
        final MPPOrder order = this;
        final Timestamp movementDate = order.getUpdated();
        final MPPOrderBOMLine line = (MPPOrderBOMLine)bomLine;
        if (line.getQtyBatch().signum() == 0 && line.getQtyBOM().signum() == 0) {
            return;
        }
        final BigDecimal qtyUsageVariancePrev = line.getQtyVariance();
        final BigDecimal qtyOpen = line.getQtyOpen();
        final BigDecimal qtyUsageVariance = qtyOpen.subtract(qtyUsageVariancePrev);
        if (qtyUsageVariance.signum() == 0 || this.getQtyReject().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        int M_Locator_ID = line.getM_Locator_ID();
        if (M_Locator_ID <= 0) {
            final MLocator locator = MLocator.getDefault(MWarehouse.get(order.getCtx(), order.getM_Warehouse_ID()));
            if (locator != null) {
                M_Locator_ID = locator.getM_Locator_ID();
            }
        }
        MPPCostCollector.createCollector(order, line.getM_Product_ID(), M_Locator_ID, line.getM_AttributeSetInstance_ID(), order.getS_Resource_ID(), line.getPP_Order_BOMLine_ID(), 0, MDocType.getDocType("MCC"), "120", movementDate, qtyUsageVariance, Env.ZERO, Env.ZERO, 0, Env.ZERO, Env.ZERO, Env.ZERO, Env.ZERO);
    }
    
    public void createUsageVariance(final I_PP_Order_Node orderNode) {
        final MPPOrder order = this;
        final Timestamp movementDate = order.getUpdated();
        final MPPOrderNode node = (MPPOrderNode)orderNode;
        final BigDecimal setupTimeReal = BigDecimal.valueOf(node.getSetupTimeReal());
        final BigDecimal durationReal = BigDecimal.valueOf(node.getDurationReal());
        if (setupTimeReal.signum() == 0 && durationReal.signum() == 0) {
            return;
        }
        final BigDecimal setupTimeVariancePrev = node.getSetupTimeUsageVariance();
        node.getDurationUsageVariance();
        final BigDecimal setupTimeRequired = BigDecimal.valueOf(node.getSetupTimeRequired());
        BigDecimal.valueOf(node.getDurationRequired());
        final BigDecimal qtyOpen = node.getQtyToDeliver().subtract(new BigDecimal(node.get_ValueAsString("qtyreserved")));
        final BigDecimal setupTimeVariance = setupTimeRequired.subtract(setupTimeReal).subtract(setupTimeVariancePrev);
        final BigDecimal durationVariance = new BigDecimal(node.getDuration()).multiply(qtyOpen);
        if (qtyOpen.signum() == 0 && setupTimeVariance.signum() == 0 && durationVariance.signum() == 0 && new BigDecimal(node.get_ValueAsString("qtyreserved")).signum() == 0) {
            return;
        }
        if (new BigDecimal(node.get_ValueAsString("qtyreserved")).compareTo(BigDecimal.ZERO) == 0) {
            MPPCostCollector.createCollector(order, order.getM_Product_ID(), order.getM_Locator_ID(), order.getM_AttributeSetInstance_ID(), node.getS_Resource_ID(), 0, node.getPP_Order_Node_ID(), MDocType.getDocType("MCC"), "120", movementDate, qtyOpen, Env.ZERO, Env.ZERO, setupTimeVariance.intValueExact(), durationVariance, Env.ZERO, Env.ZERO, new BigDecimal(node.get_ValueAsInt("jmltenagakerja")));
        }
        else {
            MPPCostCollector.createCollector(order, this.getM_Product_ID(), this.getM_Locator_ID(), this.getM_AttributeSetInstance_ID(), node.getS_Resource_ID(), 0, node.getPP_Order_Node_ID(), MDocType.getDocType("MCC"), "160", order.getUpdated(), node.getQtyRequired().subtract(node.getQtyDelivered()), Env.ZERO, Env.ZERO, setupTimeVariance.intValueExact(), new BigDecimal(node.getDuration()).multiply(node.getQtyRequired().subtract(node.getQtyDelivered())), Env.ZERO, Env.ZERO, Env.ZERO);
        }
    }
    
    public void createUsageVarianceCost(final I_PP_Order_Node orderNode) {
        final MPPOrder order = this;
        final Timestamp movementDate = order.getUpdated();
        final MPPOrderNode node = (MPPOrderNode)orderNode;
        final BigDecimal setupTimeReal = BigDecimal.valueOf(node.getSetupTimeReal());
        BigDecimal.valueOf(node.getDurationReal());
        final BigDecimal setupTimeVariancePrev = node.getSetupTimeUsageVariance();
        node.getDurationUsageVariance();
        final BigDecimal setupTimeRequired = BigDecimal.valueOf(node.getSetupTimeRequired());
        BigDecimal.valueOf(node.getDurationRequired());
        final BigDecimal qtyOpen = node.getQtyToDeliver().subtract(new BigDecimal(node.get_ValueAsString("qtyreserved")));
        final BigDecimal setupTimeVariance = setupTimeRequired.subtract(setupTimeReal).subtract(setupTimeVariancePrev);
        new BigDecimal(node.getDuration()).multiply(qtyOpen);
        this.costvarian = node.getPP_Order_Workflow().getCost().subtract(DB.getSQLValueBD((String)null, "select sum(currentcostprice) from m_cost where M_CostElement_ID != 1000000 and m_product_id = " + node.getPP_Order_Workflow().getPP_Order().getM_Product_ID(), new Object[0]));
        if (this.costvarian.compareTo(BigDecimal.ZERO) > 0) {
            MPPCostCollector.createCollector(order, order.getM_Product_ID(), order.getM_Locator_ID(), order.getM_AttributeSetInstance_ID(), node.getS_Resource_ID(), 0, node.getPP_Order_Node_ID(), MDocType.getDocType("MCC"), "120", movementDate, node.getQtyRequired().subtract(node.getQtyDelivered()), Env.ZERO, Env.ZERO, setupTimeVariance.intValueExact(), new BigDecimal(node.getDuration()).multiply(node.getQtyRequired().subtract(node.getQtyDelivered())), this.costvarian, Env.ZERO, new BigDecimal(node.get_ValueAsInt("jmltenagakerja")));
        }
    }
    
    public BigDecimal getQtyToDeliver() {
        return this.getQtyOrdered().subtract(this.getQtyDelivered());
    }
    
    public void updateMakeToKit(final BigDecimal qtyShipment) {
        final MPPOrderBOM obom = this.getMPPOrderBOM();
        this.getLines(true);
        if ("K".equals(obom.getBOMType()) && "M".equals(obom.getBOMUse())) {
            final Timestamp today = new Timestamp(System.currentTimeMillis());
            final ArrayList[][] issue = new ArrayList[this.m_lines.length][1];
            for (int i = 0; i < this.getLines().length; ++i) {
                final MPPOrderBOMLine line = this.m_lines[i];
                KeyNamePair id = null;
                if ("1".equals(line.getIssueMethod())) {
                    id = new KeyNamePair(line.get_ID(), "Y");
                }
                else {
                    id = new KeyNamePair(line.get_ID(), "N");
                }
                final ArrayList<Object> data = new ArrayList<Object>();
                final BigDecimal qtyToDeliver = qtyShipment.multiply(line.getQtyMultiplier());
                data.add(id);
                data.add(line.isCritical());
                final MProduct product = line.getM_Product();
                data.add(product.getValue());
                final KeyNamePair productKey = new KeyNamePair(product.get_ID(), product.getName());
                data.add(productKey);
                data.add(qtyToDeliver);
                data.add(Env.ZERO);
                issue[i][0] = data;
            }
            boolean forceIssue = false;
            final MOrderLine oline = (MOrderLine)this.getC_OrderLine();
            if ("L".equals(oline.getParent().getDeliveryRule()) || "O".equals(oline.getParent().getDeliveryRule())) {
                final boolean isCompleteQtyDeliver = isQtyAvailable(this, issue, today);
                if (!isCompleteQtyDeliver) {
                    throw new AdempiereException("@NoQtyAvailable@");
                }
            }
            else {
                if ("A".equals(oline.getParent().getDeliveryRule()) || "R".equals(oline.getParent().getDeliveryRule()) || "M".equals(oline.getParent().getDeliveryRule())) {
                    throw new AdempiereException("@ActionNotSupported@");
                }
                if ("F".equals(oline.getParent().getDeliveryRule())) {
                    forceIssue = true;
                }
            }
            for (int j = 0; j < issue.length; ++j) {
                int M_AttributeSetInstance_ID = 0;
                final KeyNamePair key = (KeyNamePair)issue[j][0].get(0);
                final Boolean b = (Boolean)issue[j][0].get(1);
                final String s = (String)issue[j][0].get(2);
                final KeyNamePair productkey = (KeyNamePair)issue[j][0].get(3);
                final int M_Product_ID = productkey.getKey();
                MProduct.get(this.getCtx(), M_Product_ID);
                final BigDecimal qtyToDeliver2 = (BigDecimal)issue[j][0].get(4);
                final BigDecimal qtyScrapComponent = (BigDecimal)issue[j][0].get(5);
                final int PP_Order_BOMLine_ID = key.getKey();
                if (PP_Order_BOMLine_ID > 0) {
                    final MPPOrderBOMLine orderBOMLine = new MPPOrderBOMLine(this.getCtx(), PP_Order_BOMLine_ID, this.get_TrxName());
                    M_AttributeSetInstance_ID = orderBOMLine.getM_AttributeSetInstance_ID();
                }
                final MStorageOnHand[] storages = getStorages(this.getCtx(), M_Product_ID, this.getM_Warehouse_ID(), M_AttributeSetInstance_ID, today, this.get_TrxName());
                createIssue(this, key.getKey(), today, qtyToDeliver2, qtyScrapComponent, Env.ZERO, storages, forceIssue);
            }
            createReceipt(this, today, this.getQtyDelivered(), qtyShipment, this.getQtyScrap(), this.getQtyReject(), this.getM_Locator_ID(), this.getM_AttributeSetInstance_ID());
        }
    }
    
    protected void rollup(final MProduct product, final MPPProductBOM bom) {
        for (final MCostElement element : this.getCostElements()) {
            for (final MCost cost : this.getCosts(product, element.get_ID())) {
                MPPOrder.log.info("Calculate Lower Cost for: " + bom);
                final BigDecimal price = this.getCurrentCostPriceLL(bom, element);
                MPPOrder.log.info(String.valueOf(element.getName()) + " Cost Low Level:" + price);
                cost.setCurrentCostPriceLL(price);
                this.updateCoProductCosts(bom, cost);
                cost.saveEx();
            }
        }
    }
    
    private void updateCoProductCosts(final MPPProductBOM bom, final MCost baseCost) {
        if (bom == null) {
            return;
        }
        BigDecimal costPriceTotal = Env.ZERO;
        MPPProductBOMLine[] lines;
        for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine bomline = lines[i];
            if (bomline.isCoProduct()) {
                final BigDecimal costPrice = baseCost.getCurrentCostPriceLL().multiply(bomline.getCostAllocationPerc(true));
                MCost cost = MCost.get(baseCost.getCtx(), baseCost.getAD_Client_ID(), baseCost.getAD_Org_ID(), bomline.getM_Product_ID(), baseCost.getM_CostType_ID(), baseCost.getC_AcctSchema_ID(), baseCost.getM_CostElement_ID(), 0, baseCost.get_TrxName());
                if (cost == null) {
                    cost = new MCost(baseCost.getCtx(), 0, baseCost.get_TrxName());
                    cost.setAD_Org_ID(baseCost.getAD_Org_ID());
                    cost.setM_Product_ID(bomline.getM_Product_ID());
                    cost.setM_CostType_ID(baseCost.getM_CostType_ID());
                    cost.setC_AcctSchema_ID(baseCost.getC_AcctSchema_ID());
                    cost.setM_CostElement_ID(baseCost.getM_CostElement_ID());
                    cost.setM_AttributeSetInstance_ID(0);
                }
                cost.setCurrentCostPriceLL(costPrice);
                cost.saveEx();
                costPriceTotal = costPriceTotal.add(costPrice);
            }
        }
        if (costPriceTotal.signum() != 0) {
            baseCost.setCurrentCostPriceLL(costPriceTotal);
        }
    }
    
    private BigDecimal getCurrentCostPriceLL(final MPPProductBOM bom, final MCostElement element) {
        MPPOrder.log.info("Element: " + element);
        BigDecimal costPriceLL = Env.ZERO;
        if (bom == null) {
            return costPriceLL;
        }
        MPPProductBOMLine[] lines;
        for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine bomline = lines[i];
            if (!bomline.isCoProduct()) {
                final MProduct component = MProduct.get(this.getCtx(), bomline.getM_Product_ID());
                for (final MCost cost : this.getCosts(component, element.get_ID())) {
                    final BigDecimal qty = bomline.getQty(true);
                    if (bomline.isByProduct()) {
                        cost.setCurrentCostPriceLL(Env.ZERO);
                    }
                    final BigDecimal costPrice = cost.getCurrentCostPrice().add(cost.getCurrentCostPriceLL());
                    final BigDecimal componentCost = costPrice.multiply(qty);
                    costPriceLL = costPriceLL.add(componentCost);
                    MPPOrder.log.info("CostElement: " + element.getName() + ", Component: " + component.getValue() + ", CostPrice: " + costPrice + ", Qty: " + qty + ", Cost: " + componentCost + " => Total Cost Element: " + costPriceLL);
                }
            }
        }
        return costPriceLL;
    }
    
    private Collection<MCost> getCosts(final MProduct product, final int M_CostElement_ID) {
        final MAcctSchema as = MAcctSchema.get(this.getCtx(), 1000000);
        int hasil = 0;
        CostDimension d = null;
        if (DB.getSQLValue((String)null, "select m_attributesetinstance_id from m_cost where coalesce(m_attributesetinstance_id,0) > 0 and m_product_id = ?", product.getM_Product_ID()) > 0) {
            hasil = DB.getSQLValue((String)null, "select m_attributesetinstance_id from m_cost where coalesce(m_attributesetinstance_id,0) > 0 and m_product_id = ?", product.getM_Product_ID());
        }
        else {
            hasil = 0;
        }
        d = new CostDimension(product, as, 1000000, this.getAD_Org_ID(), hasil, M_CostElement_ID);
        return d.toQuery(MCost.class, this.get_TrxName()).list();
    }
    
    private Collection<MProduct> getProducts(final int lowLevel) {
        final List<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer("AD_Client_ID=?").append(" AND ").append("LowLevel").append("=?");
        params.add(this.getAD_Client_ID());
        params.add(lowLevel);
        whereClause.append(" AND ").append("IsBOM").append("=?");
        params.add(true);
        whereClause.append(" AND ").append("M_Product_ID").append("=?");
        params.add(this.getM_Product_ID());
        return new Query(this.getCtx(), "M_Product", whereClause.toString(), this.get_TrxName()).setParameters((List)params).list();
    }
    
    private void resetCostsLLForLLC0() {
        final List<Object> params = new ArrayList<Object>();
        final StringBuffer productWhereClause = new StringBuffer();
        productWhereClause.append("AD_Client_ID=? AND LowLevel=? AND M_Product_ID=?");
        params.add(this.getAD_Client_ID());
        params.add(0);
        params.add(this.getM_Product_ID());
        final String sql = "UPDATE M_Cost c SET CurrentCostPriceLL=0 WHERE EXISTS (SELECT 1 FROM M_Product p WHERE p.M_Product_ID=c.M_Product_ID AND " + (Object)productWhereClause + ")";
        final int no = DB.executeUpdateEx(sql, params.toArray(), this.get_TrxName());
        MPPOrder.log.info("Updated #" + no);
    }
    
    private Collection<MCostElement> getCostElements() {
        if (this.m_costElements == null) {
            this.m_costElements = (Collection<MCostElement>)MCostElement.getByCostingMethod(this.getCtx(), "S");
        }
        return this.m_costElements;
    }
    
    private Collection<MProduct> getProductsResource() {
        final List<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer("AD_Client_ID=?");
        params.add(this.getAD_Client_ID());
        whereClause.append(" AND (").append("ProductType").append("=?");
        params.add("I");
        whereClause.append(" OR ").append("ProductType").append("=?");
        params.add("R");
        whereClause.append(") AND ").append("IsBOM").append("=?");
        params.add(true);
        whereClause.append(" AND ").append("M_Product_ID").append("=?");
        params.add(this.getM_Product_ID());
        final Collection<MProduct> products = new Query(this.getCtx(), "M_Product", whereClause.toString(), this.get_TrxName()).setOrderBy("LowLevel").setParameters((List)params).list();
        return products;
    }
    
    public void rollup(final MProduct product, final MWorkflow workflow) {
        this.m_routingService = RoutingServiceFactory.get().getRoutingService(this.getAD_Client_ID());
        final MWFNode[] nodes = workflow.getNodes(false, this.getAD_Client_ID());
        MWFNode[] array;
        for (int length = (array = nodes).length, i = 0; i < length; ++i) {
            final MWFNode node = array[i];
            DB.executeUpdate("update PP_Order_Node set cost = " + node.getCost() + " where ad_wf_node_id = " + node.getAD_WF_Node_ID(), (String)null);
        }
    }
    
    public int customizeValidActions(final String docStatus, final Object processing, final String orderType, final String isSOTrx, final int AD_Table_ID, final String[] docAction, final String[] options, int index) {
        for (int i = 0; i < options.length; ++i) {
            options[i] = null;
        }
        index = 0;
        if (docStatus.equals("DR")) {
            options[index++] = "PR";
            options[index++] = "VO";
        }
        else if (docStatus.equals("CO")) {
            options[index++] = "VO";
            options[index++] = "RE";
        }
        else if (docStatus.equals("IN")) {
            options[index++] = "VO";
            options[index++] = "CO";
        }
        else if (docStatus.equals("IP")) {
            options[index++] = "CO";
            options[index++] = "VO";
        }
        return index;
    }
}
