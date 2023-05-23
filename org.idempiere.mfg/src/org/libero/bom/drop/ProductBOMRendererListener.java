// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.bom.drop;

import org.adempiere.webui.event.ValueChangeEvent;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import org.zkoss.zul.Treechildren;
import java.util.Iterator;
import java.util.List;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.editor.WNumberEditor;
import org.zkoss.zul.Label;
import org.compiere.util.Msg;
import org.zkoss.zul.Treecol;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Treeitem;
import org.compiere.util.Env;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import org.zkoss.zul.Tree;
import org.compiere.util.CLogger;
import org.adempiere.webui.event.ValueChangeListener;
import java.beans.PropertyChangeListener;

public class ProductBOMRendererListener implements IRendererListener, PropertyChangeListener, ValueChangeListener
{
    protected static CLogger log;
    public static final String QTY_COMPONENT = "qty_component";
    public static final String TOTAL_QTY = "total_qty";
    public static final String TOTAL_PRICE = "total_price";
    public static final String Tree_ITEM = "tree_item";
    protected Tree tree;
    private static BigDecimal GrandTotal;
    private final PropertyChangeSupport propertyChangeSupport;
    
    static {
        ProductBOMRendererListener.log = CLogger.getCLogger((Class)ProductBOMRendererListener.class);
        ProductBOMRendererListener.GrandTotal = Env.ZERO;
    }
    
    public ProductBOMRendererListener() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    @Override
    public void render(final Treeitem item, final Treerow row, final ISupportRadioNode data, final int index) {
        if (data != null && data instanceof ProductBOMTreeNode) {
            BigDecimal buffer = Env.ZERO;
            final ProductBOMTreeNode productBOMTreeNode = (ProductBOMTreeNode)data;
            final Treecell available = new Treecell();
            row.appendChild((Component)available);
            final Treecell inputcell = new Treecell();
            row.appendChild((Component)inputcell);
            final Treecell pricecell = new Treecell();
            row.appendChild((Component)pricecell);
            final Treecell totcell = new Treecell();
            row.appendChild((Component)totcell);
            final Treecell totalpricecell = new Treecell();
            row.appendChild((Component)totalpricecell);
            if (item.getTree().getTreecols() != null && item.getTree().getTreecols().getChildren().size() < row.getChildren().size()) {
                item.getTree().getTreecols().appendChild((Component)new Treecol());
                item.getTree().getTreecols().appendChild((Component)new Treecol(Msg.translate(Env.getCtx(), "QtyAvailable")));
                item.getTree().getTreecols().appendChild((Component)new Treecol(Msg.translate(Env.getCtx(), "Qty")));
                item.getTree().getTreecols().appendChild((Component)new Treecol(Msg.translate(Env.getCtx(), "Price")));
                item.getTree().getTreecols().appendChild((Component)new Treecol(Msg.translate(Env.getCtx(), "TotalQty")));
                item.getTree().getTreecols().appendChild((Component)new Treecol(Msg.translate(Env.getCtx(), "TotalPrice")));
            }
            boolean editQty = false;
            editQty = "VA".equals(productBOMTreeNode.getComponentType());
            productBOMTreeNode.getLabel();
            final Label availableQty = new Label();
            final WNumberEditor inputQty = new WNumberEditor();
            final NumberBox price = new NumberBox(false);
            final NumberBox totQty = new NumberBox(false);
            final NumberBox totPrice = new NumberBox(false);
            price.setEnabled(false);
            totQty.setEnabled(false);
            totPrice.setEnabled(false);
            price.getDecimalbox().setScale(2);
            totPrice.getDecimalbox().setStyle("text-align:right");
            inputQty.setReadWrite(editQty);
            totQty.getDecimalbox().setScale(2);
            totPrice.getDecimalbox().setScale(2);
            available.appendChild((Component)availableQty);
            inputcell.appendChild((Component)inputQty.getComponent());
            pricecell.appendChild((Component)price);
            totcell.appendChild((Component)totQty);
            totalpricecell.appendChild((Component)totPrice);
            if (productBOMTreeNode.productBOMLine != null) {
                availableQty.setValue(productBOMTreeNode.getQtyAvailable().toString());
                inputQty.setValue((Object)productBOMTreeNode.getQty());
                price.setValue((Object)productBOMTreeNode.getRowPrice());
                totQty.setValue((Object)productBOMTreeNode.getTotQty());
                totPrice.setValue((Object)productBOMTreeNode.calculateRowTotalPrice(productBOMTreeNode.getTotQty()));
                item.setAttribute("total_qty", (Object)totQty);
                totQty.setAttribute("tree_item", (Object)item);
                item.setAttribute("total_price", (Object)totPrice);
                totPrice.setAttribute("tree_item", (Object)item);
                if (!isParentChecked(item)) {
                    totQty.setValue((Object)Env.ZERO);
                    totPrice.setValue((Object)Env.ZERO);
                }
                if (productBOMTreeNode.getChildCount() > 0) {
                    totPrice.setValue((Object)Env.ZERO);
                }
                ProductBOMRendererListener.GrandTotal = ProductBOMRendererListener.GrandTotal.add(totPrice.getValue());
                this.propertyChangeSupport.firePropertyChange("GrandTotal", buffer, ProductBOMRendererListener.GrandTotal);
                buffer = ProductBOMRendererListener.GrandTotal;
            }
            else {
                ProductBOMRendererListener.log.warning(data.toString());
            }
            item.setAttribute("qty_component", (Object)inputQty);
            inputQty.getComponent().setAttribute("tree_item", (Object)item);
            productBOMTreeNode.addPropertyChangeListener(this);
            inputQty.addValueChangeListener((ValueChangeListener)this);
        }
    }
    
    private void rollUpParentNodeTotalPricing(final Treeitem treeItem) {
        final ProductBOMTreeNode rootNode = (ProductBOMTreeNode)this.tree.getModel().getRoot();
        if (rootNode.getChildCount() > 0) {
            final BigDecimal grandtotalprice = this.rollupRoutine(rootNode.bomChilds);
            if (grandtotalprice.compareTo(ProductBOMRendererListener.GrandTotal) == 0) {
                ProductBOMRendererListener.log.info("Grand Total CORRECT = Sum of Parent Nodes");
            }
            else {
                ProductBOMRendererListener.log.info("Grand Total ERROR != Sum sub parent nodes");
            }
        }
    }
    
    private BigDecimal rollupRoutine(final List<ProductBOMTreeNode> bomchildren) {
        BigDecimal nodeTotalPrice = Env.ZERO;
        for (final ProductBOMTreeNode node : bomchildren) {
            if (node.getChildCount() > 0) {
                final BigDecimal totalPrice = this.rollupRoutine(node.bomChilds);
                final int[] pathToNode = this.tree.getModel().getPath((Object)node);
                final Treeitem treeItem = this.tree.renderItemByPath(pathToNode);
                final NumberBox itemTotalPrice = (NumberBox)treeItem.getAttribute("total_price");
                itemTotalPrice.setValue((Object)totalPrice);
                itemTotalPrice.getDecimalbox().setStyle("font-size:16px;color:gray;text-align:right;font-weight: bold");
                if (treeItem.getLevel() > 0) {
                    final Integer fontsize = treeItem.getRoot().getChildren().size() - treeItem.getLevel() + 13;
                    itemTotalPrice.getDecimalbox().setStyle("font-size:" + fontsize.toString() + "px;color:gray;text-align:right;font-weight: bold");
                }
                nodeTotalPrice = nodeTotalPrice.add(itemTotalPrice.getValue());
            }
            else {
                final int[] pathToNode2 = this.tree.getModel().getPath((Object)node);
                final Treeitem bomitem = this.tree.renderItemByPath(pathToNode2);
                final NumberBox itemTotalPrice2 = (NumberBox)bomitem.getAttribute("total_price");
                nodeTotalPrice = nodeTotalPrice.add(itemTotalPrice2.getValue());
            }
        }
        return nodeTotalPrice;
    }
    
    private static boolean isParentChecked(Treeitem thisItem) {
        if (thisItem == null) {
            return true;
        }
        for (Treeitem parentItem = thisItem; parentItem != null; parentItem = thisItem.getParentItem()) {
            final ProductBOMTreeNode dataItem = (ProductBOMTreeNode)parentItem.getAttribute("REF_DATA_MODEL");
            if (!dataItem.isChecked() || dataItem.getTotQty().compareTo(Env.ZERO) == 0) {
                return false;
            }
            thisItem = parentItem;
        }
        return true;
    }
    
    @Override
    public void onchecked(final Treeitem item, final ISupportRadioNode data, final boolean isChecked) {
        BigDecimal totQty = Env.ZERO;
        BigDecimal totPrice = Env.ZERO;
        if (isParentChecked(item)) {
            totQty = ((ProductBOMTreeNode)data).getQty();
            final Treeitem parent = item.getParentItem();
            if (parent != null) {
                final NumberBox totalQtyComponent = (NumberBox)parent.getAttribute("total_qty");
                totQty = totQty.multiply(totalQtyComponent.getValue());
            }
            totPrice = ((ProductBOMTreeNode)data).calculateRowTotalPrice(totQty);
            if (item.isEmpty()) {
                ProductBOMRendererListener.GrandTotal = ProductBOMRendererListener.GrandTotal.add(totPrice);
            }
        }
        else if (item.isEmpty()) {
            final NumberBox totalQtyComponent2 = (NumberBox)item.getAttribute("total_qty");
            ProductBOMRendererListener.GrandTotal = ProductBOMRendererListener.GrandTotal.subtract(((ProductBOMTreeNode)data).calculateRowTotalPrice(totalQtyComponent2.getValue()));
        }
        final int[] pathToNode = this.tree.getModel().getPath((Object)data);
        final Treeitem treeItem = this.tree.renderItemByPath(pathToNode);
        final NumberBox totalQtyComponent3 = (NumberBox)item.getAttribute("total_qty");
        totalQtyComponent3.setValue((Object)totQty);
        final NumberBox totPriceComponent = (NumberBox)treeItem.getAttribute("total_price");
        final BigDecimal oldvalue = totPriceComponent.getValue();
        this.propertyChangeSupport.firePropertyChange("GrandTotal", totPrice, oldvalue);
        totPriceComponent.setValue((Object)totPrice);
        if (!treeItem.isEmpty()) {
            this.cascadeChildren(treeItem);
        }
        this.rollUpParentNodeTotalPricing(treeItem);
    }
    
    private void cascadeChildren(final Treeitem treeItem) {
        final Treechildren tch = treeItem.getTreechildren();
        if (tch != null) {
            final Collection<Treeitem> children = (Collection<Treeitem>)tch.getItems();
            for (Treeitem child : children) {
                final ProductBOMTreeNode treeNode = (ProductBOMTreeNode)child.getAttribute("REF_DATA_MODEL");
                final int[] pathToNode = this.tree.getModel().getPath((Object)treeNode);
                child = this.tree.renderItemByPath(pathToNode);
                final Treeitem parentTreeItem = child.getParentItem();
                final ProductBOMTreeNode parentNode = (ProductBOMTreeNode)parentTreeItem.getAttribute("REF_DATA_MODEL");
                BigDecimal totQty = Env.ZERO;
                if (isParentChecked(child)) {
                    totQty = treeNode.getQty().multiply(parentNode.getTotQty());
                }
                final NumberBox totQtyComponent = (NumberBox)child.getAttribute("total_qty");
                totQtyComponent.setValue((Object)totQty);
                final NumberBox totPriceComponent = (NumberBox)child.getAttribute("total_price");
                final BigDecimal oldPrice = totPriceComponent.getValue();
                final BigDecimal totPrice = totQty.multiply(treeNode.getPriceStdAmt());
                ProductBOMRendererListener.GrandTotal = ProductBOMRendererListener.GrandTotal.subtract(oldPrice);
                ProductBOMRendererListener.GrandTotal = ProductBOMRendererListener.GrandTotal.add(totPrice);
                this.propertyChangeSupport.firePropertyChange("GrandTotal", totPrice, oldPrice);
                totPriceComponent.setValue((Object)totPrice);
            }
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final ProductBOMTreeNode nodeChange = (ProductBOMTreeNode)evt.getSource();
        final int[] pathToNode = this.tree.getModel().getPath((Object)nodeChange);
        final Treeitem treeItem = this.tree.renderItemByPath(pathToNode);
        final WNumberEditor editor = (WNumberEditor)treeItem.getAttribute("qty_component");
        final BigDecimal newQty = (BigDecimal)evt.getNewValue();
        editor.setValue((Object)newQty);
        final Treeitem parent = treeItem.getParentItem();
        BigDecimal newPrice = nodeChange.getPriceStdAmt();
        BigDecimal parentTotQty = Env.ONE;
        if (isParentChecked(treeItem) && parent != null) {
            final ProductBOMTreeNode parentNode = (ProductBOMTreeNode)parent.getAttribute("REF_DATA_MODEL");
            parentTotQty = parentNode.getTotQty();
        }
        final BigDecimal totQty = newQty.multiply(parentTotQty);
        newPrice = newPrice.multiply(totQty);
        final NumberBox totQtyComponent = (NumberBox)treeItem.getAttribute("total_qty");
        totQtyComponent.setValue((Object)totQty);
        final NumberBox totPriceComponent = (NumberBox)treeItem.getAttribute("total_price");
        ProductBOMRendererListener.GrandTotal = ProductBOMRendererListener.GrandTotal.subtract(totPriceComponent.getValue());
        ProductBOMRendererListener.GrandTotal = ProductBOMRendererListener.GrandTotal.add(newPrice);
        this.propertyChangeSupport.firePropertyChange("GrandTotal", totPriceComponent.getValue(), newPrice);
        totPriceComponent.setValue((Object)newPrice);
        nodeChange.setTotQty(totQty);
        if (!treeItem.isEmpty()) {
            this.cascadeChildren(treeItem);
        }
    }
    
    public void valueChange(final ValueChangeEvent evt) {
        final Treeitem treeItem = (Treeitem)((WNumberEditor)evt.getSource()).getComponent().getAttribute("tree_item");
        final ProductBOMTreeNode nodeModel = (ProductBOMTreeNode)treeItem.getAttribute("REF_DATA_MODEL");
        nodeModel.setQty((BigDecimal)evt.getNewValue());
    }
    
    public void setTree(final Tree tree) {
        this.tree = tree;
    }
    
    public static String getGrandTotal() {
        return ProductBOMRendererListener.GrandTotal.setScale(2).toString();
    }
    
    public static void setGrandTotal(final BigDecimal a) {
        ProductBOMRendererListener.GrandTotal = a;
    }
}
