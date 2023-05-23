// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.compiere.util.DB;
import org.libero.model.MPPCostCollector;
import org.compiere.util.Trx;
import java.util.Iterator;
import org.libero.model.MPPOrderWorkflow;
import org.libero.tables.I_PP_Order_Node;
import org.libero.model.MPPOrderNode;
import org.compiere.util.TrxRunnable;
import org.compiere.util.KeyNamePair;
import org.compiere.minigrid.IDColumn;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.session.SessionManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.MDocType;
import org.compiere.model.MProduct;
import org.adempiere.webui.event.ValueChangeEvent;
import org.zkoss.zul.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.compiere.minigrid.IMiniTable;
import org.zkoss.zul.Row;
import org.adempiere.webui.component.Rows;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabs;
import org.zkoss.zul.Space;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Center;
import org.compiere.model.MLookup;
import java.util.Properties;
import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.MTab;
import org.compiere.model.MWindow;
import org.compiere.model.MLocatorLookup;
import org.compiere.model.Lookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MColumn;
import org.compiere.util.Language;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.component.ListboxFactory;
import org.compiere.util.Msg;
import org.compiere.util.Env;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.editor.WLocatorEditor;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.WPAttributeEditor;
import org.adempiere.webui.component.Grid;
import org.zkoss.zul.Html;
import org.adempiere.webui.component.Tabbox;
import org.zkoss.zul.Borderlayout;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Panel;
import org.libero.model.MPPOrder;
import org.adempiere.webui.event.WTableModelListener;
import java.io.Serializable;
import org.adempiere.webui.event.ValueChangeListener;
import org.zkoss.zk.ui.event.EventListener;
import org.adempiere.webui.panel.IFormController;

public class WOrderReceiptIssue extends OrderReceiptIssue implements IFormController, EventListener, ValueChangeListener, Serializable, WTableModelListener
{
    private static final long serialVersionUID = -3451096834043054791L;
    private int m_WindowNo;
    private String m_sql;
    private MPPOrder m_PP_order;
    private Panel Generate;
    private Panel PanelBottom;
    private Panel mainPanel;
    private Panel northPanel;
    private Button Process;
    private Label attributeLabel;
    private Label orderedQtyLabel;
    private Label deliveredQtyLabel;
    private Label openQtyLabel;
    private Label orderLabel;
    private Label toDeliverQtyLabel;
    private Label movementDateLabel;
    private Label rejectQtyLabel;
    private Label resourceLabel;
    private Label DescriptionLabel;
    private CustomForm form;
    private Borderlayout ReceiptIssueOrder;
    private Tabbox TabsReceiptsIssue;
    private Html info;
    private Grid fieldGrid;
    private WPAttributeEditor attribute;
    private Label warehouseLabel;
    private Label scrapQtyLabel;
    private Label productLabel;
    private Label uomLabel;
    private Label uomorderLabel;
    private Label locatorLabel;
    private Label backflushGroupLabel;
    private Label labelcombo;
    private Label QtyBatchsLabel;
    private Label QtyBatchSizeLabel;
    private Textbox backflushGroup;
    private Textbox description;
    private WNumberEditor orderedQtyField;
    private WNumberEditor deliveredQtyField;
    private WNumberEditor openQtyField;
    private WNumberEditor toDeliverQty;
    private WNumberEditor rejectQty;
    private WNumberEditor scrapQtyField;
    private WNumberEditor qtyBatchsField;
    private WNumberEditor qtyBatchSizeField;
    private WSearchEditor orderField;
    private WSearchEditor resourceField;
    private WSearchEditor warehouseField;
    private WSearchEditor productField;
    private WSearchEditor uomField;
    private WSearchEditor uomorderField;
    private WListbox issue;
    private WDateEditor movementDateField;
    private WLocatorEditor locatorField;
    private Combobox pickcombo;
    
    public WOrderReceiptIssue() {
        this.m_WindowNo = 0;
        this.m_PP_order = null;
        this.Generate = new Panel();
        this.PanelBottom = new Panel();
        this.mainPanel = new Panel();
        this.northPanel = new Panel();
        this.Process = new Button();
        this.attributeLabel = new Label();
        this.orderedQtyLabel = new Label();
        this.deliveredQtyLabel = new Label();
        this.openQtyLabel = new Label();
        this.orderLabel = new Label();
        this.toDeliverQtyLabel = new Label();
        this.movementDateLabel = new Label();
        this.rejectQtyLabel = new Label();
        this.resourceLabel = new Label();
        this.DescriptionLabel = new Label();
        this.form = new CustomForm();
        this.ReceiptIssueOrder = new Borderlayout();
        this.TabsReceiptsIssue = new Tabbox();
        this.info = new Html();
        this.fieldGrid = GridFactory.newGridLayout();
        this.attribute = null;
        this.warehouseLabel = new Label();
        this.scrapQtyLabel = new Label();
        this.productLabel = new Label(Msg.translate(Env.getCtx(), "M_Product_ID"));
        this.uomLabel = new Label(Msg.translate(Env.getCtx(), "C_UOM_ID"));
        this.uomorderLabel = new Label(Msg.translate(Env.getCtx(), "Altert UOM"));
        this.locatorLabel = new Label(Msg.translate(Env.getCtx(), "M_Locator_ID"));
        this.backflushGroupLabel = new Label(Msg.translate(Env.getCtx(), "BackflushGroup"));
        this.labelcombo = new Label(Msg.translate(Env.getCtx(), "DeliveryRule"));
        this.QtyBatchsLabel = new Label();
        this.QtyBatchSizeLabel = new Label();
        this.backflushGroup = new Textbox();
        this.description = new Textbox();
        this.orderedQtyField = new WNumberEditor("QtyOrdered", false, false, false, 29, "QtyOrdered");
        this.deliveredQtyField = new WNumberEditor("QtyDelivered", false, false, false, 29, "QtyDelivered");
        this.openQtyField = new WNumberEditor("QtyOpen", false, false, false, 29, "QtyOpen");
        this.toDeliverQty = new WNumberEditor("QtyToDeliver", true, false, true, 29, "QtyToDeliver");
        this.rejectQty = new WNumberEditor("Qtyreject", false, false, true, 29, "QtyReject");
        this.scrapQtyField = new WNumberEditor("Qtyscrap", false, false, true, 29, "Qtyscrap");
        this.qtyBatchsField = new WNumberEditor("QtyBatchs", false, false, false, 29, "QtyBatchs");
        this.qtyBatchSizeField = new WNumberEditor("QtyBatchSize", false, false, false, 29, "QtyBatchSize");
        this.orderField = null;
        this.resourceField = null;
        this.warehouseField = null;
        this.productField = null;
        this.uomField = null;
        this.uomorderField = null;
        this.issue = ListboxFactory.newDataTable();
        this.movementDateField = new WDateEditor("MovementDate", true, false, true, "MovementDate");
        this.locatorField = null;
        this.pickcombo = new Combobox();
        Env.setContext(Env.getCtx(), this.form.getWindowNo(), "IsSOTrx", "Y");
        try {
            this.fillPicks();
            this.jbInit();
            this.dynInit();
            this.pickcombo.addEventListener("onChange", (EventListener)this);
        }
        catch (Exception e) {
            throw new AdempiereException((Throwable)e);
        }
    }
    
    private void fillPicks() throws Exception {
        final Properties ctx = Env.getCtx();
        final Language language = Language.getLoginLanguage();
        final MLookup orderLookup = MLookupFactory.get(ctx, this.m_WindowNo, MColumn.getColumn_ID("PP_Order", "PP_Order_ID"), 30, language, "PP_Order_ID", 0, false, "PP_Order.DocStatus = 'CO'");
        (this.orderField = new WSearchEditor("PP_Order_ID", false, false, true, (Lookup)orderLookup)).addValueChangeListener((ValueChangeListener)this);
        final MLookup resourceLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, MColumn.getColumn_ID("PP_Order", "S_Resource_ID"), 19);
        this.resourceField = new WSearchEditor("S_Resource_ID", false, false, false, (Lookup)resourceLookup);
        final MLookup warehouseLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, MColumn.getColumn_ID("PP_Order", "M_Warehouse_ID"), 19);
        this.warehouseField = new WSearchEditor("M_Warehouse_ID", false, false, false, (Lookup)warehouseLookup);
        final MLookup productLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, MColumn.getColumn_ID("PP_Order", "M_Product_ID"), 19);
        this.productField = new WSearchEditor("M_Product_ID", false, false, false, (Lookup)productLookup);
        final MLookup uomLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, MColumn.getColumn_ID("PP_Order", "C_UOM_ID"), 19);
        this.uomField = new WSearchEditor("C_UOM_ID", false, false, false, (Lookup)uomLookup);
        final MLookup uomOrderLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, MColumn.getColumn_ID("PP_Order", "C_UOM_ID"), 19);
        this.uomorderField = new WSearchEditor("C_UOM_ID", false, false, false, (Lookup)uomOrderLookup);
        final MLocatorLookup locatorL = new MLocatorLookup(ctx, this.m_WindowNo);
        this.locatorField = new WLocatorEditor("M_Locator_ID", true, false, true, locatorL, this.m_WindowNo);
        final int m_Window = MWindow.getWindow_ID("Manufacturing Order");
        final GridFieldVO vo = GridFieldVO.createStdField(ctx, this.m_WindowNo, 0, m_Window, MTab.getTab_ID(m_Window, "Manufacturing Order"), false, false, false);
        vo.AD_Column_ID = MColumn.getColumn_ID("PP_Order", "M_AttributeSetInstance_ID");
        vo.ColumnName = "M_AttributeSetInstance_ID";
        vo.displayType = 35;
        final GridField field = new GridField(vo);
        (this.attribute = new WPAttributeEditor(field.getGridTab(), field)).setValue((Object)0);
        this.scrapQtyField.setValue((Object)Env.ZERO);
        this.rejectQty.setValue((Object)Env.ZERO);
        this.pickcombo.appendItem(Msg.translate(Env.getCtx(), "IsBackflush"), (Object)1);
        this.pickcombo.appendItem(Msg.translate(Env.getCtx(), "OnlyIssue"), (Object)2);
        this.pickcombo.appendItem(Msg.translate(Env.getCtx(), "OnlyReceipt"), (Object)3);
        this.pickcombo.addEventListener("onChange", (EventListener)this);
        this.Process.addActionListener((EventListener)this);
        this.toDeliverQty.addValueChangeListener((ValueChangeListener)this);
        this.scrapQtyField.addValueChangeListener((ValueChangeListener)this);
    }
    
    private void jbInit() throws Exception {
        final Center center = new Center();
        final South south = new South();
        final North north = new North();
        this.form.appendChild((Component)this.mainPanel);
        this.mainPanel.appendChild((Component)this.TabsReceiptsIssue);
        this.mainPanel.setStyle("width: 100%; height: 100%; padding: 0; margin: 0");
        this.ReceiptIssueOrder.setWidth("100%");
        this.ReceiptIssueOrder.setHeight("99%");
        this.ReceiptIssueOrder.appendChild((Component)north);
        this.description.setWidth("100%");
        north.appendChild((Component)this.northPanel);
        this.northPanel.appendChild((Component)this.fieldGrid);
        this.orderLabel.setText(Msg.translate(Env.getCtx(), "PP_Order_ID"));
        final Rows tmpRows = this.fieldGrid.newRows();
        Row tmpRow = (Row)tmpRows.newRow();
        tmpRow.appendChild(this.orderLabel.rightAlign());
        tmpRow.appendChild((Component)this.orderField.getComponent());
        this.resourceLabel.setText(Msg.translate(Env.getCtx(), "S_Resource_ID"));
        tmpRow.appendChild(this.resourceLabel.rightAlign());
        tmpRow.appendChild((Component)this.resourceField.getComponent());
        this.warehouseLabel.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
        tmpRow.appendChild(this.warehouseLabel.rightAlign());
        tmpRow.appendChild((Component)this.warehouseField.getComponent());
        tmpRow = (Row)tmpRows.newRow();
        tmpRow.appendChild(this.productLabel.rightAlign());
        tmpRow.appendChild((Component)this.productField.getComponent());
        tmpRow.appendChild(this.uomLabel.rightAlign());
        tmpRow.appendChild((Component)this.uomField.getComponent());
        tmpRow.appendChild(this.uomorderLabel.rightAlign());
        tmpRow.appendChild((Component)this.uomorderField.getComponent());
        tmpRow = (Row)tmpRows.newRow();
        this.orderedQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyOrdered"));
        tmpRow.appendChild(this.orderedQtyLabel.rightAlign());
        tmpRow.appendChild((Component)this.orderedQtyField.getComponent());
        this.deliveredQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyDelivered"));
        tmpRow.appendChild(this.deliveredQtyLabel.rightAlign());
        tmpRow.appendChild((Component)this.deliveredQtyField.getComponent());
        this.openQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyOpen"));
        tmpRow.appendChild(this.openQtyLabel.rightAlign());
        tmpRow.appendChild((Component)this.openQtyField.getComponent());
        tmpRow = (Row)tmpRows.newRow();
        tmpRow.appendChild(this.productLabel.rightAlign());
        tmpRow.appendChild((Component)this.productField.getComponent());
        tmpRow.appendChild(this.uomLabel.rightAlign());
        tmpRow.appendChild((Component)this.uomField.getComponent());
        tmpRow.appendChild(this.uomorderLabel.rightAlign());
        tmpRow.appendChild((Component)this.uomorderField.getComponent());
        tmpRow = (Row)tmpRows.newRow();
        this.QtyBatchsLabel.setText(Msg.translate(Env.getCtx(), "QtyBatchs"));
        tmpRow.appendChild(this.QtyBatchsLabel.rightAlign());
        tmpRow.appendChild((Component)this.qtyBatchsField.getComponent());
        this.QtyBatchSizeLabel.setText(Msg.translate(Env.getCtx(), "QtyBatchSize"));
        tmpRow.appendChild(this.QtyBatchSizeLabel.rightAlign());
        tmpRow.appendChild((Component)this.qtyBatchSizeField.getComponent());
        this.openQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyOpen"));
        tmpRow.appendChild(this.openQtyLabel.rightAlign());
        tmpRow.appendChild((Component)this.openQtyField.getComponent());
        tmpRow = (Row)tmpRows.newRow();
        tmpRow.appendChild(this.labelcombo.rightAlign());
        tmpRow.appendChild((Component)this.pickcombo);
        tmpRow.appendChild(this.backflushGroupLabel.rightAlign());
        tmpRow.appendChild((Component)this.backflushGroup);
        tmpRow.appendChild((Component)new Space());
        tmpRow.appendChild((Component)new Space());
        tmpRow = (Row)tmpRows.newRow();
        this.toDeliverQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyToDeliver"));
        tmpRow.appendChild(this.toDeliverQtyLabel.rightAlign());
        tmpRow.appendChild((Component)this.toDeliverQty.getComponent());
        this.scrapQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyScrap"));
        tmpRow.appendChild(this.scrapQtyLabel.rightAlign());
        tmpRow.appendChild((Component)this.scrapQtyField.getComponent());
        this.rejectQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyReject"));
        tmpRow.appendChild(this.rejectQtyLabel.rightAlign());
        tmpRow.appendChild((Component)this.rejectQty.getComponent());
        tmpRow = (Row)tmpRows.newRow();
        this.movementDateLabel.setText(Msg.translate(Env.getCtx(), "MovementDate"));
        tmpRow.appendChild(this.movementDateLabel.rightAlign());
        tmpRow.appendChild((Component)this.movementDateField.getComponent());
        this.locatorLabel.setText(Msg.translate(Env.getCtx(), "M_Locator_ID"));
        tmpRow.appendChild(this.locatorLabel.rightAlign());
        tmpRow.appendChild((Component)this.locatorField.getComponent());
        this.attributeLabel.setText(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
        tmpRow.appendChild(this.attributeLabel.rightAlign());
        tmpRow.appendChild((Component)this.attribute.getComponent());
        this.ReceiptIssueOrder.appendChild((Component)center);
        center.appendChild((Component)this.issue);
        this.ReceiptIssueOrder.appendChild((Component)south);
        south.appendChild((Component)this.PanelBottom);
        tmpRow = (Row)tmpRows.newRow();
        this.DescriptionLabel.setText(Msg.translate(Env.getCtx(), "Description"));
        tmpRow.appendChild(this.DescriptionLabel.rightAlign());
        tmpRow.appendChild((Component)this.description);
        this.Process.setLabel(Msg.translate(Env.getCtx(), "OK"));
        this.PanelBottom.appendChild((Component)this.Process);
        this.PanelBottom.setWidth("100%");
        this.PanelBottom.setStyle("text-align:center");
        final Tabs tabs = new Tabs();
        final Tab tab1 = new Tab();
        final Tab tab2 = new Tab();
        tab1.setLabel(Msg.translate(Env.getCtx(), "IsShipConfirm"));
        tab2.setLabel(Msg.translate(Env.getCtx(), "Generate"));
        tabs.appendChild((Component)tab1);
        tabs.appendChild((Component)tab2);
        this.TabsReceiptsIssue.appendChild((Component)tabs);
        final Tabpanels tabps = new Tabpanels();
        final Tabpanel tabp1 = new Tabpanel();
        final Tabpanel tabp2 = new Tabpanel();
        this.TabsReceiptsIssue.appendChild((Component)tabps);
        this.TabsReceiptsIssue.setWidth("100%");
        this.TabsReceiptsIssue.setHeight("100%");
        tabps.appendChild((Component)tabp1);
        tabps.appendChild((Component)tabp2);
        tabp1.appendChild((Component)this.ReceiptIssueOrder);
        tabp1.setWidth("100%");
        tabp1.setHeight("100%");
        tabp2.appendChild((Component)this.Generate);
        tabp2.setWidth("100%");
        tabp2.setHeight("100%");
        this.Generate.appendChild((Component)this.info);
        this.Generate.setVisible(true);
        this.info.setVisible(true);
        this.TabsReceiptsIssue.addEventListener("onChange", (EventListener)this);
    }
    
    public void dynInit() {
        this.disableToDeliver();
        this.prepareTable((IMiniTable)this.issue);
        this.issue.autoSize();
        this.issue.getModel().addTableModelListener((WTableModelListener)this);
        this.issue.setRowCount(0);
    }
    
    public void prepareTable(final IMiniTable miniTable) {
        this.configureMiniTable(miniTable);
    }
    
    public void onEvent(final Event e) throws Exception {
        if (e.getName().equals("onCancel")) {
            this.dispose();
            return;
        }
        if (e.getTarget().equals(this.Process)) {
            if (this.getMovementDate() == null) {
                Messagebox.show(Msg.getMsg(Env.getCtx(), "NoDate"), "Info", 1, "z-messagebox-icon z-messagebox-information");
                return;
            }
            if ((this.isOnlyReceipt() || this.isBackflush()) && this.getM_Locator_ID() <= 0) {
                Messagebox.show(Msg.getMsg(Env.getCtx(), "NoLocator"), "Info", 1, "z-messagebox-icon z-messagebox-information");
                return;
            }
            this.TabsReceiptsIssue.setSelectedIndex(1);
            this.generateSummaryTable();
            int result = -1;
            result = Messagebox.show(Msg.getMsg(Env.getCtx(), "Update"), "", 3, "z-messagebox-icon z-messagebox-question");
            if (result == 1) {
                final boolean isCloseDocument = Messagebox.show(Msg.parseTranslation(Env.getCtx(), "@IsCloseDocument@ : &&&&" + this.getPP_Order().getDocumentNo()), "", 3, "z-messagebox-icon z-messagebox-question") == 1;
                if (this.cmd_process(isCloseDocument, (IMiniTable)this.issue)) {
                    this.dispose();
                    return;
                }
            }
            this.TabsReceiptsIssue.setSelectedIndex(0);
        }
        if (e.getTarget().equals(this.pickcombo)) {
            if (this.isOnlyReceipt()) {
                this.enableToDeliver();
                this.locatorLabel.setVisible(true);
                this.locatorField.setVisible(true);
                this.attribute.setVisible(true);
                this.attributeLabel.setVisible(true);
                this.issue.setVisible(false);
            }
            else if (this.isOnlyIssue()) {
                this.disableToDeliver();
                this.locatorLabel.setVisible(false);
                this.locatorField.setVisible(false);
                this.attribute.setVisible(false);
                this.attributeLabel.setVisible(false);
                this.issue.setVisible(true);
                this.executeQuery();
            }
            else if (this.isBackflush()) {
                this.enableToDeliver();
                this.locatorLabel.setVisible(true);
                this.locatorField.setVisible(true);
                this.attribute.setVisible(true);
                this.attributeLabel.setVisible(true);
                this.issue.setVisible(true);
                this.executeQuery();
            }
            this.setToDeliverQty(this.getOpenQty());
        }
    }
    
    public void enableToDeliver() {
        this.setToDeliver(true);
    }
    
    public void disableToDeliver() {
        this.setToDeliver(false);
    }
    
    private void setToDeliver(final Boolean state) {
        this.toDeliverQty.getComponent().setEnabled((boolean)state);
        this.scrapQtyLabel.setVisible((boolean)state);
        this.scrapQtyField.setVisible((boolean)state);
        this.rejectQtyLabel.setVisible((boolean)state);
        this.rejectQty.setVisible((boolean)state);
    }
    
    public void executeQuery() {
        this.m_sql = String.valueOf(this.m_sql) + " ORDER BY obl." + "Line";
        this.issue.clearTable();
        this.executeQuery((IMiniTable)this.issue);
        this.issue.repaint();
    }
    
    public void valueChange(final ValueChangeEvent e) {
        final String name = e.getPropertyName();
        final Object value = e.getNewValue();
        if (value == null) {
            return;
        }
        if (name.equals("PP_Order_ID")) {
            this.orderField.setValue(value);
            final MPPOrder pp_order = this.getPP_Order();
            if (pp_order != null) {
                this.setS_Resource_ID(pp_order.getS_Resource_ID());
                this.setM_Warehouse_ID(pp_order.getM_Warehouse_ID());
                this.setDeliveredQty(pp_order.getQtyDelivered());
                this.setOrderedQty(pp_order.getQtyOrdered());
                this.setQtyBatchs(pp_order.getQtyBatchs());
                this.setQtyBatchSize(pp_order.getQtyBatchSize());
                this.setOpenQty(pp_order.getQtyOrdered().subtract(pp_order.getQtyDelivered()));
                this.setToDeliverQty(this.getOpenQty());
                this.setM_Product_ID(pp_order.getM_Product_ID());
                final MProduct m_product = MProduct.get(Env.getCtx(), pp_order.getM_Product_ID());
                this.setC_UOM_ID(m_product.getC_UOM_ID());
                this.setOrder_UOM_ID(pp_order.getC_UOM_ID());
                this.setM_AttributeSetInstance_ID(pp_order.getM_Product().getM_AttributeSetInstance_ID());
                final String docBaseType = MDocType.get(pp_order.getCtx(), pp_order.getC_DocType_ID()).getDocBaseType();
                if (docBaseType.equals("MOF")) {
                    this.pickcombo.setSelectedIndex(1);
                }
                else {
                    this.pickcombo.setSelectedIndex(0);
                }
                final Event ev = new Event("onChange", (Component)this.pickcombo);
                try {
                    this.onEvent(ev);
                }
                catch (Exception e2) {
                    throw new AdempiereException((Throwable)e2);
                }
            }
        }
        if ((name.equals(this.toDeliverQty.getColumnName()) || name.equals(this.scrapQtyField.getColumnName())) && this.getPP_Order_ID() > 0 && this.isBackflush()) {
            this.executeQuery();
        }
    }
    
    @Override
    public void showMessage(final String message, final boolean error) {
        try {
            if (!error) {
                Messagebox.show(message, "Info", 1, "z-messagebox-icon z-messagebox-information");
            }
            else {
                Messagebox.show(message, "", 1, "z-messagebox-icon z-messagebox-error");
            }
        }
        catch (Exception ex) {}
    }
    
    private void generateSummaryTable() {
        this.info.setContent(this.generateSummaryTable((IMiniTable)this.issue, this.productField.getDisplay(), this.uomField.getDisplay(), this.attribute.getDisplay(), this.toDeliverQty.getDisplay(), this.deliveredQtyField.getDisplay(), this.scrapQtyField.getDisplay(), this.isBackflush(), this.isOnlyIssue(), this.isOnlyReceipt()));
    }
    
    @Override
    protected boolean isOnlyReceipt() {
        super.setIsOnlyReceipt(this.pickcombo.getText().equals("OnlyReceipt"));
        return super.isOnlyReceipt();
    }
    
    @Override
    protected boolean isOnlyIssue() {
        super.setIsOnlyIssue(this.pickcombo.getText().equals("OnlyIssue"));
        return super.isOnlyIssue();
    }
    
    @Override
    protected boolean isBackflush() {
        super.setIsBackflush(this.pickcombo.getText().equals("IsBackflush"));
        return super.isBackflush();
    }
    
    @Override
    protected Timestamp getMovementDate() {
        return this.movementDateField.getValue();
    }
    
    @Override
    protected BigDecimal getOrderedQty() {
        final BigDecimal bd = this.orderedQtyField.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected void setOrderedQty(final BigDecimal qty) {
        this.orderedQtyField.setValue((Object)qty);
    }
    
    @Override
    protected BigDecimal getDeliveredQty() {
        final BigDecimal bd = this.deliveredQtyField.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected void setDeliveredQty(final BigDecimal qty) {
        this.deliveredQtyField.setValue((Object)qty);
    }
    
    @Override
    protected BigDecimal getToDeliverQty() {
        final BigDecimal bd = this.toDeliverQty.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected void setToDeliverQty(final BigDecimal qty) {
        this.toDeliverQty.setValue((Object)qty);
    }
    
    @Override
    protected BigDecimal getScrapQty() {
        final BigDecimal bd = this.scrapQtyField.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected BigDecimal getRejectQty() {
        final BigDecimal bd = this.rejectQty.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected BigDecimal getOpenQty() {
        final BigDecimal bd = this.openQtyField.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected void setOpenQty(final BigDecimal qty) {
        this.openQtyField.setValue((Object)qty);
    }
    
    @Override
    protected BigDecimal getQtyBatchs() {
        final BigDecimal bd = this.qtyBatchsField.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected void setQtyBatchs(final BigDecimal qty) {
        this.qtyBatchsField.setValue((Object)qty);
    }
    
    @Override
    protected BigDecimal getQtyBatchSize() {
        final BigDecimal bd = this.qtyBatchSizeField.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    @Override
    protected void setQtyBatchSize(final BigDecimal qty) {
        this.qtyBatchSizeField.setValue((Object)qty);
    }
    
    @Override
    protected int getM_AttributeSetInstance_ID() {
        final Integer ii = (Integer)this.attribute.getValue();
        return (ii != null) ? ii : 0;
    }
    
    @Override
    protected void setM_AttributeSetInstance_ID(final int M_AttributeSetInstance_ID) {
        this.attribute.setValue((Object)M_AttributeSetInstance_ID);
    }
    
    @Override
    protected int getM_Locator_ID() {
        final Integer ii = (Integer)this.locatorField.getValue();
        return (ii != null) ? ii : 0;
    }
    
    @Override
    protected void setM_Locator_ID(final int M_Locator_ID) {
        this.locatorField.setValue((Object)M_Locator_ID);
    }
    
    @Override
    protected int getPP_Order_ID() {
        final Integer ii = (Integer)this.orderField.getValue();
        return (ii != null) ? ii : 0;
    }
    
    @Override
    protected MPPOrder getPP_Order() {
        final int id = this.getPP_Order_ID();
        if (id <= 0) {
            return this.m_PP_order = null;
        }
        if (this.m_PP_order == null || this.m_PP_order.get_ID() != id) {
            this.m_PP_order = new MPPOrder(Env.getCtx(), id, null);
        }
        return this.m_PP_order;
    }
    
    protected int getS_Resource_ID() {
        final Integer ii = (Integer)this.resourceField.getValue();
        return (ii != null) ? ii : 0;
    }
    
    protected void setS_Resource_ID(final int S_Resource_ID) {
        this.resourceField.setValue((Object)S_Resource_ID);
    }
    
    protected int getM_Warehouse_ID() {
        final Integer ii = (Integer)this.warehouseField.getValue();
        return (ii != null) ? ii : 0;
    }
    
    protected void setM_Warehouse_ID(final int M_Warehouse_ID) {
        this.warehouseField.setValue((Object)M_Warehouse_ID);
    }
    
    protected int getM_Product_ID() {
        final Integer ii = (Integer)this.productField.getValue();
        return (ii != null) ? ii : 0;
    }
    
    protected void setM_Product_ID(final int M_Product_ID) {
        this.productField.setValue((Object)M_Product_ID);
        Env.setContext(Env.getCtx(), this.m_WindowNo, "M_Product_ID", M_Product_ID);
    }
    
    protected int getC_UOM_ID() {
        final Integer ii = (Integer)this.uomField.getValue();
        return (ii != null) ? ii : 0;
    }
    
    protected void setC_UOM_ID(final int C_UOM_ID) {
        this.uomField.setValue((Object)C_UOM_ID);
    }
    
    protected int getOrder_UOM_ID() {
        final Integer ii = (Integer)this.uomorderField.getValue();
        return (ii != null) ? ii : 0;
    }
    
    protected void setOrder_UOM_ID(final int C_UOM_ID) {
        this.uomorderField.setValue((Object)C_UOM_ID);
    }
    
    public void dispose() {
        SessionManager.getAppDesktop().closeActiveWindow();
    }
    
    public ADForm getForm() {
        return (ADForm)this.form;
    }
    
    public void tableChanged(final WTableModelEvent event) {
    }
    
    private BigDecimal getValueBigDecimal(final IMiniTable issue, final int row, final int col) {
        final BigDecimal bd = (BigDecimal)issue.getValueAt(row, col);
        return (bd == null) ? Env.ZERO : bd;
    }
    
    public boolean cmd_process(final boolean isCloseDocument, final IMiniTable issue) {
        if ((this.isOnlyReceipt() || this.isBackflush()) && this.getM_Locator_ID() <= 0) {
            this.showMessage(Msg.getMsg(Env.getCtx(), "NoLocator"), false);
        }
        if (this.getPP_Order() == null || this.getMovementDate() == null) {
            return false;
        }
        if (this.description.getValue().equals("")) {
            this.showMessage(Msg.getMsg(Env.getCtx(), "NoDescription"), false);
            return false;
        }
        for (int i = 0; i < issue.getRowCount(); ++i) {
            final IDColumn id = (IDColumn)issue.getValueAt(i, 0);
            final KeyNamePair key = new KeyNamePair((int)id.getRecord_ID(), id.isSelected() ? "Y" : "N");
            final boolean isSelected = key.getName().equals("Y");
            if (isSelected && this.getValueBigDecimal(issue, i, 10).compareTo(this.getValueBigDecimal(issue, i, 8)) < 0 && this.getValueBigDecimal(issue, i, 8).compareTo(BigDecimal.ZERO) > 0) {
                this.showMessage(Msg.getMsg(Env.getCtx(), issue.getValueAt(i, 2) + " Qty Onhand " + this.getValueBigDecimal(issue, i, 10)), false);
                return false;
            }
        }
        try {
            Trx.run((TrxRunnable)new TrxRunnable() {
                public void run(final String trxName) {
                    final MPPOrder order = new MPPOrder(Env.getCtx(), WOrderReceiptIssue.this.getPP_Order_ID(), trxName);
                    if (WOrderReceiptIssue.this.isBackflush() || WOrderReceiptIssue.this.isOnlyIssue()) {
                        WOrderReceiptIssue.this.createIssue(order, issue);
                    }
                    if (WOrderReceiptIssue.this.isOnlyReceipt() || WOrderReceiptIssue.this.isBackflush()) {
                        MPPOrder.createReceipt(order, WOrderReceiptIssue.this.getMovementDate(), WOrderReceiptIssue.this.getDeliveredQty(), WOrderReceiptIssue.this.getToDeliverQty(), WOrderReceiptIssue.this.getScrapQty(), WOrderReceiptIssue.this.getRejectQty(), WOrderReceiptIssue.this.getM_Locator_ID(), WOrderReceiptIssue.this.getM_AttributeSetInstance_ID());
                        if (isCloseDocument && WOrderReceiptIssue.this.getToDeliverQty().compareTo(WOrderReceiptIssue.this.getOrderedQty().subtract(WOrderReceiptIssue.this.getDeliveredQty())) == 0) {
                            order.setDateFinish(WOrderReceiptIssue.this.getMovementDate());
                            order.closeIt();
                            order.saveEx();
                        }
                        else {
                            final MPPOrderWorkflow orderWorkflow = order.getMPPOrderWorkflow();
                            if (orderWorkflow != null) {
                                for (final MPPOrderNode node : orderWorkflow.getNodes(true)) {
                                    WOrderReceiptIssue.this.autoReportActivities(node, order, WOrderReceiptIssue.this.getToDeliverQty());
                                    WOrderReceiptIssue.this.createUsageVarianceCost(node, order, WOrderReceiptIssue.this.getToDeliverQty());
                                }
                            }
                        }
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            this.showMessage(e.getLocalizedMessage(), true);
            return false;
        }
        finally {
            this.m_PP_order = null;
        }
        this.m_PP_order = null;
        return true;
    }
    
    public void autoReportActivities(final I_PP_Order_Node orderNode, final MPPOrder order, final BigDecimal Qty) {
        final MPPOrderNode node = (MPPOrderNode)orderNode;
        final BigDecimal setupTimeReal = BigDecimal.valueOf(node.getSetupTimeReal());
        BigDecimal.valueOf(node.getDurationReal());
        final BigDecimal setupTimeVariancePrev = node.getSetupTimeUsageVariance();
        node.getDurationUsageVariance();
        final BigDecimal setupTimeRequired = BigDecimal.valueOf(node.getSetupTimeRequired());
        BigDecimal.valueOf(node.getDurationRequired());
        final BigDecimal setupTimeVariance = setupTimeRequired.subtract(setupTimeReal).subtract(setupTimeVariancePrev);
        final BigDecimal durationVariance = new BigDecimal(node.getDuration()).multiply(Qty);
        MPPCostCollector.createCollector(order, this.getM_Product_ID(), this.getM_Locator_ID(), this.getM_AttributeSetInstance_ID(), node.getS_Resource_ID(), 0, node.getPP_Order_Node_ID(), MDocType.getDocType("MCC"), "160", order.getUpdated(), Qty, Env.ZERO, Env.ZERO, setupTimeVariance.intValueExact(), durationVariance, Env.ZERO, node.getQtyRequired().subtract(Qty.add(node.getQtyDelivered())), Env.ZERO);
    }
    
    public void createUsageVarianceCost(final I_PP_Order_Node orderNode, final MPPOrder order, final BigDecimal Qty) {
        final MPPOrderNode node = (MPPOrderNode)orderNode;
        final BigDecimal setupTimeReal = BigDecimal.valueOf(node.getSetupTimeReal());
        final BigDecimal durationReal = BigDecimal.valueOf(node.getDurationReal());
        final BigDecimal setupTimeVariancePrev = node.getSetupTimeUsageVariance();
        final BigDecimal durationVariancePrev = node.getDurationUsageVariance();
        final BigDecimal setupTimeRequired = BigDecimal.valueOf(node.getSetupTimeRequired());
        final BigDecimal durationRequired = BigDecimal.valueOf(node.getDurationRequired());
        node.getQtyToDeliver().subtract(new BigDecimal(node.get_ValueAsString("qtyreserved")));
        final BigDecimal setupTimeVariance = setupTimeRequired.subtract(setupTimeReal).subtract(setupTimeVariancePrev);
        final BigDecimal durationVariance = durationRequired.subtract(durationReal).subtract(durationVariancePrev);
        final BigDecimal costvarian = node.getPP_Order_Workflow().getCost().subtract(DB.getSQLValueBD((String)null, "select sum(currentcostprice) from m_cost where M_CostElement_ID != 1000000 and m_product_id = " + node.getPP_Order_Workflow().getPP_Order().getM_Product_ID(), new Object[0]));
        if (costvarian.compareTo(BigDecimal.ZERO) > 0) {
            MPPCostCollector.createCollector(order, this.getM_Product_ID(), this.getM_Locator_ID(), this.getM_AttributeSetInstance_ID(), node.getS_Resource_ID(), 0, node.getPP_Order_Node_ID(), MDocType.getDocType("MCC"), "120", order.getUpdated(), Qty, Env.ZERO, Env.ZERO, setupTimeVariance.intValueExact(), durationVariance, costvarian, Env.ZERO, new BigDecimal(node.get_ValueAsInt("jmltenagakerja")));
        }
    }
}
