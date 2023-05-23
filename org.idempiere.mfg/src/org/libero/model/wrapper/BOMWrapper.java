// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model.wrapper;

import org.libero.model.reasoner.StorageReasoner;
import java.sql.Timestamp;
import org.libero.model.MPPOrderBOM;
import org.eevolution.model.MPPProductBOM;
import org.compiere.model.PO;
import java.util.Properties;

public class BOMWrapper extends AbstractPOWrapper
{
    public static final String BOM_TYPE_PRODUCT = "productBOM";
    public static final String BOM_TYPE_ORDER = "orderBOM";
    
    public static String tableName(final String type) {
        if ("productBOM".equals(type)) {
            return "PP_Product_BOM";
        }
        if ("orderBOM".equals(type)) {
            return "PP_Order_BOM";
        }
        return "";
    }
    
    public static String idColumn(final String type) {
        final String value = null;
        if ("productBOM".equals(type)) {
            return "PP_Product_BOM";
        }
        if ("orderBOM".equals(type)) {
            return "PP_Order_BOM";
        }
        return String.valueOf(value) + "_ID";
    }
    
    public BOMWrapper(final Properties ctx, final int id, final String trxName, final String type) {
        super(ctx, id, trxName, type);
    }
    
    @Override
    protected PO receivePO(final Properties ctx, final int id, final String trxName, final String type) {
        PO po = null;
        if ("productBOM".equals(type)) {
            po = (PO)new MPPProductBOM(ctx, id, trxName);
        }
        else if ("orderBOM".equals(type)) {
            po = new MPPOrderBOM(ctx, id, trxName);
        }
        return po;
    }
    
    public String getName() {
        String name = null;
        if (this.get() instanceof MPPProductBOM) {
            name = ((MPPProductBOM)this.get()).getName();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            name = ((MPPOrderBOM)this.get()).getName();
        }
        return name;
    }
    
    public String getDescription() {
        String name = null;
        if (this.get() instanceof MPPProductBOM) {
            name = ((MPPProductBOM)this.get()).getDescription();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            name = ((MPPOrderBOM)this.get()).getDescription();
        }
        return name;
    }
    
    public String getRevision() {
        String name = null;
        if (this.get() instanceof MPPProductBOM) {
            name = ((MPPProductBOM)this.get()).getRevision();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            name = ((MPPOrderBOM)this.get()).getRevision();
        }
        return name;
    }
    
    public String getDocumentNo() {
        String value = null;
        if (this.get() instanceof MPPProductBOM) {
            value = ((MPPProductBOM)this.get()).get_Value("DocumentNo").toString();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            value = ((MPPOrderBOM)this.get()).getDocumentNo();
        }
        return value;
    }
    
    public int getM_Product_ID() {
        int id = 0;
        if (this.get() instanceof MPPProductBOM) {
            id = ((MPPProductBOM)this.get()).getM_Product_ID();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            id = ((MPPOrderBOM)this.get()).getM_Product_ID();
        }
        return id;
    }
    
    public int getM_AttributeSetInstance_ID() {
        int id = 0;
        if (this.get() instanceof MPPProductBOM) {
            id = ((MPPProductBOM)this.get()).getM_AttributeSetInstance_ID();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            id = ((MPPOrderBOM)this.get()).getM_AttributeSetInstance_ID();
        }
        return id;
    }
    
    public int getC_UOM_ID() {
        int id = 0;
        if (this.get() instanceof MPPProductBOM) {
            id = ((MPPProductBOM)this.get()).getC_UOM_ID();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            id = ((MPPOrderBOM)this.get()).getC_UOM_ID();
        }
        return id;
    }
    
    public Timestamp getValidFrom() {
        Timestamp value = null;
        if (this.get() instanceof MPPProductBOM) {
            value = ((MPPProductBOM)this.get()).getValidFrom();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            value = ((MPPOrderBOM)this.get()).getValidFrom();
        }
        return value;
    }
    
    public Timestamp getValidTo() {
        Timestamp value = null;
        if (this.get() instanceof MPPProductBOM) {
            value = ((MPPProductBOM)this.get()).getValidTo();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            value = ((MPPOrderBOM)this.get()).getValidTo();
        }
        return value;
    }
    
    public String getValue() {
        String value = null;
        if (this.get() instanceof MPPProductBOM) {
            value = ((MPPProductBOM)this.get()).getValue();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            value = ((MPPOrderBOM)this.get()).getValue();
        }
        return value;
    }
    
    public String getBOMType() {
        String value = null;
        if (this.get() instanceof MPPProductBOM) {
            value = ((MPPProductBOM)this.get()).getBOMType();
        }
        else if (this.get() instanceof MPPOrderBOM) {
            value = ((MPPOrderBOM)this.get()).getBOMType();
        }
        return value;
    }
    
    public int getPP_Order_ID() {
        int id = 0;
        if (this.get() instanceof MPPOrderBOM) {
            final MPPOrderBOM bom = (MPPOrderBOM)this.get();
            id = bom.getPP_Order_ID();
        }
        return id;
    }
    
    public BOMLineWrapper[] getLines() {
        int[] ids = null;
        String type = null;
        if (this.get() instanceof MPPProductBOM) {
            type = "productBOM";
        }
        else if (this.get() instanceof MPPOrderBOM) {
            type = "orderBOM";
        }
        final StorageReasoner mr = new StorageReasoner();
        ids = mr.getPOIDs(BOMLineWrapper.tableName(type), String.valueOf(idColumn(type)) + " = " + this.getID(), null);
        final BOMLineWrapper[] lines = new BOMLineWrapper[ids.length];
        for (int i = 0; i < ids.length; ++i) {
            lines[i] = new BOMLineWrapper(this.getCtx(), ids[i], null, type);
        }
        return lines;
    }
}
