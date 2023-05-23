// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.compiere.model.MProductPricing;
import org.compiere.model.MProjectTask;
import org.compiere.model.Query;
import org.compiere.model.MProjectPhase;
import org.compiere.model.MProjectLine;
import org.compiere.model.MProject;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoice;
import org.adempiere.webui.window.FDialog;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrder;
import org.zkoss.zul.Treeitem;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.util.Callback;
import org.compiere.util.Trx;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.session.SessionManager;
import java.beans.PropertyChangeEvent;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeitemRenderer;
import org.libero.bom.drop.IRendererListener;
import org.libero.bom.drop.SupportRadioTreeitemRenderer;
import org.libero.bom.drop.ISupportRadioNode;
import org.libero.bom.drop.SupportRadioTreeModel;
import org.libero.bom.drop.ProductBOMTreeNode;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Separator;
import org.libero.bom.drop.ProductBOMRendererListener;
import org.compiere.util.DB;
import org.compiere.model.MRole;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.compiere.util.KeyNamePair;
import org.zkoss.zul.Space;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Caption;
import org.compiere.util.Msg;
import java.util.logging.Level;
import org.adempiere.webui.component.GridFactory;
import org.compiere.util.Env;
import org.zkoss.zul.Groupbox;
import org.adempiere.webui.component.Label;
import org.zkoss.zul.Decimalbox;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.ConfirmPanel;
import org.compiere.util.CLogger;
import org.zkoss.zul.Tree;
import java.math.BigDecimal;
import org.compiere.model.MProduct;
import java.beans.PropertyChangeListener;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.adempiere.webui.panel.ADForm;

public class WBOMDropConfigurator extends ADForm implements org.zkoss.zk.ui.event.EventListener<Event>, PropertyChangeListener
{
    private static final long serialVersionUID = 8864346687201400591L;
    private MProduct m_product;
    private BigDecimal m_qty;
    private Tree testProductBOMTree;
    private int m_bomLine;
    private static CLogger log;
    private ConfirmPanel confirmPanel;
    private Grid selectionPanel;
    private Listbox productField;
    private Listbox priceListField;
    private Decimalbox productQty;
    private Listbox orderField;
    private Listbox invoiceField;
    private Listbox projectPhaseField;
    private Listbox projectTaskField;
    private Listbox projectField;
    private Label totalDisplay;
    private Groupbox grpSelectionPanel;
    Integer lineCount;
    
    static {
        WBOMDropConfigurator.log = CLogger.getCLogger((Class)WBOMDropConfigurator.class);
    }
    
    public WBOMDropConfigurator() {
        this.m_qty = Env.ONE;
        this.m_bomLine = 0;
        this.confirmPanel = new ConfirmPanel(true);
        this.selectionPanel = GridFactory.newGridLayout();
        this.productField = new Listbox();
        this.priceListField = new Listbox();
        this.productQty = new Decimalbox();
        this.orderField = new Listbox();
        this.invoiceField = new Listbox();
        this.projectPhaseField = new Listbox();
        this.projectTaskField = new Listbox();
        this.projectField = new Listbox();
        this.totalDisplay = new Label();
        this.grpSelectionPanel = new Groupbox();
        this.lineCount = 0;
    }
    
    protected void initForm() {
        WBOMDropConfigurator.log.info("");
        try {
            this.confirmPanel = new ConfirmPanel(true);
            this.createSelectionPanel(true, true, true);
            this.createMainPanel();
            this.confirmPanel.addActionListener("onClick", (org.zkoss.zk.ui.event.EventListener)this);
        }
        catch (Exception e) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "", (Throwable)e);
        }
    }
    
    public void dispose() {
        if (this.selectionPanel != null) {
            this.selectionPanel.getChildren().clear();
        }
        this.selectionPanel = null;
    }
    
    private void createSelectionPanel(final boolean order, final boolean invoice, final boolean project) {
        final Caption caption = new Caption(Msg.translate(Env.getCtx(), "Selection"));
        this.grpSelectionPanel.appendChild((Component)caption);
        this.grpSelectionPanel.appendChild((Component)this.selectionPanel);
        this.productField.setRows(1);
        this.productField.setMold("select");
        KeyNamePair[] keyNamePair = this.getProducts();
        for (int i = 0; i < keyNamePair.length; ++i) {
            this.productField.addItem(keyNamePair[i]);
        }
        final Rows rows = this.selectionPanel.newRows();
        final Row boxProductQty = rows.newRow();
        final Label lblProduct = new Label(Msg.translate(Env.getCtx(), "M_Product_ID"));
        final Label lblQty = new Label(Msg.translate(Env.getCtx(), "Qty"));
        this.productQty.setValue(Env.ONE);
        this.productField.addEventListener("onSelect", (org.zkoss.zk.ui.event.EventListener)this);
        this.productQty.addEventListener("onChange", (org.zkoss.zk.ui.event.EventListener)this);
        this.productField.setWidth("99%");
        boxProductQty.appendChild(lblProduct.rightAlign());
        boxProductQty.appendChild((Component)this.productField);
        boxProductQty.appendChild(lblQty.rightAlign());
        boxProductQty.appendChild((Component)this.productQty);
        if (order) {
            keyNamePair = this.getOrders();
            this.orderField.setRows(1);
            this.orderField.setMold("select");
            this.orderField.setWidth("99%");
            for (int j = 0; j < keyNamePair.length; ++j) {
                this.orderField.addItem(keyNamePair[j]);
            }
            keyNamePair = this.getPriceList();
            this.priceListField.setRows(1);
            this.priceListField.setMold("select");
            this.priceListField.setWidth("99%");
            for (int j = 0; j < keyNamePair.length; ++j) {
                this.priceListField.addItem(keyNamePair[j]);
            }
            final Label lblOrder = new Label(Msg.translate(Env.getCtx(), "C_Order_ID"));
            final Label lblPriceList = new Label(Msg.translate(Env.getCtx(), "Price"));
            final Row boxOrder = rows.newRow();
            this.orderField.addEventListener("onClick", (org.zkoss.zk.ui.event.EventListener)this);
            this.priceListField.addEventListener("onClick", (org.zkoss.zk.ui.event.EventListener)this);
            boxOrder.appendChild(lblOrder.rightAlign());
            boxOrder.appendChild((Component)this.orderField);
            boxOrder.appendChild(lblPriceList.rightAlign());
            boxOrder.appendChild((Component)this.priceListField);
        }
        if (invoice) {
            this.invoiceField.setRows(1);
            this.invoiceField.setMold("select");
            this.invoiceField.setWidth("99%");
            keyNamePair = this.getInvoices();
            for (int j = 0; j < keyNamePair.length; ++j) {
                this.invoiceField.addItem(keyNamePair[j]);
            }
            final Label lblInvoice = new Label(Msg.translate(Env.getCtx(), "C_Invoice_ID"));
            final Row boxInvoices = rows.newRow();
            this.invoiceField.addEventListener("onSelect", (org.zkoss.zk.ui.event.EventListener)this);
            boxInvoices.appendChild(lblInvoice.rightAlign());
            boxInvoices.appendChild((Component)this.invoiceField);
            boxInvoices.appendChild((Component)new Space());
            boxInvoices.appendChild((Component)new Space());
        }
        if (project) {
            this.projectField.setRows(1);
            this.projectField.setMold("select");
            this.projectField.setWidth("99%");
            keyNamePair = this.getProjects();
            for (int j = 0; j < keyNamePair.length; ++j) {
                this.projectField.addItem(keyNamePair[j]);
            }
            final Label lblProject = new Label(Msg.translate(Env.getCtx(), "C_Project_ID"));
            final Row boxProject = rows.newRow();
            this.projectField.addEventListener("onSelect", (org.zkoss.zk.ui.event.EventListener)this);
            boxProject.appendChild(lblProject.rightAlign());
            boxProject.appendChild((Component)this.projectField);
            boxProject.appendChild((Component)new Space());
            boxProject.appendChild((Component)new Space());
            this.projectPhaseField.setRows(1);
            this.projectPhaseField.setMold("select");
            this.projectPhaseField.setWidth("99%");
            keyNamePair = this.getProjectPhases();
            for (int k = 0; k < keyNamePair.length; ++k) {
                this.projectPhaseField.addItem(keyNamePair[k]);
            }
            final Label lblProjectPhase = new Label(Msg.translate(Env.getCtx(), "C_ProjectPhase_ID"));
            final Row boxProjectPhase = rows.newRow();
            this.projectPhaseField.addEventListener("onSelect", (org.zkoss.zk.ui.event.EventListener)this);
            boxProjectPhase.appendChild(lblProjectPhase.rightAlign());
            boxProjectPhase.appendChild((Component)this.projectPhaseField);
            this.projectTaskField.setRows(1);
            this.projectTaskField.setMold("select");
            this.projectTaskField.setWidth("99%");
            keyNamePair = this.getProjectTasks();
            for (int l = 0; l < keyNamePair.length; ++l) {
                this.projectTaskField.addItem(keyNamePair[l]);
            }
            final Label lblProjectTask = new Label(Msg.translate(Env.getCtx(), "C_ProjectTask_ID"));
            this.projectTaskField.addEventListener("onSelect", (org.zkoss.zk.ui.event.EventListener)this);
            boxProjectPhase.appendChild(lblProjectTask.rightAlign());
            boxProjectPhase.appendChild((Component)this.projectTaskField);
        }
        this.confirmPanel.setEnabled("Ok", false);
    }
    
    private KeyNamePair[] getProjectTasks() {
        final String sql = "SELECT C_ProjectTask_ID, Name FROM C_ProjectTask WHERE IsActive='Y'";
        return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(sql, "C_ProjectTask", false, false), true);
    }
    
    private KeyNamePair[] getProjectPhases() {
        final String sql = "SELECT C_ProjectPhase_ID, Name FROM C_ProjectPhase WHERE IsActive='Y'";
        return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(sql, "C_ProjectPhase", false, false), true);
    }
    
    private KeyNamePair[] getProducts() {
        final String sql = "SELECT M_Product_ID, Name FROM M_Product WHERE IsBOM='Y' AND IsVerified='Y' AND IsActive='Y' ORDER BY Name";
        return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(sql, "M_Product", false, false), true);
    }
    
    private KeyNamePair[] getOrders() {
        final String sql = "SELECT C_Order_ID, DocumentNo || '_' || GrandTotal FROM C_Order WHERE Processed='N' AND DocStatus='DR' ORDER BY DocumentNo";
        return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(sql, "C_Order", false, false), true);
    }
    
    private KeyNamePair[] getPriceList() {
        final String sql = "SELECT M_PriceList_Version_ID, Name  FROM M_PriceList_Version WHERE IsActive='Y'";
        return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(sql, "M_PriceList_Version", false, false), true);
    }
    
    private KeyNamePair[] getProjects() {
        final String sql = "SELECT C_Project_ID, Name FROM C_Project WHERE Processed='N' AND IsSummary='N' AND IsActive='Y' AND ProjectCategory<>'S' ORDER BY Name";
        return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(sql, "C_Project", false, false), true);
    }
    
    private KeyNamePair[] getInvoices() {
        final String sql = "SELECT C_Invoice_ID, DocumentNo || '_' || GrandTotal FROM C_Invoice WHERE Processed='N' AND DocStatus='DR' ORDER BY DocumentNo";
        return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(sql, "C_Invoice", false, false), true);
    }
    
    private void createMainPanel() {
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config(": " + this.m_product);
        }
        this.getChildren().clear();
        ProductBOMRendererListener.setGrandTotal(Env.ZERO);
        this.appendChild((Component)new Separator());
        this.appendChild((Component)this.grpSelectionPanel);
        this.appendChild((Component)new Separator());
        final Hlayout row = new Hlayout();
        this.totalDisplay.setValue(String.valueOf(Msg.translate(Env.getCtx(), "GrandTotal")) + " " + Msg.translate(Env.getCtx(), "Price") + ": " + ProductBOMRendererListener.getGrandTotal());
        this.totalDisplay.setStyle("font-size:32px;color:gray;font-weight: bold;");
        row.appendChild((Component)this.totalDisplay);
        row.appendChild((Component)this.confirmPanel);
        row.setStyle("text-align:right");
        this.appendChild((Component)row);
        this.appendChild((Component)new Separator());
        this.setBorder("normal");
        this.setContentStyle("overflow: auto");
        if (this.m_product != null && this.m_product.get_ID() > 0) {
            if (this.m_product.getDescription() == null || this.m_product.getDescription().length() > 0) {}
            this.m_bomLine = 0;
            (this.testProductBOMTree = new Tree()).appendChild((Component)new Treecols());
            this.testProductBOMTree.getTreecols().appendChild((Component)new Treecol(this.m_product.getName()));
            final SupportRadioTreeModel model = new SupportRadioTreeModel(new ProductBOMTreeNode(this.m_product, this.m_qty));
            final SupportRadioTreeitemRenderer renderer = new SupportRadioTreeitemRenderer();
            renderer.isOpen = true;
            final ProductBOMRendererListener rendererListener = new ProductBOMRendererListener();
            rendererListener.setTree(this.testProductBOMTree);
            rendererListener.addPropertyChangeListener(this);
            renderer.setRendererListener(rendererListener);
            this.testProductBOMTree.setItemRenderer((TreeitemRenderer)renderer);
            this.testProductBOMTree.setModel((TreeModel)model);
            this.appendChild((Component)this.testProductBOMTree);
        }
    }
    
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("GrandTotal")) {
            this.totalDisplay.setValue(String.valueOf(Msg.translate(Env.getCtx(), "GrandTotal")) + " " + Msg.translate(Env.getCtx(), "Price") + ": " + ProductBOMRendererListener.getGrandTotal());
        }
    }
    
    public void onEvent(final Event e) throws Exception {
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config(e.getName());
        }
        final Object source = e.getTarget();
        if (source == this.productField || source == this.productQty) {
            this.m_qty = this.productQty.getValue();
            if (source == this.productQty && this.testProductBOMTree != null) {
                ((ProductBOMTreeNode)this.testProductBOMTree.getModel().getRoot()).setQty(this.m_qty);
            }
            final ListItem listitem = this.productField.getSelectedItem();
            KeyNamePair pp = null;
            if (listitem != null) {
                pp = listitem.toKeyNamePair();
            }
            this.m_product = ((pp != null) ? MProduct.get(Env.getCtx(), pp.getKey()) : null);
            this.createMainPanel();
        }
        else if (source == this.priceListField) {
            final ListItem listitem = this.priceListField.getSelectedItem();
            KeyNamePair pp = null;
            if (listitem != null) {
                pp = listitem.toKeyNamePair();
            }
            ProductBOMTreeNode.PriceListVersion = pp.getKey();
        }
        else if (source == this.orderField) {
            final ListItem listitem = this.orderField.getSelectedItem();
            KeyNamePair pp = null;
            if (listitem != null) {
                pp = listitem.toKeyNamePair();
            }
            final boolean valid = pp != null && pp.getKey() > 0;
            if (this.invoiceField != null) {
                this.invoiceField.setEnabled(!valid);
            }
            if (this.projectField != null) {
                this.projectField.setEnabled(!valid);
                this.projectPhaseField.setEnabled(!valid);
                this.projectTaskField.setEnabled(!valid);
            }
        }
        else if (source == this.invoiceField) {
            final ListItem listitem = this.invoiceField.getSelectedItem();
            KeyNamePair pp = null;
            if (listitem != null) {
                pp = listitem.toKeyNamePair();
            }
            final boolean valid = pp != null && pp.getKey() > 0;
            if (this.orderField != null) {
                this.orderField.setEnabled(!valid);
            }
            if (this.projectField != null) {
                this.projectField.setEnabled(!valid);
            }
        }
        else if (source == this.projectField) {
            final ListItem listitem = this.projectField.getSelectedItem();
            KeyNamePair pp = null;
            if (listitem != null) {
                pp = listitem.toKeyNamePair();
            }
            final boolean valid = pp != null && pp.getKey() > 0;
            if (this.orderField != null) {
                this.orderField.setEnabled(!valid);
            }
            if (this.invoiceField != null) {
                this.invoiceField.setEnabled(!valid);
            }
        }
        else if (this.confirmPanel.getButton("Ok").equals(e.getTarget())) {
            if (this.onSave()) {
                SessionManager.getAppDesktop().closeActiveWindow();
            }
        }
        else if (this.confirmPanel.getButton("Cancel").equals(e.getTarget())) {
            SessionManager.getAppDesktop().closeActiveWindow();
        }
        else {
            super.onEvent(e);
        }
        boolean OK = this.m_product != null;
        if (OK) {
            KeyNamePair pp = null;
            if (this.orderField != null) {
                final ListItem listitem2 = this.orderField.getSelectedItem();
                if (listitem2 != null) {
                    pp = listitem2.toKeyNamePair();
                }
            }
            if ((pp == null || pp.getKey() <= 0) && this.invoiceField != null) {
                final ListItem listitem2 = this.invoiceField.getSelectedItem();
                if (listitem2 != null) {
                    pp = listitem2.toKeyNamePair();
                }
            }
            if ((pp == null || pp.getKey() <= 0) && this.projectField != null) {
                final ListItem listitem2 = this.projectField.getSelectedItem();
                if (listitem2 != null) {
                    pp = listitem2.toKeyNamePair();
                }
            }
            OK = (pp != null && pp.getKey() > 0);
        }
        this.confirmPanel.setEnabled("Ok", OK);
    }
    
    private boolean onSave() {
        final String trxName = Trx.createTrxName("BDP");
        final Trx localTrx = Trx.get(trxName, true);
        try {
            if (this.cmd_save(localTrx)) {
                localTrx.commit();
                return true;
            }
            localTrx.rollback();
            return false;
        }
        finally {
            localTrx.close();
        }
    }
    
    private boolean cmd_save(final Trx trx) {
        ListItem listitem = this.orderField.getSelectedItem();
        KeyNamePair pp = null;
        if (listitem != null) {
            pp = listitem.toKeyNamePair();
        }
        if (pp != null && pp.getKey() > 0) {
            return this.cmd_saveOrder(pp.getKey(), trx);
        }
        listitem = this.invoiceField.getSelectedItem();
        pp = null;
        if (listitem != null) {
            pp = listitem.toKeyNamePair();
        }
        if (pp != null && pp.getKey() > 0) {
            return this.cmd_saveInvoice(pp.getKey(), trx);
        }
        listitem = this.projectField.getSelectedItem();
        pp = null;
        if (listitem != null) {
            pp = listitem.toKeyNamePair();
        }
        if (pp != null && pp.getKey() > 0) {
            return this.cmd_saveProject(pp.getKey(), trx);
        }
        listitem = this.projectPhaseField.getSelectedItem();
        pp = null;
        if (listitem != null) {
            pp = listitem.toKeyNamePair();
        }
        if (pp != null && pp.getKey() > 0) {
            return this.cmd_saveProjectPhase(pp.getKey(), trx);
        }
        listitem = this.projectTaskField.getSelectedItem();
        pp = null;
        if (listitem != null) {
            pp = listitem.toKeyNamePair();
        }
        if (pp != null && pp.getKey() > 0) {
            return this.cmd_saveProjectTask(pp.getKey(), trx);
        }
        WBOMDropConfigurator.log.log(Level.SEVERE, "Nothing selected");
        return false;
    }
    
    protected void travellerTreenode(final Tree tree, final ISupportRadioNode nodeModel, final boolean isRootNode, final Callback<TreeItemData> processNode) {
        if (!isRootNode && !nodeModel.isChecked()) {
            return;
        }
        final ProductBOMTreeNode node = (ProductBOMTreeNode)nodeModel;
        final int[] nodePath = tree.getModel().getPath((Object)nodeModel);
        final Treeitem treeItem = tree.renderItemByPath(nodePath);
        if (node.getChildCount() == 0) {
            processNode.onCallback(new TreeItemData(nodeModel, (WNumberEditor)treeItem.getAttribute("qty_component")));
        }
        for (int i = 0; i < nodeModel.getChildCount(); ++i) {
            final ISupportRadioNode childNode = nodeModel.getChild(i);
            this.travellerTreenode(tree, childNode, false, processNode);
        }
    }
    
    private boolean cmd_saveOrder(final int C_Order_ID, final Trx trx) {
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("C_Order_ID=" + C_Order_ID);
        }
        final MOrder order = new MOrder(Env.getCtx(), C_Order_ID, (trx != null) ? trx.getTrxName() : null);
        if (order.get_ID() == 0) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Not found - C_Order_ID=" + C_Order_ID);
            return false;
        }
        this.lineCount = 0;
        try {
            final ISupportRadioNode productRootNode = (ISupportRadioNode)this.testProductBOMTree.getModel().getRoot();
            this.travellerTreenode(this.testProductBOMTree, productRootNode, true, (Callback<TreeItemData>)new Callback<TreeItemData>() {
                public void onCallback(final TreeItemData itemData) {
                    final ProductBOMTreeNode productNode = (ProductBOMTreeNode)itemData.dataNode;
                    final BigDecimal qty = productNode.getTotQty();
                    final int M_Product_ID = productNode.getProductID();
                    final MOrderLine ol = new MOrderLine(order);
                    ol.setM_Product_ID(M_Product_ID, true);
                    ol.setQty(qty);
                    ol.setPrice();
                    ol.setTax();
                    ol.saveEx(trx.getTrxName());
                    final WBOMDropConfigurator this$0 = WBOMDropConfigurator.this;
                    ++this$0.lineCount;
                }
            });
            order.saveEx(trx.getTrxName());
        }
        catch (Exception e) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Line not saved");
            if (trx != null) {
                trx.rollback();
            }
            throw new AdempiereException(e.getMessage());
        }
        FDialog.info(-1, (Component)this, String.valueOf(Msg.translate(Env.getCtx(), "C_Order_ID")) + " : " + order.getDocumentInfo() + " , " + Msg.translate(Env.getCtx(), "NoOfLines") + " " + Msg.translate(Env.getCtx(), "Inserted") + " = " + this.lineCount);
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("#" + this.lineCount);
        }
        return true;
    }
    
    private boolean cmd_saveInvoice(final int C_Invoice_ID, final Trx trx) {
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("C_Invoice_ID=" + C_Invoice_ID);
        }
        final MInvoice invoice = new MInvoice(Env.getCtx(), C_Invoice_ID, (trx != null) ? trx.getTrxName() : null);
        if (invoice.get_ID() == 0) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Not found - C_Invoice_ID=" + C_Invoice_ID);
            return false;
        }
        this.lineCount = 0;
        try {
            final ISupportRadioNode productRootNode = (ISupportRadioNode)this.testProductBOMTree.getModel().getRoot();
            this.travellerTreenode(this.testProductBOMTree, productRootNode, true, (Callback<TreeItemData>)new Callback<TreeItemData>() {
                public void onCallback(final TreeItemData itemData) {
                    final ProductBOMTreeNode productNode = (ProductBOMTreeNode)itemData.dataNode;
                    final BigDecimal qty = productNode.getTotQty();
                    final int M_Product_ID = productNode.getProductID();
                    final MInvoiceLine il = new MInvoiceLine(invoice);
                    il.setM_Product_ID(M_Product_ID, true);
                    il.setQty(qty);
                    il.setPrice();
                    il.setTax();
                    il.saveEx(trx.getTrxName());
                    final WBOMDropConfigurator this$0 = WBOMDropConfigurator.this;
                    ++this$0.lineCount;
                }
            });
            invoice.save(trx.getTrxName());
        }
        catch (Exception e) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Line not saved");
            if (trx != null) {
                trx.rollback();
            }
            throw new AdempiereException(e.getMessage());
        }
        FDialog.info(-1, (Component)this, String.valueOf(Msg.translate(Env.getCtx(), "C_Invoice_ID")) + " : " + invoice.getDocumentInfo() + " , " + Msg.translate(Env.getCtx(), "NoOfLines") + " " + Msg.translate(Env.getCtx(), "Inserted") + " = " + this.lineCount);
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("#" + this.lineCount);
        }
        return true;
    }
    
    private boolean cmd_saveProject(final int C_Project_ID, final Trx trx) {
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("C_Project_ID=" + C_Project_ID);
        }
        final MProject project = new MProject(Env.getCtx(), C_Project_ID, (trx != null) ? trx.getTrxName() : null);
        if (project.get_ID() == 0) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Not found - C_Project_ID=" + C_Project_ID);
            return false;
        }
        this.lineCount = 0;
        try {
            final ISupportRadioNode productRootNode = (ISupportRadioNode)this.testProductBOMTree.getModel().getRoot();
            this.travellerTreenode(this.testProductBOMTree, productRootNode, true, (Callback<TreeItemData>)new Callback<TreeItemData>() {
                public void onCallback(final TreeItemData itemData) {
                    final ProductBOMTreeNode productNode = (ProductBOMTreeNode)itemData.dataNode;
                    final BigDecimal qty = productNode.getTotQty();
                    final int M_Product_ID = productNode.getProductID();
                    final MProjectLine pl = new MProjectLine(project);
                    pl.setM_Product_ID(M_Product_ID);
                    pl.setPlannedQty(qty);
                    pl.setPlannedPrice(WBOMDropConfigurator.this.getStandardPrice(M_Product_ID, qty.doubleValue(), project));
                    pl.saveEx(trx.getTrxName());
                    final WBOMDropConfigurator this$0 = WBOMDropConfigurator.this;
                    ++this$0.lineCount;
                }
            });
            project.saveEx(trx.getTrxName());
            project.load(trx.getTrxName(), new String[0]);
        }
        catch (Exception e) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Line not saved");
            if (trx != null) {
                trx.rollback();
            }
            throw new AdempiereException(e.getMessage());
        }
        FDialog.info(-1, (Component)this, String.valueOf(Msg.translate(Env.getCtx(), "C_Project_ID")) + " : " + project.getName() + " , " + Msg.translate(Env.getCtx(), "NoOfLines") + " " + Msg.translate(Env.getCtx(), "Inserted") + " = " + this.lineCount);
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("#" + this.lineCount);
        }
        return true;
    }
    
    private boolean cmd_saveProjectPhase(final int C_ProjectPhase_ID, final Trx trx) {
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("C_ProjectPhase_ID=" + C_ProjectPhase_ID);
        }
        final MProjectPhase phase = new MProjectPhase(Env.getCtx(), C_ProjectPhase_ID, (trx != null) ? trx.getTrxName() : null);
        if (phase.get_ID() == 0) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Not found - C_ProjectPhase_ID=" + C_ProjectPhase_ID);
            return false;
        }
        final MProject project = (MProject)new Query(Env.getCtx(), "C_Project", "C_Project_ID=?", phase.get_TrxName()).setParameters(new Object[] { phase.getC_Project_ID() }).first();
        this.lineCount = 0;
        try {
            final ISupportRadioNode productRootNode = (ISupportRadioNode)this.testProductBOMTree.getModel().getRoot();
            this.travellerTreenode(this.testProductBOMTree, productRootNode, true, (Callback<TreeItemData>)new Callback<TreeItemData>() {
                public void onCallback(final TreeItemData itemData) {
                    final ProductBOMTreeNode productNode = (ProductBOMTreeNode)itemData.dataNode;
                    final BigDecimal qty = productNode.getTotQty();
                    final int M_Product_ID = productNode.getProductID();
                    final MProjectLine pl = new MProjectLine(project);
                    pl.setM_Product_ID(M_Product_ID);
                    pl.setPlannedQty(qty);
                    pl.setPlannedPrice(WBOMDropConfigurator.this.getStandardPrice(M_Product_ID, qty.doubleValue(), project));
                    pl.saveEx(trx.getTrxName());
                    final WBOMDropConfigurator this$0 = WBOMDropConfigurator.this;
                    ++this$0.lineCount;
                }
            });
            project.saveEx(trx.getTrxName());
            project.load(trx.getTrxName(), new String[0]);
        }
        catch (Exception e) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Line not saved");
            if (trx != null) {
                trx.rollback();
            }
            throw new AdempiereException(e.getMessage());
        }
        FDialog.info(-1, (Component)this, String.valueOf(Msg.translate(Env.getCtx(), "C_Project_ID")) + " : " + project.getName() + " , " + Msg.translate(Env.getCtx(), "NoOfLines") + " " + Msg.translate(Env.getCtx(), "Inserted") + " = " + this.lineCount);
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("#" + this.lineCount);
        }
        return true;
    }
    
    private boolean cmd_saveProjectTask(final int C_ProjectTask_ID, final Trx trx) {
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("C_ProjectTask_ID=" + C_ProjectTask_ID);
        }
        final MProjectTask task = new MProjectTask(Env.getCtx(), C_ProjectTask_ID, (trx != null) ? trx.getTrxName() : null);
        if (task.get_ID() == 0) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Not found - C_ProjectTask_ID=" + C_ProjectTask_ID);
            return false;
        }
        final MProjectPhase phase = (MProjectPhase)new Query(Env.getCtx(), "C_ProjectPhase", "C_ProjectPhase_ID=?", task.get_TrxName()).setParameters(new Object[] { task.getC_ProjectPhase_ID() }).first();
        final MProject project = (MProject)new Query(Env.getCtx(), "C_Project", "C_Project_ID=?", task.get_TrxName()).setParameters(new Object[] { phase.getC_Project_ID() }).first();
        this.lineCount = 0;
        try {
            final ISupportRadioNode productRootNode = (ISupportRadioNode)this.testProductBOMTree.getModel().getRoot();
            this.travellerTreenode(this.testProductBOMTree, productRootNode, true, (Callback<TreeItemData>)new Callback<TreeItemData>() {
                public void onCallback(final TreeItemData itemData) {
                    final ProductBOMTreeNode productNode = (ProductBOMTreeNode)itemData.dataNode;
                    final BigDecimal qty = productNode.getTotQty();
                    final int M_Product_ID = productNode.getProductID();
                    final MProjectLine pl = new MProjectLine(project);
                    pl.setM_Product_ID(M_Product_ID);
                    pl.setPlannedQty(qty);
                    pl.setPlannedPrice(WBOMDropConfigurator.this.getStandardPrice(M_Product_ID, qty.doubleValue(), project));
                    pl.saveEx(trx.getTrxName());
                    final WBOMDropConfigurator this$0 = WBOMDropConfigurator.this;
                    ++this$0.lineCount;
                }
            });
            project.saveEx(trx.getTrxName());
            project.load(trx.getTrxName(), new String[0]);
        }
        catch (Exception e) {
            WBOMDropConfigurator.log.log(Level.SEVERE, "Line not saved");
            if (trx != null) {
                trx.rollback();
            }
            throw new AdempiereException(e.getMessage());
        }
        FDialog.info(-1, (Component)this, String.valueOf(Msg.translate(Env.getCtx(), "C_Project_ID")) + " : " + project.getName() + " , " + Msg.translate(Env.getCtx(), "NoOfLines") + " " + Msg.translate(Env.getCtx(), "Inserted") + " = " + this.lineCount);
        if (WBOMDropConfigurator.log.isLoggable(Level.CONFIG)) {
            WBOMDropConfigurator.log.config("#" + this.lineCount);
        }
        return true;
    }
    
    private BigDecimal getStandardPrice(final int M_Product_ID, final Double plannedQty, final MProject project) {
        final MProductPricing pp = new MProductPricing(M_Product_ID, project.getC_BPartner_ID(), new BigDecimal(plannedQty), true);
        pp.setM_PriceList_ID(project.getM_PriceList_ID());
        if (pp.calculatePrice()) {
            return pp.getPriceStd();
        }
        return Env.ZERO;
    }
    
    class TreeItemData
    {
        ISupportRadioNode dataNode;
        WNumberEditor inputQty;
        
        public TreeItemData(final ISupportRadioNode dataNode, final WNumberEditor inputQty) {
            this.dataNode = dataNode;
            this.inputQty = inputQty;
        }
    }
}
