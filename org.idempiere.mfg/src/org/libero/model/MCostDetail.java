// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.model.MProductionLine;
import org.compiere.model.MCostQueue;
import java.math.RoundingMode;
import org.compiere.model.X_M_CostHistory;
import org.compiere.model.MCost;
import org.compiere.model.PO;
import org.compiere.model.MCostElement;
import org.compiere.util.Env;
import java.util.Iterator;
import java.util.List;
import org.compiere.model.MProduct;
import org.compiere.model.Query;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.compiere.util.DB;
import org.compiere.model.MClientInfo;
import java.util.Properties;
import java.util.logging.Level;
import java.math.BigDecimal;
import org.compiere.model.MAcctSchema;
import org.compiere.util.CLogger;
import org.compiere.model.X_M_CostDetail;

public class MCostDetail extends X_M_CostDetail
{
    private static final long serialVersionUID = -3896161579785627935L;
    protected static final String INOUTLINE_DOCBASETYPE_SQL = "SELECT c.DocBaseType From M_InOut io INNER JOIN M_InOutLine iol ON io.M_InOut_ID=iol.M_InOut_ID INNER JOIN C_DocType c ON io.C_DocType_ID=c.C_DocType_ID WHERE iol.M_InOutLine_ID=?";
    private static CLogger s_log;
    
    static {
        MCostDetail.s_log = CLogger.getCLogger((Class)MCostDetail.class);
    }
    
    public static boolean createOrder(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int C_OrderLine_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final String trxName) {
        MCostDetail cd = get(as.getCtx(), "C_OrderLine_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID, C_OrderLine_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setC_OrderLine_ID(C_OrderLine_ID);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    public static boolean createInvoice(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int C_InvoiceLine_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final String trxName) {
        MCostDetail cd = get(as.getCtx(), "C_InvoiceLine_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID + " AND M_Product_ID=" + M_Product_ID, C_InvoiceLine_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setC_InvoiceLine_ID(C_InvoiceLine_ID);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    public static boolean createShipment(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int M_InOutLine_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final boolean IsSOTrx, final String trxName) {
        MCostDetail cd = get(as.getCtx(), "M_InOutLine_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID, M_InOutLine_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setM_InOutLine_ID(M_InOutLine_ID);
            cd.setIsSOTrx(IsSOTrx);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    public static boolean createInventory(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int M_InventoryLine_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final String trxName) {
        MCostDetail cd = get(as.getCtx(), "M_InventoryLine_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID, M_InventoryLine_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setM_InventoryLine_ID(M_InventoryLine_ID);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    public static boolean createMovement(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int M_MovementLine_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final boolean from, final String Description, final String trxName) {
        final StringBuilder msget = new StringBuilder("M_MovementLine_ID=? AND IsSOTrx=").append(from ? "'Y'" : "'N'").append(" AND Coalesce(M_CostElement_ID,0)=").append(M_CostElement_ID);
        MCostDetail cd = get(as.getCtx(), msget.toString(), M_MovementLine_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setM_MovementLine_ID(M_MovementLine_ID);
            cd.setIsSOTrx(from);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    public static boolean createProduction(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int M_ProductionLine_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final String trxName) {
        MCostDetail cd = get(as.getCtx(), "M_ProductionLine_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID, M_ProductionLine_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setM_ProductionLine_ID(M_ProductionLine_ID);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    public static boolean createMatchInvoice(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int M_MatchInv_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final String trxName) {
        MCostDetail cd = get(as.getCtx(), "M_MatchInv_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID, M_MatchInv_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setM_MatchInv_ID(M_MatchInv_ID);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    public static boolean createProjectIssue(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int C_ProjectIssue_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final String trxName) {
        MCostDetail cd = get(as.getCtx(), "C_ProjectIssue_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID, C_ProjectIssue_ID, M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), trxName);
        if (cd == null) {
            cd = new MCostDetail(as, AD_Org_ID, M_Product_ID, M_AttributeSetInstance_ID, M_CostElement_ID, Amt, Qty, Description, trxName);
            cd.setC_ProjectIssue_ID(C_ProjectIssue_ID);
        }
        else {
            if (cd.isProcessed()) {
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            }
            else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            else if (cd.isProcessed()) {
                return true;
            }
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("(" + ok + ") " + cd);
        }
        return ok;
    }
    
    @Deprecated
    public static MCostDetail get(final Properties ctx, final String whereClause, final int ID, final int M_AttributeSetInstance_ID, final String trxName) {
        final StringBuilder sql = new StringBuilder("SELECT * FROM M_CostDetail WHERE ").append(whereClause);
        final MClientInfo clientInfo = MClientInfo.get(ctx);
        final MAcctSchema primary = clientInfo.getMAcctSchema1();
        final int C_AcctSchema_ID = (primary != null) ? primary.getC_AcctSchema_ID() : 0;
        if (C_AcctSchema_ID > 0) {
            sql.append(" AND C_AcctSchema_ID=?");
        }
        MCostDetail retValue = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), (String)null);
            pstmt.setInt(1, ID);
            pstmt.setInt(2, M_AttributeSetInstance_ID);
            if (C_AcctSchema_ID > 0) {
                pstmt.setInt(3, C_AcctSchema_ID);
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                retValue = new MCostDetail(ctx, rs, trxName);
            }
        }
        catch (Exception e) {
            MCostDetail.s_log.log(Level.SEVERE, (Object)sql + " - " + ID, (Throwable)e);
            return retValue;
        }
        finally {
            DB.close(rs, (Statement)pstmt);
        }
        DB.close(rs, (Statement)pstmt);
        return retValue;
    }
    
    public static MCostDetail get(final Properties ctx, final String whereClause, final int ID, final int M_AttributeSetInstance_ID, final int C_AcctSchema_ID, final String trxName) {
        final StringBuilder localWhereClause = new StringBuilder(whereClause).append(" AND M_AttributeSetInstance_ID=?").append(" AND C_AcctSchema_ID=?");
        final MCostDetail retValue = (MCostDetail)new Query(ctx, "M_CostDetail", localWhereClause.toString(), trxName).setParameters(new Object[] { ID, M_AttributeSetInstance_ID, C_AcctSchema_ID }).first();
        return retValue;
    }
    
    public static boolean processProduct(final MProduct product, final String trxName) {
        int counterOK = 0;
        int counterError = 0;
        final List<MCostDetail> list = new Query(product.getCtx(), "M_CostDetail", "M_Product_ID=? AND Processed=?", trxName).setParameters(new Object[] { product.getM_Product_ID(), false }).setOrderBy("C_AcctSchema_ID, M_CostElement_ID, AD_Org_ID, M_AttributeSetInstance_ID, Created").list();
        for (final MCostDetail cd : list) {
            if (cd.process()) {
                ++counterOK;
            }
            else {
                ++counterError;
            }
        }
        if (MCostDetail.s_log.isLoggable(Level.CONFIG)) {
            MCostDetail.s_log.config("OK=" + counterOK + ", Errors=" + counterError);
        }
        return counterError == 0;
    }
    
    public MCostDetail(final Properties ctx, final int M_CostDetail_ID, final String trxName) {
        super(ctx, M_CostDetail_ID, trxName);
        if (M_CostDetail_ID == 0) {
            this.setM_AttributeSetInstance_ID(0);
            this.setProcessed(false);
            this.setAmt(Env.ZERO);
            this.setQty(Env.ZERO);
            this.setIsSOTrx(false);
            this.setDeltaAmt(Env.ZERO);
            this.setDeltaQty(Env.ZERO);
        }
    }
    
    public MCostDetail(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    public MCostDetail(final MAcctSchema as, final int AD_Org_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final int M_CostElement_ID, final BigDecimal Amt, final BigDecimal Qty, final String Description, final String trxName) {
        this(as.getCtx(), 0, trxName);
        this.setClientOrg(as.getAD_Client_ID(), AD_Org_ID);
        this.setC_AcctSchema_ID(as.getC_AcctSchema_ID());
        this.setM_Product_ID(M_Product_ID);
        this.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
        this.setM_CostElement_ID(M_CostElement_ID);
        this.setAmt(Amt);
        this.setQty(Qty);
        this.setDescription(Description);
    }
    
    public void setAmt(final BigDecimal Amt) {
        if (this.isProcessed()) {
            throw new IllegalStateException("Cannot change Amt - processed");
        }
        if (Amt == null) {
            super.setAmt(Env.ZERO);
        }
        else {
            super.setAmt(Amt);
        }
    }
    
    public void setQty(final BigDecimal Qty) {
        if (this.isProcessed()) {
            throw new IllegalStateException("Cannot change Qty - processed");
        }
        if (Qty == null) {
            super.setQty(Env.ZERO);
        }
        else {
            super.setQty(Qty);
        }
    }
    
    public boolean isOrder() {
        return this.getC_OrderLine_ID() != 0;
    }
    
    public boolean isInvoice() {
        return this.getC_InvoiceLine_ID() != 0;
    }
    
    public boolean isShipment() {
        return this.isSOTrx() && this.getM_InOutLine_ID() != 0;
    }
    
    public boolean isVendorRMA() {
        if (!this.isSOTrx() && this.getM_InOutLine_ID() > 0) {
            final String docBaseType = DB.getSQLValueString((String)null, "SELECT c.DocBaseType From M_InOut io INNER JOIN M_InOutLine iol ON io.M_InOut_ID=iol.M_InOut_ID INNER JOIN C_DocType c ON io.C_DocType_ID=c.C_DocType_ID WHERE iol.M_InOutLine_ID=?", this.getM_InOutLine_ID());
            return "MMS".equals(docBaseType);
        }
        return false;
    }
    
    public boolean isDelta() {
        return this.getDeltaAmt().signum() != 0 || this.getDeltaQty().signum() != 0;
    }
    
    protected boolean beforeDelete() {
        return !this.isProcessed();
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder("MCostDetail[");
        sb.append(this.get_ID());
        if (this.getC_OrderLine_ID() != 0) {
            sb.append(",C_OrderLine_ID=").append(this.getC_OrderLine_ID());
        }
        if (this.getM_InOutLine_ID() != 0) {
            sb.append(",M_InOutLine_ID=").append(this.getM_InOutLine_ID());
        }
        if (this.getC_InvoiceLine_ID() != 0) {
            sb.append(",C_InvoiceLine_ID=").append(this.getC_InvoiceLine_ID());
        }
        if (this.getC_ProjectIssue_ID() != 0) {
            sb.append(",C_ProjectIssue_ID=").append(this.getC_ProjectIssue_ID());
        }
        if (this.getM_MovementLine_ID() != 0) {
            sb.append(",M_MovementLine_ID=").append(this.getM_MovementLine_ID());
        }
        if (this.getM_InventoryLine_ID() != 0) {
            sb.append(",M_InventoryLine_ID=").append(this.getM_InventoryLine_ID());
        }
        if (this.getM_ProductionLine_ID() != 0) {
            sb.append(",M_ProductionLine_ID=").append(this.getM_ProductionLine_ID());
        }
        sb.append(",Amt=").append(this.getAmt()).append(",Qty=").append(this.getQty());
        if (this.isDelta()) {
            sb.append(",DeltaAmt=").append(this.getDeltaAmt()).append(",DeltaQty=").append(this.getDeltaQty());
        }
        sb.append("]");
        return sb.toString();
    }
    
    public synchronized boolean process() {
        if (this.isProcessed()) {
            this.log.info("Already processed");
            return true;
        }
        boolean ok = false;
        final MAcctSchema as = MAcctSchema.get(this.getCtx(), this.getC_AcctSchema_ID());
        final MProduct product = new MProduct(this.getCtx(), this.getM_Product_ID(), this.get_TrxName());
        final String CostingLevel = product.getCostingLevel(as);
        int Org_ID = this.getAD_Org_ID();
        int M_ASI_ID = this.getM_AttributeSetInstance_ID();
        if ("C".equals(CostingLevel)) {
            Org_ID = 0;
            M_ASI_ID = 0;
        }
        else if ("O".equals(CostingLevel)) {
            M_ASI_ID = 0;
        }
        else if ("B".equals(CostingLevel)) {
            Org_ID = 0;
        }
        if (this.getM_CostElement_ID() == 0) {
            final MCostElement[] ces = MCostElement.getCostingMethods((PO)this);
            for (int i = 0; i < ces.length; ++i) {
                final MCostElement ce = ces[i];
                if ((!ce.isAverageInvoice() && !ce.isAveragePO() && !ce.isLifo() && !ce.isFifo()) || product.isStocked()) {
                    ok = this.process(as, product, ce, Org_ID, M_ASI_ID);
                    if (!ok) {
                        break;
                    }
                }
            }
        }
        else {
            final MCostElement ce2 = MCostElement.get(this.getCtx(), this.getM_CostElement_ID());
            if (ce2.getCostingMethod() == null) {
                final MCostElement[] ces2 = MCostElement.getCostingMethods((PO)this);
                MCostElement[] array;
                for (int length = (array = ces2).length, j = 0; j < length; ++j) {
                    final MCostElement costingElement = array[j];
                    if ((!costingElement.isAverageInvoice() && !costingElement.isAveragePO() && !costingElement.isLifo() && !costingElement.isFifo()) || product.isStocked()) {
                        ok = this.process(as, product, costingElement, Org_ID, M_ASI_ID);
                        if (!ok) {
                            break;
                        }
                    }
                }
            }
            else if (ce2.isAverageInvoice() || ce2.isAveragePO() || ce2.isLifo() || ce2.isFifo()) {
                if (product.isStocked()) {
                    ok = this.process(as, product, ce2, Org_ID, M_ASI_ID);
                }
            }
            else {
                ok = this.process(as, product, ce2, Org_ID, M_ASI_ID);
            }
        }
        if (ok) {
            this.setDeltaAmt((BigDecimal)null);
            this.setDeltaQty((BigDecimal)null);
            this.setProcessed(true);
            ok = this.save();
        }
        if (this.log.isLoggable(Level.INFO)) {
            this.log.info(String.valueOf(ok) + " - " + this.toString());
        }
        return ok;
    }
    
    protected boolean process(final MAcctSchema as, final MProduct product, final MCostElement ce, final int Org_ID, final int M_ASI_ID) {
        String costingMethod = product.getCostingMethod(as);
        if ("I".equals(costingMethod)) {
            if (ce.isAveragePO()) {
                return true;
            }
        }
        else if ("A".equals(costingMethod) && ce.isAverageInvoice()) {
            return true;
        }
        final MCost cost = MCost.get(product, M_ASI_ID, as, Org_ID, ce.getM_CostElement_ID(), this.get_TrxName());
        DB.getDatabase().forUpdate((PO)cost, 120);
        final X_M_CostHistory history = new X_M_CostHistory(this.getCtx(), 0, this.get_TrxName());
        history.setM_AttributeSetInstance_ID(cost.getM_AttributeSetInstance_ID());
        history.setM_CostDetail_ID(this.getM_CostDetail_ID());
        history.setM_CostElement_ID(ce.getM_CostElement_ID());
        history.setM_CostType_ID(cost.getM_CostType_ID());
        history.setAD_Org_ID(cost.getAD_Org_ID());
        history.setOldQty(cost.getCurrentQty());
        history.setOldCostPrice(cost.getCurrentCostPrice());
        history.setOldCAmt(cost.getCumulatedAmt());
        history.setOldCQty(cost.getCumulatedQty());
        BigDecimal qty = Env.ZERO;
        BigDecimal amt = Env.ZERO;
        if (this.isDelta()) {
            qty = this.getDeltaQty();
            amt = this.getDeltaAmt();
        }
        else {
            qty = this.getQty();
            amt = this.getAmt();
        }
        boolean costAdjustment = false;
        if (this.getM_CostElement_ID() > 0 && this.getM_CostElement_ID() != ce.getM_CostElement_ID()) {
            final MCostElement thisCostElement = MCostElement.get(this.getCtx(), this.getM_CostElement_ID());
            if (thisCostElement.getCostingMethod() == null && ce.getCostingMethod() != null) {
                qty = BigDecimal.ZERO;
                costAdjustment = true;
            }
        }
        final int precision = as.getCostingPrecision();
        BigDecimal price = amt;
        if (qty.signum() != 0) {
            price = amt.divide(qty, precision, RoundingMode.HALF_UP);
        }
        if (this.getC_OrderLine_ID() != 0) {
            final boolean isReturnTrx = qty.signum() < 0;
            if (ce.isAveragePO()) {
                cost.setWeightedAverage(amt, qty);
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("PO - AveragePO - " + cost);
                }
            }
            else if (ce.isLastPOPrice() && !costAdjustment) {
                if (!isReturnTrx) {
                    if (qty.signum() != 0) {
                        cost.setCurrentCostPrice(price);
                    }
                    else {
                        final BigDecimal cCosts = cost.getCurrentCostPrice().add(amt);
                        cost.setCurrentCostPrice(cCosts);
                    }
                }
                cost.add(amt, qty);
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("PO - LastPO - " + cost);
                }
            }
            else if (ce.isStandardCosting() && !costAdjustment) {
                if (cost.getCurrentCostPrice().signum() == 0 && cost.getCurrentCostPriceLL().signum() == 0) {
                    cost.setCurrentCostPrice(price);
                    if (cost.getCurrentCostPrice().signum() == 0) {
                        cost.setCurrentCostPrice(MCost.getSeedCosts(product, M_ASI_ID, as, Org_ID, ce.getCostingMethod(), this.getC_OrderLine_ID()));
                    }
                    if (this.log.isLoggable(Level.FINEST)) {
                        this.log.finest("PO - Standard - CurrentCostPrice(seed)=" + cost.getCurrentCostPrice() + ", price=" + price);
                    }
                }
                cost.add(amt, qty);
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("PO - Standard - " + cost);
                }
            }
            else if (ce.isUserDefined()) {
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("PO - UserDef - " + cost);
                }
            }
            else if (!ce.isCostingMethod() && this.log.isLoggable(Level.FINER)) {
                this.log.finer("PO - " + ce + " - " + cost);
            }
        }
        else if (this.getC_InvoiceLine_ID() != 0) {
            final boolean isReturnTrx = qty.signum() < 0;
            if (ce.isAverageInvoice()) {
                cost.setWeightedAverage(amt, qty);
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("Inv - AverageInv - " + cost);
                }
            }
            else if (ce.isAveragePO() && costAdjustment) {
                cost.setWeightedAverage(amt, qty);
            }
            else if (ce.isFifo() || ce.isLifo()) {
                final MCostQueue cq = MCostQueue.get(product, this.getM_AttributeSetInstance_ID(), as, Org_ID, ce.getM_CostElement_ID(), this.get_TrxName());
                cq.setCosts(amt, qty, precision);
                cq.saveEx();
                final MCostQueue[] cQueue = MCostQueue.getQueue(product, M_ASI_ID, as, Org_ID, ce, this.get_TrxName());
                if (cQueue != null && cQueue.length > 0) {
                    cost.setCurrentCostPrice(cQueue[0].getCurrentCostPrice());
                }
                cost.add(amt, qty);
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("Inv - FiFo/LiFo - " + cost);
                }
            }
            else if (ce.isLastInvoice() && !costAdjustment) {
                if (!isReturnTrx) {
                    if (qty.signum() != 0) {
                        cost.setCurrentCostPrice(price);
                    }
                    else {
                        final BigDecimal cCosts = cost.getCurrentCostPrice().add(amt);
                        cost.setCurrentCostPrice(cCosts);
                    }
                }
                cost.add(amt, qty);
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("Inv - LastInv - " + cost);
                }
            }
            else if (ce.isStandardCosting() && !costAdjustment) {
                if (cost.getCurrentCostPrice().signum() == 0 && cost.getCurrentCostPriceLL().signum() == 0) {
                    cost.setCurrentCostPrice(price);
                    if (cost.getCurrentCostPrice().signum() == 0) {
                        cost.setCurrentCostPrice(MCost.getSeedCosts(product, M_ASI_ID, as, Org_ID, ce.getCostingMethod(), this.getC_OrderLine_ID()));
                        if (this.log.isLoggable(Level.FINEST)) {
                            this.log.finest("Inv - Standard - CurrentCostPrice(seed)=" + cost.getCurrentCostPrice() + ", price=" + price);
                        }
                    }
                    cost.add(amt, qty);
                }
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("Inv - Standard - " + cost);
                }
            }
            else if (ce.isUserDefined()) {
                cost.add(amt, qty);
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("Inv - UserDef - " + cost);
                }
            }
        }
        else if (this.getM_InOutLine_ID() != 0 && costAdjustment) {
            if (ce.isAverageInvoice()) {
                cost.setWeightedAverage(amt, qty);
            }
        }
        else if (this.getM_InOutLine_ID() != 0 || this.getM_MovementLine_ID() != 0 || this.getM_InventoryLine_ID() != 0 || this.getM_ProductionLine_ID() != 0 || this.getC_ProjectIssue_ID() != 0 || this.getPP_Cost_Collector_ID() != 0) {
            final boolean addition = qty.signum() > 0;
            final boolean adjustment = this.getM_InventoryLine_ID() > 0 && qty.signum() == 0 && amt.signum() != 0;
            final boolean isVendorRMA = this.isVendorRMA();
            if (ce.isAverageInvoice()) {
                if (!isVendorRMA) {
                    if (adjustment) {
                        costingMethod = this.getM_InventoryLine().getM_Inventory().getCostingMethod();
                        if ("I".equals(costingMethod)) {
                            if (cost.getCurrentQty().signum() == 0 && qty.signum() == 0) {
                                cost.setWeightedAverageInitial(amt);
                            }
                            else {
                                cost.setWeightedAverage(amt.multiply(cost.getCurrentQty()), qty);
                            }
                        }
                    }
                    else if (addition) {
                        cost.setWeightedAverage(amt, qty);
                        if (this.isShipment()) {
                            cost.setCumulatedQty(history.getOldCQty());
                            cost.setCumulatedAmt(history.getOldCAmt());
                        }
                    }
                    else {
                        cost.setCurrentQty(cost.getCurrentQty().add(qty));
                    }
                    if (this.log.isLoggable(Level.FINER)) {
                        this.log.finer("QtyAdjust - AverageInv - " + cost);
                    }
                }
            }
            else if (ce.isAveragePO()) {
                if (adjustment) {
                    costingMethod = this.getM_InventoryLine().getM_Inventory().getCostingMethod();
                    if ("A".equals(costingMethod)) {
                        if (cost.getCurrentQty().signum() == 0 && qty.signum() == 0) {
                            cost.setWeightedAverageInitial(amt);
                        }
                        else {
                            cost.setWeightedAverage(amt.multiply(cost.getCurrentQty()), qty);
                        }
                    }
                }
                else if (addition) {
                    cost.setWeightedAverage(amt, qty);
                    if (this.isShipment() && !this.isVendorRMA()) {
                        cost.setCumulatedQty(history.getOldCQty());
                        cost.setCumulatedAmt(history.getOldCAmt());
                    }
                }
                else if (isVendorRMA) {
                    cost.setWeightedAverage(amt, qty);
                }
                else {
                    cost.setCurrentQty(cost.getCurrentQty().add(qty));
                }
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("QtyAdjust - AveragePO - " + cost);
                }
            }
            else if (ce.isFifo() || ce.isLifo()) {
                if (!isVendorRMA && !adjustment) {
                    if (addition) {
                        final MCostQueue cq2 = MCostQueue.get(product, this.getM_AttributeSetInstance_ID(), as, Org_ID, ce.getM_CostElement_ID(), this.get_TrxName());
                        cq2.setCosts(amt, qty, precision);
                        cq2.saveEx();
                    }
                    else {
                        MCostQueue.adjustQty(product, M_ASI_ID, as, Org_ID, ce, qty.negate(), this.get_TrxName());
                    }
                    final MCostQueue[] cQueue2 = MCostQueue.getQueue(product, M_ASI_ID, as, Org_ID, ce, this.get_TrxName());
                    if (cQueue2 != null && cQueue2.length > 0) {
                        cost.setCurrentCostPrice(cQueue2[0].getCurrentCostPrice());
                    }
                    cost.setCurrentQty(cost.getCurrentQty().add(qty));
                    if (this.log.isLoggable(Level.FINER)) {
                        this.log.finer("QtyAdjust - FiFo/Lifo - " + cost);
                    }
                }
            }
            else if (ce.isLastInvoice() && !isVendorRMA && !adjustment) {
                cost.setCurrentQty(cost.getCurrentQty().add(qty));
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("QtyAdjust - LastInv - " + cost);
                }
            }
            else if (ce.isLastPOPrice() && !isVendorRMA && !adjustment) {
                cost.setCurrentQty(cost.getCurrentQty().add(qty));
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("QtyAdjust - LastPO - " + cost);
                }
            }
            else if (ce.isStandardCosting() && !isVendorRMA) {
                if (adjustment) {
                    costingMethod = this.getM_InventoryLine().getM_Inventory().getCostingMethod();
                    if ("S".equals(costingMethod)) {
                        cost.add(amt.multiply(cost.getCurrentQty()), qty);
                        cost.setCurrentCostPrice(cost.getCurrentCostPrice().add(amt));
                    }
                }
                else if (addition) {
                    final MProductionLine productionLine = (this.getM_ProductionLine_ID() > 0) ? new MProductionLine(this.getCtx(), this.getM_ProductionLine_ID(), this.get_TrxName()) : null;
                    if (productionLine != null && productionLine.getProductionReversalId() > 0) {
                        cost.setCurrentQty(cost.getCurrentQty().add(qty));
                    }
                    else {
                        cost.add(amt, qty);
                    }
                    if (cost.getCurrentCostPrice().signum() == 0 && cost.getCurrentCostPriceLL().signum() == 0 && cost.is_new()) {
                        cost.setCurrentCostPrice(price);
                        if (this.log.isLoggable(Level.FINEST)) {
                            this.log.finest("QtyAdjust - Standard - CurrentCostPrice=" + price);
                        }
                    }
                }
                else {
                    cost.setCurrentQty(cost.getCurrentQty().add(qty));
                }
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("QtyAdjust - Standard - " + cost);
                }
            }
            else if (ce.isUserDefined() && !isVendorRMA && !adjustment) {
                if (addition) {
                    cost.add(amt, qty);
                }
                else {
                    cost.setCurrentQty(cost.getCurrentQty().add(qty));
                }
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("QtyAdjust - UserDef - " + cost);
                }
            }
            else if (!ce.isCostingMethod()) {
                if (this.log.isLoggable(Level.FINER)) {
                    this.log.finer("QtyAdjust - ?none? - " + cost);
                }
            }
            else if (ce.isStandardCosting() && isVendorRMA) {
                cost.add(amt, qty);
            }
            else {
                this.log.warning("QtyAdjust - " + ce + " - " + cost);
            }
        }
        else {
            if (this.getM_MatchInv_ID() <= 0) {
                this.log.warning("Unknown Type: " + this.toString());
                return false;
            }
            if (ce.isAveragePO()) {
                cost.setWeightedAverage(amt, qty);
            }
        }
        if (as.getCostingMethod().equals(ce.getCostingMethod())) {
            this.setCurrentCostPrice(cost.getCurrentCostPrice());
            this.setCurrentQty(cost.getCurrentQty());
            this.setCumulatedAmt(cost.getCumulatedAmt());
            this.setCumulatedQty(cost.getCumulatedQty());
        }
        history.setNewQty(cost.getCurrentQty());
        history.setNewCostPrice(cost.getCurrentCostPrice());
        history.setNewCAmt(cost.getCumulatedAmt());
        history.setNewCQty(cost.getCumulatedQty());
        return ((history.getNewQty().compareTo(history.getOldQty()) == 0 && history.getNewCostPrice().compareTo(history.getOldCostPrice()) == 0) || history.save()) && cost.save();
    }
}
