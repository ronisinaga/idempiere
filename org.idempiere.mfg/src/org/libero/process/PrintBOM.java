// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.print.ReportCtl;
import org.compiere.print.ReportEngine;
import org.compiere.model.PrintInfo;
import org.compiere.model.MQuery;
import org.compiere.print.MPrintFormat;
import org.compiere.model.MTable;
import org.compiere.util.ValueNamePair;
import org.compiere.util.CLogger;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import org.libero.tables.X_T_BOMLine;
import org.compiere.util.DB;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.math.BigDecimal;
import org.compiere.util.Env;
import java.util.Properties;
import org.compiere.process.SvrProcess;

public class PrintBOM extends SvrProcess
{
    private static final Properties ctx;
    private int p_M_Product_ID;
    private boolean p_implosion;
    private int LevelNo;
    private int SeqNo;
    private String levels;
    private int AD_PInstance_ID;
    private static final int X_RV_PP_Product_BOMLine_Table_ID = 53063;
    private static final String X_RV_PP_Product_BOMLine_Table_Name = "RV_PP_Product_BOMLine";
    
    static {
        ctx = Env.getCtx();
    }
    
    public PrintBOM() {
        this.p_M_Product_ID = 0;
        this.p_implosion = false;
        this.LevelNo = 1;
        this.SeqNo = 0;
        this.levels = new String("....................");
        this.AD_PInstance_ID = 0;
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("M_Product_ID")) {
                    this.p_M_Product_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("Implosion")) {
                    this.p_implosion = !((String)para[i].getParameter()).equals("N");
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        this.AD_PInstance_ID = this.getAD_PInstance_ID();
        try {
            this.loadBOM();
            this.print();
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, "PrintBOM", (Object)e.toString());
            throw new Exception(e.getLocalizedMessage());
        }
        finally {
            final String sql = "DELETE FROM T_BomLine WHERE AD_PInstance_ID = " + this.AD_PInstance_ID;
            DB.executeUpdate(sql, (String)null);
        }
        final String sql = "DELETE FROM T_BomLine WHERE AD_PInstance_ID = " + this.AD_PInstance_ID;
        DB.executeUpdate(sql, (String)null);
        return "@OK@";
    }
    
    private void loadBOM() throws Exception {
        int count = 0;
        if (this.p_M_Product_ID == 0) {
            this.raiseError("Error: ", "Product ID not found");
        }
        final X_T_BOMLine tboml = new X_T_BOMLine(PrintBOM.ctx, 0, null);
        tboml.setPP_Product_BOM_ID(0);
        tboml.setPP_Product_BOMLine_ID(0);
        tboml.setM_Product_ID(this.p_M_Product_ID);
        tboml.setSel_Product_ID(this.p_M_Product_ID);
        tboml.setImplosion(this.p_implosion);
        tboml.setLevelNo(0);
        tboml.setLevels("0");
        tboml.setSeqNo(0);
        tboml.setAD_PInstance_ID(this.AD_PInstance_ID);
        tboml.save();
        if (this.p_implosion) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            final String sql = "SELECT PP_Product_BOMLine_ID FROM PP_Product_BOMLine WHERE IsActive = 'Y' AND M_Product_ID = ? ";
            try {
                stmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
                stmt.setInt(1, this.p_M_Product_ID);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    this.parentImplotion(rs.getInt(1));
                    ++count;
                }
                if (count == 0) {
                    this.raiseError("Error: ", "Product is not a component");
                }
            }
            catch (SQLException e) {
                this.log.log(Level.SEVERE, String.valueOf(e.getLocalizedMessage()) + sql, (Throwable)e);
                throw new Exception("SQLException: " + e.getLocalizedMessage());
            }
            finally {
                DB.close(rs, (Statement)stmt);
                rs = null;
                stmt = null;
            }
            DB.close(rs, (Statement)stmt);
            rs = null;
            stmt = null;
        }
        else {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            final String sql = "SELECT PP_Product_BOM_ID FROM PP_Product_BOM WHERE IsActive = 'Y' AND M_Product_ID = ? ";
            try {
                stmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
                stmt.setInt(1, this.p_M_Product_ID);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    this.parentExplotion(rs.getInt(1));
                    ++count;
                }
                if (count == 0) {
                    this.raiseError("Error: ", "Product is not a BOM");
                }
            }
            catch (SQLException e) {
                this.log.log(Level.SEVERE, String.valueOf(e.getLocalizedMessage()) + sql, (Throwable)e);
                throw new Exception("SQLException: " + e.getLocalizedMessage());
            }
            finally {
                DB.close(rs, (Statement)stmt);
                rs = null;
                stmt = null;
            }
            DB.close(rs, (Statement)stmt);
            rs = null;
            stmt = null;
        }
    }
    
    public void parentImplotion(final int PP_Product_BOMLine_ID) throws Exception {
        int PP_Product_BOM_ID = 0;
        int M_Product_ID = 0;
        final X_T_BOMLine tboml = new X_T_BOMLine(PrintBOM.ctx, 0, null);
        PP_Product_BOM_ID = DB.getSQLValue((String)null, "SELECT PP_Product_BOM_ID FROM PP_Product_BOMLine WHERE PP_Product_BOMLine_ID=?", PP_Product_BOMLine_ID);
        if (PP_Product_BOM_ID < 0) {
            throw new Exception(CLogger.retrieveErrorString("Error: PrintBOM.parentImplotion()"));
        }
        M_Product_ID = DB.getSQLValue((String)null, "SELECT M_Product_ID FROM PP_Product_BOM WHERE PP_Product_BOM_ID=?", PP_Product_BOM_ID);
        if (M_Product_ID < 0) {
            throw new Exception(CLogger.retrieveErrorString("Error: PrintBOM.parentImplotion()"));
        }
        tboml.setPP_Product_BOM_ID(PP_Product_BOM_ID);
        tboml.setPP_Product_BOMLine_ID(PP_Product_BOMLine_ID);
        tboml.setM_Product_ID(M_Product_ID);
        tboml.setLevelNo(this.LevelNo);
        tboml.setSel_Product_ID(this.p_M_Product_ID);
        tboml.setImplosion(this.p_implosion);
        if (this.LevelNo >= 11) {
            tboml.setLevels(String.valueOf(this.levels) + ">" + this.LevelNo);
        }
        else if (this.LevelNo >= 1) {
            tboml.setLevels(String.valueOf(this.levels.substring(0, this.LevelNo)) + this.LevelNo);
        }
        tboml.setSeqNo(this.SeqNo);
        tboml.setAD_PInstance_ID(this.AD_PInstance_ID);
        tboml.save();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        final String sql = "SELECT PP_Product_BOM_ID, M_Product_ID FROM PP_Product_BOM WHERE IsActive = 'Y' AND M_Product_ID = ? ";
        try {
            stmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
            stmt.setInt(1, M_Product_ID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ++this.SeqNo;
                this.component(rs.getInt(2));
            }
        }
        catch (SQLException e) {
            this.log.log(Level.SEVERE, String.valueOf(e.getLocalizedMessage()) + sql, (Throwable)e);
            throw new Exception("SQLException: " + e.getLocalizedMessage());
        }
        finally {
            DB.close(rs, (Statement)stmt);
            rs = null;
            stmt = null;
        }
        DB.close(rs, (Statement)stmt);
        rs = null;
        stmt = null;
    }
    
    public void parentExplotion(final int PP_Product_BOM_ID) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        final String sql = "SELECT PP_Product_BOMLine_ID, M_Product_ID FROM PP_Product_BOMLine boml WHERE IsActive = 'Y' AND PP_Product_BOM_ID = ? ORDER BY Line ";
        try {
            stmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
            stmt.setInt(1, PP_Product_BOM_ID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ++this.SeqNo;
                final X_T_BOMLine tboml = new X_T_BOMLine(PrintBOM.ctx, 0, null);
                tboml.setPP_Product_BOM_ID(PP_Product_BOM_ID);
                tboml.setPP_Product_BOMLine_ID(rs.getInt(1));
                tboml.setM_Product_ID(rs.getInt(2));
                tboml.setLevelNo(this.LevelNo);
                tboml.setLevels(String.valueOf(this.levels.substring(0, this.LevelNo)) + this.LevelNo);
                tboml.setSeqNo(this.SeqNo);
                tboml.setAD_PInstance_ID(this.AD_PInstance_ID);
                tboml.setSel_Product_ID(this.p_M_Product_ID);
                tboml.setImplosion(this.p_implosion);
                tboml.save();
                this.component(rs.getInt(2));
            }
        }
        catch (SQLException e) {
            this.log.log(Level.SEVERE, String.valueOf(e.getLocalizedMessage()) + sql, (Throwable)e);
            throw new Exception("SQLException: " + e.getLocalizedMessage());
        }
        finally {
            DB.close(rs, (Statement)stmt);
            rs = null;
            stmt = null;
        }
        DB.close(rs, (Statement)stmt);
        rs = null;
        stmt = null;
    }
    
    public void component(final int M_Product_ID) throws Exception {
        if (this.p_implosion) {
            ++this.LevelNo;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            final String sql = "SELECT PP_Product_BOMLine_ID FROM PP_Product_BOMLine WHERE IsActive = 'Y' AND M_Product_ID = ? ";
            try {
                stmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
                stmt.setInt(1, M_Product_ID);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    this.parentImplotion(rs.getInt(1));
                }
                rs.close();
                stmt.close();
                --this.LevelNo;
                return;
            }
            catch (SQLException e) {
                this.log.log(Level.SEVERE, String.valueOf(e.getLocalizedMessage()) + sql, (Throwable)e);
                throw new Exception("SQLException: " + e.getLocalizedMessage());
            }
            finally {
                DB.close(rs, (Statement)stmt);
                rs = null;
                stmt = null;
            }
        }
        final String sql2 = "SELECT PP_Product_BOM_ID FROM PP_Product_BOM  WHERE IsActive = 'Y' AND Value = ? ";
        PreparedStatement stmt2 = null;
        ResultSet rs2 = null;
        try {
            final String Value = DB.getSQLValueString(this.get_TrxName(), "SELECT Value FROM M_PRODUCT WHERE M_PRODUCT_ID=?", M_Product_ID);
            if (Value == null) {
                throw new Exception(CLogger.retrieveErrorString("Error: PrintBOM.component()"));
            }
            stmt2 = (PreparedStatement)DB.prepareStatement(sql2, this.get_TrxName());
            stmt2.setString(1, Value);
            rs2 = stmt2.executeQuery();
            boolean level = false;
            while (rs2.next()) {
                if (!level) {
                    ++this.LevelNo;
                }
                level = true;
                this.parentExplotion(rs2.getInt(1));
                --this.LevelNo;
            }
        }
        catch (SQLException e) {
            this.log.log(Level.SEVERE, String.valueOf(e.getLocalizedMessage()) + sql2, (Throwable)e);
            throw new Exception("SQLException: " + e.getLocalizedMessage());
        }
        finally {
            DB.close(rs2, (Statement)stmt2);
            rs2 = null;
            stmt2 = null;
        }
        DB.close(rs2, (Statement)stmt2);
        rs2 = null;
        stmt2 = null;
    }
    
    private void raiseError(final String string, final String hint) throws Exception {
        String msg = string;
        final ValueNamePair pp = CLogger.retrieveError();
        if (pp != null) {
            msg = String.valueOf(pp.getName()) + " - ";
        }
        msg = String.valueOf(msg) + hint;
        throw new Exception(msg);
    }
    
    private void print() {
        final String formatName = "Multi Level BOM & Formula Detail";
        final String tableName = "RV_PP_Product_BOMLine";
        final int format_id = MPrintFormat.getPrintFormat_ID(formatName, MTable.getTable_ID(tableName), this.getAD_Client_ID());
        final MPrintFormat format = MPrintFormat.get(this.getCtx(), format_id, true);
        if (format == null) {
            this.addLog("@NotFound@ @AD_PrintFormat_ID@" + format_id);
        }
        final MQuery query = new MQuery(tableName);
        query.addRestriction("AD_PInstance_ID", "=", this.AD_PInstance_ID);
        final PrintInfo info = new PrintInfo("RV_PP_Product_BOMLine", 53063, this.getRecord_ID());
        final ReportEngine re = new ReportEngine(this.getCtx(), format, query, info);
        ReportCtl.preview(re);
        re.print();
    }
}
