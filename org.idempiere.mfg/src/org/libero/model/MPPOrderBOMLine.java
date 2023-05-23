// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.model.I_M_Warehouse;
import org.compiere.model.I_C_UOM;
import org.compiere.model.I_M_Product;
import org.compiere.model.MLocator;
import org.compiere.model.MStorageOnHand;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import org.adempiere.exceptions.DBException;
import org.compiere.model.MWarehouse;
import org.eevolution.model.MPPProductBOM;
import org.compiere.model.MProduct;
import java.math.RoundingMode;
import org.compiere.model.MUOM;
import org.compiere.util.Env;
import org.compiere.util.DB;
import org.adempiere.exceptions.AdempiereException;
import org.eevolution.model.MPPProductBOMLine;
import java.sql.ResultSet;
import org.compiere.model.Query;
import java.util.Properties;
import java.math.BigDecimal;
import org.libero.tables.X_PP_Order_BOMLine;

public class MPPOrderBOMLine extends X_PP_Order_BOMLine
{
    private static final long serialVersionUID = 1L;
    private MPPOrder m_parent;
    private boolean m_isExplodePhantom;
    private BigDecimal m_qtyRequiredPhantom;
    private BigDecimal m_qtyOnHand;
    private BigDecimal m_qtyAvailable;
    
    public static MPPOrderBOMLine forM_Product_ID(final Properties ctx, final int PP_Order_ID, final int M_Product_ID, final String trxName) {
        return (MPPOrderBOMLine)new Query(ctx, "PP_Order_BOMLine", "PP_Order_ID=? AND M_Product_ID=?", trxName).setParameters(new Object[] { PP_Order_ID, M_Product_ID }).firstOnly();
    }
    
    public MPPOrderBOMLine(final Properties ctx, final int PP_Order_BOMLine_ID, final String trxName) {
        super(ctx, PP_Order_BOMLine_ID, trxName);
        this.m_parent = null;
        this.m_isExplodePhantom = false;
        this.m_qtyRequiredPhantom = null;
        this.m_qtyOnHand = null;
        this.m_qtyAvailable = null;
        if (PP_Order_BOMLine_ID == 0) {
            this.setDefault();
        }
    }
    
    public MPPOrderBOMLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
        this.m_parent = null;
        this.m_isExplodePhantom = false;
        this.m_qtyRequiredPhantom = null;
        this.m_qtyOnHand = null;
        this.m_qtyAvailable = null;
    }
    
    public MPPOrderBOMLine(final MPPProductBOMLine bomLine, final int PP_Order_ID, final int PP_Order_BOM_ID, final int M_Warehouse_ID, final String trxName) {
        this(bomLine.getCtx(), 0, trxName);
        this.setPP_Order_BOM_ID(PP_Order_BOM_ID);
        this.setPP_Order_ID(PP_Order_ID);
        this.setM_Warehouse_ID(M_Warehouse_ID);
        this.setM_ChangeNotice_ID(bomLine.getM_ChangeNotice_ID());
        this.setDescription(bomLine.getDescription());
        this.setHelp(bomLine.getHelp());
        this.setAssay(bomLine.getAssay());
        this.setQtyBatch(bomLine.getQtyBatch());
        this.setQtyBOM(bomLine.getQtyBOM());
        this.setIsQtyPercentage(bomLine.isQtyPercentage());
        this.setComponentType(bomLine.getComponentType());
        this.setC_UOM_ID(bomLine.getC_UOM_ID());
        this.setForecast(bomLine.getForecast());
        this.setIsCritical(bomLine.isCritical());
        this.setIssueMethod(bomLine.getIssueMethod());
        this.setLeadTimeOffset(bomLine.getLeadTimeOffset());
        this.setM_AttributeSetInstance_ID(bomLine.getM_AttributeSetInstance_ID());
        this.setM_Product_ID(bomLine.getM_Product_ID());
        this.setScrap(bomLine.getScrap());
        this.setValidFrom(bomLine.getValidFrom());
        this.setValidTo(bomLine.getValidTo());
        this.setBackflushGroup(bomLine.getBackflushGroup());
    }
    
    protected boolean beforeSave(final boolean newRecord) {
        if (!this.isActive()) {
            throw new AdempiereException("De-Activating an BOM Line is not allowed");
        }
        if (!newRecord && this.is_ValueChanged("M_Product_ID")) {
            throw new AdempiereException("Changing Product is not allowed");
        }
        if (this.getLine() == 0) {
            final String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM PP_Order_BOMLine WHERE PP_Order_ID=?";
            final int ii = DB.getSQLValueEx(this.get_TrxName(), sql, new Object[] { this.getPP_Order_ID() });
            this.setLine(ii);
        }
        if (newRecord && "PH".equals(this.getComponentType())) {
            this.m_qtyRequiredPhantom = this.getQtyRequired();
            this.m_isExplodePhantom = true;
            this.setQtyRequired(Env.ZERO);
        }
        if (newRecord || this.is_ValueChanged("C_UOM_ID") || this.is_ValueChanged("QtyEntered") || this.is_ValueChanged("QtyRequired")) {
            final int precision = MUOM.getPrecision(this.getCtx(), this.getC_UOM_ID());
            this.setQtyEntered(this.getQtyEntered().setScale(precision, RoundingMode.UP));
            this.setQtyRequired(this.getQtyRequired().setScale(precision, RoundingMode.UP));
        }
        if (this.is_ValueChanged("QtyDelivered") || this.is_ValueChanged("QtyRequired")) {
            this.reserveStock();
        }
        return true;
    }
    
    protected boolean afterSave(final boolean newRecord, final boolean success) {
        if (!success) {
            return false;
        }
        this.explodePhantom();
        return true;
    }
    
    protected boolean beforeDelete() {
        this.setQtyRequired(Env.ZERO);
        this.reserveStock();
        return true;
    }
    
    private void explodePhantom() {
        if (this.m_isExplodePhantom && this.m_qtyRequiredPhantom != null) {
            final MProduct parent = MProduct.get(this.getCtx(), this.getM_Product_ID());
            final int PP_Product_BOM_ID = MPPProductBOM.getBOMSearchKey(parent);
            if (PP_Product_BOM_ID <= 0) {
                return;
            }
            final MPPProductBOM bom = MPPProductBOM.get(this.getCtx(), PP_Product_BOM_ID);
            if (bom != null) {
                MPPProductBOMLine[] lines;
                for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
                    final MPPProductBOMLine PP_Product_BOMline = lines[i];
                    final MPPOrderBOMLine PP_Order_BOMLine = new MPPOrderBOMLine(PP_Product_BOMline, this.getPP_Order_ID(), this.getPP_Order_BOM_ID(), this.getM_Warehouse_ID(), this.get_TrxName());
                    PP_Order_BOMLine.setAD_Org_ID(this.getAD_Org_ID());
                    PP_Order_BOMLine.setQtyPlusScrap(this.m_qtyRequiredPhantom);
                    PP_Order_BOMLine.saveEx();
                }
            }
            this.m_isExplodePhantom = false;
        }
    }
    
    public MProduct getM_Product() {
        return MProduct.get(this.getCtx(), this.getM_Product_ID());
    }
    
    public MUOM getC_UOM() {
        return MUOM.get(this.getCtx(), this.getC_UOM_ID());
    }
    
    public MWarehouse getM_Warehouse() {
        return MWarehouse.get(this.getCtx(), this.getM_Warehouse_ID());
    }
    
    public BigDecimal getQtyRequiredPhantom() {
        return (this.m_qtyRequiredPhantom != null) ? this.m_qtyRequiredPhantom : Env.ZERO;
    }
    
    public MPPOrder getParent() {
        final int id = this.getPP_Order_ID();
        if (id <= 0) {
            return this.m_parent = null;
        }
        if (this.m_parent == null || this.m_parent.get_ID() != id) {
            this.m_parent = new MPPOrder(this.getCtx(), id, this.get_TrxName());
        }
        return this.m_parent;
    }
    
    public int getPrecision() {
        return MUOM.getPrecision(this.getCtx(), this.getC_UOM_ID());
    }
    
    public BigDecimal getQtyMultiplier() {
        BigDecimal qty;
        if (this.isQtyPercentage()) {
            qty = this.getQtyBatch().divide(Env.ONEHUNDRED, 8, RoundingMode.HALF_UP);
        }
        else {
            qty = this.getQtyBOM();
        }
        return qty;
    }
    
    public void setQtyPlusScrap(final BigDecimal QtyOrdered) {
        final BigDecimal multiplier = this.getQtyMultiplier();
        final BigDecimal qty = QtyOrdered.multiply(multiplier).setScale(8, RoundingMode.UP);
        if (this.isComponentType("CO", "PH", "PK", "BY", "CP")) {
            this.setQtyRequired(qty);
        }
        else {
            if (!this.isComponentType("TL")) {
                throw new AdempiereException("@NotSupported@ @ComponentType@ " + this.getComponentType());
            }
            this.setQtyRequired(multiplier);
        }
        BigDecimal qtyScrap = this.getScrap();
        if (qtyScrap.signum() != 0) {
            qtyScrap = qtyScrap.divide(Env.ONEHUNDRED, 8, 0);
            this.setQtyRequired(this.getQtyRequired().divide(Env.ONE.subtract(qtyScrap), 8, 4));
        }
    }
    
    @Override
    public void setQtyRequired(BigDecimal QtyRequired) {
        if (QtyRequired != null && this.getC_UOM_ID() != 0) {
            final int precision = this.getPrecision();
            QtyRequired = QtyRequired.setScale(precision, RoundingMode.HALF_UP);
        }
        super.setQtyRequired(QtyRequired);
    }
    
    @Override
    public void setQtyReserved(BigDecimal QtyReserved) {
        if (QtyReserved != null && this.getC_UOM_ID() != 0) {
            final int precision = this.getPrecision();
            QtyReserved = QtyReserved.setScale(precision, RoundingMode.HALF_UP);
        }
        super.setQtyReserved(QtyReserved);
    }
    
    public BigDecimal getQtyOpen() {
        return this.getQtyRequired().subtract(this.getQtyDelivered());
    }
    
    private void loadStorage(final boolean reload) {
        if (!reload && this.m_qtyOnHand != null && this.m_qtyAvailable != null) {
            return;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement("SELECT  bomQtyAvailable(M_Product_ID, M_Warehouse_ID, 0),bomQtyOnHand(M_Product_ID, M_Warehouse_ID, 0) FROM PP_Order_BOMLine WHERE PP_Order_BOMLine_ID=?", this.get_TrxName());
            DB.setParameters(pstmt, new Object[] { this.get_ID() });
            rs = pstmt.executeQuery();
            if (rs.next()) {
                this.m_qtyAvailable = rs.getBigDecimal(1);
                this.m_qtyOnHand = rs.getBigDecimal(2);
            }
        }
        catch (SQLException e) {
            throw new DBException(e, "SELECT  bomQtyAvailable(M_Product_ID, M_Warehouse_ID, 0),bomQtyOnHand(M_Product_ID, M_Warehouse_ID, 0) FROM PP_Order_BOMLine WHERE PP_Order_BOMLine_ID=?");
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
    }
    
    public BigDecimal getQtyAvailable() {
        this.loadStorage(false);
        return this.m_qtyAvailable;
    }
    
    public BigDecimal getQtyVariance() {
        final BigDecimal qtyUsageVariance = new Query(this.getCtx(), "PP_Cost_Collector", "PP_Order_BOMLine_ID=? AND PP_Order_ID=? AND DocStatus IN (?,?) AND CostCollectorType=?", this.get_TrxName()).setParameters(new Object[] { this.getPP_Order_BOMLine_ID(), this.getPP_Order_ID(), "CO", "CL", "120" }).sum("MovementQty");
        return qtyUsageVariance;
    }
    
    public BigDecimal getQtyOnHand() {
        this.loadStorage(false);
        return this.m_qtyOnHand;
    }
    
    public boolean isComponentType(final String... componentTypes) {
        final String currentType = this.getComponentType();
        for (final String type : componentTypes) {
            if (currentType.equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCoProduct() {
        return this.isComponentType("CP");
    }
    
    public boolean isByProduct() {
        return this.isComponentType("BY");
    }
    
    public boolean isComponent() {
        return this.isComponentType("CO", "PK");
    }
    
    public void addDescription(final String description) {
        final String desc = this.getDescription();
        if (desc == null) {
            this.setDescription(description);
        }
        else {
            this.setDescription(String.valueOf(desc) + " | " + description);
        }
    }
    
    private void setDefault() {
        this.setDescription("");
        this.setQtyDelivered(Env.ZERO);
        this.setQtyPost(Env.ZERO);
        this.setQtyReject(Env.ZERO);
        this.setQtyRequired(Env.ZERO);
        this.setQtyReserved(Env.ZERO);
        this.setQtyScrap(Env.ZERO);
    }
    
    protected void reserveStock() {
        final int header_M_Warehouse_ID = this.getParent().getM_Warehouse_ID();
        if (header_M_Warehouse_ID != 0) {
            if (header_M_Warehouse_ID != this.getM_Warehouse_ID()) {
                this.setM_Warehouse_ID(header_M_Warehouse_ID);
            }
            if (this.getAD_Org_ID() != this.getAD_Org_ID()) {
                this.setAD_Org_ID(this.getAD_Org_ID());
            }
        }
        final BigDecimal target = this.getQtyRequired();
        final BigDecimal difference = target.subtract(this.getQtyReserved()).subtract(this.getQtyDelivered());
        this.log.info("Line=" + this.getLine() + " - Target=" + target + ",Difference=" + difference + " - Required=" + this.getQtyRequired() + ",Reserved=" + this.getQtyReserved() + ",Delivered=" + this.getQtyDelivered());
        if (difference.signum() == 0) {
            return;
        }
        final MProduct product = this.getM_Product();
        if (!product.isStocked()) {
            return;
        }
        final BigDecimal reserved = difference;
        final int M_Locator_ID = this.getM_Locator_ID(reserved);
        if (!MStorageOnHand.add(this.getCtx(), this.getM_Warehouse_ID(), M_Locator_ID, this.getM_Product_ID(), this.getM_AttributeSetInstance_ID(), Env.ZERO, this.get_TrxName())) {
            throw new AdempiereException("Storage Update  Error!");
        }
        this.setQtyReserved(this.getQtyReserved().add(difference));
    }
    
    private int getM_Locator_ID(final BigDecimal qty) {
        int M_Locator_ID = 0;
        final int M_ASI_ID = this.getM_AttributeSetInstance_ID();
        if (M_ASI_ID != 0) {
            M_Locator_ID = MStorageOnHand.getM_Locator_ID(this.getM_Warehouse_ID(), this.getM_Product_ID(), M_ASI_ID, qty, this.get_TrxName());
        }
        if (M_Locator_ID == 0) {
            M_Locator_ID = this.getM_Locator_ID();
        }
        if (M_Locator_ID == 0) {
            final MLocator locator = MWarehouse.get(this.getCtx(), this.getM_Warehouse_ID()).getDefaultLocator();
            if (locator != null) {
                M_Locator_ID = locator.get_ID();
            }
        }
        return M_Locator_ID;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getClass().getSimpleName()) + "[" + this.get_ID() + ", Product=" + this.getM_Product_ID() + ", ComponentType=" + this.getComponentType() + ",QtyBatch=" + this.getQtyBatch() + ",QtyRequired=" + this.getQtyRequired() + ",QtyScrap=" + this.getQtyScrap() + "]";
    }
}
