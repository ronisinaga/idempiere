// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.Adempiere;
import java.util.ArrayList;
import java.math.BigDecimal;
import org.compiere.model.MPayment;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import org.compiere.model.MCashLine;
import org.compiere.util.Trx;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.CLogger;
import java.util.Properties;
import org.compiere.process.SvrProcess;

public class FixPaymentCashLine extends SvrProcess
{
    private static final Properties ctx;
    private static CLogger s_log;
    
    static {
        ctx = Env.getCtx();
        FixPaymentCashLine.s_log = CLogger.getCLogger((Class)FixPaymentCashLine.class);
    }
    
    protected void prepare() {
        this.getParameter();
    }
    
    protected String doIt() throws Exception {
        final String sql = "SELECT cl.C_CashLine_ID, c.Name FROM C_CashLine cl INNER JOIN C_Cash c ON (c.C_Cash_ID=cl.C_Cash_ID) WHERE cl.CashType='T'";
        PreparedStatement pstmt = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
            final ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                final Trx trx = Trx.get(Trx.createTrxName(), true);
                final MCashLine cashline = new MCashLine(Env.getCtx(), rs.getInt(1), trx.getTrxName());
                cashline.getC_CashLine_ID();
                final MPayment[] payments = getOfCash(Env.getCtx(), rs.getString(2), cashline.getAmount(), cashline.getC_BankAccount_ID(), cashline.getAD_Client_ID(), trx.getTrxName());
                final MPayment[] array;
                if ((array = payments).length != 0) {
                    final MPayment payment = array[0];
                    cashline.setC_Payment_ID(payment.getC_Payment_ID());
                    if (!cashline.save()) {
                        throw new IllegalStateException("Cannot assign payment to Cash Line");
                    }
                }
                trx.commit();
            }
            rs.close();
            pstmt.close();
            pstmt = null;
        }
        catch (Exception e) {
            FixPaymentCashLine.s_log.log(Level.SEVERE, sql, (Throwable)e);
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
        return "@ProcessOK@";
    }
    
    public static MPayment[] getOfCash(final Properties ctx, final String cashName, final BigDecimal amt, final int C_BankAccount_ID, final int AD_Client_ID, final String trxName) {
        final String sql = "SELECT * FROM C_Payment p WHERE p.DocumentNo=? AND R_PnRef=? AND PayAmt=? AND C_BankAccount_ID=? AND AD_Client_ID=?  AND TrxType='X' AND TenderType='X'";
        final ArrayList<MPayment> list = new ArrayList<MPayment>();
        PreparedStatement pstmt = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql, trxName);
            pstmt.setString(1, cashName);
            pstmt.setString(2, cashName);
            pstmt.setBigDecimal(3, amt.negate());
            pstmt.setInt(4, C_BankAccount_ID);
            pstmt.setInt(5, AD_Client_ID);
            final ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new MPayment(ctx, rs, trxName));
            }
            rs.close();
            pstmt.close();
            pstmt = null;
        }
        catch (Exception e) {
            FixPaymentCashLine.s_log.log(Level.SEVERE, sql, (Throwable)e);
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
        final MPayment[] retValue = new MPayment[list.size()];
        list.toArray(retValue);
        return retValue;
    }
    
    public static void main(final String[] args) {
        Adempiere.startup(true);
        Env.setContext(Env.getCtx(), "#AD_Client_ID", 11);
        final FixPaymentCashLine pcf = new FixPaymentCashLine();
        try {
            pcf.doIt();
        }
        catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
}
