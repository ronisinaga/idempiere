// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.bom.drop;

import org.compiere.model.MProductPrice;
import java.beans.PropertyChangeListener;
import org.compiere.model.MStorageOnHand;
import java.util.Comparator;
import java.util.Collections;
import org.compiere.model.Query;
import org.eevolution.model.MPPProductBOM;
import java.util.ArrayList;
import org.compiere.util.Env;
import java.math.BigDecimal;
import java.util.List;
import org.eevolution.model.MPPProductBOMLine;
import org.compiere.model.MProduct;
import java.beans.PropertyChangeSupport;
import org.compiere.util.CLogger;

public class ProductBOMTreeNode implements ISupportRadioNode
{
    protected static CLogger log;
    private final PropertyChangeSupport propertyChangeSupport;
    MProduct product;
    MPPProductBOMLine productBOMLine;
    List<ProductBOMTreeNode> bomChilds;
    boolean invidateState;
    boolean isChecked;
    private ComparatorBOMTreeNode comparatorBOMTreeNode;
    private BigDecimal unitQty;
    private BigDecimal totQty;
    private BigDecimal qtyAvailable;
    private BigDecimal priceStdAmt;
    private BigDecimal priceTotalAmt;
    public static int PriceListVersion;
    
    static {
        ProductBOMTreeNode.log = CLogger.getCLogger((Class)ProductBOMTreeNode.class);
        ProductBOMTreeNode.PriceListVersion = 0;
    }
    
    public ProductBOMTreeNode(final MProduct product, final BigDecimal qty) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.invidateState = false;
        this.isChecked = false;
        this.comparatorBOMTreeNode = new ComparatorBOMTreeNode();
        this.unitQty = BigDecimal.ZERO;
        this.totQty = BigDecimal.ZERO;
        this.priceStdAmt = Env.ZERO;
        this.priceTotalAmt = Env.ZERO;
        this.product = product;
        this.unitQty = qty;
    }
    
    public ProductBOMTreeNode(final MPPProductBOMLine productBOMLine, final BigDecimal parrentQty) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.invidateState = false;
        this.isChecked = false;
        this.comparatorBOMTreeNode = new ComparatorBOMTreeNode();
        this.unitQty = BigDecimal.ZERO;
        this.totQty = BigDecimal.ZERO;
        this.priceStdAmt = Env.ZERO;
        this.priceTotalAmt = Env.ZERO;
        this.productBOMLine = productBOMLine;
        this.totQty = parrentQty.multiply(productBOMLine.getQty());
        this.unitQty = productBOMLine.getQty();
    }
    
    protected void initChilds() {
        if (this.bomChilds != null) {
            return;
        }
        this.bomChilds = new ArrayList<ProductBOMTreeNode>();
        MPPProductBOMLine[] bomLines = null;
        if (this.product != null) {
            final MPPProductBOM bom = (MPPProductBOM)new Query(Env.getCtx(), "PP_Product_BOM", "M_Product_ID=?", (String)null).setParameters(new Object[] { this.product.get_ID() }).first();
            bomLines = bom.getLines();
        }
        else {
            if (this.productBOMLine == null) {
                this.invidateState = true;
                return;
            }
            final MPPProductBOM bom = (MPPProductBOM)new Query(Env.getCtx(), "PP_Product_BOM", "M_Product_ID=?", (String)null).setParameters(new Object[] { this.productBOMLine.getM_Product_ID() }).first();
            if (bom != null) {
                bomLines = bom.getLines();
            }
        }
        if (bomLines != null) {
            MPPProductBOMLine[] array;
            for (int length = (array = bomLines).length, i = 0; i < length; ++i) {
                final MPPProductBOMLine bomLine = array[i];
                this.bomChilds.add(new ProductBOMTreeNode(bomLine, (this.totQty.compareTo(Env.ZERO) > 0) ? this.totQty : this.unitQty));
            }
        }
        Collections.sort(this.bomChilds, this.comparatorBOMTreeNode);
    }
    
    @Override
    public boolean isLeaf() {
        this.initChilds();
        return this.bomChilds.size() == 0;
    }
    
    @Override
    public ISupportRadioNode getChild(final int index) {
        this.initChilds();
        return this.bomChilds.get(index);
    }
    
    @Override
    public int getChildCount() {
        this.initChilds();
        return this.bomChilds.size();
    }
    
    @Override
    public boolean isRadio() {
        return !"OP".equals(this.getComponentType()) && !"CO".equals(this.getComponentType()) && !"VA".equals(this.getComponentType());
    }
    
    @Override
    public String getGroupName() {
        if (this.isRadio()) {
            return this.getComponentType();
        }
        return "";
    }
    
    @Override
    public boolean isChecked() {
        return "CO".equals(this.getComponentType()) || "VA".equals(this.getComponentType()) || this.isChecked;
    }
    
    @Override
    public boolean isDisable() {
        return "VA".equals(this.getComponentType()) || "CO".equals(this.getComponentType());
    }
    
    @Override
    public void setIsChecked(final boolean isChecked) {
        if ("CO".equals(this.getComponentType()) || "VA".equals(this.getComponentType())) {
            return;
        }
        this.isChecked = isChecked;
    }
    
    @Override
    public void setIsDisable(final boolean isDisable) {
    }
    
    protected String getComponentType(final MPPProductBOMLine bomLine) {
        if (bomLine != null && bomLine.getComponentType() != null) {
            return bomLine.getComponentType();
        }
        return "CO";
    }
    
    protected String getComponentType() {
        return this.getComponentType(this.productBOMLine);
    }
    
    @Override
    public String getLabel() {
        String label = "";
        if (this.productBOMLine != null) {
            label = this.productBOMLine.getProduct().getName();
            this.qtyAvailable = this.getQtyOnHand(this.productBOMLine.getProduct());
        }
        else if (this.product != null) {
            label = this.product.getName();
            this.qtyAvailable = this.getQtyOnHand(this.product);
        }
        return label;
    }
    
    public BigDecimal getQtyAvailable() {
        return this.qtyAvailable;
    }
    
    private BigDecimal getQtyOnHand(final MProduct prod) {
        BigDecimal qtyOnHand = BigDecimal.ZERO;
        final MStorageOnHand[] storages = MStorageOnHand.getOfProduct(Env.getCtx(), prod.getM_Product_ID(), prod.get_TrxName());
        MStorageOnHand[] array;
        for (int length = (array = storages).length, i = 0; i < length; ++i) {
            final MStorageOnHand storage = array[i];
            if (storage != null) {
                qtyOnHand = qtyOnHand.add(storage.getQtyOnHand());
            }
        }
        return qtyOnHand;
    }
    
    public int getProductID() {
        if (this.product == null && this.productBOMLine == null) {
            throw new IllegalStateException("no product info in this node");
        }
        final MProduct productNode = (this.product != null) ? this.product : this.productBOMLine.getProduct();
        return productNode.get_ID();
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public BigDecimal getQty() {
        return this.unitQty;
    }
    
    public BigDecimal getTotQty() {
        return this.totQty;
    }
    
    public void setTotQty(final BigDecimal totQty) {
        this.totQty = totQty;
    }
    
    public void setQty(final BigDecimal qty) {
        if (!this.unitQty.equals(qty)) {
            final BigDecimal oldValue = this.unitQty;
            this.unitQty = qty;
            this.propertyChangeSupport.firePropertyChange("qty", oldValue, this.unitQty);
        }
    }
    
    public BigDecimal getRowPrice() {
        final MProductPrice price = (MProductPrice)new Query(Env.getCtx(), "M_ProductPrice", "M_Product_ID=? AND M_PriceList_Version_ID=?", (String)null).setParameters(new Object[] { this.productBOMLine.getM_Product_ID(), ProductBOMTreeNode.PriceListVersion }).first();
        if (price == null) {
            this.priceStdAmt = Env.ZERO;
            return Env.ZERO;
        }
        final BigDecimal priceStd = price.getPriceStd();
        return this.priceStdAmt = priceStd;
    }
    
    public BigDecimal calculateRowTotalPrice(final BigDecimal qty) {
        return this.priceTotalAmt = this.priceStdAmt.multiply(qty);
    }
    
    public BigDecimal getPriceStdAmt() {
        return this.priceStdAmt;
    }
    
    public BigDecimal getTotalPrice() {
        return this.priceTotalAmt;
    }
    
    class ComparatorBOMTreeNode implements Comparator<ProductBOMTreeNode>
    {
        @Override
        public int compare(final ProductBOMTreeNode bom1, final ProductBOMTreeNode bom2) {
            if (ProductBOMTreeNode.this.getComponentType(bom1.productBOMLine).equals(ProductBOMTreeNode.this.getComponentType(bom2.productBOMLine))) {
                return 0;
            }
            final String t1 = String.valueOf(bom1.productBOMLine.getLine() + 100000);
            final String t2 = String.valueOf(bom2.productBOMLine.getLine() + 100000);
            return t1.compareTo(t2);
        }
    }
}
