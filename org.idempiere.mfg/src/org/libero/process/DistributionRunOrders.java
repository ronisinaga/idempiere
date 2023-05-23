// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.eevolution.model.MPPProductBOMLine;
import org.eevolution.model.MPPProductBOM;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.libero.model.MPPMRP;
import org.compiere.model.MWarehouse;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import java.util.Properties;
import org.compiere.model.MPInstancePara;
import org.compiere.process.ProcessInfo;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.util.Trx;
import org.compiere.model.MDocType;
import org.compiere.util.Env;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.compiere.model.MDistributionRunLine;
import org.compiere.model.MStorageOnHand;
import org.compiere.util.DB;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.math.BigDecimal;
import org.compiere.model.MDistributionRun;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class DistributionRunOrders extends SvrProcess
{
    private int p_M_DistributionList_ID;
    private Timestamp p_DatePromised;
    private int p_AD_Org_ID;
    private String p_IsTest;
    private int p_M_Warehouse_ID;
    private String p_ConsolidateDocument;
    private String p_BasedInDamnd;
    private MDistributionRun m_run;
    
    public DistributionRunOrders() {
        this.p_M_DistributionList_ID = 0;
        this.p_DatePromised = null;
        this.p_AD_Org_ID = 0;
        this.p_IsTest = "N";
        this.p_M_Warehouse_ID = 0;
        this.p_ConsolidateDocument = "N";
        this.p_BasedInDamnd = "N";
        this.m_run = null;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("AD_Org_ID")) {
                    this.p_AD_Org_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("M_Warehouse_ID")) {
                    this.p_M_Warehouse_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("M_DistributionList_ID")) {
                    this.p_M_DistributionList_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("DatePromised")) {
                    this.p_DatePromised = (Timestamp)para[i].getParameter();
                }
                else if (name.equals("ConsolidateDocument")) {
                    this.p_ConsolidateDocument = (String)para[i].getParameter();
                }
                else if (name.equals("IsRequiredDRP")) {
                    this.p_BasedInDamnd = (String)para[i].getParameter();
                }
                else if (name.equals("IsTest")) {
                    this.p_IsTest = (String)para[i].getParameter();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        if (this.p_BasedInDamnd.equals("Y")) {
            if (!this.generateDistributionDemand()) {
                throw new Exception(Msg.getMsg(this.getCtx(), "ProcessFailed"), CLogger.retrieveException());
            }
        }
        else if (!this.generateDistribution()) {
            throw new Exception(Msg.getMsg(this.getCtx(), "ProcessFailed"), CLogger.retrieveException());
        }
        if (!this.executeDistribution()) {
            throw new Exception(Msg.getMsg(this.getCtx(), "ProcessFailed"), CLogger.retrieveException());
        }
        return Msg.getMsg(this.getCtx(), "ProcessOK");
    }
    
    public boolean generateDistribution() {
        (this.m_run = new MDistributionRun(this.getCtx(), 0, this.get_TrxName())).setName("Generate from DRP " + this.p_DatePromised);
        this.m_run.save();
        final StringBuffer sql = new StringBuffer("SELECT M_Product_ID , SUM (QtyOrdered-QtyDelivered) AS TotalQty, l.M_Warehouse_ID FROM DD_OrderLine ol INNER JOIN M_Locator l ON (l.M_Locator_ID=ol.M_Locator_ID) INNER JOIN DD_Order o ON (o.DD_Order_ID=ol.DD_Order_ID) ");
        sql.append(" WHERE o.DocStatus IN ('DR','IN') AND ol.DatePromised <= ? AND l.M_Warehouse_ID=? GROUP BY M_Product_ID");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), this.get_TrxName());
            pstmt.setTimestamp(1, this.p_DatePromised);
            pstmt.setInt(2, this.p_M_Warehouse_ID);
            rs = pstmt.executeQuery();
            int line = 10;
            while (rs.next()) {
                final int M_Product_ID = rs.getInt("M_Product_ID");
                final BigDecimal QtyAvailable = MStorageOnHand.getQtyOnHand(M_Product_ID, this.p_M_Warehouse_ID, 0, this.get_TrxName());
                BigDecimal QtyOrdered = rs.getBigDecimal("TotalQty");
                final MDistributionRunLine m_runLine = new MDistributionRunLine(this.getCtx(), 0, this.get_TrxName());
                m_runLine.setM_DistributionRun_ID(this.m_run.getM_DistributionRun_ID());
                m_runLine.setAD_Org_ID(this.p_AD_Org_ID);
                m_runLine.setM_DistributionList_ID(this.p_M_DistributionList_ID);
                m_runLine.setLine(line);
                m_runLine.setM_Product_ID(M_Product_ID);
                m_runLine.setDescription(String.valueOf(Msg.getMsg(this.getCtx(), "QtyAvailable")) + " : " + QtyAvailable + " " + Msg.getMsg(this.getCtx(), "QtyOrdered") + " : " + QtyOrdered);
                if (QtyOrdered.compareTo(QtyAvailable) > 0) {
                    QtyOrdered = QtyAvailable;
                }
                m_runLine.setTotalQty(QtyOrdered);
                m_runLine.save();
                line += 10;
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, "doIt - " + (Object)sql, (Throwable)e);
            return false;
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return true;
    }
    
    public boolean generateDistributionDemand() {
        (this.m_run = new MDistributionRun(this.getCtx(), 0, (String)null)).setName("Generate from DRP " + this.p_DatePromised);
        this.m_run.save();
        final StringBuffer sql = new StringBuffer("SELECT M_Product_ID , SUM (TargetQty) AS MinQty, SUM (QtyOrdered-QtyDelivered) AS TotalQty FROM DD_OrderLine ol INNER JOIN M_Locator l ON (l.M_Locator_ID=ol.M_Locator_ID) INNER JOIN DD_Order o ON (o.DD_Order_ID=ol.DD_Order_ID) ");
        sql.append(" WHERE o.DocStatus IN ('DR','IN') AND ol.DatePromised <= ? AND l.M_Warehouse_ID=? GROUP BY M_Product_ID");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), this.get_TrxName());
            pstmt.setTimestamp(1, this.p_DatePromised);
            pstmt.setInt(2, this.p_M_Warehouse_ID);
            rs = pstmt.executeQuery();
            int line = 10;
            while (rs.next()) {
                final int M_Product_ID = rs.getInt("M_Product_ID");
                BigDecimal QtyAvailable = MStorageOnHand.getQtyOnHand(M_Product_ID, this.p_M_Warehouse_ID, 0, this.get_TrxName());
                if (QtyAvailable.signum() <= 0) {
                    continue;
                }
                BigDecimal QtyToDistribute = rs.getBigDecimal("TotalQty");
                if (QtyAvailable.compareTo(QtyToDistribute) >= 0) {
                    QtyAvailable = QtyToDistribute;
                }
                else {
                    final BigDecimal QtyReserved = this.getTargetQty(M_Product_ID);
                    QtyToDistribute = QtyAvailable.subtract(QtyReserved);
                }
                final MDistributionRunLine m_runLine = new MDistributionRunLine(this.getCtx(), 0, this.get_TrxName());
                m_runLine.setM_DistributionRun_ID(this.m_run.getM_DistributionRun_ID());
                m_runLine.setAD_Org_ID(this.p_AD_Org_ID);
                m_runLine.setM_DistributionList_ID(this.p_M_DistributionList_ID);
                m_runLine.setLine(line);
                m_runLine.setM_Product_ID(M_Product_ID);
                m_runLine.setDescription(String.valueOf(Msg.translate(this.getCtx(), "QtyAvailable")) + " : " + QtyAvailable + " " + Msg.translate(this.getCtx(), "QtyOrdered") + " : " + QtyToDistribute);
                m_runLine.setTotalQty(QtyToDistribute);
                m_runLine.saveEx();
                line += 10;
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, "doIt - " + (Object)sql, (Throwable)e);
            return false;
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return true;
    }
    
    private BigDecimal getTargetQty(final int M_Product_ID) {
        final StringBuffer sql = new StringBuffer("SELECT SUM (TargetQty)  FROM DD_OrderLine ol INNER JOIN M_Locator l ON (l.M_Locator_ID=ol.M_Locator_ID) INNER JOIN DD_Order o ON (o.DD_Order_ID=ol.DD_Order_ID) ");
        sql.append(" WHERE o.DocStatus IN ('DR','IN') AND ol.DatePromised <= ? AND l.M_Warehouse_ID=? AND ol.M_Product_ID=? GROUP BY M_Product_ID");
        final BigDecimal qty = DB.getSQLValueBD(this.get_TrxName(), sql.toString(), new Object[] { this.p_DatePromised, this.p_M_Warehouse_ID, M_Product_ID });
        if (qty == null) {
            return Env.ZERO;
        }
        return qty;
    }
    
    public boolean executeDistribution() throws Exception {
        int M_DocType_ID = 0;
        final MDocType[] doc = MDocType.getOfDocBaseType(this.getCtx(), "DOO");
        if (doc == null || doc.length == 0) {
            this.log.severe("Not found default document type for docbasetype DOO");
            throw new Exception(Msg.getMsg(this.getCtx(), "SequenceDocNotFound"), CLogger.retrieveException());
        }
        M_DocType_ID = doc[0].getC_DocType_ID();
        final String trxName = Trx.createTrxName("Run Distribution to DRP");
        Trx.get(trxName, true);
        int AD_Process_ID = 271;
        AD_Process_ID = MProcess.getProcess_ID("M_DistributionRun Create", this.get_TrxName());
        final MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, 0);
        if (!instance.save()) {
            throw new Exception(Msg.getMsg(this.getCtx(), "ProcessNoInstance"), CLogger.retrieveException());
        }
        final ProcessInfo pi = new ProcessInfo("M_DistributionRun Orders", AD_Process_ID);
        pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
        pi.setRecord_ID(this.m_run.getM_DistributionRun_ID());
        MPInstancePara ip = new MPInstancePara(instance, 10);
        ip.setParameter("C_DocType_ID", M_DocType_ID);
        if (!ip.save()) {
            final String msg = "No Parameter added";
            throw new Exception(msg, CLogger.retrieveException());
        }
        ip = new MPInstancePara(instance, 20);
        ip.setParameter("DatePromised", "");
        ip.setP_Date(this.p_DatePromised);
        if (!ip.save()) {
            final String msg = "No Parameter added";
            throw new Exception(msg, CLogger.retrieveException());
        }
        ip = new MPInstancePara(instance, 30);
        ip.setParameter("M_Warehouse_ID", this.p_M_Warehouse_ID);
        if (!ip.save()) {
            final String msg = "No Parameter added";
            throw new Exception(msg, CLogger.retrieveException());
        }
        ip = new MPInstancePara(instance, 40);
        ip.setParameter("ConsolidateDocument", this.p_ConsolidateDocument);
        if (!ip.save()) {
            final String msg = "No Parameter added";
            throw new Exception(msg, CLogger.retrieveException());
        }
        ip = new MPInstancePara(instance, 50);
        ip.setParameter("IsTest", this.p_IsTest);
        if (!ip.save()) {
            final String msg = "No Parameter added";
            throw new Exception(msg, CLogger.retrieveException());
        }
        ip = new MPInstancePara(instance, 60);
        ip.setParameter("M_DistributionList_ID", this.p_M_DistributionList_ID);
        if (!ip.save()) {
            final String msg = "No Parameter added";
            throw new Exception(msg, CLogger.retrieveException());
        }
        ip = new MPInstancePara(instance, 70);
        ip.setParameter("IsRequiredDRP", this.p_BasedInDamnd);
        if (!ip.save()) {
            final String msg = "No Parameter added";
            throw new Exception(msg, CLogger.retrieveException());
        }
        final MProcess worker = new MProcess(this.getCtx(), AD_Process_ID, this.get_TrxName());
        worker.processIt(pi, Trx.get(this.get_TrxName(), true));
        this.m_run.delete(true);
        return true;
    }
    
    public String groovy(final String A_TrxName, final Properties A_Ctx, final int P_M_Warehouse_ID, final int P_M_PriceList_Version_ID, final int P_M_DistributionList_ID) {
        final MPriceListVersion plv = new MPriceListVersion(A_Ctx, P_M_PriceList_Version_ID, A_TrxName);
        new MPriceList(A_Ctx, plv.getM_PriceList_ID(), A_TrxName);
        final MWarehouse w = new MWarehouse(A_Ctx, P_M_Warehouse_ID, A_TrxName);
        final MDistributionRun dr = new MDistributionRun(A_Ctx, 0, A_TrxName);
        dr.setName(plv.getName());
        dr.setIsActive(true);
        dr.setAD_Org_ID(w.getAD_Org_ID());
        dr.saveEx();
        final MProductPrice[] products = plv.getProductPrice(true);
        final int seq = 10;
        MProductPrice[] array;
        for (int length = (array = products).length, i = 0; i < length; ++i) {
            final MProductPrice pp = array[i];
            final int M_Product_ID = pp.getM_Product_ID();
            final BigDecimal QtyAvailable = MStorageOnHand.getQtyOnHand(M_Product_ID, this.p_M_Warehouse_ID, 0, this.get_TrxName());
            final BigDecimal QtyOnHand = MPPMRP.getQtyOnHand(A_Ctx, P_M_Warehouse_ID, M_Product_ID, A_TrxName);
            final MDistributionRunLine drl = new MDistributionRunLine(A_Ctx, 0, A_TrxName);
            drl.setM_DistributionRun_ID(dr.get_ID());
            drl.setLine(seq);
            drl.setM_Product_ID(M_Product_ID);
            drl.setM_DistributionList_ID(P_M_DistributionList_ID);
            drl.setDescription(String.valueOf(Msg.translate(A_Ctx, "QtyAvailable")) + " = " + QtyAvailable + " | " + Msg.translate(A_Ctx, "QtyOnHand") + " = " + QtyOnHand);
            drl.setTotalQty(QtyAvailable);
            drl.saveEx();
        }
        return "";
    }
    
    public String groovy1(final String A_TrxName, final Properties A_Ctx, final int P_M_Warehouse_ID, final int P_M_PriceList_Version_ID, final int P_M_DistributionList_ID) {
        final MDistributionRunLine main = new MDistributionRunLine(A_Ctx, 0, A_TrxName);
        final MProduct product = MProduct.get(A_Ctx, main.getM_Product_ID());
        final BigDecimal Qty = main.getTotalQty();
        int seq = main.getLine();
        int num = 1;
        if (product.isBOM() && Qty.signum() > 0) {
            ++seq;
            final MPPProductBOM bom = MPPProductBOM.getDefault(product, A_TrxName);
            MPPProductBOMLine[] lines;
            for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
                final MPPProductBOMLine line = lines[i];
                ++num;
                final int M_Product_ID = line.getM_Product_ID();
                final BigDecimal QtyRequired = line.getQtyBOM().multiply(Qty);
                final BigDecimal QtyAvailable = MStorageOnHand.getQtyOnHand(M_Product_ID, this.p_M_Warehouse_ID, 0, this.get_TrxName());
                final BigDecimal QtyOnHand = MPPMRP.getQtyOnHand(A_Ctx, P_M_Warehouse_ID, M_Product_ID, A_TrxName);
                BigDecimal QtyToDeliver = QtyRequired;
                if (QtyRequired.compareTo(QtyAvailable) > 0) {
                    QtyToDeliver = QtyAvailable;
                }
                final MDistributionRunLine drl = new MDistributionRunLine(A_Ctx, 0, A_TrxName);
                drl.setM_DistributionRun_ID(main.getM_DistributionRun_ID());
                drl.setLine(seq);
                drl.setM_Product_ID(M_Product_ID);
                drl.setM_DistributionList_ID(main.getM_DistributionList_ID());
                drl.setDescription(String.valueOf(Msg.translate(A_Ctx, "QtyRequired")) + " = " + QtyRequired.intValue() + " | " + Msg.translate(A_Ctx, "QtyAvailable") + " = " + QtyAvailable + " | " + Msg.translate(A_Ctx, "QtyOnHand") + " = " + QtyOnHand);
                drl.setTotalQty(QtyToDeliver);
                drl.saveEx();
            }
        }
        main.setIsActive(false);
        return "Componentes del Juego:" + num;
    }
}
