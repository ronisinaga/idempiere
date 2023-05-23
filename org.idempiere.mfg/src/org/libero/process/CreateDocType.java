// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.MDocType;
import org.compiere.model.MSequence;
import java.util.logging.Level;
import org.compiere.model.MGLCategory;
import org.compiere.util.Env;
import org.compiere.process.SvrProcess;

public class CreateDocType extends SvrProcess
{
    private int AD_Client_ID;
    private String trxname;
    
    public CreateDocType() {
        this.AD_Client_ID = 0;
        this.trxname = null;
    }
    
    protected void prepare() {
        System.out.println("In AddLiberoRecords prepare");
        this.log.fine("In AddLiberoRecords prepare");
        this.AD_Client_ID = Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));
        this.getParameter();
    }
    
    protected String doIt() throws Exception {
        System.out.println("In AddLiberoRecords doIt");
        this.log.fine("In AddLiberoRecords doIt");
        Env.getCtx();
        this.trxname = this.get_TrxName();
        final int GL_Manufacturing = this.createGLCategory("Manufactuing", "D", false);
        final int GL_Distribution = this.createGLCategory("Distribution", "D", false);
        this.createDocType("Manufacturing Order", "Manufacturing Order", "MOP", null, 0, 0, 80000, GL_Manufacturing);
        this.createDocType("Manufacturing Cost Collector", "Cost Collector", "MCC", null, 0, 0, 81000, GL_Manufacturing);
        this.createDocType("Maintenance Order", "Maintenance Order", "MOF", null, 0, 0, 86000, GL_Manufacturing);
        this.createDocType("Quality Order", "Quality Order", "MQO", null, 0, 0, 87000, GL_Manufacturing);
        this.createDocType("Distribution Order", "Distribution Orde", "DOO", null, 0, 0, 88000, GL_Distribution);
        return "ok";
    }
    
    private int createGLCategory(final String Name, final String CategoryType, final boolean isDefault) {
        final MGLCategory cat = new MGLCategory(Env.getCtx(), 0, this.trxname);
        cat.setName(Name);
        cat.setCategoryType(CategoryType);
        cat.setIsDefault(isDefault);
        if (!cat.save()) {
            this.log.log(Level.SEVERE, "GL Category NOT created - " + Name);
            return 0;
        }
        return cat.getGL_Category_ID();
    }
    
    private int createDocType(final String Name, final String PrintName, final String DocBaseType, final String DocSubTypeSO, final int C_DocTypeShipment_ID, final int C_DocTypeInvoice_ID, final int StartNo, final int GL_Category_ID) {
        this.log.fine("In createDocType");
        this.log.fine("docBaseType: " + DocBaseType);
        this.log.fine("GL_Category_ID: " + GL_Category_ID);
        MSequence sequence = null;
        if (StartNo != 0) {
            sequence = new MSequence(Env.getCtx(), this.getAD_Client_ID(), Name, StartNo, this.trxname);
            if (!sequence.save()) {
                this.log.log(Level.SEVERE, "Sequence NOT created - " + Name);
                return 0;
            }
        }
        final MDocType dt = new MDocType(Env.getCtx(), 0, this.trxname);
        dt.setAD_Org_ID(0);
        dt.set_CustomColumn("DocBaseType", (Object)DocBaseType);
        dt.setName(Name);
        dt.setPrintName(Name);
        if (DocSubTypeSO != null) {
            dt.setDocSubTypeSO(DocSubTypeSO);
        }
        if (C_DocTypeShipment_ID != 0) {
            dt.setC_DocTypeShipment_ID(C_DocTypeShipment_ID);
        }
        if (C_DocTypeInvoice_ID != 0) {
            dt.setC_DocTypeInvoice_ID(C_DocTypeInvoice_ID);
        }
        if (GL_Category_ID != 0) {
            dt.setGL_Category_ID(GL_Category_ID);
        }
        if (sequence == null) {
            dt.setIsDocNoControlled(false);
        }
        else {
            dt.setIsDocNoControlled(true);
            dt.setDocNoSequence_ID(sequence.getAD_Sequence_ID());
        }
        dt.setIsSOTrx(false);
        if (!dt.save()) {
            this.log.log(Level.SEVERE, "DocType NOT created - " + Name);
            return 0;
        }
        return dt.getC_DocType_ID();
    }
}
