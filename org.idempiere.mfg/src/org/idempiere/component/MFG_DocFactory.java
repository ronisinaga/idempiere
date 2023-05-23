// 
// Decompiled by Procyon v0.5.36
// 

package org.idempiere.component;

import org.compiere.acct.Doc_PPCostCollector;
import org.compiere.acct.Doc_DDOrder;
import org.compiere.acct.Doc_PPOrder;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Level;
import org.compiere.util.DB;
import org.compiere.model.MTable;
import org.compiere.util.Env;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.util.CLogger;
import org.adempiere.base.IDocFactory;

public class MFG_DocFactory implements IDocFactory
{
    private static final CLogger s_log;
    
    static {
        s_log = CLogger.getCLogger((Class)MFG_DocFactory.class);
    }
    
    public Doc getDocument(final MAcctSchema as, final int AD_Table_ID, final int Record_ID, final String trxName) {
        final String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
        if (!tableName.equals("PP_Order") || !tableName.equals("DD_Order") || !tableName.equals("PP_Cost_Collector")) {
            return null;
        }
        Doc doc = null;
        final StringBuffer sql = new StringBuffer("SELECT * FROM ").append(tableName).append(" WHERE ").append(tableName).append("_ID=? AND Processed='Y'");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), trxName);
            pstmt.setInt(1, Record_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                doc = this.getDocument(as, AD_Table_ID, rs, trxName);
            }
            else {
                MFG_DocFactory.s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
            }
        }
        catch (Exception e) {
            MFG_DocFactory.s_log.log(Level.SEVERE, sql.toString(), (Throwable)e);
            return doc;
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return doc;
    }
    
    public Doc getDocument(final MAcctSchema as, final int AD_Table_ID, final ResultSet rs, final String trxName) {
        final String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
        if (tableName.equals("PP_Order")) {
            return new Doc_PPOrder(as, rs, trxName);
        }
        if (tableName.equals("DD_Order")) {
            return new Doc_DDOrder(as, rs, trxName);
        }
        if (tableName.equals("PP_Cost_Collector")) {
            return new Doc_PPCostCollector(as, rs, trxName);
        }
        return null;
    }
}
