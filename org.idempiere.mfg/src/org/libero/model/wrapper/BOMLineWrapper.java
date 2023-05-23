// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model.wrapper;

import java.sql.Timestamp;
import java.math.BigDecimal;
import org.libero.model.MPPOrderBOMLine;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.model.PO;
import java.util.Properties;

public class BOMLineWrapper extends AbstractPOWrapper
{
    public static String tableName(final String type) {
        if ("productBOM".equals(type)) {
            return "PP_Product_BOMLine";
        }
        if ("orderBOM".equals(type)) {
            return "PP_Order_BOMLine";
        }
        return "";
    }
    
    public static String idColumn(final String type) {
        return String.valueOf(tableName(type)) + "_ID";
    }
    
    public BOMLineWrapper(final Properties ctx, final int id, final String trxName, final String type) {
        super(ctx, id, trxName, type);
    }
    
    @Override
    protected PO receivePO(final Properties ctx, final int id, final String trxName, final String type) {
        PO po = null;
        if ("productBOM".equals(type)) {
            po = (PO)new MPPProductBOMLine(ctx, id, trxName);
        }
        else if ("orderBOM".equals(type)) {
            po = new MPPOrderBOMLine(ctx, id, trxName);
        }
        return po;
    }
    
    public String getComponentType() {
        String type = null;
        if (this.get() instanceof MPPProductBOMLine) {
            type = ((MPPProductBOMLine)this.get()).getComponentType();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            type = ((MPPOrderBOMLine)this.get()).getComponentType();
        }
        return type;
    }
    
    public BigDecimal getAssay() {
        BigDecimal assay = null;
        if (this.get() instanceof MPPProductBOMLine) {
            assay = ((MPPProductBOMLine)this.get()).getAssay();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            assay = ((MPPOrderBOMLine)this.get()).getAssay();
        }
        return assay;
    }
    
    public int getM_ChangeNotice_ID() {
        int M_ChangeNotice_ID = 0;
        if (this.get() instanceof MPPProductBOMLine) {
            M_ChangeNotice_ID = ((MPPProductBOMLine)this.get()).getM_ChangeNotice_ID();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            M_ChangeNotice_ID = ((MPPOrderBOMLine)this.get()).getM_ChangeNotice_ID();
        }
        return M_ChangeNotice_ID;
    }
    
    public String getHelp() {
        String Help = null;
        if (this.get() instanceof MPPProductBOMLine) {
            Help = ((MPPProductBOMLine)this.get()).getHelp();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            Help = ((MPPOrderBOMLine)this.get()).getHelp();
        }
        return Help;
    }
    
    public BigDecimal getQtyBatch() {
        BigDecimal qty = null;
        if (this.get() instanceof MPPProductBOMLine) {
            qty = ((MPPProductBOMLine)this.get()).getQtyBatch();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            qty = ((MPPOrderBOMLine)this.get()).getQtyBatch();
        }
        return qty;
    }
    
    public BigDecimal getForecast() {
        BigDecimal fc = null;
        if (this.get() instanceof MPPProductBOMLine) {
            fc = ((MPPProductBOMLine)this.get()).getForecast();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            fc = ((MPPOrderBOMLine)this.get()).getForecast();
        }
        return fc;
    }
    
    public Integer getLeadTimeOffset() {
        Integer offset = null;
        if (this.get() instanceof MPPProductBOMLine) {
            offset = ((MPPProductBOMLine)this.get()).getLeadTimeOffset();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            offset = ((MPPOrderBOMLine)this.get()).getLeadTimeOffset();
        }
        return offset;
    }
    
    public boolean isQtyPercentage() {
        boolean percentage = false;
        if (this.get() instanceof MPPProductBOMLine) {
            percentage = ((MPPProductBOMLine)this.get()).isQtyPercentage();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            percentage = ((MPPOrderBOMLine)this.get()).isQtyPercentage();
        }
        return percentage;
    }
    
    public boolean isCritical() {
        boolean critical = false;
        if (this.get() instanceof MPPProductBOMLine) {
            critical = ((MPPProductBOMLine)this.get()).isCritical();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            critical = ((MPPOrderBOMLine)this.get()).isCritical();
        }
        return critical;
    }
    
    public String getIssueMethod() {
        String issue = null;
        if (this.get() instanceof MPPProductBOMLine) {
            issue = ((MPPProductBOMLine)this.get()).getIssueMethod();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            issue = ((MPPOrderBOMLine)this.get()).getIssueMethod();
        }
        return issue;
    }
    
    public int getLine() {
        int line = 0;
        if (this.get() instanceof MPPProductBOMLine) {
            line = ((MPPProductBOMLine)this.get()).getLine();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            line = ((MPPOrderBOMLine)this.get()).getLine();
        }
        return line;
    }
    
    public String getDescription() {
        String type = null;
        if (this.get() instanceof MPPProductBOMLine) {
            type = ((MPPProductBOMLine)this.get()).getDescription();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            type = ((MPPOrderBOMLine)this.get()).getDescription();
        }
        return type;
    }
    
    public int getM_Product_ID() {
        int id = 0;
        if (this.get() instanceof MPPProductBOMLine) {
            id = ((MPPProductBOMLine)this.get()).getM_Product_ID();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            id = ((MPPOrderBOMLine)this.get()).getM_Product_ID();
        }
        return id;
    }
    
    public int getPP_Order_ID() {
        int id = 0;
        if (this.get() instanceof MPPOrderBOMLine) {
            final MPPOrderBOMLine line = (MPPOrderBOMLine)this.get();
            id = line.getPP_Order_ID();
        }
        return id;
    }
    
    public int getPP_BOM_ID() {
        int id = 0;
        if (this.get() instanceof MPPProductBOMLine) {
            id = ((MPPProductBOMLine)this.get()).getPP_Product_BOM_ID();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            id = ((MPPOrderBOMLine)this.get()).getPP_Order_BOM_ID();
        }
        return id;
    }
    
    public int getM_AttributeSetInstance_ID() {
        int id = 0;
        if (this.get() instanceof MPPProductBOMLine) {
            id = ((MPPProductBOMLine)this.get()).getM_AttributeSetInstance_ID();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            id = ((MPPOrderBOMLine)this.get()).getM_AttributeSetInstance_ID();
        }
        return id;
    }
    
    public void setM_AttributeSetInstance_ID(final int id) {
        if (this.get() instanceof MPPProductBOMLine) {
            ((MPPProductBOMLine)this.get()).setM_AttributeSetInstance_ID(id);
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            ((MPPOrderBOMLine)this.get()).setM_AttributeSetInstance_ID(id);
        }
    }
    
    public void setQtyBOM(final BigDecimal qty) {
        if (this.get() instanceof MPPProductBOMLine) {
            ((MPPProductBOMLine)this.get()).setQtyBOM(qty);
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            ((MPPOrderBOMLine)this.get()).setQtyBOM(qty);
        }
    }
    
    public BigDecimal getQtyBOM() {
        BigDecimal value = null;
        if (this.get() instanceof MPPProductBOMLine) {
            value = ((MPPProductBOMLine)this.get()).getQtyBOM();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            value = ((MPPOrderBOMLine)this.get()).getQtyBOM();
        }
        return value;
    }
    
    public int getC_UOM_ID() {
        int value = 0;
        if (this.get() instanceof MPPProductBOMLine) {
            value = ((MPPProductBOMLine)this.get()).getC_UOM_ID();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            value = ((MPPOrderBOMLine)this.get()).getC_UOM_ID();
        }
        return value;
    }
    
    public int getPo() {
        int value = 0;
        if (this.get() instanceof MPPProductBOMLine) {
            value = ((MPPProductBOMLine)this.get()).getLine();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            value = ((MPPOrderBOMLine)this.get()).getLine();
        }
        return value;
    }
    
    public BigDecimal getScrap() {
        BigDecimal value = new BigDecimal(0);
        if (this.get() instanceof MPPProductBOMLine) {
            value = ((MPPProductBOMLine)this.get()).getScrap();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            value = ((MPPOrderBOMLine)this.get()).getScrap();
        }
        return value;
    }
    
    public Timestamp getValidFrom() {
        Timestamp value = null;
        if (this.get() instanceof MPPProductBOMLine) {
            value = ((MPPProductBOMLine)this.get()).getValidFrom();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            value = ((MPPOrderBOMLine)this.get()).getValidFrom();
        }
        return value;
    }
    
    public Timestamp getValidTo() {
        Timestamp value = null;
        if (this.get() instanceof MPPProductBOMLine) {
            value = ((MPPProductBOMLine)this.get()).getValidTo();
        }
        else if (this.get() instanceof MPPOrderBOMLine) {
            value = ((MPPOrderBOMLine)this.get()).getValidTo();
        }
        return value;
    }
}
