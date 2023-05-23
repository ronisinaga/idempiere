// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.PO;
import java.util.Iterator;
import org.compiere.model.MRefList;
import org.adempiere.exceptions.AdempiereException;
import org.eevolution.model.MPPProductBOMLine;
import java.util.List;
import org.compiere.model.Query;
import java.util.ArrayList;
import org.adempiere.exceptions.FillMandatoryException;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class ComponentChange extends SvrProcess
{
    private static final int ACTION_AD_Reference_ID = 53227;
    private static final String ACTION_Add = "A";
    private static final String ACTION_Deactivate = "D";
    private static final String ACTION_Expire = "E";
    private static final String ACTION_Replace = "R";
    private static final String ACTION_ReplaceAndExpire = "RE";
    private int p_M_Product_ID;
    private Timestamp p_ValidTo;
    private Timestamp p_ValidFrom;
    private String p_Action;
    private int p_New_M_Product_ID;
    private BigDecimal p_Qty;
    private int p_M_ChangeNotice_ID;
    
    public ComponentChange() {
        this.p_M_Product_ID = 0;
        this.p_ValidTo = null;
        this.p_ValidFrom = null;
        this.p_New_M_Product_ID = 0;
        this.p_Qty = null;
        this.p_M_ChangeNotice_ID = 0;
    }
    
    protected void prepare() {
        int morepara = 0;
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("M_Product_ID") && morepara == 0) {
                    this.p_M_Product_ID = para.getParameterAsInt();
                    morepara = 1;
                }
                else if (name.equals("ValidTo")) {
                    this.p_ValidTo = (Timestamp)para.getParameter();
                }
                else if (name.equals("ValidFrom")) {
                    this.p_ValidFrom = (Timestamp)para.getParameter();
                }
                else if (name.equals("Action")) {
                    this.p_Action = (String)para.getParameter();
                }
                else if (name.equals("M_Product_ID")) {
                    this.p_New_M_Product_ID = para.getParameterAsInt();
                }
                else if (name.equals("Qty")) {
                    this.p_Qty = (BigDecimal)para.getParameter();
                }
                else if (name.equals("M_ChangeNotice_ID")) {
                    this.p_M_ChangeNotice_ID = para.getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        if (this.p_Action == null) {
            throw new FillMandatoryException(new String[] { "Action" });
        }
        final List<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer();
        whereClause.append("M_Product_ID=?");
        params.add(this.p_M_Product_ID);
        if (this.p_ValidTo != null) {
            whereClause.append(" AND TRUNC(ValidTo) <= ?");
            params.add(this.p_ValidTo);
        }
        if (this.p_ValidFrom != null) {
            whereClause.append(" AND TRUNC(ValidFrom) >= ?");
            params.add(this.p_ValidFrom);
        }
        final List<MPPProductBOMLine> components = new Query(this.getCtx(), "PP_Product_BOMLine", whereClause.toString(), this.get_TrxName()).setParameters((List)params).list();
        for (final MPPProductBOMLine bomline : components) {
            if (this.p_Action.equals("A")) {
                this.actionAdd(bomline, 0);
            }
            else if (this.p_Action.equals("D")) {
                this.actionDeactivate(bomline);
            }
            else if (this.p_Action.equals("E")) {
                this.actionExpire(bomline);
            }
            else if (this.p_Action.equals("R")) {
                this.actionAdd(bomline, bomline.getLine() + 1);
                this.actionDeactivate(bomline);
            }
            else {
                if (!this.p_Action.equals("RE")) {
                    throw new AdempiereException("Action not supported - " + this.p_Action);
                }
                this.actionAdd(bomline, bomline.getLine() + 1);
                this.actionExpire(bomline);
            }
            this.addLog(MRefList.getListName(this.getCtx(), 53227, this.p_Action));
        }
        return "@OK@";
    }
    
    protected void actionAdd(final MPPProductBOMLine bomline, final int line) {
        final MPPProductBOMLine newbomline = new MPPProductBOMLine(this.getCtx(), 0, this.get_TrxName());
        MPPProductBOMLine.copyValues((PO)bomline, (PO)newbomline);
        newbomline.setIsActive(true);
        newbomline.setLine(line);
        newbomline.setM_ChangeNotice_ID(this.p_M_ChangeNotice_ID);
        newbomline.setM_Product_ID(this.p_New_M_Product_ID);
        if (this.p_Qty.signum() != 0) {
            newbomline.setQtyBOM(this.p_Qty);
        }
        newbomline.setValidFrom(newbomline.getUpdated());
        newbomline.saveEx();
    }
    
    protected void actionDeactivate(final MPPProductBOMLine bomline) {
        bomline.setIsActive(false);
        bomline.setM_ChangeNotice_ID(this.p_M_ChangeNotice_ID);
        bomline.saveEx();
    }
    
    protected void actionExpire(final MPPProductBOMLine bomline) {
        bomline.setIsActive(true);
        bomline.setValidTo(bomline.getUpdated());
        bomline.setM_ChangeNotice_ID(this.p_M_ChangeNotice_ID);
        bomline.saveEx();
    }
}
