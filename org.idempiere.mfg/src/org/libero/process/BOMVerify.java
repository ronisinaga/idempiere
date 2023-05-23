// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.util.Iterator;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.eevolution.model.MPPProductBOMLine;
import org.eevolution.model.MPPProductBOM;
import org.compiere.model.Query;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.model.MProduct;
import java.util.ArrayList;
import org.compiere.process.SvrProcess;

public class BOMVerify extends SvrProcess
{
    private int p_M_Product_ID;
    private int p_M_Product_Category_ID;
    private boolean p_IsReValidate;
    private boolean p_fromButton;
    private ArrayList<MProduct> foundproducts;
    private ArrayList<MProduct> validproducts;
    private ArrayList<MProduct> invalidproducts;
    private ArrayList<MProduct> containinvalidproducts;
    private ArrayList<MProduct> checkedproducts;
    
    public BOMVerify() {
        this.p_M_Product_ID = 0;
        this.p_M_Product_Category_ID = 0;
        this.p_IsReValidate = false;
        this.p_fromButton = false;
        this.foundproducts = new ArrayList<MProduct>();
        this.validproducts = new ArrayList<MProduct>();
        this.invalidproducts = new ArrayList<MProduct>();
        this.containinvalidproducts = new ArrayList<MProduct>();
        this.checkedproducts = new ArrayList<MProduct>();
    }
    
    protected void prepare() {
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("M_Product_ID")) {
                    this.p_M_Product_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("M_Product_Category_ID")) {
                    this.p_M_Product_Category_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("IsReValidate")) {
                    this.p_IsReValidate = "Y".equals(para[i].getParameter());
                }
                else {
                    this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
                }
            }
        }
        if (this.p_M_Product_ID == 0) {
            this.p_M_Product_ID = this.getRecord_ID();
        }
        this.p_fromButton = (this.getRecord_ID() > 0);
    }
    
    protected String doIt() throws Exception {
        if (this.p_M_Product_ID != 0) {
            if (this.log.isLoggable(Level.INFO)) {
                this.log.info("M_Product_ID=" + this.p_M_Product_ID);
            }
            this.checkProduct(new MProduct(this.getCtx(), this.p_M_Product_ID, this.get_TrxName()));
            return "Product BOM [based libero] Checked ";
        }
        if (this.log.isLoggable(Level.INFO)) {
            this.log.info("M_Product_Category_ID=" + this.p_M_Product_Category_ID + ", IsReValidate=" + this.p_IsReValidate);
        }
        int counter = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT M_Product_ID FROM M_Product WHERE IsBOM='Y' AND ";
        if (this.p_M_Product_Category_ID == 0) {
            sql = String.valueOf(sql) + "AD_Client_ID=? ";
        }
        else {
            sql = String.valueOf(sql) + "M_Product_Category_ID=? ";
        }
        if (!this.p_IsReValidate) {
            sql = String.valueOf(sql) + "AND IsVerified<>'Y' ";
        }
        sql = String.valueOf(sql) + "ORDER BY Name";
        final int AD_Client_ID = Env.getAD_Client_ID(this.getCtx());
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
            if (this.p_M_Product_Category_ID == 0) {
                pstmt.setInt(1, AD_Client_ID);
            }
            else {
                pstmt.setInt(1, this.p_M_Product_Category_ID);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                this.p_M_Product_ID = rs.getInt(1);
                this.checkProduct(new MProduct(this.getCtx(), this.p_M_Product_ID, this.get_TrxName()));
                ++counter;
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return "#" + counter;
    }
    
    private void checkProduct(final MProduct product) {
        if (product.isBOM() && !this.checkedproducts.contains(product)) {
            this.validateProduct(product);
        }
    }
    
    private boolean validateProduct(final MProduct product) {
        if (!product.isBOM()) {
            return false;
        }
        Env.getContextAsDate(this.getCtx(), "#Date");
        if (this.log.isLoggable(Level.CONFIG)) {
            this.log.config(product.getName());
        }
        this.foundproducts.add(product);
        final List<MPPProductBOMLine> productsBOMLines = new Query(product.getCtx(), "PP_Product_BOMLine", "PP_Product_BOM_ID=?", product.get_TrxName()).setParameters(new Object[] { MPPProductBOM.getBOMSearchKey(product) }).list();
        boolean containsinvalid = false;
        boolean invalid = false;
        int lines = 0;
        for (final MPPProductBOMLine productsBOMLine : productsBOMLines) {
            if (!productsBOMLine.isActive()) {
                continue;
            }
            ++lines;
            final MProduct pp = new MProduct(this.getCtx(), productsBOMLine.getM_Product_ID(), this.get_TrxName());
            if (!pp.isBOM()) {
                if (!this.log.isLoggable(Level.FINER)) {
                    continue;
                }
                this.log.finer(pp.getName());
            }
            else {
                this.validproducts.contains(pp);
                if (this.invalidproducts.contains(pp)) {
                    containsinvalid = true;
                }
                else if (this.foundproducts.contains(pp)) {
                    invalid = true;
                    if (this.p_fromButton) {
                        this.addLog(0, (Timestamp)null, (BigDecimal)null, String.valueOf(product.getValue()) + " recursively contains " + pp.getValue());
                    }
                    else {
                        this.addBufferLog(0, (Timestamp)null, (BigDecimal)null, String.valueOf(product.getValue()) + " recursively contains " + pp.getValue(), 208, product.getM_Product_ID());
                    }
                }
                else {
                    if (this.validateProduct(pp)) {
                        continue;
                    }
                    containsinvalid = true;
                }
            }
        }
        if (lines == 0) {
            invalid = true;
            if (this.p_fromButton) {
                this.addLog(0, (Timestamp)null, (BigDecimal)null, String.valueOf(product.getValue()) + " does not have lines");
            }
            else {
                this.addBufferLog(0, (Timestamp)null, (BigDecimal)null, String.valueOf(product.getValue()) + " does not have lines", 208, product.getM_Product_ID());
            }
        }
        this.checkedproducts.add(product);
        this.foundproducts.remove(product);
        if (invalid) {
            this.invalidproducts.add(product);
            product.setIsVerified(false);
            product.saveEx();
            return false;
        }
        if (containsinvalid) {
            this.containinvalidproducts.add(product);
            product.setIsVerified(false);
            product.saveEx();
            return false;
        }
        this.validproducts.add(product);
        product.setIsVerified(true);
        product.saveEx();
        return true;
    }
}
