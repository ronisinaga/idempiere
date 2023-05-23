// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.util.List;
import org.compiere.model.Query;
import org.eevolution.model.MPPProductBOM;
import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.X_PP_Order_BOM;

public class MPPOrderBOM extends X_PP_Order_BOM
{
    private static final long serialVersionUID = 1L;
    
    public MPPOrderBOM(final Properties ctx, final int PP_Order_BOM_ID, final String trxName) {
        super(ctx, PP_Order_BOM_ID, trxName);
        if (PP_Order_BOM_ID == 0) {
            this.setProcessing(false);
        }
    }
    
    public MPPOrderBOM(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    public MPPOrderBOM(final MPPProductBOM bom, final int PP_Order_ID, final String trxName) {
        this(bom.getCtx(), 0, trxName);
        this.setBOMType(bom.getBOMType());
        this.setBOMUse(bom.getBOMUse());
        this.setM_ChangeNotice_ID(bom.getM_ChangeNotice_ID());
        this.setHelp(bom.getHelp());
        this.setProcessing(bom.isProcessing());
        this.setHelp(bom.getHelp());
        this.setDescription(bom.getDescription());
        this.setM_AttributeSetInstance_ID(bom.getM_AttributeSetInstance_ID());
        this.setM_Product_ID(bom.getM_Product_ID());
        this.setName(bom.getName());
        this.setRevision(bom.getRevision());
        this.setValidFrom(bom.getValidFrom());
        this.setValidTo(bom.getValidTo());
        this.setValue(bom.getValue());
        this.setDocumentNo(bom.get_Value("DocumentNo").toString());
        this.setC_UOM_ID(bom.getC_UOM_ID());
        this.setPP_Order_ID(PP_Order_ID);
    }
    
    public MPPOrderBOMLine[] getLines() {
        final String whereClause = "PP_Order_BOM_ID=?";
        final List<MPPOrderBOMLine> list = new Query(this.getCtx(), "PP_Order_BOMLine", whereClause, this.get_TrxName()).setParameters(new Object[] { this.get_ID() }).list();
        return list.toArray(new MPPOrderBOMLine[list.size()]);
    }
    
    protected boolean beforeDelete() {
        MPPOrderBOMLine[] lines;
        for (int length = (lines = this.getLines()).length, i = 0; i < length; ++i) {
            final MPPOrderBOMLine line = lines[i];
            line.deleteEx(false);
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MPPOrderBOM[").append(this.get_ID()).append("-").append(this.getDocumentNo()).append("]");
        return sb.toString();
    }
}
