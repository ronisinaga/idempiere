// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.panel.ADForm;
import java.util.Iterator;
import org.eevolution.model.MPPProductBOM;
import org.zkoss.zul.TreeitemRenderer;
import org.adempiere.webui.component.SimpleTreeModel;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.eevolution.model.MPPProductBOMLine;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.DefaultTreeNode;
import java.util.ArrayList;
import org.compiere.model.MProduct;
import org.zkoss.zk.ui.event.Event;
import java.beans.PropertyChangeEvent;
import org.zkoss.zul.Center;
import org.zkoss.zul.West;
import org.zkoss.zul.South;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.zkoss.zul.Space;
import org.zkoss.zul.North;
import org.zkoss.zk.ui.Component;
import org.compiere.model.MLookup;
import java.util.Properties;
import org.compiere.model.Lookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MColumn;
import org.compiere.util.Language;
import java.math.BigDecimal;
import org.compiere.util.KeyNamePair;
import java.sql.Timestamp;
import java.util.List;
import java.util.Collection;
import org.adempiere.webui.component.ListModelTable;
import org.compiere.util.Msg;
import java.util.logging.Level;
import java.util.Vector;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Tree;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.event.WTableModelListener;
import org.zkoss.zk.ui.event.EventListener;
import org.adempiere.webui.panel.IFormController;

public class WTreeBOM extends TreeBOM implements IFormController, EventListener, WTableModelListener
{
    private static final long serialVersionUID = 8534705083972399511L;
    private int m_WindowNo;
    private CustomForm m_frame;
    private Tree m_tree;
    private Borderlayout mainLayout;
    private Panel northPanel;
    private Label labelProduct;
    private WSearchEditor fieldProduct;
    private Checkbox implosion;
    private Label treeInfo;
    private Panel dataPane;
    private Panel treePane;
    private ConfirmPanel confirmPanel;
    private WListbox tableBOM;
    private Vector<Vector<Object>> dataBOM;
    
    public WTreeBOM() {
        this.m_WindowNo = 0;
        this.m_frame = new CustomForm();
        this.m_tree = new Tree();
        this.mainLayout = new Borderlayout();
        this.northPanel = new Panel();
        this.labelProduct = new Label();
        this.implosion = new Checkbox();
        this.treeInfo = new Label();
        this.dataPane = new Panel();
        this.treePane = new Panel();
        this.confirmPanel = new ConfirmPanel(true);
        this.tableBOM = new WListbox();
        this.dataBOM = new Vector<Vector<Object>>();
        try {
            this.preInit();
            this.jbInit();
        }
        catch (Exception e) {
            WTreeBOM.log.log(Level.SEVERE, "VTreeBOM.init", (Throwable)e);
        }
    }
    
    private void loadTableBOM() {
        final Vector<String> columnNames = new Vector<String>();
        columnNames.add(Msg.translate(this.getCtx(), "Select"));
        columnNames.add(Msg.translate(this.getCtx(), "IsActive"));
        columnNames.add(Msg.translate(this.getCtx(), "Line"));
        columnNames.add(Msg.translate(this.getCtx(), "ValidFrom"));
        columnNames.add(Msg.translate(this.getCtx(), "ValidTo"));
        columnNames.add(Msg.translate(this.getCtx(), "M_Product_ID"));
        columnNames.add(Msg.translate(this.getCtx(), "C_UOM_ID"));
        columnNames.add(Msg.translate(this.getCtx(), "IsQtyPercentage"));
        columnNames.add(Msg.translate(this.getCtx(), "QtyBatch"));
        columnNames.add(Msg.translate(this.getCtx(), "QtyBOM"));
        columnNames.add(Msg.translate(this.getCtx(), "IsCritical"));
        columnNames.add(Msg.translate(this.getCtx(), "LeadTimeOffset"));
        columnNames.add(Msg.translate(this.getCtx(), "Assay"));
        columnNames.add(Msg.translate(this.getCtx(), "Scrap"));
        columnNames.add(Msg.translate(this.getCtx(), "IssueMethod"));
        columnNames.add(Msg.translate(this.getCtx(), "BackflushGroup"));
        columnNames.add(Msg.translate(this.getCtx(), "Forecast"));
        this.tableBOM.clear();
        this.tableBOM.getModel().removeTableModelListener((WTableModelListener)this);
        final ListModelTable model = new ListModelTable((Collection)this.dataBOM);
        model.addTableModelListener((WTableModelListener)this);
        this.tableBOM.setData(model, (List)columnNames);
        this.tableBOM.setColumnClass(0, (Class)Boolean.class, false);
        this.tableBOM.setColumnClass(1, (Class)Boolean.class, false);
        this.tableBOM.setColumnClass(2, (Class)Integer.class, false);
        this.tableBOM.setColumnClass(3, (Class)Timestamp.class, false);
        this.tableBOM.setColumnClass(4, (Class)Timestamp.class, false);
        this.tableBOM.setColumnClass(5, (Class)KeyNamePair.class, false);
        this.tableBOM.setColumnClass(6, (Class)KeyNamePair.class, false);
        this.tableBOM.setColumnClass(7, (Class)Boolean.class, false);
        this.tableBOM.setColumnClass(8, (Class)BigDecimal.class, false);
        this.tableBOM.setColumnClass(9, (Class)BigDecimal.class, false);
        this.tableBOM.setColumnClass(10, (Class)Boolean.class, false);
        this.tableBOM.setColumnClass(11, (Class)BigDecimal.class, false);
        this.tableBOM.setColumnClass(12, (Class)BigDecimal.class, false);
        this.tableBOM.setColumnClass(13, (Class)Integer.class, false);
        this.tableBOM.setColumnClass(14, (Class)String.class, false);
        this.tableBOM.setColumnClass(15, (Class)String.class, false);
        this.tableBOM.setColumnClass(16, (Class)BigDecimal.class, false);
        this.tableBOM.autoSize();
    }
    
    private void preInit() throws Exception {
        final Properties ctx = this.getCtx();
        final Language language = Language.getLoginLanguage();
        final MLookup m_fieldProduct = MLookupFactory.get(ctx, this.m_WindowNo, MColumn.getColumn_ID("M_Product", "M_Product_ID"), 30, language, "M_Product_ID", 0, false, " M_Product.IsSummary = 'N'");
        this.fieldProduct = new WSearchEditor("M_Product_ID", true, false, true, m_fieldProduct) {
            public void setValue(final Object value) {
                super.setValue(value);
                WTreeBOM.this.action_loadBOM();
            }
        };
        this.implosion.addActionListener((EventListener)this);
    }
    
    private void jbInit() {
        this.m_frame.setWidth("99%");
        this.m_frame.setHeight("100%");
        this.m_frame.setStyle("position: absolute; padding: 0; margin: 0");
        this.m_frame.appendChild((Component)this.mainLayout);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("100%");
        this.mainLayout.setStyle("position: absolute");
        this.labelProduct.setText(Msg.getElement(this.getCtx(), "M_Product_ID"));
        this.implosion.setText(Msg.getElement(this.getCtx(), "Implosion"));
        final North north = new North();
        this.mainLayout.appendChild((Component)north);
        north.appendChild((Component)this.northPanel);
        north.setHeight("28px");
        this.northPanel.appendChild((Component)this.labelProduct);
        this.northPanel.appendChild((Component)new Space());
        ZKUpdateUtil.setWidth((HtmlBasedComponent)this.fieldProduct.getComponent(), "300px");
        this.northPanel.appendChild((Component)this.fieldProduct.getComponent());
        this.northPanel.appendChild((Component)new Space());
        this.northPanel.appendChild((Component)this.implosion);
        this.northPanel.appendChild((Component)new Space());
        this.northPanel.appendChild((Component)this.treeInfo);
        final South south = new South();
        this.mainLayout.appendChild((Component)south);
        south.appendChild((Component)this.confirmPanel);
        this.confirmPanel.addActionListener((EventListener)this);
        final West west = new West();
        this.mainLayout.appendChild((Component)west);
        west.setSplittable(true);
        west.appendChild((Component)this.treePane);
        this.treePane.appendChild((Component)this.m_tree);
        this.m_tree.setStyle("border: none");
        west.setWidth("25%");
        west.setAutoscroll(true);
        final Center center = new Center();
        this.mainLayout.appendChild((Component)center);
        center.appendChild((Component)this.dataPane);
        this.dataPane.appendChild((Component)this.tableBOM);
        this.tableBOM.setVflex(true);
        this.tableBOM.setFixedLayout(true);
        center.setFlex(true);
        center.setAutoscroll(true);
    }
    
    public void dispose() {
        if (this.m_frame != null) {
            this.m_frame.dispose();
        }
        this.m_frame = null;
    }
    
    public void vetoableChange(final PropertyChangeEvent e) {
        final String name = e.getPropertyName();
        final Object value = e.getNewValue();
        if (value == null) {
            return;
        }
        if (name.equals("M_Product_ID") && this.fieldProduct != null) {
            this.action_loadBOM();
        }
    }
    
    public void onEvent(final Event event) throws Exception {
        if (event.getTarget().equals(this.implosion)) {
            this.action_loadBOM();
        }
        if (event.getName().equals("onOK")) {
            this.action_loadBOM();
        }
        if (event.getName().equals("onCancel")) {
            this.dispose();
        }
    }
    
    private void action_loadBOM() {
        final int M_Product_ID = this.getM_Product_ID();
        if (M_Product_ID == 0) {
            return;
        }
        final MProduct product = MProduct.get(this.getCtx(), M_Product_ID);
        final DefaultTreeNode parent = new DefaultTreeNode((Object)this.productSummary(product, false), (Collection)new ArrayList());
        this.dataBOM.clear();
        if (this.isImplosion()) {
            try {
                this.m_tree.setModel((TreeModel)null);
            }
            catch (Exception ex) {}
            if (this.m_tree.getTreecols() != null) {
                this.m_tree.getTreecols().detach();
            }
            if (this.m_tree.getTreefoot() != null) {
                this.m_tree.getTreefoot().detach();
            }
            if (this.m_tree.getTreechildren() != null) {
                this.m_tree.getTreechildren().detach();
            }
            for (final MPPProductBOMLine bomline : MPPProductBOMLine.getByProduct(product)) {
                parent.getChildren().add(this.parent(bomline));
            }
            final Treecols treeCols = new Treecols();
            this.m_tree.appendChild((Component)treeCols);
            final Treecol treeCol = new Treecol();
            treeCols.appendChild((Component)treeCol);
            final SimpleTreeModel model = new SimpleTreeModel(parent);
            this.m_tree.setPageSize(-1);
            this.m_tree.setTreeitemRenderer((TreeitemRenderer)model);
            this.m_tree.setModel((TreeModel)model);
        }
        else {
            try {
                this.m_tree.setModel((TreeModel)null);
            }
            catch (Exception ex2) {}
            if (this.m_tree.getTreecols() != null) {
                this.m_tree.getTreecols().detach();
            }
            if (this.m_tree.getTreefoot() != null) {
                this.m_tree.getTreefoot().detach();
            }
            if (this.m_tree.getTreechildren() != null) {
                this.m_tree.getTreechildren().detach();
            }
            for (final MPPProductBOM bom : MPPProductBOM.getProductBOMs(product)) {
                parent.getChildren().add(this.parent(bom));
            }
            final Treecols treeCols = new Treecols();
            this.m_tree.appendChild((Component)treeCols);
            final Treecol treeCol = new Treecol();
            treeCols.appendChild((Component)treeCol);
            final SimpleTreeModel model = new SimpleTreeModel(parent);
            this.m_tree.setPageSize(-1);
            this.m_tree.setTreeitemRenderer((TreeitemRenderer)model);
            this.m_tree.setModel((TreeModel)model);
        }
        this.m_tree.addEventListener("onSelection", (EventListener)this);
        this.loadTableBOM();
    }
    
    public DefaultTreeNode parent(final MPPProductBOMLine bomline) {
        final MProduct M_Product = MProduct.get(this.getCtx(), bomline.getM_Product_ID());
        final MPPProductBOM bomproduct = new MPPProductBOM(this.getCtx(), bomline.getPP_Product_BOM_ID(), (String)null);
        final DefaultTreeNode parent = new DefaultTreeNode((Object)this.productSummary(M_Product, false), (Collection)new ArrayList());
        final Vector<Object> line = new Vector<Object>(17);
        line.add(new Boolean(false));
        line.add(new Boolean(true));
        line.add(new Integer(bomline.getLine()));
        line.add(bomline.getValidFrom());
        line.add(bomline.getValidTo());
        final KeyNamePair pp = new KeyNamePair(M_Product.getM_Product_ID(), M_Product.getName());
        line.add(pp);
        final KeyNamePair uom = new KeyNamePair(bomline.getC_UOM_ID(), bomline.getC_UOM().getUOMSymbol());
        line.add(uom);
        line.add(new Boolean(bomline.isQtyPercentage()));
        line.add(bomline.getQtyBatch());
        line.add((bomline.getQtyBOM() != null) ? bomline.getQtyBOM() : new BigDecimal(0));
        line.add(new Boolean(bomline.isCritical()));
        line.add(bomline.getLeadTimeOffset());
        line.add(bomline.getAssay());
        line.add(bomline.getScrap());
        line.add(bomline.getIssueMethod());
        line.add(bomline.getBackflushGroup());
        line.add(bomline.getForecast());
        this.dataBOM.add(line);
        final Iterator<MPPProductBOM> iterator = MPPProductBOM.getProductBOMs((MProduct)bomproduct.getM_Product()).iterator();
        if (iterator.hasNext()) {
            final MPPProductBOM bom = iterator.next();
            final MProduct component = MProduct.get(this.getCtx(), bom.getM_Product_ID());
            return this.component(component);
        }
        return parent;
    }
    
    public DefaultTreeNode parent(final MPPProductBOM bom) {
        final DefaultTreeNode parent = new DefaultTreeNode((Object)this.productSummary(bom), (Collection)new ArrayList());
        MPPProductBOMLine[] lines;
        for (int length = (lines = bom.getLines()).length, i = 0; i < length; ++i) {
            final MPPProductBOMLine bomline = lines[i];
            final MProduct component = MProduct.get(this.getCtx(), bomline.getM_Product_ID());
            final Vector<Object> line = new Vector<Object>(17);
            line.add(new Boolean(false));
            line.add(new Boolean(true));
            line.add(new Integer(bomline.getLine()));
            line.add(bomline.getValidFrom());
            line.add(bomline.getValidTo());
            final KeyNamePair pp = new KeyNamePair(component.getM_Product_ID(), component.getName());
            line.add(pp);
            final KeyNamePair uom = new KeyNamePair(bomline.getC_UOM_ID(), bomline.getC_UOM().getUOMSymbol());
            line.add(uom);
            line.add(new Boolean(bomline.isQtyPercentage()));
            line.add(bomline.getQtyBatch());
            line.add(bomline.getQtyBOM());
            line.add(new Boolean(bomline.isCritical()));
            line.add(bomline.getLeadTimeOffset());
            line.add(bomline.getAssay());
            line.add(bomline.getScrap());
            line.add(bomline.getIssueMethod());
            line.add(bomline.getBackflushGroup());
            line.add(bomline.getForecast());
            this.dataBOM.add(line);
            parent.getChildren().add(this.component(component));
        }
        return parent;
    }
    
    public DefaultTreeNode component(final MProduct product) {
        if (this.isImplosion()) {
            final DefaultTreeNode parent = new DefaultTreeNode((Object)this.productSummary(product, false), (Collection)new ArrayList());
            for (final MPPProductBOMLine bomline : MPPProductBOMLine.getByProduct(product)) {
                parent.getChildren().add(this.parent(bomline));
            }
            return parent;
        }
        final Iterator<MPPProductBOM> iterator2 = MPPProductBOM.getProductBOMs(product).iterator();
        if (iterator2.hasNext()) {
            final MPPProductBOM bom = iterator2.next();
            return this.parent(bom);
        }
        return new DefaultTreeNode((Object)this.productSummary(product, true), (Collection)new ArrayList());
    }
    
    private int getM_Product_ID() {
        final Integer Product = (Integer)this.fieldProduct.getValue();
        if (Product == null) {
            return 0;
        }
        return Product;
    }
    
    private boolean isImplosion() {
        return this.implosion.isSelected();
    }
    
    public ADForm getForm() {
        return (ADForm)this.m_frame;
    }
    
    public void tableChanged(final WTableModelEvent event) {
    }
}
