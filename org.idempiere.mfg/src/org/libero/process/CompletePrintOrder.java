// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.PrintInfo;
import org.compiere.model.MQuery;
import org.compiere.print.MPrintFormat;
import org.compiere.model.MTable;
import org.compiere.print.ReportEngine;
import org.compiere.print.ReportCtl;
import org.adempiere.exceptions.AdempiereException;
import org.libero.model.MPPOrder;
import org.adempiere.exceptions.FillMandatoryException;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import org.compiere.process.ClientProcess;
import org.compiere.process.SvrProcess;

public class CompletePrintOrder extends SvrProcess implements ClientProcess
{
    private int p_PP_Order_ID;
    private boolean p_IsPrintPickList;
    private boolean p_IsPrintWorkflow;
    private boolean p_IsPrintPackList;
    private boolean p_IsComplete;
    
    public CompletePrintOrder() {
        this.p_PP_Order_ID = 0;
        this.p_IsPrintPickList = false;
        this.p_IsPrintWorkflow = false;
        this.p_IsPrintPackList = false;
        this.p_IsComplete = false;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals("PP_Order_ID")) {
                    this.p_PP_Order_ID = para.getParameterAsInt();
                }
                else if (name.equals("IsPrintPickList")) {
                    this.p_IsPrintPickList = para.getParameterAsBoolean();
                }
                else if (name.equals("IsPrintWorkflow")) {
                    this.p_IsPrintWorkflow = para.getParameterAsBoolean();
                }
                else if (name.equals("IsPrintPackingList")) {
                    this.p_IsPrintPackList = para.getParameterAsBoolean();
                }
                else if (name.equals("IsComplete")) {
                    this.p_IsComplete = para.getParameterAsBoolean();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        if (this.p_PP_Order_ID == 0) {
            throw new FillMandatoryException(new String[] { "PP_Order_ID" });
        }
        if (this.p_IsComplete) {
            final MPPOrder order = new MPPOrder(this.getCtx(), this.p_PP_Order_ID, this.get_TrxName());
            if (!order.isAvailable()) {
                throw new AdempiereException("@NoQtyAvailable@");
            }
            final boolean ok = order.processIt("CO");
            order.saveEx();
            if (!ok) {
                throw new AdempiereException(order.getProcessMsg());
            }
            if (!"CO".equals(order.getDocStatus())) {
                throw new AdempiereException(order.getProcessMsg());
            }
        }
        if (this.p_IsPrintPickList) {
            final ReportEngine re = this.getReportEngine("Manufacturing_Order_BOM_Header ** TEMPLATE **", "PP_Order_BOM_Header_v");
            if (re == null) {
                return "";
            }
            ReportCtl.preview(re);
            re.print();
        }
        if (this.p_IsPrintPackList) {
            final ReportEngine re = this.getReportEngine("Manufacturing_Order_BOM_Header_Packing ** TEMPLATE **", "PP_Order_BOM_Header_v");
            if (re == null) {
                return "";
            }
            ReportCtl.preview(re);
            re.print();
        }
        if (this.p_IsPrintWorkflow) {
            final ReportEngine re = this.getReportEngine("Manufacturing_Order_Workflow_Header ** TEMPLATE **", "PP_Order_Workflow_Header_v");
            if (re == null) {
                return "";
            }
            ReportCtl.preview(re);
            re.print();
        }
        return "@OK@";
    }
    
    private ReportEngine getReportEngine(final String formatName, final String tableName) {
        final int format_id = MPrintFormat.getPrintFormat_ID(formatName, MTable.getTable_ID(tableName), this.getAD_Client_ID());
        final MPrintFormat format = MPrintFormat.get(this.getCtx(), format_id, true);
        if (format == null) {
            this.addLog("@NotFound@ @AD_PrintFormat_ID@");
            return null;
        }
        final MQuery query = new MQuery(tableName);
        query.addRestriction("PP_Order_ID", "=", this.p_PP_Order_ID);
        final PrintInfo info = new PrintInfo(tableName, MTable.getTable_ID(tableName), this.p_PP_Order_ID);
        final ReportEngine re = new ReportEngine(this.getCtx(), format, query, info);
        return re;
    }
}
